package com.mudda.backend.initiative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InitiativeRepository extends JpaRepository<CommunityInitiative, Long> {

    List<CommunityInitiative> findByCommunityIdAndActiveTrue(Long communityId);

    List<CommunityInitiative> findByCommunityId(Long communityId);

    long countByCommunityIdAndActiveTrue(Long communityId);
}
