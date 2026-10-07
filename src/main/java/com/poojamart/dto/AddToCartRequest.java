package com.poojamart.dto;

import javax.validation.constraints.NotNull;

public class AddToCartRequest {

    private Long userId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Integer quantity = 1;

    public AddToCartRequest() {}

    public AddToCartRequest(Long userId, Long productId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
