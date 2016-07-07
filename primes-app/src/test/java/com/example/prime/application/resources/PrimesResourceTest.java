package com.example.prime.application.resources;

import static java.util.Collections.unmodifiableList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.example.prime.core.CalculationException;
import com.example.prime.core.PrimeSupplier;
import com.google.common.base.Optional;

/**
 * Check that {@link PrimesResource} appropriately handles invalid inputs and
 * failing calculations.
 */
public class PrimesResourceTest {

    private static final int DEFAULT_BOUNDS = 1000;
    private static final String DEFAULT_ALGORITHM = "test";

    private static final List<Integer> TEST_LIST =
            unmodifiableList(new ArrayList<>());

    // a stub implementation that always returns the same list
    private static final PrimeSupplier EMPTY_IMPLEMENTATION =
            i -> {
                if (i < 2) throw new IllegalArgumentException();
                return TEST_LIST;
            };

    // Currently installed algorithms
    private Map<String, PrimeSupplier> algorithms;

    // Object under test
    private PrimesResource primes;

    @Before
    public void setup() {
        algorithms = new HashMap<>();

        algorithms.put(DEFAULT_ALGORITHM, EMPTY_IMPLEMENTATION);

        primes = new PrimesResource(
                        algorithms::get,
                        DEFAULT_BOUNDS,
                        DEFAULT_ALGORITHM);
    }

    /**
     * If the calculation fails then we expect INTERNAL_SERVER_ERROR
     */
    @Test
    public void testAlgorithmError() {
        algorithms.put("error", i -> {throw new CalculationException();});

        try {
            primes.calculatePrimes(
                    Optional.absent(),
                    Optional.of("error"));

            fail("should have raised exception");

        } catch ( final WebApplicationException ex ) {
            assertErrorCodeIs(INTERNAL_SERVER_ERROR, ex);
        }
    }

    /**
     * Calling an unknown algorithm is a BAD_REQUEST
     */
    @Test
    public void testUnknownAlgorithm() {
        try {
            primes.calculatePrimes(
                    Optional.absent(),
                    Optional.of("bad"));

            fail("should have raised exception");

        } catch ( final WebApplicationException ex ) {
            assertErrorCodeIs(BAD_REQUEST, ex);
        }
    }

    /**
     * If the {@link PrimeSupplier} returns an invalid list then it is an
     * INTERNAL_SERVER_ERROR
     */
    @Test
    public void testBadAlgorithmReturn() {
        algorithms.put("error", i -> null);

        try {
            primes.calculatePrimes(
                    Optional.absent(),
                    Optional.of("error"));

            fail("should have raised exception");

        } catch ( final WebApplicationException ex ) {
            assertErrorCodeIs(INTERNAL_SERVER_ERROR, ex);
        }
    }

    /**
     * Invalid bounds yield a BAD_REQUEST
     */
    @Test
    public void testBadBounds() {
        try {
            primes.calculatePrimes(
                    Optional.of(-2),
                    Optional.absent());

            fail("should have raised exception");

        } catch ( final WebApplicationException ex ) {
            assertErrorCodeIs(BAD_REQUEST, ex);
        }
    }

    /**
     * A well formed request returns the expected list of primes
     */
    @Test
    public void testValidResponse() {
        assertSame(
                TEST_LIST,
                primes.calculatePrimes(
                    Optional.of(5),
                    Optional.absent()).getPrimes());
    }

    /**
     * Check that the thrown exception matches the expected error code
     */
    private void assertErrorCodeIs(
            final Response.Status expected,
            final WebApplicationException actual) {

        assertEquals(
                expected.getStatusCode(),
                actual.getResponse().getStatus());
    }
}
