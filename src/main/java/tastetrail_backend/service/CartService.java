package tastetrail_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.User;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.CartRepository;
import tastetrail_backend.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

  
    public Cart createCartForUser(Long userId) {

        // Check whether the user already has a cart
        java.util.Optional<Cart> existingCart =
                cartRepository.findByUserUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    // Create cart
    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }

    // Get all carts
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    // Get cart by ID
    public Cart getCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }
 // Get cart by user ID
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    // Delete cart
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }
}