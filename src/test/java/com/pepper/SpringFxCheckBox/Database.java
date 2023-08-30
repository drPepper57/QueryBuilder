package com.pepper.SpringFxCheckBox;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;


public class Database 
{
    private static BasicDataSource dataSource;
    
    public static BasicDataSource createDataSource(String url, String username, String password, String database) throws SQLException
    {
       dataSource = new BasicDataSource();       
       String fullUrl = url + "/" + database;
       dataSource.setDriverClassName("com.mysql.jdbc.Driver");
       dataSource.setUrl(fullUrl);
       dataSource.setUsername(username);
       dataSource.setPassword(password);
       //3@5QnK7HrgnWex9

       dataSource.setMaxTotal(10); // Maximum number of connections in the pool
       dataSource.setInitialSize(5); // Initial number of connections to be created        

       return dataSource;
    }
     
     public static Connection getConnection(String url, String username, String password, String database) throws SQLException {
        return createDataSource(url, username, password, database).getConnection();
    }

}
