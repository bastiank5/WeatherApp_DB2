package org.example.weatherapp_db2;

public class CityNotFoundException extends Exception {
    public CityNotFoundException(String message) {
        super(message);
    }
}