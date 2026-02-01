package m2.microservices.WalletService.infrastructure;

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

    // Queues
    public static final String Q_ACCOUNT_EVENTS = "wallet.account.events.queue";
    public static final String Q_BET_PLACED = "wallet.bet.placed.queue";
    public static final String Q_BET_SETTLED = "wallet.bet.settled.queue";

    // Routing Keys (To bind)
    public static final String KEY_ACCOUNT_REGISTERED = "account.registered";
    public static final String KEY_BET_PLACED = "betting.lifecycle.placed";   // Matches Bet Service
    public static final String KEY_BET_SETTLED = "betting.settlement.settled"; // Matches Settlement Service

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // --- QUEUE DEFINITIONS ---
    @Bean public Queue accountQueue() { return new Queue(Q_ACCOUNT_EVENTS, true); }
    @Bean public Queue betPlacedQueue() { return new Queue(Q_BET_PLACED, true); }
    @Bean public Queue betSettledQueue() { return new Queue(Q_BET_SETTLED, true); }

    // --- EXCHANGE ---
    @Bean public TopicExchange exchange() { return new TopicExchange(EXCHANGE_NAME); }

    // --- BINDINGS ---
    @Bean
    public Binding bindAccount(Queue accountQueue, TopicExchange exchange) {
        return BindingBuilder.bind(accountQueue).to(exchange).with(KEY_ACCOUNT_REGISTERED);
    }

    @Bean
    public Binding bindBetPlaced(Queue betPlacedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(betPlacedQueue).to(exchange).with(KEY_BET_PLACED);
    }

    // ... binding for settled
}