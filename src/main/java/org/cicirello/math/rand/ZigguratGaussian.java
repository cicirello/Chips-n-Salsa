/*
 * ZigguratGaussian.java:
 * Java port of the GNU Scientific Library's C implementation
 * of the Ziggurat method for generating Gaussian distributed
 * random numbers.
 *
 * Modifications made to port to the Java language are subject to
 * the following copyright:
 *
 * Copyright 2015, 2017-2019 Vincent A. Cicirello, <https://www.cicirello.org/>.
 *
 * ZigguratGaussian.java is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 *
 * ZigguratGaussian.java is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.	See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZigguratGaussian.java.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/* Original Copyright Notice and License of the C Language version.
 * 
 * gauss.c - gaussian random numbers, using the Ziggurat method
 *
 * Copyright (C) 2005  Jochen Voss.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA	 02111-1307	 USA
 */
 
package org.cicirello.math.rand;
 
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>This class provides methods for generating pseudorandom numbers 
 * from a Gaussian distribution using the Ziggurat Algorithm. The Ziggurat 
 * algorithm is significantly faster than the more commonly encountered
 * Polar method, and has some other desirable statistical properties.
 * The ZigguratGaussian class is a Java port of the GNU Scientific 
 * Library's C implementation (Voss, 2005) of the Ziggurat method.
 * In porting to Java, we have made a few subtle, and minor, optimizations that are
 * not worth specifying in the API documentation as they do not impact usage of
 * the class. If interested, see the source code comments, which highlights any
 * differences between this Java implementation and the C implementation on which it
 * is based.</p>
 *
 * <p>This Java implementation originated as part of an effort to speed
 * up the runtime of a parallel genetic algorithm (PGA).  The PGA in
 * question evolved its control parameters (i.e., crossover and mutation rates,
 * etc) using Gaussian mutation.  The only Gaussian implementation within the
 * Java API is the polar method (nextGaussian method of the {@link Random} and
 * {@link ThreadLocalRandom} classes, however the polar method is quite slow
 * relative to other newer available alternatives, such as the Ziggurat method.</p>
 *
 * <p>You can find some experimental data comparing the performance of a sequential
 * genetic algorithm (GA) using this implementation of the Ziggurat method for
 * Gaussian mutation vs using the more common polar method, as well as experimental data
 * for the same comparison but with a PGA, in the following paper:</p>
 * <ul>
 * <li>V. A. Cicirello. 
 * <a href=https://www.cicirello.org/publications/cicirello2018flairs.html target=_top>Impact of 
 * Random Number Generation on Parallel Genetic Algorithms</a>. Proceedings of the Thirty-First 
 * International Florida Artificial Intelligence Research Society Conference, pages 2-7. 
 * AAAI Press, May 2018.</li>
 * </ul>
 *
 * <p>See the following articles for detailed description of the Ziggurat algorithm:</p>
 * <ul>
 * <li>G. Marsaglia and W. W. Tsang. <a href=http://www.jstatsoft.org/v05/i08/ target=_top>The 
 * ziggurat method for generating random variables</a>. 
 * Journal of Statistical Software. 5(1):1–7, 2000.</li>
 * <li>P. H. W. Leong, G. Zhang, D. Lee, W. Luk, and J. Villasenor.
 * <a href=https://www.jstatsoft.org/article/view/v012i07 target=_top>A Comment on the 
 * Implementation of the Ziggurat Method</a>. Journal of Statistical Software. 12(7):1–4, 2005.</li>
 * <li>J. Voss. <a href=http://www.seehuhn.de/pages/ziggurat target=_top>The Ziggurat Method for 
 * Generating Gaussian Random Numbers</a>. GSL: GNU Scientific Library. 2005.</li>
 * </ul>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 8.16.2019
 */
public final class ZigguratGaussian {
	
	/*
	 * A Few Java Implementation Notes:
	 *
	 * - I wanted to support generating Gaussian distributed random 
	 *   numbers via the Ziggurat method using any of the following 
	 *   Java classes as the underlying pseudorandom number generator:
	 *   Random, ThreadLocalRandom, and SplittableRandom.
	 *
	 * - One option to accomplish that would be to have three classes,
	 *   each extending one of those three and either overriding
	 *   nextGaussian (for the two classes that have such a method),
	 *   or in the case of SplittableRandom adding such a method.
	 *   There are problems with this.  First, SplittableRandom is a
	 *   final class so extending is not an option.  Second, although
	 *   ThreadLocalRandom is not final, it is impractical to extend
	 *   none-the-less due to how it manages each local thread's
	 *   ThreadLocalRandom instance, relying on the static method current().
	 *   Overriding ThreadLocalRandom would likely involve recreating most
	 *   of the class's functionality due to this.  So although this option 
	 *   would be fine for the Random class, I desired a common approach 
	 *   to support all three.
	 *
	 * - A second option would be to have three classes, one corresponding to 
	 *   each of the three Java library classes above.  In the cases of
	 *   Random and SplittableRandom, the classes would wrap an instance of 
	 *   the corresponding library class, duplicate its interface, and delegate
	 *   work to the methods of the wrapped object, except for the nextGaussian
	 *   method.  In the case of ThreadLocalRandom, the interface would be
	 *   duplicated with work delegated through calls of the form 
	 *   ThreadLocalRandom.current.nextInt(), etc (except for the nextGaussian
	 *   method).  This option is impractical for a variety of reasons, and is also
	 *   not future proof in the event that additional methods are added to
	 *   the three classes in future versions of the Java API.
	 *
	 * - The third option considered, and the one I went with, is to have a single
	 *   utility class of static methods that take the pseudorandom number generator
	 *   object as a parameter (in the case of Random and SplittableRandom) and
	 *   which simply makes the relevant call to ThreadLocalRandom.current() for the third
	 *   case.  The drawback to this approach is that the programmer who wants to
	 *   use the Gaussian implementation of this class runs the risk of calling the
	 *   existing nextGaussian of either Random or ThreadLocalRandom since it is still
	 *   available to them.  E.g., They'd need to remember to call ZigguratGaussian.nextGaussian()
	 *   rather than ThreadLocalRandom.current().nextGaussian() in the case of using the
	 *   ThreadLocalRandom class; and they'd similarly have to remember to call
	 *   ZigguratGaussian.nextGaussian(r) rather than r.nextGaussian(), assuming that
	 *   r is an instance of the Random class.
	 *
	 * - Some of these issues are discussed more generally by the authors of "JEP 356: Enhanced 
	 *   Pseudo-Random Number Generators" (https://openjdk.java.net/jeps/356).
	 *   I intend to watch the progress of this JEP.  If that JEP is implemented
	 *   in a future version of the Java API, I will likely revisit my approach here to
	 *   this Gaussian implementation.
	 */
	
	/* 
	 * Utility class with nothing but static methods, 
	 * so constructor is private to prevent instantiation.
	 */
	private ZigguratGaussian() {}
	
	// position of right-most step 
	private static final double PARAM_R = 3.44428647676;
	
	// Extremely minor optimization to save multiplication of constants.
	// Not done in C version (but C compiler would probably do this).
	// Probably unnecessary in Java as well (Java JIT might make this optimization).
	private static final double HALF_PARAM_R = 1.72214323838; // 0.5 * PARAM_R
	
	// Another optimization that is not done in the C version.
	// This one probably would not be done by the C compiler, and
	// also probably not done by the Java JIT.
	// Specifically, rather than divide by PARAM_R, we set this constant
	// to 1 / PARAM_R, and then use a multiplication.  Multiplication is
	// faster than division.
	private static final double PARAM_R_INV = 0.2903358959097643; // 1.0 / PARAM_R
	
	// tabulated values for the height of the Ziggurat levels 
	private static final double[] ytab = {
		1, 0.963598623011, 0.936280813353, 0.913041104253,
		0.892278506696, 0.873239356919, 0.855496407634, 0.838778928349,
		0.822902083699, 0.807732738234, 0.793171045519, 0.779139726505,
		0.765577436082, 0.752434456248, 0.739669787677, 0.727249120285,
		0.715143377413, 0.703327646455, 0.691780377035, 0.68048276891,
		0.669418297233, 0.65857233912, 0.647931876189, 0.637485254896,
		0.62722199145, 0.617132611532, 0.607208517467, 0.597441877296,
		0.587825531465, 0.578352913803, 0.569017984198, 0.559815170911,
		0.550739320877, 0.541785656682, 0.532949739145, 0.524227434628,
		0.515614886373, 0.507108489253, 0.498704867478, 0.490400854812,
		0.482193476986, 0.47407993601, 0.466057596125, 0.458123971214,
		0.450276713467, 0.442513603171, 0.434832539473, 0.427231532022,
		0.419708693379, 0.41226223212, 0.404890446548, 0.397591718955,
		0.390364510382, 0.383207355816, 0.376118859788, 0.369097692334,
		0.362142585282, 0.355252328834, 0.348425768415, 0.341661801776,
		0.334959376311, 0.328317486588, 0.321735172063, 0.31521151497,
		0.308745638367, 0.302336704338, 0.29598391232, 0.289686497571,
		0.283443729739, 0.27725491156, 0.271119377649, 0.265036493387,
		0.259005653912, 0.253026283183, 0.247097833139, 0.241219782932,
		0.235391638239, 0.229612930649, 0.223883217122, 0.218202079518,
		0.212569124201, 0.206983981709, 0.201446306496, 0.195955776745,
		0.190512094256, 0.185114984406, 0.179764196185, 0.174459502324,
		0.169200699492, 0.1639876086, 0.158820075195, 0.153697969964,
		0.148621189348, 0.143589656295, 0.138603321143, 0.133662162669,
		0.128766189309, 0.123915440582, 0.119109988745, 0.114349940703,
		0.10963544023, 0.104966670533, 0.100343857232, 0.0957672718266,
		0.0912372357329, 0.0867541250127, 0.082318375932, 0.0779304915295,
		0.0735910494266, 0.0693007111742, 0.065060233529, 0.0608704821745,
		0.056732448584, 0.05264727098, 0.0486162607163, 0.0446409359769,
		0.0407230655415, 0.0368647267386, 0.0330683839378, 0.0293369977411,
		0.0256741818288, 0.0220844372634, 0.0185735200577, 0.0151490552854,
		0.0118216532614, 0.00860719483079, 0.00553245272614, 0.00265435214565
	};
		
	// tabulated values for 2^24 times x[i]/x[i+1],
	// used to accept for U*x[i+1]<=x[i] without any floating point operations 
	private static final int[] ktab = {
		0, 12590644, 14272653, 14988939,
		15384584, 15635009, 15807561, 15933577,
		16029594, 16105155, 16166147, 16216399,
		16258508, 16294295, 16325078, 16351831,
		16375291, 16396026, 16414479, 16431002,
		16445880, 16459343, 16471578, 16482744,
		16492970, 16502368, 16511031, 16519039,
		16526459, 16533352, 16539769, 16545755,
		16551348, 16556584, 16561493, 16566101,
		16570433, 16574511, 16578353, 16581977,
		16585398, 16588629, 16591685, 16594575,
		16597311, 16599901, 16602354, 16604679,
		16606881, 16608968, 16610945, 16612818,
		16614592, 16616272, 16617861, 16619363,
		16620782, 16622121, 16623383, 16624570,
		16625685, 16626730, 16627708, 16628619,
		16629465, 16630248, 16630969, 16631628,
		16632228, 16632768, 16633248, 16633671,
		16634034, 16634340, 16634586, 16634774,
		16634903, 16634972, 16634980, 16634926,
		16634810, 16634628, 16634381, 16634066,
		16633680, 16633222, 16632688, 16632075,
		16631380, 16630598, 16629726, 16628757,
		16627686, 16626507, 16625212, 16623794,
		16622243, 16620548, 16618698, 16616679,
		16614476, 16612071, 16609444, 16606571,
		16603425, 16599973, 16596178, 16591995,
		16587369, 16582237, 16576520, 16570120,
		16562917, 16554758, 16545450, 16534739,
		16522287, 16507638, 16490152, 16468907,
		16442518, 16408804, 16364095, 16301683,
		16207738, 16047994, 15704248, 15472926
	};
		
	// tabulated values of 2^{-24}*x[i] 
	private static final double[] wtab = {
		1.62318314817e-08, 2.16291505214e-08, 2.54246305087e-08, 2.84579525938e-08,
		3.10340022482e-08, 3.33011726243e-08, 3.53439060345e-08, 3.72152672658e-08,
		3.8950989572e-08, 4.05763964764e-08, 4.21101548915e-08, 4.35664624904e-08,
		4.49563968336e-08, 4.62887864029e-08, 4.75707945735e-08, 4.88083237257e-08,
		5.00063025384e-08, 5.11688950428e-08, 5.22996558616e-08, 5.34016475624e-08,
		5.44775307871e-08, 5.55296344581e-08, 5.65600111659e-08, 5.75704813695e-08,
		5.85626690412e-08, 5.95380306862e-08, 6.04978791776e-08, 6.14434034901e-08,
		6.23756851626e-08, 6.32957121259e-08, 6.42043903937e-08, 6.51025540077e-08,
		6.59909735447e-08, 6.68703634341e-08, 6.77413882848e-08, 6.8604668381e-08,
		6.94607844804e-08, 7.03102820203e-08, 7.11536748229e-08, 7.1991448372e-08,
		7.2824062723e-08, 7.36519550992e-08, 7.44755422158e-08, 7.52952223703e-08,
		7.61113773308e-08, 7.69243740467e-08, 7.77345662086e-08, 7.85422956743e-08,
		7.93478937793e-08, 8.01516825471e-08, 8.09539758128e-08, 8.17550802699e-08,
		8.25552964535e-08, 8.33549196661e-08, 8.41542408569e-08, 8.49535474601e-08,
		8.57531242006e-08, 8.65532538723e-08, 8.73542180955e-08, 8.8156298059e-08,
		8.89597752521e-08, 8.97649321908e-08, 9.05720531451e-08, 9.138142487e-08,
		9.21933373471e-08, 9.30080845407e-08, 9.38259651738e-08, 9.46472835298e-08,
		9.54723502847e-08, 9.63014833769e-08, 9.71350089201e-08, 9.79732621669e-08,
		9.88165885297e-08, 9.96653446693e-08, 1.00519899658e-07, 1.0138063623e-07,
		1.02247952126e-07, 1.03122261554e-07, 1.04003996769e-07, 1.04893609795e-07,
		1.05791574313e-07, 1.06698387725e-07, 1.07614573423e-07, 1.08540683296e-07,
		1.09477300508e-07, 1.1042504257e-07, 1.11384564771e-07, 1.12356564007e-07,
		1.13341783071e-07, 1.14341015475e-07, 1.15355110887e-07, 1.16384981291e-07,
		1.17431607977e-07, 1.18496049514e-07, 1.19579450872e-07, 1.20683053909e-07,
		1.21808209468e-07, 1.2295639141e-07, 1.24129212952e-07, 1.25328445797e-07,
		1.26556042658e-07, 1.27814163916e-07, 1.29105209375e-07, 1.30431856341e-07,
		1.31797105598e-07, 1.3320433736e-07, 1.34657379914e-07, 1.36160594606e-07,
		1.37718982103e-07, 1.39338316679e-07, 1.41025317971e-07, 1.42787873535e-07,
		1.44635331499e-07, 1.4657889173e-07, 1.48632138436e-07, 1.50811780719e-07,
		1.53138707402e-07, 1.55639532047e-07, 1.58348931426e-07, 1.61313325908e-07,
		1.64596952856e-07, 1.68292495203e-07, 1.72541128694e-07, 1.77574279496e-07,
		1.83813550477e-07, 1.92166040885e-07, 2.05295471952e-07, 2.22600839893e-07
	};
	
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
		double x, y;
		int sign;

		// Original C code used infinite loop with 2 breaks for escaping.
		// Rewrote as do-while loop since one of the break conditions was at end anyway.
		do {
			// In the original C language version, there were 2
			// calls to the pseudorandom number generator here.
			// One of them was to get a random 8 bit integer for i.
			// And the other was to get a random 24 bit integer for j.
			// The left most bit of i was then used for the random sign
			// and the other 7 bits for i.
			//
			// In this Java language port, I instead make a single call to
			// the nextInt() method to get one random 32 bit integer, using the
			// left most bit for sign, the next 7 bits for i, and the right
			// 24 bits for j.  
			int i = r.nextInt();	  
			sign = (i < 0) ? 1 : -1; 
			int j = i & 0x00ffffff;	
			i = (i >>> 24) & 0x7f;

			x = j * wtab[i];

			if (j < ktab[i]) break;

			if (i < 127) {
				double y0 = ytab[i];
				double y1 = ytab[i+1];
				y = y1 + (y0 - y1) * r.nextDouble();
			} else {
				// Includes a couple optimizations not done in original C version.
				// See the comments where PARAM_R_INV and HALF_PARAM_R are declared
				// for explanation.
				x = PARAM_R - StrictMath.log(1.0 - r.nextDouble()) * PARAM_R_INV;
				y = StrictMath.exp(-PARAM_R * (x - HALF_PARAM_R)) * r.nextDouble();
			}

		} while (y >= StrictMath.exp(-0.5 * x * x));

		return sign * x;
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
		double x, y;
		int sign;

		// Original C code used infinite loop with 2 breaks for escaping.
		// Rewrote as do-while loop since one of the break conditions was at end anyway.
		do {
			// In the original C language version, there were 2
			// calls to the pseudorandom number generator here.
			// One of them was to get a random 8 bit integer for i.
			// And the other was to get a random 24 bit integer for j.
			// The left most bit of i was then used for the random sign
			// and the other 7 bits for i.
			//
			// In this Java language port, I instead make a single call to
			// the nextInt() method to get one random 32 bit integer, using the
			// left most bit for sign, the next 7 bits for i, and the right
			// 24 bits for j.  
			int i = r.nextInt();	  
			sign = (i < 0) ? 1 : -1; 
			int j = i & 0x00ffffff;	
			i = (i >>> 24) & 0x7f;

			x = j * wtab[i];

			if (j < ktab[i]) break;

			if (i < 127) {
				double y0 = ytab[i];
				double y1 = ytab[i+1];
				y = y1 + (y0 - y1) * r.nextDouble();
			} else {
				// Includes a couple optimizations not done in original C version.
				// See the comments where PARAM_R_INV and HALF_PARAM_R are declared
				// for explanation.
				x = PARAM_R - StrictMath.log(1.0 - r.nextDouble()) * PARAM_R_INV;
				y = StrictMath.exp(-PARAM_R * (x - HALF_PARAM_R)) * r.nextDouble();
			}

		} while (y >= StrictMath.exp(-0.5 * x * x));

		return sign * x;
	}
}	
 
 
/* NOTICE: The original C language version (which is Copyright (C) 2005	 Jochen Voss)
 * can be found below.	All code and comments below are directly from
 * the C language version from the GNU Scientific Library.	I have simply 
 * placed comment markers around the C language code below.
 *
 * I have chosen to keep a copy of the original C code intact below as a 
 * reference point for the origin of my Java implementation, and so that the
 * interested programmer can compare my Java version with the original C version.
 *
 * ORIGINAL C VERSION BEGINS BELOW THIS LINE.
 */ 

