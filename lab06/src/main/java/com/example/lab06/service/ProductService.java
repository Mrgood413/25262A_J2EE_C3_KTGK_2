package com.example.lab06.service;

import com.example.lab06.model.Product;
import com.example.lab06.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Page<Product> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> findByCategoryId(Integer categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    public List<Product> getAllSortedByPrice(String direction) {
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by("price").descending()
                : Sort.by("price").ascending();
        return productRepository.findAll(sort);
    }

    public Product get(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public void add(@NonNull Product newProduct) {
        productRepository.save(newProduct);
    }

    public void update(@NonNull Product editProduct) {
        productRepository.save(editProduct);
    }

    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public void updateImage(Product product, MultipartFile imageProduct) {
        String contentType = imageProduct.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Tep tai len khong phai la hinh anh");
        }

        if (!imageProduct.isEmpty()) {
            try {
                Path dirImages = Paths.get("src/main/resources/static/images");
                if (!Files.exists(dirImages)) {
                    Files.createDirectories(dirImages);
                }
                String originalFileName = imageProduct.getOriginalFilename();
                String extension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID() + extension;
                Path pathFileUpload = dirImages.resolve(newFileName);
                Files.copy(imageProduct.getInputStream(), pathFileUpload, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(newFileName);
            } catch (IOException e) {
                throw new RuntimeException("Khong the luu hinh anh", e);
            }
        }
    }
}

