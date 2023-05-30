package com.springboot.evaluation_task.model;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class PortfolioRequest {
    @NotBlank(message = "symbol Invalid")
    private String symbol;
    @Positive(message = "PurchasePrice Should Not Be Negative")
    private Integer purchasePrice;
    @NotBlank(message = "purchaseDate Invalid")
    private String purchaseDate;
    @Min(value = 1 ,message = "Quantity Should Not Be Less Than Zero")
    private Integer quantity;
}
