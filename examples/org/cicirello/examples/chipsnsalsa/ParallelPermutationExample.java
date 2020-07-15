/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
 *
 * This file is part of Chips-n-Salsa (https://chips-n-salsa.cicirello.org/).
 * 
 * Chips-n-Salsa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Chips-n-Salsa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.cicirello.examples.chipsnsalsa;

import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.ExactMatchDistance;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.PermutationInAHaystack;
import org.cicirello.search.operators.permutations.SwapMutation;
import org.cicirello.search.operators.permutations.PermutationInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.concurrent.ParallelMultistarter;
import org.cicirello.search.SolutionCostPair;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use a multistart simulated annealer, as well 
 * as how to use a parallel multistart simulated annealer.  The example uses a
 * permutation optimization problem.  The example also times how long the searches
 * take, both the sequential multistart, as well as the parallel multistart with
 * different number of parallel instances.
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class ParallelPermutationExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// The problem that this example uses searches for
		// a permutation to minimize a cost function.
		// This variable controls the length of the permutation
		// under optimization.
		int permutationLength = 100;
		
		// This example executes a multistart simulated annealer (both sequentially
		// and in parallel).  All restarts in this example use the same run length
		// in number of simulated annealing iterations.  This variable controls
		// the run length used.
		// Note that the library includes support for restart schedules that vary
		// the run length for each restart.
		int runLength = 10000;
		
		// This example compares the runtime of the parallel multistart simulated
		// annealer with different number of threads.  This variable controls
		// the maximum number of threads considered.  I've set this to 4 since my
		// test system has a quad-core processor.  On my system, therefore, I should
		// get no benefit beyond 4 parallel instances of the search.
		int maxThreads = 4;
		
		// This block of code just generates text explaining the output format, etc.
		System.out.println("This example compares the runtime of multistart simulated");
		System.out.println("annealing with parallel multistart with different numbers");
		System.out.println("of threads.  The optimization problem used is a problem");
		System.out.println("called the Permutation in a Haystack, which is a permutation");
		System.out.println("optimization problem used for benchmarking and testing");
		System.out.println("metaheuristics.");
		System.out.println();
		System.out.println("The column heading abbreviations mean the following:");
		System.out.println("   #restarts: number of restarts");
		System.out.println("   cost: The value we are minimizing.");
		System.out.println("   seq.time: time (in seconds) for the sequential multistart.");
		System.out.println("   p.n.time: time (in seconds) for parallel multistart with n threads.");
		System.out.println("Each cost column corresponds to either sequential multistart or");
		System.out.println("parallel multistart, and specifically corresponds to the one whose");
		System.out.println("time immediately follows.  Within a row of output, the values of");
		System.out.println("cost should be no different statistically, although with only one");
		System.out.println("trial for each number of restarts you cannot actually verify that.");
		System.out.println();
		
		// This block of code just generates a header row for the output data.
		System.out.print("#restarts cost seq.time ");
		for (int i = 1; i <= maxThreads; i++) {
			System.out.print("cost p."+i+".time ");
		}
		System.out.println();
		
		// This example compares runtime of serial vs parallel multistarts for different
		// numbers of restarts.  I'm using 12 restarts as the simplest case because
		// the rest of the example compares running the parallel search 
		// with 1, 2, 3, and 4 threads, and thus I want to make sure the number of restarts
		// in total is equally divisible by 1, 2, 3, and 4.
		for (int numRestarts = 12; numRestarts <= 12000; numRestarts *= 10) {
		
			// In this example, our optimization problem is a benchmarking problem
			// known as the Permutation in a Haystack.  To generate an instance
			// of this problem, we need to choose a distance metric on permutations as
			// well as decide the length of permutations we are optimizing.  This example
			// uses a distance metric known as Exact Match Distance.
			IntegerCostOptimizationProblem<Permutation> problem = 
				new PermutationInAHaystack(new ExactMatchDistance(), permutationLength);
				
			// We need a mutation operator for permutations.  There are many available in 
			// the library.  This example uses one of the simpler ones, Swap Mutation, which
			// randomly exchanges two elements within the permutation.
			SwapMutation mutation = new SwapMutation();
			
			// Our simulated annealer also needs a way to generate random starting 
			// solutions.  For this it needs an object of a class that implements the
			// Initializer<Permutation> interface.  We use the initializer provided
			// in the library that simply generates random permutations of the required
			// length, generated uniformly from among the set of all possible permutations
			// of that length.
			PermutationInitializer init = new PermutationInitializer(permutationLength);
				
			// We can now create a SimulatedAnnealing object for the base form of the search.
			// This example simply uses the default annealing schedule, an adaptive annealing schedule known
			// as the Modified Lam schedule.  There is an optional fourth parameter that can be used
			// to specify a different annealing schedule, and there are several to choose from 
			// in the library.
			SimulatedAnnealing<Permutation> sa = SimulatedAnnealing.createInstance(problem, mutation, init);
			
			// If you want to run a multistart search (i.e., run same search multiple times, taking the
			// best of the solutions found across the restarts), then you can use a Multistarter.
			// A Multistarter takes a search (in this example, simulated annealing), and a run length
			// for how many iterations of simulated annealing's main loop to execute before restarting.
			// Alternatively, the library also provides several restart schedules that vary the run lengths
			// for the restarts in various ways.
			Multistarter<Permutation> saMultiStart = new Multistarter<Permutation>(sa, runLength); 
			
			// This example times multistarts with different number of threads.
			// Record time before call to optimize.
			long timeStart = System.nanoTime();
			
			// You can then call the optimize method on your Multistarter, specifying the number of restarts.
			SolutionCostPair<Permutation> solution = saMultiStart.optimize(numRestarts);
			
			// This example times multistarts with different number of threads.
			// Record time after call to optimize.
			long timeEnd = System.nanoTime();
			
			// compute time in seconds
			double time = (timeEnd-timeStart) / 1000000000.0;
			
			System.out.printf("%9d %4d %8.4f ", numRestarts, solution.getCost(), time);
			
			for (int numThreads = 1; numThreads <= maxThreads; numThreads++) {
				// In this example, we want to use the same total number of restarts as
				// the sequential case above, so divide numRestarts by number of threads.
				int restartsPerThread = numRestarts / numThreads;
				
				// We need a simulated annealer.
				// It is safe to use the same problem instance given the prior search.
				// But it might not be safe to use the same mutation and initializer objects.
				// This example uses the same mutation operator (just a new instance of it)
				// and initializer as the sequential case above.
				mutation = new SwapMutation();
				init = new PermutationInitializer(permutationLength);
				sa = SimulatedAnnealing.createInstance(problem, mutation, init);
				
				// We now use a ParallelMultistarter, which uses multiple threads to run multistart in parallel.
				// The ParallelMultistarter is given the Simulated Annealer and the run length for the restarts,
				// just like initializing a Multistarter (and you can also use restart schedules).
				// You also specify the number of threads.
				ParallelMultistarter<Permutation> pm = new ParallelMultistarter<Permutation>(sa, runLength, numThreads);
				
				// This example times parallel multistarts with different number of threads.
				// Record time before call to optimize.
				timeStart = System.nanoTime();
				
				// You can then call the optimize method on your ParallelMultistarter, 
				// specifying the number of restarts per thread.
				solution = pm.optimize(restartsPerThread);
				
				// This example times parallel multistarts with different number of threads.
				// Record time before call to optimize.
				timeEnd = System.nanoTime();
				
				// compute time in seconds
				time = (timeEnd-timeStart) / 1000000000.0;
				
				// IMPORTANT: When you are done with a ParallelMultistarter, you must call close.
				// Otherwise you risk threads persisting (e.g., it uses a thread pool to save time
				// across multiple calls to the optimize method, and other parallel methods).
				// Alternatively, since the ParallelMultistarter implements AutoCloseable, you can 
				// use a try-with-resources statement to handle automatic calling of close.
				pm.close();
			
				System.out.printf("%4d %8.4f ", solution.getCost(), time);
			}
			System.out.println();
		}			
		
	}
	
}