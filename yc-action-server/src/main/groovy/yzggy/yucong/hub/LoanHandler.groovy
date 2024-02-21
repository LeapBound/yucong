package yzggy.yucong.hub

import org.camunda.bpm.client.spring.SpringTopicSubscription
import org.camunda.bpm.client.spring.event.SubscriptionInitializedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

/**
 *
 * @author yamath
 * @since 2024/1/26 16:41
 */
@Configuration
class LoanHandler {

    static Logger log = LoggerFactory.getLogger(LoanHandler.class);

    @EventListener(SubscriptionInitializedEvent.class)
    def catchSubscriptionInitEvent(SubscriptionInitializedEvent event) {
        SpringTopicSubscription topicSubscription = event.getSource();
        log.info("Subscription with topic  {}", topicSubscription.getTopicName());
    }
}