package com.bugtracker.service;

import com.bugtracker.model.User;

public class AuthService {
    public boolean canAssignBug(User user) {
        if (user == null)
            return false;
        String r = user.getRole();
        return "ADMIN".equals(r) || "MANAGER".equals(r);
    }

    public boolean canManageSystem(User user) {
        if (user == null)
            return false;
        return "ADMIN".equals(user.getRole());
    }

    public boolean canAddProduct(User user) {
        if (user == null)
            return false;
        return "ADMIN".equals(user.getRole()) || "MANAGER".equals(user.getRole());
    }

    public boolean canManageProducts(User user) {
        if (user == null)
            return false;
        return "ADMIN".equals(user.getRole());
    }

    public boolean canUpdateStatus(User user) {
        if (user == null)
            return false;
        String r = user.getRole();
        // Admin removed from status updates as requested
        return "MANAGER".equals(r) || "TECH_PERSON".equals(r);
    }

    public boolean canAddComment(User user) {
        if (user == null)
            return false;
        // Only Tech Person can add comments
        return "TECH_PERSON".equals(user.getRole());
    }

    public boolean canViewComments(User user) {
        if (user == null)
            return false;
        String r = user.getRole();
        return "ADMIN".equals(r) || "MANAGER".equals(r) || "TECH_PERSON".equals(r) || "CUSTOMER".equals(r);
    }

    public boolean canReportBug(User user) {
        if (user == null)
            return false;
        return "CUSTOMER".equals(user.getRole());
    }

    public boolean canViewStatus(User user) {
        if (user == null)
            return false;
        return "CUSTOMER".equals(user.getRole());
    }
}
