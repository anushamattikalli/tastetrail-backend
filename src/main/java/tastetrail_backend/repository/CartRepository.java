package tastetrail_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserUserId(Long userId);
}