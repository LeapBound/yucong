package yzggy.yucong.action.utils.classloader;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.plugin.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * hotClassloader, load jar from path.
 *
 * @author yamath
 * @since 2023/7/4 17:02
 */
public class HotClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(HotClassLoader.class);

    /**
     * <pre>
     *     jar 包更新时间记录，避免重复加载
     *     key      : jar 绝对路径
     *     value    : jar 更新时间，0-正在加载，>0 上一次更新时间
     * </pre>
     */
    private static final Map<String, Long> jarUpdateTimeMap;

    private static final Map<String, List<String>> jarClassNameMap;

    public static final Map<String, HotClassLoader> jarClassloaderMap;

    static {
        jarUpdateTimeMap = new HashMap<>();
        jarClassNameMap = new HashMap<>();
        jarClassloaderMap = new HashMap<>();
    }

    public HotClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    /**
     * load jar
     *
     * @param jarPath     jar path
     * @param classLoader hotClassloader
     * @return msg, error msg
     * @throws Exception pluginException
     */
    public static String loadJar(String jarPath, HotClassLoader classLoader) throws Exception {
        // load jar check
        Long lastModifyTime = jarUpdateTimeMap.get(jarPath);
        if (Objects.equals(lastModifyTime, 0L)) {
            logger.warn("HotClassLoader.loadJar loading ,please not repeat the operation, jarPath = {}", jarPath);
            return "HotClassLoader.loadJar loading ,please not repeat the operation";
        }
        // jar file
        File file = new File(jarPath);
        if (!file.exists()) {
            logger.warn("HotClassLoader.loadJar fail file not exist, jarPath = {}", jarPath);
            return "HotClassLoader.loadJar fail file not exist";
        }
        // jar version
        long currentJarModifyTime = file.getAbsoluteFile().lastModified();
        if (Objects.equals(lastModifyTime, currentJarModifyTime)) {
            logger.warn("HotClassLoader.loadJar current version has bean loaded , jarPath = {}", jarPath);
            return "HotClassLoader.loadJar current version has bean loaded";
        }
        // classloader
        if (classLoader == null) {
            classLoader = SpringInjectService.getBean(HotClassLoader.class);
        }
        // exist
        if (jarUpdateTimeMap.containsKey(jarPath)) {
            // exist, unload jar
            logger.info("已存在 jar, 先卸载, jar = {}", jarPath);
            String msg = unloadJar(jarPath);
            if (!StrUtil.isEmptyIfStr(msg)) {
                return msg;
            }
        }
        // add classloader
        jarClassloaderMap.put(jarPath, classLoader);

        try {
            classLoader.addURL(file.toURI().toURL());
            // load class and register beans
            // 记录jar 加载时间
            jarUpdateTimeMap.put(jarPath, 0L);
            // load class
            List<String> classNameList = injectBean(classLoader, jarPath);
            // 记录jar包中的 class 文件相对路径
            jarClassNameMap.put(jarPath, classNameList);

            // 记录jar 文件的更新时间
            jarUpdateTimeMap.put(jarPath, currentJarModifyTime);
            //
        } catch (MalformedURLException e) {
            throw new PluginException("通过url 添加 jar 是失败");
        } catch (PluginException ex) {
            throw new PluginException(ex.getMessage(), ex);
        } finally {
            if (Objects.equals(jarUpdateTimeMap.get(jarPath), 0L)) {
                jarUpdateTimeMap.remove(jarPath);
            }
        }
        return null;
    }

    /**
     * unload jar
     *
     * @param jarPath jar path
     * @return msg, error msg
     * @throws Exception pluginException
     */
    public static String unloadJar(String jarPath) throws Exception {
        // 1. 校验文件是否存在
        File file = new File(jarPath);
        if (!file.exists()) {
            logger.warn("HotClassLoader.unloadJar fail file not exist, jarPath = {}", jarPath);
            return "HotClassLoader.unloadJar fail file not exist";
        }
        List<String> classNameList = jarClassNameMap.get(jarPath);
        if (CollectionUtils.isEmpty(classNameList)) {
            logger.warn("HotClassLoader.unloadJar fail,the jar no class, jarPath = {}", jarPath);
            return "HotClassLoader.unloadJar fail,the jar no class";
        }
        //
        HotClassLoader oldClassloader = null;
        try {
            // old classloader
            if (jarClassloaderMap.containsKey(jarPath)) { // exist, remove old classloader
                oldClassloader = jarClassloaderMap.get(jarPath);
                jarClassloaderMap.remove(jarPath, oldClassloader);
            }
            // 遍历移除 spring 中对应的 bean, 关闭类加载器,移除引用
            for (String loopClassName : classNameList) {
                boolean beanExist = SpringInjectService.containsBean(loopClassName);
                if (beanExist) { // exist, remove bean
                    SpringInjectService.removeBean(loopClassName);
                }
                //
                if (oldClassloader != null) {
                    // set class null
                    Class<?> oldClazz = oldClassloader.loadClass(loopClassName, false);
                    oldClazz = null;
                }
            }
            //
            jarUpdateTimeMap.remove(jarPath);
            // 关闭 classloader
            if (oldClassloader != null) {
                oldClassloader.close();
                oldClassloader = null;
            }
        } catch (IOException e) {
            throw new PluginException("HotClassLoader 卸载失败");
        }
        return null;
    }

    /**
     * 注册 bean
     *
     * @param classLoader hotClassloader
     * @param jarPath     jar path
     * @return class list
     */
    private static List<String> injectBean(HotClassLoader classLoader, String jarPath) {
        List<String> classNameList = new ArrayList<>();
        // 遍历 jar 包中的类
        try (JarFile jarFile = new JarFile(jarPath)) {
            //
            List<JarEntry> jarEntryList = jarFile.stream().sequential().toList();
            // jar entry
            for (JarEntry loopJar : jarEntryList) {
                //
                String fileName = loopJar.getName();
                //
                if (!fileName.endsWith(".class")) {
                    continue;
                }
                // class
                String className = fileName.replace(".class", "").replace("/", ".");
                // 将 class 注册到 spring 容器
                boolean beanExist = SpringInjectService.containsBean(className);
                if (beanExist) { // exist, remove bean
                    SpringInjectService.removeBean(className);
                }
                // load class
                Class<?> clazz = classLoader.loadClass(className, false);
                // register bean
                SpringInjectService.registerBean(className, clazz);
                //
                classNameList.add(className);
            }
        } catch (IOException | ClassNotFoundException e) {
            jarUpdateTimeMap.remove(jarPath);
            throw new PluginException("jar包解析失败");
        }
        return classNameList;
    }

    private static void doAutowired(String className, Class<?> clz) {
        Map<String, Object> beanMap = SpringInjectService.getBeanMap(Service.class);
        if (beanMap == null || beanMap.size() == 0) {
            return;
        }
        /**拿到clz的接口*/
        Class<?>[] clzInterfaces = clz.getInterfaces();
        beanMap.forEach((k, v) -> {
            Class<?> cz = v.getClass();
            /**拿到class所有的字段（private，protected，public，但不包括父类的）*/
            Field[] declaredFields = cz.getDeclaredFields();
            if (declaredFields == null || declaredFields.length == 0) {
                return;
            }
            /**遍历字段，只处理@Autowired注解的字段值的注入*/
            for (Field declaredField : declaredFields) {
                if (!declaredField.isAnnotationPresent(Autowired.class)) {
                    return;
                }
                /**推断下字段类型是否是接口（如果是接口的话，注入的条件稍显"复杂"些）*/
                boolean bInterface = declaredField.getType().isInterface();
                /**拿到字段的类型完全限定名*/
                String fieldTypeName = declaredField.getType().getName();

                /**设置字段可以被修改，这一版本，先不考虑多态bean的情况，下一个版本完善时再考虑*/
                declaredField.setAccessible(true);
                try {
                    /**如果字段的类型非接口并且字段的类的完全限定名就等于clz的名，那就直接setter设置*/
                    if (!bInterface && fieldTypeName == clz.getName()) {
                        declaredField.set(v, SpringInjectService.getBean(className, clz));
                    }
                    /**如果字段类型是接口，还得判断下clz是不是实现了某些接口，如果是，得判断两边接口类型是否一致才能注入值*/
                    if (bInterface) {
                        if (clzInterfaces != null || clzInterfaces.length > 0) {
                            for (Class inter : clzInterfaces) {
                                if (fieldTypeName == inter.getName()) {
                                    declaredField.set(v, SpringInjectService.getBean(className, clz));
                                    break;
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) {
            return ClassLoader.getSystemClassLoader().loadClass(name);
        }

        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                return loadClass(name);
            }
            return clazz;
        }
        return super.loadClass(name, resolve);
    }
}
