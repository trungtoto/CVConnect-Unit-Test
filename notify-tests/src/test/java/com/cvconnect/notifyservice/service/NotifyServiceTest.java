package com.cvconnect.notifyservice.service;

import com.cvconnect.notifyservice.entity.Notification;
import com.cvconnect.notifyservice.repository.NotifyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotifyServiceTest {

    @Mock
    private NotifyRepository notifyRepository;

    @InjectMocks
    private NotifyService notifyService;

    @Test
    void testCreateNotification_WhenValidInput_ShouldSaveAndReturnNotification() {
        // TC_Notify_021
        Notification savedNotification = new Notification(1001L, "Interview scheduled", "SENT");
        savedNotification.setId(1L);

        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1001L, "Interview scheduled", "SENT");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1001L, result.getUserId());
        assertEquals("Interview scheduled", result.getMessage());
        assertEquals("SENT", result.getStatus());
        verify(notifyRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_WhenCalled_ShouldMapUserIdToEntityBeforeSave() {
        // TC_Notify_022
        Notification savedNotification = new Notification(1002L, "Offer letter", "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notifyService.createNotification(1002L, "Offer letter", "NEW");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifyRepository).save(captor.capture());
        assertEquals(1002L, captor.getValue().getUserId());
    }

    @Test
    void testCreateNotification_WhenCalled_ShouldMapMessageToEntityBeforeSave() {
        // TC_Notify_023
        when(notifyRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notifyService.createNotification(1003L, "Message payload", "NEW");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifyRepository).save(captor.capture());
        assertEquals("Message payload", captor.getValue().getMessage());
    }

    @Test
    void testCreateNotification_WhenCalled_ShouldMapStatusToEntityBeforeSave() {
        // TC_Notify_024
        when(notifyRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notifyService.createNotification(1004L, "Status map", "QUEUED");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notifyRepository).save(captor.capture());
        assertEquals("QUEUED", captor.getValue().getStatus());
    }

    @Test
    void testCreateNotification_WhenUserIdIsNull_ShouldPassNullToRepository() {
        // TC_Notify_025
        Notification savedNotification = new Notification(null, "No user", "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(null, "No user", "NEW");

        assertNull(result.getUserId());
    }

    @Test
    void testCreateNotification_WhenMessageIsNull_ShouldPassNullToRepository() {
        // TC_Notify_026
        Notification savedNotification = new Notification(1006L, null, "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1006L, null, "NEW");

        assertNull(result.getMessage());
    }

    @Test
    void testCreateNotification_WhenStatusIsNull_ShouldPassNullToRepository() {
        // TC_Notify_027
        Notification savedNotification = new Notification(1007L, "Status null", null);
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1007L, "Status null", null);

        assertNull(result.getStatus());
    }

    @Test
    void testCreateNotification_WhenMessageIsEmpty_ShouldPersistEmptyMessage() {
        // TC_Notify_028
        Notification savedNotification = new Notification(1008L, "", "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1008L, "", "NEW");

        assertEquals("", result.getMessage());
    }

    @Test
    void testCreateNotification_WhenStatusIsNew_ShouldReturnNewStatus() {
        // TC_Notify_029
        Notification savedNotification = new Notification(1009L, "New status", "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1009L, "New status", "NEW");

        assertEquals("NEW", result.getStatus());
    }

    @Test
    void testCreateNotification_WhenStatusIsSent_ShouldReturnSentStatus() {
        // TC_Notify_030
        Notification savedNotification = new Notification(1010L, "Sent status", "SENT");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1010L, "Sent status", "SENT");

        assertEquals("SENT", result.getStatus());
    }

    @Test
    void testCreateNotification_WhenStatusIsFailed_ShouldReturnFailedStatus() {
        // TC_Notify_031
        Notification savedNotification = new Notification(1011L, "Failed status", "FAILED");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1011L, "Failed status", "FAILED");

        assertEquals("FAILED", result.getStatus());
    }

    @Test
    void testCreateNotification_WhenRepositoryThrowsException_ShouldPropagateException() {
        // TC_Notify_032
        when(notifyRepository.save(any(Notification.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notifyService.createNotification(1012L, "Error path", "NEW"));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testCreateNotification_WhenRepositoryAssignsId_ShouldReturnAssignedId() {
        // TC_Notify_033
        Notification savedNotification = new Notification(1013L, "Id assigned", "NEW");
        savedNotification.setId(913L);
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1013L, "Id assigned", "NEW");

        assertEquals(913L, result.getId());
    }

    @Test
    void testCreateNotification_WhenCalledMultipleTimes_ShouldCallSaveEachTime() {
        // TC_Notify_034
        when(notifyRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notifyService.createNotification(1014L, "First", "NEW");
        notifyService.createNotification(1015L, "Second", "SENT");

        verify(notifyRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_WhenMessageLengthIsBoundary_ShouldPersistSuccessfully() {
        // TC_Notify_035
        String message = "A".repeat(255);
        Notification savedNotification = new Notification(1016L, message, "NEW");
        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1016L, message, "NEW");

        assertEquals(255, result.getMessage().length());
    }
}

