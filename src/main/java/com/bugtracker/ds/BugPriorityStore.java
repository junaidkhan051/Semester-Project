package com.bugtracker.ds;

import com.bugtracker.model.Bug;
import com.bugtracker.model.enums.Severity;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * PriorityQueue<Bug> (Heap) for severity-based bug handling.
 */
public class BugPriorityStore {
    private final PriorityQueue<Bug> pq;

    public BugPriorityStore() {
        // Comparator: CRITICAL < HIGH < MEDIUM < LOW (Enum order might be LOW, MEDIUM,
        // HIGH, CRITICAL)
        // Checking Severity enum needed, usually enums are comparable by ordinal.
        // Assuming typical Severity enum: LOW, MEDIUM, HIGH, CRITICAL.
        // We want Higher ordinal first (descending order).

        Comparator<Bug> severityComparator = (b1, b2) -> {
            int s1 = b1.getSeverity() == null ? -1 : b1.getSeverity().ordinal();
            int s2 = b2.getSeverity() == null ? -1 : b2.getSeverity().ordinal();
            // Compare severity (Descending)
            if (s1 != s2) {
                return Integer.compare(s2, s1);
            }
            // If same severity, use ID or Creation time (Ascending - FIFO for same
            // priority)
            return Integer.compare(b1.getId(), b2.getId());
        };

        this.pq = new PriorityQueue<>(severityComparator);
    }

    public void addBug(Bug bug) {
        pq.offer(bug);
    }

    public Bug getMostCriticalBug() {
        return pq.poll();
    }

    public Bug peekMostCriticalBug() {
        return pq.peek();
    }

    public boolean isEmpty() {
        return pq.isEmpty();
    }

    public void clear() {
        pq.clear();
    }
}
