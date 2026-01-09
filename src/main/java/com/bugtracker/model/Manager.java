package com.bugtracker.model;

public class Manager extends User {
    public Manager() {
        super();
        setRole("MANAGER");
    }

    public Manager(int id, String username, String password, String fullName, String email) {
        super(id, username, password, fullName, email, "MANAGER");
    }
}
