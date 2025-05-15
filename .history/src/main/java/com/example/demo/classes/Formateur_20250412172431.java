package com.example.demo.classes;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
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
@DiscriminatorValue("FORMATEUR")
public class Formateur extends User { // Should extend Utilisateur, not User

  ***

    @ElementCollection
    @CollectionTable(name = "formateur_certificat", joinColumns = @JoinColumn(name = "formateur_id"))
    @Column(name = "certificat")
    private List<String> certificats; // Simple list of certificates

    @ElementCollection
    @CollectionTable(name = "formateur_experience", joinColumns = @JoinColumn(name = "formateur_id"))
    @Column(name = "experience")
    private List<String> experiences; // Simple list of experiences

}
