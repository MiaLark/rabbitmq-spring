package com.wangwei.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangwei.entity.Order;
import com.wangwei.entity.Packaged;
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

import java.nio.file.Files;
import java.nio.file.Paths;
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

    //回调函数: confirm确认
    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.err.println("correlationData: " + correlationData);
        System.err.println("ack: " + ack);
        if(!ack){
            System.err.println("异常处理....");
        }
    };

    //回调函数: return返回
    private final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> System.err.println("return exchange: " + exchange + ", routingKey: "
            + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);

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

    @Test
    public void testSendJsonMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);

        rabbitTemplate.send("repayExchange", "repay.order", message);
    }

    @Test
    public void testSendJavaMessage() throws Exception {

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.getHeaders().put("__TypeId__", "com.wangwei.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("repayExchange", "repay.order", message);
    }

    @Test
    public void testSendMappingMessage() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");

        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("repayExchange", "repay.order", message1);


        Packaged pack = new Packaged();
        pack.setId("002");
        pack.setName("包裹消息");
        pack.setDescription("包裹描述信息");

        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("repayExchange", "repay.pack", message2);
    }

    @Test
    public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "image_queue", message);

        byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "pdf_queue", message);
    }
}