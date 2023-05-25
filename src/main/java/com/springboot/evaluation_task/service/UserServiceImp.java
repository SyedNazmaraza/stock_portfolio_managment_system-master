package com.springboot.evaluation_task.service;


import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Tokens;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.entity.User;
import com.springboot.evaluation_task.exception.InvalidToken;
import com.springboot.evaluation_task.model.*;
import com.springboot.evaluation_task.repository.PortfolioRepository;
import com.springboot.evaluation_task.repository.TokenRepository;
import com.springboot.evaluation_task.repository.TransactionRepo;
import com.springboot.evaluation_task.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TransactionRepo transactionRepo;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public String login(UserRequest userRequest) {
        User user = User.builder()
                .userName(userRequest.getUserName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        Tokens t = Tokens.builder()
                .token(token)
                .userid(user.getId())
                .build();
        tokenRepository.save(t);
        return token;
    }

    @Override
    public String buyPortfolio(PortfolioRequest portfolioRequest) {
        try {
            int id = tokenRepository.findByToken(portfolioRequest.getToken()).getUserid();
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
        catch (Exception e){
            throw new InvalidToken("InvalidToken");
        }
    }
    @Override
    public String sellPortfolio(PortfolioRequest portfolioRequest) {
        int quantity = portfolioRequest.getQuantity();
        int id;
        try {
             id = tokenRepository.findByToken(portfolioRequest.getToken()).getUserid();
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
                        break;
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
        catch (Exception e){
            throw new InvalidToken("Invalid Token");
        }

    }

    @Override
    public List<Portfolio> getAllPortfolio(PortfolioRequest portfolioRequest) {
        int id = tokenRepository.findByToken(portfolioRequest.getToken()).getUserid();
        return portfolioRepository.findByUserId(id);
    }
    @Override
    public List<Transactions> getAllTransaction(SourceTokenRequest token) {
        int id = tokenRepository.findByToken(token.getToken()).getUserid();
        return transactionRepo.findByUserId(id);
    }
    @Override
    public HashMap<String, TotalValuesResponse> findProfitOrLoss(SourceTokenRequest token, int price) {
        HashMap<String, TotalValuesResponse> map = new HashMap<>();
        int id = tokenRepository.findByToken(token.getToken()).getUserid();
        List<Portfolio> list = portfolioRepository.findByUserId(id);
        for(Portfolio i : list){
            if(map.containsKey(i.getSymbol())){

                TotalValuesResponse totalValuesResponse = map.get(i.getSymbol());
                totalValuesResponse.setTotalQuantity(totalValuesResponse.getTotalQuantity()+i.getQuantity());
                totalValuesResponse.setTotalPrice((totalValuesResponse.getTotalPrice()+i.getPurchasePrice()));

                Caluclator c = caluclatePorLAmount(price, totalValuesResponse.getTotalPrice(), totalValuesResponse.getTotalQuantity());
                totalValuesResponse.setPrlAmount(c.getAmount());
                totalValuesResponse.setProfitOrLoss(c.getPOrLoss());
                map.put(i.getSymbol(), totalValuesResponse);
            } else {
                Caluclator c = caluclatePorLAmount( price,  i.getPurchasePrice(),i.getQuantity());
                TotalValuesResponse totalValuesResponse = new TotalValuesResponse( i.getQuantity(),  i.getPurchasePrice(),c.getPOrLoss(),c.getAmount());
                map.put(i.getSymbol(), totalValuesResponse);
            }
        }
        return map;
    }

    private Caluclator caluclatePorLAmount(int price,int totalPrice,int totalQuantity) {
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
