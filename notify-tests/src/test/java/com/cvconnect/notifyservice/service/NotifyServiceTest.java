package com.cvconnect.notifyservice.service;

import com.cvconnect.notifyservice.entity.Notification;
import com.cvconnect.notifyservice.repository.NotifyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
        // TC_Notify_01
        Notification savedNotification = new Notification(1001L, "Interview scheduled", "SENT");
        savedNotification.setId(1L);

        when(notifyRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notifyService.createNotification(1001L, "Interview scheduled", "SENT");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1001L, result.getUserId());
        assertEquals("Interview scheduled", result.getMessage());
        assertEquals("SENT", result.getStatus());
        verify(notifyRepository).save(any(Notification.class));
    }
}

