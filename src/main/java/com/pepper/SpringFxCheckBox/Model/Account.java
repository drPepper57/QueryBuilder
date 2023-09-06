package com.pepper.SpringFxCheckBox.Model;


public class Account 
{
    private String url;
    private String database;
    private  String userLogin;
    private String userDB;
    private String password;

    public Account(String user, String password) //loginhoz
    {
        this.userLogin = user;
        this.password = password;
    }
    public Account(String url, String database, String user) //adatbázishoz
    {
        this.url = url;
        this.database = database;
        this.userDB = user;
        this.password = "";
    }

    public String getUrl() {
        return url;
    }
    public String getDatabase() {
        return database;
    }
    public String getUserLogin() {
        return userLogin;
    }    
    public String getUserDB() {
        return userDB;
    }
    public String getPassword() {
        return password;
    }
    

    public void setUrl(String url) {
        this.url = url;
    }
    public void setDatabase(String database) {
        this.database = database;
    }
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }    
    public void setUserDB(String user) {
        this.userDB = user;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    public Account getAccByUser(String userName)
    {
        
        
        return null;
    }

}
