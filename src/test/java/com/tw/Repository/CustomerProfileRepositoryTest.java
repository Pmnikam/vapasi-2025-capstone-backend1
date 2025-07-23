package com.tw.Repository;



import com.tw.entity.CustomerProfile;
import com.tw.entity.UserAccount;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerProfileRepositoryTest {

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

        private UserAccount createUser() {
            return userAccountRepository.save(
                    new UserAccount("Surbhi", "surbhi@gmail.com", "abcdefg", "ROLE_USER")
            );
        }

        private CustomerProfile createProfile(UserAccount user) {
            return CustomerProfile.builder()
                    .dob(LocalDate.of(1995, 5, 15))
                    .mobileNo("9876543210")
                    .address("Delhi")
                    .aadharNo("123456789012")
                    .panNo("ABCDE1234F")
                    .loginAccount(user)
                    .build();
        }

        @Test
        @Order(1)
        @DisplayName("Save CustomerProfile with UserAccount")
        void shouldSaveCustomerProfile() {
            UserAccount user = createUser();
            CustomerProfile profile = createProfile(user);
            CustomerProfile saved = customerProfileRepository.save(profile);

            assertNotNull(saved.getPId());
            assertEquals("Delhi", saved.getAddress());
            assertEquals("Surbhi", saved.getLoginAccount().getName());
        }

        @Test
        @Order(2)
        @DisplayName("Find CustomerProfile by ID")
        void shouldFindById() {
            UserAccount user = createUser();
            CustomerProfile profile = customerProfileRepository.save(createProfile(user));

            Optional<CustomerProfile> found = customerProfileRepository.findById(profile.getPId());
            assertTrue(found.isPresent());
            assertEquals("9876543210", found.get().getMobileNo());
        }

        @Test
        @Order(3)
        @DisplayName("Update CustomerProfile address")
        void shouldUpdateProfile() {
            UserAccount user = createUser();
            CustomerProfile profile = customerProfileRepository.save(createProfile(user));

            profile.setAddress("Mumbai");
            CustomerProfile updated = customerProfileRepository.save(profile);

            assertEquals("Mumbai", updated.getAddress());
        }

    }


