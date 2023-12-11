package com.example.demo.controllers;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerForTest {


    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository",userRepository);
        TestUtils.injectObjects(userController, "cartRepository",cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder",encoder);


    }

    @Test
    public void createUserTest () throws Exception {
        when(encoder.encode("hellohello")).thenReturn("PassEncode");
        CreateUserRequest createUser = new CreateUserRequest();
        createUser.setUsername("hello");
        createUser.setPassword("hellohello");
        createUser.setConfirmPassword("hellohello");

        final ResponseEntity<User> response = userController.createUser(createUser);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("hello",user.getUsername());
        assertEquals("PassEncode",user.getPassword());
    }

    @Test
    public void findUserByIdTest() throws Exception {
        CreateUserRequest createUser = new CreateUserRequest();
        createUser.setUsername("hello");
        createUser.setPassword("hellohello");
        createUser.setConfirmPassword("hellohello");

        final ResponseEntity<User> response = userController.createUser(createUser);

        // Extract user ID from the created user
        Long userId = response.getBody().getId();
        assertNotNull(userId);

        // Mock the behavior of userRepository.findById
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(response.getBody()));

        final ResponseEntity<User> response2 = userController.findById(userId);
        assertNotNull(response2);
        assertEquals(200, response2.getStatusCodeValue());

        User user = response2.getBody();
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("hello", user.getUsername());
    }
}



