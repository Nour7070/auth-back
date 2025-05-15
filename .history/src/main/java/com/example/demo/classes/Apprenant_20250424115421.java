package com.example.demo.classes;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("APPRENANT")
public class Apprenant extends User {

    @NotNull
    @Column(name = "niveau_etude")
    private String niveauEtude;

    // @ElementCollection
    @CollectionTable(name = "apprenant_interet", joinColumns = @JoinColumn(name = "apprenant_id"))
    @Column(name = "interet")
    private String interets;

    @Override
    public boolean canLogin() {
        return true; 
    }
}
