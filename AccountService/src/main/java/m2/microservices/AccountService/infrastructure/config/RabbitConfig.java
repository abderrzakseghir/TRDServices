package m2.microservices.AccountService.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "betting.events.exchange";

    public static final String KEY_BET_PLACED = "betting.bet.placed";
    public static final String KEY_BET_SETTLED = "betting.bet.settled";

    public static final String Q_BET_PLACEMENT = "account.bet-placement.queue";
    public static final String Q_BET_SETTLEMENT = "account.bet-settlement.queue";

    @Bean
    public TopicExchange bettingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }


    @Bean
    public Queue placementQueue() {
        // Durable = true (If RabbitMQ restarts, keep the queue)
        return new Queue(Q_BET_PLACEMENT, true);
    }

    @Bean
    public Queue settlementQueue() {
        return new Queue(Q_BET_SETTLEMENT, true);
    }


    @Bean
    public Binding bindingPlacement(Queue placementQueue, TopicExchange bettingExchange) {
        return BindingBuilder.bind(placementQueue)
                .to(bettingExchange)
                .with(KEY_BET_PLACED);
    }

    @Bean
    public Binding bindingSettlement(Queue settlementQueue, TopicExchange bettingExchange) {
        return BindingBuilder.bind(settlementQueue)
                .to(bettingExchange)
                .with(KEY_BET_SETTLED);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        // Inject the configured ObjectMapper from JacksonConfig
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
