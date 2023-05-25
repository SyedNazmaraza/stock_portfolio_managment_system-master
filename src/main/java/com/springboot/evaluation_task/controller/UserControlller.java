package com.springboot.evaluation_task.controller;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.model.*;
import com.springboot.evaluation_task.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserControlller {

    private  final UserService service;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserRequest userRequest){
        return new ResponseEntity<>(service.login(userRequest), HttpStatus.CREATED);
    }
    @PostMapping("/portfolio/buy")
    public ResponseEntity<String> buyStocks(@RequestBody @Valid PortfolioRequest portfolioRequest){
        return new ResponseEntity<>(service.buyPortfolio(portfolioRequest), HttpStatus.CREATED);
    }
    @PostMapping("/portfolio/sell")
    public ResponseEntity<String> sellStocks(@RequestBody @Valid PortfolioRequest portfolioRequest){
        return  ResponseEntity.ok(service.sellPortfolio(portfolioRequest));

    }
    @GetMapping("/portfolio")
    public List<Portfolio> getAllPortfolio(@RequestBody @Valid PortfolioRequest portfolioRequest){
        return service.getAllPortfolio(portfolioRequest);
    }
    @GetMapping("/transaction/list")
    public  List<Transactions> getAllTransaction(@RequestBody @Valid SourceTokenRequest token) {
        return service.getAllTransaction(token);
    }
    @GetMapping("/portfolio/pl/{price}")
    public HashMap<String, TotalValuesResponse> findingPl(@RequestBody @Valid SourceTokenRequest token, @PathVariable("price") Integer price){
        return service.findProfitOrLoss(token,price);
    }




}
