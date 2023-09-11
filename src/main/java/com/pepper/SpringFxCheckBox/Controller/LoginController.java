package com.pepper.SpringFxCheckBox.Controller;

import Security.AccountManager;
import Security.PasswordFactory;
import com.pepper.SpringFxCheckBox.AppCoreChB;
import com.pepper.SpringFxCheckBox.Model.Account;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController 
{
    @FXML
    TextField userTF;
    @FXML
    PasswordField passF;
    @FXML
    Button loginBtn, signUpBtn;
    @FXML
    Label msgL;
    //email, salt
    
    private Timer timer;
    
    public void initialize()
    {
        
    }
    
    @FXML
    public void signUp()
    {
        if(!userTF.getText().isEmpty() && !passF.getText().isEmpty())
        {
            byte[] salt = PasswordFactory.generateSalt16Byte();
            System.out.println("ELmentett SALT signUP után: " + Arrays.toString(salt));
            
            String hashedP = PasswordFactory.HashPassword(passF.getText(), salt);
            System.out.println("ELmentett jelszó signUP után: " + hashedP);
            
            String user = userTF.getText();
            Account loginAcc = new Account(user, hashedP);
            //két fájlt kell létrehozni: user, salt && user hashedPassword
            AccountManager.SaveAcc(loginAcc);
            AccountManager.saveUserNameAndSalt(user, salt);
            
            msgL.setStyle("-fx-text-fill: white;");
            msgL.setText("Account saved");
        } else {
            System.out.println("TextField-s empty");
        } 
   }
    
    @FXML
    public void login() throws Exception
    {
        Map<String, byte[]> usernameSaltMap = AccountManager.LoadUserNamesAndSalts(); ////user, salt
        Map<String, String> userAndHashedP = AccountManager.LoadUsers(); //user, hashedP
        if(usernameSaltMap.isEmpty()){
            System.out.println("usernameSaltMap.isEmpty() " + usernameSaltMap.isEmpty() );
        }
        
        if(!userTF.getText().isEmpty() && !passF.getText().isEmpty())//TF nem üres
        {
            if(usernameSaltMap.containsKey(userTF.getText())) //van ilyen user
            {
                String user = userTF.getText();                
                byte[] salt = usernameSaltMap.get(user); // lekérjük a mentett saltot
                
                System.out.println("Mapból betöltött SALT logIN után: " + Arrays.toString(salt));
                String hashedP = PasswordFactory.HashPassword(passF.getText(), salt);//generálunk egy hashedP-t a beírt jelszóval és az elmentett salt-tal
                
                System.out.println("Jelenleg beírt hashelt P: "+hashedP + " Fájlból betöltött P: " + userAndHashedP.get(userTF.getText()) );
                if (MessageDigest.isEqual(hashedP.getBytes(), userAndHashedP.get(userTF.getText()).getBytes())) //az éppen beírt hashelt password == az elmentett hashelt pass-al
                {
                    msgL.setStyle("-fx-text-fill: white;");
                    msgL.setText("Login successful");
                    
                    AppCoreChB.loginAcc.setUserLogin(user);
                    loginBtn.getScene().getWindow().hide();
                    AppCoreChB.setRootMain();
                }//
                else 
                {
                    msgL.setStyle("-fx-text-fill: white;");
                    msgL.setText("Wrong password");
                    System.out.println("wrong");
                }
            } else {
                msgL.setStyle("-fx-text-fill: white;");
                msgL.setText("Username not found");
            }
        }
    }
    
    public void delayMethod(Runnable method, int delay) //nem szereti ha voidot kap
            
    {
        timer = new Timer();
        timer.schedule(new TimerTask() 
        {
            @Override
            public void run() 
            {
                Platform.runLater(() -> {
                    method.run();
                    timer.cancel();
                });
            }
        }, delay);
    }
   
}
