/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueCategory
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum IssueCategory {
    INF("Infrastructure"),
    SAN("Sanitation"),
    ELE("Electricity");

    private final String label;

    IssueCategory(String label) {
        this.label = label;
    }

    public String getCode() {
        return name();
    }

    public static IssueCategory fromCode(String code) {
        if (code == null) return null;
        return IssueCategory.valueOf(code);
    }

}
