/*
 * JUnit test cases for PolarGaussian.
 *
 * Copyright 2019 Vincent A. Cicirello, <https://www.cicirello.org/>.
 *
 * The JUnit test cases for PolarGaussian is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 *
 * The JUnit test cases for PolarGaussian are distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.	See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PolarGaussianTests.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.cicirello.math.rand;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.Arrays;

/**
 * JUnit 4 test cases for the methods of the PolarGaussian class.
 */
public class PolarGaussianTests {
	
	// Test cases use chi square goodness of fit.  This constant 
	// can be used to adjust the number of samples used for this test.
	private static final int EXPECTED_SAMPLES_PER_BUCKET = 50;
	
	// Change to true to see extra statistical output not otherwise used
	// by automated tests (e.g., to see the specific chi square statistic value).  
	// Extra output sent to standard out.
	private static final boolean VERBOSE_OUTPUT = false;
	
	@Test
	public void testRandom1() {
		Random r = new Random(42);
		int[] buckets = new int[20];
		final int N = buckets.length * EXPECTED_SAMPLES_PER_BUCKET;
		for (int i = 0; i < N; i++) {
			int j = whichBucket(PolarGaussian.nextGaussian(r));
			buckets[j]++;
		}
		double chi = chiSquare(buckets);
		if (VERBOSE_OUTPUT) {
			System.out.printf("Random, sigma=1, chi=%5.4f\n", chi);
		}
		assertTrue(chi <= 30.144); // 19 degrees of freedom, 95% percentage point of chi square distribution: 30.144
	}
	
	@Test
	public void testRandom10() {
		Random r = new Random(42);
		int[] buckets = new int[20];
		final int N = buckets.length * EXPECTED_SAMPLES_PER_BUCKET;
		for (int i = 0; i < N; i++) {
			int j = whichBucket(PolarGaussian.nextGaussian(10, r), 10);
			buckets[j]++;
		}
		double chi = chiSquare(buckets);
		if (VERBOSE_OUTPUT) {
			System.out.printf("Random, sigma=10, chi=%5.4f\n", chi);
		}
		assertTrue(chi <= 30.144); // 19 degrees of freedom, 95% percentage point of chi square distribution: 30.144
	}
	
	@Test
	public void testSplittableRandom1() {
		SplittableRandom r = new SplittableRandom(42);
		int[] buckets = new int[20];
		final int N = buckets.length * EXPECTED_SAMPLES_PER_BUCKET;
		for (int i = 0; i < N; i++) {
			int j = whichBucket(PolarGaussian.nextGaussian(r));
			buckets[j]++;
		}
		double chi = chiSquare(buckets);
		if (VERBOSE_OUTPUT) {
			System.out.printf("SplittableRandom, sigma=1, chi=%5.4f\n", chi);
		}
		assertTrue(chi <= 30.144); // 19 degrees of freedom, 95% percentage point of chi square distribution: 30.144
	}
	
	@Test
	public void testSplittableRandom10() {
		SplittableRandom r = new SplittableRandom(42);
		int[] buckets = new int[20];
		final int N = buckets.length * EXPECTED_SAMPLES_PER_BUCKET;
		for (int i = 0; i < N; i++) {
			int j = whichBucket(PolarGaussian.nextGaussian(10, r), 10);
			buckets[j]++;
		}
		double chi = chiSquare(buckets);
		if (VERBOSE_OUTPUT) {
			System.out.printf("SplittableRandom, sigma=10, chi=%5.4f\n", chi);
		}
		assertTrue(chi <= 30.144); // 19 degrees of freedom, 95% percentage point of chi square distribution: 30.144
	}
	
