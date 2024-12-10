package com.example.hello;

public class weatherDate {

    // 创建一个静态变量来存储单例实例
    private static weatherDate instance;

    // 用于存储配置信息的字段
    private String appName;
    private String environment;
    private String version;

    // 私有构造函数，防止外部实例化
    private weatherDate() {
        // 默认配置值
        this.appName = "My App";
        this.environment = "development";
        this.version = "1.0.0";
    }

    // 获取单例实例的方法
    public static weatherDate getInstance() {
        // 如果实例为空，则创建新的实例
        if (instance == null) {
            synchronized (weatherDate.class) {
                if (instance == null) {
                    instance = new weatherDate();
                }
            }
        }
        return instance;
    }

    // 获取配置值
    public String getAppName() {
        return appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getVersion() {
        return version;
    }

    // 设置配置值
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // 更新配置，批量设置多个配置
    public void updateConfig(String appName, String environment, String version) {
        this.appName = appName;
        this.environment = environment;
        this.version = version;
    }
}
