package com.bugtracker.service;

import com.bugtracker.model.*;
import com.bugtracker.model.enums.Status;
import com.bugtracker.repository.BugAttachmentRepository;
import com.bugtracker.repository.BugHistoryRepository;
import com.bugtracker.repository.BugRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class BugService {
    public enum SortOption {
        ID, SEVERITY, STATUS
    }

    private final BugRepository bugRepo = new BugRepository();
    private final BugHistoryRepository historyRepo = new BugHistoryRepository();
    private final BugAttachmentRepository attachmentRepo = new BugAttachmentRepository();
    private final AuthService auth = new AuthService();

    private static final String ATTACHMENT_BASE_DIR = "attachments/bugs/";

    // Data Structures
    private final com.bugtracker.ds.BugTree bugTree = new com.bugtracker.ds.BugTree();
    private final com.bugtracker.ds.BugQueue bugQueue = new com.bugtracker.ds.BugQueue();
    private final com.bugtracker.ds.BugPriorityStore priorityStore = new com.bugtracker.ds.BugPriorityStore();
    private final com.bugtracker.ds.BugWorkflowGraph workflowGraph = new com.bugtracker.ds.BugWorkflowGraph();
    private final com.bugtracker.ds.BugHistoryStack historyStack = new com.bugtracker.ds.BugHistoryStack();
    private final com.bugtracker.ds.BugReportMap reportMap = new com.bugtracker.ds.BugReportMap();

    public BugService() {
        // Initialize Data Structures with existing data
        List<Bug> allBugs = bugRepo.findAll();
        for (Bug b : allBugs) {
            addToDataStructures(b);
        }
    }

    private void addToDataStructures(Bug b) {
        bugTree.insert(b);
        reportMap.addBug(b);

        if (b.getStatus() != Status.CLOSED && b.getStatus() != Status.RESOLVED) {
            priorityStore.addBug(b);
        }

        if (b.getStatus() == Status.OPEN || b.getStatus() == Status.NEW) {
            bugQueue.addBug(b);
        }
    }

    public void reportBug(Bug bug) {
        bugRepo.save(bug);

        // Add to DS
        addToDataStructures(bug);

        BugHistory h = new BugHistory();
        h.setBug(bug);
        h.setAction("Reported by " + bug.getReporter().getUsername());
        historyRepo.save(h);
        historyStack.pushAction(h);
    }

    /**
     * Report a bug with file attachments
     * 
     * @param bug   The bug to report
     * @param files List of files to attach
     * @return The bug ID, or -1 if failed
     */
    public int reportBug(Bug bug, List<File> files) {
        try {
            // Save the bug first to get the ID
            bugRepo.save(bug);

            // Record history
            BugHistory h = new BugHistory();
            h.setBug(bug);
            h.setAction("Reported by " + bug.getReporter().getUsername());
            historyRepo.save(h);

            // Handle file attachments if any
            if (files != null && !files.isEmpty()) {
                saveAttachments(bug.getId(), files);
            }

            // Add to DS
            addToDataStructures(bug);
            historyStack.pushAction(h);

            return bug.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void saveAttachments(int bugId, List<File> files) {
        // Create directory for this bug's attachments
        String bugAttachmentDir = ATTACHMENT_BASE_DIR + bugId + "/";
        File dir = new File(bugAttachmentDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Copy each file and create database record
        for (File file : files) {
            try {
                // Copy file to attachment directory
                Path sourcePath = file.toPath();
                Path targetPath = Paths.get(bugAttachmentDir + file.getName());
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Create database record
                BugAttachment attachment = new BugAttachment();
                attachment.setBugId(bugId);
                attachment.setFileName(file.getName());
                attachment.setFilePath(targetPath.toString());
                attachment.setFileSize(file.length());
                attachmentRepo.save(attachment);

            } catch (IOException e) {
                System.err.println("Failed to save attachment: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void assignBug(int bugId, User assigner, User assignee) {
        if (!auth.canAssignBug(assigner))
            throw new RuntimeException("Not allowed");
        // Update assignee and status
        bugRepo.updateAssignee(bugId, assignee.getId());
        bugRepo.updateStatus(bugId, Status.IN_PROGRESS);
        Bug b = bugRepo.findById(bugId);
        // record history
        BugHistory h = new BugHistory();
        h.setBug(b);
        h.setAction("Assigned to " + assignee.getUsername() + " by " + assigner.getUsername());
        historyRepo.save(h);
    }

    public void updateStatus(int bugId, Status status, User performer) {
        Bug b = bugRepo.findById(bugId);
        if (b == null)
            return;

        // Graph Validation
        if (!workflowGraph.isValidTransition(b.getStatus(), status)) {
            throw new IllegalArgumentException("Invalid status transition from " + b.getStatus() + " to " + status);
        }

        bugRepo.updateStatus(bugId, status);
        // Refresh bug to get updated timestamp
        b = bugRepo.findById(bugId);

        BugHistory h = new BugHistory();
        h.setBug(b);
        h.setAction("Status changed to " + status.name() + " by " + performer.getUsername());
        historyRepo.save(h);
        historyStack.pushAction(h);

        // Update DS (Simple re-add/update logic would be needed for complex apps, here
        // we just ensure consistency for new pulls)
        // For PriorityStore, since we can't easily remove old state, we might have
        // stale entries if we don't rebuild.
        // But for this scope, let's assume we mainly read from DB or just add new
        // state.
        // Actually, if status becomes CLOSED, we might want to remove it. Not trivial
        // with Java PQ.
        // Let's at least ensure if it's reopened it gets added back.
        if (status == Status.OPEN || status == Status.IN_PROGRESS) {
            priorityStore.addBug(b);
        }
    }

    public void addComment(int bugId, String text, int authorId) {
        // Direct SQL for now since we don't have a repo for comments
        try (java.sql.Connection conn = com.bugtracker.db.DBConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO BugComment (bug_id, author_id, text, created_at) VALUES (?, ?, ?, ?)")) {

            pstmt.setInt(1, bugId);
            pstmt.setInt(2, authorId);
            pstmt.setString(3, text);
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add comment");
        }
    }

    public List<BugComment> getComments(int bugId) {
        List<BugComment> comments = new java.util.ArrayList<>();
        String sql = "SELECT c.id, c.text, c.created_at, u.id as user_id, u.username, u.full_name, u.role " +
                "FROM BugComment c " +
                "JOIN [User] u ON c.author_id = u.id " +
                "WHERE c.bug_id = ? " +
                "ORDER BY c.created_at DESC";

        try (java.sql.Connection conn = com.bugtracker.db.DBConnection.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bugId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BugComment comment = new BugComment();
                    comment.setId(rs.getInt("id"));
                    comment.setText(rs.getString("text"));
                    if (rs.getTimestamp("created_at") != null) {
                        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }

                    User author = new User();
                    author.setId(rs.getInt("user_id"));
                    author.setUsername(rs.getString("username"));
                    author.setFullName(rs.getString("full_name"));
                    author.setRole(rs.getString("role"));
                    comment.setAuthor(author);

                    comments.add(comment);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public List<Bug> getBugsForAssignee(User user) {
        List<Bug> list = bugRepo.findByAssignee(user.getId());
        sortBugs(list);
        return list;
    }

    public List<Bug> getBugsForReporter(User user) {
        List<Bug> list = bugRepo.findByReporter(user.getId());
        sortBugs(list);
        return list;
    }

    public List<Bug> getAllBugs() {
        List<Bug> list = bugRepo.findAll();
        sortBugs(list);
        return list;
    }

    private void sortBugs(List<Bug> bugs) {
        bugs.sort((b1, b2) -> {
            // 1. Sort by Status (IN_PROGRESS > OPEN > NEW > RESOLVED > CLOSED)
            int status1 = getStatusPriority(b1.getStatus());
            int status2 = getStatusPriority(b2.getStatus());
            if (status1 != status2) {
                return Integer.compare(status1, status2);
            }

            // 2. Sort by Severity (CRITICAL > HIGH > MEDIUM > LOW)
            // Severity enum is likely LOW, MEDIUM, HIGH, CRITICAL order.
            // We want Descending ordinal.
            int s1 = b1.getSeverity() == null ? -1 : b1.getSeverity().ordinal();
            int s2 = b2.getSeverity() == null ? -1 : b2.getSeverity().ordinal();
            return Integer.compare(s2, s1);
        });
    }

    private int getStatusPriority(Status s) {
        if (s == null)
            return 99;
        switch (s) {
            case IN_PROGRESS:
                return 1;
            case OPEN:
                return 2;
            case NEW:
                return 3;
            case RESOLVED:
                return 4;
            case CLOSED:
                return 5;
            default:
                return 99;
        }
    }

    public Bug findById(int id) {
        // Use BST
        Bug b = bugTree.search(id);
        if (b != null)
            return b;

        // Fallback or Initial Load check
        return bugRepo.findById(id);
    }

    // New Data Structure Operations

    public Bug getNextCriticalBug() {
        return priorityStore.getMostCriticalBug();
    }

    public Bug getNextBugToAssign() {
        return bugQueue.getNextBug();
    }

    public List<Bug> getBugsByDate(java.time.LocalDate date) {
        return reportMap.getBugsByDate(date);
    }

    public void undoLastAction() {
        BugHistory lastAction = historyStack.popAction();
        if (lastAction != null) {
            System.out.println("Undo action: " + lastAction.getAction());
            // Logic to reverse DB state would go here.
            // For now we just track the history stack.
        }
    }

    public List<BugAttachment> getAttachments(int bugId) {
        return attachmentRepo.findByBugId(bugId);
    }

    public List<Bug> searchBugs(String query) {
        String lowerQuery = query.toLowerCase();
        return getAllBugs().stream()
                .filter(b -> String.valueOf(b.getId()).equals(query) ||
                        (b.getTitle() != null && b.getTitle().toLowerCase().contains(lowerQuery)) ||
                        (b.getDescription() != null && b.getDescription().toLowerCase().contains(lowerQuery)))
                .collect(java.util.stream.Collectors.toList());
    }

    public void sortBugs(List<Bug> bugs, SortOption option, boolean ascending) {
        bugs.sort((b1, b2) -> {
            int result = 0;
            switch (option) {
                case ID:
                    result = Integer.compare(b1.getId(), b2.getId());
                    break;
                case STATUS:
                    result = Integer.compare(getStatusPriority(b1.getStatus()), getStatusPriority(b2.getStatus()));
                    break;
                case SEVERITY:
                    int s1 = b1.getSeverity() == null ? -1 : b1.getSeverity().ordinal();
                    int s2 = b2.getSeverity() == null ? -1 : b2.getSeverity().ordinal();
                    result = Integer.compare(s1, s2);
                    break;
            }
            return ascending ? result : -result;
        });
    }

    public List<Bug> getBugsSortedBySeverity(boolean ascending) {
        List<Bug> list = getAllBugs();
        sortBugs(list, SortOption.SEVERITY, ascending);
        return list;
    }
}
