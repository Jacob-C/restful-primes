package com.example.prime.api;

import static java.util.Objects.nonNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A list of primes that can be marshalled into JSON
 */
public class PrimesResults {

    private List<Integer> primes;

    public PrimesResults(final List<Integer> primes) {
        nonNull(primes);

        this.primes = primes;
    }

    @JsonProperty
    public List<Integer> getPrimes() {
        return primes;
    }
}
