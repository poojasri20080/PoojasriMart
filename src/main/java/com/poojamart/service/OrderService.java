package com.poojamart.service;

import com.poojamart.dto.OrderRequest;
import com.poojamart.dto.OrderStatusUpdateRequest;
import com.poojamart.model.Order;
import com.poojamart.model.OrderItem;
import com.poojamart.model.Product;
import com.poojamart.model.User;
import com.poojamart.repository.CartItemRepository;
import com.poojamart.repository.OrderItemRepository;
import com.poojamart.repository.OrderRepository;
import com.poojamart.repository.ProductRepository;
import com.poojamart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Order createOrder(OrderRequest request) {
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }

        // Generate Flipkart-style order ID: PMT-yyyyMMdd-XXXXX
        String orderNumber = "PMT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                             "-" + (10000 + new Random().nextInt(90000));

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setPhone(request.getPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPostalCode(request.getPostalCode());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("CASH_ON_DELIVERY".equalsIgnoreCase(request.getPaymentMethod()) ? "PENDING" : "PAID");
        order.setOrderStatus("PLACED");
        order.setOrderDate(LocalDateTime.now());
        order.setExpectedDeliveryDate(LocalDateTime.now().plusDays(4));

        double itemsTotal = 0.0;

        for (OrderRequest.OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

            int qty = itemDto.getQuantity();
            if (product.getStockQuantity() < qty) {
                qty = Math.max(1, product.getStockQuantity());
            }

            // Deduct stock
            product.setStockQuantity(Math.max(0, product.getStockQuantity() - qty));
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(product, qty);
            order.addItem(orderItem);
            itemsTotal += orderItem.getSubtotal();
        }

        double deliveryCharge = request.getDeliveryCharge() != null ? request.getDeliveryCharge() : 
                                (itemsTotal > 499 ? 0.0 : 40.0);
        double discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0;
        double payableAmount = Math.max(0.0, itemsTotal + deliveryCharge - discountAmount);

        order.setTotalAmount(Math.round(itemsTotal * 100.0) / 100.0);
        order.setDeliveryCharge(deliveryCharge);
        order.setDiscountAmount(discountAmount);
        order.setPayableAmount(Math.round(payableAmount * 100.0) / 100.0);

        Order savedOrder = orderRepository.save(order);

        // If user placed order, clear their cart
        if (user != null) {
            cartItemRepository.deleteByUserId(user.getId());
        }

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            order.setOrderStatus(request.getStatus().toUpperCase());
        }
        if (request.getPaymentStatus() != null && !request.getPaymentStatus().isBlank()) {
            order.setPaymentStatus(request.getPaymentStatus().toUpperCase());
        }

        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if ("DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new RuntimeException("Delivered orders cannot be cancelled.");
        }

        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);

        // Restore stock
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            }
        }
    }
}
