package com.poojamart.repository;

import com.poojamart.model.CartItem;
import com.poojamart.model.Product;
import com.poojamart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByAddedAtDesc(User user);
    List<CartItem> findByUserIdOrderByAddedAtDesc(Long userId);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUserId(Long userId);
    void deleteByUser(User user);
}
