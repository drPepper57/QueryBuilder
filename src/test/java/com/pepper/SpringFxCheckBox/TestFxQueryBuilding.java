package com.pepper.SpringFxCheckBox;

import org.testfx.api.FxToolkit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class TestFxQueryBuilding extends ApplicationTest
{
    Button loginBtn = new Button("Login");
    Label label = new Label("Hello, ");
    TextField userTF = new TextField();
    CheckBox tabelChB = new CheckBox("Income");
    ComboBox ColumnNamesCB = new ComboBox();
    ComboBox aggregationCB = new ComboBox();
    TextField parameterTF = new TextField();
    CheckBox isNullChb = new CheckBox("Null");        
    TextArea queryTA = new TextArea();
    Button buildQueryBtn = new Button("Build query");
    Button exeBtn = new Button("Execute query");
    
    @BeforeAll
    public static void setupClass() throws Exception {
        FxToolkit.registerPrimaryStage();
    }
    
    @Override
    public void start(Stage stage) 
    {
        
        List<String> colNames = new ArrayList<>();
        colNames.add("id");
        colNames.add("amount");
        colNames.add("created");        
        ColumnNamesCB.getItems().addAll(colNames);
        colNames.add(0, null);
        List<String> operators = new ArrayList<>();
        operators.add("<");
        operators.add(">");
        operators.add("=");
        operators.add("<=");
        operators.add(">=");
        aggregationCB.getItems().addAll(operators);
        aggregationCB.getItems().add(0, null);
        
        queryTA.setPrefHeight(30);
        
        VBox root = new VBox(); 
        root.setStyle("-fx-background-color: darkgrey");
        root.getChildren().add(userTF);
        root.getChildren().add(loginBtn);
        root.getChildren().add(label);
        root.getChildren().add(tabelChB);
        root.getChildren().add(ColumnNamesCB);
        root.getChildren().add(aggregationCB);
        root.getChildren().add(parameterTF);
        root.getChildren().add(isNullChb);
        root.getChildren().add(queryTA);
        root.getChildren().add(buildQueryBtn);
        root.getChildren().add(exeBtn);
        
        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("TestFX");
        stage.setScene(scene);
        stage.show();
        
        loginBtn.setOnAction(event -> 
        {
            String user = "";
            if(userTF.getText() != null){
                user = userTF.getText();
            } else {
                System.out.println("Please enter your name");
            }
            label.setText(label.getText() + user + " !");            
        });
        
        StringBuilder queryBuilder = new StringBuilder();
        buildQueryBtn.setOnAction(event -> 
        {            
            queryBuilder.append("SELECT * FROM DATABASE ");
            //WHERE
            if(ColumnNamesCB.getValue() != null){ // WHERE *oszlopnév* 
                queryBuilder.append("WHERE ").append(ColumnNamesCB.getValue()).append(" ");
            }
            if( isNullChb.isSelected()){
                queryBuilder.append("IS NULL");
            } else if(aggregationCB.getValue() != null && parameterTF.getText() != null){ // *oszlopnév* < parameterTF ben megadott érték
                queryBuilder.append(aggregationCB.getValue()).append(" ").append(parameterTF.getText());
            }
            queryBuilder.append(";");
            String query = queryBuilder.toString();
            if(!query.isEmpty()){
                queryTA.setText(query);
            }
            
        });
        
        exeBtn.setOnAction(event ->
        {
            
        });
    }
    
    @Test
    void testLoginAction()
    {
        userTF.setText("Pepper");
        clickOn(loginBtn);
        Assertions.assertEquals("Hello, Pepper !", label.getText());
    }
    
    @Test
    void testQueryBuilding()
    {
        /*parameterTF
        1. tábla kiválasztása
        2. oszlopNév
        3. feltétel
        4. Query TextAreaba helyezés
        */
        clickOn(tabelChB);
        clickOn(ColumnNamesCB);
        clickOn("amount");
        clickOn(aggregationCB);
        clickOn(">");
        clickOn(parameterTF);
        write("50000");
        clickOn(buildQueryBtn);
        Assertions.assertEquals("SELECT * FROM DATABASE WHERE amount > 50000;", queryTA.getText());        
    }
    @Test
    void testQueryBuilding1()
    {   //SELECT * FROM DATABASE WHERE created IS NULL;
        //SELECT * FROM DATABASE WHERE created IS NULL ;
        clickOn(tabelChB);
        clickOn(ColumnNamesCB);
        clickOn("created");        
        clickOn(isNullChb);
        clickOn(buildQueryBtn);
        Assertions.assertEquals("SELECT * FROM DATABASE WHERE created IS NULL;", queryTA.getText());
    }
    @Test
    void testQueryBuilding2()
    {   clickOn(tabelChB);
        clickOn(ColumnNamesCB);
        clickOn("id");
        clickOn(aggregationCB);
        clickOn("=");
        clickOn(parameterTF);
        write("32");
        clickOn(buildQueryBtn);
        Assertions.assertEquals("SELECT * FROM DATABASE WHERE id = 32;", queryTA.getText());
    }
}
