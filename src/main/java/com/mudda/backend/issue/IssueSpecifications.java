package com.mudda.backend.issue;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class IssueSpecifications {

    public static Specification<Issue> containsText(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) return null;
            String pattern = "%" + search.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Issue> hasStatus(IssueStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Issue> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ? null : criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Issue> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null ? null : criteriaBuilder.equal(root.get("categoryId"), categoryId);
    }

    public static Specification<Issue> hasLocationId(Long locationId) {
        return (root, query, criteriaBuilder) ->
                locationId == null ? null : criteriaBuilder.equal(root.get("locationId"), locationId);
    }

    public static Specification<Issue> isUrgent(Boolean urgency) {
        return (root, query, criteriaBuilder) -> {
            if (urgency == null) return null;
            return urgency ? criteriaBuilder.isTrue(root.get("urgencyFlag"))
                    : criteriaBuilder.isFalse(root.get("urgencyFlag"));
        };
    }

    public static Specification<Issue> createdAfter(Instant date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Issue> createdBefore(Instant date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Issue> severityBetween(Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) criteriaBuilder.between(root.get("severityScore"), min, max);
            if (min != null) return criteriaBuilder.greaterThanOrEqualTo(root.get("severityScore"), min);
            return criteriaBuilder.lessThanOrEqualTo(root.get("severityScore"), max);
        };
    }
}
