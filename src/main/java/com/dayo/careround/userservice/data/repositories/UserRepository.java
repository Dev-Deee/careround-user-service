package com.dayo.careround.userservice.data.repositories;

import com.dayo.careround.userservice.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPublicId(UUID publicId);

    @Query("select u from User u where (lower(u.firstName) like :prefix or lower(u.lastName) like :prefix or lower(concat(u.firstName, ' ', u.lastName)) like :prefix)")
    Page<User> searchAll(@Param("prefix") String prefix, Pageable pageable);

    @Query("select u from Doctor u where (lower(u.firstName) like :prefix or lower(u.lastName) like :prefix or lower(concat(u.firstName, ' ', u.lastName)) like :prefix)")
    Page<User> searchDoctors(@Param("prefix") String prefix, Pageable pageable);

    @Query("select u from Nurse u where (lower(u.firstName) like :prefix or lower(u.lastName) like :prefix or lower(concat(u.firstName, ' ', u.lastName)) like :prefix)")
    Page<User> searchNurses(@Param("prefix") String prefix, Pageable pageable);
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Doctor d WHERE d.licenseNumber = :licenseNumber")
    boolean doctorLicenseExists(@Param("licenseNumber") String licenseNumber);
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Nurse n WHERE n.licenseNumber = :licenseNumber")
    boolean nurseLicenseExists(@Param("licenseNumber") String licenseNumber);
}