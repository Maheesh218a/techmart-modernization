package com.techmart.api;

import com.techmart.entity.Customer;
import com.techmart.service.CustomerService;

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

    @POST
    public Response createCustomer(Customer customer) {
        try {
            customerService.registerCustomer(customer);
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
