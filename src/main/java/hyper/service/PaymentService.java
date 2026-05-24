package hyper.service;

import hyper.exception.ServiceUnavailableException;
import hyper.exception.PaymentNotFoundException;
import hyper.model.Payment;
import hyper.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private static final String CIRCUIT_BREAKER_NAME = "paymentService";

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrieveAllPaymentsFallback")
    public Page<Payment> retrieveAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrievePaymentFallback")
    public Payment retrievePayment(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("id-" + id));
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deletePaymentFallback")
    public void deletePayment(UUID id) {
        paymentRepository.deleteById(id);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createPaymentFallback")
    public Payment createPayment(Payment payment) {
        payment.setId(null);
        return paymentRepository.save(payment);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updatePaymentFallback")
    public boolean updatePayment(UUID id, Payment payment) {
        if (paymentRepository.findById(id).isEmpty()) {
            return false;
        }

        payment.setId(id);
        paymentRepository.save(payment);
        return true;
    }

    private Page<Payment> retrieveAllPaymentsFallback(Pageable pageable, Throwable throwable) {
        throw serviceUnavailable("Payment list is temporarily unavailable", throwable);
    }

    private Payment retrievePaymentFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Payment retrieval is temporarily unavailable for id " + id, throwable);
    }

    private void deletePaymentFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Payment deletion is temporarily unavailable for id " + id, throwable);
    }

    private Payment createPaymentFallback(Payment payment, Throwable throwable) {
        throw serviceUnavailable("Payment creation is temporarily unavailable", throwable);
    }

    private boolean updatePaymentFallback(UUID id, Payment payment, Throwable throwable) {
        throw serviceUnavailable("Payment update is temporarily unavailable for id " + id, throwable);
    }

    private ServiceUnavailableException serviceUnavailable(String message, Throwable throwable) {
        return new ServiceUnavailableException(message, throwable);
    }
}

