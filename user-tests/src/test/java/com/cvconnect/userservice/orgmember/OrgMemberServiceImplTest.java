package com.cvconnect.userservice.orgmember;

/**
 * ============================================================
 * FILE: OrgMemberServiceImplTest.java
 * MODULE: user-service
 * PURPOSE: Unit tests for OrgMemberServiceImpl.
 *
 * COVERAGE FOCUS:
 *   - DB access behavior for org-member retrieval.
 *   - Validation branches in invite flow.
 *   - DB change behavior for invite creation.
 * ============================================================
 */

import com.cvconnect.dto.common.InviteUserRequest;
import com.cvconnect.dto.internal.response.OrgDto;
import com.cvconnect.dto.inviteJoinOrg.InviteJoinOrgDto;
import com.cvconnect.dto.orgMember.OrgMemberDto;
import com.cvconnect.dto.role.RoleDto;
import com.cvconnect.dto.user.UserDto;
import com.cvconnect.entity.OrgMember;
import com.cvconnect.enums.InviteJoinStatus;
import com.cvconnect.enums.MemberType;
import com.cvconnect.enums.UserErrorCode;
import com.cvconnect.repository.OrgMemberRepository;
import com.cvconnect.service.InviteJoinOrgService;
import com.cvconnect.service.RoleService;
import com.cvconnect.service.UserService;
import com.cvconnect.service.impl.OrgMemberServiceImpl;
import com.cvconnect.utils.JwtUtils;
import com.cvconnect.utils.ServiceUtils;
import nmquan.commonlib.enums.EmailTemplateEnum;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.service.SendEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgMemberServiceImpl - Unit Tests")
class OrgMemberServiceImplTest {

    @Mock
    private OrgMemberRepository orgMemberRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private InviteJoinOrgService inviteJoinOrgService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private UserService userService;

    @Mock
    private com.cvconnect.common.RestTemplateClient restTemplateClient;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private nmquan.commonlib.utils.KafkaUtils kafkaUtils;

    @InjectMocks
    private OrgMemberServiceImpl orgMemberService;

    /**
     * Test Case ID: TC-US-OM-001
     * Test Objective: Return null when user has no org member record.
     * Input: repository findByUserId returns empty.
     * Expected Output: null result and no downstream org lookup.
     * Notes: DB read branch (not found).
     */
    @Test
    @DisplayName("getOrgMember: return null when org-member record is missing")
    void getOrgMember_notFound_shouldReturnNull() {
        when(orgMemberRepository.findByUserId(1L)).thenReturn(Optional.empty());

        OrgMemberDto result = orgMemberService.getOrgMember(1L);

        assertThat(result).isNull();
        verifyNoInteractions(restTemplateClient);
    }

    /**
     * Test Case ID: TC-US-OM-002
     * Test Objective: Return null when org member account is inactive.
     * Input: repository returns entity with isActive=false.
     * Expected Output: null result.
     * Notes: DB read branch with inactive record.
     */
    @Test
    @DisplayName("getOrgMember: return null for inactive org-member")
    void getOrgMember_inactive_shouldReturnNull() {
        OrgMember orgMember = new OrgMember();
        orgMember.setUserId(2L);
        orgMember.setOrgId(99L);
        orgMember.setIsActive(false);

        when(orgMemberRepository.findByUserId(2L)).thenReturn(Optional.of(orgMember));

        OrgMemberDto result = orgMemberService.getOrgMember(2L);

        assertThat(result).isNull();
        verifyNoInteractions(restTemplateClient);
    }

    /**
     * Test Case ID: TC-US-OM-003
     * Test Objective: Map active org-member entity to DTO including organization.
     * Input: active org-member and mock org response.
     * Expected Output: non-null OrgMemberDto with org info populated.
     * Notes: DB read + external enrichment branch.
     */
    @Test
    @DisplayName("getOrgMember: map active org-member with organization data")
    void getOrgMember_active_shouldMapAndSetOrg() {
        OrgMember orgMember = new OrgMember();
        orgMember.setId(33L);
        orgMember.setUserId(3L);
        orgMember.setOrgId(100L);
        orgMember.setIsActive(true);

        OrgDto orgDto = OrgDto.builder().id(100L).name("CVConnect Org").build();

        when(orgMemberRepository.findByUserId(3L)).thenReturn(Optional.of(orgMember));
        when(restTemplateClient.getOrgById(100L)).thenReturn(orgDto);

        OrgMemberDto result = orgMemberService.getOrgMember(3L);

        assertThat(result).isNotNull();
        assertThat(result.getOrgId()).isEqualTo(100L);
        assertThat(result.getOrg()).isNotNull();
        assertThat(result.getOrg().getName()).isEqualTo("CVConnect Org");
    }

