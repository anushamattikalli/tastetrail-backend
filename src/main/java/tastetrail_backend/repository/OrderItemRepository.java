package tastetrail_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
