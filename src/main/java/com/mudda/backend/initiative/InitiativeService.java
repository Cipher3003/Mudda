package com.mudda.backend.initiative;

import com.mudda.backend.initiative.dto.CreateInitiativeRequest;
import com.mudda.backend.initiative.dto.HubResponse;
import com.mudda.backend.initiative.dto.InitiativeResponse;
import com.mudda.backend.initiative.dto.ParticipateRequest;

public interface InitiativeService {

    HubResponse getCommunityHub(Long communityId);

    InitiativeResponse createInitiative(Long communityId, CreateInitiativeRequest request);

    InitiativeResponse participate(Long initiativeId, Long userId, ParticipateRequest request);
}
