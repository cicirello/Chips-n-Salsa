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

import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.operators.bits.BitFlipMutation;
import org.cicirello.search.operators.bits.BitVectorInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use simulated annealing for an optimization
 * problem, where the cost of a solution is integer valued.  In particular, it 
 * provides an example of implementing the IntegerCostOptimizationProblem interface 
 * to define your own optimization problem.
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class CustomIntegerCostProblemExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// If you want to use the library to solve an
		// optimization problem that you define, and
		// if that problem's optimization objective
		// is integer-valued, then you will define the 
		// problem by implementing the IntegerCostOptimizationProblem
		// interface.  
		//
		// The example below defines a problem to search for a
		// vector of bits that is a palindrome (i.e., same bit values
		// if read forward or backward).
		//
		// The library makes extensive use of generics to enable
		// optimizing a variety of structure types.  In this case,
		// the problem is searching the space of vectors of bits.
		// We can use the BitVector class as the type parameter.
		
		class Palindrome implements IntegerCostOptimizationProblem<BitVector> {
			
			private int n;
			
			public Palindrome(int n) {
				this.n = n;
			}
			
			@Override
			public int cost(BitVector candidate) {
				// We need a cost function that if minimized gets us
				// closer and closer to a palindrome.  
				// We'll just count the number of bits in the left half
				// not equal to corresponding bit in the right half.
				int count = 0;
				int i = 0;
				int j = candidate.length()-1;
				if (candidate.length() > n) {
					// if the bit vector is longer than the desired length n,
					// then any extra bits should count against us in the cost.
					count = candidate.length() - n;
				} else if (candidate.length() < n) {
					// if the bit vector is shorter than desired length n,
					// then any missing bits should count against us in the cost.
					count = n - candidate.length();
					j -= count;
					i = count;
				}
				for ( ; i < j; i++, j--) {
					if (candidate.getBit(i) != candidate.getBit(j)) {
						count++;
					}
				}				
				return count;
			}
			
			@Override
			public int value(BitVector candidate) {
				// We'll just call cost for this.  The value method
				// is the optimization objective function we are optimizing,
				// which can be either minimization or maximization.  The cost
				// is specifically a function we are minimizing.  The cost
				// method is most important as all of the search algorithms
				// in the library use cost for solving IntegerCostOptimizationProblems,
				// while they all ignore value.  The value method is simply
				// here for the convenience of the programmer using the library
				// when the actual optimization function differs in some way from
				// the specific one used for problem solving (most likely case is when
				// problem is a maximization problem).
				return cost(candidate);
			}
			
			@Override
			public int minCost() {
				// Implementing this method is OPTIONAL.  The default implementation
				// simply returns Integer.MIN_VALUE.  This method should return a
				// lower bound on the cost function.  If the search ever encounters
				// a solution with that minimum cost, it will terminate early.  So,
				// it is important to implement this in cases where tight bounds on the
				// minimum cost is easily determined.  In this case, we have found a palindrome
				// if the cost is 0 (each bit in left half equals corresponding bit in right half).
				return 0;
			}
			
			@Override
			public boolean isMinCost(int cost) {
				// This method is also OPTIONAL.  The default implementation simply compares
				// cost == minCost(), which is fine in most cases, including this one.
				// I've implemented it anyway for demonstration purposes.
				return cost == 0;
			}
		}
		
		// Define a constant here for the length of the bit vector for our palindrome.
		final int N = 80;
		
		// Construct an instance of the problem we're solving.
		Palindrome problem = new Palindrome(N);
		
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point.  In this example, we'll initialize the search with a random BitVector
		// of the desired length N.  At the present time, the library doesn't have any mutation
		// operators that chance a BitVector's length, so we definitely need to ensure that the
		// random BitVectors generated for initialization are of the correct length to begin with.
		BitVectorInitializer initializer = new BitVectorInitializer(N);
		
		// We need a mutation operator.  We use bit flip mutation in this example,
		// which randomly flips bit values.  The parameter is the probability of 
		// flipping an individual value of a bit.  I've set it here so that an
		// average of 2 bits will be flipped during each call to the mutation operator.
		BitFlipMutation mutation = new BitFlipMutation(2.0 / N);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, and the initializer.  This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// factory methods that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example BitVector.
		SimulatedAnnealing<BitVector> sa = SimulatedAnnealing.createInstance(problem, mutation, initializer);
		
		// Run the simulated annealer.  The parameter of 1000000 means to run
		// 1000000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<BitVector> solution = sa.optimize(1000000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		BitVector x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		// Since this is an integer cost problem, we can use the getCost() 
		// method, which returns an int.
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
		
		// The ProgressTracker holds other data as well, such as the elapsed time
		// from when it was initialized to when the solution it contains was found
		// in nanoseconds.
		long timeBestSolutionFound = tracker.elapsed();
		
		// Simply print the best of run solution.
		System.out.println("\nBest of run solution is:\n" + x + ",\nwith cost of " + cost);
		System.out.printf("\nBest solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0); 
		
		// If you want to know the total run length in number
		// of simulated annealing iterations, you can use the
		// following method call.  This will give you the
		// total number of iterations across all calls to
		// optimize (e.g., if you called it again to execute a second
		// run).  It is possible that this might return a value less than
		// what you passed to optimize, in the event that the optimal solution
		// was found earlier (recall the minCost and isMinCost methods).
		long runLength = sa.getTotalRunLength();
		System.out.println("The search executed " + runLength + " total simulated annealing iterations.");
	}
}