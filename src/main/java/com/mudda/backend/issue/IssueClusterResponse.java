package com.mudda.backend.issue;

import java.util.List;

public record IssueClusterResponse(
        List<IssueClusterDTO> clusters
) {
}
