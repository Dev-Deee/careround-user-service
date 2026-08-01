package com.dayo.careround.userservice.dto.event;

import java.util.UUID;

public record RegistrationCancelledEvent(
        UUID userId
) {}