package com.example.prime.core;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Calculates primes using the Seive of Sundaram algorithm
 */
public class SundaramSeive implements PrimeSupplier {

    @Override
    public List<Integer> primesUpTo(final int upperBound) throws CalculationException {

        if ( upperBound <= 1 ) {
            throw new IllegalArgumentException("There are no primes below 2");
        }

        final int upperIdx = (upperBound) / 2;

        // BitSet upper bound is exclusive, add 1 to include upperIdx in the
        // range.
        final BitSet sources = new BitSet(upperIdx + 1);
        sources.set(1, upperIdx + 1);

        for (int j = 1; j < upperIdx; j++ ) {
            // if the inner loop can't do anything then break early
            if ( j + 2*j > upperIdx ) {
                break;
            }

            inner:for ( int i = 1; i <= j; i++) {
                // FIXME this feels like the wrong way to guard against overflow
                long discardIdx = i + j + 2*i*j;

                if (discardIdx <= upperIdx && discardIdx < Integer.MAX_VALUE) {
                    sources.clear((int)discardIdx);
                }
                else {
                    // if we've reached the end of the array for this 'j' then
                    // break out and try the next one rather than looping all
                    // the way to i == j
                    break inner;
                }
            }
        }

        final List<Integer> primes = new ArrayList<>( upperIdx );
        primes.add(2);

        // the final step is to map all of the source values into the primes
        // that they encode. i -> 2*i+1
        return sources.stream()
                      .map(i -> 2*i+1)
                      .filter(i -> i <= upperBound)
                      .boxed()
                      .collect(toCollection(()->primes));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
