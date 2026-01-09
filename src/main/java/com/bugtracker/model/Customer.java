package com.bugtracker.model;

public class Customer extends User {
    public Customer() {
        super();
        setRole("CUSTOMER");
    }

    public Customer(int id, String username, String password, String fullName, String email) {
        super(id, username, password, fullName, email, "CUSTOMER");
    }
}
