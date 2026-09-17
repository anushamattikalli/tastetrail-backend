package tastetrail_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tastetrail_backend.entity.MenuItem;
import tastetrail_backend.entity.Restaurant;
import tastetrail_backend.enums.MenuCategory;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.MenuItemRepository;
import tastetrail_backend.repository.RestaurantRepository;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           RestaurantRepository restaurantRepository) {

        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // Add one menu item
    public MenuItem addMenuItem(MenuItem menuItem, Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found"));

        menuItem.setRestaurant(restaurant);

        return menuItemRepository.save(menuItem);
    }

    // Get all menu items
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // Get menu item by ID
    public MenuItem getMenuItemById(Long id) {

        return menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));
    }

    // Delete menu item
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
    
 // Add multiple menu items for one restaurant
    public List<MenuItem> addMenuItems(List<MenuItem> menuItems, Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found"));

        for (MenuItem menuItem : menuItems) {
            menuItem.setRestaurant(restaurant);
        }

        return menuItemRepository.saveAll(menuItems);
    }
    
 // Get Menu Items By Restaurant ID
    public List<MenuItem> getMenuItemsByRestaurantId(Long restaurantId) {
        return menuItemRepository
                .findByRestaurantRestaurantId(restaurantId);
    }
    
 // Get Menu Items By Category
    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        return menuItemRepository.findByCategory(category);
    }
    
 // Get Menu Items By Restaurant ID and Category
    public List<MenuItem> getMenuItemsByRestaurantIdAndCategory(
            Long restaurantId,
            MenuCategory category) {

        return menuItemRepository
                .findByRestaurantRestaurantIdAndCategory(
                        restaurantId,
                        category);
    }
    

    
 // Get available menu items
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }
 // Update menu item availability
    public MenuItem updateMenuItemAvailability(
            Long id,
            boolean available) {

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        menuItem.setAvailable(available);

        return menuItemRepository.save(menuItem);
    }
    
 // Search menu items by name
    public List<MenuItem> searchMenuItems(String name) {

        return menuItemRepository
                .findByNameContainingIgnoreCase(name);
    }
    
 // Search menu items by restaurant and name
    public List<MenuItem> searchMenuItemsByRestaurant(
            Long restaurantId,
            String name) {

        return menuItemRepository
                .findByRestaurantRestaurantIdAndNameContainingIgnoreCase(
                        restaurantId,
                        name);
    }
    
 // Get available menu items by restaurant
    public List<MenuItem> getAvailableMenuItemsByRestaurant(
            Long restaurantId) {

        return menuItemRepository
                .findByRestaurantRestaurantIdAndAvailableTrue(
                        restaurantId);
    }
    
 // Get available menu items by restaurant and category
    public List<MenuItem> getAvailableMenuItemsByRestaurantAndCategory(
            Long restaurantId,
            MenuCategory category) {

        return menuItemRepository
                .findByRestaurantRestaurantIdAndCategoryAndAvailableTrue(
                        restaurantId,
                        category);
    }
    
 // Get Unavailable Menu Items By Restaurant ID
    public List<MenuItem> getUnavailableMenuItemsByRestaurantId(
            Long restaurantId) {

        return menuItemRepository
                .findByRestaurantRestaurantIdAndAvailableFalse(
                        restaurantId);
    }
    
 // Get all unavailable menu items
    public List<MenuItem> getUnavailableMenuItems() {

        return menuItemRepository.findByAvailableFalse();
    }
    
 // Update complete menu item details
    public MenuItem updateMenuItem(
            Long id,
            MenuItem updatedMenuItem) {

        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        existingMenuItem.setName(updatedMenuItem.getName());
        existingMenuItem.setDescription(updatedMenuItem.getDescription());
        existingMenuItem.setPrice(updatedMenuItem.getPrice());
        existingMenuItem.setImageUrl(updatedMenuItem.getImageUrl());
        existingMenuItem.setCategory(updatedMenuItem.getCategory());
        existingMenuItem.setAvailable(updatedMenuItem.isAvailable());

        return menuItemRepository.save(existingMenuItem);
    }
    
   
    
}

