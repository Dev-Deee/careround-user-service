package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.event.RegistrationCancelledEvent;
import com.dayo.careround.userservice.dto.event.UserRegisteredEvent;
import com.dayo.careround.userservice.utils.UserProfileFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserRepository userRepository;
    private final UserProfileFactory userProfileFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "user.registered", groupId = "user-service-group")
    public void handleUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);

            if (userRepository.existsById(event.userId())) {
                log.info("User {} already exists in user-service, skipping duplicate event", event.userId());
                return;
            }

            User user = userProfileFactory.create(event);
            userRepository.save(user);
            log.info("Created {} profile for userId {}", event.role(), event.userId());

        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", e.getMessage(), e);
            // Not re-throwing: a poison-pill event shouldn't block the consumer forever.
            // Worth a dead-letter topic later; logging is the stopgap for now.
        }
    }

    @KafkaListener(topics = "user.registration_cancelled", groupId = "user-service-group")
    public void handleRegistrationCancelled(String payload) {
        try {
            RegistrationCancelledEvent event = objectMapper.readValue(payload, RegistrationCancelledEvent.class);
            userRepository.deleteById(event.userId());
            log.info("Deleted profile row for cancelled registration: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to process user.registration_cancelled event: {}", e.getMessage(), e);
        }
    }
}