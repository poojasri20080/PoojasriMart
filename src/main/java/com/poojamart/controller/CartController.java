package com.poojamart.controller;

import com.poojamart.dto.AddToCartRequest;
import com.poojamart.dto.UpdateCartRequest;
import com.poojamart.model.CartItem;
import com.poojamart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCartSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartSummary(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            CartItem item = cartService.addToCart(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Product added to cart!", "item", item));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@Valid @RequestBody UpdateCartRequest request) {
        try {
            CartItem item = cartService.updateQuantity(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "Cart updated", "item", item != null ? item : "removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Item removed from cart"));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Cart cleared"));
    }
}
