package tastetrail_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.MenuItem;
import tastetrail_backend.enums.MenuCategory;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantRestaurantId(Long restaurantId);

    List<MenuItem> findByCategory(MenuCategory category);

    List<MenuItem> findByRestaurantRestaurantIdAndCategory(
            Long restaurantId,
            MenuCategory category
    );

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByNameContainingIgnoreCase(String name);

    List<MenuItem> findByRestaurantRestaurantIdAndNameContainingIgnoreCase(
            Long restaurantId,
            String name
    );

    List<MenuItem> findByRestaurantRestaurantIdAndAvailableTrue(
            Long restaurantId
    );
    
    List<MenuItem> findByRestaurantRestaurantIdAndCategoryAndAvailableTrue(
            Long restaurantId,
            MenuCategory category
    );
    
    List<MenuItem> findByRestaurantRestaurantIdAndAvailableFalse(
            Long restaurantId
    );
    
    List<MenuItem> findByAvailableFalse();
    
    
}