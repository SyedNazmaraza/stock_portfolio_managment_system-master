package com.springboot.evaluation_task.service;


import com.springboot.evaluation_task.config.JwtService;
import com.springboot.evaluation_task.entity.*;
import com.springboot.evaluation_task.exception.InvalidToken;
import com.springboot.evaluation_task.model.*;
import com.springboot.evaluation_task.repository.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final User1Repository user1Repository;
    private final TransactionRepo transactionRepo;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest request;



    @Override
    public String signIn(SignInRequest signInRequest) {
        User1 user = User1.builder()
                .userName(signInRequest.getUserName())
                .password(passwordEncoder.encode(signInRequest.getPassword()))
                .email(signInRequest.getEmail())
                .role(signInRequest.getRole())
                .build();
        user1Repository.save(user);
        var jwttoken =  jwtService.generateToken(user);
        return jwttoken;
    }
    @Override
    public String login(LoginRequest loginRequest) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        try {
            var userName = user1Repository.findByEmail(loginRequest.getEmail());
            var jwtToken = jwtService.generateToken(userName.get());
            return jwtToken;
        }
        catch (Exception e){
            throw new InvalidToken("Invalid Details");
        }
    }

    @Override
    public String buyPortfolio(PortfolioRequest portfolioRequest) {
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        else {
            throw new InvalidToken("Invalid token");
        }
        String email = jwtService.extractUsername(token);
        Integer id = user1Repository.findByEmail(email).get().getId();
        Portfolio p = Portfolio.builder()
                .userId(id)
                .quantity(portfolioRequest.getQuantity())
                .purchaseDate(portfolioRequest.getPurchaseDate())
                .purchasePrice(portfolioRequest.getPurchasePrice())
                .symbol(portfolioRequest.getSymbol())
                .build();
        portfolioRepository.save(p);
        Transactions transactions = Transactions.builder()
                .userId(id)
                .symbol(portfolioRequest.getSymbol())
                .transactionPrice(portfolioRequest.getPurchasePrice())
                .transactionType("Buy")
                .transactionDate(portfolioRequest.getPurchaseDate())
                .quantity(portfolioRequest.getQuantity())
                .build();
        transactionRepo.save(transactions);
        return "Success";


    }
    @Override
    public String sellPortfolio(PortfolioRequest portfolioRequest) {
        int quantity = portfolioRequest.getQuantity();
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        else {
            throw new InvalidToken("Invalid token");
        }
        String email = jwtService.extractUsername(token);
        Integer id = user1Repository.findByEmail(email).get().getId();
        List<Portfolio> list = portfolioRepository.findByUserIdAndSymbolOrderByPurchasePriceAsc(id,portfolioRequest.getSymbol());
        int totalQuantity = list.stream().mapToInt(Portfolio::getQuantity).sum();
        if(totalQuantity<quantity){
            throw new InvalidToken("LESS QUANTITY");
        }
        else {
            for (Portfolio i : list){
                if(i.getQuantity()<quantity){
                    portfolioRepository.delete(i);
                    quantity=quantity-i.getQuantity();
                } else if (i.getQuantity()==quantity) {
                    portfolioRepository.delete(i);
                    break;
                }else {
                    i.setQuantity(i.getQuantity()-quantity);
                    portfolioRepository.save(i);

                }
            }
            Transactions transactions = Transactions.builder()
                    .userId(id)
                    .symbol(portfolioRequest.getSymbol())
                    .transactionPrice(portfolioRequest.getPurchasePrice())
                    .transactionType("SELL")
                    .transactionDate(portfolioRequest.getPurchaseDate())
                    .quantity(portfolioRequest.getQuantity())
                    .build();
            transactionRepo.save(transactions);
            return "Success";
        }
    }

    @Override
    public List<Portfolio> getAllPortfolio() {
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        else {
            throw new InvalidToken("Invalid token");
        }
        return portfolioRepository.findByUserId(user1Repository.findByEmail(jwtService.extractUsername(token)).get().getId());

    }
    @Override
    public List<Transactions> getAllTransaction() {
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        else {
            throw new InvalidToken("Invalid token");
        }
        return transactionRepo.findByUserId(user1Repository.findByEmail(jwtService.extractUsername(token)).get().getId());
    }
    @Override
    public HashMap<String, TotalValuesResponse> findProfitOrLoss(int price) {
        HashMap<String, TotalValuesResponse> map = new HashMap<>();
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        else {
            throw new InvalidToken("Invalid token");
        }
        List<Portfolio> list = portfolioRepository.findByUserId(user1Repository.findByEmail(jwtService.extractUsername(token)).get().getId());
        for(Portfolio i : list){
            if(map.containsKey(i.getSymbol())){

                TotalValuesResponse totalValuesResponse = map.get(i.getSymbol());
                totalValuesResponse.setTotalQuantity(totalValuesResponse.getTotalQuantity()+i.getQuantity());
                totalValuesResponse.setTotalPrice((totalValuesResponse.getTotalPrice()+i.getPurchasePrice()));

                Caluclator c = calculateProfitOrLossAmount(price, totalValuesResponse.getTotalPrice(), totalValuesResponse.getTotalQuantity());
                totalValuesResponse.setPrlAmount(c.getAmount());
                totalValuesResponse.setProfitOrLoss(c.getPOrLoss());
                map.put(i.getSymbol(), totalValuesResponse);
            } else {
                Caluclator c = calculateProfitOrLossAmount( price,  i.getPurchasePrice(),i.getQuantity());
                TotalValuesResponse totalValuesResponse = new TotalValuesResponse( i.getQuantity(),  i.getPurchasePrice(),c.getPOrLoss(),c.getAmount());
                map.put(i.getSymbol(), totalValuesResponse);
            }
        }
        return map;
    }

    private Caluclator calculateProfitOrLossAmount(int price,int totalPrice,int totalQuantity) {
        Caluclator caluclator = new Caluclator();
        if(price*totalQuantity>totalPrice){
            caluclator.setPOrLoss("Profit");
            caluclator.setAmount(price*totalQuantity-totalPrice);
            return caluclator;
        }
        else if (price*totalQuantity<totalPrice){
            caluclator.setPOrLoss("Loss");
            caluclator.setAmount(price*totalQuantity-totalPrice);
            return caluclator;
        }
        else{
            caluclator.setPOrLoss("NoLossNoGain");
            caluclator.setAmount(price*totalQuantity-totalPrice);
            return caluclator;
        }
    }
}
