package com.example.prime.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.prime.application.resources.PrimesResource;
import com.example.prime.core.PrimeSupplier;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about the supported resources
 */
public class MetadataResults {
    
    private final Primes primes;
    
    /**
     * Information about the {@link PrimesResource}
     */
    public static final class Primes {
        private final Map<String, Object> parameters;

        private Primes(final Collection<String> algorithms) {
            super();
            this.parameters = new HashMap<>();
            parameters.put("upto", "[2,INT_MAX]");
            parameters.put("algorithm", algorithms);
        }
        
        @JsonProperty
        public Map<String, Object> getParameters() {
            return parameters;
        }
    }

    /**
     * Fluent factory method to make constructing meta-data easier
     */
    public static MetadataResults metadataFor(
            final Map<String, PrimeSupplier> algorithms) {
        
        return new MetadataResults(
                new MetadataResults.Primes(algorithms.keySet()));
    }

    public MetadataResults(final Primes primes) {
        Objects.nonNull(primes);
        this.primes = primes;
    }

    @JsonProperty
    public Primes getPrimes() {
        return primes;
    }
}
