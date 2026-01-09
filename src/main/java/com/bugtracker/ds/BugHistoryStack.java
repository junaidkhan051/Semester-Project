package com.bugtracker.ds;

import com.bugtracker.model.BugHistory;
import java.util.Stack;

/**
 * Stack<BugHistory> for undo / tracking bug status history.
 */
public class BugHistoryStack {
    private final Stack<BugHistory> stack;

    public BugHistoryStack() {
        this.stack = new Stack<>();
    }

    public void pushAction(BugHistory history) {
        stack.push(history);
    }

    public BugHistory popAction() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }

    public BugHistory peekAction() {
        if (!stack.isEmpty()) {
            return stack.peek();
        }
        return null;
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
