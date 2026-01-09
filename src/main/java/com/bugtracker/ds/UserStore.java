package com.bugtracker.ds;

import com.bugtracker.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * HashMap<Integer, User> for user authentication and role-based access.
 */
public class UserStore {
    private final Map<Integer, User> userMap;

    public UserStore() {
        this.userMap = new HashMap<>();
    }

    public void addUser(User user) {
        if (user != null) {
            userMap.put(user.getId(), user);
        }
    }

    public User getUser(int id) {
        return userMap.get(id);
    }

    public User getUserByUsername(String username) {
        // Linear search needed if we only have ID map, or we could maintain a secondary
        // index
        for (User u : userMap.values()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public void clear() {
        userMap.clear();
    }
}
