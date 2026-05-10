package hyper.service;

import hyper.exception.ServiceUnavailableException;
import hyper.exception.CustomerNotFoundException;
import hyper.model.Customer;
import hyper.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Page<Customer> retrieveAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
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

    private Page<Customer> retrieveAllCustomersFallback(Pageable pageable, Throwable throwable) {
        throw serviceUnavailable("Customer list is temporarily unavailable", throwable);
    }

    private Customer retrieveCustomerFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Customer retrieval is temporarily unavailable for id " + id, throwable);
    }

    private void deleteCustomerFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Customer deletion is temporarily unavailable for id " + id, throwable);
    }

    private Customer createCustomerFallback(Customer customer, Throwable throwable) {
        throw serviceUnavailable("Customer creation is temporarily unavailable", throwable);
    }

    private boolean updateCustomerFallback(UUID id, Customer customer, Throwable throwable) {
        throw serviceUnavailable("Customer update is temporarily unavailable for id " + id, throwable);
    }

    private ServiceUnavailableException serviceUnavailable(String message, Throwable throwable) {
        return new ServiceUnavailableException(message, throwable);
    }
}
