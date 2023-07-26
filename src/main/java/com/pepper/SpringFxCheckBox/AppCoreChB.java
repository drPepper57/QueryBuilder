package com.pepper.SpringFxCheckBox;

import com.pepper.SpringFxCheckBox.Controller.AppControllerChB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class AppCoreChB extends Application
{
    private static ConfigurableApplicationContext context;
    Scene scene;

    @Override
    public void start(Stage stage) throws Exception 
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
        scene = new Scene(loader.load(), 1024, 768);
        stage.setScene(scene);
        stage.setTitle("DataBase query builder");
        stage.show();
        
        //AppControllerChB controller = new AppControllerChB(scene);
    }

    @Override
    public void init() throws Exception {
        super.init(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        context = new SpringApplicationBuilder(SpringFxCheckBox.class).run();
    }
    
    public static ConfigurableApplicationContext getContext()
    {
        return context;
    }
    public Scene getScene() {
        return scene;
    }
    
    
}
