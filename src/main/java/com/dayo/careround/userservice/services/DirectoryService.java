package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.Doctor;
import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.User;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.response.DirectoryEntryResponse;
import com.dayo.careround.userservice.dto.response.PageResponse;
import com.dayo.careround.userservice.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    public static final int MAX_IDS = 100;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private final UserRepository userRepository;

    // Batch lookup by internal id (the staff references in tokens and events). Unknown ids are simply absent.
    @Transactional(readOnly = true)
    public List<DirectoryEntryResponse> byIds(List<UUID> ids) {
        if (ids.size() > MAX_IDS) {
            throw new BadRequestException("At most " + MAX_IDS + " ids per request");
        }
        return userRepository.findAllById(ids.stream().distinct().toList()).stream()
                .sorted(Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .map(DirectoryService::toEntry)
                .toList();
    }

    // Name-prefix search for pickers: first name, last name or "first last" starts with q (case-insensitive).
    // Sorted by last name, first name, id; size capped at MAX_SIZE.
    @Transactional(readOnly = true)
    public PageResponse<DirectoryEntryResponse> search(String role, String q, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : Math.max(page, 0),
                size == null ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE),
                Sort.by(Sort.Order.asc("lastName").ignoreCase(), Sort.Order.asc("firstName").ignoreCase(), Sort.Order.asc("id")));
        String prefix = q == null || q.isBlank() ? "%" : q.trim().toLowerCase(Locale.ROOT) + "%";
        if (role == null || role.isBlank()) {
            return PageResponse.of(userRepository.searchAll(prefix, pageable), DirectoryService::toEntry);
        }
        return switch (role.toUpperCase(Locale.ROOT)) {
            case "DOCTOR" -> PageResponse.of(userRepository.searchDoctors(prefix, pageable), DirectoryService::toEntry);
            case "NURSE" -> PageResponse.of(userRepository.searchNurses(prefix, pageable), DirectoryService::toEntry);
            default -> throw new BadRequestException("role must be DOCTOR or NURSE");
        };
    }

    private static DirectoryEntryResponse toEntry(User user) {
        String role;
        String grade = null;
        if (user instanceof Doctor d) {
            role = "DOCTOR";
            grade = d.getGrade() != null ? d.getGrade().name() : null;
        } else if (user instanceof Nurse n) {
            role = "NURSE";
            grade = n.getGrade() != null ? n.getGrade().name() : null;
        } else {
            role = "ADMIN";
        }
        return new DirectoryEntryResponse(user.getId(), user.getPublicId(),
                (user.getFirstName() + " " + user.getLastName()).trim(), role, grade);
    }
}
