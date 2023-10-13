package com.pepper.SpringFxCheckBox.View;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


public class DisplayFK 
{
    private HBox box;
    private VBox getBox, gotBox;
    private Label getColName, gotColName;
    private Label getFK, gotFK;
    private Pane parent;
    
    public DisplayFK(Pane parent, String col1, String fk1, String col2, String fk2)
    {
        this.parent = parent;
        
        box = new HBox();
        box.setMaxWidth(Region.USE_PREF_SIZE);
        HBox.setMargin(box, new Insets(8, 0, 0,0));
        
        getBox = new VBox();
        getBox.setAlignment(Pos.CENTER_LEFT);
        getBox.setStyle("-fx-border-color: rgb(140,140,155);");
        getBox.setPrefWidth(145);
        getBox.setMaxWidth(Region.BASELINE_OFFSET_SAME_AS_HEIGHT);
        
        
        gotBox = new VBox();        
        gotBox.setAlignment(Pos.CENTER_RIGHT);
        gotBox.setStyle("-fx-border-color: rgb(140,140,155); -fx-border-style: solid solid solid hidden;");
        gotBox.setPrefWidth(145);
        gotBox.setMaxWidth(Region.BASELINE_OFFSET_SAME_AS_HEIGHT);
        
        box.setMargin(getBox, new Insets(0, 0, 0,3));
        box.setMargin(gotBox, new Insets(0, 3, 0,0));
        
        getColName = new Label(col1);
        getColName.getStyleClass().add("fk");
        getColName.getStyleClass().add("fkTable");
        
        getFK = new Label(fk1);
        getFK.getStyleClass().add("fk");
        
        
        gotColName = new Label(col2);
        gotColName.getStyleClass().add("fk");
        gotColName.getStyleClass().add("fkTable");
        gotColName.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
        gotFK = new Label(fk2);
        gotFK.getStyleClass().add("fk");        
        gotFK.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
        getBox.getChildren().addAll(getColName, getFK);
        gotBox.getChildren().addAll(gotColName, gotFK);
        
        
        box.getChildren().addAll(getBox, gotBox);        
        
        parent.getChildren().add(box);        
    }
    
    public void clear()
    {
        parent.getChildren().clear();
    }
    

}
