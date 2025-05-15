package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.classes.Moderateur;

@Repository
public interface ModerateurRepository extends JpaRepository<Moderateur, Long> {
}
