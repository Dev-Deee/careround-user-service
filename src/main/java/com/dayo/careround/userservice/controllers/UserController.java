package com.dayo.careround.userservice.controllers;

import com.dayo.careround.userservice.dto.response.UserResponse;
import com.dayo.careround.userservice.exception.BadRequestException;
import com.dayo.careround.userservice.services.DirectoryService;
import com.dayo.careround.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DirectoryService directoryService;

    // Staff directory. ?ids= (internal user ids = JWT sub, max 100) returns the found entries as a list; otherwise a
    // paged name/role search (?role=DOCTOR|NURSE&q=<name prefix>&page&size). Any authenticated user; no email/phone.
    @GetMapping("/directory")
    public ResponseEntity<?> directory(@RequestParam(required = false) List<UUID> ids,
                                       @RequestParam(required = false) String role,
                                       @RequestParam(required = false) String q,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        if (ids != null) {
            if (role != null || q != null) {
                throw new BadRequestException("Use either ids or role/q, not both");
            }
            return ResponseEntity.ok(directoryService.byIds(ids));
        }
        return ResponseEntity.ok(directoryService.search(role, q, page, size));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID publicId) {
        return ResponseEntity.ok(userService.getByPublicId(publicId));
    }
}