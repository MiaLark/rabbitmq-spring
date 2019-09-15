package com.wangwei.entity;

import com.rabbitmq.client.Channel;
import com.wangwei.config.MainConfig;
import com.wangwei.rabbitmqconsumer.RabbitmqConsumerApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * @author: wangwei
 * @date: 2019-08-28 16:07
 */
@ContextConfiguration(classes = MainConfig.class)
public class RabbitReceiverTest extends RabbitmqConsumerApplicationTests {

    @Autowired
    private RabbitReceiver rabbitReceiver;

    @Test
    public void onMessage() {
    }

    @Test
    public void onOrderMessage() {
    }
}