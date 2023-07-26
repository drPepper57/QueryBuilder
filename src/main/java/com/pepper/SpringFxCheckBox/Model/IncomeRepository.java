package com.pepper.SpringFxCheckBox.Model;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface IncomeRepository extends CrudRepository<Income, Integer>
{
    Income save(Income income);
    List<Income> findAllByOrderByCreated();
    List<Income> findTop25ByOrderByCreatedDesc();
    List<Income> findByApprovedIsNull();
    Income findFirstByOrderByCreatedDesc();
    @Query("SELECT i FROM Income i JOIN FETCH i.partner p WHERE i.created = (SELECT MAX(created) FROM Income)")
    Income findLatestIncomeWithPartnerDetails();
    //@Query("SELECT p, COUNT(i) FROM Partner p LEFT JOIN Income i ON p.id = i.partner GROUP BY p")
    //List<Object[]> findPartnerProjectCounts();
    
    Income findById(int id);
    
}
