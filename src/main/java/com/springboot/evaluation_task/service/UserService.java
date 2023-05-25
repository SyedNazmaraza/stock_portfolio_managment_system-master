package com.springboot.evaluation_task.service;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.model.*;

import java.util.HashMap;
import java.util.List;
public interface UserService {
    String login(UserRequest userRequest);
    String buyPortfolio(PortfolioRequest portfolioRequest);
    List<Portfolio> getAllPortfolio(PortfolioRequest portfolioRequest);
    String sellPortfolio(PortfolioRequest PortfolioRequest);
    List<Transactions> getAllTransaction(SourceTokenRequest token);
    HashMap<String, TotalValuesResponse> findProfitOrLoss(SourceTokenRequest token, int price);
}
