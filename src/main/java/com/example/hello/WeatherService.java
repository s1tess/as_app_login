package com.example.hello;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {

    // 接口回调，用来返回天气数据
    public interface WeatherCallback {
        void onWeatherDataReceived(List<WeatherDay> weatherDays);
        void onFailure(String errorMessage);
    }

    private static final String API_KEY = "S1zjIJZZvI1wEmbdG"; //
    private static final String BASE_URL = "https://api.seniverse.com/v3/weather/daily.json";

    // 获取天气数据的方法
    public static void getWeatherData(String city, final WeatherCallback callback) {
        // 构造 URL
        String url = BASE_URL + "?key=" + API_KEY + "&location=" + city + "&language=zh-Hans&unit=c";

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 异步请求
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // 请求失败，调用回调通知主线程
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("WeatherResponse", jsonResponse);
                    // 解析数据并返回结果
                    List<WeatherDay> weatherDays = parseWeatherData(jsonResponse);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onWeatherDataReceived(weatherDays));
                } else {
                    // 请求失败，调用回调通知主线程
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Failed to get weather data"));
                }
            }
        });
    }

    // 解析天气数据
    private static List<WeatherDay> parseWeatherData(String jsonResponse) {
        List<WeatherDay> weatherDays = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            JSONObject result = results.getJSONObject(0);
            JSONArray daily = result.getJSONArray("daily");

            // 限制显示的天数，最多 7 天
            int daysToDisplay = Math.min(daily.length(), 15);

            for (int i = 0; i < daysToDisplay; i++) {
                JSONObject dayWeather = daily.getJSONObject(i);
                String date = dayWeather.optString("date", "N/A");
                String textDay = dayWeather.optString("text_day", "N/A");
                String textNight = dayWeather.optString("text_night", "N/A");
                String high = dayWeather.optString("high", "N/A");
                String low = dayWeather.optString("low", "N/A");
                String windDirection = dayWeather.optString("wind_direction", "N/A");
                String windSpeed = dayWeather.optString("wind_speed", "N/A");
                String humidity = dayWeather.optString("humidity", "N/A");

                // 创建 WeatherDay 实例
                WeatherDay weatherDay = new WeatherDay(date, textDay, textNight, high, low, windDirection, windSpeed, humidity);
                weatherDays.add(weatherDay);

                // 日志输出每个 WeatherDay 实例的内容
                Log.d("WeatherDay " + i, weatherDay.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherDays;
    }
}
