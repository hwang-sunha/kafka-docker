package com.example.catalogservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CatalogDto {
    private String productId;
    private String qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;

}
