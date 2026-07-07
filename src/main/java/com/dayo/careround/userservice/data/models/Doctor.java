package com.dayo.careround.userservice.data.models;

import com.dayo.careround.userservice.data.models.enums.DoctorGrade;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "doctors")
public class Doctor extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorGrade grade;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false, unique = true)
    private String licenseNumber;
}
