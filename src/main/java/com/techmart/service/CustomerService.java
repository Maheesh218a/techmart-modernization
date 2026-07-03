package com.techmart.service;

import com.techmart.entity.Customer;
import com.techmart.repository.CustomerRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class CustomerService {

    @EJB
    private CustomerRepository customerRepository;

    public void registerCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()) != null) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }
        customerRepository.create(customer);
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
