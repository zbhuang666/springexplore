package com.hx.zbhuang.service;

import com.hx.zbhuang.annotion.Autowired;
import com.hx.zbhuang.annotion.Component;
import com.hx.zbhuang.annotion.Scope;

@Component("orderService")
@Scope("singleton")
public class OrderService {
    @Autowired
    PaymentService paymentService;
    public void test(){
        System.out.println("orderService"+"====="+paymentService.test());
    }
}
