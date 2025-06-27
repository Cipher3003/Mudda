package com.mudda.backend.postgres.repositories;

import com.mudda.backend.postgres.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findByPhoneNumber(String phoneNumber);
}
