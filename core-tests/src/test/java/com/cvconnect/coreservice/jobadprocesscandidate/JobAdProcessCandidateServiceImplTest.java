package com.cvconnect.coreservice.jobadprocesscandidate;

/**
 * ============================================================
 * FILE: JobAdProcessCandidateServiceImplTest.java
 * MODULE: core-service
 * PURPOSE: Unit test cho hệ thống Quy trình đánh giá Ứng Viên 
 *
 * BAO PHỦ CÁC LUỒNG CẤP 2 (Branch Coverage):
 *   - create: Rỗng vs có data.
 *   - findByJobAdCandidateId: List empty vs list data.
 *   - findById: Optional null vs has data.
 *   - getDetailByJobAdCandidateIds: Rỗng vs Grouping & Sorting data.
 * ============================================================
 */

import com.cvconnect.dto.jobAdCandidate.JobAdProcessCandidateDto;
import com.cvconnect.entity.JobAdProcessCandidate;
import com.cvconnect.repository.JobAdProcessCandidateRepository;
import com.cvconnect.service.impl.JobAdProcessCandidateServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobAdProcessCandidateServiceImpl - Unit Tests (C2 Branch Coverage)")
class JobAdProcessCandidateServiceImplTest {

    @Mock
    private JobAdProcessCandidateRepository jobAdProcessCandidateRepository;

    @InjectMocks
    private JobAdProcessCandidateServiceImpl jobAdProcessCandidateService;

    @Test
    @DisplayName("TC-JPC-001: Hàm create branch data null/rỗng")
    void TC_JPC_001_createEmpty() {
        jobAdProcessCandidateService.create(Collections.emptyList());
        verify(jobAdProcessCandidateRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("TC-JPC-002: Hàm create branch saving")
    void TC_JPC_002_createSuccess() {
        JobAdProcessCandidateDto dto = new JobAdProcessCandidateDto();
        dto.setId(1L);
        jobAdProcessCandidateService.create(List.of(dto));
        verify(jobAdProcessCandidateRepository).saveAll(any());
    }

    @Test
    @DisplayName("TC-JPC-003: findByJobAdCandidateId trả về rỗng")
    void TC_JPC_003_findByJobAdCandidateIdEmpty() {
        when(jobAdProcessCandidateRepository.findByJobAdCandidateId(10L)).thenReturn(Collections.emptyList());
        List<JobAdProcessCandidateDto> result = jobAdProcessCandidateService.findByJobAdCandidateId(10L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("TC-JPC-004: findByJobAdCandidateId trả về danh sách")
    void TC_JPC_004_findByJobAdCandidateIdSuccess() {
        JobAdProcessCandidate pc = new JobAdProcessCandidate(); pc.setId(5L);
        when(jobAdProcessCandidateRepository.findByJobAdCandidateId(10L)).thenReturn(List.of(pc));
        List<JobAdProcessCandidateDto> result = jobAdProcessCandidateService.findByJobAdCandidateId(10L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("TC-JPC-005: findById trả về rỗng (nếu dùng Optional.orElse(null))")
    void TC_JPC_005_findByIdNull() {
        when(jobAdProcessCandidateRepository.findById(9L)).thenReturn(Optional.empty());
        JobAdProcessCandidateDto res = jobAdProcessCandidateService.findById(9L);
        assertThat(res).isNull();
    }

    @Test
    @DisplayName("TC-JPC-006: findById tìm thấy")
    void TC_JPC_006_findByIdSuccess() {
        JobAdProcessCandidate pc = new JobAdProcessCandidate(); pc.setId(9L);
        when(jobAdProcessCandidateRepository.findById(9L)).thenReturn(Optional.of(pc));
        JobAdProcessCandidateDto res = jobAdProcessCandidateService.findById(9L);
        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(9L);
    }

    @Test
    @DisplayName("TC-JPC-007: boolean checks")
    void TC_JPC_007_booleanChecks() {
        when(jobAdProcessCandidateRepository.validateProcessOrderChange(1L, 2L)).thenReturn(true);
        when(jobAdProcessCandidateRepository.validateCurrentProcessTypeIs(2L, "ONB")).thenReturn(false);
        JobAdProcessCandidateDto pcDto = new JobAdProcessCandidateDto(); pcDto.setId(10L);
        when(jobAdProcessCandidateRepository.getCurrentProcess(1L, 3L)).thenReturn(pcDto);

        assertThat(jobAdProcessCandidateService.validateProcessOrderChange(1L, 2L)).isTrue();
        assertThat(jobAdProcessCandidateService.validateCurrentProcessTypeIs(2L, "ONB")).isFalse();
        assertThat(jobAdProcessCandidateService.getCurrentProcess(1L, 3L).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("TC-JPC-008: Map Dto details grouping bằng empty")
    void TC_JPC_008_getDetailMapEmpty() {
        when(jobAdProcessCandidateRepository.getDetailByJobAdCandidateIds(anyList())).thenReturn(Collections.emptyList());
        Map<Long, List<JobAdProcessCandidateDto>> res = jobAdProcessCandidateService.getDetailByJobAdCandidateIds(List.of(1L, 2L));
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("TC-JPC-009: Map Dto details grouping và sorting list")
    void TC_JPC_009_getDetailMapDataAndSorting() {
        JobAdProcessCandidateDto d1 = new JobAdProcessCandidateDto(); d1.setJobAdCandidateId(5L); d1.setSortOrder(5); // Sort 5
        JobAdProcessCandidateDto d2 = new JobAdProcessCandidateDto(); d2.setJobAdCandidateId(5L); d2.setSortOrder(2); // Sort 2 -> expected lên đầu
        JobAdProcessCandidateDto d3 = new JobAdProcessCandidateDto(); d3.setJobAdCandidateId(9L); d3.setSortOrder(1);

        when(jobAdProcessCandidateRepository.getDetailByJobAdCandidateIds(anyList())).thenReturn(List.of(d1, d2, d3));

        Map<Long, List<JobAdProcessCandidateDto>> res = jobAdProcessCandidateService.getDetailByJobAdCandidateIds(List.of(5L, 9L));

        assertThat(res).hasSize(2);
        List<JobAdProcessCandidateDto> group5 = res.get(5L);
        assertThat(group5).hasSize(2);
        assertThat(group5.get(0).getSortOrder()).isEqualTo(2); // d2 sorted first
        assertThat(group5.get(1).getSortOrder()).isEqualTo(5); // d1 sorted next
    }
}
