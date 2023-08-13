package com.pepper.SpringFxCheckBox.Model;


public class EntityHandlerFactory 
{
    public static EntityHandler createEntityHandler(Class<?> entityClass) {
        return new EntityHandler(entityClass);
    }
}
