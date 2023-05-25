package com.springboot.evaluation_task.model;

import lombok.Data;
@Data
public class Buy {
    private String token;
    private String symbol;
    private double purchasePrice;
    private String purchaseDate;
    private Integer quantity;
}
