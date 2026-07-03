package com.techmart.api;

import com.techmart.entity.Cart;
import com.techmart.service.CartService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @EJB
    private CartService cartService;

    @GET
    @Path("/{customerId}")
    public Response getCart(@PathParam("customerId") Long customerId) {
        Cart cart = cartService.getCartByCustomerId(customerId);
        if (cart != null) {
            return Response.ok(cart).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{customerId}/sync")
    public Response syncCart(@PathParam("customerId") Long customerId, Cart frontendCart) {
        Cart cart = cartService.syncCart(customerId, frontendCart);
        if (cart != null) {
            return Response.ok(cart).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{customerId}")
    public Response clearCart(@PathParam("customerId") Long customerId) {
        cartService.clearCart(customerId);
        return Response.noContent().build();
    }
}
