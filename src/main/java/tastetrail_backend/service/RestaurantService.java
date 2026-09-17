package tastetrail_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tastetrail_backend.entity.Restaurant;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // Add Restaurant
    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    // Get All Restaurants
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // Get Restaurant By ID
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    // Delete Restaurant
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
    
 // Add Multiple Restaurants
    public List<Restaurant> addRestaurants(List<Restaurant> restaurants) {
        return restaurantRepository.saveAll(restaurants);
    }
    
 // Update Restaurant
    public Restaurant updateRestaurant(Long id, Restaurant updatedRestaurant) {

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setDescription(updatedRestaurant.getDescription());
        existingRestaurant.setAddress(updatedRestaurant.getAddress());
        existingRestaurant.setPhone(updatedRestaurant.getPhone());
        existingRestaurant.setEmail(updatedRestaurant.getEmail());
        existingRestaurant.setImageUrl(updatedRestaurant.getImageUrl());
        existingRestaurant.setActive(updatedRestaurant.isActive());

        return restaurantRepository.save(existingRestaurant);
    }
    
 // Get active restaurants
    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepository.findByActiveTrue();
    }
    
    
 // Update restaurant active status
    public Restaurant updateRestaurantStatus(Long id, boolean active) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        restaurant.setActive(active);

        return restaurantRepository.save(restaurant);
    }
}
