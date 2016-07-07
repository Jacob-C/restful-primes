package com.example.prime.core;

import java.util.List;

public interface PrimeSupplier {

    /**
     * Find all the primes in the range [2,upperBound].
     * 
     * @param upperBound The largest value that may appear in the list of primes
     *  
     * @return A list of prime numbers up to and including 'upperBound'. Must
     *         not return null.
     * @throws CalculationException if the calculation cannot be completed
     */
    List<Integer> primesUpTo(int upperBound) throws CalculationException;

}
