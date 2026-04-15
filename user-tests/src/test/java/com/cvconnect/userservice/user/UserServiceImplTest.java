package com.cvconnect.userservice.user;

/**
 * ============================================================
 * FILE: UserServiceImplTest.java
 * MODULE: user-service
 * PURPOSE: Unit tests for UserServiceImpl with focus on DB access/change validation.
 *
 * COVERAGE FOCUS:
 *   - DB read/write behavior for password and email verification updates.
 *   - Guard conditions that must prevent DB mutation.
 *   - Mapping behavior in getUsersByRoleCodeOrg.
 * ============================================================
 */

import com.cvconnect.dto.user.UserDto;
import com.cvconnect.entity.User;
import com.cvconnect.enums.AccessMethod;
import com.cvconnect.enums.UserErrorCode;
import com.cvconnect.repository.UserRepository;
import com.cvconnect.service.impl.UserServiceImpl;
import nmquan.commonlib.exception.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Test Case ID: TC-US-USER-001
     * Test Objective: Ensure resetPassword rejects missing user.
     * Input: userId not found in repository.
     * Expected Output: AppException(USER_NOT_FOUND) and no DB save call.
     * Notes: CheckDB + Rollback behavior (no mutation on invalid input).
     */
    @Test
    @DisplayName("resetPassword: user not found")
    void resetPassword_userNotFound_shouldThrowAndNotSave() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resetPassword(100L, "newPassword"))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(UserErrorCode.USER_NOT_FOUND));

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test Case ID: TC-US-USER-002
     * Test Objective: Ensure resetPassword blocks third-party-only account.
     * Input: user accessMethod does not contain LOCAL.
     * Expected Output: AppException(REGISTER_THIRD_PARTY) and no DB save call.
     * Notes: CheckDB + Rollback behavior (no mutation on business rule violation).
     */
    @Test
    @DisplayName("resetPassword: reject third-party account")
    void resetPassword_thirdPartyAccount_shouldThrowAndNotSave() {
        User user = new User();
        user.setId(1L);
        user.setAccessMethod(AccessMethod.GOOGLE.name());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.resetPassword(1L, "newPassword"))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(UserErrorCode.REGISTER_THIRD_PARTY));

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test Case ID: TC-US-USER-003
     * Test Objective: Ensure resetPassword updates encoded password and persists.
     * Input: existing LOCAL account and new raw password.
     * Expected Output: repository.save called once with encoded password.
     * Notes: CheckDB behavior for update path.
     */
    @Test
    @DisplayName("resetPassword: update encoded password and save")
    void resetPassword_localAccount_shouldEncodeAndSave() {
        User user = new User();
        user.setId(2L);
        user.setAccessMethod(AccessMethod.LOCAL.name() + "," + AccessMethod.GOOGLE.name());
        user.setPassword("old-password");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded-password");

        userService.resetPassword(2L, "newPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    /**
     * Test Case ID: TC-US-USER-004
     * Test Objective: Ensure updateEmailVerified rejects missing user.
     * Input: userId not found in repository.
     * Expected Output: AppException(USER_NOT_FOUND), no DB save.
     * Notes: CheckDB + Rollback behavior.
     */
    @Test
    @DisplayName("updateEmailVerified: user not found")
    void updateEmailVerified_userNotFound_shouldThrowAndNotSave() {
        when(userRepository.findById(88L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateEmailVerified(88L, true))
                .isInstanceOf(AppException.class)
                .satisfies(exception -> assertThat(((AppException) exception).getErrorCode())
                        .isEqualTo(UserErrorCode.USER_NOT_FOUND));

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test Case ID: TC-US-USER-005
     * Test Objective: Ensure updateEmailVerified persists the new verification flag.
     * Input: valid user and emailVerified=false.
     * Expected Output: repository.save called with isEmailVerified updated.
     * Notes: CheckDB behavior for direct field update.
     */
    @Test
    @DisplayName("updateEmailVerified: persist updated value")
    void updateEmailVerified_shouldSaveUpdatedFlag() {
        User user = new User();
        user.setId(3L);
        user.setIsEmailVerified(true);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        userService.updateEmailVerified(3L, false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getIsEmailVerified()).isFalse();
    }

    /**
     * Test Case ID: TC-US-USER-006
     * Test Objective: Ensure getUsersByRoleCodeOrg returns empty list when no records.
     * Input: repository returns empty list.
     * Expected Output: empty DTO list.
     * Notes: DB read branch with empty dataset.
     */
    @Test
    @DisplayName("getUsersByRoleCodeOrg: empty repository result")
    void getUsersByRoleCodeOrg_emptyUsers_shouldReturnEmptyList() {
        when(userRepository.getUsersByRoleCodeOrg("HR", 55L, true)).thenReturn(List.of());

        List<UserDto> result = userService.getUsersByRoleCodeOrg("HR", 55L);

        assertThat(result).isEmpty();
    }

    /**
     * Test Case ID: TC-US-USER-007
     * Test Objective: Ensure getUsersByRoleCodeOrg maps entity to response-safe DTO.
     * Input: repository returns one user entity with password set.
     * Expected Output: DTO contains user info and password removed by configResponse.
     * Notes: DB read + response sanitization check.
     */
    @Test
    @DisplayName("getUsersByRoleCodeOrg: map entity and hide password")
    void getUsersByRoleCodeOrg_hasUsers_shouldMapAndHidePassword() {
        User user = new User();
        user.setId(77L);
        user.setUsername("tester");
        user.setPassword("secret");
        user.setEmail("tester@cvconnect.vn");
        user.setFullName("Test User");
        user.setAccessMethod(AccessMethod.LOCAL.name());

        when(userRepository.getUsersByRoleCodeOrg("ORG_ADMIN", 9L, true)).thenReturn(List.of(user));

        List<UserDto> result = userService.getUsersByRoleCodeOrg("ORG_ADMIN", 9L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("tester");
        assertThat(result.get(0).getPassword()).isNull();
    }
}
