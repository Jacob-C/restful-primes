package com.example.prime.core;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A modified Segmented Sieve of Erastosthenes that can use multiple threads
 * to calculate its primes.
 */
public class SegmentedSieve implements PrimeSupplier {

    private final ExecutorService executor;

    /**
     * Construct a new {@link SegmentedSieve}
     *
     * @param executor service that executes the segment calculations
     */
    public SegmentedSieve(final ExecutorService executor) {
        nonNull(executor);
        this.executor = executor;
    }

    @Override
    public List<Integer> primesUpTo(int upperBound) throws CalculationException {

        if ( upperBound <= 1 ) {
            throw new IllegalArgumentException("There are no primes below 2");
        }

        final int segmentSize = (int) Math.sqrt(upperBound);

        final List<Callable<List<Integer>>> segmentCalculations =
                new ArrayList<>(segmentSize + 1);

        for ( int lowerBound = 2;
              lowerBound <= upperBound;
              lowerBound += segmentSize) {

            // Handle small final segments when upperBound is not a square
            // number
            int to = Math.min(upperBound, lowerBound + segmentSize - 1);

            // Redeclare 'from' as a final, otherwise eclipse complains that
            // it is not 'effectively final'
            final int ffrom = lowerBound;
            segmentCalculations.add( () -> seiveSegment(ffrom, to) );
        }

        try {
            // Calculate the values of the segments
            final List<Future<List<Integer>>> primeSegments =
                    executor.invokeAll(segmentCalculations);

            // Inject 2 at the beginning of the data, then unpack each segment
            // from its Future
            return Stream.concat( Stream.of(2),
                                  primeSegments.stream()
                                               .flatMap(this::unpackFuture) )
                         // build the resulting stream into a list
                         .collect(Collectors.toList());
        }
        catch (final RuntimeException ex) {
            if (ex.getCause() != null) {
                // If there's an underlying cause then re-throw it
                throw new CalculationException(ex.getCause());
            }
            else {
                throw new CalculationException(ex);
            }
        }
        catch (final InterruptedException  ex) {
            throw new CalculationException(ex);
        }
    }

    /**
     * Find the primes in the range [lowerBound, upperBound]
     *
     * @param lowerBound  smallest value that may appear in the list
     * @param upperBound  largest value that may appear in the list
     *
     * @return list of prime numbers in the segment
     */
    public List<Integer> seiveSegment(
            final int lowerBound,
            final int upperBound) {

        // number of odd values in the interval. '(upperBound & 1)' corrects for
        // an off by one error if 'lowerBound' is an odd number
        final int numOdds =
                (upperBound & 1) + (upperBound - lowerBound) / 2;

        final BitSet oddPrimeCandidates = new BitSet(numOdds);
        oddPrimeCandidates.set(0, numOdds);

        // Remove all the multiples of each odd number up to sqrt(upperBound) 
        for ( int oddFactor = 3;
              oddFactor * oddFactor <= upperBound;
              oddFactor += 2) {

            int smallestMultiple = 
                    ((lowerBound + oddFactor - 1) / oddFactor) * oddFactor;

            // start value must be odd as even numbers have already been
            // excluded
            if ((smallestMultiple & 1) == 0) {
                smallestMultiple += oddFactor;
            }

            // find all odd composite numbers
            for (int composite = smallestMultiple; 
                 composite <= upperBound;
                 composite += 2*oddFactor) {

                int compositeIndex = composite - lowerBound;
                oddPrimeCandidates.clear(compositeIndex/2);
            }
        }

        // need to add 1 if the lowerBound is even to make sure we get odd
        // values out of the mapping function
        final int toOdd = ((lowerBound & 1) == 0) ? 1 : 0;

        // map the candidate indices back into prime numbers
        return oddPrimeCandidates.stream()
                            .map(i -> lowerBound + 2*i + toOdd)
                            .boxed()
                            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * Helper function to retrieve the primes from a Future as part of a stream
     * operation.
     *
     * @param future  containing the data
     *
     * @return stream of integer data
     *
     * @throws RuntimeException if the Future contains an error
     */
    private Stream<Integer> unpackFuture(final Future<List<Integer>> future) {
        try {
            return future.get().stream();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
