package com.example.atc.domain.user.repository;

import com.example.atc.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByMemberId(@Param("memberId") String memberId);
    boolean existsByMemberIdAndUserPw(@Param("memberId") String memberId, @Param("userPw") String userPw);
    boolean existsByMemberId(@Param("memberId") String memberId);
}
