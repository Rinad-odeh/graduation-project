package com.baligh.backend.util;

import com.baligh.backend.exception.InvalidStatusTransitionException;
import com.baligh.backend.model.enums.IssueStatus;

import java.util.Map;
import java.util.Set;

public class IssueStatusValidator {

    private static final Map<IssueStatus, Set<IssueStatus>> ALLOWED_TRANSITIONS = Map.of(
        IssueStatus.SUBMITTED,     Set.of(IssueStatus.UNDER_REVIEW),
        IssueStatus.UNDER_REVIEW,  Set.of(IssueStatus.IN_PROGRESS, IssueStatus.REJECTED),
        IssueStatus.IN_PROGRESS,   Set.of(IssueStatus.ON_HOLD, IssueStatus.RESOLVED),
        IssueStatus.ON_HOLD,       Set.of(IssueStatus.IN_PROGRESS, IssueStatus.REJECTED),
        IssueStatus.RESOLVED,      Set.of(),
        IssueStatus.REJECTED,      Set.of()
    );

    public static void validate(IssueStatus from, IssueStatus to) {
        Set<IssueStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidStatusTransitionException(from, to);
        }
    }
}
