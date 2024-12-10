package com.example.hello;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;



public class WeatherFragment extends Fragment {

    private static final String ARG_WEATHER_DATA = "weather_data";

    private TextView textViewWeatherDetails;

    public WeatherFragment() {
        // Required empty public constructor
    }

    // 创建新的实例，传入每一天的天气数据
    public static WeatherFragment newInstance(JSONObject dayWeather) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEATHER_DATA, dayWeather.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        textViewWeatherDetails = rootView.findViewById(R.id.textViewWeatherDetails);

        // 获取传递过来的天气数据
        if (getArguments() != null) {
            String weatherData = getArguments().getString(ARG_WEATHER_DATA);
            try {
                JSONObject dayWeather = new JSONObject(weatherData);
                String weatherInfo = generateWeatherInfo(dayWeather);
                textViewWeatherDetails.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }

    private String generateWeatherInfo(JSONObject dayWeather) {
        try {
            String date = dayWeather.optString("date", "N/A");
            String textDay = dayWeather.optString("text_day", "N/A");
            String textNight = dayWeather.optString("text_night", "N/A");
            String high = dayWeather.optString("high", "N/A");
            String low = dayWeather.optString("low", "N/A");
            String windDirection = dayWeather.optString("wind_direction", "N/A");
            String windSpeed = dayWeather.optString("wind_speed", "N/A");
            String humidity = dayWeather.optString("humidity", "N/A");

            return "Date: " + date +
                    "\nDay: " + textDay +
                    "\nNight: " + textNight +
                    "\nHigh: " + high + "°C" +
                    "\nLow: " + low + "°C" +
                    "\nWind: " + windDirection + ", Speed: " + windSpeed + " km/h" +
                    "\nHumidity: " + humidity;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving weather data";
        }
    }
}
