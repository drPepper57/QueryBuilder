
package com.pepper.SpringFxCheckBox.Gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MessageBox 
{
    private static Alert msg;

    public MessageBox(String title, String header, String content, int type ) 
    {
        init(title, header, content, type);
    }
    
    public MessageBox(String title, String header, String content)
    {
        init(title, header, content, INFO);
    }
    
    public MessageBox(String title, String content)
    {
        init(title, null, content, INFO  );
    }
    
    private void init(String title, String header, String content, int type)
    {
        AlertType aType = Alert.AlertType.NONE;
        
        switch(type)
        {
            case INFO: aType = Alert.AlertType.INFORMATION;
            break;
            case WARN: aType = Alert.AlertType.WARNING;
            break;
            case ERROR: aType = aType = Alert.AlertType.ERROR;
            break;
        }
        
        msg = new Alert(Alert.AlertType.NONE);
        msg.setTitle(title);
        msg.setHeaderText(header);
        msg.setContentText(content);
    }
    
    public void show()
    {
        msg.showAndWait();
    }
    
    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;
    
    
    public static void Show(String title, String header, String content, int type)
    {
        AlertType aType = Alert.AlertType.NONE;
        
        switch(type)
        {
            case INFO: aType = Alert.AlertType.INFORMATION;
            break;
            case WARN: aType = Alert.AlertType.WARNING;
            break;
            case ERROR: aType = aType = Alert.AlertType.ERROR;
            break;
        }
        
        Alert msg = new Alert(aType); // ITT aType helyett Alert.AlertType.NONE volt :D
        msg.setTitle(title);
        msg.setHeaderText(header);
        msg.setContentText(content);
        msg.showAndWait();
    }
    
    public static void Show(String title, String header, String content)
    {
        Show(title, header, content, INFO);
    }
    
    public static void Show(String title, String content)
    {
        Show(title, null, content);
    }
}