/*
 * This routine is based on the following article, with a couple of
 * modifications which simplify the implementation.
 *
 *	   George Marsaglia, Wai Wan Tsang
 *	   The Ziggurat Method for Generating Random Variables
 *	   Journal of Statistical Software, vol. 5 (2000), no. 8
 *	   http://www.jstatsoft.org/v05/i08/
 *
 * The modifications are:
 *
 * 1) use 128 steps instead of 256 to decrease the amount of static
 * data necessary.	
 *
 * 2) use an acceptance sampling from an exponential wedge
 * exp(-R*(x-R/2)) for the tail of the base strip to simplify the
 * implementation.	The area of exponential wedge is used in
 * calculating 'v' and the coefficients in ziggurat table, so the
 * coefficients differ slightly from those in the Marsaglia and Tsang
 * paper.
 *
 * See also Leong et al, "A Comment on the Implementation of the
 * Ziggurat Method", Journal of Statistical Software, vol 5 (2005), no 7.
 *
 */

 /*	 
#include <config.h>
#include <math.h>
#include <gsl/gsl_math.h>
#include <gsl/gsl_rng.h>
#include <gsl/gsl_randist.h>
*/

/* position of right-most step */
// #define PARAM_R 3.44428647676

/* tabulated values for the heigt of the Ziggurat levels */
/*
static const double ytab[128] = {
  1, 0.963598623011, 0.936280813353, 0.913041104253,
  0.892278506696, 0.873239356919, 0.855496407634, 0.838778928349,
  0.822902083699, 0.807732738234, 0.793171045519, 0.779139726505,
  0.765577436082, 0.752434456248, 0.739669787677, 0.727249120285,
  0.715143377413, 0.703327646455, 0.691780377035, 0.68048276891,
  0.669418297233, 0.65857233912, 0.647931876189, 0.637485254896,
  0.62722199145, 0.617132611532, 0.607208517467, 0.597441877296,
  0.587825531465, 0.578352913803, 0.569017984198, 0.559815170911,
  0.550739320877, 0.541785656682, 0.532949739145, 0.524227434628,
  0.515614886373, 0.507108489253, 0.498704867478, 0.490400854812,
  0.482193476986, 0.47407993601, 0.466057596125, 0.458123971214,
  0.450276713467, 0.442513603171, 0.434832539473, 0.427231532022,
  0.419708693379, 0.41226223212, 0.404890446548, 0.397591718955,
  0.390364510382, 0.383207355816, 0.376118859788, 0.369097692334,
  0.362142585282, 0.355252328834, 0.348425768415, 0.341661801776,
  0.334959376311, 0.328317486588, 0.321735172063, 0.31521151497,
  0.308745638367, 0.302336704338, 0.29598391232, 0.289686497571,
  0.283443729739, 0.27725491156, 0.271119377649, 0.265036493387,
  0.259005653912, 0.253026283183, 0.247097833139, 0.241219782932,
  0.235391638239, 0.229612930649, 0.223883217122, 0.218202079518,
  0.212569124201, 0.206983981709, 0.201446306496, 0.195955776745,
  0.190512094256, 0.185114984406, 0.179764196185, 0.174459502324,
  0.169200699492, 0.1639876086, 0.158820075195, 0.153697969964,
  0.148621189348, 0.143589656295, 0.138603321143, 0.133662162669,
  0.128766189309, 0.123915440582, 0.119109988745, 0.114349940703,
  0.10963544023, 0.104966670533, 0.100343857232, 0.0957672718266,
  0.0912372357329, 0.0867541250127, 0.082318375932, 0.0779304915295,
  0.0735910494266, 0.0693007111742, 0.065060233529, 0.0608704821745,
  0.056732448584, 0.05264727098, 0.0486162607163, 0.0446409359769,
  0.0407230655415, 0.0368647267386, 0.0330683839378, 0.0293369977411,
  0.0256741818288, 0.0220844372634, 0.0185735200577, 0.0151490552854,
  0.0118216532614, 0.00860719483079, 0.00553245272614, 0.00265435214565
};
*/

