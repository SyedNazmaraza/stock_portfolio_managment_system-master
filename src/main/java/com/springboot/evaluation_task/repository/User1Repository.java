package com.springboot.evaluation_task.repository;

import com.springboot.evaluation_task.entity.User1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface User1Repository extends JpaRepository<User1,Integer> {

    Optional<User1> findByEmail(String username);
}
