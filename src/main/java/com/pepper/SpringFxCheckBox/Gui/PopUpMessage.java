package com.pepper.SpringFxCheckBox.Gui;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class PopUpMessage extends Popup
{
    private Timer timer;
    
    public PopUpMessage(String message, Pane parent)
    {
        Label msg = new Label(message);
        msg.getStyleClass().add("popUp");
        
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        
        vBox.getChildren().add(msg);
        
        Scene popupScene = new Scene(vBox, 200, 50);        
        Stage popupStage = new Stage();        
        
        
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.setScene(popupScene);
        
        popupStage.show();
        
        timer = new Timer();
        timer.schedule(new TimerTask() 
        {
            @Override
            public void run() 
            {
                Platform.runLater(() -> {
                    popupStage.close();
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }
                });
            }
        }, 1500);
    }

}
