-- Tabellenerstellung:
CREATE TABLE dbo.Kempe_WeatherData (
    LogID INT IDENTITY(1,1) NOT NULL,
    CityName NVARCHAR(100) NOT NULL,
    TemperatureCelsius DECIMAL(5, 2) NOT NULL,
    WeatherDescription NVARCHAR(255) NOT NULL,
    TimestampUTC DATETIME2 NOT NULL CONSTRAINT DF_WeatherData_TimestampUTC DEFAULT GETUTCDATE(),

    CONSTRAINT PK_WeatherData PRIMARY KEY (LogID)
);
GO

------------------------------------------------------------------------------------
-- Stored Procedure:
-- Überprüfen, ob die Prozedur bereits existiert, und sie ggf. löschen
    IF OBJECT_ID('dbo.Kempe_InsertWeatherData', 'P') IS NOT NULL
        DROP PROCEDURE dbo.Kempe_InsertWeatherData;
    GO

    -- Erstellen der Stored Procedure
    CREATE PROCEDURE dbo.Kempe_InsertWeatherData
        @CityName NVARCHAR(100),
        @TemperatureCelsius DECIMAL(5, 2),
        @WeatherDescription NVARCHAR(255)
    AS
    BEGIN
        SET NOCOUNT ON; -- Verhindert, dass die Anzahl der betroffenen Zeilen zurückgegeben wird

        DECLARE @MinimumTimeDifferenceMinutes INT = 5; -- Mindestzeit in Minuten für einen neuen Eintrag

        -- Prüfen, ob ein sehr ähnlicher Eintrag für dieselbe Stadt kürzlich gemacht wurde
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.Kempe_WeatherData wd
            WHERE wd.CityName = @CityName
              AND wd.TemperatureCelsius = @TemperatureCelsius
              AND wd.WeatherDescription = @WeatherDescription
              AND wd.TimestampUTC >= DATEADD(MINUTE, -@MinimumTimeDifferenceMinutes, GETUTCDATE())
        )
        BEGIN
            -- Neuen Datensatz einfügen
            INSERT INTO dbo.Kempe_WeatherData (CityName, TemperatureCelsius, WeatherDescription, TimestampUTC)
            VALUES (@CityName, @TemperatureCelsius, @WeatherDescription, GETUTCDATE());

            -- Rückgabe der ID des neu eingefügten Datensatzes (optional, aber nützlich für das Java-Programm)
            SELECT SCOPE_IDENTITY() AS NewLogID;
        END
        ELSE
        BEGIN
            -- Optional: Eine Meldung zurückgeben oder einfach nichts tun, wenn ein Duplikat verhindert wurde
            -- SELECT -1 AS NewLogID; -- Ein Indikator, dass nichts eingefügt wurde
            PRINT 'Duplicate weather data for ' + @CityName + ' within the last ' + CAST(@MinimumTimeDifferenceMinutes AS VARCHAR) + ' minutes. No new record inserted.';
            SELECT 0 AS NewLogID; -- Gebe 0 zurück, wenn nichts eingefügt wurde, um dem Java-Code eine Zahl zu geben.
        END
    END
    GO

------------------------------------------------------------------------------------
-- Function:
-- Überprüfen, ob die Funktion bereits existiert, und sie ggf. löschen
IF OBJECT_ID('dbo.Kempe_GetAverageTemperatureForCityLast24Hours', 'FN') IS NOT NULL
DROP FUNCTION dbo.Kempe_GetAverageTemperatureForCityLast24Hours;
GO

-- Erstellen der Scalar-valued Function
CREATE FUNCTION dbo.Kempe_GetAverageTemperatureForCityLast24Hours
(
    @CityName NVARCHAR(100)
)
    RETURNS DECIMAL(5, 2) -- Der Rückgabetyp
AS
BEGIN
    DECLARE @AverageTemperature DECIMAL(5, 2);

SELECT @AverageTemperature = AVG(TemperatureCelsius)
FROM dbo.Kempe_WeatherData
WHERE CityName = @CityName
  AND TimestampUTC >= DATEADD(HOUR, -24, GETUTCDATE()); -- Einträge der letzten 24 Stunden

RETURN @AverageTemperature;
END
GO

------------------------------------------------------------------------------------
-- Stored Procedure:
-- Überprüfen, ob die Prozedur bereits existiert, und sie ggf. löschen
IF OBJECT_ID('dbo.Kempe_GetMinMaxTemperatures', 'P') IS NOT NULL
DROP PROCEDURE dbo.Kempe_GetMinMaxTemperatures;
GO

-- Erstellen der Stored Procedure
CREATE PROCEDURE dbo.Kempe_GetMinMaxTemperatures
    @CityName NVARCHAR(100),
    @MinTemperature DECIMAL(5, 2) OUTPUT,
    @MaxTemperature DECIMAL(5, 2) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

SELECT
    @MinTemperature = MIN(TemperatureCelsius),
    @MaxTemperature = MAX(TemperatureCelsius)
FROM
    dbo.Kempe_WeatherData wd
WHERE
    wd.CityName = @CityName;

-- Fallbehandlung, wenn die Tabelle leer ist
IF @MinTemperature IS NULL AND @MaxTemperature IS NULL
BEGIN
        -- Setze auf einen Indikatorwert oder lasse sie NULL, je nach Fehlerbehandlung im Java-Code
        -- Hier lassen wir sie NULL, Java muss das behandeln.
        PRINT 'WeatherData table is empty or contains no temperature data.';
END
END
GO