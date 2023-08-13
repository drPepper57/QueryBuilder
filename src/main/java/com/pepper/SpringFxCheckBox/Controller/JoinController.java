package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.TextField;
import com.pepper.SpringFxCheckBox.Model.Income;
import com.pepper.SpringFxCheckBox.Model.Model;
import com.pepper.SpringFxCheckBox.Model.Partner;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.CheckBox;


public class JoinController 
{
    AppControllerChB P;
    IncomeController incomeController;
    PartnerController partnerController;
    Model model;
    private List<String> inColNames;
    private List<String> prtColNames;
    private List<String> selectedColumns1, selectedColumns2;
    private List<String> joinColumnNames1, joinColumnNames2;
    private List<TextField> asTxtListIncome, asTxtListPrt;
    private List<CheckBox> checkBoxes; 
    private String query;
    private String a, b;
    
    
    public JoinController(AppControllerChB parent, IncomeController incomeController, PartnerController partnerController)
    {
        this.incomeController = incomeController; // Assign the passed instances to class variables
        this.partnerController = partnerController; // Assign the passed instances to class variables
        
        this.joinColumnNames1 = new ArrayList<>();
        this.joinColumnNames2 = new ArrayList<>();
        this.prtColNames = new ArrayList<>();
        this.inColNames = new ArrayList<>();
        this.P = parent;        
        
        model = AppCoreChB.getContext().getBean(Model.class);
    }
    public void setUpQueryData() // 2 tábla kiválasztásakor fut le
    {
        inColNames = model.getColumnNames(Income.class);
        prtColNames = model.getColumnNames(Partner.class);
                
        this.selectedColumns1 = new ArrayList<>();
        this.selectedColumns2 = new ArrayList<>();
        selectedColumns1.clear();
        joinColumnNames1.clear();
        System.out.println("Begining of setUpQueryMaterial() : " + selectedColumns1.size() + " " + selectedColumns2.size());
        selectedColumns1.addAll(incomeController.getSelectedColumns());        
        a = P.getJoinAS0();
        joinColumnNames1.addAll(createJoinNames(selectedColumns1, a)); // lekérjük a neveket a többi controllertől, hozzáadjuk a custom *.táblaNév
        for(String name: joinColumnNames1){
            System.out.println("setUpQueryMaterial() joinColumnNames " + name);
        }
        
        selectedColumns2.clear();
        joinColumnNames2.clear();
        selectedColumns2.addAll(partnerController.getSelectedColumns());
        b = P.getJoinAS1();
        joinColumnNames2.addAll(createJoinNames(selectedColumns2, b));
        System.out.println("After geting data: " + selectedColumns1.size() + " " + selectedColumns2.size());
        
        asTxtListIncome = new ArrayList<>();
        asTxtListPrt = new ArrayList<>();
        asTxtListIncome.clear();
        asTxtListPrt.clear();
        asTxtListIncome = incomeController.getAsTxtList(); 
        asTxtListPrt = partnerController.getAsTxtList();
        System.out.println("TextField Listek mérete setUpQueryMaterial() végén: " + asTxtListIncome.size() + " " + asTxtListPrt.size());
        
        
    }
    public List<String> createJoinNames(List<String> list, String alias)
    {
        System.out.println("createJoinNames triggered in JoinController");
        
        
        if(list.isEmpty()){
            System.out.println("Nem érkeztek nevek a createJoinNames metódusba");
        } else {System.out.println("createJoinNames, kapott lista !null");}
        
        List<String> aliasDotColNames = new ArrayList<>();
        for(int i = 0; i < list.size(); i++)
        {
            System.out.println("loop entered in createJoinNames");
            
            aliasDotColNames.add(alias + "." +list.get(i));
            System.out.println(aliasDotColNames.get(i));
        }
        return aliasDotColNames;
    }
    
    
    public void buildQuery()
    {
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        
        for(int i = 0; i < joinColumnNames1.size(); i ++) // aliasok hozzáadása columnNevekhez textFieldekbe írt 
        {
            String alias = asTxtListIncome.get(incomeController.getChbIndex(selectedColumns1.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                //queryBuilder.append(joinColumnNames1.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
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

            if( i < selectedColumns1.size() ) // első elem után ", " ad hozzá
            {
                queryBuilder.append(", ");
            }
        }
        for(int i = 0; i < joinColumnNames2.size(); i ++) // aliasok hozzáadása columnNevekhez textFieldekbe írt 
        {
            System.out.println("Entered joinController's Qbuilder second loop");
            System.out.println(joinColumnNames2.get(i));
            String alias = asTxtListPrt.get(partnerController.getChbIndex(selectedColumns2.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                if(P.getAggregateMap().containsKey(selectedColumns2.get(i)))
                {   
                    String aggregateFunction = P.getAggregateMap().get(selectedColumns2.get(i));
                    queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames2.get(i)).append(")").append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..                    
                }
                else
                {
                    queryBuilder.append(joinColumnNames2.get(i)).append(" AS ").append("`").append(alias).append("`"); // ..ColName AS alias..
                }
            }
            else if(P.getAggregateMap().containsKey(selectedColumns2.get(i))) //AGGREGATE CLAUSE
            {   // SUM, AVG, etc
                String aggregateFunction = P.getAggregateMap().get(selectedColumns2.get(i));
                queryBuilder.append(aggregateFunction).append("(").append(joinColumnNames2.get(i)).append(")");
            }
            else 
            {queryBuilder.append(joinColumnNames2.get(i));} 

            if( i < selectedColumns2.size()-1 ) // első elem után ", " ad hozzá
            {
                queryBuilder.append(", ");
            }
        }
        if(joinColumnNames1.size() <= 0 && joinColumnNames2.size() <= 0 )
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
            System.out.println("hiba 3!!!!!!!!!!!!!!!!!!!!!!!!!");
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
        if(P.getOrderBy() != null)
        {
            
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
        
        //LIMIT
        if(!P.isLimitSelected()){
            queryBuilder.append(" LIMIT ").append(P.getTopValue());
        }
        
        queryBuilder.append(";");
        query = queryBuilder.toString();
        P.getQueryTxtArea().setText(query);
    }
    
    public void inflateWhereCb(String a, String b)
    {
        List<String> allColNames = new ArrayList<>();
        
        List<String> firstColNames = new ArrayList<>();
        firstColNames.addAll(model.getColumnNames(Income.class));
        firstColNames = createJoinNames(firstColNames, a);
        
        List<String> secColNames = new ArrayList<>();
        secColNames.addAll(model.getColumnNames(Partner.class));
        secColNames = createJoinNames(secColNames, b);
        
        allColNames.addAll(firstColNames);
        allColNames.addAll(secColNames);
        if(allColNames.isEmpty()){
            System.out.println("allColNames is empty");
        } else{
            System.out.println("allColNames not empty");
        }
        P.getWhereJoinCB().getItems().addAll(allColNames);
    }
}
