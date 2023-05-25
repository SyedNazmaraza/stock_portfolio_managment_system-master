package com.springboot.evaluation_task.repository;

import com.springboot.evaluation_task.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transactions,Integer> {
    List<Transactions> findByUserId(Integer id);
}
