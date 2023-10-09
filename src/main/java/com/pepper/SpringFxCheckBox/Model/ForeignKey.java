package com.pepper.SpringFxCheckBox.Model;

import java.util.List;


public class ForeignKey 
{
    String tableName;
    String FK;
    List<String> foreignKeys;

    public ForeignKey(String tableName, List<String> foreignKeys) {
        this.tableName = tableName;
        this.foreignKeys = foreignKeys;
    }
    
    

}
