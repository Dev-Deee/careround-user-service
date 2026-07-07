package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.Admin;
import com.dayo.careround.userservice.data.models.Doctor;
import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.models.enums.DoctorGrade;
import com.dayo.careround.userservice.data.models.enums.NurseGrade;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.event.UserRegisteredEvent;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "user.registered", groupId = "user-service-group")
    public void handleUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);

            if (userRepository.existsById(event.userId())) {
                log.info("User {} already exists in user-service, skipping duplicate event", event.userId());
                return;
            }

            User user = switch (event.role()) {
                case "DOCTOR" -> buildDoctor(event);
                case "NURSE" -> buildNurse(event);
                case "ADMIN" -> buildAdmin(event);
                default -> throw new IllegalArgumentException("Unknown role: " + event.role());
            };

            userRepository.save(user);
            log.info("Created {} profile for userId {}", event.role(), event.userId());

        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", e.getMessage(), e);
            // Not re-throwing: a poison-pill event shouldn't block the consumer forever.
            // Worth a dead-letter topic later; logging is the stopgap for now.
        }
    }

    private Doctor buildDoctor(UserRegisteredEvent event) {
        Doctor doctor = new Doctor();
        applyCommonFields(doctor, event);
        doctor.setGrade(DoctorGrade.valueOf(event.grade()));
        doctor.setSpecialization(event.specialization());
        doctor.setLicenseNumber(event.licenseNumber());
        return doctor;
    }

    private Nurse buildNurse(UserRegisteredEvent event) {
        Nurse nurse = new Nurse();
        applyCommonFields(nurse, event);
        nurse.setGrade(NurseGrade.valueOf(event.grade()));
        nurse.setSpecialization(event.specialization()); // nullable — fine if null
        nurse.setLicenseNumber(event.licenseNumber());
        return nurse;
    }

    private Admin buildAdmin(UserRegisteredEvent event) {
        Admin admin = new Admin();
        applyCommonFields(admin, event);
        return admin;
    }

    private void applyCommonFields(User user, UserRegisteredEvent event) {
        user.setId(event.userId());
        user.setPublicId(event.publicId());
        user.setFirstName(event.firstName());
        user.setLastName(event.lastName());
    }
}