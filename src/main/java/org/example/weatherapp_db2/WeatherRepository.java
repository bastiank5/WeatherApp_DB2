package org.example.weatherapp_db2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class WeatherRepository {

    public void saveWeatherData(WeatherDataDTO data) throws SQLException {

        String sql = "{CALL dbo.Kempe_InsertWeatherData(?, ?, ?)}";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Zusätzliche Prüfung, falls getConnection() null zurückgibt (sollte durch obige Prüfung abgedeckt sein, aber sicher ist sicher)
            if (conn == null) {
                System.err.println("WARNUNG: Konnte keine Datenbankverbindung für saveWeatherData erhalten, obwohl DB-Interaktionen aktiviert sein sollten. Überspringe Speichern.");
                return;
            }

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, data.getCityName());
                stmt.setDouble(2, data.getTemperature());
                stmt.setString(3, data.getWeatherDescription());

                boolean hasResultSet = stmt.execute();
                if (hasResultSet) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        if (rs.next()) {
                            int newLogId = rs.getInt("NewLogID");
                            if (newLogId > 0) {
                                System.out.println("Wetterdaten erfolgreich gespeichert mit LogID: " + newLogId);
                            } else { // Z.B. -1 wenn Duplikat durch SP verhindert
                                System.out.println("Wetterdaten wurden nicht gespeichert (ggf. Duplikat laut Stored Procedure).");
                            }
                        }
                    }
                } else {
                    System.out.println("Stored Procedure InsertWeatherData ausgeführt (keine direkte Ergebnismenge).");
                }
            }
        } catch (SQLException e) {
            System.err.println("Datenbankfehler beim Speichern der Wetterdaten: " + e.getMessage());
            throw e; // Fehler weiterwerfen, damit die aufrufende Schicht ihn behandeln kann
        }
    }

    public Double getAverageTemperatureForCityLast24Hours(String cityName) throws SQLException {

        String sql = "{? = CALL dbo.Kempe_GetAverageTemperatureForCityLast24Hours(?)}";
        Double averageTemp = null;

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("WARNUNG: Konnte keine Datenbankverbindung für getAverageTemperature erhalten, obwohl DB-Interaktionen aktiviert sein sollten. Gebe null zurück.");
                return null;
            }

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.DECIMAL);
                stmt.setString(2, cityName);

                stmt.execute();
                averageTemp = stmt.getDouble(1);

                if (stmt.wasNull()) {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Datenbankfehler beim Abrufen der Durchschnittstemperatur: " + e.getMessage());
            throw e;
        }
        return averageTemp;
    }
}