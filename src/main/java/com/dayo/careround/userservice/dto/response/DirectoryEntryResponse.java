package com.dayo.careround.userservice.dto.response;

import java.util.UUID;

// Staff directory entry: turns the internal user id carried in tokens and events (createdBy, assignedNurseId,
// authorId, ...) into a name. No email or phone. Account status lives in auth-service and is not included.
public record DirectoryEntryResponse(
        UUID id,
        UUID publicId,
        String displayName,
        String role,
        String grade
) {}
