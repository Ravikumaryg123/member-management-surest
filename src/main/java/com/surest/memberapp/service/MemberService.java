package com.surest.memberapp.service;

import com.surest.memberapp.entity.Member;
import com.surest.memberapp.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Page<Member> getMembers(int page, int size, String sort, String firstName, String lastName) {
        Sort sortObj = Sort.by(Sort.Order.by(sort.split(",")[0]));
        if (sort.endsWith("desc")) sortObj = sortObj.descending();
        else sortObj = sortObj.ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Member> spec = Specification.where(null);
        if (firstName != null && !firstName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        return memberRepository.findAll(spec, pageable);
    }

    public Member getMember(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    public Member createMember(Member member) {
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public Member updateMember(UUID id, Member updated) {
        Member existing = getMember(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(existing);
    }

    public void deleteMember(UUID id) {
        if (!memberRepository.existsById(id)) {
            throw new EntityNotFoundException("Member not found");
        }
        memberRepository.deleteById(id);
    }
}
