package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.View.TextField;
import com.pepper.SpringFxCheckBox.Model.DynamicDTO;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import com.pepper.SpringFxCheckBox.Model.Model;
import com.pepper.SpringFxCheckBox.View.DynamicTable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.TableView;


public class JoinEntityController 
{
    AppControllerChB P;
    EntityController EC0;
    EntityController EC1;
    Model model;
    private List<String> selectedColumns0, selectedColumns1;
    private List<String> joinColumnNames0, joinColumnNames1;
    private List<TextField> asTxtListEC0, asTxtListEC1;
    private String query;
    private String a, b;
    private DynamicTable dynamicTable;
    private int tableIndex; // orderBy comboBoxok azonosításához kell, még nincs belőve
    private int tableIndex1;
    
    public JoinEntityController (AppControllerChB parent, EntityController ec0, EntityController ec1)
    {
        this.EC0 = ec0;
        this.EC1 = ec1;
        
        this.joinColumnNames0 = new ArrayList<>();
        this.joinColumnNames1 = new ArrayList<>();            
        this.P = parent;
        
        model = AppCoreChB.getContext().getBean(Model.class);
        setUpQueryData();
    }
    
    public void setUpQueryData()
    {
        this.selectedColumns0 = new ArrayList<>();
        this.selectedColumns1 = new ArrayList<>();
        selectedColumns0.clear();
        joinColumnNames0.clear();
        
        selectedColumns0.addAll(EC0.getSelectedColumns());
        a = P.getJoinAS0();
        joinColumnNames0.addAll(createJoinNames(selectedColumns0, a));
        
        selectedColumns1.clear();
        joinColumnNames1.clear();
        selectedColumns1.addAll(EC1.getSelectedColumns());
        b = P.getJoinAS1();
        joinColumnNames1.addAll(createJoinNames(selectedColumns1, b));
        
        asTxtListEC0 = new ArrayList<>();
        asTxtListEC1 = new ArrayList<>();
        asTxtListEC0.clear();
        asTxtListEC1.clear();
        asTxtListEC0.addAll(EC0.getAsTxtList());
        asTxtListEC1.addAll(EC1.getAsTxtList());
        
    }    
    public void buildQuery()
    {
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        
        for(int i = 0; i < joinColumnNames0.size(); i ++) // aliasok hozzáadása columnNevekhez textFieldekbe írt 
        {
            String alias = asTxtListEC0.get(EC0.getChbIndex(selectedColumns0.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                //queryBuilder.append(joinColumnNames1.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
                if(P.getAggregateMap().containsKey(selectedColumns0.get(i)))
                {   
                    String aggregateFunction = P.getAggregateMap().get(selectedColumns0.get(i));
                    queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames0.get(i)).append(")").append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..                    
                }
                else
                {
                    queryBuilder.append(joinColumnNames0.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
                }                
            }
            else if(P.getAggregateMap().containsKey(selectedColumns0.get(i))) //AGGREGATE CLAUSE
            {   // SUM, AVG, etc
                String aggregateFunction = P.getAggregateMap().get(selectedColumns0.get(i));
                queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames0.get(i)).append(")");
            }
            else 
            {queryBuilder.append(joinColumnNames0.get(i));} 

            if( i < selectedColumns0.size() ) // első elem után ", " ad hozzá
            {
                queryBuilder.append(", ");
            }
        }
        for(int i = 0; i < joinColumnNames1.size(); i ++) // aliasok hozzáadása columnNevekhez textFieldekbe írt 
        {
            System.out.println("Entered joinController's Qbuilder second loop");
            System.out.println(joinColumnNames1.get(i));
            String alias = asTxtListEC1.get(EC1.getChbIndex(selectedColumns1.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                if(P.getAggregateMap().containsKey(selectedColumns1.get(i)))
                {   
                    String aggregateFunction = P.getAggregateMap().get(selectedColumns1.get(i));
                    queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames1.get(i)).append(")").append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..                    
                }
                else
                {
                    queryBuilder.append(joinColumnNames1.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
                }
            }
            else if(P.getAggregateMap().containsKey(selectedColumns1.get(i))) //AGGREGATE CLAUSE
            {   // SUM, AVG, etc
                String aggregateFunction = P.getAggregateMap().get(selectedColumns1.get(i));
                queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames1.get(i)).append(")");
            }
            else 
            {queryBuilder.append(joinColumnNames1.get(i));} 

            if( i < selectedColumns1.size()-1 ) // első elem után ", " ad hozzá
            {
                queryBuilder.append(", ");
            }
        }
        if(joinColumnNames0.size() <= 0 && joinColumnNames1.size() <= 0 )
        {
                              //ezt átírni dinamikusra
            queryBuilder.append(" * FROM db__income ").append(P.getJoinAS0()).append(" JOIN db__partners ").append(P.getJoinAS1()).append(" ON ").append(P.getJoinAS0() +"."+ P.getOnCB0()).append(" = ").append(P.getJoinAS1()+ "." + P.getOnCB1());         
        }
        else
        {                    //ezt átírni dinamikusra
            queryBuilder.append(" FROM db__income ").append(P.getJoinAS0()).append(" JOIN db__partners ").append(P.getJoinAS1()).append(" ON ").append(P.getJoinAS0() +"."+ P.getOnCB0()).append(" = ").append(P.getJoinAS1()+ "." + P.getOnCB1());
        }
        // WHERE *** IS NULL
        if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null && P.getAndOrTF1().getText() == null)
        {
            System.out.println("hiba 1");
            queryBuilder.append(" WHERE ").append(P.getWhereCB().getValue()).append(" IS NULL");            
        }
        // WHERE kisebb nagyobb mint 
        if(P.getWhereCB().getValue() != null && P.getThanTxt().getText() != null && P.getwhereOpCB().getValue() != null && P.getAndOrTF1().getText() == null)
        {      
            System.out.println("hiba 2");
            String whereColName = P.getWhereCB().getValue();            
            String operator = P.getwhereOpCB().getSelectionModel().getSelectedItem();            
            // Check if the user entered a numeric value
            try {
            int value = Integer.parseInt(P.getThanTxt().getText(), 10); // You can use other numeric types if needed
                System.out.println(value);
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
        }
        if(P.getAndOrTF1().getText() != null)
        {            
            queryBuilder.append( " WHERE ").append(P.getAndOrTF1().getText());
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
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
    }
    
    public void expectoResult()
    {
        System.out.println("JoinEntitycontroller expectoResult triggered");
        deleteTable();
        if(dynamicTable != null && !dynamicTable.getColumns().isEmpty())
        {
            dynamicTable.getItems().clear();
            dynamicTable.getColumns().clear();
        }
        String query = P.getQueryTxtArea().getText();
        ExecuteQuery eq = new ExecuteQuery();
        EntityHandler entHand = new EntityHandler(DynamicDTO.class);
        
        List<String> selectedColumns = new ArrayList<>();
        selectedColumns.addAll(selectedColumns0);
        selectedColumns.addAll(selectedColumns1);
        
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
    public List<String> createJoinNames(List<String> list, String alias)
    {
        List<String> aliasDotColNames = new ArrayList<>();
        for(int i = 0; i < list.size(); i++)
        {
            aliasDotColNames.add(alias + "." +list.get(i));            
        }
        return aliasDotColNames;
    }
    public void inflateWhereCb(String a, String b) // NEM TÖLTI BE debuggolni !!!!!!!!!!!!!!!!!!!!!!!
    {
        try
        {
            List<String> allColNames = new ArrayList<>();
            
            List<String> firstColNames = new ArrayList<>();
            firstColNames.addAll(model.getColumnNamesNew(EC0.getTableName()));
            firstColNames = createJoinNames(firstColNames, a);
            
            List<String> secColNames = new ArrayList<>();
            secColNames.addAll(model.getColumnNamesNew(EC1.getTableName()));
            secColNames = createJoinNames(secColNames, b);
            
            allColNames.addAll(firstColNames);
            allColNames.addAll(secColNames);
            
            P.getWhereJoinCB().getItems().addAll(allColNames);
        } catch (SQLException ex)
        {
            Logger.getLogger(JoinEntityController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