	@Test
	public void testNoParamNextGaussian1() {
		// Since we cannot set the seed for the random number generator
		// in this case (ThreadLocalRandom does not allow setting seeds),
		// we do not do any goodness of fit testing here.  Without the ability 
		// to set a seed, the chi square test statistic would be different
		// each test run, and tests at the 95% level could fail on average 1
		// out of every 20 runs and still be statistically valid.
		
		// Also note that ThreadLocalRandom implements the same pseudorandom
		// number generator algorithm as SplittableRandom, without the split
		// functionality.  And our implementation of PolarGaussian.nextGaussian()
		// delegates computation to PolarGaussian.nextGaussian(Random) by passing
		// ThreadLocalRandom.current() as the param since ThreadLocalRandom extends
		// Random.  So if the other test cases pass the goodness of fit tests, we
		// should be fine here as well.
		
		// We simply test instead that PolarGaussian.nextGaussian() 
		// gives both negative and positive values over a large number of trials.  
		boolean positive = false;
		boolean negative = false;
		for (int i = 0; i < 1000; i++) {
			double x = PolarGaussian.nextGaussian();
			if (x < 0) negative = true;
			else if (x > 0) positive = true;
			if (positive && negative) break;
		}
		assertTrue(positive && negative);
	}
	
	@Test
	public void testNoParamNextGaussian10() {
		// Since we cannot set the seed for the random number generator
		// in this case (ThreadLocalRandom does not allow setting seeds),
		// we do not do any goodness of fit testing here.  Without the ability 
		// to set a seed, the chi square test statistic would be different
		// each test run, and tests at the 95% level could fail on average 1
		// out of every 20 runs and still be statistically valid.
		
		// Also note that ThreadLocalRandom implements the same pseudorandom
		// number generator algorithm as SplittableRandom, without the split
		// functionality.  And our implementation of PolarGaussian.nextGaussian()
		// delegates computation to PolarGaussian.nextGaussian(Random) by passing
		// ThreadLocalRandom.current() as the param since ThreadLocalRandom extends
		// Random.  So if the other test cases pass the goodness of fit tests, we
		// should be fine here as well.
		
		// We simply test instead that PolarGaussian.nextGaussian() 
		// gives both negative and positive values over a large number of trials.  
		boolean positive = false;
		boolean negative = false;
		for (int i = 0; i < 1000; i++) {
			double x = PolarGaussian.nextGaussian(10);
			if (x < 0) negative = true;
			else if (x > 0) positive = true;
			if (positive && negative) break;
		}
		assertTrue(positive && negative);
	}
	
	
	private double chiSquare(int[] buckets) {
		int x = 0;
		for (int e : buckets) {
			x = x + e*e;
		}
		return 1.0 * x / EXPECTED_SAMPLES_PER_BUCKET - buckets.length * EXPECTED_SAMPLES_PER_BUCKET;
	}
	
	private int whichBucket(double x) {
		final double[] upperBoundaries = {
			-1.644853627, -1.281551566, -1.036433389, -0.841621234,
			-0.67448975, -0.524400513, -0.385320466, -0.253347103,
			-0.125661347, 0, 0.125661347, 0.253347103, 0.385320466,
			0.524400513, 0.67448975, 0.841621234, 1.036433389,
			1.281551566, 1.644853627
		};
		for (int i = 0; i < upperBoundaries.length; i++) {
			if (x <= upperBoundaries[i]) return i;
		}
		return upperBoundaries.length;
	}
	
	private int whichBucket(double x, double sigma) {
		final double[] upperBoundaries = {
			-1.644853627, -1.281551566, -1.036433389, -0.841621234,
			-0.67448975, -0.524400513, -0.385320466, -0.253347103,
			-0.125661347, 0, 0.125661347, 0.253347103, 0.385320466,
			0.524400513, 0.67448975, 0.841621234, 1.036433389,
			1.281551566, 1.644853627
		};
		for (int i = 0; i < upperBoundaries.length; i++) {
			upperBoundaries[i] = upperBoundaries[i] * sigma;
		}
		for (int i = 0; i < upperBoundaries.length; i++) {
			if (x <= upperBoundaries[i]) return i;
		}
		return upperBoundaries.length;
	}
}