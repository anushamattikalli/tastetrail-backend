package tastetrail_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.CartItem;

import tastetrail_backend.service.CartItemService;
import tastetrail_backend.service.CartService;

@RestController
@RequestMapping("/cart-items")
@Validated
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartService cartService;

    public CartItemController(
            CartItemService cartItemService,
            CartService cartService) {

        this.cartItemService = cartItemService;
        this.cartService = cartService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Add item to cart
    @PostMapping
    public CartItem addCartItem(@RequestBody CartItem cartItem) {
        return cartItemService.addCartItem(cartItem);
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Get all cart items
    @GetMapping
    public List<CartItem> getAllCartItems() {
        return cartItemService.getAllCartItems();
    }

    @GetMapping("/{id}")
    public CartItem getCartItemById(
            @PathVariable Long id,
            Authentication authentication) {

        CartItem cartItem = cartItemService.getCartItemById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return cartItem;
        }

        String username = authentication.getName();

        if (cartItem.getCart() == null ||
                cartItem.getCart().getUser() == null ||
                !cartItem.getCart().getUser().getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You can only view your own cart item");
        }

        return cartItem;
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getCartItemsByCartId(
            @PathVariable Long cartId,
            Authentication authentication) {

        Cart cart = cartService.getCartById(cartId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cart.getUser() == null ||
                    !cart.getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only view items from your own cart");
            }
        }

        return cartItemService.getCartItemsByCartId(cartId);
    }

    @PutMapping("/{id}/quantity")
    public CartItem updateQuantity(
            @PathVariable Long id,
            @RequestParam 
            @Min(value = 1, message = "Quantity must be at least 1")
            int quantity,
            Authentication authentication) {

        CartItem cartItem = cartItemService.getCartItemById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cartItem.getCart() == null ||
                    cartItem.getCart().getUser() == null ||
                    !cartItem.getCart().getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only update items in your own cart");
            }
        }

        return cartItemService.updateQuantity(id, quantity);
    }

    @DeleteMapping("/{id}")
    public String removeCartItem(
            @PathVariable Long id,
            Authentication authentication) {

        CartItem cartItem = cartItemService.getCartItemById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cartItem.getCart() == null ||
                    cartItem.getCart().getUser() == null ||
                    !cartItem.getCart().getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only remove items from your own cart");
            }
        }

        cartItemService.removeCartItem(id);

        return "Cart item removed successfully";
    }
    
    @PostMapping("/add")
    public CartItem addItemToCart(
            @RequestParam Long cartId,
            @RequestParam Long menuItemId,
            @RequestParam
            @Min(value = 1, message = "Quantity must be at least 1")
            int quantity,
            Authentication authentication) {

        Cart cart = cartService.getCartById(cartId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cart.getUser() == null ||
                    !cart.getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only add items to your own cart");
            }
        }

        return cartItemService.addItemToCart(
                cartId,
                menuItemId,
                quantity
        );
    }
    @DeleteMapping("/cart/{cartId}/clear")
    public String clearCart(
            @PathVariable Long cartId,
            Authentication authentication) {

        Cart cart = cartService.getCartById(cartId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cart.getUser() == null ||
                    !cart.getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only clear your own cart");
            }
        }

        cartItemService.clearCart(cartId);

        return "Cart cleared successfully";
    }
   
    @GetMapping("/cart/{cartId}/total")
    public BigDecimal getCartTotal(
            @PathVariable Long cartId,
            Authentication authentication) {

        Cart cart = cartService.getCartById(cartId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {

            String username = authentication.getName();

            if (cart.getUser() == null ||
                    !cart.getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only view your own cart total");
            }
        }

        return cartItemService.calculateCartTotal(cartId);
    }
    }

