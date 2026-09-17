package tastetrail_backend.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tastetrail_backend.enums.MenuCategory;
import tastetrail_backend.entity.MenuItem;
import tastetrail_backend.service.MenuItemService;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Add one menu item
    @PostMapping
    public MenuItem addMenuItem(
    		@Valid
            @RequestBody MenuItem menuItem,
            @RequestParam Long restaurantId) {

        return menuItemService.addMenuItem(menuItem, restaurantId);
    }

    // Get all menu items
    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    // Get menu item by ID
    @GetMapping("/{id}")
    public MenuItem getMenuItemById(@PathVariable Long id) {
        return menuItemService.getMenuItemById(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Delete menu item
    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Add multiple menu items
    @PostMapping("/all")
    public List<MenuItem> addMenuItems(
            @RequestBody List<MenuItem> menuItems,
            @RequestParam Long restaurantId) {

        return menuItemService.addMenuItems(menuItems, restaurantId);
    }
    
 // Get Menu Items By Restaurant ID
    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItem> getMenuItemsByRestaurantId(
            @PathVariable Long restaurantId) {

        return menuItemService
                .getMenuItemsByRestaurantId(restaurantId);
    }
    
    
 // Get Menu Items By Category
    @GetMapping("/category/{category}")
    public List<MenuItem> getMenuItemsByCategory(
            @PathVariable MenuCategory category) {

        return menuItemService.getMenuItemsByCategory(category);
    }
    
 // Get Menu Items By Restaurant ID and Category
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    public List<MenuItem> getMenuItemsByRestaurantIdAndCategory(
            @PathVariable Long restaurantId,
            @PathVariable MenuCategory category) {

        return menuItemService
                .getMenuItemsByRestaurantIdAndCategory(
                        restaurantId,
                        category);
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Update Menu Item
    @PutMapping("/{id}")
    public MenuItem updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItem updatedMenuItem) {

        return menuItemService.updateMenuItem(id, updatedMenuItem);
    }
    
 // Get available menu items
    @GetMapping("/available")
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemService.getAvailableMenuItems();
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Update menu item availability
    @PutMapping("/{id}/availability")
    public MenuItem updateMenuItemAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {

        return menuItemService
                .updateMenuItemAvailability(id, available);
    }
    
 // Search menu items by name
    @GetMapping("/search")
    public List<MenuItem> searchMenuItems(
            @RequestParam String name) {

        return menuItemService.searchMenuItems(name);
    }
    
 // Search menu items by restaurant and name
    @GetMapping("/restaurant/{restaurantId}/search")
    public List<MenuItem> searchMenuItemsByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam String name) {

        return menuItemService.searchMenuItemsByRestaurant(
                restaurantId,
                name);
    }
    
 // Get available menu items by restaurant
    @GetMapping("/restaurant/{restaurantId}/available")
    public List<MenuItem> getAvailableMenuItemsByRestaurant(
            @PathVariable Long restaurantId) {

        return menuItemService
                .getAvailableMenuItemsByRestaurant(restaurantId);
    }
    
 // Get available menu items by restaurant and category
    @GetMapping("/restaurant/{restaurantId}/category/{category}/available")
    public List<MenuItem> getAvailableMenuItemsByRestaurantAndCategory(
            @PathVariable Long restaurantId,
            @PathVariable MenuCategory category) {

        return menuItemService
                .getAvailableMenuItemsByRestaurantAndCategory(
                        restaurantId,
                        category);
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Get Unavailable Menu Items By Restaurant ID
    @GetMapping("/restaurant/{restaurantId}/unavailable")
    public List<MenuItem> getUnavailableMenuItemsByRestaurantId(
            @PathVariable Long restaurantId) {

        return menuItemService
                .getUnavailableMenuItemsByRestaurantId(restaurantId);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
 // Get all unavailable menu items
    @GetMapping("/unavailable")
    public List<MenuItem> getUnavailableMenuItems() {

        return menuItemService.getUnavailableMenuItems();
    }
    
    
}