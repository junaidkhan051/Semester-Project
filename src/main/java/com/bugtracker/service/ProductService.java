package com.bugtracker.service;

import com.bugtracker.model.Product;
import com.bugtracker.repository.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository repo = new ProductRepository();
    private final com.bugtracker.ds.ProductStore productStore = new com.bugtracker.ds.ProductStore();

    public ProductService() {
        repo.findAll().forEach(productStore::addProduct);
    }

    public List<Product> getAllProducts() {
        return productStore.getAllProducts();
    }

    public Product findById(int id) {
        return productStore.getProduct(id);
    }

    public boolean addProduct(Product product) {
        boolean saved = repo.save(product);
        if (saved) {
            productStore.addProduct(product);
        }
        return saved;
    }

    public boolean removeProduct(int productId) {
        boolean deleted = repo.deleteById(productId);
        if (deleted) {
            Product p = productStore.getProduct(productId);
            if (p != null)
                productStore.removeProduct(p);
        }
        return deleted;
    }
}
