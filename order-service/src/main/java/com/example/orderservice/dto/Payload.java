package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    String order_id;
    String user_id;
    String product_id;
    int qty;
    int unit_price;
    int total_price;


}
