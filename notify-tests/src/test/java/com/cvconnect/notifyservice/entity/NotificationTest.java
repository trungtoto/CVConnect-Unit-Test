package com.cvconnect.notifyservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationTest {

    @Test
    void testDefaultConstructor_WhenCreated_ShouldHaveNullId() {
        // TC_Notify_001
        Notification notification = new Notification();

        assertNull(notification.getId());
    }

    @Test
    void testDefaultConstructor_WhenCreated_ShouldHaveNullUserId() {
        // TC_Notify_002
        Notification notification = new Notification();

        assertNull(notification.getUserId());
    }

    @Test
    void testDefaultConstructor_WhenCreated_ShouldHaveNullMessage() {
        // TC_Notify_003
        Notification notification = new Notification();

        assertNull(notification.getMessage());
    }

    @Test
    void testDefaultConstructor_WhenCreated_ShouldHaveNullStatus() {
        // TC_Notify_004
        Notification notification = new Notification();

        assertNull(notification.getStatus());
    }

    @Test
    void testSetId_WhenPositiveValue_ShouldReturnSameValue() {
        // TC_Notify_005
        Notification notification = new Notification();

        notification.setId(10L);

        assertEquals(10L, notification.getId());
    }

    @Test
    void testSetId_WhenZeroValue_ShouldReturnZero() {
        // TC_Notify_006
        Notification notification = new Notification();

        notification.setId(0L);

        assertEquals(0L, notification.getId());
    }

    @Test
    void testSetId_WhenNull_ShouldRemainNull() {
        // TC_Notify_007
        Notification notification = new Notification();

        notification.setId(null);

        assertNull(notification.getId());
    }

    @Test
    void testSetUserId_WhenPositiveValue_ShouldReturnSameValue() {
        // TC_Notify_008
        Notification notification = new Notification();

        notification.setUserId(3003L);

        assertEquals(3003L, notification.getUserId());
    }

    @Test
    void testSetUserId_WhenZeroValue_ShouldReturnZero() {
        // TC_Notify_009
        Notification notification = new Notification();

        notification.setUserId(0L);

        assertEquals(0L, notification.getUserId());
    }

    @Test
    void testSetUserId_WhenNull_ShouldRemainNull() {
        // TC_Notify_010
        Notification notification = new Notification();

        notification.setUserId(null);

        assertNull(notification.getUserId());
    }

    @Test
    void testSetMessage_WhenNormalValue_ShouldReturnSameValue() {
        // TC_Notify_011
        Notification notification = new Notification();

        notification.setMessage("Offer sent");

        assertEquals("Offer sent", notification.getMessage());
    }

    @Test
    void testSetMessage_WhenEmptyValue_ShouldReturnEmptyString() {
        // TC_Notify_012
        Notification notification = new Notification();

        notification.setMessage("");

        assertEquals("", notification.getMessage());
    }

    @Test
    void testSetMessage_WhenNull_ShouldRemainNull() {
        // TC_Notify_013
        Notification notification = new Notification();

        notification.setMessage(null);

        assertNull(notification.getMessage());
    }

    @Test
    void testSetStatus_WhenNormalValue_ShouldReturnSameValue() {
        // TC_Notify_014
        Notification notification = new Notification();

        notification.setStatus("QUEUED");

        assertEquals("QUEUED", notification.getStatus());
    }

    @Test
    void testSetStatus_WhenLowercaseValue_ShouldReturnSameValue() {
        // TC_Notify_015
        Notification notification = new Notification();

        notification.setStatus("sent");

        assertEquals("sent", notification.getStatus());
    }

    @Test
    void testSetStatus_WhenEmptyValue_ShouldReturnEmptyString() {
        // TC_Notify_016
        Notification notification = new Notification();

        notification.setStatus("");

        assertEquals("", notification.getStatus());
    }

    @Test
    void testSetStatus_WhenNull_ShouldRemainNull() {
        // TC_Notify_017
        Notification notification = new Notification();

        notification.setStatus(null);

        assertNull(notification.getStatus());
    }

    @Test
    void testAllArgsConstructor_WhenCreated_ShouldMapUserId() {
        // TC_Notify_018
        Notification notification = new Notification(4004L, "Interview reminder", "SENT");

        assertEquals(4004L, notification.getUserId());
    }

    @Test
    void testAllArgsConstructor_WhenCreated_ShouldMapMessage() {
        // TC_Notify_019
        Notification notification = new Notification(4004L, "Interview reminder", "SENT");

        assertEquals("Interview reminder", notification.getMessage());
    }

    @Test
    void testAllArgsConstructor_WhenCreated_ShouldMapStatusAndKeepIdNull() {
        // TC_Notify_020
        Notification notification = new Notification(4004L, "Interview reminder", "SENT");

        assertNull(notification.getId());
        assertEquals("SENT", notification.getStatus());
    }
}

