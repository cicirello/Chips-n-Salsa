/*
 * PolarGaussian: Java implementation of the Polar Method
 * for generating Gaussian distributed random numbers.
 *
 * Copyright 2015, 2017-2019 Vincent A. Cicirello, <https://www.cicirello.org/>.
 *
 * PolarGaussian is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 *
 * PolarGaussian is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.	See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PolarGaussian.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
 
package org.cicirello.math.rand;
 
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>This class provides methods for generating pseudorandom numbers 
 * from a Gaussian distribution using the classic Polar Method. Other 
 * methods exist that are faster than Polar, and with superior statistical
 * properties over the Polar method.  One such algorithm is the Ziggurat
 * method, implemented in the {@link ZigguratGaussian} class.  The Polar method
 * implementation provided in the PolarGaussian class was originally implemented
 * as part of an experimental study comparing the effects of different Gaussian
 * algorithms on the performance of a genetic algorithm.  It is included here
 * in this repository, however, if you are looking for a fast algorithm for 
 * generating Gaussian distributed random numbers, we suggest you consider
 * the {@link ZigguratGaussian} class instead.</p>
 *
 * <p>It should be noted that the Java API includes a polar method implementation
 * in both the {@link Random} and {@link ThreadLocalRandom} classes.  However, the
 * experimental study mentioned above also included the use of {@link SplittableRandom}
 * which does not provide any methods for generating Gaussian distributed random numbers.
 * The {@link SplittableRandom} class is also declared final, so extending to add 
 * such a method was not an option.  Our solution was a static method in this
 * class with a parameter for the underlying pseudorandom number generator (PRNG).
 * We chose to do the same for {@link Random} and {@link ThreadLocalRandom} so that 
 * our approach was consistent across all 3 PRNGs used in the study.</p>
 *
 * <p>You can find some experimental data comparing the performance of a sequential
 * genetic algorithm (GA) using this implementation of the Polar method for
 * Gaussian mutation vs using the faster Ziggurat method, as well as experimental data
 * for the same comparison but with a Parallel GA, in the following paper:</p>
 * <ul>
 * <li>V. A. Cicirello. 
 * <a href=https://www.cicirello.org/publications/cicirello2018flairs.html target=_top>Impact of 
 * Random Number Generation on Parallel Genetic Algorithms</a>. Proceedings of the Thirty-First 
 * International Florida Artificial Intelligence Research Society Conference, pages 2-7. 
 * AAAI Press, May 2018.</li>
 * </ul>
 * 
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 8.15.2019
 */
public final class PolarGaussian {
	
	/*
	 * Used to hold the second of the pair of Gaussian values.  Every other
	 * call to nextGaussian (by a thread) uses the value stored here.
	 */
	private static final ThreadLocal<Double> nextG = new ThreadLocal<Double>();
	
	/* 
	 * Utility class with nothing but static methods, 
	 * so constructor is private to prevent instantiation.
	 */
	private PolarGaussian() {}
		
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation, sigma, or your choosing.
	 * {@link ThreadLocalRandom} is used as the pseudorandom number generator for the 
	 * source of randomness.
	 * @param sigma The standard deviation of the Gaussian.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation sigma.
	 */
	public static double nextGaussian(double sigma) {
		return sigma * nextGaussian(ThreadLocalRandom.current());
	}
	
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation, sigma, or your choosing.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param r The pseudorandom number generator to use for the 
	 * source of randomness.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation sigma.
	 */
	public static double nextGaussian(double sigma, Random r) {
		return sigma * nextGaussian(r);
	}
	
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation, sigma, or your choosing.
	 * @param sigma The standard deviation of the Gaussian.
	 * @param r The pseudorandom number generator to use for the 
	 * source of randomness.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation sigma.
	 */
	public static double nextGaussian(double sigma, SplittableRandom r) {
		return sigma * nextGaussian(r);
	}
	
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation 1.
	 * {@link ThreadLocalRandom} is used as the pseudorandom number generator for the 
	 * source of randomness.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation 1.
	 */
	public static double nextGaussian() {
		return nextGaussian(ThreadLocalRandom.current());
	}
	
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation 1.
	 * @param r The pseudorandom number generator to use for the 
	 * source of randomness.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation 1.
	 */
	public static double nextGaussian(Random r) {
		Double next = nextG.get();
		if (next != null) {
			nextG.set(null);
			return next;
		} else {
			double v1 = 0;
			double v2 = 0; 
			double s = 0;
			while (s >= 1 || s == 0) {
				v1 = 2 * r.nextDouble() - 1; 
				v2 = 2 * r.nextDouble() - 1; 
				s = v1 * v1 + v2 * v2;
			}
			double m = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
			nextG.set(v2 * m);
			return v1 * m;
		}
	}
	
	/**
	 * Generates a random number from a Gaussian distribution with
	 * mean 0 and standard deviation 1.
	 * @param r The pseudorandom number generator to use for the 
	 * source of randomness.
	 * @return A random number from a Gaussian distribution with mean 0 and
	 * standard deviation 1.
	 */
	public static double nextGaussian(SplittableRandom r) {
		Double next = nextG.get();
		if (next != null) {
			nextG.set(null);
			return next;
		} else {
			double v1 = 0;
			double v2 = 0; 
			double s = 0;
			while (s >= 1 || s == 0) {
				v1 = 2 * r.nextDouble() - 1; 
				v2 = 2 * r.nextDouble() - 1; 
				s = v1 * v1 + v2 * v2;
			}
			double m = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
			nextG.set(v2 * m);
			return v1 * m;
		}
	}
}	
 


