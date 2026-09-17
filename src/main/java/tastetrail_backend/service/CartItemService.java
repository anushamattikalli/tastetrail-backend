package tastetrail_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tastetrail_backend.entity.CartItem;
import tastetrail_backend.repository.CartItemRepository;
import tastetrail_backend.entity.Cart;
import tastetrail_backend.entity.MenuItem;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.CartRepository;
import tastetrail_backend.repository.MenuItemRepository;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;

    public CartItemService(
            CartItemRepository cartItemRepository,
            CartRepository cartRepository,
            MenuItemRepository menuItemRepository) {

        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // Add item to cart
    public CartItem addCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    // Get all cart items
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    // Get cart item by ID
    public CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
    }

    // Get all items in a particular cart
    public List<CartItem> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartCartId(cartId);
    }

    public CartItem updateQuantity(Long id, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }
    @Transactional
    public void removeCartItem(Long id) {
        cartItemRepository.deleteCartItemById(id);
    }

    public CartItem addItemToCart(Long cartId, Long menuItemId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (!menuItem.isAvailable()) {
            throw new RuntimeException("Menu item is currently unavailable");
        }

        java.util.Optional<CartItem> existingItem =
                cartItemRepository.findByCartCartIdAndMenuItemMenuItemId(
                        cartId,
                        menuItemId
                );

        if (existingItem.isPresent()) {

            CartItem cartItem = existingItem.get();

            cartItem.setQuantity(cartItem.getQuantity() + quantity);

            return cartItemRepository.save(cartItem);
        }

        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }
    @Transactional
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
    
    public BigDecimal calculateCartTotal(Long cartId) {

        List<CartItem> cartItems =
                cartItemRepository.findByCartCartId(cartId);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            BigDecimal price = cartItem.getMenuItem().getPrice();

            if (price == null) {
                throw new RuntimeException(
                        "Price is missing for menu item: "
                        + cartItem.getMenuItem().getMenuItemId()
                );
            }

            BigDecimal itemTotal =
                    price.multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    );

            total = total.add(itemTotal);
        }

        return total;
    }
}