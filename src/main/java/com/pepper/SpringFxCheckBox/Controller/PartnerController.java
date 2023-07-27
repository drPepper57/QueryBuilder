package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.TextField;
import com.pepper.SpringFxCheckBox.Model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;


public class PartnerController 
{
    private AppControllerChB P;
    private Model model;
    private List<String> prtColNames;    
    private final List<String>selectedColumns;
    private List<TextField> asTxtList;
    private List<CheckBox> checkBoxes;
    private String query;
    private TextArea queryTxtArea; // utóbbi 2 nem biztos, h kell
    
    public PartnerController(AppControllerChB parent)
    {
        this.selectedColumns = new ArrayList<>();
        this.prtColNames = new ArrayList<>();
        this.P = parent;
        model = AppCoreChB.getContext().getBean(Model.class);
    }
    
    public void createPrtCheckBoxes()
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
        Platform.runLater(() -> {createAStxtField(); });
        //comboboxok feltöltése
        inflateCombobox(P.getWhereCB1(), prtColNames); //
        inflateCombobox(P.getOrderByCB(), prtColNames); //ezeket nem tölti fel mert size()>1...
        inflateCombobox(P.getGroupByCB(), prtColNames);
    }
    public void clearCheckBoxes() // ez kell
    {        
        P.getPrtChbContainer().getChildren().clear();
    }
    
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
                    selectedColumns.add(prtColNames.get(index));
                    booleans.get(index).set(true);
                } else { selectedColumns.remove(prtColNames.get(index)); booleans.get(index).get();}
            });
        }
    }
    public void createAStxtField()
    {
        asTxtList = new ArrayList<>();
        Platform.runLater(() -> {
            for(int i = 0; i < prtColNames.size(); i++)
            {   //checkBoxes.get(i).getWidth() + 
                double chbWidth = checkBoxes.get(i).getLayoutBounds().getWidth();
                System.out.println(checkBoxes.get(i).getWidth() + " " + checkBoxes.get(i).getLayoutBounds().getWidth());
                TextField asTxt = new TextField(P.getAsPrt(), chbWidth);
                asTxtList.add(asTxt);
            }
        });
        
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
    
    public void buildQuery() // ez egy queryBuilder o.o TO FIX: ha semmit nem jelölnek ki akkor is lefut 
    {
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for(int i = 0; i < selectedColumns.size(); i++) //kiválasztott oszlopok közé vesszőt szór
        {
            String alias = asTxtList.get(getChbIndex(selectedColumns.get(i))).getText().trim();
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
                { 
                    queryBuilder.append(selectedColumns.get(i)).append(" AS ").append(alias); // ..ColName AS alias..

                } else {queryBuilder.append(selectedColumns.get(i));}
            
            if(i < selectedColumns.size()-1 )
            {
                queryBuilder.append(", ");
            }            
        }
        if( selectedColumns.size() <= 0)
        {
            queryBuilder.append(" * FROM db_partner");
        } else {
            queryBuilder.append(" FROM db_partner");
        }
        // WHERE IS NULL
        if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null){
            queryBuilder.append(" WHERE ").append(P.getWhereCB().getValue()).append(" IS NULL");            
        }
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB().getValue() != null && P.getThanTxt().getText() != null && P.getwhereOpCB().getValue() != null)
        {
            String whereColName = P.getWhereCB().getValue();            
            String operator = P.getwhereOpCB().getSelectionModel().getSelectedItem();
            try 
            {
                int value = Integer.parseInt(P.getThanTxt().getText(), 10);
                
                switch (operator) 
                {
                    case ">": 
                        queryBuilder.append( " WHERE ").append(whereColName).append(" > ").append(value);
                        break;
                    case "<":
                        queryBuilder.append( " WHERE ").append(whereColName).append(" < ").append(value);
                        break;
                    case "=":
                        queryBuilder.append( " WHERE ").append(whereColName).append(" = ").append(value);
                        break;
                    case "<=":
                        queryBuilder.append( " WHERE ").append(whereColName).append(" <= ").append(value);
                        break;
                    case ">=":
                        queryBuilder.append( " WHERE ").append(whereColName).append(" <= ").append(value);
                        break;                                        
                    default:                       
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("number format exception");
                // Handle the case where the user entered a non-numeric value in the textfield
                // Show an error message to the user, or handle it based on your application's requirements
            }
        }
        // ORDER BY
        if(P.getOrderBy() != null){
            queryBuilder.append(" ORDER BY ").append(P.getOrderBy());
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
    }
    
    private void inflateCombobox(ComboBox<String> comboBox, List<String> namesList) 
    {
        if(comboBox.getItems().size() == 1){
            comboBox.getItems().addAll(namesList);
        }        
    }

    public List<String> getPrtColNames() {
        return prtColNames;
    }
    public List<String> getSelectedColumns() {
        return selectedColumns;
    }
    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }
    public List<TextField> getAsTxtList() { // nincs kész
        return asTxtList;
    }
    


}
