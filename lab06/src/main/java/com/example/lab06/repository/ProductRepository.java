package com.example.lab06.repository;

import com.example.lab06.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategory_Id(Integer categoryId);
}

