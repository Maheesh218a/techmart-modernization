package com.techmart.api;

import com.techmart.entity.Customer;
import com.techmart.entity.SessionLog;
import com.techmart.service.CustomerService;
import com.techmart.service.PerformanceMetricsService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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

    @PUT
    @Path("/{id}/status")
    public Response updateCustomerStatus(@PathParam("id") Long id, @QueryParam("active") boolean active) {
        try {
            customerService.updateCustomerStatus(id, active);
            return Response.ok().entity("{\"status\":\"success\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
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
    public Response login(LoginRequest loginRequest, @Context HttpServletRequest request) {
        try {
            Customer customer = customerService.loginCustomer(loginRequest.email, loginRequest.password);
            if (customer == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password").build();
            }
            
            // Create session log
            String ipAddress = request.getRemoteAddr();
            SessionLog session = customerService.createSession(customer, ipAddress);
            
            // Increment active users metric on successful login
            metricsService.incrementActiveUsers();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("customer", customer);
            responseData.put("sessionId", session.getSessionId());
            
            return Response.ok(responseData).build();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg = e.getCause().getMessage();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    public static class LogoutRequest {
        public String sessionId;
    }

    @POST
    @Path("/logout")
    public Response logout(LogoutRequest request) {
        try {
            if (request != null && request.sessionId != null) {
                customerService.endSession(request.sessionId);
            }
            metricsService.decrementActiveUsers();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
