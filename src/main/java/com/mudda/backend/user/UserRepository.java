package com.mudda.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MuddaUser, Long>, JpaSpecificationExecutor<MuddaUser> {

    Optional<MuddaUser> findByUsername(String username);

    Optional<MuddaUser> findByEmail(String email);

    boolean existsByUsername(String userName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
