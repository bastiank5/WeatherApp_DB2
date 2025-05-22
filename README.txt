Informationen zu den einzelnen Klassen:
- Main.java: Startet die JavaFX-Anwendung.
- WeatherController.java: Verbindet die FXML-Benutzeroberfläche mit der Anwendungslogik.
- WeatherAppService.java: Dient als Fassade und koordiniert die Interaktionen zwischen der API und der Datenbank.
- OpenWeatherMapService.java: Kümmert sich ausschließlich um die Kommunikation mit der OpenWeatherMap API.
- WeatherRepository.java: Ist für alle Datenbankinteraktionen zuständig.
- DatabaseConfig.java: Konfiguriert und liefert die Datenbankverbindung.
- WeatherDataDTO.java und TemperatureStatsDTO.java: Sind reine Datenübertragungsobjekte (Data Transfer Objects), die Daten strukturiert zwischen Schichten transportieren.
- CityNotFoundException.java: Eine benutzerdefinierte Exception für einen spezifischen Fehlerfall.