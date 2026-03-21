package com.mudda.backend.initiative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InitiativeParticipantRepository extends JpaRepository<InitiativeParticipant, Long> {

    Optional<InitiativeParticipant> findByUserIdAndInitiativeId(Long userId, Long initiativeId);

    boolean existsByUserIdAndInitiativeId(Long userId, Long initiativeId);

    List<InitiativeParticipant> findByInitiativeId(Long initiativeId);

    long countByInitiativeId(Long initiativeId);
}
