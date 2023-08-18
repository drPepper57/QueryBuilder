package com.pepper.SpringFxCheckBox.Model;

import org.apache.commons.dbcp2.BasicDataSource;


public class Database 
{
    private static BasicDataSource dataSource;
    
     public static BasicDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/financial_management");
            dataSource.setUsername("root");
            dataSource.setPassword("3@5QnK7HrgnWex9");

            
            dataSource.setMaxTotal(10); // Maximum number of connections in the pool
            dataSource.setInitialSize(5); // Initial number of connections to be created
        }
        return dataSource;
    }
}
