package com.example.prime.application.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.example.prime.api.MetadataResults;

/**
 * Resource that returns information about the primes resource
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource {
    
    // cached description of supported queries
    private final MetadataResults metadata;
    
    public MetadataResource(final MetadataResults metadata) {
        super();
        this.metadata = metadata;
    }

    /**
     * Return the cached metadata
     */
    @GET
    public MetadataResults getMetadata() {
        return metadata;
    }
}
