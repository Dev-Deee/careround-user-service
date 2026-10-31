package com.dayo.careround.userservice.utils;

import com.dayo.careround.userservice.data.models.Admin;
import com.dayo.careround.userservice.data.models.Doctor;
import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.models.enums.DoctorGrade;
import com.dayo.careround.userservice.data.models.enums.NurseGrade;
import com.dayo.careround.userservice.dto.event.UserRegisteredEvent;
import org.springframework.stereotype.Component;

@Component
public class UserProfileFactory {

    public User create(UserRegisteredEvent event) {
        return switch (event.role()) {
            case "DOCTOR" -> buildDoctor(event);
            case "NURSE" -> buildNurse(event);
            case "ADMIN" -> buildAdmin(event);
            default -> throw new IllegalArgumentException("Unknown role: " + event.role());
        };
    }

    private Doctor buildDoctor(UserRegisteredEvent event) {
        Doctor doctor = new Doctor();
        applyCommonFields(doctor, event);
        doctor.setGrade(parseGrade(DoctorGrade.class, event));
        doctor.setSpecialization(event.specialization());
        doctor.setLicenseNumber(event.licenseNumber());
        return doctor;
    }

    private Nurse buildNurse(UserRegisteredEvent event) {
        Nurse nurse = new Nurse();
        applyCommonFields(nurse, event);
        nurse.setGrade(parseGrade(NurseGrade.class, event));
        nurse.setSpecialization(event.specialization()); // nullable — fine if null
        nurse.setLicenseNumber(event.licenseNumber());
        return nurse;
    }

    private Admin buildAdmin(UserRegisteredEvent event) {
        Admin admin = new Admin();
        applyCommonFields(admin, event);
        return admin;
    }

    private void applyCommonFields(User user, UserRegisteredEvent event) {
        user.setId(event.userId());
        user.setPublicId(event.publicId());
        user.setFirstName(event.firstName());
        user.setLastName(event.lastName());
    }

    // valueOf(null) would be a NullPointerException; both cases are a bad event, not a transient failure.
    private static <E extends Enum<E>> E parseGrade(Class<E> grades, UserRegisteredEvent event) {
        if (event.grade() == null) {
            throw new IllegalArgumentException("user.registered for " + event.role() + " " + event.userId() + " has no grade");
        }
        return Enum.valueOf(grades, event.grade()); // IllegalArgumentException for an unknown grade
    }
}
