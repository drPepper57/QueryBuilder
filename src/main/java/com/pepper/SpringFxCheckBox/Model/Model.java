package com.pepper.SpringFxCheckBox.Model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Model<T>
{
    @Autowired
    private IncomeRepository IncomeRepo;
    @Autowired
    private PartnerRepository PartnerRepo;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void save(Income income)
    {
        IncomeRepo.save(income);
    }
    
    public List<Income> findTop25()
    {
        return IncomeRepo.findTop25ByOrderByCreatedDesc();
        //SELECT *FROM your_table ORDER BY created DESC LIMIT 25;
    }
    public List<String> getColumnNames(String tableName) 
    {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = LOWER(?) AND UPPER(COLUMN_NAME) NOT IN ('PARTNER_ID') ORDER BY ORDINAL_POSITION";
        return jdbcTemplate.queryForList(query, new Object[] { tableName }, String.class);

    }
    
    public List<String> getTableNames(JdbcTemplate jdbcTemplate, String databaseName) {
    String query = "SELECT table_name FROM information_schema.tables "
                 + "WHERE table_schema = ? AND table_type = 'BASE TABLE'";
    return jdbcTemplate.queryForList(query, new Object[]{databaseName}, String.class);
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    

    
    
    

}
