package hyper.service;

import hyper.exception.ServiceUnavailableException;
import hyper.exception.CustomerNotFoundException;
import hyper.model.Customer;
import hyper.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private static final String CIRCUIT_BREAKER_NAME = "customerService";

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrieveAllCustomersFallback")
    public List<Customer> retrieveAllCustomers() {
        return customerRepository.findAll();
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrieveCustomerFallback")
    public Customer retrieveCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("id-" + id));
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteCustomerFallback")
    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createCustomerFallback")
    public Customer createCustomer(Customer customer) {
        customer.setId(null);
        return customerRepository.save(customer);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateCustomerFallback")
    public boolean updateStudent(UUID id, Customer customer) {
        if (customerRepository.findById(id).isEmpty()) {
            return false;
        }

        customer.setId(id);
        customerRepository.save(customer);
        return true;
    }

    private List<Customer> retrieveAllStudentsFallback(Throwable throwable) {
        throw serviceUnavailable("Student list is temporarily unavailable", throwable);
    }

    private Customer retrieveStudentFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Student retrieval is temporarily unavailable for id " + id, throwable);
    }

    private void deleteStudentFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Student deletion is temporarily unavailable for id " + id, throwable);
    }

    private Customer createStudentFallback(Customer customer, Throwable throwable) {
        throw serviceUnavailable("Student creation is temporarily unavailable", throwable);
    }

    private boolean updateStudentFallback(UUID id, Customer customer, Throwable throwable) {
        throw serviceUnavailable("Student update is temporarily unavailable for id " + id, throwable);
    }

    private ServiceUnavailableException serviceUnavailable(String message, Throwable throwable) {
        return new ServiceUnavailableException(message, throwable);
    }
}
