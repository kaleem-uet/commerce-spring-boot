package com.example.commerce.services;

import com.example.commerce.dtos.OrderItemRequestDTO;
import com.example.commerce.dtos.OrderRequestDTO;
import com.example.commerce.dtos.OrderResponseDTO;
import com.example.commerce.entities.*;
import com.example.commerce.exceptions.ResourceNotFoundException;
import com.example.commerce.mappers.OrderMapper;
import com.example.commerce.repositories.AddressRepository;
import com.example.commerce.repositories.OrderRepository;
import com.example.commerce.repositories.ProductRepository;
import com.example.commerce.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        AddressRepository addressRepository,
                        ProductRepository productRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponseDTO> getAllOrders() {
        logger.debug("Fetching all orders from database");
        List<OrderResponseDTO> orders = orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} orders", orders.size());
        return orders;
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        logger.debug("Fetching order with id: {}", id);
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return orderMapper.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        logger.debug("Fetching orders for user: {}", userId);
        List<OrderResponseDTO> orders = orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} orders for user {}", orders.size(), userId);
        return orders;
    }

    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        logger.debug("Fetching orders with status: {}", status);
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        List<OrderResponseDTO> orders = orderRepository.findByStatus(orderStatus)
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.debug("Found {} orders with status {}", orders.size(), status);
        return orders;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        logger.debug("Creating new order for user: {}", orderRequestDTO.getUserId());

        // Validation
        if (orderRequestDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (orderRequestDTO.getOrderItems() == null || orderRequestDTO.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Fetch user
        User user = userRepository.findById(orderRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", orderRequestDTO.getUserId()));

        // Fetch shipping address if provided
        Address shippingAddress = null;
        if (orderRequestDTO.getShippingAddressId() != null) {
            shippingAddress = addressRepository.findById(orderRequestDTO.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", orderRequestDTO.getShippingAddressId()));
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Calculate total and add order items
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequestDTO itemDTO : orderRequestDTO.getOrderItems()) {
            if (itemDTO.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDTO.getProductId()));

            // Validate product has a price
            if (product.getPrice() == null) {
                throw new IllegalArgumentException("Product " + product.getId() + " does not have a price set");
            }

            // Always use the current product price from the database
            BigDecimal itemPrice = product.getPrice();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .price(itemPrice)
                    .build();

            order.addOrderItem(orderItem);
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with id: {}", savedOrder.getId());
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        logger.debug("Updating order {} status to {}", id, status);

        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order {} status updated to {}", id, status);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        logger.debug("Deleting order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        orderRepository.delete(order);
        logger.info("Order deleted successfully with id: {}", id);
    }
}
