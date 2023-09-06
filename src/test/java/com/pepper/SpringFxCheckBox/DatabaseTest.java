package com.pepper.SpringFxCheckBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


public class DatabaseTest extends ApplicationTest
{
    Connection connection;
    
    @Test
    void testDatabaseConnection()
    {
        try
        {
            connection = Database.getConnection("jdbc:mysql://localhost:3306", "root", "3@5QnK7HrgnWex9", "financial_management");
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Test
    void testRetrieveData()
    {
        try
        {
            connection = Database.getConnection("jdbc:mysql://localhost:3306", "root", "3@5QnK7HrgnWex9", "financial_management");
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query = "SELECT project FROM db__income WHERE id = ?";
        
        try
        {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, 6);
            
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                String projectName = resultSet.getString("project");
                Assertions.assertNotNull(projectName);
                Assertions.assertEquals("Nu Jamaica server", projectName);
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Test
    void testRetrieveData1()
    {
        try
        {
            connection = Database.getConnection("jdbc:mysql://localhost:3306", "root", "3@5QnK7HrgnWex9", "financial_management");
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query = "Select * from db__income ORDER BY created DESC LIMIT 1;";
        
        try
        {
            PreparedStatement statement = connection.prepareStatement(query);            
            
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                String id = resultSet.getString("id");
                String partner = resultSet.getString("partner");
                String amount = resultSet.getString("amount");
                String projectName = resultSet.getString("project");
                Date approved = resultSet.getDate("approved");         
                
                Assertions.assertNotNull(projectName);                
                Assertions.assertEquals("1093", id);
                Assertions.assertEquals("2", partner);
                Assertions.assertEquals("713000", amount);
                Assertions.assertEquals("Pi Panama layers", projectName);
                Assertions.assertNull(approved);
                
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
/*
Database Connection:
Data Retrieval:
Queries and Filters:
Error Handling:
*/