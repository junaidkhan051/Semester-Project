package com.bugtracker.ds;

import com.bugtracker.model.Bug;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * TreeMap<LocalDate, List<Bug>> for date-wise sorted reports.
 */
public class BugReportMap {
    private final TreeMap<LocalDate, List<Bug>> map;

    public BugReportMap() {
        this.map = new TreeMap<>();
    }

    public void addBug(Bug bug) {
        if (bug.getCreatedAt() != null) {
            LocalDate date = bug.getCreatedAt().toLocalDate();
            map.computeIfAbsent(date, k -> new ArrayList<>()).add(bug);
        }
    }

    public List<Bug> getBugsByDate(LocalDate date) {
        return map.getOrDefault(date, new ArrayList<>());
    }

    public TreeMap<LocalDate, List<Bug>> getReport() {
        return map;
    }

    public void clear() {
        map.clear();
    }
}
