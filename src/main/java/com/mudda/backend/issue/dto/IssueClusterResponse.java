package com.mudda.backend.issue.dto;

import java.util.List;

public record IssueClusterResponse(
        // TODO: inline in controller and remove DTO
        List<IssueClusterDTO> clusters
) {
}
