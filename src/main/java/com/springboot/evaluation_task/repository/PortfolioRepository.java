package com.springboot.evaluation_task.repository;

import com.springboot.evaluation_task.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio,Integer> {


    List<Portfolio> findByUserId(Integer id);


    List<Portfolio> findByUserIdAndSymbol(Integer id, String symbol);

    List<Portfolio> findByUserIdAndSymbolOrderByPurchasePriceAsc(Integer id, String symbol);
}
