package com.pepper.SpringFxCheckBox.Model;

import com.pepper.SpringFxCheckBox.Controller.AppControllerChB;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Model<T>
{
    private Connection connection;
    
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
    public List<String> getColumnNamesNew(String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        AppControllerChB.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            while (columns.next()) {
                if(!columnNames.contains(columns.getString("COLUMN_NAME").trim())){
                    columnNames.add(columns.getString("COLUMN_NAME"));
                }
            }
        }
        return columnNames;
    }
    public List<String> getTableNamesNew(String database) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        AppControllerChB.getConnection();
        
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(database, null, null, new String[]{"TABLE"})) {
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
        }
        
        return tableNames;
    }
    /*
    CATALOG: sémákat tartalmazó konténer (adatbázis név paraméter helye). Schema: Az adatbázisséma meghatározza, hogy az adatok hogyan vannak rendezve egy relációs adatbázison belül;
    ez magában foglalja a logikai összefüggéseket, például a táblaneveket, a mezőket, az adattípusokat és az ezen entitások közötti kapcsolatokat.
    null paraméterrel jelzem, hogy nincs filter ami szerint lekérem a catalogot
    SCHEMA PATTERN: A sémák adatbázis-objektumok, például táblák, nézetek, eljárások stb. konténer. A null érték megadásával sémanévként azt jelzi,
    hogy az összes sémából szeretne információkat, vagy nem egy adott séma alapján szűri a keresést.
    TABLE NAME PATTERN: ebben az esetben ez a paraméter, ezzel mondom meg melyik tábla neveit kérem
    COLUMN NAME PATTERN: Ez az oszlopok nevük alapján történő szűrésére szolgál. A null érték megadásával nem alkalmaz az oszlopnevek alapján szűrést.
    A megadott táblázat összes oszlopáról lekéri az információkat.
    ---táblanevek lekérdezésekor


    */
}
