package com.techmart.service;

import com.techmart.entity.Customer;
import com.techmart.entity.SessionLog;
import com.techmart.repository.CustomerRepository;
import com.techmart.repository.SessionLogRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class CustomerService {

    @EJB
    private CustomerRepository customerRepository;

    @EJB
    private SessionLogRepository sessionLogRepository;

    public SessionLog createSession(Customer customer, String ipAddress) {
        SessionLog sessionLog = new SessionLog();
        sessionLog.setCustomer(customer);
        sessionLog.setSessionId(UUID.randomUUID().toString());
        sessionLog.setIpAddress(ipAddress);
        sessionLogRepository.create(sessionLog);
        return sessionLog;
    }

    public void endSession(String sessionId) {
        SessionLog sessionLog = sessionLogRepository.findBySessionId(sessionId);
        if (sessionLog != null) {
            sessionLog.setLogoutTime(LocalDateTime.now());
            sessionLog.setActive(false);
            sessionLogRepository.edit(sessionLog);
        }
    }

    public void registerCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()) != null) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }
        if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        customerRepository.create(customer);
    }

    public Customer loginCustomer(String email, String password) {
        return customerRepository.findByEmailAndPassword(email, password);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.find(id);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public void updateCustomerStatus(Long id, boolean isActive) {
        Customer customer = customerRepository.find(id);
        if (customer != null) {
            customer.setActive(isActive);
            customerRepository.edit(customer);
        } else {
            throw new IllegalArgumentException("Customer not found");
        }
    }

    public void updateCustomer(Customer customer) {
        customerRepository.edit(customer);
    }
    
    public void addLoyaltyPoints(Long customerId, int points) {
        Customer customer = customerRepository.find(customerId);
        if (customer != null) {
            int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            customer.setLoyaltyPoints(currentPoints + points);
            customerRepository.edit(customer);
        }
    }
}
