package com.example.hello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherDayManager {

    // 使用静态集合来存储多个 WeatherDay 实例
    private static List<WeatherDay> weatherDayList = new ArrayList<>();
    private static Map<String, WeatherDay> weatherDayMap = new HashMap<>();

    // 添加 WeatherDay 实例到集合
    public static void addWeatherDay(WeatherDay weatherDay) {
        weatherDayList.add(weatherDay);
        weatherDayMap.put(weatherDay.getDate(), weatherDay);
    }

    // 根据日期获取 WeatherDay 实例
    public static WeatherDay getWeatherDayByDate(String date) {
        return weatherDayMap.get(date);  // 使用 Map 通过日期来查找实例
    }

    // 获取所有的 WeatherDay 实例
    public static List<WeatherDay> getAllWeatherDays() {
        return weatherDayList;  // 返回 List 中所有的 WeatherDay 实例
    }

    // 清空所有实例
    public static void clearWeatherDays() {
        weatherDayList.clear();
        weatherDayMap.clear();
    }
}
