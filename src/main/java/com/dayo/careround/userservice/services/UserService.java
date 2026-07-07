package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.response.UserResponse;
import com.dayo.careround.userservice.exception.UserNotFoundException;
import com.dayo.careround.userservice.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getByPublicId(UUID publicId) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new UserNotFoundException("No user with that public id"));
        return UserMapper.toUserResponse(user);
    }
}