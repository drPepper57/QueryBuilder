package com.pepper.SpringFxCheckBox.Model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PartnerRepository extends CrudRepository<Partner, Integer>
{
    @Override
    Optional<Partner> findById(Integer id);
    @Override
    List<Partner> findAll();
    @Query("SELECT p, COUNT(i.project) FROM Partner p LEFT JOIN Income i ON p.id = i.partnerId GROUP BY p")
    List<Object[]> findPartnersWithProjectCounts();
}
