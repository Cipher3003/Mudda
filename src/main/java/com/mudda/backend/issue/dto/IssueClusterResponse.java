package com.mudda.backend.issue.dto;

import java.util.List;

public record IssueClusterResponse(
        List<IssueClusterDTO> clusters
) {
}
