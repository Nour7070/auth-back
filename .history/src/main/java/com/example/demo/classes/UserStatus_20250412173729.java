package com.example.demo.classes;

public enum UserStatus {
    PENDING,   
    APPROVED,   // Validé par le superviseur
    REJECTED,   // Rejeté par le superviseur
    ACTIVE      // Compte actif (optionnel)
}