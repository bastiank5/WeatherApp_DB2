package org.example.weatherapp_db2;

public class TemperatureStatsDTO {
    private final Double minTemperature;
    private final Double maxTemperature;

    public TemperatureStatsDTO(Double minTemperature, Double maxTemperature) {
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public boolean hasData() {
        return minTemperature != null && maxTemperature != null;
    }
}
