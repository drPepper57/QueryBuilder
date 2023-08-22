package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Gui.MessageBox;
import com.pepper.SpringFxCheckBox.Gui.PopUpMessage;
import com.pepper.SpringFxCheckBox.Model.Database;
import com.pepper.SpringFxCheckBox.Model.Model;
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
import javafx.animation.PauseTransition;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javax.swing.JPasswordField;

public class AppControllerChB implements Initializable 
{ // !!!! HIBA: ha nincs hozzáadva TEXTFIELDHEZ WHERE, ORDERBY, GROUP BY colName akkor is bekerül a querybe "WHERE" "ORDER BY".. colName nélkül
    AppCoreChB appCore;
    private IncomeController incomeController;
    private PartnerController partnerController;
    private JoinController joinController;
    private List<EntityController> entityControllerList;
    private JoinEntityController joCo;
    private List<CheckBox> tblChbList;
    private Model model;
    private Database database;
    private static Connection connection;
    private PauseTransition triggerDel;
    private Scene scene;    
    @FXML
    private Button delBtn1, delBtn, loadBtn;
    @FXML
    private TextArea queryTxtArea;
    @FXML
    private Label nfo;
    @FXML
    private Spinner<Integer> topSpin; 
    @FXML
    private CheckBox disableTopSpin, descChb, isNullChB, isNullChB1, notNullChB1, notNullChB;
    @FXML
    private ComboBox<String> whereCB, whereJoinCB, groupByCB, whereOpCB, whereOpCB1, joinCB, onCB0, onCB1, aggregateCB, aggName;
    @FXML
    private TextField andOrTF, andOrTF1, thanTF, thanTF1, joinAS0, joinAS1, orderByTF, groupTF;
    @FXML
    TextField urlTF, databaseTF, userTF;
    @FXML
    PasswordField passwordTF;
    private List<String> selectedAggrFunct, selectedAggrNameList, tableNames;
    private Map<String, String> aggregateMap = new HashMap<>();
    public String aggregateFunction = new String();
    public String selectedAggName = new String();
    private List<ComboBox> orderByCBList;
    // Table
    @FXML
    private VBox root;
    @FXML                         //oszlop név checkBoxok
    private HBox tableChbContainer, colNameChbContainer, orderBcontainer;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {        
        appCore = new AppCoreChB();
        scene = appCore.getScene();
        incomeController = new IncomeController(this);
        partnerController = new PartnerController(this);
        joinController = new JoinController(this, incomeController, partnerController); // Pass the existing instances here
        model = AppCoreChB.getContext().getBean(Model.class);
        database = new Database();
        orderByCBList = new ArrayList<>();
        entityControllerList = new ArrayList<>();        
        
        //createTableChbs("financial_management");
        setUpUI();        
    }
    public static Connection getConnection()
    {
        return connection;
    }
    public void connectToDatabase()
    {
        System.out.println("connectToDatabase triggered");
        if(!urlTF.getText().isEmpty() && !databaseTF.getText().isEmpty() && !userTF.getText().isEmpty() && !passwordTF.getText().isEmpty())
        {
            try{
                connection = Database.getConnection(urlTF.getText(), userTF.getText(), passwordTF.getText(), databaseTF.getText());
                model.setConnection(connection);
                if(connection != null){
                     PopUpMessage msg = new PopUpMessage("Database connected", root);
                }
                    
            }
            catch (SQLException e){
                MessageBox.Show("Error", e.getMessage());
            } 
        } else {
            System.out.println("valamelyik Textfield empty");
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
    
    public void createTableChbs(String databaseName) //Table checkBoxok + onAction
    {   try
        {
            tableNames = model.getTableNamesNew(databaseName);
        } catch (SQLException ex)
        {
            Logger.getLogger(AppControllerChB.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblChbList = new ArrayList<>();
        //minden tableCheckboxhoz tartozik egy selectedTable instance.
        //az Integer az entitás lekérdezésére kell a tbChbList-ből
        //kezdetben mindegyik false: nincs kiválasztva
        //onActionbe tenni: hogy ha kiválasztanak egy táblát false->true
        //majd ..        
        //vaagy szükségem van a ...checkbox indexére amit használhatok EntityControllert választani az entityList-ből
        
        for(int i = 0; i < tableNames.size(); i++){
            tblChbList.add(new CheckBox(tableNames.get(i))); // checkbox létrehozása tábla névvel + szélesség beállítása
            Text txt = new Text( tblChbList.get(i).getText());
            Double width = txt.getLayoutBounds().getWidth();
            tblChbList.get(i).setMinWidth(width * 1.4);            
            
            tableChbContainer.getChildren().add(tblChbList.get(i)); // checkbox konténerhez adása
            
            EntityController entity = new EntityController(this, colNameChbContainer);//új entity
            entityControllerList.add(entity); 
            
            ComboBox<String> comboBox = new ComboBox<>(); // orderBy comboboxok létrehozása
            comboBox.setMinWidth(116);
            comboBox.getItems().add(null);
            orderByCBList.add(comboBox);
            orderBcontainer.getChildren().add(orderByCBList.get(i));
        }
        for(int i =0; i < tableNames.size(); i++) //táblaNév checkboxokhoz onAction hozzáadása
        {
            final int index = i;
            tblChbList.get(i).setOnAction(event ->{
                if(tblChbList.get(index).isSelected())
                {
                    entityControllerList.get(index).createColumnChb( tableNames.get(index), index);
                    queryTxtArea.setText("SELECT ");
                }
                else
                {
                    entityControllerList.get(index).clearCheckBoxes();
                }
            });
        }            
    }
    // KÖVETKEZŐ 
    public void setQueryToTxtArea()
    {   /*tudnom kell: 1. Van-e kiválasztva entityController chb.
                       2. Melyik van kiválasztva.
                       3. Egy vagy két tábla van kiválasztva ?
        Tábla chb-ok listája: tblChbList
        */
        List<Integer> selectedTables = new ArrayList<>();
        for(int i = 0; i < entityControllerList.size(); i++) // kiderül 1 vagy több és melyik tábla van kiválasztva(egyelőre 2 tábla kiválasztása van csak megoldva
        {            
            if(tblChbList.get(i).isSelected() && !selectedTables.contains(i)) 
            {
                selectedTables.add(i);
            }
        }
        int selectedTblCount = selectedTables.size();
        
        if(selectedTblCount > 1) //JOIN query build
        {
            System.out.println("Join query building");
            int index0 = selectedTables.get(0);
            int index1 = selectedTables.get(1);
            joCo = new JoinEntityController(this, entityControllerList.get(index0), entityControllerList.get(index1));            
            joCo.buildQuery();
        }
        else if(selectedTblCount <= 1) //solo table query build
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
        List<Integer> selectedTables = new ArrayList<>();
        for(int i = 0; i < entityControllerList.size(); i++) // kiderül 1 vagy több és melyik tábla van kiválasztva(egyelőre 2 tábla kiválasztása van csak megoldva
        {            
            if(tblChbList.get(i).isSelected() ) 
            {
                selectedTables.add(i);
            }
        }
        int selectedTblCount = selectedTables.size();
        
        if(selectedTblCount > 1) //JOIN QUERY
        {
            joCo.expectoResult();
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
    public void inflateJoinWhereCB()
    {
        String a = joinAS0.getText();
        String b = joinAS1.getText();
        if(!a.isEmpty() && !b.isEmpty() && joCo != null)
        {
            joCo.inflateWhereCb(a, b);
        }
    }
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
        txt = txt.substring(0, txt.length()-2); // utolsó szóköz és vessző törlése
        if (!txt.isEmpty())
        {
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
        for(int i = 0; i < orderByCBList.size(); i++)
        {
            if(orderByCBList.get(i).getValue() != null)
            {
                if(descChb.isSelected()){
                    orderByTF.appendText(orderByCBList.get(i).getValue() + " DESC, ");
                } else {
                    orderByTF.appendText(orderByCBList.get(i).getValue() + " ASC, ");
                }
            }
        }       
    }
    //TÖRLÉS GOMB
    public void delLastOrderByClause()
    {
        String txt = orderByTF.getText().trim();
        txt = txt.substring(0, txt.length()-2);        
        if (!txt.isEmpty())
        {
            int lastSpaceIndex = txt.lastIndexOf(" ") + 1;
            if(lastSpaceIndex > 0){
                orderByTF.setText(txt.substring(0, lastSpaceIndex));
            }
            else {
                orderByTF.clear();
            }
        }
    }
    
    public void setUpUI()
    {   //INFO MSG
        selectedAggrFunct = new ArrayList<>();
        selectedAggrNameList = new ArrayList<>();
        Tooltip tooltip = new Tooltip("Unselect all to SELECT * ");
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(nfo, tooltip);        
        nfo.getStyleClass().add("nfo");
        nfo.setOnMouseEntered(event -> {
            nfo.getStyleClass().add("hover");
        });
        nfo.setOnMouseExited(event -> {
            nfo.getStyleClass().remove("hover");
        });
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
        
        
        
        //ORDER BY        
        for(int i = 0; i < orderByCBList.size(); i++)
        {   
            int index = i;
            orderByCBList.get(i).valueProperty().addListener((observable, oldValue, newValue) -> // TEEEEEEEEEEEEEST
            {
                if(orderByCBList.get(index).getValue() != null){
                        for(int j = 0; j < orderByCBList.size(); j++)
                        {
                            if(j != index){
                                orderByCBList.get(j).getSelectionModel().select(0);
                            }
                            
                        }
                    }
            });
        }
        
        //JOIN
        andOrTF1.setText(null);
        List<String> tableNames = model.getTableNames(model.getJdbcTemplate(), "financial_management");
        joinCB.getItems().addAll(tableNames);
        joinAS0.textProperty().addListener((observable, oldValue, newValue) ->
        {
            inflateJoinWhereCB();
        });
        joinAS1.textProperty().addListener((observable, oldValue, newValue) ->
        {
            inflateJoinWhereCB();
        });
        
        //ON átírni dinamikusra
        onCB0.getItems().addAll(model.getColumnNames("db__income"));
        onCB1.getItems().addAll(model.getColumnNames("db__partners"));
        aggName.getItems().addAll(model.getColumnNames("db__income"));
        aggName.getItems().addAll(model.getColumnNames("db__partners"));
        groupByCB.getItems().addAll(model.getColumnNames("db__income"));
        groupByCB.getItems().addAll(model.getColumnNames("db__partners"));
        whereCB.getItems().addAll(model.getColumnNames("db__income"));
        whereCB.getItems().addAll(model.getColumnNames("db__partners"));
        
        // IIIIITTTTT TARTOOOK lehet kéne egy JoinController mert.
        
        //AGGREGATE CLAUSE
        
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
        whereCB.getItems().add(0, null);
        whereJoinCB.getItems().add(0, null);
        groupByCB.getItems().add(0, null);
        whereOpCB.getItems().add(0, null);
        whereOpCB1.getItems().add(0, null);
        aggregateCB.getItems().add(0,null);
    }
    
    /*
    SELECT i.id, i.amount, i.created, i.approved, p.name AS partner_name, p.contact AS partner_contact
    FROM db__income i
    JOIN db__partners p ON i.partner = p.id;
    */
    
    
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
    public List<ComboBox> getOrderBcBList() {
        return orderByCBList;
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
    
    
}
