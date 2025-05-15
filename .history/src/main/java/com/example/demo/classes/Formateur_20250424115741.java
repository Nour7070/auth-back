package com.example.demo.classes;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Formateur extends User {

    @ElementCollection
    @CollectionTable(name = "formateur_certificat", joinColumns = @JoinColumn(name = "formateur_id"))
    @Column(name = "certificat")
    private List<String> certificats;

    @ElementCollection
    @CollectionTable(name = "formateur_experience", joinColumns = @JoinColumn(name = "formateur_id"))
    @Column(name = "experience")
    private List<String> experiences;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Override
    public boolean canLogin() {
        return status == UserStatus.APPROVED;
    }

    public List<String> getCertificats() {
        return certificats;
    }

    public void setCertificats(List<String> certificats) {
        this.certificats = certificats;
    }

    public List<String> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<String> experiences) {
        this.experiences = experiences;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
