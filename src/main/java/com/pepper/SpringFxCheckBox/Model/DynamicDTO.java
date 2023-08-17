package com.pepper.SpringFxCheckBox.Model;

import java.util.HashMap;
import java.util.Map;


public class DynamicDTO 
{
    private Map<String, Object> properties = new HashMap<>();

    public void setValue(String columnName, Object value) 
    {
        properties.put(columnName, value);
    }

    public Object getValue(String columnName) 
    {
        return properties.get(columnName);
    }
    
    public Map<String, Object> getProperties()
    {
        return properties;
    }

}
/* Majd az EntityHandlerbe:
    // ... Inside your processing code ...

ResultSetMetaData metaData = resultSet.getMetaData();
int columnCount = metaData.getColumnCount();

DynamicDTO dto = new DynamicDTO();
for (int i = 1; i <= columnCount; i++) {
    String columnName = metaData.getColumnName(i);
    Object value = resultSet.getObject(i);
    dto.setValue(columnName, value);
}

// Use dto to access the retrieved values
*/