package com.pepper.SpringFxCheckBox.View;

import com.pepper.SpringFxCheckBox.Model.DTO;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class DynamicTable<T> extends TableView<T> 
{
    private List<String> colLabel = new ArrayList<>();
    int width = 100;
      
    public DynamicTable(Pane parent, Class<T> entityClass, List<String> selectedColumns, EntityHandler<T> entityHandler)
    {
        super();
        parent.getChildren().add(this);
        this.prefHeightProperty().bind(parent.heightProperty());
        colLabel = entityHandler.getColLabel(); //resultSetből kinyert alias vagyis ColumnLabel
        
        if(entityClass == DTO.class)
        {
            for(String col : colLabel)
            {
                System.out.println("DynamicT with DTO entered");
                TableColumn<T, String> column = new TableColumn<>(col);
                column.setCellValueFactory(cellData -> {                    
                    Object value = ((DTO) cellData.getValue()).getProperties().get(col);
                    return value != null ? new SimpleStringProperty(value.toString()) : new SimpleStringProperty("");
                });
                column.setMinWidth(USE_PREF_SIZE);
                getColumns().add(column);
            }
        } else 
        {
            Field[] fields = entityClass.getDeclaredFields();

            int i = 0;
            for(Field field : fields)
            {                        //név lista ahogy adatbázisban vannak,tartalmazza a field.getName-t
                if(selectedColumns != null && selectedColumns.contains(field.getName())) //Custom Columns
                {

                    System.out.println("CUSTOM dynamicT ENTERED");
                    field.setAccessible(true);
                    String header = field.getName();
                    if(!colLabel.isEmpty() && !header.equals(colLabel.get(i)))
                    {
                        header = colLabel.get(i);
                        if(i<colLabel.size()){
                        i++;
                    }  
                    } else {
                        if(i<colLabel.size()){
                        i++;}
                    }

                    
                    

                    TableColumn<T, String> column = new TableColumn<>(header);
                    column.setCellValueFactory(cellData ->
                    {
                        try {
                            Object value = field.get(cellData.getValue());
                            System.out.println("value-dynamicTable: " + value + " " + " fieldName: " + field.getName());
                            return value != null ? new SimpleStringProperty(value.toString()) : new SimpleStringProperty(""); 
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            System.out.println("IllegalAccessException: " + e.getMessage());
                            return new SimpleStringProperty("");                        
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            System.out.println("IllegalArgumentException: " + e.getMessage());
                            return new SimpleStringProperty("");
                        }
                    });
                column.setMinWidth(width);
                getColumns().add(column);
                } else if(selectedColumns == null || selectedColumns.isEmpty())// SELECT *
                {
                    System.out.println("dynamic table else condition- SELECT *");
                    field.setAccessible(true); // private változókhoz hozzáférés
                    String header = field.getName(); // field név: oszlop cím
                    String propertyName = field.getName(); //
                    int width = 100;                
                    //TableColumn<Income, Integer> idCol = new TableColumn<>("ID");
                    TableColumn<T, String> column = new TableColumn<>(header);
                    //idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                    column.setCellValueFactory(cellData -> {
                        try 
                        {   // itt derül ki a változók tulajdonsága
                            //Java reflection minden fieldet Object típusként kezel függetlenül a field tulajdonságától(String, int..)
                            //field.get(cellData.getValue()); egy Objectet ad vissza
                            Object value = field.get(cellData.getValue());
                            //azt az esetet kezeli amikor lehet null értéke egy cellának, akkor egy üres String jelenik meg 'null' helyett
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
        
        System.out.println("DYNAMIC TABLE END");
    }

    public void setItems(List<T> items) {
        getItems().clear();
        getItems().setAll(items);
    }
    private void setColLabel(List<String> columnAlias) {
        this.colLabel = columnAlias;
    }
}
/*
 public DynamicTable(Pane parent, Class<T> entityClass) 
    {
        super();
        parent.getChildren().add(this);
        
        Field[] fields = entityClass.getDeclaredFields(); // lekéri az entitás változóit (aminek van gettere
        for (Field field : fields) 
        {       //!field.isSynthetic(): enumok kiszűrése és minden más változó kiszűrése amit nem én declaráltam osztályváltozóként
            if (!field.isSynthetic() && !field.isAnnotationPresent(Transient.class)) 
            {
                field.setAccessible(true); // private változókhoz hozzáférés
                String header = field.getName(); // field név: oszlop cím
                String propertyName = field.getName(); //
                int width = 100;
                
                //TableColumn<Income, Integer> idCol = new TableColumn<>("ID");
                TableColumn<T, String> column = new TableColumn<>(header);
                //idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                column.setCellValueFactory(cellData -> {
                    try 
                    {   // itt derül ki a változók tulajdonsága
                        //Java reflection minden fieldet Object típusként kezel függetlenül a field tulajdonságától(String, int..)
                        //field.get(cellData.getValue()); egy Objectet ad vissza
                        Object value = field.get(cellData.getValue());
                        //azt az esetet kezeli amikor az adatbázisban lehet null értéke egy cellának, akkor egy üres String jelenik meg 'null' helyett
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
*/