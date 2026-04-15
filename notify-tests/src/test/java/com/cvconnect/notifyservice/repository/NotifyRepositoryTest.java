package com.cvconnect.notifyservice.repository;

import com.cvconnect.notifyservice.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(NotifyRepositoryTest.TestApplication.class)
class NotifyRepositoryTest {

    @Autowired
    private NotifyRepository notifyRepository;

    @Test
    void testSave_WhenValidData_ShouldReturnSuccess() {
        // TC_Notify_02
        Notification input = new Notification(2002L, "Profile approved", "NEW");

        Notification saved = notifyRepository.save(input);

        // CheckDB: verify the record can be read back from DB after save.
        Optional<Notification> loadedFromDb = notifyRepository.findById(saved.getId());

        assertTrue(loadedFromDb.isPresent());
        assertEquals(2002L, loadedFromDb.get().getUserId());
        assertEquals("Profile approved", loadedFromDb.get().getMessage());
        assertEquals("NEW", loadedFromDb.get().getStatus());
    }

    @SpringBootApplication(scanBasePackages = "com.cvconnect.notifyservice")
    static class TestApplication {
    }
}

