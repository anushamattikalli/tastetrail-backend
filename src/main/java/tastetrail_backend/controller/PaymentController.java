package tastetrail_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import tastetrail_backend.entity.Payment;
import tastetrail_backend.service.PaymentService;
import tastetrail_backend.entity.User;
import tastetrail_backend.entity.Order;
import tastetrail_backend.repository.UserRepository;
import tastetrail_backend.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final OrderService orderService;
    public PaymentController(
            PaymentService paymentService,
            UserRepository userRepository,
            OrderService orderService) {

        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping
    public Payment createPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentMethod,
            Authentication authentication) {

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(orderId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            if (order.getUser() == null ||
                    !order.getUser().getUserId().equals(loggedInUser.getUserId())) {

                throw new AccessDeniedException(
                        "You can only make payment for your own order");
            }
        }

        return paymentService.createPayment(orderId, paymentMethod);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{id}")
    public Payment getPaymentById(
            @PathVariable Long id,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return payment;
        }

        String username = authentication.getName();

        if (payment.getOrder() == null ||
                payment.getOrder().getUser() == null ||
                !payment.getOrder().getUser().getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You can only view your own payment");
        }

        return payment;
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrderId(
            @PathVariable Long orderId,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentByOrderId(orderId);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return payment;
        }

        String username = authentication.getName();

        if (payment.getOrder() == null ||
                payment.getOrder().getUser() == null ||
                !payment.getOrder().getUser().getUsername().equals(username)) {

            throw new AccessDeniedException(
                    "You can only view your own payment");
        }

        return payment;
    }
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PutMapping("/{id}/status")
    public Payment updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        Payment payment = paymentService.getPaymentById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String username = authentication.getName();
            if (payment.getOrder() == null ||
                    payment.getOrder().getUser() == null ||
                    !payment.getOrder().getUser().getUsername().equals(username)) {

                throw new AccessDeniedException(
                        "You can only update payment for your own order");
            }
        }

        return paymentService.updatePaymentStatus(id, status);
    }
}