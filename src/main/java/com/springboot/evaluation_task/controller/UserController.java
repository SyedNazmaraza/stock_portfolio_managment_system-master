package com.springboot.evaluation_task.controller;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.model.*;
import com.springboot.evaluation_task.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private  final UserService service;

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok().body(service.signIn(signInRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest){
        java.net.URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/login").toUriString());
        return ResponseEntity.created(uri).body(service.login(loginRequest));
    }
    @PostMapping("/portfolio/buy")
    public ResponseEntity<String> buyStocks(@RequestBody @Valid PortfolioRequest portfolioRequest){
        return ResponseEntity.accepted().body(service.buyPortfolio(portfolioRequest));
    }
    @PostMapping("/portfolio/sell")
    public ResponseEntity<String> sellStocks(@RequestBody @Valid PortfolioRequest portfolioRequest){
        return ResponseEntity.accepted().body(service.sellPortfolio(portfolioRequest));

    }
    @GetMapping("/portfolio")
    public ResponseEntity<List<Portfolio>> getAllPortfolio(){
        return ResponseEntity.ok().body(service.getAllPortfolio());
    }
    @GetMapping("/transaction/list")
    public  ResponseEntity<List<Transactions>> getAllTransaction() {
        return  ResponseEntity.ok().body(service.getAllTransaction());
    }
    @GetMapping("/portfolio/pl/{price}")
    public ResponseEntity<HashMap<String, TotalValuesResponse>> findingPl(@PathVariable("price") Integer price){
        return ResponseEntity.ok().body(service.findProfitOrLoss(price));
    }




}
