package com.pepper.SpringFxCheckBox.Model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Transient;

public class EntityHandler<T> 
{
    private final Class<T> entityClass;
    private List<T> entityList = new ArrayList<>();
    private List<String> columnLabel = new ArrayList<>();
    
    public EntityHandler(Class<T> entityClass) {
        this.entityClass = entityClass;        
    }
    
    public List<T> processResultSet(ResultSet resultSet, List<String> selectedColumns) throws SQLException 
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i); // eredeti column name
            String columnAlias = metaData.getColumnLabel(i); // Alias, ha van, ha nincs = columnName
            columnLabel.add(columnAlias);
            System.out.println(columnName +  " NAME");
            System.out.println(columnAlias +  " ALIAS");
        }
        
        try 
        {
            while (resultSet.next()) 
            {
                if(entityClass == DTO.class)
                {
                    System.out.println("DTO CLASS DETECTED ");
                    DTO dto = new DTO();
                    for(int i = 1; i <= columnCount; i++)
                    {
                        String columnName = metaData.getColumnName(i);
                        String columnAlias = metaData.getColumnLabel(i);
                        Object value = resultSet.getObject(columnAlias);
                        dto.setValue(columnAlias, value);                        
                    }
                    entityList.add((T) dto);
                }
                else
                {
                    T entity = entityClass.getDeclaredConstructor().newInstance();

                    Field[] fields = entityClass.getDeclaredFields();
                    int i = 1;
                    for (Field field : fields) 
                    {
                        String fieldName = field.getName();                    
                        System.out.println("all fieldNames : " + fieldName );

                        if (selectedColumns.contains(fieldName) && !field.isAnnotationPresent(Transient.class))
                        {
                            //Ha tartalmaz alias-t a metaData-> fieldName = alias (=columnLabel)
                            String columnLabel = metaData.getColumnLabel(i); // Get columnLabel/alias from metadata
                            if (!fieldName.equals(columnLabel)) {

                                fieldName = columnLabel; 
                                
                                if (i < fields.length) { i++; }
                            } else {
                                System.out.println("ELSE fieldName equals columnLabel");
                                System.out.println(fieldName + " index " + i);
                                if (i < fields.length) { i++; }
                            }
                            field.setAccessible(true);

                            if (field.getType() == String.class ) 
                            {
                                field.set(entity, resultSet.getString(fieldName));
                            }
                            else if (field.getType() == int.class) 
                            {
                                System.out.println("getType: " + field.getType() + " fieldName " + fieldName);

                               field.set(entity, resultSet.getInt(fieldName));
                            }
                            else if (field.getType() == double.class || field.getType() == Double.class) 
                            {
                                field.set(entity, resultSet.getDouble(fieldName));
                            }
                            else if (field.getType() == boolean.class || field.getType() == Boolean.class) 
                            {
                                field.set(entity, resultSet.getInt(fieldName) == 1);
                            }
                            else if(field.getType() == LocalDate.class )
                            {
                                field.set(entity, resultSet.getObject(fieldName, LocalDate.class));
                            }

                        } else if(selectedColumns.isEmpty() || selectedColumns == null && !field.isAnnotationPresent(Transient.class))
                        {
                            field.setAccessible(true);                            

                            if (field.getType() == String.class ) 
                            {                        
                                field.set(entity, resultSet.getString(fieldName));
                            }
                            else if (field.getType() == int.class) 
                            {
                               field.set(entity, resultSet.getInt(fieldName));
                            }
                            else if (field.getType() == double.class || field.getType() == Double.class) 
                            {
                                field.set(entity, resultSet.getDouble(fieldName));
                            }
                            else if (field.getType() == boolean.class || field.getType() == Boolean.class) 
                            {
                                field.set(entity, resultSet.getInt(fieldName) == 1);
                            }
                            else if(field.getType() == LocalDate.class )
                            {
                                field.set(entity, resultSet.getObject(fieldName, LocalDate.class));
                            }
                        }
                    }
                    entityList.add(entity);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return entityList;
    }
    public List<String> getColLabel(){
        System.out.println("EntityHandler getColLabel triggered, columnLabel.isEmpty: " + columnLabel.isEmpty());
        return columnLabel;
    }
    /*
    EntityHandler class is attempting to access a column by
    its original name (not the alias) and failing to find it.
    
    ResultSetMetaData interface provides information about the columns in a result set obtained from
    a database query. It gives you details about the structure and characteristics of the columns in the
    result set,such as column names, data types, whether columns are nullable, and more.
    
    1. Oszlopadatok Lekérdezése: A ResultSetMetaData segítségével információt kaphatsz a lekérdezésből származó
    eredményhalmaz oszlopairól, ideértve azok neveit, aliasait, adattípusait és azt, hogy olvashatók-e.

    2. Dinamikus Oszlophandling: Ha olyan eredményhalmazzal dolgozol, amelynek oszlopstruktúrája előre nem ismert
    (pl. dinamikus lekérdezések vagy felhasználó által definiált lekérdezések esetén), a ResultSetMetaData
    használható az oszlopnevek és adattípusok dinamikus meghatározásához.

    3. Jelentések Generálása vagy Adatok Exportálása: Amikor jelentéseket kell generálnod vagy adatokat
    különböző formátumokba exportálnod (például CSV, Excel vagy PDF), a ResultSetMetaData segítségével
    oszlopadatokat kaphatsz a fejlécek létrehozásához vagy a kimenet formázásához.

    4. Lekérdezési Eredmények Ellenőrzése: A ResultSetMetaData használatával ellenőrizheted,
    hogy a kapott eredményhalmaz megfelel-e az elvárt oszlopstruktúrának, mielőtt további feldolgozásra kerülne.
    */
    
    /*
    public List<T> processResultSet(ResultSet resultSet) throws SQLException 
    {
        
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        try 
        {
            while (resultSet.next()) 
            {
                T entity = entityClass.getDeclaredConstructor().newInstance();

                Field[] fields = entityClass.getDeclaredFields();
                for (Field field : fields) 
                {
                    String fieldName = field.getName();
                    
                    field.setAccessible(true);
  
                    if (field.getType() == String.class && !"partnerId".equals(fieldName)) 
                    {                        
                        field.set(entity, resultSet.getString(fieldName));
                    }
                    else if (field.getType() == int.class) 
                    {
                        if("partnerId".equals(fieldName)){
                            fieldName = "partner";                            
                            field.set(entity, resultSet.getInt(fieldName));
                        }
                       field.set(entity, resultSet.getInt(fieldName));
                    }
                    else if (field.getType() == double.class || field.getType() == Double.class) 
                    {
                        field.set(entity, resultSet.getDouble(fieldName));
                    }
                    else if (field.getType() == boolean.class || field.getType() == Boolean.class) 
                    {
                        field.set(entity, resultSet.getInt(fieldName) == 1);
                    }
                    else if(field.getType() == LocalDate.class )
                    {
                        field.set(entity, resultSet.getObject(fieldName, LocalDate.class));
                    }              
                }
                queryResult.add(entity);  
                // Process the entity as needed, for example, print its values
                //System.out.println("EntityHandler toString "+entity.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return queryResult;
    }
    for (Field field : fields) {
            String fieldName = field.getName();
            if (selectedColumns.contains(fieldName)) {
                field.setAccessible(true);
                Object value = resultSet.getObject(fieldName);

                try {
                    field.set(entity, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    for (Field field : fields) 
    {
        String fieldName = field.getName();

        field.setAccessible(true);

        if (field.getType() == String.class && !"partnerId".equals(fieldName)) 
        {                        
            field.set(entity, resultSet.getString(fieldName));
        }
        else if (field.getType() == int.class) 
        {
            if("partnerId".equals(fieldName)){
                fieldName = "partner";
                System.out.println(resultSet.getInt(fieldName));
                field.set(entity, resultSet.getInt(fieldName));
            }
           field.set(entity, resultSet.getInt(fieldName));
        }
        else if (field.getType() == double.class || field.getType() == Double.class) 
        {
            field.set(entity, resultSet.getDouble(fieldName));
        }
        else if (field.getType() == boolean.class || field.getType() == Boolean.class) 
        {
            field.set(entity, resultSet.getInt(fieldName) == 1);
        }
        else if(field.getType() == LocalDate.class )
        {
            field.set(entity, resultSet.getObject(fieldName, LocalDate.class));
        }              
    }*/

    
}
