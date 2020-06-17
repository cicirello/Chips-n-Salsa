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
import org.cicirello.search.operators.bits.BitFlipMutation;
import org.cicirello.search.operators.bits.BitVectorInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * This example demonstrates how to use simulated annealing for an integer-valued
 * optimization problem (i.e., cost function is integer-valued), where we are 
 * searching for a vector of bits that optimizes
 * some objective.  The example is specifically of the OneMax problem, which is often
 * used in benchmarking genetic algorithms and other forms of evolutionary computation.
 * In the OneMax problem, we are searching for a vector of bits of a specified length
 * that maximizes the number of bits equal to a 1.  The optimal solution is obviously
 * all 1s.  The library includes a BitVector class for representing solutions to 
 * optimization problems with a vector of bits.  A BitVector is an indexable vector of
 * bits.  The library also includes two variations of bit-flip mutation, commonly used
 * in genetic algorithms, although here in this example we use it with simulated annealing.
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class BitVectorExample {
	
	public static void printCopyrightAndLicense() {
		System.out.println("Example program for Chips-n-Salsa library.");
		System.out.println("Copyright (C) 2020  Vincent A. Cicirello");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.  This is free");
		System.out.println("software, and you are welcome to redistribute it under certain");
		System.out.println("conditions.  See the GNU General Public License for more"); 
		System.out.println("details: https://www.gnu.org/licenses/gpl-3.0.html");
		System.out.println();
	}
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// This constant is defined to make it easy to change the
		// length of the OneMax problem used in the example.
		final int N = 80;
		
		// Construct an instance of the OneMax problem.
		// The constructor requires no parameters.  The length
		// of the BitVectors is controlled by the Initializer.
		OneMax problem = new OneMax();
		
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point.  The parameter indicates the length of the BitVectors to generate.
		BitVectorInitializer initializer = new BitVectorInitializer(N);
		
		// We need a mutation operator for simulated annealing.
		// We use BitFlipMutation in this example.  The parameter to
		// the constructor is the probability of flipping any bit.
		// For this example, we've passed a probability below that
		// will lead to 3 bits on average being flipped during any
		// individual call to the mutation operator. 
		BitFlipMutation mutation = new BitFlipMutation(3.0/N);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, and the initializer.  This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// factory methods that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example BitVector.
		SimulatedAnnealing<BitVector> sa = SimulatedAnnealing.createInstance(problem, mutation, initializer);
		
		// Run the simulated annealer.  The parameter of 10000 means to run
		// 10000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<BitVector> solution = sa.optimize(10000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		BitVector x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		// In this example, the cost will be equal to the number of 0 bits.
		// A cost of 0 means that all bits are equal to 1, the optimal solution.
		int cost = solution.getCost();
		
		// Simply prints the result.  BitVector overrides toString, so we can
		// use it where a String is expected, such as in the concatenation below.
		System.out.println("End of run solution is:\n" + x + ",\nwith cost of " + cost);
		
		// The ProgressTracker contained in the SimulatedAnnealing object
		// contains the best of run solution.  Since simulated annealing
		// sometimes takes worsening moves (e.g., to try to escape local optima),
		// the best of run solution may be different than the end of run solution.
		ProgressTracker<BitVector> tracker = sa.getProgressTracker();
		x = tracker.getSolution();
		cost = tracker.getCost();
		
		// The ProgressTracker holds other data as well, such as the elpased time
		// from when it was initialized to when the solution it contains was found
		// in nanoseconds.
		long timeBestSolutionFound = tracker.elapsed();
		
		// Simply print the best of run solution.
		System.out.println("\nBest of run solution is:\n" + x + ",\nwith cost of " + cost);
		System.out.printf("Best solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0); 
		
	}
	
}
