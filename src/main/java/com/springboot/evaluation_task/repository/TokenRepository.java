package com.springboot.evaluation_task.repository;

import com.springboot.evaluation_task.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Tokens,Integer> {
    Tokens findByToken(String token);
}
