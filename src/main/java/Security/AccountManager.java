package Security;

import com.pepper.SpringFxCheckBox.Model.Account;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
Map<String, Account> acc = AccountManager.loadDBAccounts();
        acc.get(AppCoreChB.loginAcc.getUserLogin());

*/

public class AccountManager 
{
    public static void SaveDBAccount(Map<String, Account> acc) //loginAccal összekötött DBaccount
    {                                                         //key == userLogin
        File file = new File("accounts.csv");
        System.out.println("SaveDBAccount triggered");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for(Map.Entry<String, Account> entry : acc.entrySet())
            {
                String key = entry.getKey();                
                Account account = entry.getValue();
                String line = key + ";" + account.getUrl() + ";" + account.getDatabase() + ";" + account.getUserDB();
                writer.write(line);
                writer.newLine();
            }
            
        }
        catch (IOException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static Map<String, Account> loadDBAccounts()
    {
        File file = new File("accounts.csv");
        Map<String, Account> accMap = new HashMap<>();
        
        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {            
            String line;
            while((line = reader.readLine()) != null)
            {
                System.out.println("(line = reader.readLine()) != null");
                String[] parts = line.split(";");
                System.out.println("parts.length " + parts.length);
                if(parts.length == 4)
                {
                    System.out.println("parts.length == 3 at loadDBAccounts");
                    String key = parts[0];
                    System.out.println("key at loadDBAccounts " + key);
                    Account account = new Account(parts[1], parts[2], parts[3]);
                    accMap.put(key, account);                    
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return accMap;
    }
    
    public static void SaveAcc(Account acc) // login account
    {
        File file = new File("loginAccs.csv");
        try
        {
            FileWriter writer = new FileWriter(file);
            //loginACC
            String line = acc.getUserLogin()+";"+acc.getPassword();
            writer.write(line);
            writer.close();            
            
        } catch (IOException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Map<String, String> LoadUsers() // username, hashedP
    {
        Map<String, String> userList = new HashMap<>();
        
        File file = new File("loginAccs.csv");
        try
        {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                
                userList.put(parts[0], parts[1]);
            }
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userList;
    }
    
    public static void saveUserNameAndSalt(String userName, byte[] newSalt) // userName, salt
    {
        String fileName = "usernamesAndSalts.bin";
        System.out.println("save user: " + userName + " save salt: " + newSalt);
        try (OutputStream os = new FileOutputStream(fileName);
         DataOutputStream dos = new DataOutputStream(os))
        {
            // Write the username followed by a newline character
            dos.writeUTF(userName);

            // Write the salt length and then the salt itself
            dos.writeInt(newSalt.length);
            dos.write(newSalt);

            // Write a newline character as a delimiter between entries
            dos.write('\n');
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static Map<String,byte[]> LoadUserNamesAndSalts() // userName, salt
    {
        String fileName = "usernamesAndSalts.bin";
        Map<String, byte[]> usernameSaltMap = new HashMap<>();
        
        try (InputStream is = new FileInputStream(fileName);
             DataInputStream dis = new DataInputStream(is)) 
        {
            
            while (dis.available() > 0) 
            {
                
                String userName = dis.readUTF();
                int saltLength = dis.readInt();
                byte[] salt = new byte[saltLength];
                dis.readFully(salt);
                System.out.println("Loaded user: " + userName + " Loaded salt: " + Arrays.toString(salt));
                
                // Skip the newline character ('\n') as the delimiter
                dis.skipBytes(1);
                System.out.println("skipped byte: " + dis.skipBytes(1));
                usernameSaltMap.put(userName, salt);
            }
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usernameSaltMap;
    }
}
