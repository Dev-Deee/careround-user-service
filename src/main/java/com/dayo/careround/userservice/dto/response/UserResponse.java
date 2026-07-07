package com.dayo.careround.userservice.dto.response;

import java.util.UUID;

public record UserResponse (
        UUID publicId,
        String firstName,
        String lastName,
        String photoUrl,
        String role

){}
