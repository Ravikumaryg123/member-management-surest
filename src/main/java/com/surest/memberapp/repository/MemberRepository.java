package com.surest.memberapp.repository;

import com.surest.memberapp.entity.Member;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {}
