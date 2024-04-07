package com.github.leapbound.yc.action.utils.mybatis;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author yamath
 * @since 2023/7/3 13:31
 */
public class MybatisGenerator {

    private static final String URL = "jdbc:mysql://192.168.117.199:3306/yucong?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&autoReconnect=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static String[] tables = {"yc_function_groovy"};

    private static String projectPath = System.getProperty("user.dir");
    private static String outputDir = projectPath;
//    private static String outputDirMapperXml = projectPath + "/src/main/resources/mapper";
    private static String outputDirEntity = projectPath + "/src/main/java/yzggy/yucong/action/entities";
    private static String outputDirMapper = projectPath + "/src/main/java/yzggy/yucong/action/mapper";

    private static Map<OutputFile, String> MAP = Maps.newHashMap();

    static {
        MAP.put(OutputFile.entity, outputDirEntity);
        MAP.put(OutputFile.mapper, outputDirMapper);
//        MAP.put(OutputFile.xml, outputDirMapperXml);
        MAP.put(OutputFile.service, null);
        MAP.put(OutputFile.serviceImpl, null);
        MAP.put(OutputFile.controller, null);
    }

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author("yamath").outputDir(outputDir);
                })
                .packageConfig(builder -> {
                    builder.parent("yzggy.yucong.action")
                            .entity("entities")
                            .mapper("mapper")
                            .pathInfo(MAP);
                })
//                .templateConfig(builder -> {
//                    builder.xml("template/mapper.xml");
//                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
                            .mapperBuilder()
                            .superClass(EasyBaseMapper.class)
                            .mapperAnnotation(Mapper.class)
                            .enableBaseColumnList()
                            .enableBaseResultMap()
//                            .enableFileOverride() // 已有文件覆盖
                    ;
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
