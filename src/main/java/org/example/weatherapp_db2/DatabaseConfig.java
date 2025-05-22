package org.example.weatherapp_db2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {

    public static boolean DATABASE_ENABLED = true;

    private static final String DB_HOST;
    private static final String DB_PORT;
    private static final String DB_NAME;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Dotenv dotenv = Dotenv.load();

        DB_HOST = dotenv.get("DB_HOST");
        DB_PORT = dotenv.get("DB_PORT");
        DB_NAME = dotenv.get("DB_NAME");
        DB_USER = dotenv.get("DB_USER");
        DB_PASSWORD = dotenv.get("DB_PASSWORD");
    }

    private static final String DB_URL_TEMPLATE = "jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=false;";
    private static final String DB_URL = String.format(DB_URL_TEMPLATE, DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);


    public static Connection getConnection() throws SQLException {
        // Prüfen, ob Datenbankinteraktionen global aktiviert sind
        if (!DATABASE_ENABLED) {
            System.out.println("INFO: Datenbankinteraktionen sind global deaktiviert (DatabaseConfig.DATABASE_ENABLED = false). Es wird keine Verbindung hergestellt.");
            return null; // Keine Verbindung zurückgeben
        }

        // Normale Verbindungslogik
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Microsoft JDBC Driver nicht gefunden: " + e.getMessage());
            throw new SQLException("JDBC Driver nicht gefunden", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void main(String[] args) {
        // Beispiel: Testen mit aktivierter und deaktivierter Datenbank
        System.out.println("--- Test mit DATABASE_ENABLED = true ---");
        DATABASE_ENABLED = true;
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Verbindung zum Server " + DB_HOST + ", Datenbank " + DB_NAME + " erfolgreich hergestellt!");
            } else {
                // Dieser Fall sollte bei DATABASE_ENABLED = true und korrekter Konfig nicht eintreten,
                // es sei denn, es gibt ein echtes Verbindungsproblem.
                System.err.println("Verbindung fehlgeschlagen, obwohl DB aktiviert (prüfe Konfiguration/VPN).");
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankverbindung (DB aktiviert):");
            e.printStackTrace();
        }

        System.out.println("\n--- Test mit DATABASE_ENABLED = false ---");
        DATABASE_ENABLED = false;
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.out.println("Wie erwartet: Keine Verbindung hergestellt, da DB deaktiviert.");
            } else {
                System.err.println("FEHLER: Verbindung wurde hergestellt, obwohl DB deaktiviert sein sollte!");
            }
        } catch (SQLException e) {
            // Sollte nicht passieren, wenn conn == null ist
            System.err.println("Unerwarteter SQL-Fehler bei deaktivierter DB:");
            e.printStackTrace();
        }
        // Setze zurück auf den Standard für andere Tests oder den App-Start
        DATABASE_ENABLED = true;
    }
}