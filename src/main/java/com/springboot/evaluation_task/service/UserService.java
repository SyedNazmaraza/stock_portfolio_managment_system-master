package com.springboot.evaluation_task.service;

import com.springboot.evaluation_task.entity.Portfolio;
import com.springboot.evaluation_task.entity.Transactions;
import com.springboot.evaluation_task.model.*;

import java.util.HashMap;
import java.util.List;

public interface UserService {
    String signIn(SignInRequest signInRequest );
    String login(LoginRequest loginRequest);
    String buyPortfolio(PortfolioRequest portfolioRequest);
    List<Portfolio> getAllPortfolio();
    String sellPortfolio(PortfolioRequest PortfolioRequest);
    List<Transactions> getAllTransaction();
    HashMap<String, TotalValuesResponse> findProfitOrLoss(int price);
}
