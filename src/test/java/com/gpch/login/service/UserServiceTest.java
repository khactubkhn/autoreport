package com.gpch.login.service;

import com.gpch.login.model.User;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    private UserService userServiceUnderTest;
    private User user;

    @Before
    public void setUp() {
        initMocks(this);
        userServiceUnderTest = new UserService(mockUserRepository,
                                               mockRoleRepository,
                                               mockBCryptPasswordEncoder);
        user = new User();
        user.setFirstName("lv");
        user.setLastName("Duc");
        user.setPhone("0372291297");
        
        Mockito.when(mockUserRepository.save(any()))
                .thenReturn(user);
        Mockito.when(mockUserRepository.findByUsername(anyString()))
                .thenReturn(user);
    }

    @Test
    public void testFindUserByEmail() {
        // Setup
        final String username = "test";

        // Run the test
        final User result = userServiceUnderTest.findUserByUsername(username);

        // Verify the results
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testSaveUser() {
        // Setup
        final String email = "test@test.com";
        user = new User();
        user.setFirstName("lv");
        user.setLastName("Duc");
        user.setPhone("0372291297");
        

        // Run the test
        User result = userServiceUnderTest.saveUser(user);

        // Verify the results
        assertEquals(email, result.getUsername());
    }
}
