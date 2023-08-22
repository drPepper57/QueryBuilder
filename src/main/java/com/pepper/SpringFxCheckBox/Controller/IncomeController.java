package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.TextField;
import com.pepper.SpringFxCheckBox.Model.DynamicDTO;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import com.pepper.SpringFxCheckBox.Model.Model;
import com.pepper.SpringFxCheckBox.View.DynamicTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;


public class IncomeController 
{
    private AppControllerChB P;
    private Model model;    
    private List<String> inColNames;
    private final List<String> selectedColumns;    
    private List<String> inColJoinNames;
    private List<TextField> asTxtList;
    private List<CheckBox> checkBoxes;
    StringBuilder queryBuilder, originalQ;
    private String query;
    private Timer timer;
    DynamicTable dynamicTable;
    
    public IncomeController(AppControllerChB parent)
    {
        this.inColNames = new ArrayList<>();
        this.selectedColumns = new ArrayList<>();
        this.P = parent;        
        model = AppCoreChB.getContext().getBean(Model.class);
    }  
    
    
   /* public void createIncCheckBoxes() // checkboxok dinamikus létrehozása hozzáadása szöveggel parenthez, onAction nélkül
    {
        inColNames = model.getColumnNames("db__income");
        
        checkBoxes = new ArrayList<>();
        
        
        for(int i = 0; i < inColNames.size(); i++) // checkboxok létrehozása
        {
            checkBoxes.add(new CheckBox(inColNames.get(i)));         
        }
        
        for(int i = 0; i < checkBoxes.size(); i++) // checkboxok hozzáadása parenthez
        {
            CheckBox chb = checkBoxes.get(i);
            P.getIncChBContainer().getChildren().add(chb);
        }
        
        
        addSelectedColumnOnAction(checkBoxes);
        //HIBA ahányszor kijelölik a táblát annyiszor adja hozzá FIXED
        inflateCombobox(P.getOrderByCB(), inColNames);//comboboxok feltöltése        
        inflateCombobox(P.getGroupByCB(), inColNames);  
        
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
        }, 200);
    }
    public void createAStxtField()
    {
        asTxtList = new ArrayList<>(); //AS txtFieldek
        
            for(int i = 0; i < inColNames.size(); i++)
            {   //checkBoxes.get(i).getWidth() + 
                double chbWidth = checkBoxes.get(i).getLayoutBounds().getWidth();                
                TextField asTxt = new TextField(P.getAsInc(), chbWidth);
                asTxtList.add(asTxt);
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
        System.out.println("clearCheckBoxes() is triggered");
        P.getIncChBContainer().getChildren().clear();
        selectedColumns.clear();
    }    */
    
