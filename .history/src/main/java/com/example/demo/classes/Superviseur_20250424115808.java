package com.example.demo.classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("SUPERVISEUR")
public class Superviseur extends User {

    private String supervisorSpecificField;

    @Override
    public boolean canLogin() {
        return true; 
    }

    public String getSupervisorSpecificField() {
        return supervisorSpecificField;
    }

    public void setSupervisorSpecificField(String supervisorSpecificField) {
        this.supervisorSpecificField = supervisorSpecificField;
    }
}