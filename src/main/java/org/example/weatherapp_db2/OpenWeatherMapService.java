package org.example.weatherapp_db2;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenWeatherMapService {

    static Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient httpClient;

    public OpenWeatherMapService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public WeatherDataDTO getCurrentWeather(String cityName) throws Exception {
        if (API_KEY == null || API_KEY.equals("DEIN_OPENWEATHERMAP_API_KEY") || API_KEY.trim().isEmpty()) {
            throw new IllegalStateException("API Key für OpenWeatherMap ist nicht konfiguriert.");
        }

        String urlString = String.format("%s?q=%s&appid=%s&units=metric&lang=de",
                API_BASE_URL, cityName.replace(" ", "+"), API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            String city = jsonResponse.getString("name");
            double temp = jsonResponse.getJSONObject("main").getDouble("temp");
            String description = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
            return new WeatherDataDTO(city, temp, description);
        } else if (response.statusCode() == 404) {
            throw new CityNotFoundException("Stadt nicht gefunden: " + cityName);
        } else {
            throw new Exception("Fehler beim Abrufen der Wetterdaten: " + response.statusCode() + " - " + response.body());
        }
    }
}