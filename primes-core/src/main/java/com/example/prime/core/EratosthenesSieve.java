package com.example.prime.core;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Calculates primes using the Sieve of Eratosthenes algorithm
 */
public class EratosthenesSieve implements PrimeSupplier {

    @Override
    public List<Integer> primesUpTo(final int upperBound) throws CalculationException {

        if ( upperBound <= 1 ) {
            throw new IllegalArgumentException("There are no primes below 2");
        }

        // The Sieve of Eratosthenes is very space inefficient.  Using a bitset
        // allows us to use ~1 bit per candidate number instead of 32 bits.
        // For large numbers this can be the difference between an
        // OutOfMemoryEcxeption and a successful result.
        //
        // BitSet upper bound is exclusive, add 1 to include upperIdx in the
        // range.
        final BitSet candidates = new BitSet(upperBound + 1);

        // Values below 2 are not primes
        candidates.set(2, upperBound + 1);

        for ( int multiplier = 2; multiplier * multiplier <= upperBound; multiplier++ ) {
            for ( int i = 2; multiplier * i <= upperBound; i++ ) {
                candidates.clear(multiplier*i);
            }
        }

        final List<Integer> primes =
                new ArrayList<>( candidates.cardinality() );

        return candidates.stream()
                         .boxed()
                         .collect(toCollection(()->primes));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
