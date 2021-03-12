package com.myseckill.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author HCW
 * @date 2021/1/29-10:38
 */
@Configuration
public class RabbitMqConfig {
    private static final String QUEUE = "seckillQueue";
  private static final String EXCHANGE = "seckillExchange";
  @Bean
  public Queue queue(){
   return new Queue(QUEUE);
  }
  @Bean
  public TopicExchange topicExchange(){
   return new TopicExchange(EXCHANGE);
  }
  @Bean
  public Binding binding01(){
   return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
  }
}
