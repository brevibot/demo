package com.example.demo.db1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // Make sure this import is present

@Entity
@Table(name = "app_users") // This annotation fixes the SQL syntax error
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
