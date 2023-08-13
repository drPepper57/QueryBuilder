package com.pepper.SpringFxCheckBox.Controller;


import com.pepper.SpringFxCheckBox.Gui.MessageBox;
import com.pepper.SpringFxCheckBox.Model.Database;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteQuery<T>
{
    // meg kell kapja a selectedColumns-t parameterben
    public <T> List<T> executeQuery(String query, Class<T> entityClass, List<String> selectedColumns, EntityHandler<T> entityHandler) 
    {
        List<T> queryResult = new ArrayList<>();
        try (Connection connection = Database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery())
        {
            // Process the result set
            queryResult = entityHandler.processResultSet(resultSet, selectedColumns);
           
        } catch (SQLSyntaxErrorException e) {
            e.printStackTrace();
            MessageBox.Show("Error","SQL syntax error",  e.getMessage()+"\nPlease check your query and try again.", 2);
        }
        catch (SQLException e) {
            
            e.printStackTrace();            
            MessageBox.Show("Error", "SQL error", e.getMessage());
        }
        return queryResult;
    }
}