    /**
     * Test Case ID: TC-US-OM-004
     * Test Objective: Block invite when user already belongs to current organization.
     * Input: existing org-member with same orgId.
     * Expected Output: AppException(USER_JOINED_ORG), no invite created.
     * Notes: CheckDB + Rollback behavior (no write when validation fails).
     */
    @Test
    @DisplayName("inviteUserToJoinOrg: reject user already in current org")
    void inviteUserToJoinOrg_userJoinedOrg_shouldThrow() {
        InviteUserRequest request = InviteUserRequest.builder()
                .userId(11L)
                .roleId(21L)
                .build();

        OrgMember existing = new OrgMember();
        existing.setUserId(11L);
        existing.setOrgId(500L);

        when(serviceUtils.validOrgMember()).thenReturn(500L);
        when(orgMemberRepository.findByUserId(11L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> orgMemberService.inviteUserToJoinOrg(request))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(UserErrorCode.USER_JOINED_ORG));

        verify(inviteJoinOrgService, never()).create(any());
        verify(sendEmailService, never()).sendEmailWithTemplate(any(), any(), any(), any());
    }

    /**
     * Test Case ID: TC-US-OM-005
     * Test Objective: Block invite when target user email is not verified.
     * Input: user.isEmailVerified=false.
     * Expected Output: AppException(EMAIL_NOT_VERIFIED), no invite created.
     * Notes: CheckDB + Rollback behavior (no write when validation fails).
     */
    @Test
    @DisplayName("inviteUserToJoinOrg: reject target user with unverified email")
    void inviteUserToJoinOrg_unverifiedEmail_shouldThrow() {
        InviteUserRequest request = InviteUserRequest.builder()
                .userId(12L)
                .roleId(22L)
                .build();

        UserDto targetUser = UserDto.builder()
                .id(12L)
                .email("candidate@cvconnect.vn")
                .fullName("Candidate User")
                .isEmailVerified(false)
                .build();

        when(serviceUtils.validOrgMember()).thenReturn(600L);
        when(orgMemberRepository.findByUserId(12L)).thenReturn(Optional.empty());
        when(userService.findById(12L)).thenReturn(targetUser);

        assertThatThrownBy(() -> orgMemberService.inviteUserToJoinOrg(request))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(UserErrorCode.EMAIL_NOT_VERIFIED));

        verify(inviteJoinOrgService, never()).create(any());
        verify(sendEmailService, never()).sendEmailWithTemplate(any(), any(), any(), any());
    }

    /**
     * Test Case ID: TC-US-OM-006
     * Test Objective: Create invite and send email when request is valid.
     * Input: valid org, user, and organization role.
     * Expected Output: inviteJoinOrgService.create called with PENDING invite and token; email sent.
     * Notes: CheckDB behavior for invite insert path.
     */
    @Test
    @DisplayName("inviteUserToJoinOrg: create invite and send email")
    void inviteUserToJoinOrg_validRequest_shouldCreateInviteAndSendEmail() {
        InviteUserRequest request = InviteUserRequest.builder()
                .userId(13L)
                .roleId(23L)
                .build();

        UserDto targetUser = UserDto.builder()
                .id(13L)
                .email("new.member@cvconnect.vn")
                .fullName("New Member")
                .isEmailVerified(true)
                .build();

        RoleDto role = RoleDto.builder()
                .id(23L)
                .code("HR")
                .name("HR")
                .memberType(MemberType.ORGANIZATION)
                .build();

        OrgDto orgDto = OrgDto.builder()
                .id(700L)
                .name("CVConnect Company")
                .build();

        when(serviceUtils.validOrgMember()).thenReturn(700L);
        when(orgMemberRepository.findByUserId(13L)).thenReturn(Optional.empty());
        when(userService.findById(13L)).thenReturn(targetUser);
        when(roleService.getRoleById(23L)).thenReturn(role);
        when(jwtUtils.generateTokenInviteJoinOrg()).thenReturn("invite-token-700");
        when(restTemplateClient.getOrgById(700L)).thenReturn(orgDto);

        orgMemberService.inviteUserToJoinOrg(request);

        ArgumentCaptor<List<InviteJoinOrgDto>> inviteCaptor = ArgumentCaptor.forClass(List.class);
        verify(inviteJoinOrgService).create(inviteCaptor.capture());
        assertThat(inviteCaptor.getValue()).hasSize(1);
        InviteJoinOrgDto invite = inviteCaptor.getValue().get(0);
        assertThat(invite.getUserId()).isEqualTo(13L);
        assertThat(invite.getRoleId()).isEqualTo(23L);
        assertThat(invite.getOrgId()).isEqualTo(700L);
        assertThat(invite.getStatus()).isEqualTo(InviteJoinStatus.PENDING.name());
        assertThat(invite.getToken()).isEqualTo("invite-token-700");

        verify(sendEmailService).sendEmailWithTemplate(
                eq(List.of("new.member@cvconnect.vn")),
                isNull(),
                eq(EmailTemplateEnum.INVITE_JOIN_ORG),
                anyMap()
        );
    }
}
