package com.dayo.careround.userservice.grpc;

import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.models.enums.NurseGrade;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class NurseSeniorityGrpcService extends NurseSeniorityServiceGrpc.NurseSeniorityServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getMostSeniorNurse(SeniorityRequest request, StreamObserver<SeniorityResponse> responseObserver) {
        List<NurseCandidate> candidates = request.getCandidatesList();

        List<UUID> nurseIds = candidates.stream()
                .map(c -> UUID.fromString(c.getNurseId()))
                .toList();

        List<User> users = userRepository.findAllById(nurseIds);

        UUID winner = candidates.stream()
                .max(Comparator
                        .<NurseCandidate, Integer>comparing(c -> gradeOf(users, c).map(NurseGrade::ordinal).orElse(-1))
                        .thenComparing(Comparator.comparingLong(NurseCandidate::getClockInEpochSeconds).reversed()))
                .map(c -> UUID.fromString(c.getNurseId()))
                .orElseThrow(() -> new RuntimeException("No matching nurses found"));

        SeniorityResponse response = SeniorityResponse.newBuilder()
                .setNurseId(winner.toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private java.util.Optional<NurseGrade> gradeOf(List<User> users, NurseCandidate candidate) {
        return users.stream()
                .filter(u -> u.getId().equals(UUID.fromString(candidate.getNurseId())))
                .filter(u -> u instanceof Nurse)
                .map(u -> ((Nurse) u).getGrade())
                .findFirst();
    }
}