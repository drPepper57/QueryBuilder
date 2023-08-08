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
    List<T> queryResult = new ArrayList<>();
    
    public EntityHandler(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    // meg kell kapja a selectedColumns-t parameterben
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
    public List<T> processResultSet(ResultSet resultSet, List<String> selectedColumns) throws SQLException 
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
         //itt a baj: nem talál se partner se partnerId nevet
                    if (selectedColumns.contains(fieldName) && !field.isAnnotationPresent(Transient.class))
                    {
                        System.out.println("Entered if (selectedColumns.contains(fieldName) " + fieldName);
                        field.setAccessible(true);
                        Object value = resultSet.getObject(fieldName);
                    
                                                               //valamiért azt hitte String és crashelt
                        if (field.getType() == String.class && !"partnerId".equals(fieldName)) 
                        {                        
                            field.set(entity, resultSet.getString(fieldName));
                        }
                        else if (field.getType() == int.class) 
                        {
                            /*if("partnerID".equals(fieldName)) // nem talál se partner se partnerId nevet
                            {
                                fieldName = "partner";
                                //System.out.println(fieldName + " " + resultSet.getInt(fieldName));
                                field.set(entity, resultSet.getInt(fieldName));
                            }*/
                            //else {System.out.println(fieldName + " else " + resultSet.getInt(fieldName));}
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
    
    
    
    
    
    /*
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
