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

import org.cicirello.search.problems.OneMax;
import org.cicirello.search.operators.bits.BitVectorInitializer;
import org.cicirello.search.operators.bits.DefiniteBitFlipMutation;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.concurrent.TimedParallelMultistarter;
import org.cicirello.search.restarts.ParallelVariableAnnealingLength;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import java.util.ArrayList;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use the TimedParallelMultistarter class
 * to execute a parallel multistart simulated annealer for a specified length
 * of time.  The example uses the OneMax problem, which is a problem often
 * used to benchmark genetic algorithms.  In the OneMax problem, you are searching
 * the space of vectors of bits of a specified length for the vector consisting of
 * all 1s (e.g., maximizing the number of 1s in the bit vector).
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class TimedParallelExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// The number of threads to use in this parallel example.
		final int NUM_THREADS = 3;
		
		// This constant defines the length of time for the example, in seconds.
		final int RUN_TIME = 30;
		
		// This example uses the problem known as OneMax, often
		// used in benchmarking genetic algorithms and other 
		// optimizers over the space of vectors of bits.
		// In the OneMax problem, we're searching the space of 
		// vectors of bits of a specified length for the vector
		// of all 1s.
		OneMax problem = new OneMax();
		
		// The length of the bit vector we're optimizing.
		final int BIT_LENGTH = 100000;
		
		// In this example, we're going to use a parallel simulated annealer
		// to solve the OneMax problem.  To do so, we need an Initializer<BitVector>.
		// We're using the BitVectorInitializer class, which simply generates
		// uniformly random bit vectors of the specified length.
		BitVectorInitializer init = new BitVectorInitializer(BIT_LENGTH);
		
		// Our simulated annealer will need a mutation operator.
		// This example uses the DefiniteBitFlipMutation operator, which
		// flips the value of 1 or more randomly selected bits.  The number of
		// random bits to flip is chosen uniformly from the interval [1, b].
		// We pass 1 for this here, which means exactly 1 random bit will be
		// flipped during each mutation.
		DefiniteBitFlipMutation mutation = new DefiniteBitFlipMutation(1);
		
		// Create the simulated annealing object.  
		// We're using the default annealing schedule,
		// the Modified Lam schedule in this example.  
		// There is another factory method that enables
		// specifying a different annealing schedule.
		SimulatedAnnealing<BitVector> sa = SimulatedAnnealing.createInstance(problem, mutation, init);
		
		// Construct the TimedParallelMultistarter.  
		// There are several constructors for this class.
		// In this example, we use the constructor that 
		// takes one metaheuristic object, in this case a SimulatedAnnealing; and
		// a list of restart scheduled for the second parameter.  In this
		// example, we use the Parallel Variable Annealing Length (P-VAL) parallel
		// restart schedule [V.A. Cicirello, 2017].
		TimedParallelMultistarter<BitVector> multistarter = new TimedParallelMultistarter<BitVector>(sa, ParallelVariableAnnealingLength.createRestartSchedules(NUM_THREADS));
		
		// This bock of code just outputs a description of the output of the example,
		// as well as a message highlighting the expected length of time the user will
		// wait for that output, since the call after this block will take several seconds.
		System.out.println("This example program uses a parallel simulated annealer,");
		System.out.println("with the parallel restart schedule known as Parallel");
		System.out.println("Variable Annealing Length to control the length of each");
		System.out.println("restarted run in terms of number of simulated annealing");
		System.out.println("iterations.  The specific problem solved is the OneMax");
		System.out.println("problem.  Once the search completes, the output will show");
		System.out.println("the best solution at one second intervals.");
		System.out.println();
		System.out.println("THE PARALLEL SEARCH IS NOW RUNNING.... PLEASE WAIT....");
		System.out.println("APPROXIMATE WAIT TIME " + RUN_TIME + " seconds....");
		System.out.println();
		
		// This statement executes the search.  The run time is
		// RUN_TIME time units in length.  Since this example didn't
		// set the time units, the default time units of 1000 ms is used.
		// So the total runtime of this call is RUN_TIME seconds.
		SolutionCostPair<BitVector> solution = multistarter.optimize(RUN_TIME);
		
		// Don't forget to close the TimedParallelMultistarter when you no longer
		// need to do any further optimization to allow it to shut down the threadpool.
		multistarter.close();
		
		// Gets the history of solutions found during the search, at
		// each time interval.  In this case, since the time unit is 1000 ms, 
		// gets the history of solutions at 1 second intervals.
		ArrayList<SolutionCostPair<BitVector>> history = multistarter.getSearchHistory();
		
		// Output the history of solutions found at each time interval.
		int i = 0;
		for (SolutionCostPair<BitVector> s : history) {
			i++;
			System.out.println("At " + i + " seconds, number of bits equal to a one is " + problem.value(s.getSolution()));
		}
		if (i < RUN_TIME) {
			System.out.println("\nNote that the search might terminate in less time");
			System.out.println("than requested if the optimal solution was found.");
		}
		
		System.out.println("\nIt is possible for the solution returned by optimize");
		System.out.println("to be better than the last in the history since it is");
		System.out.println("possible for one or more threads to improve upon best");
		System.out.println("solution while the parallel multistarter is in the process");
		System.out.println("of shutting down threads.  It is also possible for it");
		System.out.println("to be worse than the last in the history since simulated");
		System.out.println("annealing sometimes makes worsening moves.");
		
		System.out.println("\nEnd of run solution: number of bits equal to a one is " + problem.value(solution.getSolution()));
		
		System.out.println("\nFor the actual best of run solution, use the solution");
		System.out.println("that is in the ProgressTracker.");
		
		ProgressTracker<BitVector> tracker = multistarter.getProgressTracker();
		System.out.println("\nBest of run solution: number of bits equal to a one is " + problem.value(tracker.getSolution()));
		
		System.out.println("\nTotal number of simulated annealing iterations: " + multistarter.getTotalRunLength());
		
	}
}