    public void addSelectedColumnOnAction(List<CheckBox> chb) // onAction hozzáadása checkboxokhoz
    {
        final Map<Integer, AtomicBoolean> booleans = new HashMap<>();        
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
                    if(index < selectedColumns.size())
                    {
                        selectedColumns.add(index,inColNames.get(index));// ha kiválaszt egy oszlopot hozzáadja egy List<String>-hez
                        booleans.get(index).set(true);
                    } else {
                        selectedColumns.add(inColNames.get(index));
                        booleans.get(index).set(true);
                    }
                } else //
                {
                    selectedColumns.remove(inColNames.get(index));
                    booleans.get(index).set(false); 
                    
                } // ha megszünt a kijelölést törli
            });
        }
    }
    
    public int getChbIndex(String chbTxt)
    {
        int index = -1;
        for(int i = 0; i < checkBoxes.size(); i++){
            if(checkBoxes.get(i).getText().equals(chbTxt)){
                index = i;
                break;
            }
        }
        return index; //végigmegyünk a checkbox list szövegein, ahol equals selectedColumn, annak az indexét adja vissza és az alapján kérjük le az aliasTF szövegét
    }
   
   /* public void buildQuery() // ez egy queryBuilder o.o TO FIX: ha semmit nem jelölnek ki akkor is lefut 
    {
        queryBuilder = new StringBuilder("SELECT ");        
        for(int i = 0; i < selectedColumns.size(); i++) // oszlop nevek hozzáadása
        {                            //lekérjük a checkbox indexét 
            String alias = asTxtList.get(getChbIndex(selectedColumns.get(i))).getText().trim(); //AS txtField tartalma
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
            queryBuilder.append(" * FROM db__income");
        } else {
            queryBuilder.append(" FROM db__income");
        }        
        
        // WHERE *** IS NULL
        if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null && P.getAndOrTF().getText() == null)
        {
            queryBuilder.append(" WHERE ").append(P.getWhereCB().getValue()).append(" IS NULL");
        }
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB().getValue() != null && P.getThanTxt().getText() != null && P.getwhereOpCB().getValue() != null && P.getAndOrTF().getText() == null)
        {
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
            } catch (NumberFormatException e) {
                System.out.println("number format exception");
                // Handle the case where the user entered a non-numeric value in the textfield
                // Show an error message to the user, or handle it based on your application's requirements
            }
        }
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
        if(P.getOrderBy() != null)
        {
            
            if(!P.getOrderByTF().getText().isEmpty())
            {
                String orderBy = P.getOrderByTF().getText();
                int length = orderBy.length();
                String orderByReady = orderBy.substring(0, length - 2); //", "
                queryBuilder.append(" ORDER BY ").append(orderByReady);
            }
            else
            {
                if(P.descIsSelected()){
                    queryBuilder.append(" ORDER BY ").append(P.getOrderByCB().getValue()).append(" DESC");
                } else{
                    queryBuilder.append(" ORDER BY ").append(P.getOrderByCB().getValue());
                }
            }
        }    
        //LIMIT
        if(!P.isLimitSelected()){
            queryBuilder.append(" LIMIT ").append(P.getTopValue());
        }
        originalQ = queryBuilder;
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
    }*/
    //TABLE
    public void expectoResult()
    {
        System.out.println("Income controller expectoResult triggered");
        deleteTable();        
        if(dynamicTable != null &&  !dynamicTable.getColumns().isEmpty())
        {
            dynamicTable.getItems().clear();
            dynamicTable.getColumns().clear();
        }
        
        String query = P.getQueryTxtArea().getText();
        ExecuteQuery eq = new ExecuteQuery();
        EntityHandler entHand = new EntityHandler(DynamicDTO.class);        
        List<DynamicDTO> list = eq.executeQuery(query, DynamicDTO.class, selectedColumns, entHand); // itt kell paraméterben a SelectedColumns de kell egy processResultSet verzió ami nem kap
                                                                             // selectedColumnst, ha Select * van -> selectedColumns == null
        dynamicTable = new DynamicTable<>(P.getRoot(), DynamicDTO.class, selectedColumns, entHand);
        dynamicTable.setItems(list);
    }
    public void deleteTable()
    {
        TableView<?> table = null;
        for(Node node : P.getRoot().getChildren()) //Node elemek
        {
            if(node instanceof TableView<?>)
            {
                table = (TableView<?>) node;
                P.getRoot().getChildren().remove(table);
                
                break;
            }
        }
    }
    
    public StringBuilder getOriginalQ() {
        return originalQ;
    }
    public String getQuery() {
        return query;
    }
    public List<String> getColNames() {
       return inColNames;
    }
    public List<String> getSelectedColumns() 
    {   
        if(selectedColumns.isEmpty()){System.out.println("selectedColumns IS NULL inside IncomeController");}
        return selectedColumns;
    }
    public boolean isSelectedColNull(){
        return selectedColumns.isEmpty();
    }
    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
    public List<TextField> getAsTxtList() {
        return asTxtList;
    }
    
    private void inflateCombobox(ComboBox<String> comboBox, List<String> namesList) 
    {
        if(comboBox.getItems().size() == 1){
            comboBox.getItems().addAll(namesList);
        }        
    }
    
    
    
    
    
    /*
    //törlés StringBuilderből:
     String operator = parent.getwhereOpCBValue(); //törlés StringBuilderből: String lekérése
            int operatorIndex = queryBuilder.indexOf(operator); // String indexe, ez a két adat kell törléshez            
            if(operatorIndex >= 0 && valueIndex >= 0){
                queryBuilder.delete(operatorIndex, operatorIndex + operator.length());            
            }
    
    public void selectColumn0() // EZEK AZ ONACTION FGVNYEK TXTAREA-hoz adnak hozzá stringeket, közvetetten a selectedColumns-szal
    {                           // lényegében csak stringeket adok hozzá egy  List<String> selectedColumns hoz
        if (checkBoxes.get(0).isSelected() && chb0b == false) {
        //selectedColumns.add("amount ");
        selectedColumns.add(getColNames().get(0));
        chb0b = true;
        
        }   else { selectedColumns.remove(getColNames().get(0)); chb0b = false; }
    }
    public void selectColumn1()
    {
        if (checkBoxes.get(1).isSelected() && chb1b == false) {
        selectedColumns.add(getColNames().get(1));
        chb1b = true;
        
        } else { selectedColumns.remove(getColNames().get(1)); chb1b = false; }
    }
    public void selectColumn2()
    {
        if (checkBoxes.get(2).isSelected() && chb2b == false) {
        selectedColumns.add(getColNames().get(2));
        chb2b = true;
        
        } else { selectedColumns.remove(getColNames().get(2)); chb2b = false; }
    }
    public void selectColumn3()
    {
        if (checkBoxes.get(3).isSelected() && chb3b == false) {
        selectedColumns.add(getColNames().get(3));
        chb3b = true;
        
        } else { selectedColumns.remove(getColNames().get(3)); chb3b = false; }
    }
    public void selectColumn4()
    {
        if (checkBoxes.get(4).isSelected() && chb4b == false) {
        selectedColumns.add(getColNames().get(4));
        chb4b = true;
        
        } else { selectedColumns.remove(getColNames().get(4)); chb4b = false; }
    }
    public void selectColumn5()
    {
       if (checkBoxes.get(5).isSelected() && chb5b == false) {
        selectedColumns.add(getColNames().get(5));
        chb5b = true;
        
        } else { selectedColumns.remove(getColNames().get(5)); chb5b = false; }
    }
*/

    

    
}
