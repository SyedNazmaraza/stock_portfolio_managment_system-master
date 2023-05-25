package com.springboot.evaluation_task.repository;

import com.springboot.evaluation_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
