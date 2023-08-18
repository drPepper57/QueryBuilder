package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Model.Model;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class AppControllerChB implements Initializable 
{ // !!!! HIBA: ha nincs hozzáadva TEXTFIELDHEZ WHERE, ORDERBY, GROUP BY colName akkor is bekerül a querybe "WHERE" "ORDER BY".. colName nélkül
    AppCoreChB appCore;
    IncomeController incomeController;
    PartnerController partnerController;
    JoinController joinController;
    private List<EntityController> entityList;
    Model model;
    PauseTransition triggerDel;
    private Scene scene;
    @FXML
    private Pane chbIncContainer, PrtChbContainer, asInc, asPrt;
    @FXML
    private CheckBox incTableChB, prtTableChB;
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
    private ComboBox<String> orderByCB, orderByCB1, whereCB, whereJoinCB, groupByCB, whereOpCB, whereOpCB1, joinCB, onCB0, onCB1, aggregateCB, aggName;
    @FXML
    private TextField andOrTF, andOrTF1, thanTF, thanTF1, joinAS0, joinAS1, orderByTF, groupTF;
    private List<String> selectedAggrFunct, selectedAggrNameList;
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
        orderByCBList = new ArrayList<>();
        entityList = new ArrayList<>();
        
        //orderByCBList.add(orderByCB);
        //orderByCBList.add(orderByCB1);
        
        setUpUI();
        createTableChbs();
    }
    
    public void createTableChbs()
    {                                                 //ezt majd textFieldből 
        List<String> tableNames = model.getTableNames("financial_management");
        List<CheckBox> tbChbList = new ArrayList<>();
        
        for(int i = 0; i < tableNames.size(); i++){
            tbChbList.add(new CheckBox(tableNames.get(i))); // checkbox létrehozása tábla névvel
            Text txt = new Text( tbChbList.get(i).getText());
            Double width = txt.getLayoutBounds().getWidth();            
            tbChbList.get(i).setMinWidth(width * 1.4);            
            tableChbContainer.getChildren().add(tbChbList.get(i)); // checkbox konténerhez adása
            
            EntityController entity = new EntityController(this, colNameChbContainer);//új entity
            entityList.add(entity); 
            
            ComboBox<String> comboBox = new ComboBox<>(); // orderBy comboboxok létrehozása
            comboBox.getItems().add("Új kombobox");
            orderByCBList.add(comboBox);
            orderBcontainer.getChildren().add(orderByCBList.get(i));
        }
        for(int i =0; i < tableNames.size(); i++) //táblaNév checkboxokhoz onAction hozzáadása
        {
            final int index = i;
            tbChbList.get(i).setOnAction(event ->{
                if(tbChbList.get(index).isSelected())
                {
                    entityList.get(index).createColumnChb( tableNames.get(index), index);
                    queryTxtArea.setText("SELECT ");
                }
                else
                {
                    entityList.get(index).clearCheckBoxes();
                }
            });
        }            
    }
    /*
    public void setColNames() //1.tábla kiválasztása
    {
        if(incTableChB.isSelected())
        {
            incomeController.createIncCheckBoxes();
            queryTxtArea.setText("SELECT ");
        } else {
            incomeController.clearCheckBoxes();
            asInc.getChildren().clear();
            queryTxtArea.clear();
        }  
    }
    
    public void setColNames1()  //2.tábla kiválasztása
    {
        if(prtTableChB.isSelected())
        {
            partnerController.createPrtCheckBoxes();
            queryTxtArea.setText("SELECT ");      
        } 
        else 
        {            
            partnerController.clearCheckBoxes();
            asPrt.getChildren().clear();
            queryTxtArea.clear();
        }  
    }
    */
    public void setQueryToTxtArea()
    {
        if(incTableChB.isSelected() && prtTableChB.isSelected())
        {
            System.out.println("join query");
            joinController.setUpQueryData();
            joinController.buildQuery();
        }
        else if(incTableChB.isSelected() )
        {
            System.out.println("income query");
            incomeController.buildQuery();
        }
        else if(prtTableChB.isSelected())
        {
            System.out.println("partner query");
            partnerController.buildQuery();
        }       
    }
    public void expectoQuery()
    {
        if(incTableChB.isSelected() && prtTableChB.isSelected()){
            joinController.expectoResult();
        } 
        else if(incTableChB.isSelected()){
            incomeController.expectoResult();
        }
        else if(prtTableChB.isSelected()){
            partnerController.expectoResult();
        }
        
        
       
        
        
    }
    //JOIN
    public void inflateJoinWhereCB()
    {
        String a = joinAS0.getText();
        String b = joinAS1.getText();
        if(!a.isEmpty() && !b.isEmpty())
        {
            joinController.inflateWhereCb(a, b);
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
    } //paraméterben kéne megadni melyik gombbal melyik TFt törölje
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
                System.out.println(lastSpaceIndex + "last SpaceIndex");
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
        if(orderByCB1.getValue() != null)
        {
            if(descChb.isSelected()){
                orderByTF.appendText(orderByCB1.getValue() + " DESC, ");
            } else {
                orderByTF.appendText(orderByCB1.getValue() + " ASC, ");
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
        AtomicBoolean orderByCBChanged = new AtomicBoolean(false);
        AtomicBoolean orderByCB1Changed = new AtomicBoolean(false);
        orderByCB.valueProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null && !orderByCBChanged.get()) 
            {
                orderByCB1.getSelectionModel().select(0);  
                orderByTF.clear();
            }
            orderByCBChanged.set(true);
            orderByCB1Changed.set(false);
            
        });
        orderByCB1.valueProperty().addListener((observable, oldValue, newValue) -> 
        {
            if (newValue != null && !orderByCB1Changed.get()) 
            {
                orderByCB.getSelectionModel().select(0);   
                orderByTF.clear();
            }
            orderByCBChanged.set(false);
            orderByCB1Changed.set(true);
        });
        
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
        orderByCB.getItems().add(0, null);
        orderByCB1.getItems().add(0, null);
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
    public Pane getIncChBContainer() {
        return chbIncContainer;
    }
    public Pane getPrtChbContainer() {
        return PrtChbContainer;
    }    
    public TextArea getQueryTxtArea() {
        return queryTxtArea;
    }
    public boolean descIsSelected() {
        return descChb.isSelected();
    }
    //ORDER BY
    public ComboBox<String> getOrderByCB() {
        return orderByCB;
    }
    public String getOrderBy(){
        return orderByCB.getValue();
    }
    public ComboBox<String> getOrderByCB1() {
        return orderByCB1;
    }
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
    public Pane getAsInc() {
        return asInc;
    }
    public Pane getAsPrt() {
        return asPrt;
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
