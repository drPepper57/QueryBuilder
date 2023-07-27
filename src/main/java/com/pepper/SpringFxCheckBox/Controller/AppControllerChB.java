package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Model.Model;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class AppControllerChB implements Initializable 
{
    AppCoreChB appCore;
    IncomeController incomeController;
    PartnerController partnerController;
    JoinController joinController;
    Model model;
    private Scene scene;
    @FXML
    private Pane chbIncContainer, PrtChbContainer, asInc, asPrt;
    @FXML
    private CheckBox incTableChB, prtTableChB;
    @FXML
    private TextArea queryTxtArea;
    @FXML
    private Label nfo;
    @FXML
    private Spinner<Integer> topSpin; 
    @FXML
    private CheckBox disableTopSpin, descChb, isNullChB, isNullChB1;
    @FXML
    private ComboBox<String> orderByCB, whereCB, whereCB1, gourpByCB, whereOpCB, whereOpCB1, joinCB, onCB0, onCB1;
    @FXML
    private TextField thanTxt, thanTxt1, joinAS0, joinAS1;

   
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        appCore = new AppCoreChB();
        scene = appCore.getScene();
        incomeController = new IncomeController(this);
        partnerController = new PartnerController(this);
        joinController = new JoinController(this, incomeController, partnerController); // Pass the existing instances here
        model = AppCoreChB.getContext().getBean(Model.class);
        
        setUpUI();
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
    
    public void setColNames() //1.tábla kiválasztása
    {
        if(incTableChB.isSelected())
        {            
            //IncomeController.setIncomeTableColumnNames();
            incomeController.createIncCheckBoxes();
            queryTxtArea.setText("SELECT ");            
        } else {
            incomeController.clearCheckBoxes();
            asInc.getChildren().clear();
            queryTxtArea.clear();
        }  
    }
    public void setQueryToTxtArea()
    {
        if(incTableChB.isSelected() && prtTableChB.isSelected())
        {
            System.out.println("join query");
            joinController.setUpQueryMaterial();
            joinController.buildQuery();
        }
        else if(incTableChB.isSelected() )
        {
            System.out.println("income query");
            incomeController.buildQuery();
        }
        else
        {
            System.out.println("partner query");
            partnerController.buildQuery();
        }       
    }
    
    public void setUpUI()
    {
        Tooltip tooltip = new Tooltip("Unselect all to select all");
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(nfo, tooltip);
                
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 25);
        topSpin.setValueFactory(valueFactory);
        disableTopSpin.setOnAction(event -> {
            if(disableTopSpin.isSelected()) {topSpin.setDisable(true); disableTopSpin.setText("Enable");}
            else {topSpin.setDisable(false); disableTopSpin.setText("Disable");}         
        });
        //WHERE
        isNullChB.setOnAction(event -> // null<->operátor egymást inaktiválja
        { 
            isNullChB.setSelected(true);
            thanTxt.clear();
            whereOpCB.getSelectionModel().select(0);
        });
        isNullChB1.setOnAction(event -> // null<->operátor egymást inaktiválja
        { 
            isNullChB1.setSelected(true);
            thanTxt1.clear();
            whereOpCB1.getSelectionModel().select(0);
        });
        
        whereOpCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            isNullChB.setSelected(false);
        }
        });
        whereOpCB1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            isNullChB1.setSelected(false);
        }
        });
        
        joinCB.getItems().add(0, null);
        onCB0.getItems().add(0, null);
        onCB1.getItems().add(0, null);
        orderByCB.getItems().add(0, null);
        whereCB.getItems().add(0, null);
        whereCB1.getItems().add(0, null);
        gourpByCB.getItems().add(0, null);
        whereOpCB.getItems().add(0, null);
        
        //JOIN
        List<String> tableNames = model.getTableNames(model.getJdbcTemplate(), "financial_management");
        joinCB.getItems().addAll(tableNames);        
        //ON
        onCB0.getItems().addAll(model.getColumnNames("db__income"));
        onCB1.getItems().addAll(model.getColumnNames("db__partners"));
        // IIIIITTTTT TARTOOOK lehet kéne egy JoinController mert 
        
        nfo.getStyleClass().add("nfo");
        nfo.setOnMouseEntered(event -> {
            nfo.getStyleClass().add("hover");
        });
        nfo.setOnMouseExited(event -> {
            nfo.getStyleClass().remove("hover");
        });
        
        
        
        setwhereOpCB();
    }
    
    /*
    SELECT i.id, i.amount, i.created, i.approved, p.name AS partner_name, p.contact AS partner_contact
    FROM db__income i
    JOIN db__partners p ON i.partner = p.id;
    */
    
    public boolean isNull(){ //SELECT * FROM your_table_name WHERE approved IS NULL;
        return isNullChB.isSelected();
    }
    public boolean isLimitSelected(){
        return disableTopSpin.isSelected();
    }
    public int getTopValue(){
        return topSpin.getValue();
    }
    public String getOrderBy(){
        return orderByCB.getValue();
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
    public ComboBox<String> getOrderByCB() {
        return orderByCB;
    }
    public ComboBox<String> getWhereCB() {
        return whereCB;
    }
    public ComboBox<String> getGroupByCB() {
        return gourpByCB;
    }
    //WHERE
    public TextField getThanTxt() {
        return thanTxt;
    }
    public ComboBox<String> getwhereOpCB(){
        return whereOpCB;
    }
    public String getwhereOpCBValue(){
        return whereOpCB.getValue();
    }
    public String getThanTxtValue(){
        return thanTxt.getText();
    }
    public ComboBox<String> getWhereCB1() {
        return whereCB1;
    }

    public ComboBox<String> getWhereOpCB1() {
        return whereOpCB1;
    }

    public TextField getThanTxt1() {
        return thanTxt1;
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
