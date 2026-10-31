package com.dayo.careround.userservice.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

// Additive contract: fields this consumer does not know (e.g. activationToken, sent by old producers) are ignored,
// so removing or adding fields upstream never dead-letters a valid registration.
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRegisteredEvent(
        UUID userId,
        UUID publicId,
        String email,
        String role,
        String firstName,
        String lastName,
        String grade,
        String specialization,
        String licenseNumber
) {}