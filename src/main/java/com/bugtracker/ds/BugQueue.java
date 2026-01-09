package com.bugtracker.ds;

import com.bugtracker.model.Bug;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Queue<Bug> for FIFO bug assignment.
 */
public class BugQueue {
    private final Queue<Bug> queue;

    public BugQueue() {
        this.queue = new LinkedList<>();
    }

    public void addBug(Bug bug) {
        queue.add(bug);
    }

    public Bug getNextBug() {
        return queue.poll();
    }

    public Bug peekNextBug() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }
}
