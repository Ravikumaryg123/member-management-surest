package com.surest.memberapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.memberapp.entity.Member;
import com.surest.memberapp.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetMembers() throws Exception {
        Member member = new Member(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);
        Page<Member> page = new PageImpl<>(java.util.List.of(member));
        Mockito.when(memberService.getMembers(anyInt(), anyInt(), anyString(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetMemberById() throws Exception {
        UUID id = UUID.randomUUID();
        Member member = new Member(id,"John","Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);
        Mockito.when(memberService.getMember(id)).thenReturn(member);

        mockMvc.perform(get("/members/{id}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateMember() throws Exception {
        Member member = new Member(null,"John","Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);
        Member savedMember = new Member(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);

        Mockito.when(memberService.createMember(any(Member.class))).thenReturn(savedMember);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateMember() throws Exception {
        UUID id = UUID.randomUUID();
        Member member = new Member(null,"John", "Smith", LocalDate.of(1990,1,1), "john@example.com", null, null);
        Member updatedMember = new Member(id, "John", "Smith", LocalDate.of(1990,1,1), "john@example.com", null, null);

        Mockito.when(memberService.updateMember(eq(id), any(Member.class))).thenReturn(updatedMember);

        mockMvc.perform(put("/members/{id}", id.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteMember() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(memberService).deleteMember(id);

        mockMvc.perform(delete("/members/{id}", id.toString()))
                .andExpect(status().isNoContent());
    }
}
