package com.bugtracker.model;

public class TechnicalPerson extends User {
    public TechnicalPerson() {
        super();
        setRole("TECH_PERSON");
    }

    public TechnicalPerson(int id, String username, String password, String fullName, String email) {
        super(id, username, password, fullName, email, "TECH_PERSON");
    }
}
