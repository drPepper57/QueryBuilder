package com.pepper.SpringFxCheckBox.Controller;


import com.pepper.SpringFxCheckBox.Gui.MessageBox;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteQuery<T>
{
    
    // meg kell kapja a selectedColumns-t parameterben
    public <T> List<T> executeQuery(String query, Class<T> entityClass, List<String> selectedColumns, EntityHandler<T> entityHandler) 
    {
        List<T> entityList = new ArrayList<>();
        
        try (Connection connection = AppControllerChB.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            // Process the result set
            entityList = entityHandler.processResultSet(resultSet, selectedColumns);
            connection.close();
           
        }
        catch (SQLSyntaxErrorException e) 
        {
            MessageBox.Show("Error","SQL syntax error",  e.getMessage()+"\nPlease check your query and try again.", 2);
        }
        catch (SQLException e) 
        {            
            MessageBox.Show("Error", "SQL error", e.getMessage());
        }
        return entityList;
    }
}
