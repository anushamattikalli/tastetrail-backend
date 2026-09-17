package tastetrail_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tastetrail_backend.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderOrderId(Long orderId);
}