package com.pepper.SpringFxCheckBox.View;

import javafx.scene.layout.Pane;


public class TextField extends javafx.scene.control.TextField
{
    public TextField(Pane parent, double width)
    {
        super();
        this.setPrefWidth(width);
        this.setPromptText("AS");
        parent.getChildren().add(this);
    }
}
