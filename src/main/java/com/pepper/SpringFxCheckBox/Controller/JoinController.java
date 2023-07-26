package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.TextField;
import com.pepper.SpringFxCheckBox.Model.Model;
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
    
    
    public JoinController(AppControllerChB parent, IncomeController incomeController, PartnerController partnerController)
    {
        this.incomeController = incomeController; // Assign the passed instances to class variables
        this.partnerController = partnerController; // Assign the passed instances to class variables
        this.selectedColumns2 = new ArrayList<>();
        this.joinColumnNames1 = new ArrayList<>();
        this.joinColumnNames2 = new ArrayList<>();
        this.selectedColumns1 = new ArrayList<>();
        this.prtColNames = new ArrayList<>();
        this.inColNames = new ArrayList<>();
        this.P = parent;
        
        model = AppCoreChB.getContext().getBean(Model.class);
    }
    //az a cél, hogy a selectedColumns tartalmazza több tábla oszlopNeveit
    public void buildQuery() 
    {
        inColNames = model.getColumnNames("db__income");
        prtColNames = model.getColumnNames("db__partners");
        
        selectedColumns1.addAll(incomeController.getSelectedColumns());        
        String a = P.getJoinAS0();
        joinColumnNames1.addAll(createInColJoinNames(selectedColumns1, a)); // lekérjük a neveket a többi controllertől, hozzáadjuk a custom *.táblaNév
        
        selectedColumns2.addAll(partnerController.getSelectedColumns());
        String b = P.getJoinAS1();
        joinColumnNames2.addAll(createInColJoinNames(selectedColumns2, b));
        
        asTxtListIncome = incomeController.getAsTxtList(); // List<TextField> aliasok
        asTxtListPrt = partnerController.getAsTxtList(); // külön kell kezelni a táblák neveit-aliasokat, checkboxokat, nem szabad összefésülni őket
        
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        
        for(int i = 0; i < joinColumnNames1.size(); i ++) // aliasok hozzáadása columnNevekhez textFieldekbe írt 
        {
            System.out.println("Mi a fasz történik a joinController első loopjában?");
            System.out.println(joinColumnNames1.get(i));
            //ITT VAN HIBA, ha utólag adok hozzá aliast a textFieldbe összehányja magát
            String alias = asTxtListIncome.get(incomeController.getChbIndex(selectedColumns1.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                queryBuilder.append(joinColumnNames1.get(i)).append(" AS ").append(alias); // ..ColName AS alias..

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
            System.out.println("Mi a fasz történik a joinController második loopjában?");
            System.out.println(joinColumnNames2.get(i));
            String alias = asTxtListPrt.get(partnerController.getChbIndex(selectedColumns2.get(i))).getText().trim(); //AS txtField tartalma
            if(!alias.isEmpty()) // ha megadtak AliaS-t, asTxtList tartalmazza az AS TextFieldeket
            {
                queryBuilder.append(" ").append(joinColumnNames2.get(i)).append(" AS ").append(alias); // ..ColName AS alias..

            }
            else 
            {queryBuilder.append(joinColumnNames2.get(i));} 

            if( i < selectedColumns2.size()-1 ) // első elem után ", " ad hozzá
            {
                queryBuilder.append(", ");
            }
        }
                           //ezt átírni dinamikusra
        queryBuilder.append(" FROM db_income ").append(P.getJoinAS0()).append(" JOIN db__partners ").append(P.getJoinAS1()).append(" ON ").append(P.getJoinAS0() +"."+ P.getOnCB0()).append(" = ").append(P.getJoinAS1()+ "." + P.getOnCB1());
        
        /*if( joinColumnNames1.size() <= 0) // ha nincs oszlop kijelölve 
        {
            queryBuilder.append(" * FROM db_income");
        } else {
            queryBuilder.append(" FROM db_income");
        }*/
        
        // WHERE *** IS NULL
        if(P.getWhereCB().getValue() != null && P.isNull() && P.getwhereOpCBValue() == null)
        {
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
    public int getChbIndex(String chbTxt)
    {
        checkBoxes = incomeController.getCheckBoxes();
        System.out.println(checkBoxes.size() + "checkBoxes.size() a getChbIndexben, JoinControllerben");
        int index = -1;
        for(int i = 0; i < checkBoxes.size(); i++){
            if(checkBoxes.get(i).getText().equals(chbTxt)){
                index = i;
                break;
            }
        }
        System.out.println(index + " getChbIndex");
        return index;
    }
    public List<String> createInColJoinNames(List<String> list, String alias)
    {
        System.out.println("createInColJoinNames triggered in JoinController");
        
        
        if(list.isEmpty()){
            System.out.println("Nem érkeztek nevek a createInColJoinNames metódusba");
        } else {System.out.println("createInColJoinNames, kapott lista !null de szarok a tóba!");}
        
        List<String> aliasDotColNames = new ArrayList<>();
        for(int i = 0; i < list.size(); i++)
        {
            System.out.println("loop entered in createInColJoinNames");
            
            aliasDotColNames.add(alias + "." +list.get(i));
            System.out.println(aliasDotColNames.get(i));
        }
        return aliasDotColNames;
    }

}
