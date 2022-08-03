package com.example.crowdfunding.controller;

import com.example.crowdfunding.model.Backer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BackerRepository extends JpaRepository<Backer, Long> {
    @Query("SELECT b FROM Backer b WHERE b.projectId = ?1")
    List<Backer> findAllByProjectId(Long projectId);
}
