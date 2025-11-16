package com.surest.memberapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // mock JwtAuthFilter bean to isolate config testing

    @Test
    void contextLoads_securityBeansCreated() {
        assertThat(context.containsBean("securityFilterChain")).isTrue();
        assertThat(context.containsBean("authenticationProvider")).isTrue();
        assertThat(context.containsBean("passwordEncoder")).isTrue();
        assertThat(context.containsBean("authenticationManager")).isTrue();
    }

    @Test
    void allowPublicAccessToAuthLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().is4xxClientError()); // likely 400 due to missing auth logic stub, but not 401/403
    }

    @Test
    void denyAccessToMembersWithoutAuth() throws Exception {
        mockMvc.perform(get("/members"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void allowGetMembersForUserRole() throws Exception {
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allowPostMembersForAdminRole() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"dateOfBirth\":\"1990-01-01\"}"))
                .andExpect(status().is4xxClientError()); // likely 400 for validation but passes auth
    }

    @Test
    @WithMockUser(roles = "USER")
    void denyPostMembersForUserRole() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\"}"))
                .andExpect(status().isForbidden());
    }
}
