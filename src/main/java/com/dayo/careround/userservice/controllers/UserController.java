package com.dayo.careround.userservice.controllers;

import com.dayo.careround.userservice.dto.response.UserResponse;
import com.dayo.careround.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{publicId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID publicId) {
        return ResponseEntity.ok(userService.getByPublicId(publicId));
    }
}