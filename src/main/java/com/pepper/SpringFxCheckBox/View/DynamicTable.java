package com.pepper.SpringFxCheckBox.View;


import java.lang.reflect.Field;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.springframework.data.annotation.Transient;

public class DynamicTable<T> extends TableView<T> {

    public DynamicTable(Pane parent, Class<T> entityClass) 
    {
        super();
        parent.getChildren().add(this);
        
        Field[] fields = entityClass.getDeclaredFields(); // lekéri az entitás változóit (aminek van gettere
        for (Field field : fields) 
        {       //mieza szintetikus ??
            if (!field.isSynthetic() && !field.isAnnotationPresent(Transient.class)) 
            {
                field.setAccessible(true); // Allow access to private fields if needed
                String header = field.getName(); // Use the field name as the column header
                String propertyName = field.getName();
                int width = 100; // Set a default width, you can change this as needed

                TableColumn<T, String> column = new TableColumn<>(header);
                column.setCellValueFactory(cellData -> {
                    try 
                    {
                        Object value = field.get(cellData.getValue());
                        return value != null ? new SimpleStringProperty(value.toString()) : new SimpleStringProperty("");
                    }
                    catch (IllegalAccessException e) 
                    {
                        e.printStackTrace();
                        return new SimpleStringProperty("");
                    }
                });
                column.setMinWidth(width);
                getColumns().add(column);
            }
        }
    }

    public void setItems(List<T> items) {
        getItems().clear();
        getItems().setAll(items);
    }
}
