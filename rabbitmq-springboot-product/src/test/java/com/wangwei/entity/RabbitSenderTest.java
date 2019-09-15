package com.wangwei.entity;

import com.wangwei.config.MainConfig;
import com.wangwei.rabbitmqspringbootproduct.RabbitmqSpringbootProductApplicationTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author: wangwei
 * @date: 2019-08-27 10:22
 */

@ContextConfiguration(classes = MainConfig.class)
public class RabbitSenderTest extends RabbitmqSpringbootProductApplicationTests{

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void send() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("send_time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        rabbitSender.send("Hello RabbitMQ For Spring Boot!", properties);
    }

    @Test
    public void send2() throws Exception {
        Order order = new Order("1", "测试");
        rabbitSender.sendOrder(order);
    }
}