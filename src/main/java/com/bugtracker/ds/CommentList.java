package com.bugtracker.ds;

import com.bugtracker.model.BugComment;
import java.util.LinkedList;
import java.util.List;

/**
 * LinkedList<BugComment> for ordered bug discussions.
 */
public class CommentList {
    private final LinkedList<BugComment> comments;

    public CommentList() {
        this.comments = new LinkedList<>();
    }

    public void addComment(BugComment comment) {
        comments.add(comment);
    }

    public List<BugComment> getAllComments() {
        return new LinkedList<>(comments);
    }

    public void clear() {
        comments.clear();
    }
}
