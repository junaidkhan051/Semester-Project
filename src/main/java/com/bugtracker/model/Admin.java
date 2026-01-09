package com.bugtracker.model;

public class Admin extends User {
    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int id, String username, String password, String fullName, String email) {
        super(id, username, password, fullName, email, "ADMIN");
    }
}
