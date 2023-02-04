package com.example.springbootbasic.job.domain.account;

import com.example.springbootbasic.job.domain.order.Orders;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@ToString
@Entity
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderItem;
    private Integer price;
    private Date orderDate;
    private Date accountDate;

    public static Accounts of(Orders item) {
        Accounts a = new Accounts();
        a.id = item.getId();
        a.orderItem = item.getOrderItem();
        a.price = item.getPrice();
        a.orderDate = item.getOrderDate();
        a.accountDate = new Date();
        return a;
    }
}
