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
import java.util.stream.Collectors;

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
    public String login(User user) {
        String s = user.getPassword();
        user.setPassword(passwordEncoder.encode(s));
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
    public String buyPortfolio(Buy buy) {
        try {
            Integer id = tokenRepository.findByToken(buy.getToken()).getUserid();
            Portfolio p = Portfolio.builder()
                    .userId(id)
                    .quantity(buy.getQuantity())
                    .purchaseDate(buy.getPurchaseDate())
                    .purchasePrice(buy.getPurchasePrice())
                    .symbol(buy.getSymbol())
                    .build();
            portfolioRepository.save(p);
            Transactions transactions = Transactions.builder()
                    .userId(id)
                    .symbol(buy.getSymbol())
                    .transactionPrice(buy.getPurchasePrice())
                    .transactionType("Buy")
                    .transactionDate(buy.getPurchaseDate())
                    .quantity(buy.getQuantity())
                    .build();
            transactionRepo.save(transactions);
            return "Success";
        }
        catch (Exception e){
            throw new InvalidToken("InvalidToken");
        }
    }
    @Override
    public String sellPortfolio(Sell sell) {
        int quality = sell.getQuantity();
        Integer id;
        try {
             id = tokenRepository.findByToken(sell.getToken()).getUserid();
            log.info(id.toString());
            List<Portfolio> list = portfolioRepository.findByUserIdAndSymbolOrderByPurchasePriceAsc(id,sell.getSymbol());
            int totalQuantity = list.stream().collect(Collectors.summingInt(Portfolio::getQuantity));
            if(totalQuantity<quality){
                throw new InvalidToken("LESS QUANTITY");
            }
            else {
                for (Portfolio i : list){
                    if(i.getQuantity()<quality){
                        portfolioRepository.delete(i);
                        quality=quality-i.getQuantity();
                    } else if (i.getQuantity()==quality) {
                        portfolioRepository.delete(i);
                        break;
                    }else {
                        i.setQuantity(i.getQuantity()-quality);
                        portfolioRepository.save(i);
                        break;
                    }
                }
                Transactions transactions = Transactions.builder()
                        .userId(id)
                        .symbol(sell.getSymbol())
                        .transactionPrice(sell.getPurchasePrice())
                        .transactionType("SELL")
                        .transactionDate(sell.getPurchaseDate())
                        .quantity(sell.getQuantity())
                        .build();
                transactionRepo.save(transactions);
                return "Sucess";
            }
        }
        catch (NullPointerException e){
            throw new InvalidToken("Invalid Token");
        }

    }

    @Override
    public List<Portfolio> getAllPortfolio(Buy buy) {
        Integer id = tokenRepository.findByToken(buy.getToken()).getUserid();
        return portfolioRepository.findByUserId(id);
    }
    @Override
    public List<Transactions> getAllTransaction(SourceToken token) {
        Integer id = tokenRepository.findByToken(token.getToken()).getUserid();
        return transactionRepo.findByUserId(id);
    }
    @Override
    public HashMap<String, TotalValues> findProfitOrLoss(SourceToken token, Integer price) {
        HashMap<String, TotalValues> map = new HashMap<>();
        Integer id = tokenRepository.findByToken(token.getToken()).getUserid();
        List<Portfolio> list = portfolioRepository.findByUserId(id);
        for(Portfolio i : list){
            if(map.containsKey(i.getSymbol())){

                TotalValues totalValues = map.get(i.getSymbol());
                totalValues.setTotalQuantity(totalValues.getTotalQuantity()+i.getQuantity());
                totalValues.setTotalPrice((int) (totalValues.getTotalPrice()+i.getPurchasePrice()));

                Caluclator c = caluclatePorLAmount((Integer) price, totalValues.getTotalPrice(),totalValues.getTotalQuantity());
                totalValues.setPrlAmount(c.getAmount());
                totalValues.setProfitOrLoss(c.getPOrLoss());
                map.put(i.getSymbol(),totalValues);
            } else {
                Caluclator c = caluclatePorLAmount((Integer) price, (int) i.getPurchasePrice(),i.getQuantity());
                TotalValues totalValues = new TotalValues((Integer) i.getQuantity(), (int) i.getPurchasePrice(),c.getPOrLoss(),c.getAmount());
                map.put(i.getSymbol(),totalValues);
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
