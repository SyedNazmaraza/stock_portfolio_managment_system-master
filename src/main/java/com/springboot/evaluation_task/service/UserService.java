package com.springboot.evaluation_task.service;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.entity.User;
import com.springboot.evaluation_task.model.*;

import java.util.HashMap;
import java.util.List;
public interface UserService {
    String login(User user);
    String buyPortfolio(Buy buy);
    List<Portfolio> getAllPortfolio(Buy buy);
    String sellPortfolio(Sell sell);
    List<Transactions> getAllTransaction(SourceToken token);
    HashMap<String, TotalValues> findProfitOrLoss(SourceToken token, Integer price);
}
