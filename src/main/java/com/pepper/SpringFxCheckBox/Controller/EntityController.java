package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.ColumnNameContainer;
import com.pepper.SpringFxCheckBox.View.TextField;
import com.pepper.SpringFxCheckBox.Model.DynamicDTO;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import com.pepper.SpringFxCheckBox.Model.Model;
import com.pepper.SpringFxCheckBox.View.DynamicTable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class EntityController 
{
    private AppControllerChB P;
    private Model model;
    private String tableName;
    private List<String> colNames;
    private final List<String> selectedColumns;    
    private List<TextField> asTxTFList;
    private List<CheckBox> checkBoxes;
    private StringBuilder queryBuilder, originalQ;
    private String query;
    private Timer timer;
    private DynamicTable dynamicTable;
    private ColumnNameContainer colNContainer;    
    private HBox AScontainer, nameChbCont, container;
    private int tableIndex;
    
    
    public EntityController(AppControllerChB parent, Pane container, List<String> colNames)
    {
        this.colNames = colNames;
        this.selectedColumns = new ArrayList<>();
        this.P = parent;        
        model = AppCoreChB.getContext().getBean(Model.class);
        this.container = (HBox) container;
        
    }
    
    public void createColumnChb( String tableName, int index)
    {
        System.out.println("EntityController createColumn checkboxes");
        AppControllerChB.getConnection();
        this.tableName = tableName;
        tableIndex = index;
        
        checkBoxes = new ArrayList<>();
        
        colNContainer = new ColumnNameContainer(container); //checkboxokat tartalmazó konténer létrehozása
        applyFadeInAnimation(colNContainer);
        AScontainer = colNContainer.getAsTFcontainer();        
        nameChbCont = colNContainer.getColNameChbContainer();        
        
        for(int i = 0; i < colNames.size(); i++)
        {
            checkBoxes.add(new CheckBox(colNames.get(i)));
            checkBoxes.get(i).getStyleClass().add("checkbox");
            nameChbCont.getChildren().add(checkBoxes.get(i)); //
        }
        
        addSelectedColumnOnAction(checkBoxes);
        inflateCombobox(P.getOrderByCB(), colNames);
        
        timer = new Timer();
        timer.schedule(new TimerTask() 
        {
            @Override
            public void run() 
            {
                Platform.runLater(() -> {
                    createAStxtField();
                });
            }
        }, 50);
    }
    private void addSelectedColumnOnAction(List<CheckBox> chb) 
    {
        final Map<Integer, AtomicBoolean> booleans = new HashMap<>();
        List<Integer> selectedIndices = new ArrayList<>();
        for(int i = 0; i < chb.size(); i++) // AtomicBoolean dinamikus létrehozása
        {
            AtomicBoolean atcBoolean = new AtomicBoolean(false);
            booleans.put(i, atcBoolean);
        }         
        for(int i = 0; i < chb.size(); i++) // checkbox.setOnAction
        {
            final int index = i;
            chb.get(i).setOnAction((event) -> // selectedColumns List amibe pakolom a Col neveket a queryBuildernek
            {//
                if(chb.get(index).isSelected() && !booleans.get(index).get()) 
                {
                    if (!selectedIndices.contains(index)) {
                        selectedIndices.add(index);
                    }
                } else// ha megszünt a kijelölést törli
                {
                    selectedIndices.remove(Integer.valueOf(index));
                    booleans.get(index).set(false);                    
                }
                Collections.sort(selectedIndices);
                selectedColumns.clear();
                for (Integer x : selectedIndices) 
                {
                    if (x < colNames.size()) {
                        selectedColumns.add(colNames.get(x));
                    }
                }
            });
        }
    } 
    public void createAStxtField()
    {        
        asTxTFList = new ArrayList<>(); //AS txtFieldek        
            for(int i = 0; i < colNames.size(); i++)
            {   //checkBoxes.get(i).getWidth() + 
                double chbWidth = checkBoxes.get(i).getLayoutBounds().getWidth();                
                TextField asTxt = new TextField(AScontainer, chbWidth);
                asTxTFList.add(asTxt);
            }
        stopTimer();
    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void clearCheckBoxes()
    {
        applyFadeOutAnimation(colNContainer);
        //colNContainer.getChildren().clear();
        selectedColumns.clear();
    }
    
    
    public int getChbIndex(String checkBoxTxt)
    {
        int index = -1;
        for(int i = 0; i < checkBoxes.size(); i++){
            if(checkBoxes.get(i).getText().equals(checkBoxTxt)){
                index = i;
                break;
            }
        }
        return index; //végigmegyünk a checkbox list szövegein, ahol equals selectedColumn, annak az indexét adja vissza és az alapján kérjük le az aliasTF szövegét
    }
    
    public void buildQuery(String tableName) // ez egy queryBuilder o.o
    {
        queryBuilder = new StringBuilder("SELECT ");        
        for(int i = 0; i < selectedColumns.size(); i++) // oszlop nevek hozzáadása
        {                            //lekérjük a checkbox indexét 
            String alias = asTxTFList.get(getChbIndex(selectedColumns.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // HA megadtak ALIAS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                if(P.getAggregateMap().containsKey(selectedColumns.get(i))) //SUM,AVG, etc
                {   
                    String aggregateFunction = P.getAggregateMap().get(selectedColumns.get(i));
                    queryBuilder.append(aggregateFunction).append("(").append(selectedColumns.get(i)).append(")").append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..                    
                }
                else
                {
                    queryBuilder.append(selectedColumns.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
                }
            }
            else if(P.getAggregateMap().containsKey(selectedColumns.get(i))) //AGGREGATE CLAUSE
            {   // SUM, AVG, etc
                String aggregateFunction = P.getAggregateMap().get(selectedColumns.get(i));
                queryBuilder.append(aggregateFunction).append("(").append(selectedColumns.get(i)).append(")");
            }
            else {queryBuilder.append(selectedColumns.get(i));} 

            if( i < selectedColumns.size()-1 ){ // utolsó előtti elemig ", " ad hozzá
                queryBuilder.append(", ");
            }
        }
        if( selectedColumns.size() <= 0){ // ha nincs oszlop kijelölve 
            queryBuilder.append(" * FROM " + tableName);
        } else {
            queryBuilder.append(" FROM " + tableName);
        }        
        
        // WHERE *** IS NULL
        /*if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null && P.getAndOrTF().getText() == null)
        {
            queryBuilder.append(" WHERE ").append(P.getWhereCB().getValue()).append(" IS NULL");
        }
        
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB().getValue() != null && P.getThanTxt().getText() != null && P.getwhereOpCB().getValue() != null && P.getAndOrTF().getText() == null)
        {/*
            String whereColName = P.getWhereCB().getValue();            
            String operator = P.getwhereOpCB().getSelectionModel().getSelectedItem();            
            // Check if the user entered a numeric value
            try 
            {
                int value = Integer.parseInt(P.getThanTxt().getText(), 10); // You can use other numeric types if needed
                
                // Append the appropriate condition based on the operator
                switch (operator) 
                {
                    case ">" -> queryBuilder.append( " WHERE ").append(whereColName).append(" > ").append(value);
                    case "<" -> queryBuilder.append( " WHERE ").append(whereColName).append(" < ").append(value);
                    case "=" -> queryBuilder.append( " WHERE ").append(whereColName).append(" = ").append(value);
                    case "<=" -> queryBuilder.append( " WHERE ").append(whereColName).append(" <= ").append(value);
                    case ">=" -> queryBuilder.append( " WHERE ").append(whereColName).append(" <= ").append(value);
                    default ->{}
                }
                // Handle unsupported operator or show an error message to the user
            } catch (NumberFormatException e) {
                System.out.println("number format exception");
                // Handle the case where the user entered a non-numeric value in the textfield
                // Show an error message to the user, or handle it based on your application's requirements
            }
        }*/
        if(P.getAndOrTF().getText() != null)
        {
            queryBuilder.append( " WHERE ").append(P.getAndOrTF().getText());
        }
        // GROUP BY
        if(P.getGroupByCB().getValue() != null){
            
            if(!P.getGroupTF().getText().isEmpty())
            {
                String orderBy = P.getGroupTF().getText();
                int length = orderBy.length();
                String groupByReady = orderBy.substring(0, length - 2); //", "
                queryBuilder.append(" GROUP BY ").append(groupByReady);
            } else {
                queryBuilder.append(" GROUP BY ").append(P.getGroupByCB().getValue());
            }            
        }
        // ORDER BY
        if(!P.getOrderByTF().getText().isEmpty())
        {
            String orderBy = P.getOrderByTF().getText();
            int length = orderBy.length();
            String orderByReady = orderBy.substring(0, length - 2); //", "
            queryBuilder.append(" ORDER BY ").append(orderByReady);
        }
        //LIMIT
        if(!P.isLimitSelected()){
            queryBuilder.append(" LIMIT ").append(P.getTopValue());
        }
        originalQ = queryBuilder;
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
    }
    
    public void expectoResult()
    {
        System.out.println("Entity controller expectoResult triggered");
        deleteTable();
        if(dynamicTable != null &&  !dynamicTable.getColumns().isEmpty())
        {
            dynamicTable.getItems().clear();
            dynamicTable.getColumns().clear();
        }
        
        String query = P.getQueryTxtArea().getText();
        ExecuteQuery eq = new ExecuteQuery();
        EntityHandler entHand = new EntityHandler(DynamicDTO.class);
        List<DynamicDTO> list = eq.executeQuery(query, DynamicDTO.class, selectedColumns, entHand);

        dynamicTable = new DynamicTable<>(P.getRoot(), DynamicDTO.class, selectedColumns, entHand);
        dynamicTable.setItems(list);
        applyFadeInAnimation(dynamicTable);
    }
    public void deleteTable()
    {
        TableView<?> table = null;
        for(Node node : P.getRoot().getChildren()) //Node: elemek gyűjtőneve
        {
            if(node instanceof TableView<?>)
            {
                table = (TableView<?>) node;
                P.getRoot().getChildren().remove(table);
                
                break;
            }
        }
    }
    /*private void applyFadeInAnimation(CheckBox checkBox) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), checkBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }*/
    private void applyFadeOutAnimation(ColumnNameContainer container) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), container);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(event -> {
        colNContainer.getChildren().clear(); // Clear the container after the fade-out animation
        // You can also add the checkboxes back here if needed
        });
        fadeTransition.play();
    } //ColumnNameContainer
    private void applyFadeInAnimation(ColumnNameContainer container) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), container);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
    private void applyFadeInAnimation(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), node);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
    
    private void inflateCombobox(ComboBox<String> cb, List<String> nameList)
    {
        if(cb.getItems().size() == 1){
            cb.getItems().addAll(nameList);
        }
    }
    
    public String getTableName(){
        return tableName;
    }
    public int getTableIndex(){
        return tableIndex;
    }
    public List<String> getColNames() {
       return colNames;
    }
    public List<String> getSelectedColumns() 
    {   
        if(selectedColumns.isEmpty()){System.out.println("selectedColumns IS NULL inside EntityController");}
        return selectedColumns;
    }
    public boolean isSelectedColNull(){
        return selectedColumns.isEmpty();
    }
    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
    public List<TextField> getAsTxtList() {
        return asTxTFList;
    }
}
