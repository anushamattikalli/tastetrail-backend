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

import tastetrail_backend.entity.Restaurant;
import tastetrail_backend.service.RestaurantService;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
    
    
    @PreAuthorize("hasRole('ADMIN')")

    // Add Restaurant
    @PostMapping
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    // Get All Restaurants
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    // Get Restaurant By ID
    @GetMapping("/{id}")
    public Restaurant getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    // Delete Restaurant
    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Add Multiple Restaurants
    @PostMapping("/all")
    public List<Restaurant> addRestaurants(
            @RequestBody List<Restaurant> restaurants) {

        return restaurantService.addRestaurants(restaurants);
    }
    @PreAuthorize("hasRole('ADMIN')")
 // Update Restaurant
    @PutMapping("/{id}")
    public Restaurant updateRestaurant(
            @PathVariable Long id,
            @RequestBody Restaurant updatedRestaurant) {

        return restaurantService.updateRestaurant(id, updatedRestaurant);
    }
    
 // Get active restaurants
    @GetMapping("/active")
    public List<Restaurant> getActiveRestaurants() {
        return restaurantService.getActiveRestaurants();
    } 
    
    @PreAuthorize("hasRole('ADMIN')")
 // Update restaurant active status
    @PutMapping("/{id}/status")
    public Restaurant updateRestaurantStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return restaurantService.updateRestaurantStatus(id, active);
    }
    
 
}