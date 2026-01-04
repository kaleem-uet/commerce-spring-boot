package com.example.commerce.controllers;

import com.example.commerce.dtos.OrderRequestDTO;
import com.example.commerce.dtos.OrderResponseDTO;
import com.example.commerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Orders", description = "Order management APIs - Create, read, update and delete orders")
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get all orders", description = "Retrieve all orders with optional filtering by userId or status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
            @ApiResponse(responseCode = "400", description = "Invalid status parameter")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @Parameter(description = "Filter orders by user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter orders by status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)") @RequestParam(required = false) String status
    ) {
        logger.info("GET /orders - Fetching orders with filters - userId: {}, status: {}", userId, status);

        List<OrderResponseDTO> orders;
        if (userId != null) {
            orders = orderService.getOrdersByUserId(userId);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }

        logger.info("GET /orders - Returning {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve a specific order with all its items by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        logger.info("GET /orders/{} - Fetching order by id", id);
        OrderResponseDTO order = orderService.getOrderById(id);
        logger.info("GET /orders/{} - Order found", id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Create new order", description = "Create a new order with order items. Total amount is calculated automatically.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User, product or address not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Order request with user ID, shipping address and order items")
            @RequestBody OrderRequestDTO orderRequestDTO) {
        logger.info("POST /orders - Creating new order for user: {}", orderRequestDTO.getUserId());
        OrderResponseDTO createdOrder = orderService.createOrder(orderRequestDTO);
        logger.info("POST /orders - Order created with id: {}", createdOrder.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Status update (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)")
            @RequestBody Map<String, String> statusUpdate
    ) {
        String status = statusUpdate.get("status");
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        logger.info("PATCH /orders/{}/status - Updating order status to {}", id, status);
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, status);
        logger.info("PATCH /orders/{}/status - Order status updated successfully", id);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Delete order", description = "Delete an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        logger.info("DELETE /orders/{} - Deleting order", id);
        orderService.deleteOrder(id);
        logger.info("DELETE /orders/{} - Order deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
