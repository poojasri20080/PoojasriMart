package com.poojamart.service;

import com.poojamart.model.Product;
import com.poojamart.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getBestSellers() {
        return productRepository.findByBestSellerTrue();
    }

    public List<Product> getNewArrivals() {
        return productRepository.findByNewArrivalTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getRelatedProducts(Long categoryId, Long productId) {
        return productRepository.findTop8ByCategoryIdAndIdNot(categoryId, productId);
    }

    public Page<Product> searchAndFilterProducts(
            Long categoryId,
            String keyword,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // default: newest

        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else if ("popularity".equalsIgnoreCase(sortBy) || "rating".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "reviewCount"));
        } else if ("newest".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), size > 0 ? size : 20, sort);
        return productRepository.searchProducts(categoryId, keyword, minPrice, maxPrice, pageable);
    }

    public Product saveProduct(Product product) {
        if (product.getOriginalPrice() != null && product.getOriginalPrice() > product.getPrice()) {
            product.setDiscountPercent((int) Math.round(((product.getOriginalPrice() - product.getPrice()) / product.getOriginalPrice()) * 100));
        } else {
            product.setDiscountPercent(0);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setOriginalPrice(updatedProduct.getOriginalPrice());
        if (updatedProduct.getOriginalPrice() != null && updatedProduct.getOriginalPrice() > updatedProduct.getPrice()) {
            existing.setDiscountPercent((int) Math.round(((updatedProduct.getOriginalPrice() - updatedProduct.getPrice()) / updatedProduct.getOriginalPrice()) * 100));
        } else {
            existing.setDiscountPercent(0);
        }
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        if (updatedProduct.getCategory() != null) {
            existing.setCategory(updatedProduct.getCategory());
        }
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setAdditionalImages(updatedProduct.getAdditionalImages());
        existing.setBrand(updatedProduct.getBrand());
        existing.setAgeGroup(updatedProduct.getAgeGroup());
        existing.setSpecifications(updatedProduct.getSpecifications());
        existing.setFeatured(updatedProduct.getFeatured());
        existing.setBestSeller(updatedProduct.getBestSeller());
        existing.setNewArrival(updatedProduct.getNewArrival());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public long getTotalProductCount() {
        return productRepository.count();
    }
}
