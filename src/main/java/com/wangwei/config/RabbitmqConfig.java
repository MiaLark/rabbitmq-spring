package com.wangwei.config;

import com.wangwei.adapter.MessageDelegate;
import com.wangwei.convert.ImageMessageConverter;
import com.wangwei.convert.PDFMessageConverter;
import com.wangwei.convert.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: wangwei
 * @date: 2019-08-25 15:35
 */
@Configuration
@ComponentScan(basePackages = {"com.wangwei"})
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("39.107.234.188");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public FanoutExchange creditExchange() {
        return new FanoutExchange("creditExchange", true, false);
    }

    @Bean
    public Queue creditQueue() {
        return new Queue("creditQueue", true);
    }

    @Bean
    public Binding creditBinding() {
        return BindingBuilder.bind(creditQueue()).to(creditExchange());
    }

    @Bean
    public TopicExchange repayExchange() {
        return new TopicExchange("repayExchange", true, false);
    }

    @Bean
    public Queue repayQueue() {
        return new Queue("repayQueue", true);
    }

    @Bean
    public Binding repayBinding() {
        return BindingBuilder.bind(repayQueue()).to(repayExchange()).with("repay.#");
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(creditQueue(), repayQueue());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());

/*        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            String body = new String(message.getBody());
            System.err.println("------------消费者: " + body);
        });*/

        /**
         * 1 适配器方式. 默认是有自己的方法名字的：handleMessage
         // 可以自己指定一个方法的名字: consumeMessage
         // 也可以添加一个转换器: 从字节数组转换为String */
/*         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");
         adapter.setMessageConverter(new TextMessageConverter());
         container.setMessageListener(adapter);*/


        /**
         * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
         * */
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setMessageConverter(new TextMessageConverter());
         Map<String, String> queueOrTagToMethodName = new HashMap<>();
         queueOrTagToMethodName.put("creditQueue", "method1");
         queueOrTagToMethodName.put("repayQueue", "method2");
         adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
         container.setMessageListener(adapter);


        // 1.1 支持json格式的转换器
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         adapter.setMessageConverter(jackson2JsonMessageConverter);

         container.setMessageListener(adapter);
         */



        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");
         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

         Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
         idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
         idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);

         javaTypeMapper.setIdClassMapping(idClassMapping);

         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */

        //1.4 ext convert

/*        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局的转换器:
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        convert.addDelegate("json", jsonConvert);
        convert.addDelegate("application/json", jsonConvert);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        convert.addDelegate("image/png", imageConverter);
        convert.addDelegate("image", imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        convert.addDelegate("application/pdf", pdfConverter);


        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);*/
        return container;
    }
}
