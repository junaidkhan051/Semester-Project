package com.bugtracker.ds;

import com.bugtracker.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList<Product> for product storage.
 */
public class ProductStore {
    private final List<Product> products;

    public ProductStore() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public Product getProduct(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public void clear() {
        products.clear();
    }
}
