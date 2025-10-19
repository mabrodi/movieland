package org.dimchik.service.base;

import org.dimchik.dto.UserAuthDTO;
import org.dimchik.entity.User;
import org.dimchik.repository.UserRepository;
import org.dimchik.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceBaseTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceBase(userRepository);
    }


    @Test
    void findAndValidateShouldReturnUserWhenCredentialAndValid() {
        String email = "john@example.com";
        String password = "password123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setId(1L);
        user.setName("John Doe");

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserAuthDTO result = userService.authenticate(email, password);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        UserService service = new UserServiceBase(userRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.authenticate(email, "anyPassword")
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowWhenPasswordIsIncorrect() {
        String email = "john@example.com";
        String correctPassword = "correct";
        String incorrectPassword = "wrong";

        User user = new User();
        user.setEmail(email);
        user.setPassword(correctPassword);

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserService service = new UserServiceBase(userRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.authenticate(email, incorrectPassword)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }
}