package com.bugtracker.ds;

import com.bugtracker.model.Bug;

/**
 * Binary Search Tree (BST) for searching bugs by Bug ID.
 */
public class BugTree {
    private Node root;

    private class Node {
        Bug bug;
        Node left, right;

        Node(Bug bug) {
            this.bug = bug;
        }
    }

    public void insert(Bug bug) {
        root = insertRec(root, bug);
    }

    private Node insertRec(Node root, Bug bug) {
        if (root == null) {
            root = new Node(bug);
            return root;
        }

        if (bug.getId() < root.bug.getId())
            root.left = insertRec(root.left, bug);
        else if (bug.getId() > root.bug.getId())
            root.right = insertRec(root.right, bug);

        return root;
    }

    public Bug search(int bugId) {
        return searchRec(root, bugId);
    }

    private Bug searchRec(Node root, int bugId) {
        if (root == null)
            return null;
        if (root.bug.getId() == bugId)
            return root.bug;

        if (bugId < root.bug.getId())
            return searchRec(root.left, bugId);

        return searchRec(root.right, bugId);
    }

    public void clear() {
        root = null;
    }
}
