package com.pepper.SpringFxCheckBox.Model;

import jakarta.persistence.Column;
import java.time.LocalDate;


public class JoinEntity 
{
    private int id;
    private int partner;
    private int amount;
    private String project;
    private LocalDate created;
    private LocalDate approved;
    
    @Column(name = "id")
    private int partner_id;
    private  String name;
    private String contact;
   
    public JoinEntity(){}

    public int getIncome_id() {
        return id;
    }

    public int getIncome_partner() {
        return partner;
    }

    public int getIncome_amount() {
        return amount;
    }

    public String getIncome_project() {
        return project;
    }

    public LocalDate getIncome_created() {
        return created;
    }

    public LocalDate getIncome_approved() {
        return approved;
    }

    public int getPartner_id() {
        return partner_id;
    }

    public String getPartner_name() {
        return name;
    }

    public String getPartner_contact() {
        return contact;
    }

    @Override
    public String toString() {
        return "JoinEntity{" + "i_id=" + id + ", i_partner=" + partner + ", i_amount=" + amount + ", i_project=" + project + ", i_created=" + created + ", i_approved=" + approved + ", p_id=" + partner_id + ", p_name=" + name + ", p_contact=" + contact + '}';
    }
}
