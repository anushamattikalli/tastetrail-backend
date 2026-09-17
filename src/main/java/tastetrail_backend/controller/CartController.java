package tastetrail_backend.controller;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.User;
import tastetrail_backend.repository.UserRepository;
import tastetrail_backend.service.CartService;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    
    public CartController(
            CartService cartService,
            UserRepository userRepository) {

        this.cartService = cartService;
        this.userRepository = userRepository;
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Create cart
    @PostMapping
    public Cart createCart(@RequestBody Cart cart) {
        return cartService.createCart(cart);
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Get all carts
    @GetMapping
    public List<Cart> getAllCarts() {
        return cartService.getAllCarts();
    }
    @GetMapping("/{id}")
    public Cart getCartById(
            @PathVariable Long id,
            Authentication authentication) {

        Cart cart = cartService.getCartById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return cart;
        }

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cart.getUser() == null ||
                !cart.getUser().getUserId().equals(loggedInUser.getUserId())) {

            throw new AccessDeniedException(
                    "You can only view your own cart");
        }

        return cart;
    }
    @DeleteMapping("/{id}")
    public String deleteCart(
            @PathVariable Long id,
            Authentication authentication) {

        Cart cart = cartService.getCartById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            User loggedInUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (cart.getUser() == null ||
                    !cart.getUser().getUserId().equals(loggedInUser.getUserId())) {

                throw new AccessDeniedException(
                        "You can only delete your own cart");
            }
        }

        cartService.deleteCart(id);

        return "Cart deleted successfully";
    } 
    
    @PostMapping("/user/{userId}")
    public Cart createCartForUser(
            @PathVariable Long userId,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You can only create a cart for yourself");
        }

        return cartService.createCartForUser(userId);
    } 
    
    @GetMapping("/user/{userId}")
    public Cart getCartByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You can only view your own cart");
        }

        return cartService.getCartByUserId(userId);
    }
}