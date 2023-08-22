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


public class PartnerController 
{
    private AppControllerChB P;
    private Model model;
    private List<String> prtColNames;    
    private final List<String>selectedColumns;
    private List<TextField> asTxtList;
    private List<CheckBox> checkBoxes;
    private String query;
    StringBuilder queryBuilder;
    private boolean whereAdded, orderAdded, groupAdded;
    private Timer timer;
    DynamicTable dynamicTable;
    
    public PartnerController(AppControllerChB parent)
    {
        this.selectedColumns = new ArrayList<>();
        this.prtColNames = new ArrayList<>();
        this.P = parent;
        model = AppCoreChB.getContext().getBean(Model.class);
        whereAdded = orderAdded = groupAdded = false;
    }
    
    /*public void createPrtCheckBoxes()
    {
        prtColNames = model.getColumnNames("db__partners");
        checkBoxes = new ArrayList<>();
        
        for(int i = 0; i < prtColNames.size(); i++)
        {
            checkBoxes.add(new CheckBox(prtColNames.get(i)));
        }
        
        for( int i = 0; i < checkBoxes.size(); i++)
        {
            CheckBox chb = checkBoxes.get(i);
            P.getPrtChbContainer().getChildren().add(chb);
        }
        createSelectColumnOnAction(checkBoxes);
        
        //comboboxok feltöltése        
        inflateCombobox(P.getOrderByCB1(), prtColNames, orderAdded); //ezeket nem tölti fel mert size()>1...
        inflateCombobox(P.getGroupByCB(), prtColNames, groupAdded);
        
        timer = new Timer();
        timer.schedule(new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(() -> {
                createAStxtField();
            });
        }
        }, 200);
    }
    public void createAStxtField()
    {
        asTxtList = new ArrayList<>();
        
            for(int i = 0; i < prtColNames.size(); i++)
            {   //checkBoxes.get(i).getWidth() + 
                double chbWidth = checkBoxes.get(i).getLayoutBounds().getWidth();
                System.out.println(checkBoxes.get(i).getWidth() + " " + checkBoxes.get(i).getLayoutBounds().getWidth());
                TextField asTxt = new TextField(P.getAsPrt(), chbWidth);
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
    public void clearCheckBoxes() // ez kell
    {        
        P.getPrtChbContainer().getChildren().clear();
    }*/
    
    private void createSelectColumnOnAction(List<CheckBox> chb) 
    {
        final Map<Integer, AtomicBoolean> booleans = new HashMap<>();
        for(int i = 0; i < chb.size(); i++)
        {
            AtomicBoolean atcBoolean = new AtomicBoolean(false);
            booleans.put(i, atcBoolean);
        }
        for(int i = 0; i < chb.size(); i++)
        {
            final int index = i;
            chb.get(i).setOnAction(event ->
            {
                if(chb.get(index).isSelected() && !booleans.get(index).get())
                {
                    if(index < selectedColumns.size())
                    {
                        selectedColumns.add(index,prtColNames.get(index));// ha kiválaszt egy oszlopot hozzáadja egy List<String>-hez
                        booleans.get(index).set(true);
                    } else {
                        selectedColumns.add(prtColNames.get(index));
                        booleans.get(index).set(true);
                    }
                } else 
                {
                    selectedColumns.remove(prtColNames.get(index));
                    booleans.get(index).set(false);
                }
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
        return index;
    }
    
 /*   public void buildQuery() // ez egy queryBuilder o.o TO FIX: ha semmit nem jelölnek ki akkor is lefut 
    {
        queryBuilder = new StringBuilder("SELECT ");
        for(int i = 0; i < selectedColumns.size(); i++) //kiválasztott oszlopok közé vesszőt szór
        {
            String alias = asTxtList.get(getChbIndex(selectedColumns.get(i))).getText().trim();
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            { 
                if(P.getAggregateMap().containsKey(selectedColumns.get(i)))
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
            else 
            {
                queryBuilder.append(selectedColumns.get(i));
            }
            
            if(i < selectedColumns.size()-1 )
            {
                queryBuilder.append(", ");
            }            
        }
        if( selectedColumns.size() <= 0)
        {
            queryBuilder.append(" * FROM db__partners");
        } else {
            queryBuilder.append(" FROM db__partners");
        }
        // WHERE IS NULL
        if(P.getWhereCB1().getValue() != null && P.isNull1() && P.getwhereOpCBValue1() == null){
            queryBuilder.append(" WHERE ").append(P.getWhereCB1().getValue()).append(" IS NULL");            
        }
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB1().getValue() != null && P.getThanTxt1().getText() != null && P.getWhereOpCB1().getValue() != null)
        {
            String whereColName = P.getWhereCB1().getValue();            
            String operator = P.getWhereOpCB1().getSelectionModel().getSelectedItem();
            try 
            {
                int value = Integer.parseInt(P.getThanTxt1().getText(), 10);
                
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
        if(P.getOrderByCB1().getValue() != null)
        {
            
            if(!P.getOrderByTF().getText().isEmpty())
            {
                String orderBy = P.getOrderByTF().getText();
                int length = orderBy.length();
                String orderByReady = orderBy.substring(0, length - 2); //", "
                queryBuilder.append(" ORDER BY ").append(orderByReady);
            } else {
                queryBuilder.append(" ORDER BY ").append(P.getOrderByCB1().getValue());
            }
            
        }
        if(P.descIsSelected() && P.getOrderBy() != null) {
            queryBuilder.append(" DESC "); // itt van egy extra szóköz ha DESC is belekerül
        }
        if(!P.isLimitSelected()){
            queryBuilder.append(" LIMIT ").append(P.getTopValue());
        }
        
        
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
    }*/
    
    public void expectoResult()
    {
        System.out.println("Partner controller expectoResult triggered");
        deleteTable();
        if(dynamicTable != null && !dynamicTable.getColumns().isEmpty())
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
    }
    public void deleteTable()
    {
        TableView<?> table = null;
        for(Node node : P.getRoot().getChildren())
        {
            if(node instanceof TableView<?>)
            {
                table = (TableView<?>) node;
                P.getRoot().getChildren().remove(table);
                break;
            }
        }
    }

    public List<String> getPrtColNames() {
        return prtColNames;
    }
    public List<String> getSelectedColumns() {
        return selectedColumns;
    }
    public boolean isSelectedColNull(){
        return selectedColumns.isEmpty();
    }
    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
    public List<TextField> getAsTxtList() { // nincs kész
        return asTxtList;
    }
    
    private void inflateCombobox(ComboBox<String> comboBox, List<String> namesList, boolean isAdded) 
        {
            if(comboBox.getItems().size() == 1 && !isAdded){
                comboBox.getItems().addAll(namesList);
                isAdded = true;
            }        
        }

}
