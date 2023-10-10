package com.pepper.SpringFxCheckBox.Model;

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
        HBox.setMargin(box, new Insets(5, 0, 0,0));
        
        getBox = new VBox();
        getBox.setAlignment(Pos.CENTER_LEFT);
        getBox.setStyle("-fx-border-color: rgb(140,140,155);");
        getBox.setPrefWidth(145);
        getBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
        
        
        gotBox = new VBox();        
        gotBox.setAlignment(Pos.CENTER_RIGHT);
        gotBox.setStyle("-fx-border-color: rgb(140,140,155); -fx-border-style: solid solid solid hidden;");
        gotBox.setPrefWidth(145);
        gotBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
        
        box.setMargin(getBox, new Insets(0, 0, 0,3));
        box.setMargin(gotBox, new Insets(0, 3, 0,0));
        
        getColName = new Label(col1);
        getColName.getStyleClass().add("fk");
        getColName.setPrefWidth(145);        
        
        getFK = new Label(fk1);
        getFK.getStyleClass().add("fk");        
        getFK.setPrefWidth(145);
        getFK.setStyle("-fx-border-color: rgb(140,140,155); -fx-border-style: solid hidden hidden hidden;");
        
        gotColName = new Label(col2);
        gotColName.getStyleClass().add("fk");
        gotColName.setPrefWidth(145);
        gotColName.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        
        gotFK = new Label(fk2);
        gotFK.getStyleClass().add("fk");
        gotFK.setPrefWidth(145);
        gotFK.setStyle("-fx-border-color: rgb(140,140,155); -fx-border-style: solid hidden hidden hidden;");
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
