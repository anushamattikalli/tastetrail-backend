package tastetrail_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tastetrail_backend.entity.Order;
import tastetrail_backend.entity.Payment;
import tastetrail_backend.enums.OrderStatus;
import tastetrail_backend.exception.ResourceNotFoundException;
import tastetrail_backend.repository.OrderRepository;
import tastetrail_backend.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment createPayment(
            Long orderId,
            String paymentMethod) {
    	if (paymentMethod == null || paymentMethod.isBlank()) {
    	    throw new RuntimeException("Payment method is required");
    	}

    	if (!paymentMethod.equalsIgnoreCase("UPI")
    	        && !paymentMethod.equalsIgnoreCase("CARD")
    	        && !paymentMethod.equalsIgnoreCase("CASH")) {

    	    throw new RuntimeException(
    	            "Invalid payment method. Use UPI, CARD, or CASH");
    	}

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Order not found"));

        if (paymentRepository.findByOrderOrderId(orderId).isPresent()) {
            throw new RuntimeException(
                    "Payment already exists for this order");
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("PENDING");

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Payment not found"));
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Payment not found for this order"));
    }
    
    public Payment updatePaymentStatus(Long id, String status) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Payment status is required");
        }

        status = status.toUpperCase();

        if (!status.equals("PENDING")
                && !status.equals("SUCCESS")
                && !status.equals("FAILED")) {

            throw new RuntimeException(
                    "Invalid payment status. Use PENDING, SUCCESS, or FAILED");
        }

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Payment is already successful");
        }

        if ("FAILED".equals(payment.getPaymentStatus())
                && !"SUCCESS".equals(status)) {

            throw new RuntimeException(
                    "Failed payment can only be changed to SUCCESS");
        }

        payment.setPaymentStatus(status);

        if ("SUCCESS".equals(status)) {

            Order order = payment.getOrder();

            if (order == null) {
                throw new RuntimeException(
                        "Payment is not linked to an order");
            }

            if (order.getStatus() == OrderStatus.DELIVERED) {
                throw new RuntimeException(
                        "Payment cannot be marked successful for a delivered order");
            }

            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new RuntimeException(
                        "Payment cannot be marked successful for a cancelled order");
            }

            if (order.getStatus() == OrderStatus.PLACED) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
        }

        return paymentRepository.save(payment);
    }
  
}