package com.example.hello;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class LocationManagerFragment extends Fragment {

    private EditText editTextCity;
    private Button buttonGetWeather;
    private TextView textViewWeather;
    private Button buttonPrev, buttonNext;
    private List<WeatherDay> weatherDays = null;  // weatherDays 数据可以是空的，替换为实际数据获取。
    private int currentIndex = 0; // 当前显示的天气数据的索引

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // 将Fragment的布局传给根视图
            View view = inflater.inflate(R.layout.weather, container, false);

            // 初始化视图组件
            editTextCity = view.findViewById(R.id.editTextCity);
            buttonGetWeather = view.findViewById(R.id.buttonGetWeather);
            buttonPrev = view.findViewById(R.id.buttonPrev);
            buttonNext = view.findViewById(R.id.buttonNext);

            // 获取天气按钮点击事件
            buttonGetWeather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String city = editTextCity.getText().toString().trim();
                    if (!city.isEmpty()) {
                        // 使用 WeatherService 获取天气数据
                        WeatherService.getWeatherData(city, new WeatherService.WeatherCallback() {
                            @Override
                            public void onWeatherDataReceived(List<WeatherDay> weatherData) {
                                if(weatherData != null && !weatherData.isEmpty()) {
                                    // 获取天气数据成功，更新 UI
                                    weatherDays = weatherData;
                                    currentIndex = 0; // 重置当前索引为第一个
                                    updateWeatherUI();  // 更新天气信息到UI
                                } else {
                                    Toast.makeText(getContext(), "获取天气数据为空", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // 获取天气数据失败，更新 UI
                                Toast.makeText(getContext(), "获取天气失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "请输入一个城市", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // 向左滑动的按钮点击事件
            buttonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weatherDays != null && weatherDays.size() > 0) {
                        // 如果当前不是第一天，展示上一天的天气
                        if (currentIndex > 0) {
                            currentIndex--;
                            updateWeatherUI();
                        } else {
                            Toast.makeText(getContext(), "这是第一天", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            // 向右滑动的按钮点击事件
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weatherDays != null && weatherDays.size() > 0) {
                        // 如果当前不是最后一天，展示下一天的天气
                        if (currentIndex < weatherDays.size() - 1) {
                            currentIndex++;
                            updateWeatherUI();
                        } else {
                            Toast.makeText(getContext(), "这是最后一天", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            return view; // 返回Fragment的视图

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "发生错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // 更新UI来显示天气信息
    private void updateWeatherUI() {
        try {
            if (weatherDays != null && weatherDays.size() > 0 && getView() != null) {
                WeatherDay currentDay = weatherDays.get(currentIndex);

                TextView dateView = getView().findViewById(R.id.textViewDate);
                TextView dayWeatherView = getView().findViewById(R.id.textViewDayWeather);
                TextView nightWeatherView = getView().findViewById(R.id.textViewNightWeather);
                TextView highTempView = getView().findViewById(R.id.textViewHighTemp);
                TextView lowTempView = getView().findViewById(R.id.textViewLowTemp);
                TextView windView = getView().findViewById(R.id.textViewWind);
                TextView humidityView = getView().findViewById(R.id.textViewHumidity);

                if(dateView != null) dateView.setText(currentDay.getDate());
                if(dayWeatherView != null) dayWeatherView.setText(currentDay.getTextDay());
                if(nightWeatherView != null) nightWeatherView.setText(currentDay.getTextNight());
                if(highTempView != null) highTempView.setText(currentDay.getHigh() + "°C");
                if(lowTempView != null) lowTempView.setText(currentDay.getLow() + "°C");
                if(windView != null) windView.setText(currentDay.getWindDirection() + "风 " + currentDay.getWindSpeed()+"km/h");
                if(humidityView != null) humidityView.setText(currentDay.getHumidity() + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "更新UI时发生错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
