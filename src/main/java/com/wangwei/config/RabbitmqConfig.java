package com.wangwei.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: wangwei
 * @date: 2019-08-25 15:35
 */
@Configuration
@ComponentScan({"com.wangwei.*"})
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("39.107.234.188");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public FanoutExchange creditExchange(){
        return new FanoutExchange("creditExchange", true, false);
    }

    @Bean
    public Queue creditQueue(){
        return new Queue("creditQueue", true);
    }

    @Bean
    public Binding creditBinding(){
        return BindingBuilder.bind(creditQueue()).to(creditExchange());
    }

    @Bean
    public TopicExchange repayExchange(){
        return new TopicExchange("repayExchange", true, false);
    }

    @Bean
    public Queue repayQueue(){
        return new Queue("repayQueue", true);
    }

    @Bean
    public Binding repayBinding(){
        return BindingBuilder.bind(repayQueue()).to(repayExchange()).with("repay.#");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        return new RabbitTemplate(connectionFactory);
    }
}
