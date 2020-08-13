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

import org.cicirello.search.problems.BoundMax;
import org.cicirello.search.operators.integers.UndoableRandomValueChangeMutation;
import org.cicirello.search.operators.integers.IntegerVectorInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use simulated annealing for an integer-valued
 * optimization problem, where we are searching for a vector of integers that optimizes
 * an integer-valued objective function.  The example is specifically of a problem that this
 * library calls BoundMax.  BoundMax is a generalization of the well-known problem OneMax, 
 * which is often used in benchmarking genetic algorithms and other forms of evolutionary 
 * computation.  In the OneMax problem, we are searching for a vector of bits of a specified length
 * that maximizes the number of bits equal to a 1.  The optimal solution is obviously
 * all 1s.  In BoundMax, instead of a vector of bits, we are optimizing a vector of integers.
 * The integers in the vector are in the interval, [0,B], where B is the bound.  
 * And the problem is to search for
 * the vector that maximizes the number of integers equal to B. 
 * The library includes an IntegerVector class for representing solutions to 
 * optimization problems with a vector of integers, as well as mutation operators for use
 * with simulated annealing
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class IntegerVectorExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// This constant is defined to make it easy to change the
		// length of the BoundMax problem used in the example.
		final int N = 80;
		
		// Construct an instance of the BoundMax problem.
		// The first parameter is the length, and the second is the bound B.
		// In this example, we each integer is in the interval [0, 9].
		BoundMax problem = new BoundMax(N, 9);
		
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point.  In this example, we will pass the problem object as
		// the Initializer as well.  The reason is that BoundMax extends IntegerVectorInitializer 
		// in such a way as to ensure that the components of the vector are initialized within bounds
		// (i.e., within the interval [0, 9]).
		IntegerVectorInitializer initializer = problem;
		
		// We need a mutation operator for simulated annealing.
		// We use a random value change mutation in this example.  
		// The first 2 parameters to the constructor specify the interval
		// that the integers must come from, in this case [0, 9].  The
		// third parameter is the probability of changing any integer's value.
		// For this example, we've passed a probability below that
		// will lead to 2 integers on average being changed during any
		// individual call to the mutation operator.  The last parameter,
		// which is optional, indicates the minimum number of integers to
		// change during a call to the mutation operator.  We've passed 1
		// here to ensure that at least 1 integer is changed during each mutation.
		UndoableRandomValueChangeMutation<IntegerVector> mutation = new UndoableRandomValueChangeMutation<IntegerVector>(0, 9, 2.0/N, 1);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, and the initializer.  This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// constructors that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example IntegerVector.
		SimulatedAnnealing<IntegerVector> sa = new SimulatedAnnealing<IntegerVector>(problem, mutation, initializer);
		
		// Run the simulated annealer.  The parameter of 10000 means to run
		// 10000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<IntegerVector> solution = sa.optimize(10000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		IntegerVector x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		// In this example, the cost will be equal to the number of integers not equal
		// to the bound B, which in this example is B=9.
		// A cost of 0 means that all integers are equal to B, the optimal solution.
		int cost = solution.getCost();
		
		// Prints the result.  
		System.out.println("End of run solution is:");
		for (int i = 0; i < x.length(); i++) System.out.print(x.get(i));
		System.out.println();
		System.out.println("with cost of " + cost);
		
		// The ProgressTracker contained in the SimulatedAnnealing object
		// contains the best of run solution.  Since simulated annealing
		// sometimes takes worsening moves (e.g., to try to escape local optima),
		// the best of run solution may be different than the end of run solution.
		ProgressTracker<IntegerVector> tracker = sa.getProgressTracker();
		x = tracker.getSolution();
		cost = tracker.getCost();
		
		// The ProgressTracker holds other data as well, such as the elpased time
		// from when it was initialized to when the solution it contains was found
		// in nanoseconds.
		long timeBestSolutionFound = tracker.elapsed();
		
		// Simply print the best of run solution.
		System.out.println("\nBest of run solution is:");
		for (int i = 0; i < x.length(); i++) System.out.print(x.get(i));
		System.out.println();
		System.out.println("with cost of " + cost);
		System.out.printf("Best solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0);
		
		// You can call optimize multiple times on a SimulatedAnnealing object
		// to restart.  Each call to optimize, begins at a new random solution.
		// Alternatively, you can call, reoptimize, as in the example below to
		// reanneal the beginning at the current best of run solution.  When
		// reannealing the parameters of the annealing schedule controlling
		// temperature are reinitialized.
		solution = sa.reoptimize(10000);
		x = solution.getSolution();
		cost = solution.getCost();
		
		// Prints the result.  
		System.out.println("\nReannealing end of run solution is:");
		for (int i = 0; i < x.length(); i++) System.out.print(x.get(i));
		System.out.println();
		System.out.println("with cost of " + cost);
		
		// The ProgressTracker always has the best solution found
		// across all runs (i.e., across all calls to optimize and
		// reoptimize).
		x = tracker.getSolution();
		cost = tracker.getCost();
		timeBestSolutionFound = tracker.elapsed();
		
		// Prints the result.  
		System.out.println("\nBest of run solution after reannealing is:");
		for (int i = 0; i < x.length(); i++) System.out.print(x.get(i));
		System.out.println();
		System.out.println("with cost of " + cost);
		System.out.printf("Best solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0);
		
	}
	
}
