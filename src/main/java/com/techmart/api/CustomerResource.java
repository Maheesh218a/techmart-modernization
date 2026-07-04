package com.techmart.api;

import com.techmart.entity.Customer;
import com.techmart.service.CustomerService;
import com.techmart.service.PerformanceMetricsService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @EJB
    private CustomerService customerService;

    @EJB
    private PerformanceMetricsService metricsService;

    @GET
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            return Response.ok(customer).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String phone;
        public String address;
        public String password;
        public Boolean active;
    }

    @POST
    public Response createCustomer(RegisterRequest request) {
        try {
            Customer customer = new Customer();
            customer.setName(request.name);
            customer.setEmail(request.email);
            customer.setPhone(request.phone);
            customer.setAddress(request.address);
            customer.setPassword(request.password);
            if (request.active != null) {
                customer.setActive(request.active);
            }
            customerService.registerCustomer(customer);
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg = e.getCause().getMessage();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            Customer customer = customerService.loginCustomer(request.email, request.password);
            if (customer == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password").build();
            }
            
            // Increment active users metric on successful login
            metricsService.incrementActiveUsers();
            
            return Response.ok(customer).build();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg = e.getCause().getMessage();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        try {
            metricsService.decrementActiveUsers();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
