package com.dayo.careround.userservice.utils;

import com.dayo.careround.userservice.data.models.Admin;
import com.dayo.careround.userservice.data.models.Doctor;
import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.dto.response.UserResponse;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        String role = switch (user) {
            case Doctor d -> "DOCTOR";
            case Nurse n -> "NURSE";
            case Admin a -> "ADMIN";
            default -> "UNKNOWN";
        };

        return new UserResponse(
                user.getPublicId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhotoUrl(),
                role
        );
    }
}