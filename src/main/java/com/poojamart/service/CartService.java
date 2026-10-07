package com.poojamart.service;

import com.poojamart.dto.AddToCartRequest;
import com.poojamart.dto.UpdateCartRequest;
import com.poojamart.model.CartItem;
import com.poojamart.model.Product;
import com.poojamart.model.User;
import com.poojamart.repository.CartItemRepository;
import com.poojamart.repository.ProductRepository;
import com.poojamart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
    }

    public CartItem addToCart(AddToCartRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        if (product.getStockQuantity() <= 0) {
            throw new RuntimeException("Product is out of stock!");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserAndProduct(user, product);
        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQty = existing.getQuantity() + (request.getQuantity() != null ? request.getQuantity() : 1);
            if (newQty > product.getStockQuantity()) {
                newQty = product.getStockQuantity();
            }
            existing.setQuantity(newQty);
            return cartItemRepository.save(existing);
        } else {
            int qty = request.getQuantity() != null ? request.getQuantity() : 1;
            if (qty > product.getStockQuantity()) {
                qty = product.getStockQuantity();
            }
            CartItem newItem = new CartItem(user, product, qty);
            return cartItemRepository.save(newItem);
        }
    }

    public CartItem updateQuantity(UpdateCartRequest request) {
        CartItem item = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + request.getCartItemId()));

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        if (request.getQuantity() > item.getProduct().getStockQuantity()) {
            item.setQuantity(item.getProduct().getStockQuantity());
        } else {
            item.setQuantity(request.getQuantity());
        }

        return cartItemRepository.save(item);
    }

    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public Map<String, Object> getCartSummary(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        double originalTotal = 0.0;
        double subtotal = 0.0;
        int totalQuantity = 0;

        for (CartItem item : items) {
            double price = item.getProduct().getPrice();
            double origPrice = item.getProduct().getOriginalPrice() != null ? item.getProduct().getOriginalPrice() : price;
            subtotal += price * item.getQuantity();
            originalTotal += origPrice * item.getQuantity();
            totalQuantity += item.getQuantity();
        }

        double discount = Math.max(0.0, originalTotal - subtotal);
        double deliveryFee = (subtotal > 499 || subtotal == 0) ? 0.0 : 40.0; // Free delivery over ₹499
        double finalAmount = subtotal + deliveryFee;

        Map<String, Object> summary = new HashMap<>();
        summary.put("items", items);
        summary.put("itemCount", items.size());
        summary.put("totalQuantity", totalQuantity);
        summary.put("originalTotal", Math.round(originalTotal * 100.0) / 100.0);
        summary.put("subtotal", Math.round(subtotal * 100.0) / 100.0);
        summary.put("discount", Math.round(discount * 100.0) / 100.0);
        summary.put("deliveryFee", deliveryFee);
        summary.put("finalAmount", Math.round(finalAmount * 100.0) / 100.0);

        return summary;
    }
}
