package com.pepper.SpringFxCheckBox.Gui;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class ColumnNameContainer extends AnchorPane
{
    HBox colNameChbContainer, asTFcontainer;
    
    public ColumnNameContainer(Pane parent)
    {
        super();
        this.setPrefHeight(66);
        VBox actualContainer = new VBox();
        actualContainer.setPrefHeight(66);
        colNameChbContainer = new HBox();
        colNameChbContainer.getStyleClass().add("hbox");
        asTFcontainer = new HBox();
        asTFcontainer.getStyleClass().add("hbox");
        
        actualContainer.getChildren().add(colNameChbContainer);
        actualContainer.getChildren().add(asTFcontainer);
        this.getChildren().add(actualContainer);
        parent.getChildren().add(this);
    }

    public HBox getColNameChbContainer() {
        return colNameChbContainer;
    }

    public HBox getAsTFcontainer() {
        return asTFcontainer;
    }    
}
