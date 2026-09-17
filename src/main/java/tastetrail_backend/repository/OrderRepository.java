package tastetrail_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.Order;
import tastetrail_backend.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserUserId(Long userId);
	
	List<Order> findByUserUserIdAndStatus(
	        Long userId,
	        OrderStatus status
	);
}