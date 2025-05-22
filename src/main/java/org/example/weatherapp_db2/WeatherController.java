package org.example.weatherapp_db2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class WeatherController {

    @FXML
    private TextField cityInputField;
    @FXML
    private Button searchButton;
    @FXML
    private Button showAverageTempButton;
    @FXML
    private Label cityNameLabel;
    @FXML
    private Label temperatureLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label avgTemperatureLabel;

    private WeatherAppService weatherAppService;
    private String currentDisplayCityName = null; // Speichert den aktuell angezeigten Stadtnamen

    @FXML
    public void initialize() {
        this.weatherAppService = new WeatherAppService();
        statusLabel.setText("Bitte Stadt eingeben und auf Suchen klicken.");
        clearWeatherInfo();
        //Button für Durchschnittstemperatur nur aktivieren, wenn eine Stadt erfolgreich geladen wurde
        if (showAverageTempButton != null) showAverageTempButton.setDisable(true);
    }

    @FXML
    protected void handleSearchButtonAction(ActionEvent event) {
        String cityNameFromInput = cityInputField.getText();
        if (cityNameFromInput == null || cityNameFromInput.trim().isEmpty()) {
            statusLabel.setText("Bitte einen Stadtnamen eingeben.");
            clearWeatherInfo();
            currentDisplayCityName = null;
            if (showAverageTempButton != null) showAverageTempButton.setDisable(true);
            return;
        }

        statusLabel.setText("Suche Wetter für " + cityNameFromInput + "...");
        searchButton.setDisable(true);
        if (showAverageTempButton != null) showAverageTempButton.setDisable(true);
        clearWeatherInfo();
        currentDisplayCityName = null;


        new Thread(() -> {
            try {
                WeatherDataDTO weatherData = weatherAppService.fetchAndStoreWeather(cityNameFromInput.trim());
                Platform.runLater(() -> {
                    if (weatherData != null) {
                        cityNameLabel.setText("Wetter in: " + weatherData.getCityName());
                        temperatureLabel.setText("Temperatur: " + String.format("%.1f", weatherData.getTemperature()) + " °C");
                        descriptionLabel.setText("Beschreibung: " + weatherData.getWeatherDescription());
                        statusLabel.setText("Wetterdaten erfolgreich abgerufen.");
                        currentDisplayCityName = weatherData.getCityName(); // Stadtname für spätere Verwendung speichern
                    }
                });
            } catch (CityNotFoundException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Fehler: " + e.getMessage());
                    clearWeatherInfo();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Ein unerwarteter Fehler ist aufgetreten. Details siehe Konsole.");
                    clearWeatherInfo();
                });
            } finally {
                Platform.runLater(() -> {
                    searchButton.setDisable(false);
                     //Button für Durchschnittstemperatur nur aktivieren, wenn eine Stadt erfolgreich geladen wurde
                     if (currentDisplayCityName != null && showAverageTempButton != null) {
                        showAverageTempButton.setDisable(false);
                     }
                });
            }
        }).start();
    }

    @FXML
    protected void handleShowAverageTemperatureAction(ActionEvent event) {
        if (currentDisplayCityName != null && !currentDisplayCityName.trim().isEmpty()) {
            // Prüfen, ob DB-Interaktionen überhaupt aktiv sind
            if (!DatabaseConfig.DATABASE_ENABLED) {
                avgTemperatureLabel.setText("Durchschnitt (24h): DB-Interaktion deaktiviert.");
                statusLabel.setText("Datenbank ist deaktiviert.");
                return;
            }
            loadAndDisplayAverageTemperature(currentDisplayCityName);
        } else {
            statusLabel.setText("Zuerst erfolgreich Wetterdaten für eine Stadt abrufen.");
            avgTemperatureLabel.setText("");
        }
    }

    private void loadAndDisplayAverageTemperature(String cityName) {
        avgTemperatureLabel.setText("Lade Durchschnittstemperatur für " + cityName + "...");
        // Ggf. Button während des Ladens deaktivieren
        if (showAverageTempButton != null) showAverageTempButton.setDisable(true);

        new Thread(() -> {
            try {
                Double avgTemp = weatherAppService.getAverageTemperatureLast24h(cityName);
                Platform.runLater(() -> {
                    if (avgTemp != null) {
                        avgTemperatureLabel.setText("Durchschnitt (24h): " + String.format("%.1f", avgTemp) + " °C");
                        statusLabel.setText("Durchschnittstemperatur für " + cityName + " geladen.");
                    } else {
                        avgTemperatureLabel.setText("Durchschnitt (24h): Keine Daten verfügbar.");
                        statusLabel.setText("Keine Durchschnittstemperatur für " + cityName + " in den letzten 24h gefunden.");
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    avgTemperatureLabel.setText("Durchschnitt (24h): Fehler beim Laden.");
                    statusLabel.setText("Datenbankfehler beim Laden der Durchschnittstemperatur.");
                });
            } finally {
                // Ggf. Button wieder aktivieren
                 Platform.runLater(() -> {
                    if (showAverageTempButton != null) showAverageTempButton.setDisable(false);
                 });
            }
        }).start();
    }

    private void clearWeatherInfo() {
        cityNameLabel.setText("");
        temperatureLabel.setText("");
        descriptionLabel.setText("");
        avgTemperatureLabel.setText("");
    }
}