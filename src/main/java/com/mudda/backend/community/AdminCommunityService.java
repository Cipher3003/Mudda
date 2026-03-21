package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.DashboardStatsResponse;
import com.mudda.backend.community.dto.UpdateIssueStatusRequest;
import com.mudda.backend.issue.IssueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCommunityService {

    DashboardStatsResponse getDashboard(Long communityId);

    Page<IssueResponse> getCommunityIssues(Long communityId, Pageable pageable);

    void updateIssueStatus(Long issueId, Long adminUserId, UpdateIssueStatusRequest request);

    CommunityMemberResponse verifyMember(Long communityId, Long memberId, boolean accept);
}
