package com.pepper.SpringFxCheckBox;

import com.pepper.SpringFxCheckBox.Model.Account;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class AppCoreChB extends Application
{
    private static ConfigurableApplicationContext context;
    private static Stage primaryStage;
    private static Scene scene, mainScene;
    private static final String LOGIN_FXML = "login";
    private static final String MAIN_FXML = "apptest";
    public static Account loginAcc;
    
    @Override
    public void start(Stage Stage) throws Exception 
    {
        this.primaryStage = Stage;
        scene = new Scene(loadFXML(LOGIN_FXML));
        primaryStage.setScene(scene);
        //primaryStage.setMaximized(true);  
        primaryStage.initStyle(primaryStage.getStyle().DECORATED);
        primaryStage.setTitle("SQL query builder");
        primaryStage.show();
        
        
        loginAcc = new Account("", "");
    }
    
    
    public static void setRoot() throws IOException 
    {
        mainScene = new Scene(loadFXML(MAIN_FXML));        
        
        primaryStage.setScene(mainScene); 
        primaryStage.setMaximized(true);
        primaryStage.show();
        
    }
    private static Parent loadFXML(String fxml) throws IOException 
    {
        FXMLLoader fxmlLoader = new FXMLLoader(AppCoreChB.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void init() throws Exception {
        super.init(); 
        context = new SpringApplicationBuilder(SpringFxCheckBox.class).run();
    }
    
    public static ConfigurableApplicationContext getContext()
    {
        return context;
    }
    public Scene getScene() {
        return scene;
    }
    @Override
    public void stop() throws Exception 
    {
        // Close the Spring Boot context when the JavaFX application stops
        context.close();
        // Perform any cleanup or resource release tasks here

        // Call the superclass's stop() method to ensure proper shutdown
        super.stop();
    }
    
}
