package com.example.hello;

public class WeatherDay {

    private String date;
    private String textDay;
    private String textNight;
    private String high;
    private String low;
    private String windDirection;
    private String windSpeed;
    private String humidity;

    public WeatherDay(String date, String textDay, String textNight, String high, String low, String windDirection, String windSpeed, String humidity) {
        this.date = date;
        this.textDay = textDay;
        this.textNight = textNight;
        this.high = high;
        this.low = low;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
    }

    public String getDate() {
        return date;
    }

    public String getTextDay() {
        return textDay;
    }

    public String getTextNight() {
        return textNight;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return "WeatherDay{" +
                "date='" + date + '\'' +
                ", textDay='" + textDay + '\'' +
                ", textNight='" + textNight + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }
}
