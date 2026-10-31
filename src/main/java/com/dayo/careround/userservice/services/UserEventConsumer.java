package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.event.RegistrationCancelledEvent;
import com.dayo.careround.userservice.dto.event.UserRegisteredEvent;
import com.dayo.careround.userservice.utils.UserProfileFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserRepository userRepository;
    private final UserProfileFactory userProfileFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Failures propagate to KafkaErrorHandlingConfig: transient ones are retried, unparseable events (bad JSON,
    // unknown role or grade) go straight to user.registered-dlt. Never swallowed, or the account exists in auth
    // without a profile here.
    @KafkaListener(topics = "user.registered", groupId = "user-service-group")
    public void handleUserRegistered(ConsumerRecord<String, String> record) throws JsonProcessingException {
        try {
            UserRegisteredEvent event = objectMapper.readValue(record.value(), UserRegisteredEvent.class);

            if (userRepository.existsById(event.userId())) {
                log.info("User {} already exists in user-service, skipping duplicate event", event.userId());
                return;
            }

            User user = userProfileFactory.create(event);
            userRepository.save(user);
            log.info("Created {} profile for userId {}", event.role(), event.userId());
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("Failed to process user.registered key={}: {}", record.key(), e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "user.registration_cancelled", groupId = "user-service-group")
    public void handleRegistrationCancelled(ConsumerRecord<String, String> record) throws JsonProcessingException {
        try {
            RegistrationCancelledEvent event = objectMapper.readValue(record.value(), RegistrationCancelledEvent.class);
            userRepository.deleteById(event.userId());
            log.info("Deleted profile row for cancelled registration: {}", event.userId());
        } catch (JsonProcessingException | RuntimeException e) {
            log.error("Failed to process user.registration_cancelled key={}: {}", record.key(), e.getMessage(), e);
            throw e;
        }
    }
}