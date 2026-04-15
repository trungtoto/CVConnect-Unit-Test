package com.cvconnect.notifyservice.repository;

import com.cvconnect.notifyservice.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = NotifyRepositoryTest.TestApplication.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EntityScan(basePackages = "com.cvconnect.notifyservice.entity")
class NotifyRepositoryTest {

    @Autowired
    private NotifyRepository notifyRepository;

    @Test
    void testSave_WhenValidData_ShouldReturnSuccess() {
        // TC_Notify_036
        Notification input = new Notification(2002L, "Profile approved", "NEW");

        Notification saved = notifyRepository.save(input);

        // CheckDB: verify the record can be read back from DB after save.
        Optional<Notification> loadedFromDb = notifyRepository.findById(saved.getId());

        assertTrue(loadedFromDb.isPresent());
        assertEquals(2002L, loadedFromDb.get().getUserId());
        assertEquals("Profile approved", loadedFromDb.get().getMessage());
        assertEquals("NEW", loadedFromDb.get().getStatus());
    }

    @Test
    void testSave_WhenValidData_ShouldGenerateId() {
        // TC_Notify_037
        Notification saved = notifyRepository.save(new Notification(2003L, "Generated id", "NEW"));

        assertNotNull(saved.getId());
    }

    @Test
    void testSaveAndUpdate_WhenMessageChanges_ShouldPersistUpdatedMessage() {
        // TC_Notify_038
        Notification saved = notifyRepository.save(new Notification(2004L, "Old message", "NEW"));
        saved.setMessage("Updated message");
        notifyRepository.save(saved);

        Optional<Notification> loadedFromDb = notifyRepository.findById(saved.getId());

        assertTrue(loadedFromDb.isPresent());
        assertEquals("Updated message", loadedFromDb.get().getMessage());
    }

    @Test
    void testSaveAndUpdate_WhenStatusChanges_ShouldPersistUpdatedStatus() {
        // TC_Notify_039
        Notification saved = notifyRepository.save(new Notification(2005L, "Status change", "NEW"));
        saved.setStatus("SENT");
        notifyRepository.save(saved);

        Optional<Notification> loadedFromDb = notifyRepository.findById(saved.getId());

        assertTrue(loadedFromDb.isPresent());
        assertEquals("SENT", loadedFromDb.get().getStatus());
    }

    @Test
    void testFindById_WhenRecordDoesNotExist_ShouldReturnEmpty() {
        // TC_Notify_040
        Optional<Notification> loadedFromDb = notifyRepository.findById(-1L);

        assertFalse(loadedFromDb.isPresent());
    }

    @Test
    void testSaveAll_WhenTwoRecordsProvided_ShouldPersistTwoRecords() {
        // TC_Notify_041
        List<Notification> saved = notifyRepository.saveAll(List.of(
                new Notification(2006L, "Bulk one", "NEW"),
                new Notification(2007L, "Bulk two", "NEW")
        ));

        assertEquals(2, saved.size());
    }

    @Test
    void testCount_WhenTwoRecordsSaved_ShouldReturnTwo() {
        // TC_Notify_042
        notifyRepository.save(new Notification(2008L, "Count one", "NEW"));
        notifyRepository.save(new Notification(2009L, "Count two", "NEW"));

        assertEquals(2, notifyRepository.count());
    }

    @Test
    void testDeleteById_WhenRecordExists_ShouldRemoveRecord() {
        // TC_Notify_043
        Notification saved = notifyRepository.save(new Notification(2010L, "Delete by id", "NEW"));

        notifyRepository.deleteById(saved.getId());

        assertFalse(notifyRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDelete_WhenEntityExists_ShouldRemoveRecord() {
        // TC_Notify_044
        Notification saved = notifyRepository.save(new Notification(2011L, "Delete entity", "NEW"));

        notifyRepository.delete(saved);

        assertFalse(notifyRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testSaveAndFlush_WhenUserIdIsNull_ShouldThrowException() {
        // TC_Notify_045
        Notification input = new Notification(null, "Null user", "NEW");

        assertThrows(DataIntegrityViolationException.class, () -> notifyRepository.saveAndFlush(input));
    }

    @Test
    void testSaveAndFlush_WhenMessageIsNull_ShouldThrowException() {
        // TC_Notify_046
        Notification input = new Notification(2012L, null, "NEW");

        assertThrows(DataIntegrityViolationException.class, () -> notifyRepository.saveAndFlush(input));
    }

    @Test
    void testSaveAndFlush_WhenStatusIsNull_ShouldThrowException() {
        // TC_Notify_047
        Notification input = new Notification(2013L, "Null status", null);

        assertThrows(DataIntegrityViolationException.class, () -> notifyRepository.saveAndFlush(input));
    }

    @Test
    void testSaveAndFlush_WhenMessageLengthIs255_ShouldPersistSuccessfully() {
        // TC_Notify_048
        Notification input = new Notification(2014L, "A".repeat(255), "NEW");

        Notification saved = notifyRepository.saveAndFlush(input);

        assertNotNull(saved.getId());
        assertEquals(255, saved.getMessage().length());
    }

    @Test
    void testSaveAndFlush_WhenMessageLengthExceeds255_ShouldThrowException() {
        // TC_Notify_049
        Notification input = new Notification(2015L, "B".repeat(256), "NEW");

        assertThrows(DataIntegrityViolationException.class, () -> notifyRepository.saveAndFlush(input));
    }

    @Test
    void testSaveAndFlush_WhenStatusLengthExceeds50_ShouldThrowException() {
        // TC_Notify_050
        Notification input = new Notification(2016L, "Status too long", "C".repeat(51));

        assertThrows(DataIntegrityViolationException.class, () -> notifyRepository.saveAndFlush(input));
    }

    @SpringBootApplication
    @EntityScan(basePackages = "com.cvconnect.notifyservice.entity")
    @EnableJpaRepositories(basePackages = "com.cvconnect.notifyservice.repository")
    static class TestApplication {
    }
}

