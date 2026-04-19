package com.baligh.backend.exception;

import com.baligh.backend.model.enums.IssueStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(IssueStatus from, IssueStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}
