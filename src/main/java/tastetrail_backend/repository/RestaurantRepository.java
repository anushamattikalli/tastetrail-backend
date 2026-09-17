package tastetrail_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.Restaurant;

public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrue();

}