package com.dayo.careround.userservice.data.models;

import com.dayo.careround.userservice.data.models.enums.NurseGrade;
import com.dayo.careround.userservice.data.models.enums.Shift;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "nurses")

public class Nurse extends User {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NurseGrade grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Shift shift;

    @Column
    private UUID assignedWardId;
}
