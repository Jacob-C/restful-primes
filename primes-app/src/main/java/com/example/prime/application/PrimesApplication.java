package com.example.prime.application;

import java.util.HashMap;
import java.util.Map;

import com.example.prime.api.MetadataResults;
import com.example.prime.application.resources.MetadataResource;
import com.example.prime.application.resources.PrimesResource;
import com.example.prime.core.EratosthenesSeive;
import com.example.prime.core.PrimeSupplier;
import com.example.prime.core.SundaramSeive;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Entry-point for the application
 */
public class PrimesApplication extends Application<PrimesConfiguration> {

    public static void main(String[] args) throws Exception {
        new PrimesApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "primes";
    }
    
    @Override
    public void run(
            final PrimesConfiguration configuration, 
            final Environment environment) throws Exception {

        // Install the primes resource
        final Map<String, PrimeSupplier> algorithms = new HashMap<>();
        algorithms.put("sundaram", new SundaramSeive());
        algorithms.put("eratosthenes", new EratosthenesSeive());
        
        final PrimesResource primesResource = 
                new PrimesResource(
                        algorithms::get,
                        configuration.getDefaultBounds(),
                        configuration.getDefaultAlgorithm());
        
        environment.jersey().register(primesResource);
        
        // Install the metadata resource
        final MetadataResource metadataResource = 
                new MetadataResource(
                        MetadataResults.metadataFor(algorithms));
                        
        environment.jersey().register(metadataResource);
    }

}