/* tabulated values for 2^24 times x[i]/x[i+1],
 * used to accept for U*x[i+1]<=x[i] without any floating point operations */
 /*
static const unsigned long ktab[128] = {
  0, 12590644, 14272653, 14988939,
  15384584, 15635009, 15807561, 15933577,
  16029594, 16105155, 16166147, 16216399,
  16258508, 16294295, 16325078, 16351831,
  16375291, 16396026, 16414479, 16431002,
  16445880, 16459343, 16471578, 16482744,
  16492970, 16502368, 16511031, 16519039,
  16526459, 16533352, 16539769, 16545755,
  16551348, 16556584, 16561493, 16566101,
  16570433, 16574511, 16578353, 16581977,
  16585398, 16588629, 16591685, 16594575,
  16597311, 16599901, 16602354, 16604679,
  16606881, 16608968, 16610945, 16612818,
  16614592, 16616272, 16617861, 16619363,
  16620782, 16622121, 16623383, 16624570,
  16625685, 16626730, 16627708, 16628619,
  16629465, 16630248, 16630969, 16631628,
  16632228, 16632768, 16633248, 16633671,
  16634034, 16634340, 16634586, 16634774,
  16634903, 16634972, 16634980, 16634926,
  16634810, 16634628, 16634381, 16634066,
  16633680, 16633222, 16632688, 16632075,
  16631380, 16630598, 16629726, 16628757,
  16627686, 16626507, 16625212, 16623794,
  16622243, 16620548, 16618698, 16616679,
  16614476, 16612071, 16609444, 16606571,
  16603425, 16599973, 16596178, 16591995,
  16587369, 16582237, 16576520, 16570120,
  16562917, 16554758, 16545450, 16534739,
  16522287, 16507638, 16490152, 16468907,
  16442518, 16408804, 16364095, 16301683,
  16207738, 16047994, 15704248, 15472926
};
*/

