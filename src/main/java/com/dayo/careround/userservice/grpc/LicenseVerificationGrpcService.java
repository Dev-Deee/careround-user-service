package com.dayo.careround.userservice.grpc;

import com.dayo.careround.userservice.data.repositories.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class LicenseVerificationGrpcService extends LicenseVerificationServiceGrpc.LicenseVerificationServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void licenseNumberExists(LicenseCheckRequest request, StreamObserver<LicenseCheckResponse> responseObserver) {
        String licenseNumber = request.getLicenseNumber();

        boolean exists = userRepository.doctorLicenseExists(licenseNumber)
                || userRepository.nurseLicenseExists(licenseNumber);

        LicenseCheckResponse response = LicenseCheckResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}