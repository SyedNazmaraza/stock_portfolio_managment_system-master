package com.springboot.evaluation_task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalValues {
    private int totalQuantity;
    private int totalPrice;
    private String profitOrLoss;
    private int prlAmount;
}
