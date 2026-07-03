package com.techmart.api;

import com.techmart.entity.Product;
import com.techmart.service.ProductService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @EJB
    private ProductService productService;

    @GET
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GET
    @Path("/active")
    public List<Product> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @GET
    @Path("/{id}")
    public Response getProduct(@PathParam("id") Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return Response.ok(product).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createProduct(Product product) {
        productService.addProduct(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }
}
