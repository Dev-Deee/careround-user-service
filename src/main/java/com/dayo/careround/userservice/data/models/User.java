package com.dayo.careround.userservice.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    private UUID id;   // same UUID as AuthUser.id — set explicitly, never auto-generated here

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String photoUrl;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;   // same publicId as AuthUser.publicId — copied, not regenerated
}