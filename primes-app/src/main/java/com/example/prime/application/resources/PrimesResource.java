package com.example.prime.application.resources;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.util.List;
import java.util.function.Function;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.example.prime.api.PrimesResults;
import com.example.prime.core.CalculationException;
import com.example.prime.core.PrimeSupplier;
import com.google.common.base.Optional;

/**
 * Exposes the various prime calculation methods as a REST resource
 */
@Path("/primes")
@Produces(MediaType.APPLICATION_JSON)
public class PrimesResource {

    // a mapping from algorithm name to PrimeSuppplier
    private final Function<String,PrimeSupplier> primeSupplierFactory;

    // configured defaults
    private final int defaultBounds;
    private final String defaultAlgorithm;

    /**
     * Construct a new {@link PrimesResource}
     *
     * @param primeSupplierFactory
     *          function that provides a thread-safe {@link PrimeSupplier} or
     *          null when given an algorithm name
     * @param defaultBounds
     *          max value that can appear in the list of primes if not specified
     * @param defaultAlgorithm
     *          calculation type to use if not specified
     */
    public PrimesResource(
            final Function<String,PrimeSupplier> primeSupplierFactory,
            final int defaultBounds,
            final String defaultAlgorithm) {

        this.primeSupplierFactory = primeSupplierFactory;
        this.defaultBounds = defaultBounds;
        this.defaultAlgorithm = defaultAlgorithm;
    }

    /**
     * Get the list of primes up to upperBounds in a form that can be marshalled
     * to JSON
     *
     * @param upperBounds  largest value that may appear in the list of primes
     * @param algorithm  prime list calculation method
     *
     */
    @GET
    @Timed
    public PrimesResults calculatePrimes(
            @QueryParam("upto") final Optional<Integer> upperBounds,
            @QueryParam("algorithm") final Optional<String> algorithm) {

        try {
            // perform any defaulting then calculate the list of primes.
            return calculatePrimes(
                        upperBounds.or(defaultBounds),
                        algorithm.or(defaultAlgorithm));
        }
        catch ( final IllegalArgumentException ex ) {
            // The caller has provided bad parameters.
            //
            // Give them a helpful message and HTTP status
            throw new WebApplicationException(
                            ex.getMessage(),
                            ex,
                            BAD_REQUEST);
        }
        catch ( final CalculationException ex ) {
            // The calculation has failed for some reason.
            //
            // It's not a good idea to return a descriptive message here as it
            // might be exposing too much internal information to (potentially)
            // malicious users.
            //
            // The full stack trace will appear in the log by default
            throw new WebApplicationException(
                            ex,
                            INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculate the list of primes up to and including upperBounds using the
     * specified algorithm.
     *
     * @param upperBounds  highest number that can appear in the list
     * @param algorithm    algorithm for prime calculation
     *
     * @return A list of primes
     *
     * @throws CalculationException  if the calculation fails unexpectedly
     * @throws IllegalArgumentException if the specified algorithm doesn't exist
     * @throws NullPointerException if any argument is null
     */
    private PrimesResults calculatePrimes(
            final Integer upperBounds,
            final String algorithm) throws CalculationException {

        nonNull(upperBounds);
        nonNull(algorithm);

        // Get the implementation of the requested algorithm
        final PrimeSupplier primeSupplier =
                primeSupplierFactory.apply(algorithm);

        if ( primeSupplier == null ) {
            throw new IllegalArgumentException(
                    algorithm + " is not a supported algorithm");
        }

        final List<Integer> primes =
                primeSupplier.primesUpTo(upperBounds);

        // Check that the PrimeSupplier has obeyed its contract
        if ( primes == null ) {
            throw new CalculationException(
                    format("%s (%s) did not return a valid list of primes",
                           algorithm,
                           primeSupplier.getClass().getCanonicalName()));
        }

        // Calculate and return the primes
        return new PrimesResults(primes);
    }
}
