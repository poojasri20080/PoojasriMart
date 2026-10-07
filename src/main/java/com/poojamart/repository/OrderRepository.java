package com.poojamart.repository;

import com.poojamart.model.Order;
import com.poojamart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT SUM(o.payableAmount) FROM Order o WHERE o.orderStatus <> 'CANCELLED'")
    Double calculateTotalRevenue();

    long countByOrderStatus(String orderStatus);
}
