package com.bugtracker.ds;

import com.bugtracker.model.enums.Status;
import java.util.*;

/**
 * Directed Graph for Bug Lifecycle:
 * REPORTED -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
 */
public class BugWorkflowGraph {
    private final Map<Status, List<Status>> adjList;

    public BugWorkflowGraph() {
        adjList = new HashMap<>();
        initializeGraph();
    }

    private void initializeGraph() {
        // Define ALL possible states
        for (Status s : Status.values()) {
            adjList.put(s, new ArrayList<>());
        }

        // Define valid transitions
        // NEW -> OPEN (Reported -> Accepted)
        addTransition(Status.NEW, Status.OPEN);

        // OPEN -> IN_PROGRESS
        addTransition(Status.OPEN, Status.IN_PROGRESS);

        // IN_PROGRESS -> RESOLVED
        addTransition(Status.IN_PROGRESS, Status.RESOLVED);

        // RESOLVED -> CLOSED
        addTransition(Status.RESOLVED, Status.CLOSED);

        // RESOLVED -> IN_PROGRESS (Reopened)
        addTransition(Status.RESOLVED, Status.IN_PROGRESS);
    }

    // Helper to add edge
    public void addTransition(Status from, Status to) {
        if (!adjList.containsKey(from))
            adjList.put(from, new ArrayList<>());
        adjList.get(from).add(to);
    }

    public boolean isValidTransition(Status current, Status next) {
        // pure graph traversal check (BFS/DFS not strictly needed for direct neighbor
        // check,
        // but "Reachability" might be what they mean by traversal.
        // For simple state machine, checking adjacency is enough.
        // But I will implement a BFS reachability check just to satisfy "Graph
        // Traversal" requirement if needed for more complex flows.
        // Here, Is 'next' a direct neighbor of 'current'?
        if (current == null || next == null)
            return false;
        if (current == next)
            return true; // Self transition usually ok or no-op

        List<Status> neighbors = adjList.get(current);
        return neighbors != null && neighbors.contains(next);
    }

    // BFS to warn if a state is reachable at all (sanity check algo)
    public boolean isReachable(Status start, Status end) {
        if (start == end)
            return true;
        Set<Status> visited = new HashSet<>();
        Queue<Status> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Status curr = queue.poll();
            if (curr == end)
                return true;

            for (Status neighbor : adjList.getOrDefault(curr, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }
}
