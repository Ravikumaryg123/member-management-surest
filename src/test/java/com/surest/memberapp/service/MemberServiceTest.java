package com.surest.memberapp.service;

import com.surest.memberapp.entity.Member;
import com.surest.memberapp.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final MemberService memberService = new MemberService(memberRepository);

    @Test
    void testGetMemberSuccess() {
        UUID id = UUID.randomUUID();
        Member member = new Member(id,"John","Doe",LocalDate.of(1990,1,1),"john@example.com",null,null);

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        Member fetched = memberService.getMember(id);
        assertEquals("John", fetched.getFirstName());
    }

    @Test
    void testGetMemberNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> memberService.getMember(id));
    }

    @Test
    void testCreateMember() {
        Member member = new Member(null,"John", "Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);
        Member saved = new Member(UUID.randomUUID(),"John", "Doe", LocalDate.of(1990,1,1), "john@example.com", null, null);

        when(memberRepository.save(any(Member.class))).thenReturn(saved);

        Member result = memberService.createMember(member);
        assertNotNull(result.getId());
    }

    @Test
    void testDeleteMemberExists() {
        UUID id = UUID.randomUUID();
        when(memberRepository.existsById(id)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(id);

        assertDoesNotThrow(() -> memberService.deleteMember(id));
        verify(memberRepository).deleteById(id);
    }
}

