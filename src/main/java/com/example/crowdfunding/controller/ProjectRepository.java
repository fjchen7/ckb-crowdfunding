package com.example.crowdfunding.controller;

import com.example.crowdfunding.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
