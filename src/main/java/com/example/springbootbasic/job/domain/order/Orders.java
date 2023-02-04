package com.example.springbootbasic.job.domain.order;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderItem;
    private Integer price;
    private Date orderDate;
}
