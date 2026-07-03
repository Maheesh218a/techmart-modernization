package com.techmart.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class JAXRSConfiguration extends Application {
    // This class configures the base path for all REST APIs in the application
}
