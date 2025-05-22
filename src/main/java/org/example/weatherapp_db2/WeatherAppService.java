package org.example.weatherapp_db2;

import java.sql.SQLException;

public class WeatherAppService {

    private final OpenWeatherMapService apiClient;
    private final WeatherRepository repository;

    public WeatherAppService() {
        this.apiClient = new OpenWeatherMapService();
        this.repository = new WeatherRepository();
    }

    /**
     * Ruft aktuelle Wetterdaten für eine Stadt ab, speichert sie und gibt sie zurück.
     * @param cityName Der Name der Stadt.
     * @return WeatherDataDTO mit den aktuellen Wetterdaten.
     * @throws CityNotFoundException Wenn die Stadt von der API nicht gefunden wird.
     * @throws Exception Bei anderen Fehlern (API, Datenbank).
     */
    public WeatherDataDTO fetchAndStoreWeather(String cityName) throws Exception {
        try {
            WeatherDataDTO weatherData = apiClient.getCurrentWeather(cityName);
            if (weatherData != null) {
                repository.saveWeatherData(weatherData);
            }
            return weatherData;
        } catch (CityNotFoundException e) {
            System.err.println(e.getMessage());
            throw e; // Weiterleiten, damit die GUI es behandeln kann
        } catch (SQLException e) {
            System.err.println("Fehler beim Speichern der Wetterdaten in der Datenbank: " + e.getMessage());
            throw new Exception("Datenbankfehler: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
            throw e; // Weiterleiten
        }
    }

    /**
     * Ruft die Durchschnittstemperatur für eine Stadt der letzten 24 Stunden aus der Datenbank ab.
     * @param cityName Der Name der Stadt.
     * @return Die Durchschnittstemperatur oder null, wenn keine Daten vorhanden sind.
     * @throws SQLException Bei Datenbankfehlern.
     */
    public Double getAverageTemperatureLast24h(String cityName) throws SQLException {
        return repository.getAverageTemperatureForCityLast24Hours(cityName);
    }
}
