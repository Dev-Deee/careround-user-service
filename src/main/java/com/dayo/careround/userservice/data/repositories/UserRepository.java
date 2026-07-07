package com.dayo.careround.userservice.data.repositories;

import com.dayo.careround.userservice.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPublicId(UUID publicId);
}