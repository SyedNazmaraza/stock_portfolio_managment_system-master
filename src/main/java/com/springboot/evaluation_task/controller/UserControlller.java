package com.springboot.evaluation_task.controller;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.entity.User;
import com.springboot.evaluation_task.model.*;
import com.springboot.evaluation_task.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserControlller {

    private  final UserService service;

    @PostMapping("/login")
    public String login(@RequestBody User user){
        return service.login(user);
    }
    @PostMapping("/portfolio/buy")
    public String buyStocks(@RequestBody Buy buy){
        return service.buyPortfolio(buy);
    }
    @PostMapping("/portfolio/sell")
    public String sellStocks(@RequestBody Sell sell){
        return service.sellPortfolio(sell);
    }
    @GetMapping("/portfolio")
    public List<Portfolio> getAllPortfolio(@RequestBody Buy buy){
        return service.getAllPortfolio(buy);
    }
    @GetMapping("/transaction/list")
    public  List<Transactions> getAllTransaction(@RequestBody SourceToken token) {
        return service.getAllTransaction(token);
    }
    @GetMapping("/portfolio/pl/{price}")
    public HashMap<String, TotalValues> findingPl(@RequestBody SourceToken token, @PathVariable("price") Integer price){
        return service.findProfitOrLoss(token,price);
    }




}
