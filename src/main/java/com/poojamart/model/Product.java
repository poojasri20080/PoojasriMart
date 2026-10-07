package com.poojamart.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double price;

    private Double originalPrice;

    private Integer discountPercent;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 1000, nullable = false)
    private String imageUrl;

    @Column(length = 4000)
    private String additionalImages; // JSON or comma-separated list of image URLs

    private Double rating;

    private Integer reviewCount;

    private String brand;

    private String ageGroup;

    @Column(length = 3000)
    private String specifications; // Key specs e.g. "Material: ABS Plastic, Battery: Required 2xAA..."

    private Boolean featured;

    private Boolean bestSeller;

    private Boolean newArrival;

    private LocalDateTime createdAt;

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.rating = 4.5;
        this.reviewCount = 120;
        this.featured = false;
        this.bestSeller = false;
        this.newArrival = false;
        this.discountPercent = 0;
    }

    public Product(String name, String description, Double price, Double originalPrice, 
                   Integer stockQuantity, Category category, String imageUrl, 
                   String additionalImages, Double rating, Integer reviewCount, 
                   String brand, String ageGroup, String specifications, 
                   Boolean featured, Boolean bestSeller, Boolean newArrival) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        if (originalPrice != null && originalPrice > price) {
            this.discountPercent = (int) Math.round(((originalPrice - price) / originalPrice) * 100);
        } else {
            this.discountPercent = 0;
        }
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imageUrl = imageUrl;
        this.additionalImages = additionalImages;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.brand = brand;
        this.ageGroup = ageGroup;
        this.specifications = specifications;
        this.featured = featured;
        this.bestSeller = bestSeller;
        this.newArrival = newArrival;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(String additionalImages) {
        this.additionalImages = additionalImages;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(Boolean bestSeller) {
        this.bestSeller = bestSeller;
    }

    public Boolean getNewArrival() {
        return newArrival;
    }

    public void setNewArrival(Boolean newArrival) {
        this.newArrival = newArrival;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isInStock() {
        return this.stockQuantity != null && this.stockQuantity > 0;
    }
}
