package com.pepper.SpringFxCheckBox.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

 @Entity
 @Table(name = "db__income")
public class Income 
{
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    
    @Column(name = "partner")
    private int partner;

    private int amount;
    private String project;
    private LocalDate created;
    private LocalDate approved;
       
   

    public Income(){}
  
    public int getId() {
        return id;
    }    
    public Integer getPartnerId() {
        return partner;
    }
    public int getAmount() {
        return amount;
    }
    public String getProject() {
        return project;
    }
    public LocalDate getCreated() {
        return created;
    }
    public LocalDate getApproved() {
        return approved;
    }
    
    
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public void setCreated(LocalDate created) {
        this.created = created;
    }
    public void setApproved(LocalDate approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Income{" + "id=" + id + ", partner=" + partner + ", amount=" + amount + ", project=" + project + ", created=" + created + ", approved=" + approved + '}';
    }
     
    
}
