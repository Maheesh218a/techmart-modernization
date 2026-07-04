package com.techmart.api;

import com.techmart.entity.MessageLog;
import com.techmart.repository.MessageLogRepository;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageLogResource {

    @EJB
    private MessageLogRepository messageLogRepository;

    @GET
    public Response getAllMessages() {
        try {
            List<MessageLog> logs = messageLogRepository.findAll();
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
