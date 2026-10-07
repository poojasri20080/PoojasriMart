package com.poojamart.service;

import com.poojamart.dto.DashboardStatsDto;
import com.poojamart.model.Product;
import com.poojamart.repository.OrderRepository;
import com.poojamart.repository.ProductRepository;
import com.poojamart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        Double rev = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(rev != null ? Math.round(rev * 100.0) / 100.0 : 0.0);
        stats.setTotalOrders(orderRepository.count());
        stats.setTotalProducts(productRepository.count());
        stats.setTotalUsers(userRepository.count());

        stats.setPendingOrders(orderRepository.countByOrderStatus("PLACED") + orderRepository.countByOrderStatus("CONFIRMED"));
        stats.setDeliveredOrders(orderRepository.countByOrderStatus("DELIVERED"));

        List<Product> products = productRepository.findAll();
        long lowStock = products.stream().filter(p -> p.getStockQuantity() != null && p.getStockQuantity() <= 5).count();
        stats.setLowStockProducts(lowStock);

        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("PLACED", orderRepository.countByOrderStatus("PLACED"));
        statusMap.put("CONFIRMED", orderRepository.countByOrderStatus("CONFIRMED"));
        statusMap.put("SHIPPED", orderRepository.countByOrderStatus("SHIPPED"));
        statusMap.put("OUT_FOR_DELIVERY", orderRepository.countByOrderStatus("OUT_FOR_DELIVERY"));
        statusMap.put("DELIVERED", orderRepository.countByOrderStatus("DELIVERED"));
        statusMap.put("CANCELLED", orderRepository.countByOrderStatus("CANCELLED"));
        stats.setOrdersByStatus(statusMap);

        return stats;
    }
}
