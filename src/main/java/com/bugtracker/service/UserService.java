package com.bugtracker.service;

import com.bugtracker.model.User;
import com.bugtracker.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository repo = new UserRepository();
    private final com.bugtracker.ds.UserStore userStore = new com.bugtracker.ds.UserStore();

    public UserService() {
        repo.findAll().forEach(userStore::addUser);
    }

    public User authenticate(String username, String password) {
        User u = userStore.getUserByUsername(username);
        if (u != null && u.getPassword().equals(password))
            return u;
        return null;
    }

    public boolean register(User user) {
        if (userStore.getUserByUsername(user.getUsername()) != null)
            return false; // Already exists

        boolean saved = repo.save(user);
        if (saved) {
            userStore.addUser(user);
        }
        return saved;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User findById(int id) {
        return repo.findById(id);
    }

    public List<User> getUsersByRole(String role) {
        return repo.findByRole(role);
    }

    public List<User> searchCustomers(String query) {
        String lowerQuery = query.toLowerCase();
        return getUsersByRole("CUSTOMER").stream()
                .filter(u -> u.getUsername().toLowerCase().contains(lowerQuery) ||
                        (u.getFullName() != null && u.getFullName().toLowerCase().contains(lowerQuery)) ||
                        (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerQuery)))
                .collect(java.util.stream.Collectors.toList());
    }
}
