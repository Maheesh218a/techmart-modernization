package com.techmart.service;

import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Customer;
import com.techmart.entity.Product;
import com.techmart.repository.CartRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class CartService {

    @EJB
    private CartRepository cartRepository;

    @EJB
    private CustomerService customerService;

    @EJB
    private ProductService productService;

    public Cart getCartByCustomerId(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart == null) {
            Customer customer = customerService.getCustomerById(customerId);
            if (customer != null) {
                cart = new Cart();
                cart.setCustomer(customer);
                cartRepository.create(cart);
            }
        }
        return cart;
    }

    public Cart syncCart(Long customerId, Cart frontendCart) {
        Cart dbCart = getCartByCustomerId(customerId);
        if (dbCart == null) return null;
        
        // Clear existing
        dbCart.getItems().clear();
        
        // Add new
        for (CartItem item : frontendCart.getItems()) {
            Product p = productService.getProductById(item.getProduct().getId());
            if (p != null) {
                CartItem newItem = new CartItem();
                newItem.setProduct(p);
                newItem.setQuantity(item.getQuantity());
                dbCart.addItem(newItem);
            }
        }
        cartRepository.edit(dbCart);
        return dbCart;
    }

    public void clearCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.edit(cart);
        }
    }
}
