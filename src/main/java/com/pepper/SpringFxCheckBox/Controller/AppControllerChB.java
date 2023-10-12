package com.pepper.SpringFxCheckBox.Controller;

import Security.AccountManager;
import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.MessageBox;
import com.pepper.SpringFxCheckBox.Gui.PopUpMessage;
import com.pepper.SpringFxCheckBox.Model.Account;
import com.pepper.SpringFxCheckBox.Model.DTO;
import com.pepper.SpringFxCheckBox.Model.Database;
import com.pepper.SpringFxCheckBox.Model.DisplayFK;
import com.pepper.SpringFxCheckBox.Model.FK;
import com.pepper.SpringFxCheckBox.Model.Model;
import com.pepper.SpringFxCheckBox.View.DynamicTable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AppControllerChB implements Initializable 
{
    // join comboboxok nem működnek, partner tábla oszlopaiból egyet nem tölt be...
    private AppCoreChB appCore;
    private JoinEntityController joinEntController;    
    private Model model;
    private Database database;
    private static Connection connection;
    public static String url, databaseName, user, password;
    private PauseTransition triggerDel;
    private Scene scene;    
    private List<EntityController> entityControllerList;
    private List<String> selectedAggrFunct, selectedAggrNameList, tableNames;
    private List<Integer> selectedTableIndexs;   
    private List<String> selectedTableNames;
    private List<CheckBox> tblChbList;
    private List<List<String>> colNameListList;
    private Map<String, String> aggregateMap = new HashMap<>();
    private List<Integer> selectedtbl = new ArrayList<>();;
    public String aggregateFunction = new String();
    public String selectedAggName = new String();
    
    @FXML
    private AnchorPane tblContainer, fkContainer;
    @FXML
    private Button delBtn1, delBtn, loadBtn;
    @FXML
    private TextArea queryTxtArea;
    @FXML
    private Label nfo, userLabel;
    @FXML
    private Spinner<Integer> topSpin; 
    @FXML
    private CheckBox disableTopSpin, descChb, isNullChB, isNullChB1, notNullChB1, notNullChB, saveAccChb;
    @FXML
    private ComboBox<String> whereCB, whereJoinCB, groupByCB, whereOpCB, whereOpCB1, joinCB, onCB0, onCB1, aggregateCB, aggName, orderByCB;
    @FXML
    private TextField andOrTF, andOrTF1, thanTF, thanTF1, joinAS0, joinAS1, orderByTF, groupTF;
    @FXML
    private TextField urlTF, databaseTF, userTF;
    @FXML
    private PasswordField passwordTF;    
    // Table
    @FXML
    private VBox root, select, select2, FKcontainer;
    @FXML                         //oszlop név checkBoxok
    private HBox tableChbContainer, colNameChbContainer, orderBcontainer, select1, clauses, build;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {        
        appCore = new AppCoreChB();
        scene = appCore.getScene();        
        model = new Model(); //AppCoreChB.getContext().getBean(Model.class);
        database = new Database();        
        entityControllerList = new ArrayList<>();
        selectedTableIndexs = new ArrayList<>();
        selectedTableNames = new ArrayList<>();
        colNameListList = new ArrayList<>();
        setupUI();
        loadAcc();
    }
    private void loadAcc() //megoldani: egy acc Listából kiszűrni ki jelentkezett be
    {
        Map<String, Account> accMap = AccountManager.loadDBAccounts();
        if(accMap.isEmpty()){
            System.out.println("accMap.isEmpty()");
        }
        String key = AppCoreChB.loginAcc.getUserLogin();
        System.out.println("asd " + key);
        Account recent = accMap.get(key);
        
        if(recent != null){
            urlTF.setText(recent.getUrl());
            databaseTF.setText(recent.getDatabase());
            userTF.setText(recent.getUserDB());
        } else {
            System.out.println("betöltött acc(rec) IS NULL");
        }
        
    }
   /* public static Connection getConnection()
    {
        try
        {
            if(connection == null || connection.isClosed()){
                connection = Database.connectDB(url, user, password, databaseName);
            }
            if(connection != null){
                System.out.println("AppController, getConnection triggered, connection NOT NULL ");
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }*/
    public void connectToDatabase() throws SQLException
    {
        System.out.println("connectToDatabase triggered");
        selectedTableIndexs.clear();
        if(connection != null){
           Database.closeConnection();
        }
        if(!urlTF.getText().isEmpty() && !databaseTF.getText().isEmpty() && !userTF.getText().isEmpty() && !passwordTF.getText().isEmpty())
        {
            //url, databaseName, user, password;
            url = urlTF.getText();
            databaseName = databaseTF.getText();
            user = userTF.getText();
            password = passwordTF.getText();
            if(saveAccChb.isSelected()) //account mentése
            {                
                String key = AppCoreChB.loginAcc.getUserLogin();
                Account dbAcc = new Account(url, databaseName, user);
                Map<String, Account> acc = new HashMap<>();
                acc.put(key, dbAcc);
                
                AccountManager.SaveDBAccount(acc);
                System.out.println("ACC saved");
            }
            
            try{
                Database.createDataSource(url, user, password, databaseName);
                connection = Database.getConnection();
                model.setConnection();
                if(connection != null){
                     PopUpMessage msg = new PopUpMessage("Database connected", root);
                     loadTables();
                } else {
                    PopUpMessage msg = new PopUpMessage("Database not connected", root);
                }                   
            }
            catch (SQLException e){
                MessageBox.Show("Error", e.getMessage());
            } 
        } else {
            System.out.println("valamelyik Textfield üres");
        }
    }
    
    public void loadTables()
    {
        if(connection != null){
        createTableChbs(databaseTF.getText());            
        } else {
            MessageBox.Show("Error", "connection is null");
        }        
    }
    
    public void loadFK() //esetleg lehetne a gombhoz egy hint: Select only one table
    {        
        try
        {
            if(connection != null || !connection.isClosed())
            {
                for(CheckBox chb: tblChbList)
                {
                    if(chb.isSelected())
                    {
                        String tblName = chb.getText();
                        List<FK> fkList = model.getForeignKeys(tblName);
                        if(fkList != null && !fkList.isEmpty())
                        {   
                            FKcontainer.getChildren().clear();
                            for(FK fk : fkList)
                            {

                                DisplayFK displayFK = new DisplayFK(FKcontainer, fk.getRequestedTable(), fk.getRtForeignKey(), fk.getReferencedTable(), fk.getReferencedFK());
                            }
                        }  
                    }
                }
            }
            else {
                MessageBox.Show("Error", "connection is null");
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void createTableChbs(String databaseName) //Table checkBoxok + onAction
    {
        try
        {
            tableNames = model.getTableNamesNew(databaseName);
            joinCB.getItems().clear();
            joinCB.getItems().add(0, null);
            joinCB.getItems().addAll(tableNames);
        } catch (SQLException ex)
        {
            Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblChbList = new ArrayList<>();
        //szükségem van a ...checkbox indexére amit használhatok EntityControllert választani az entityList-ből
        clearControls(); //inputmezők.clear()
        
        List<String> columnNames = new ArrayList<>();
        for(int i = 0; i < tableNames.size(); i++)
        {
            tblChbList.add(new CheckBox(tableNames.get(i))); // checkbox létrehozása tábla névvel + szélesség beállítása
            tblChbList.get(i).getStyleClass().add("checkbox");
            Text txt = new Text( tblChbList.get(i).getText());
            Double width = txt.getLayoutBounds().getWidth();
            tblChbList.get(i).setMinWidth(width * 1.4); 

            tableChbContainer.getChildren().add(tblChbList.get(i)); // checkbox konténerhez adása
            applyFadeInAnimation(tblChbList.get(i));
            try
            {
                columnNames = model.getColumnNamesNew(tableNames.get(i));
                System.out.println("columnNames:  "+columnNames.get(0));
                colNameListList.add(i, columnNames);              
                
            } catch (SQLException ex)
            {
                Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            EntityController entity = new EntityController(this, colNameChbContainer, columnNames);//új entity
            entityControllerList.add(entity);
        }
        for(int i =0; i < tableNames.size(); i++) //táblaNév checkboxokhoz onAction hozzáadása
        {
            final int index = i;
            tblChbList.get(i).setOnAction(event ->{
                if(tblChbList.get(index).isSelected())
                {
                    entityControllerList.get(index).createColumnChb( tableNames.get(index), index);
                    updateCBs(index);
                    queryTxtArea.setText("SELECT ");
                }
                else
                {
                    entityControllerList.get(index).clearCheckBoxes();
                    queryTxtArea.clear();
                    clearCbs();
                }
            });
            tblChbList.get(i).selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                if(newValue)
                {
                    System.out.println("tblChbList.get(i).getText() " );
                    selectedTableIndexs.add(index);
                    System.out.println("selectedtbl.size(); " + selectedTableIndexs.size()); 
                    addSelectedTBLindex();
                } else if( oldValue){
                    selectedTableIndexs.remove(Integer.valueOf(index));
                    System.out.println("selectedtbl.size(); " + selectedTableIndexs.size()); 
                    addSelectedTBLindex();
                }
            });
        }
    }
    public void addSelectedTBLindex() // kiderül 1 vagy több és melyik tábla van kiválasztva(egyelőre 2 tábla kiválasztása van csak megoldva JOIN-hoz
    {
        if(selectedTableIndexs.size() == 2){
            updateJoinComboBoxs(selectedTableIndexs);
            System.out.println("getSelectedTblCount() :" + selectedTableIndexs.size());
        }
    }
    private void updateJoinComboBoxs(List<Integer> selectedtbl)
    {   System.out.println("updateJoinComboBoxs triggered");
        
                   
        joinCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null)
            {
                try
                {
                    onCB0.getItems().clear();
                    onCB0.getItems().add(0, null);
                    onCB0.getItems().addAll(model.getColumnNamesNew(joinCB.getValue()));
                } catch (SQLException ex)
                {
                    System.out.println("AppController: " + ex.getMessage());
                }
            }
        });              

        onCB0.getSelectionModel().selectedItemProperty().addListener((ObservableValue, oldValue, newValue)->
        {
            if(newValue != null)
            {
                try
                {
                    if(tblChbList.get(selectedtbl.get(1)).getText().equals(joinCB.getValue())){
                        onCB1.getItems().clear();
                        onCB1.getItems().add(0, null);
                        onCB1.getItems().addAll(model.getColumnNamesNew(tblChbList.get(selectedtbl.get(0)).getText()));
                    } else {
                        onCB1.getItems().clear();
                        onCB1.getItems().add(0, null);
                        onCB1.getItems().addAll(model.getColumnNamesNew(tblChbList.get(selectedtbl.get(1)).getText()));
                    }
                } catch (SQLException ex)
                {
                    Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });
         
    }
    private void updateCBs(int index)
    {
        updateCb(index, whereCB);
        updateCb(index, groupByCB);
        updateCb(index, orderByCB);
        updateCb(index, aggName);           
    }
    private void updateCb(int index, ComboBox comboBox)
    {        
        comboBox.getItems().setAll(colNameListList.get(index));
        comboBox.getItems().add(0, null);
    }
    private void clearCbs()
    {
        whereCB.getItems().clear();
        groupByCB.getItems().clear();
        orderByCB.getItems().clear();
        aggName.getItems().clear();
    }
    // KÖVETKEZŐ 
    public void setQueryToTxtArea()
    {   /*tudnom kell: 1. Van-e kiválasztva entityController chb.
                       2. Melyik van kiválasztva.
                       3. Egy vagy két tábla van kiválasztva ?
        Tábla chb-ok listája: tblChbList
        */
        System.out.println("selectedTables.size() at setQueryToTxtArea: " + selectedTableIndexs.size());
        if(selectedTableIndexs.size() > 1) //JOIN query build
        {
            System.out.println("Join query building");
            int entityContr0 = selectedTableIndexs.get(0);
            int entityContr1 = selectedTableIndexs.get(1); //le kell kérni a tábla neveket és lepasszolni az JoinEntitynek
            joinEntController = new JoinEntityController(this, entityControllerList.get(entityContr0), entityControllerList.get(entityContr1));// HAMARABB LÉTRE KELL HOZNI
            joinEntController.buildQuery();
        }
        else if(selectedTableIndexs.size() == 1) //solo table query build
        {
            System.out.println("solo table query building");
            for(int i = 0; i < entityControllerList.size(); i++)
            {
                if(tblChbList.get(i).isSelected() ) { 
                    entityControllerList.get(i).buildQuery(tableNames.get(i));// ez egy tábla kiválasztásakor oké de joinhoz kevés + kell egy JoinEntityController:kész 
                }                
            }
        }      
    }
    public void expectoQuery()
    {
        if(selectedTableIndexs.size() > 1) //JOIN QUERY
        {
            joinEntController.expectoResult();
        }
        else 
        {
            for(int i = 0; i < entityControllerList.size(); i++) // SOLO TABLE QUERY
            {
                if(tblChbList.get(i).isSelected() ) {
                    entityControllerList.get(i).expectoResult();
                }                
            }
        } 
    }
    
    //JOIN    
    public void updateJoinWhereCB() // aliasTF.textProperty().addListener-höz kötve
    {//ehhez kell tudni melyik táblákat választották ki, lekérni az oszlopneveket és elküldeni a createJoinNames-nek
        String a = joinAS0.getText();
        String b = joinAS1.getText();
        ObservableList<String> observableList0 = onCB0.getItems(); //először ObsL-be tudom menteni a CB elemeket..
        ObservableList<String> observableList1 = onCB1.getItems();
        List<String> nameList0 = new ArrayList<>(observableList0);
        List<String> nameList1 = new ArrayList<>(observableList1);
        List<String> aliasDotColNames = new ArrayList<>();
        if(!a.isEmpty() && !b.isEmpty())
        {
            for(int i = 0; i < nameList0.size(); i++)
            {
                if(nameList0.get(i)!=null){                    
                    aliasDotColNames.add(a + "." + nameList0.get(i));  
                }
            }
            for(int i = 0; i < nameList1.size(); i++)
            {
                if(nameList1.get(i) != null){                    
                    aliasDotColNames.add(b + "." + nameList1.get(i));
                }                
            }            
            whereJoinCB.getItems().addAll(aliasDotColNames);
        }        
    }
    //gomb onActionök, query részletek előkészítése
    public void andWhereClause1()
    {
        if(whereJoinCB.getValue() != null)
        {
            if(isNullChB1.isSelected()){
                
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NULL AND ");
            }else if(notNullChB1.isSelected()){
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NOT NULL AND ");
            }
            if(whereOpCB1.getValue() != null && thanTF1.getText() != null){
                andOrTF1.appendText(whereJoinCB.getValue() + whereOpCB1.getValue() + " " +  thanTF1.getText() + " AND ");
            }            
        }
    }
    public void orWhereClause1()
    {
        if(whereJoinCB.getValue() != null)
        {
            if(isNullChB.isSelected()){
                
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NULL OR");
            }else if(notNullChB1.isSelected()){
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NOT NULL OR ");
            }
            if(whereOpCB1.getValue() != null && thanTF1.getText() != null){
                andOrTF1.appendText(whereJoinCB.getValue() + whereOpCB1.getValue() + " " +  thanTF1.getText() + " OR ");
            }            
        }
    }
    public void addWhereClause1()
    {
        if(whereJoinCB.getValue() != null)
        {
            if(isNullChB1.isSelected()){                
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NULL");
            }else if(notNullChB1.isSelected()){
                andOrTF1.appendText(whereJoinCB.getValue() + " IS NOT NULL ");
            }
            else if(whereOpCB1.getValue() != null && thanTF1.getText() != null){
                andOrTF1.appendText(whereJoinCB.getValue() + " " + whereOpCB1.getValue() + " " +  thanTF1.getText());
            }
        }
    }
    
    //WHERE
    public void andWhereClause()
    {
        if(whereCB.getValue() != null)
        {
            if(isNullChB.isSelected()){
                
                andOrTF.appendText(whereCB.getValue() + " IS NULL AND ");
            } else if(notNullChB.isSelected()){
                andOrTF.appendText(whereCB.getValue() + " IS NOT NULL AND ");
            }
            if(whereOpCB.getValue() != null && thanTF.getText() != null){
                andOrTF.appendText(whereCB.getValue() + whereOpCB.getValue() + " " +  thanTF.getText() + " AND ");
            }
        }
    }
    public void orWhereClause()
    {
        if(whereCB.getValue() != null)
        {
            if(isNullChB.isSelected()){
                
                andOrTF.appendText(whereCB.getValue() + " IS NULL OR ");
            }else if(notNullChB.isSelected()){
                andOrTF.appendText(whereCB.getValue() + " IS NOT NULL OR ");
            }
            if(whereOpCB.getValue() != null && thanTF.getText() != null){
                andOrTF.appendText(whereCB.getValue() + whereOpCB.getValue() + " " +  thanTF.getText() + " OR ");
            }
        }
    }
    public void addWhereClause()
    {
        if(whereCB.getValue() != null)
        {
            if(isNullChB.isSelected()){
                
                andOrTF.appendText(whereCB.getValue() + " IS NULL ");
            }else if(notNullChB.isSelected()){
                andOrTF.appendText(whereCB.getValue() + " IS NOT NULL ");
            }
            if(whereOpCB.getValue() != null && thanTF.getText() != null){
                andOrTF.appendText(whereCB.getValue() + " " + whereOpCB.getValue() + " " +  thanTF.getText());
            }
        }
    } //paraméterben kéne megadni melyik gombbal melyik TFt törölje, még nincs belőve
    public void delTF(Button source, TextField tf)
    {
        if(tf.getText().length() > 1){
          tf.setText(tf.getText().trim().substring(0, tf.getText().length()-1));  
        } else {
            tf.clear();
        }
    }    
    
    //GROUP BY
    public void addGroupByClause()
    {
        if(groupByCB.getValue() != null)
        {
            groupTF.appendText(groupByCB.getValue() + ", ");
        }
    }
    public void delLastGroupByClause()
    {
        String txt = groupTF.getText().trim();
        
        if (!txt.isEmpty())
        {
            txt = txt.substring(0, txt.length()-2); // utolsó szóköz és vessző törlése
            int lastSpaceIndex = txt.lastIndexOf(" ") + 1;
            if(lastSpaceIndex > 0){
                groupTF.setText(txt.substring(0, lastSpaceIndex));                
            } else {
                groupTF.clear();
            }
        }
    }
    //ORDER BY 
    public void addOrderByClause()
    {
        
        if(orderByCB.getValue() != null)
        {
            if(descChb.isSelected()){
                orderByTF.appendText(orderByCB.getValue() + " DESC, ");
            } else {
                orderByTF.appendText(orderByCB.getValue() + " ASC, ");
            }
        }             
    }
    //TÖRLÉS GOMB
    public void delLastOrderByClause()
    {
        String txt = orderByTF.getText().trim();
        
        if (!txt.isEmpty())
        {
            txt = txt.substring(0, txt.length()-2);
            int lastSpaceIndex = txt.lastIndexOf(" ") + 1;
            if(lastSpaceIndex > 0){
                orderByTF.setText(txt.substring(0, lastSpaceIndex));
            }
            else {
                orderByTF.clear();
            }
        }
    }
    
    public void setupUI()
    {
        userLabel.setText(AppCoreChB.loginAcc.getUserLogin() +", ");
        
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor( rgb(50, 50, 65));
        innerShadow.setRadius(10);
        select.setEffect(innerShadow);
        select1.setEffect(innerShadow);
        select2.setEffect(innerShadow);
        clauses.setEffect(innerShadow);
        
        build.setEffect(innerShadow);
        queryTxtArea.setEffect(innerShadow);
        root.setEffect(innerShadow);
        joinCB.setStyle("-fx-prompt-text-fill: rgba(255, 255, 255, 0.7)");
        
        selectedAggrFunct = new ArrayList<>();
        selectedAggrNameList = new ArrayList<>();
        //INFO MSG
        Tooltip tooltip = new Tooltip("Deselect  all to SELECT * ");
        tooltip.setStyle("-fx-font-size: 13px;");
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(nfo, tooltip);        
        nfo.getStyleClass().add("nfo");
        nfo.setOnMouseEntered(event -> {
            nfo.getStyleClass().add("hover");
        });
        nfo.setOnMouseExited(event -> {
            nfo.getStyleClass().remove("hover");
        });
        Tooltip tooltip1 = new Tooltip("Password won't be saved");
        tooltip1.setStyle("-fx-font-size: 13px;");
        tooltip1.setShowDelay(Duration.ZERO);        
        tooltip1.install(saveAccChb, tooltip1);
        //LIMIT
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 25);
        topSpin.setValueFactory(valueFactory);
        disableTopSpin.setOnAction(event -> {
            if(disableTopSpin.isSelected()) {topSpin.setDisable(true); disableTopSpin.setText("Enable");}
            else {topSpin.setDisable(false); disableTopSpin.setText("Disable");}         
        });
        //WHERE
        andOrTF.setText(null);
        isNullChB1.setOnAction(event -> // null<->operátor egymást inaktiválja
        { 
            isNullChB1.setSelected(true); //bal oldali where
            notNullChB1.setSelected(false);
            thanTF1.clear();
            whereOpCB1.getSelectionModel().select(0);
        });
        isNullChB.setOnAction(event -> // null<->operátor egymást inaktiválja
        { 
            isNullChB.setSelected(true); //jobb oldali where
            notNullChB.setSelected(false);
            thanTF.clear();
            whereOpCB.getSelectionModel().select(0);
        });
        notNullChB1.setOnAction(event ->{
            isNullChB1.setSelected(false); //bal oldali where
            notNullChB1.setSelected(true);
            thanTF1.clear();
            whereOpCB1.getSelectionModel().select(0);
        });
        notNullChB.setOnAction(event -> {
            isNullChB.setSelected(false); //jobb oldali where
            notNullChB.setSelected(true);
            thanTF.clear();
            whereOpCB.getSelectionModel().select(0);
        });
        whereOpCB1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null)
            {
                notNullChB1.setSelected(false);
                isNullChB1.setSelected(false);
            }
        });
        whereOpCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null)
            {
                notNullChB.setSelected(false);
                isNullChB.setSelected(false);
            }
        });
        //JOIN
        
        andOrTF1.setText(null);        
        joinAS0.textProperty().addListener((observable, oldValue, newValue) ->
        {
            updateJoinWhereCB();
        });
        joinAS1.textProperty().addListener((observable, oldValue, newValue) ->
        {
            updateJoinWhereCB();
        });
        // IIIIITTTTT TARTOOOK lehet kéne egy JoinController mert.
        
        //AGGREGATE
        aggregateCB.valueProperty().addListener((observable, oldValue, newValue) ->
        {        
            if(aggName.getValue() != null)
            {
                String selectedColumn = aggName.getValue();
                aggregateMap.put(selectedColumn, newValue);
                System.out.println("Selected Aggregate Function for " + selectedColumn + ": " + newValue);
            }
        });
        aggName.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if(aggregateCB.getValue() != null)
            {
                String selectedAggregate = aggregateCB.getValue();
                aggregateMap.put(newValue, selectedAggregate);
                System.out.println("Selected Column for " + selectedAggregate + ": " + newValue);
            }
        });
        /*Button source;
        triggerDel = new PauseTransition(Duration.millis(100));
        triggerDel.setOnFinished(event ->
        {
            
        });
        
        triggerPause = new PauseTransition(Duration.millis(100));
        triggerPause.setOnFinished(event -> {
            if (source == delBtn1) {
                handleButtonAction(delBtn1, andOrTF1);
            } else if (triggerSource == delBtn) {
                handleButtonAction(delBtn, andOrTF);
            }
            triggerPause.playFromStart(); // Restart the PauseTransition
        });

        delBtn1.setOnMousePressed(event -> {
            source = delBtn1; // Set triggerSource when button is pressed
            triggerPause.play();
        });
        delBtn1.setOnMouseReleased(event -> {
            source = null; // Reset triggerSource when button is released
            triggerPause.stop();
        });
        */        
        
        setAggregateCB();
        setwhereOpCB();
        joinCB.getItems().add(0, null);
        onCB0.getItems().add(0, null);
        onCB1.getItems().add(0, null);   
        whereJoinCB.getItems().add(0, null);
        whereOpCB.getItems().add(0, null);
        whereOpCB1.getItems().add(0, null);
        aggregateCB.getItems().add(0,null);        
    }
    
    public void clearControls()
    {
        tblChbList.clear();        
        entityControllerList.clear();
        tableChbContainer.getChildren().clear();
        queryTxtArea.clear();
        colNameChbContainer.getChildren().clear();
        aggName.getItems().clear();
        orderByCB.getItems().clear();
        groupByCB.getItems().clear();
        whereCB.getItems().clear();
        whereJoinCB.getItems().clear();        
        onCB0.getItems().clear();
        onCB1.getItems().clear();        
    }
  
    private void applyFadeInAnimation(CheckBox checkBox) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.7), checkBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
    private void setAggregateCB(){
        List<String> aggregateFunctionsList = new ArrayList<>();        
        aggregateFunctionsList.add("SUM");
        aggregateFunctionsList.add("AVG");
        aggregateFunctionsList.add("COUNT");
        aggregateFunctionsList.add("MAX");
        aggregateFunctionsList.add("MIN");
        aggregateFunctionsList.add("GROUP_CONCAT");
        aggregateFunctionsList.add("STDDEV");
        aggregateFunctionsList.add("VARIANCE");
       aggregateCB.getItems().addAll(aggregateFunctionsList);
    }
    
    private void setwhereOpCB() {
        List<String> operators = new ArrayList<>();
        operators.add("<");
        operators.add(">");
        operators.add("=");
        operators.add("<=");
        operators.add(">=");
       whereOpCB.getItems().addAll(operators);
       whereOpCB1.getItems().addAll(operators);
    }

        
    public boolean isLimitSelected(){
        return disableTopSpin.isSelected();
    }
    public int getTopValue(){
        return topSpin.getValue();
    }
    public HBox getColNameChbContainer() {
        return colNameChbContainer;
    } 
    public TextArea getQueryTxtArea() {
        return queryTxtArea;
    }
    public boolean descIsSelected() {
        return descChb.isSelected();
    }
    //ORDER BY    
    public TextField getOrderByTF() {
        return orderByTF;
    }
    public boolean isDescSelected(){
       return descChb.isSelected();
    }
    public ComboBox<String> getOrderByCB() {
        return orderByCB;
    }    
    
    //GROUP BY
    public ComboBox<String> getGroupByCB() {
        return groupByCB;
    }
    public TextField getGroupTF() {
        return groupTF;
    }    
    //WHERE
    public boolean isNull(){ //SELECT * FROM your_table_name WHERE approved IS NULL;
        return isNullChB.isSelected();
    }
    public boolean  isNull1(){
        return isNullChB1.isSelected();
    }
    public boolean  notNull(){
        return notNullChB1.isSelected();
    }
    public boolean  notNul2(){
        return notNullChB.isSelected();
    }
    public ComboBox<String> getWhereCB() {
        return whereCB;
    }
    public TextField getThanTxt() {
        return thanTF;
    }
    public TextField getThanTxt1(){
        return thanTF1;
    }
    public String getThanTxtValue(){
        return thanTF.getText();
    }
    public ComboBox<String> getwhereOpCB(){
        return whereOpCB;
    }
    public String getwhereOpCBValue(){
        return whereOpCB.getValue();
    }
    public ComboBox<String> getWhereOpCB1() {
        return whereOpCB1;
    }
    public String getwhereOpCBValue1(){
        return whereOpCB1.getValue();
    }    
    public ComboBox<String> getWhereCB1() {
        return whereJoinCB;
    }       
    public TextField getAndOrTF() {
        return andOrTF;
    }    
    public Scene getScene() {
        return scene;
    }    
    
    //JOIN
    public ComboBox<String> getWhereJoinCB() {
        return whereJoinCB;
    }
    public TextField getAndOrTF1() {
        return andOrTF1;
    }    
    public String getJoinAS0() {
        return joinAS0.getText();
    }
    public String getJoinAS1() {
        return joinAS1.getText();
    }
    public String getJoinCBValue() {
        return joinCB.getValue();
    }
    public String getOnCB0() {
        return onCB0.getValue();
    }
    public String getOnCB1() {
        return onCB1.getValue();
    }
    //AGGREGATE CLAUSE
    public ComboBox getAggregateCB(){
        return aggregateCB;
    }
    public ComboBox<String> getAggName() {
        return aggName;
    }
    public List<String> getAggregateList(){
        return selectedAggrFunct;
    }
    public List<String> getSelectedAggrNameList(){
        return selectedAggrNameList;
    }
    public Map<String, String> getAggregateMap() {
        return aggregateMap;
    }
    //TABLE    
    public VBox getRoot() {
        return root;
    }
    public HBox getTableChbContainer() {
        return tableChbContainer;
    }

    public AnchorPane getTblContainer() {
        return tblContainer;
    }

    public VBox getFKcontainer() {
        return FKcontainer;
    }
    
    
    
    
}
/*

*/