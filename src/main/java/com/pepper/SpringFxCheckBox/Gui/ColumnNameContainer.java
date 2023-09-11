package com.pepper.SpringFxCheckBox.Gui;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;


public class ColumnNameContainer extends AnchorPane
{
    HBox colNameChbContainer, asTFcontainer;
    
    public ColumnNameContainer(Pane parent)
    {
        super();
        this.setPrefHeight(66);
        //this.setStyle("-fx-border-color:  rgba(70, 70, 90, 0.3); -fx-border-width: 1; -fx-border-style: hidden solid hidden hidden;");
        
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor( rgb(70, 70, 100));
        innerShadow.setRadius(15);
        
        VBox actualContainer = new VBox();
        
        
        actualContainer.setPrefHeight(66);
        colNameChbContainer = new HBox();
        colNameChbContainer.getStyleClass().add("hbox");
        asTFcontainer = new HBox();
        asTFcontainer.getStyleClass().add("hbox");   
        
        Separator sepVert = new Separator();
        sepVert.setOrientation(Orientation.VERTICAL);
        sepVert.setPrefHeight(68);
        sepVert.setEffect(innerShadow);
        
        //sepVert.setOpacity(0.3);
        
        actualContainer.getChildren().addAll(colNameChbContainer,asTFcontainer);        
        this.getChildren().addAll(actualContainer,sepVert);
        
        parent.getChildren().add(this);
    }

    public HBox getColNameChbContainer() {
        return colNameChbContainer;
    }

    public HBox getAsTFcontainer() {
        return asTFcontainer;
    }    
}
