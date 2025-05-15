package com.example.demo.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("MODERATEUR")
public class Moderateur extends User {
    @Column(name = "permissions")
    private String permissions; // Ex: "VALIDER_CANDIDATURES, GÉRER_UTILISATEURS"
}
