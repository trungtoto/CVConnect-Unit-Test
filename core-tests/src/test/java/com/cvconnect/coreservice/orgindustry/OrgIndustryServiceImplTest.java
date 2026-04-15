package com.cvconnect.coreservice.orgindustry;

/**
 * ============================================================
 * FILE: OrgIndustryServiceImplTest.java
 * MODULE: core-service
 * PURPOSE: Unit test cho hệ thống Ngành Nghề Tổ Chức
 *
 * BAO PHỦ CÁC LUỒNG CẤP 2 (Branch Coverage):
 *   - create, delete trực tiếp repository wrapper.
 * ============================================================
 */

import com.cvconnect.dto.org.OrgIndustryDto;
import com.cvconnect.repository.OrgIndustryRepository;
import com.cvconnect.service.impl.OrgIndustryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgIndustryServiceImpl - Unit Tests (C2 Branch Coverage)")
class OrgIndustryServiceImplTest {

    @Mock
    private OrgIndustryRepository orgIndustryRepository;

    @InjectMocks
    private OrgIndustryServiceImpl orgIndustryService;

    @Test
    @DisplayName("TC-OI-001: Convert list & lưu thông tin Industry")
    void testCreateIndustries() {
        OrgIndustryDto dto = new OrgIndustryDto();
        dto.setIndustryId(1L);

        orgIndustryService.createIndustries(List.of(dto));

        verify(orgIndustryRepository).saveAll(any());
    }

    @Test
    @DisplayName("TC-OI-002: Xóa by orgId")
    void testDeleteByOrgId() {
        orgIndustryService.deleteByOrgId(5L);
        verify(orgIndustryRepository).deleteByOrgId(5L);
    }

    @Test
    @DisplayName("TC-OI-003: Xóa by list ID")
    void testDeleteByIndustryIds() {
        orgIndustryService.deleteByIndustryIdsAndOrgId(List.of(7L, 8L), 5L);
        verify(orgIndustryRepository).deleteByIndustryIdsAndOrgId(List.of(7L, 8L), 5L);
    }
}
