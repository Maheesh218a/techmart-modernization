package com.techmart.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class JAXRSConfiguration extends Application {
    // This class configures the base path for all REST APIs in the application
}
