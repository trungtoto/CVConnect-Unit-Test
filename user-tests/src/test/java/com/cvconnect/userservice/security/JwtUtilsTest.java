package com.cvconnect.userservice.security;

/**
 * ============================================================
 * FILE: JwtUtilsTest.java
 * MODULE: user-service
 * PURPOSE: Unit tests for JwtUtils token generation.
 * ============================================================
 */

import com.cvconnect.dto.role.RoleDto;
import com.cvconnect.dto.user.UserDto;
import com.cvconnect.enums.MemberType;
import com.cvconnect.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils - Unit Tests")
class JwtUtilsTest {

        private static final String VALID_BASE64_SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Mock
    private com.cvconnect.service.RoleMenuService roleMenuService;

    @Mock
    private com.cvconnect.service.RoleService roleService;

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "EXPIRATION", 3600);
                ReflectionTestUtils.setField(jwtUtils, "SECRET_KEY", VALID_BASE64_SECRET);
    }

    /**
     * Test Case ID: TC-US-JWT-001
     * Test Objective: Generate JWT containing expected user/role/permission claims.
     * Input: valid user with two roles and mocked authorities.
     * Expected Output: non-empty JWT, parsable claims include roles and permissions.
     * Notes: Main happy path for authentication token generation.
     */
    @Test
    @DisplayName("generateToken: include user, roles, and permissions claims")
    void generateToken_validInput_shouldContainExpectedClaims() {
        UserDto user = UserDto.builder()
                .id(10L)
                .username("org.admin")
                .email("admin@cvconnect.vn")
                .fullName("Organization Admin")
                .orgId(77L)
                .build();

        RoleDto orgAdminRole = RoleDto.builder()
                .id(1L)
                .code("ORG_ADMIN")
                .name("Organization Admin")
                .memberType(MemberType.ORGANIZATION)
                .build();

        RoleDto hrRole = RoleDto.builder()
                .id(2L)
                .code("HR")
                .name("Human Resources")
                .memberType(MemberType.ORGANIZATION)
                .build();

        Map<String, List<String>> permissions = Map.of(
                "USER", List.of("VIEW", "UPDATE"),
                "ROLE", List.of("VIEW")
        );

        when(roleService.getRoleByUserId(10L)).thenReturn(List.of(orgAdminRole, hrRole));
        when(roleMenuService.getAuthorities(10L, List.of("ORG_ADMIN", "HR"))).thenReturn(permissions);

        String token = jwtUtils.generateToken(user);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(nmquan.commonlib.utils.JwtUtils.getSignInKey(VALID_BASE64_SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(token).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo("org.admin");
        assertThat((List<String>) claims.get("roles")).contains("ORG_ADMIN", "HR", "ORGANIZATION");
        assertThat((Map<String, List<String>>) claims.get("permissions")).containsEntry("USER", List.of("VIEW", "UPDATE"));
        assertThat((String) claims.get("user")).contains("org.admin");
        assertThat((String) claims.get("user")).contains("admin@cvconnect.vn");
    }

    /**
     * Test Case ID: TC-US-JWT-002
     * Test Objective: Throw standardized AppException when signing key configuration is invalid.
     * Input: SECRET_KEY is null.
     * Expected Output: AppException(CommonErrorCode.ERROR).
     * Notes: Error-handling branch in generateToken.
     */
    @Test
    @DisplayName("generateToken: throw AppException when secret key is invalid")
    void generateToken_invalidSecret_shouldThrowAppException() {
        ReflectionTestUtils.setField(jwtUtils, "SECRET_KEY", null);

        UserDto user = UserDto.builder()
                .id(20L)
                .username("candidate")
                .email("candidate@cvconnect.vn")
                .fullName("Candidate")
                .build();

        RoleDto candidateRole = RoleDto.builder()
                .id(3L)
                .code("CANDIDATE")
                .memberType(MemberType.CANDIDATE)
                .build();

        when(roleService.getRoleByUserId(20L)).thenReturn(List.of(candidateRole));
        when(roleMenuService.getAuthorities(20L, List.of("CANDIDATE"))).thenReturn(Map.of());

        assertThatThrownBy(() -> jwtUtils.generateToken(user))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(CommonErrorCode.ERROR));
    }

    /**
     * Test Case ID: TC-US-JWT-003
     * Test Objective: Ensure generated helper tokens are non-empty UUID values.
     * Input: no input.
     * Expected Output: each token is non-empty and differs between calls.
     * Notes: Covers refresh/verify/reset/invite helper generation methods.
     */
    @Test
    @DisplayName("helper token generators: return non-empty unique values")
    void helperTokenGenerators_shouldReturnNonEmptyUniqueValues() {
        String refresh1 = jwtUtils.generateRefreshToken();
        String refresh2 = jwtUtils.generateRefreshToken();
        String verifyToken = jwtUtils.generateTokenVerifyEmail();
        String resetToken = jwtUtils.generateTokenResetPassword();
        String inviteToken = jwtUtils.generateTokenInviteJoinOrg();

        assertThat(refresh1).isNotBlank();
        assertThat(refresh2).isNotBlank();
        assertThat(refresh1).isNotEqualTo(refresh2);
        assertThat(verifyToken).isNotBlank();
        assertThat(resetToken).isNotBlank();
        assertThat(inviteToken).isNotBlank();
    }
}
