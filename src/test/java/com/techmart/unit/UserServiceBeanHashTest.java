package com.techmart.unit;

import com.techmart.entity.Customer;
import com.techmart.repository.CustomerRepository;
import com.techmart.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceBeanHashTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCustomerRegistration_ValidatesPassword() {
        // Arrange
        Customer customer = new Customer();
        customer.setEmail("test@email.com");
        customer.setPassword(""); // Invalid empty password

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.registerCustomer(customer);
        });

        assertEquals("Password is required", exception.getMessage());
        verify(customerRepository, never()).create(any(Customer.class));
    }

    @Test
    public void testCustomerLogin_ValidCredentials() {
        // Arrange
        String email = "user@domain.com";
        String password = "securePassword123";
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        mockCustomer.setPassword(password);

        when(customerRepository.findByEmailAndPassword(email, password)).thenReturn(mockCustomer);

        // Act
        Customer loggedInUser = customerService.loginCustomer(email, password);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
        verify(customerRepository, times(1)).findByEmailAndPassword(email, password);
    }
}
