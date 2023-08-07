package com.pepper.SpringFxCheckBox.Controller;

import com.pepper.SpringFxCheckBox.Model.Database;
import com.pepper.SpringFxCheckBox.Model.EntityHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteQuery<T>
{   // meg kell kapja a selectedColumns-t parameterben
    public <T> List<T> executeQuery(String query, Class<T> entityClass) 
    {
        List<T> queryResult = new ArrayList<>();
        try (Connection connection = Database.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) 
        {
            EntityHandler<T> entityHandler = new EntityHandler<>(entityClass);
            // Process the result set
            queryResult = entityHandler.processResultSet(resultSet);
            /*for (T entity : queryResult) {
                System.out.println(" executeQuery method");
            System.out.println(entity.toString());
            // Do other processing with the entities
            }*/
        } catch (SQLException e) {
            
            e.printStackTrace();
            System.out.println("An error occurred executing query: " + e.getMessage());
        }
        return queryResult;
    }
}
