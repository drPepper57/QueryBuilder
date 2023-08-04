package com.pepper.SpringFxCheckBox.Gui;

//import com.pepper.fxguiclasses.Person;
import java.util.function.BiConsumer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;


public class Table <T> extends TableView //table definiálásakor meg kell adni milyen entitást használ majd(<T>)
{
    
    //oszlopokat sorokat vagy gombokat tudjunk könnyebben hozzáadni
    public Table(Pane parent) // konstruktor: ez az osztály Table objektumként viselkedik amit hozzá kell adni egy containerhez
    {
        super();
        parent.getChildren().add(this);
    }
    
    public void addColumn(String header, String propertyName, int width) // oszlop létrehozása: Cím, változó név amit hozzá akarunk majd adni
    {
        TableColumn<T, String> column = new TableColumn<>(header);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setMinWidth(width);
        getColumns().add(column);
    }
    
    public void addActionColumn(String buttonText, BiConsumer<T, Integer> onClick) // gomb hozzáadása
    {
        TableColumn<T, String> column = new TableColumn<>();
        
        Callback<TableColumn<T, String>, TableCell<T, String>> factory;
        factory = new Callback<>() 
        {
            
            @Override
            public TableCell<T, String> call(TableColumn<T, String> param) 
            {
                TableCell<T, String> cell = new TableCell<>()
                {
                    @Override
                    protected void updateItem(String item, boolean empty) 
                    {
                        super.updateItem(item, empty); 

                        if(empty)
                        {
                            setGraphic(null);
                        }
                        else
                        {
                            Button button = new Button(buttonText);
                            button.setOnAction(evt -> 
                            {
                                int index = getIndex();
                                T entity = getTableRow().getItem();

                                onClick.accept(entity, index);
                            });

                            setGraphic(button);
                        }
                        setText(null);
                    }
                };
                return cell;
            }
            
        };
        column.setCellFactory(factory);
        getColumns().add(column);
    }
    
    public void addItem(T item) // elem hozzáadása
    {
        getItems().add(item);
    }
    
    public void setPlaceHolder(String placeHolder)
    {
        setPlaceholder(new Label(placeHolder));
    }
    
    
}
