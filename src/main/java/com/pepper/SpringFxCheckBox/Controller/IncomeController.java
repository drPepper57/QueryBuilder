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


public class IncomeController 
{
    private AppControllerChB P;
    private Model model;
    private TextArea queryTxtArea;
    private List<String> inColNames, pufferList;
    private final List<String> selectedColumns;    
    private List<String> inColJoinNames;
    private List<TextField> asTxtList;
    private List<CheckBox> checkBoxes;
    StringBuilder queryBuilder, originalQ;
    private String query;
    
    public IncomeController(AppControllerChB parent)
    {
        this.inColNames = new ArrayList<>();
        this.selectedColumns = new ArrayList<>();
        this.pufferList = new ArrayList<>();        
        this.P = parent;        
        model = AppCoreChB.getContext().getBean(Model.class);
    }  
    
    
    public void createIncCheckBoxes() // checkboxok dinamikus létrehozása hozzáadása szöveggel parenthez, onAction nélkül
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
        //HIBA ahányszor kijelölik a táblát annyiszor adja hozzá
        inflateCombobox(P.getOrderByCB(), inColNames);//comboboxok feltöltése
        inflateCombobox(P.getWhereCB(), inColNames);
        inflateCombobox(P.getGroupByCB(), inColNames);  
        
        Platform.runLater(() -> {createAStxtField(); });
    }    
    public void clearCheckBoxes() // ez kell
    {
        System.out.println("clearCheckBoxes() is triggered");
        P.getIncChBContainer().getChildren().clear();
        selectedColumns.clear();
    }    
    
    
    
    public void addSelectedColumnOnAction(List<CheckBox> chb) // onAction hozzáadása checkboxokhoz
    {
        final Map<Integer, AtomicBoolean> booleans = new HashMap<>();      // lehet nem is kell AtomicBoolean ...
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
                    selectedColumns.add(inColNames.get(index)); // ha kiválaszt egy oszlopot hozzáadja egy List<String>-hez
                    booleans.get(index).set(true);
                } else //
                {
                    selectedColumns.remove(inColNames.get(index));
                    booleans.get(index).set(false); 
                    
                } // ha megszünt a kijelölést törli
            });
        }
        //Platform.runLater(() -> {createAStxtField(); });
    }
    public void createAStxtField()
    {
        asTxtList = new ArrayList<>(); //AS txtFieldek
        
            for(int i = 0; i < inColNames.size(); i++)
            {   //checkBoxes.get(i).getWidth() + 
                double chbWidth = checkBoxes.get(i).getLayoutBounds().getWidth();
                System.out.println(checkBoxes.get(i).getWidth() + " " + checkBoxes.get(i).getLayoutBounds().getWidth());
                TextField asTxt = new TextField(P.getAsInc(), chbWidth);
                asTxtList.add(asTxt);
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
    /*
    SELECT i.id, i.amount, i.created, i.approved, p.name AS partner_name, p.contact AS partner_contact
    FROM db__income i
    JOIN db__partners p ON i.partner = p.id;
    */
    public void buildQuery() // ez egy queryBuilder o.o TO FIX: ha semmit nem jelölnek ki akkor is lefut 
    {
        queryBuilder = new StringBuilder("SELECT ");
        
        if(P.getJoinCBValue() != null) // ha kiválasztottak join table-t
        {
            createInColJoinNames(); // ez nincs jó helyen
            for(int i = 0; i < inColJoinNames.size(); i ++)
            {
                System.out.println(inColJoinNames.get(i));
                String alias = asTxtList.get(getChbIndex(selectedColumns.get(i))).getText().trim(); //AS txtField tartalma
                if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
                { 
                    queryBuilder.append(inColJoinNames.get(i)).append(" AS ").append(alias); // ..ColName AS alias..

                } else {queryBuilder.append(inColJoinNames.get(i));} 
                if( i < selectedColumns.size()-1 ) // az utolsó előtti elemig ", " ad hozzá
                {
                    queryBuilder.append(", ");
                }
            }  
            
            queryBuilder.append(" FROM db_income ").append(P.getJoinAS0()).append(" JOIN db__partners ").append(P.getJoinAS1()).append(" ON ").append(P.getJoinAS0() +"."+ P.getOnCB0()).append(" = ").append(P.getJoinAS1()+ "." + P.getOnCB1());
        }
        else
        {
            for(int i = 0; i < selectedColumns.size(); i++) // oszlop nevek hozzáadása
            {
                String alias = asTxtList.get(getChbIndex(selectedColumns.get(i))).getText().trim(); //AS txtField tartalma
                if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
                { 
                    queryBuilder.append(selectedColumns.get(i)).append(" AS ").append(alias); // ..ColName AS alias..

                } else {queryBuilder.append(selectedColumns.get(i));} 


                if( i < selectedColumns.size()-1 ) // első elem után ", " ad hozzá
                {
                    queryBuilder.append(", ");
                }
            }
            if( selectedColumns.size() <= 0) // ha nincs oszlop kijelölve 
            {
                queryBuilder.append(" * FROM db_income");
            } else {
                queryBuilder.append(" FROM db_income");
            }
        }
        /*
        SELECT column1, column2, ...
        FROM table_name
        WHERE condition1
        AND/OR condition2
        AND/OR condition3
        ...;  
        
        SELECT product_name, unit_price
        FROM products
        WHERE (category = 'Electronics' AND unit_price > 500)
        OR (category = 'Appliances' AND unit_price > 300);*/
        // WHERE *** IS NULL
        if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null)
        {            
            /* String operator = parent.getwhereOpCBValue(); //törlés StringBuilderből: String lekérése
            int operatorIndex = queryBuilder.indexOf(operator); // String indexe, ez a két adat kell törléshez            
            if(operatorIndex >= 0 && valueIndex >= 0){
                queryBuilder.delete(operatorIndex, operatorIndex + operator.length());            
            }*/
            queryBuilder.append(" WHERE ").append(P.getWhereCB().getValue()).append(" IS NULL");            
        }
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB().getValue() != null && P.getThanTxt().getText() != null && P.getwhereOpCB().getValue() != null)
        {            
            String whereColName = P.getWhereCB().getValue();            
            String operator = P.getwhereOpCB().getSelectionModel().getSelectedItem();            
            // Check if the user entered a numeric value
            try {
            int value = Integer.parseInt(P.getThanTxt().getText(), 10); // You can use other numeric types if needed
                System.out.println(value);
                // Append the appropriate condition based on the operator
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
                        // Handle unsupported operator or show an error message to the user
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("number format exception");
                // Handle the case where the user entered a non-numeric value in the textfield
                // Show an error message to the user, or handle it based on your application's requirements
            }
        }
        /*
        SELECT column1, column2, ...
        FROM table_name
        WHERE condition
        ORDER BY column1 ASC/DESC, column2 ASC/DESC, ...;*/
        
        if(P.getGroupByCB().getValue() != null){
            queryBuilder.append(" GROUP BY ").append(P.getGroupByCB().getValue());
        }
        
        // ORDER BY
        if(P.getOrderBy() != null){
            
            if(!P.getOrderByTF().getText().isEmpty())
            {
                String orderBy = P.getOrderByTF().getText();
                int length = orderBy.length();
                String orderByReady = orderBy.substring(0, length - 2); //", "
                queryBuilder.append(" ORDER BY ").append(orderByReady);
            } else {
                queryBuilder.append(" ORDER BY ").append(P.getOrderByCB().getValue());
            }
            
        } 
              
        
        if(!P.isLimitSelected()){
            queryBuilder.append(" LIMIT ").append(P.getTopValue());
        }
        originalQ = queryBuilder;
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
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
    
    public List<String> createInColJoinNames()
    {
        System.out.println("createInColJoinNames triggered in IncomeController");
        String a = P.getJoinAS0();

        inColJoinNames = new ArrayList<>();
        for(int i = 0; i < selectedColumns.size(); i++){
            inColJoinNames.add(a + "." + selectedColumns.get(i));
            System.out.println(inColJoinNames.get(i));
        }
        return inColJoinNames;
    }
    
    
    
    /*
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
