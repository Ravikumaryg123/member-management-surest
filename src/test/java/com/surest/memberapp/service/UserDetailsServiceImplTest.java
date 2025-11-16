package com.surest.memberapp.service;

import com.surest.memberapp.entity.Role;
import com.surest.memberapp.entity.User;
import com.surest.memberapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPasswordHash("hashedpass");
        mockUser.setRole(role);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("hashedpass");
        assertThat(userDetails.getAuthorities())
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        verify(userRepository).findByUsername("admin");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });

        verify(userRepository).findByUsername("unknown");
    }
}

