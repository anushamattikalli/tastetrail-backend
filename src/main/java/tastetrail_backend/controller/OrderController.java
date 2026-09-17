package tastetrail_backend.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.Order;
import tastetrail_backend.entity.User;
import tastetrail_backend.enums.OrderStatus;
import tastetrail_backend.repository.CartRepository;
import tastetrail_backend.repository.UserRepository;
import tastetrail_backend.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/orders")
@Validated

public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderController(
            OrderService orderService,
            UserRepository userRepository,
            CartRepository cartRepository) {

        this.orderService = orderService;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    // Create order
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        Order order = orderService.getOrderById(id);

        String username = authentication.getName();

        // ADMIN can view any order
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return order;
        }

        // CUSTOMER can view only their own order
        if (order.getUser() == null ||
                !order.getUser().getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You can only view your own order");
        }

        return order;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    // Delete order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/place")
    public Order placeOrder(
            @RequestParam Long cartId,
            @RequestParam 
            @NotBlank(message = "Delivery address is required")
            String deliveryAddress,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getUser() == null ||
                !cart.getUser().getUserId().equals(loggedInUser.getUserId())) {

            throw new AccessDeniedException(
                    "You can only place an order from your own cart");
        }

        return orderService.placeOrder(cartId, deliveryAddress);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return orderService.updateOrderStatus(id, status);
    }
    
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        // ADMIN can cancel any order
        if (!isAdmin) {

            // CUSTOMER can cancel only their own order
            if (order.getUser() == null ||
                    !order.getUser().getUserId().equals(loggedInUser.getUserId())) {

                throw new AccessDeniedException(
                        "You can only cancel your own order");
            }
        }

        return orderService.cancelOrder(id);
    }
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You can only view your own orders");
        }

        return orderService.getOrdersByUserId(userId);
    }
    
    
    @GetMapping("/user/{userId}/status")
    public List<Order> getOrdersByUserIdAndStatus(
            @PathVariable Long userId,
            @RequestParam OrderStatus status,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You can only view your own orders");
        }

        return orderService.getOrdersByUserIdAndStatus(userId, status);
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PutMapping("/{id}/address")
    public Order updateDeliveryAddress(
            @PathVariable Long id,
            @RequestParam
            @NotBlank(message = "Delivery address is required")
            String deliveryAddress,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        // ADMIN can update any order address
        if (!isAdmin) {

            // CUSTOMER can update only their own order
            if (order.getUser() == null ||
                    !order.getUser().getUserId().equals(loggedInUser.getUserId())) {

                throw new AccessDeniedException(
                        "You can only update your own order address");
            }
        }

        return orderService.updateDeliveryAddress(id, deliveryAddress);
    }
}
