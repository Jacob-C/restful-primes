package com.example.prime.core;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Common test cases for {@link PrimeSupplier} implementations
 *
 * TODO possible further test cases:
 * <li> using INT_MAX as an upper bound
 * <li> timing out or interrupting a calculation
 */
@RunWith(Parameterized.class)
public class PrimeSupplierTest {

    // pre-calculated list of small primes
    private final static int[] SMALL_PRIMES =
        { 2 ,3 ,5 ,7 ,11 ,13 ,17 , 19 , 23 , 29, 31, 37, 41, 43, 47 };

    // Object under test
    @Parameter
    public PrimeSupplier source;

    /**
     *
     */
    @Parameters(name = "{0}")
    public static Iterable<Object[]> parameters() {
        return asList(
                    testCase(new EratosthenesSieve()),
                    testCase(new SundaramSieve()));
    }

    /**
     * Quick smoke test to check that the first few primes are returned
     * correctly.
     */
    @Test
    public void testSmallBound() throws CalculationException {
        assertArrayEquals(
                new Integer[] {2,3,5,7},
                source.primesUpTo(10).toArray());
    }

    /**
     * Some implementations have optimisations based on the square of the bound.
     * Check that they behave correctly
     */
    @Test
    public void testSquareBound() throws CalculationException {
        assertArrayEquals(
                new Integer[] {2,3,5,7},
                source.primesUpTo(9).toArray());
    }

    /**
     * Check that the function returns primes <i>including</i> the upper bound.
     */
    @Test
    public void testPrimeBound() throws CalculationException {
        assertArrayEquals(
                new Integer[] {2,3,5,7},
                source.primesUpTo(7).toArray());
    }

    /**
     * Check that every number returned is prime.
     *
     * Necessary, but not sufficient to prove correctness. Also need to check
     * that every prime in the interval is returned
     */
    @Test
    public void testLargeBound() throws CalculationException {
        assertThat( source.primesUpTo(10000000), areAllPrime() );
    }

    /**
     * Check that a zero upper limit is rejected
     */
    @Test(expected=IllegalArgumentException.class)
    public void testZeroBounds() throws CalculationException {
        source.primesUpTo(0);
    }

    /**
     * Check that negative upper limits are rejected
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNegativeBounds() throws CalculationException {
        source.primesUpTo(-1);
    }

    /**
     * Matcher factory for testing primality
     */
    private static Matcher<Collection<Integer>> areAllPrime() {
        return new TypeSafeDiagnosingMatcher<Collection<Integer>>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("all values are prime");
            }

            @Override
            protected boolean matchesSafely(
                    final Collection<Integer> item,
                    final Description mismatchDescription) {

                return item.parallelStream()
                           .allMatch(PrimeSupplierTest::isProbablyPrime);
            }
        };
    }

    /**
     * Basic primality test, check that the given number is probably prime
     *
     * Use a relaxed check for the sake of running time
     */
    private static boolean isProbablyPrime(int candidate) {
        final int sqrt = (int) Math.sqrt(candidate);

        for ( int i = 0;
              i < SMALL_PRIMES.length && SMALL_PRIMES[i] <= sqrt;
              i++) {

            if ( candidate % SMALL_PRIMES[i] == 0 ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fluent helper method to make specifying sets of test parameters less
     * painful.
     *
     * @param args set of parameters for the test case
     *
     * @return the parameters, presented as an Object array
     */
    private static Object[] testCase(Object... args) {
        return args;
    }
}
