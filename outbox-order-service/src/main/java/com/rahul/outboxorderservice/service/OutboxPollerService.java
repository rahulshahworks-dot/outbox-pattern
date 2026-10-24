package com.rahul.outboxorderservice.service;

import com.rahul.outboxorderservice.entity.OutboxEvent;
import com.rahul.outboxorderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPollerService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_NAME = "order-events";

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        // Pessimistic lock ke sath uthao
        List<OutboxEvent> pendingEvents = outboxRepository.findPendingEventsWithLock("PENDING");

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                // Broker acknowledgment ka wait karo (Safe delivery)
                kafkaTemplate.send(TOPIC_NAME, event.getAggregateId(), event.getPayload())
                        .get(3, TimeUnit.SECONDS);

                event.setStatus("PROCESSED");
                outboxRepository.save(event);
                log.info("Successfully pushed and confirmed event: {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to push event {}: {}", event.getId(), e.getMessage());
            }
        }
    }
}