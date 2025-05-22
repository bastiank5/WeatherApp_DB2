package org.example.weatherapp_db2;

import java.time.LocalDateTime;

public class WeatherDataDTO {
    private String cityName;
    private double temperature;
    private String weatherDescription;
    private LocalDateTime timestamp; // Optional, wenn du es im DTO brauchst

    // Konstruktor für von API abgerufene Daten
    public WeatherDataDTO(String cityName, double temperature, String weatherDescription) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
    }

    // Getters
    public String getCityName() {
        return cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    } // Falls du es nach dem DB-Insert setzen willst

    @Override
    public String toString() {
        return "Wetter in " + cityName + ": " + temperature + "°C, " + weatherDescription;
    }
}