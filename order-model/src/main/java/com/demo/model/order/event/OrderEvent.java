package com.demo.model.order.event;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderEvent implements Serializable {

    private Long customerId;
    private Long productId;
}
