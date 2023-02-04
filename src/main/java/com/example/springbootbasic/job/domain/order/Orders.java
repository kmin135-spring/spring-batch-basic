package com.example.springbootbasic.job.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@ToString
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderItem;
    private Integer price;
    private Date orderDate;
}
