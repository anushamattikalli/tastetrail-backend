
package tastetrail_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import tastetrail_backend.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartCartId(Long cartId);

    Optional<CartItem> findByCartCartIdAndMenuItemMenuItemId(
            Long cartId,
            Long menuItemId
    );

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.cartId = :cartId")
    int deleteByCartId(@Param("cartId") Long cartId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.cartItemId = :id")
    int deleteCartItemById(@Param("id") Long id);
}
