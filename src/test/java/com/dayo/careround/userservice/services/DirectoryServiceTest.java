package com.dayo.careround.userservice.services;

import com.dayo.careround.userservice.data.models.Admin;
import com.dayo.careround.userservice.data.models.Doctor;
import com.dayo.careround.userservice.data.models.Nurse;
import com.dayo.careround.userservice.data.models.enums.DoctorGrade;
import com.dayo.careround.userservice.data.models.enums.NurseGrade;
import com.dayo.careround.userservice.data.repositories.UserRepository;
import com.dayo.careround.userservice.dto.response.DirectoryEntryResponse;
import com.dayo.careround.userservice.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DirectoryServiceTest {

    private final UserRepository repo = mock(UserRepository.class);
    private final DirectoryService service = new DirectoryService(repo);

    @Test
    void moreThan100IdsIsRejected() {
        List<UUID> ids = IntStream.range(0, 101).mapToObj(i -> UUID.randomUUID()).toList();
        assertThatThrownBy(() -> service.byIds(ids)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void entriesCarryRoleGradeAndNameOnly() {
        Doctor d = new Doctor(); d.setId(UUID.randomUUID()); d.setPublicId(UUID.randomUUID()); d.setFirstName("Ada"); d.setLastName("Obi"); d.setGrade(DoctorGrade.CONSULTANT);
        Nurse n = new Nurse(); n.setId(UUID.randomUUID()); n.setPublicId(UUID.randomUUID()); n.setFirstName("Bisi"); n.setLastName("Ade"); n.setGrade(NurseGrade.STAFF_NURSE);
        Admin a = new Admin(); a.setId(UUID.randomUUID()); a.setPublicId(UUID.randomUUID()); a.setFirstName("Cy"); a.setLastName("Zed");
        when(repo.findAllById(anyList())).thenReturn(List.of(d, n, a));
        List<DirectoryEntryResponse> out = service.byIds(List.of(d.getId(), n.getId(), a.getId(), UUID.randomUUID()));
        assertThat(out).extracting(DirectoryEntryResponse::displayName).containsExactly("Bisi Ade", "Ada Obi", "Cy Zed");
        assertThat(out).extracting(DirectoryEntryResponse::role).containsExactly("NURSE", "DOCTOR", "ADMIN");
        assertThat(out).extracting(DirectoryEntryResponse::grade).containsExactly("STAFF_NURSE", "CONSULTANT", null);
        assertThat(out.get(0).id()).isEqualTo(n.getId());
    }

    @Test
    void searchRejectsUnknownRoleAndCapsSize() {
        assertThatThrownBy(() -> service.search("ADMIN", null, 0, 20)).isInstanceOf(BadRequestException.class);
        when(repo.searchNurses(eq("bi%"), any(Pageable.class))).thenReturn(Page.empty());
        service.search("nurse", "Bi", 0, 500);
        verify(repo).searchNurses(eq("bi%"), org.mockito.ArgumentMatchers.argThat(p -> p.getPageSize() == 100));
    }
}
