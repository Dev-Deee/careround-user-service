package com.dayo.careround.userservice.data.repositories;

import com.dayo.careround.userservice.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPublicId(UUID publicId);
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Doctor d WHERE d.licenseNumber = :licenseNumber")
    boolean doctorLicenseExists(@Param("licenseNumber") String licenseNumber);
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Nurse n WHERE n.licenseNumber = :licenseNumber")
    boolean nurseLicenseExists(@Param("licenseNumber") String licenseNumber);
}