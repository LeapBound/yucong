package yzggy.yucong.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yzggy.yucong.consts.MqConsts;

@Configuration
public class DirectRabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queueMessage() {
        return new Queue(MqConsts.MQ_CHAT_MESSAGE);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(MqConsts.MQ_DEFAULT_DIRECT_EXCHANGE);
    }

    @Bean
    Binding bindingExchangeOne(Queue queueMessage, DirectExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).with(MqConsts.MQ_CHAT_MESSAGE_KEY);
    }

}
