package com.wangwei.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;


/**
 * @author: wangwei
 * @date: 2019-08-25 15:48
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RabbitmqConfig.class)
public class RabbitmqConfigTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void rabbitAdmin() {
        rabbitAdmin.declareExchange(new DirectExchange("spring.direct", false, false));

        rabbitAdmin.declareExchange(new TopicExchange("spring.topic", false, false));

        rabbitAdmin.declareExchange(new FanoutExchange("spring.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("spring.direct", false));
        rabbitAdmin.declareQueue(new Queue("spring.topic", false));
        rabbitAdmin.declareQueue(new Queue("spring.fanout", false));

        rabbitAdmin.declareBinding(new Binding("spring.direct"
                , Binding.DestinationType.QUEUE
                , "spring.direct"
                , "direct", new HashMap<>()));

//        rabbitAdmin.declareBinding(BindingBuilder
//                .bind(new Queue("spring.topic.queue", false))
//                .to(new TopicExchange("spring.topic.exchange", false, false))
//                .with("spirng.#"));

        rabbitAdmin.purgeQueue("spring.direct", false);

    }

    @Test
    public void testSendMessage() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述..");
        messageProperties.getHeaders().put("type", "自定义消息类型..");
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

        rabbitTemplate.convertAndSend("creditExchange", "",  message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.err.println("------添加额外的设置---------");
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
                message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
                return message;
            }
        });
    }

    @Test
    public void testSendMessage2() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType("text/plain");
//        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);

        rabbitTemplate.send("creditExchange", null, message);
        rabbitTemplate.send("repayExchange", "repay.text", message);

//        rabbitTemplate.convertAndSend("repayExchange", "repay.amqp", "hello object message send!".getBytes());
    }
}