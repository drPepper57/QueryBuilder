package com.pepper.SpringFxCheckBox.Model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PartnerRepository extends CrudRepository<Partner, Integer> {
    @Override
    Optional<Partner> findById(Integer id);

    @Override
    List<Partner> findAll();

    //@Query("SELECT p, COUNT(i) FROM Partner p LEFT JOIN Income i ON p.id = i.partnerId GROUP BY p")
   // List<Object[]> findPartnersWithProjectCounts();
}

/*
Filter:
SELECT * FROM Products WHERE price > 100;

Aggregate Functions:
SELECT COUNT(*) FROM Orders;

Subqueries:
SELECT * FROM Customers WHERE id IN (SELECT customer_id FROM Orders WHERE total_amount > 1000);

Limit/Offset:
SELECT * FROM Products LIMIT 10 OFFSET 20;

Conditional Statements:
SELECT name, CASE WHEN age < 18 THEN 'Minor' ELSE 'Adult' END AS age_category FROM Customers;

Advanced Options:
SELECT DISTINCT category FROM Products;

SELECT * FROM Customers GROUP BY country HAVING COUNT(*) > 10;

SELECT * FROM Orders UNION SELECT * FROM Archived_Orders;

SELECT * FROM Customers JOIN Orders ON Customers.id = Orders.customer_id;
*/