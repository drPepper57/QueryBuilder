package com.pepper.SpringFxCheckBox.Model;

import java.util.HashMap;
import java.util.Map;

public class DTO 
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
    // Amikor foreignKey-ket tárolok és ha egy táblának több fk-e is van az Object List<String>lesz
    //kérdés hogy rakom azt táblázatba... 
}
