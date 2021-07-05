package com.hx.zbhuang.mianEntrance;

import com.hx.zbhuang.service.OrderService;
import com.hx.zbhuang.spring.config.AppConfig;
import com.hx.zbhuang.spring.DouFuDanApplicationContext;

public class explore {
    public static void main(String[] args) {
        DouFuDanApplicationContext applicationContext = new DouFuDanApplicationContext(AppConfig.class);
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        orderService.test();
    }
}
