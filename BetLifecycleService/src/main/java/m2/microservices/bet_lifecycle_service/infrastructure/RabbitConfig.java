package m2.microservices.bet_lifecycle_service.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "betting.events.exchange";
    @Value("${spring.rabbitmq.queues.payment-updates}")
    private String paymentQueueName;

    @Value("${spring.rabbitmq.queues.game-updates}")
    private String gameQueueName;
    public static final String KEY_PAYMENT_UPDATE = "betting.payment.update";
    public static final String KEY_FUND_RESERVED = "wallet.transaction.reserved";
    public static final String KEY_GAME_UPDATE = "betting.game.update";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public TopicExchange bettingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(paymentQueueName, true);
    }

    @Bean
    public Queue gameQueue() {
        return new Queue(gameQueueName, true);
    }

    @Bean
    public Binding bindingPayment(Queue paymentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentQueue).to(exchange).with(KEY_PAYMENT_UPDATE);
    }

    @Bean
    public Binding bindingFundReserved(Queue paymentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentQueue).to(exchange).with(KEY_FUND_RESERVED);
    }

    @Bean
    public Binding bindingGame(Queue gameQueue, TopicExchange exchange) {
        return BindingBuilder.bind(gameQueue).to(exchange).with(KEY_GAME_UPDATE);
    }
}