/* tabulated values of 2^{-24}*x[i] */
/*
static const double wtab[128] = {
  1.62318314817e-08, 2.16291505214e-08, 2.54246305087e-08, 2.84579525938e-08,
  3.10340022482e-08, 3.33011726243e-08, 3.53439060345e-08, 3.72152672658e-08,
  3.8950989572e-08, 4.05763964764e-08, 4.21101548915e-08, 4.35664624904e-08,
  4.49563968336e-08, 4.62887864029e-08, 4.75707945735e-08, 4.88083237257e-08,
  5.00063025384e-08, 5.11688950428e-08, 5.22996558616e-08, 5.34016475624e-08,
  5.44775307871e-08, 5.55296344581e-08, 5.65600111659e-08, 5.75704813695e-08,
  5.85626690412e-08, 5.95380306862e-08, 6.04978791776e-08, 6.14434034901e-08,
  6.23756851626e-08, 6.32957121259e-08, 6.42043903937e-08, 6.51025540077e-08,
  6.59909735447e-08, 6.68703634341e-08, 6.77413882848e-08, 6.8604668381e-08,
  6.94607844804e-08, 7.03102820203e-08, 7.11536748229e-08, 7.1991448372e-08,
  7.2824062723e-08, 7.36519550992e-08, 7.44755422158e-08, 7.52952223703e-08,
  7.61113773308e-08, 7.69243740467e-08, 7.77345662086e-08, 7.85422956743e-08,
  7.93478937793e-08, 8.01516825471e-08, 8.09539758128e-08, 8.17550802699e-08,
  8.25552964535e-08, 8.33549196661e-08, 8.41542408569e-08, 8.49535474601e-08,
  8.57531242006e-08, 8.65532538723e-08, 8.73542180955e-08, 8.8156298059e-08,
  8.89597752521e-08, 8.97649321908e-08, 9.05720531451e-08, 9.138142487e-08,
  9.21933373471e-08, 9.30080845407e-08, 9.38259651738e-08, 9.46472835298e-08,
  9.54723502847e-08, 9.63014833769e-08, 9.71350089201e-08, 9.79732621669e-08,
  9.88165885297e-08, 9.96653446693e-08, 1.00519899658e-07, 1.0138063623e-07,
  1.02247952126e-07, 1.03122261554e-07, 1.04003996769e-07, 1.04893609795e-07,
  1.05791574313e-07, 1.06698387725e-07, 1.07614573423e-07, 1.08540683296e-07,
  1.09477300508e-07, 1.1042504257e-07, 1.11384564771e-07, 1.12356564007e-07,
  1.13341783071e-07, 1.14341015475e-07, 1.15355110887e-07, 1.16384981291e-07,
  1.17431607977e-07, 1.18496049514e-07, 1.19579450872e-07, 1.20683053909e-07,
  1.21808209468e-07, 1.2295639141e-07, 1.24129212952e-07, 1.25328445797e-07,
  1.26556042658e-07, 1.27814163916e-07, 1.29105209375e-07, 1.30431856341e-07,
  1.31797105598e-07, 1.3320433736e-07, 1.34657379914e-07, 1.36160594606e-07,
  1.37718982103e-07, 1.39338316679e-07, 1.41025317971e-07, 1.42787873535e-07,
  1.44635331499e-07, 1.4657889173e-07, 1.48632138436e-07, 1.50811780719e-07,
  1.53138707402e-07, 1.55639532047e-07, 1.58348931426e-07, 1.61313325908e-07,
  1.64596952856e-07, 1.68292495203e-07, 1.72541128694e-07, 1.77574279496e-07,
  1.83813550477e-07, 1.92166040885e-07, 2.05295471952e-07, 2.22600839893e-07
};
*/

/*
double
gsl_ran_gaussian_ziggurat (const gsl_rng * r, const double sigma)
{
  unsigned long int i, j;
  int sign;
  double x, y;

  while (1)
	{
	  i = gsl_rng_uniform_int (r, 256); //	choose the step 
	  j = gsl_rng_uniform_int (r, 16777216);  // sample from 2^24 
	  sign = (i & 0x80) ? +1 : -1;
	  i &= 0x7f;

	  x = j * wtab[i];

	  if (j < ktab[i])
		break;

	  if (i < 127)
		{
		  double y0, y1, U1;
		  y0 = ytab[i];
		  y1 = ytab[i + 1];
		  U1 = gsl_rng_uniform (r);
		  y = y1 + (y0 - y1) * U1;
		}
	  else
		{
		  double U1, U2;
		  U1 = 1.0 - gsl_rng_uniform (r);
		  U2 = gsl_rng_uniform (r);
		  x = PARAM_R - log (U1) / PARAM_R;
		  y = exp (-PARAM_R * (x - 0.5 * PARAM_R)) * U2;
		}

	  if (y < exp (-0.5 * x * x))
		break;
	}

  return sign * sigma * x;
}
*/

