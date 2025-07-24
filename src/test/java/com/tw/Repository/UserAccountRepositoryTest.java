package com.tw.Repository;

import com.tw.entity.UserAccount;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccount createTestUser(String email) {
        return new UserAccount("Surbhi", email, "abcdefg", "ROLE_USER");
    }

    @Test
    void shouldSaveUserSuccessfully() {
        UserAccount user = createTestUser("surbhi@gmail.com");
        UserAccount saved = userAccountRepository.save(user);
        assertNotNull(saved.getLoginId());
        assertEquals("Surbhi", saved.getName());
    }

    @Test
    void shouldFindUserByEmail() {
        UserAccount user = createTestUser("testuser@gmail.com");
        userAccountRepository.save(user);

        Optional<UserAccount> found = userAccountRepository.findByEmail("testuser@gmail.com");
        assertTrue(found.isPresent());
        assertEquals("testuser@gmail.com", found.get().getEmail());
    }

    @Test
    void shouldThrowExceptionForDuplicateEmail() {
        UserAccount user1 = createTestUser("duplicate@gmail.com");
        UserAccount user2 = createTestUser("duplicate@gmail.com");

        userAccountRepository.save(user1);
        assertThrows(Exception.class, () -> {
            userAccountRepository.saveAndFlush(user2);
        });
    }

    @Test
    void shouldUpdateUserNameSuccessfully() {
        UserAccount user = createTestUser("updateuser@gmail.com");
        UserAccount saved = userAccountRepository.save(user);

        saved.setName("Updated Name");
        UserAccount updated = userAccountRepository.save(saved);

        assertEquals("Updated Name", updated.getName());
    }


}
