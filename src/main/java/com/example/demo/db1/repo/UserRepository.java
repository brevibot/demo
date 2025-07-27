package com.example.demo.db1.repo;

import com.example.demo.db1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}