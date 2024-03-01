package com.github.leapbound.yc.action.utils.classloader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author yamath
 * @since 2023/7/4 17:06
 */
@Component
public class SpringInjectService implements ApplicationContextAware {
    public static DefaultListableBeanFactory defaultListableBeanFactory;

    private static ConfigurableApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext) applicationContext;
        defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
    }

    /**
     * 注册bean到spring容器中
     *
     * @param beanName 名称
     * @param clazz    class
     */
    public static void registerBean(String beanName, Class<?> clazz) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinitionBuilder.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);

        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public static <T> T getBean(Class<T> clazz) {
        return defaultListableBeanFactory.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return defaultListableBeanFactory.getBean(name, clazz);
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }

    public static void removeBean(String name) {
        defaultListableBeanFactory.removeBeanDefinition(name);
    }

    public static Map<String, Object> getBeanMap(Class<? extends Annotation> clazz) {
        return context.getBeansWithAnnotation(clazz);
    }
}
