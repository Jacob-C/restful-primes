## Synopsis

An example maven project that allows the user to generate lists of primes
over a RESTful API.

## Installation

1. Check out the sources from github
2. Build with maven using
   `> mvn package`
3. Launch the demo using
   `> java -jar primes-app\target\primes-app-0.0.1-SNAPSHOT.jar server primes-app\src\main\resources\primes.yml`


## API reference
This application exposes two resources: '/' and '/primes'

**Primes**

The resource at '/primes' (e.g. `http://localhost:8080/primes`) will return 
a JSON document containing a list of prime numbers.  
E.g. `{"primes":[2,3,5,7,11,]}`

It supports two parameters
1. `upto` The maximum value that may appear in the list. Defaults to 1000.
   Valid range is 2 >= x >= Integer.MAX_VALUE
2. `algorithm` The method used to generate the list of primes.  Valid values
   are `eratosthenes`, `sundaram` and `error`

**Metadata**

Requesting the resource at '/' (e.g. `http://localhost:8080/`) provides a 
JSON document describing the resource at '/primes'.
E.g. `{"primes":{"parameters":{"upto":"[2,INT_MAX]","algorithm":["sundaram","eratosthenes","error"]}}}`

## Examples
1. Listing primes using all the default parameters
   `GET /primes HTTP/1.1`
   `{"primes":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809,811,821,823,827,829,839,853,857,859,863,877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997]}`
2. Listing primes specifying an upper bound
   `GET /primes?upto=100 HTTP/1.1`
   `{"primes":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97]}`
3. Generating primes using the Sieve of Eratosthenes
   `GET /primes?upto=100&algorithm=eratosthenes HTTP/1.1`
   `{"primes":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97]}`
4. Generating primes using the Sieve of Sundaram
   `GET /primes?upto=100&algorithm=sundaram HTTP/1.1`
   `{"primes":[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97]}`
5. Generating primes using a non-existant algorithm
   `GET /primes?upto=100&algorithm=foo HTTP/1.1`
   `{"code":400,"message":"foo is not a supported algorithm"}`
5. Invoking the an algorithm that is guaranteeed to raise an error
   `GET /primes?algorithm=error HTTP/1.1`
   `{"code":500,"message":"HTTP 500 Internal Server Error"}`
6. Supplying bad parameters
   `GET /primes?upto=-2 HTTP/1.1`
   `{"code":400,"message":"There are no primes below 2"}`


## Todo
1. Multi-threaded prime sieve implementation
2. Improved application launcher  
3. Automated HTTP tests of the client
4. Caching sieve implementation