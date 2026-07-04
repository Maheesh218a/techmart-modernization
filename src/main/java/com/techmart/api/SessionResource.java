package com.techmart.api;

import com.techmart.entity.SessionLog;
import com.techmart.repository.SessionLogRepository;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    @EJB
    private SessionLogRepository sessionLogRepository;

    @GET
    @Path("/active")
    public Response getActiveSessions() {
        try {
            // Find all sessions, then filter active ones
            // In a real scenario, this would be a specific query in the repository
            List<SessionLog> activeSessions = sessionLogRepository.findAll()
                .stream()
                .filter(SessionLog::isActive)
                .collect(Collectors.toList());
                
            return Response.ok(activeSessions).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
