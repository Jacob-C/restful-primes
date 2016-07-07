package com.example.prime.core;

import java.util.List;

/**
 * Dummy implementation that always raises an error.
 */
public class ErrorGeneratingPrimeSupplier implements PrimeSupplier {
    @Override
    public List<Integer> primesUpTo(int upperBound) throws CalculationException {
        throw new CalculationException(
                "This implementation always returns an error");
    }
}
