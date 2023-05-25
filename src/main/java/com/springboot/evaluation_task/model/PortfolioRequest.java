package com.springboot.evaluation_task.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class PortfolioRequest {

    @NotBlank(message = "Token Invalid")
    private String token;
    @NotBlank(message = "symbol Invalid")
    private String symbol;
    @Positive(message = "PurchasePrice Should Not Be Negative")
    private int purchasePrice;
    @NotBlank(message = "purchaseDate Invalid")
    private String purchaseDate;
    @Min(value = 1 ,message = "Quantity Should Not Be Less Than Zero")
    private int quantity;
}
