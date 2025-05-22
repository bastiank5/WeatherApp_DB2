module org.example.weatherapp_db2 {
    requires javafx.controls;
    requires javafx.fxml;
    // requires javafx.web; // Nur wenn du es wirklich für andere Zwecke brauchst

    // UI Bibliotheken, falls du sie in deiner GUI verwendest
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    // Erforderliche Module für Datenbank und JSON
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc; // Microsoft JDBC Driver
    requires org.json;
    requires java.net.http;
    requires io.github.cdimascio.dotenv.java; // Für JSON-Verarbeitung

    opens org.example.weatherapp_db2 to javafx.fxml;
    exports org.example.weatherapp_db2;
}