package tastetrail_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tastetrail_backend.entity.Order;
import tastetrail_backend.entity.OrderItem;
import tastetrail_backend.entity.User;
import tastetrail_backend.enums.OrderStatus;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.OrderRepository;
import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.CartItem;
import tastetrail_backend.entity.MenuItem;
import tastetrail_backend.repository.CartRepository;
import tastetrail_backend.repository.CartItemRepository;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }
    // Create order
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found");
        }

        orderRepository.deleteById(id);
    }
    public List<CartItem> getCartItemsForOrder(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return cartItemRepository.findByCartCartId(cart.getCartId());
    }
    
    @Transactional
    public Order placeOrder(Long cartId, String deliveryAddress) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        User user = cart.getUser();

        if (user == null) {
            throw new RuntimeException("Cart is not linked to a user");
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCartCartId(cartId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);

        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.PLACED);

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
        	
        	 if (cartItem.getQuantity() < 1) {
        	        throw new RuntimeException(
        	                "Order item quantity must be at least 1"
        	        );
        	    }

            MenuItem menuItem = cartItem.getMenuItem();
            
            if (menuItem == null) {
                throw new RuntimeException("Menu item is missing");
            }

            if (!menuItem.isAvailable()) {
                throw new RuntimeException(
                        "Menu item is currently unavailable: "
                        + menuItem.getName()
                );
            }

            BigDecimal price = menuItem.getPrice();

            if (price == null) {
                throw new RuntimeException(
                        "Price is missing for menu item: "
                        + menuItem.getMenuItemId()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(price);

            orderItems.add(orderItem);

            BigDecimal itemTotal =
                    price.multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    );
            

         orderItem.setSubtotal(itemTotal);

            total = total.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);


       Order savedOrder = orderRepository.save(order);

       cartItemRepository.deleteByCartId(cartId);

       return savedOrder;
    }
    
    public Order updateOrderStatus(Long id, OrderStatus status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        // Do not allow changing a delivered or cancelled order
        if (currentStatus == OrderStatus.DELIVERED ||
            currentStatus == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order status cannot be changed from " + currentStatus
            );
        }

        // Allow only the correct status sequence
        if (currentStatus == OrderStatus.PLACED &&
            status != OrderStatus.CONFIRMED &&
            status != OrderStatus.CANCELLED) {

            throw new RuntimeException("Invalid status transition");
        }

        if (currentStatus == OrderStatus.CONFIRMED &&
            status != OrderStatus.PREPARING &&
            status != OrderStatus.CANCELLED) {

            throw new RuntimeException("Invalid status transition");
        }

        if (currentStatus == OrderStatus.PREPARING &&
            status != OrderStatus.OUT_FOR_DELIVERY &&
            status != OrderStatus.CANCELLED) {

            throw new RuntimeException("Invalid status transition");
        }

        if (currentStatus == OrderStatus.OUT_FOR_DELIVERY &&
            status != OrderStatus.DELIVERED) {

            throw new RuntimeException("Invalid status transition");
        }

        order.setStatus(status);

        return orderRepository.save(order);
    }
    
    public Order cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }
    
    public List<Order> getOrdersByUserIdAndStatus(
            Long userId,
            OrderStatus status) {

        return orderRepository.findByUserUserIdAndStatus(userId, status);
    }
    
    public Order updateDeliveryAddress(Long id, String deliveryAddress) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered order address cannot be changed");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cancelled order address cannot be changed");
        }

        order.setDeliveryAddress(deliveryAddress);

        return orderRepository.save(order);
    }
}
