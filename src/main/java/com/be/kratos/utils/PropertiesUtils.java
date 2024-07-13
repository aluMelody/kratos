package com.be.kratos.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static Properties properties;
    // 读根目录env.properties文件
    public static String getProperty(String key) {
        if (properties == null) {
            properties = new Properties();
        }
        try {
            InputStream inputStream = new FileInputStream("env.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

    // 读src/main/java/resources/env.properties文件
    public static String getProperty1(String key) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("env.properties");
            if (properties == null) {
                properties = new Properties();
            }
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return properties.getProperty(key);
    }

    public static String getFilePath(String key) {
        return ClassLoader.getSystemResource("./prepareData/" + key + ".csv").getFile();
    }

}
