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

import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.operators.reals.UndoableGaussianMutation;
import org.cicirello.search.operators.reals.RealValueInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;

/**
 * This example demonstrates how to use simulated annealing for a real-valued
 * optimization problem.  In particular, it provides an example of implementing
 * the OptimizationProblem interface to define your own optimization problem.
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class CustomProblemExample {
	
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
		
		// If you want to use the library to solve an
		// optimization problem that you define, and
		// if that problem's optimization objective
		// is real-valued, then you will define the 
		// problem by implementing the OptimizationProblem
		// interface.
		//
		// The example below defines a problem to find
		// the value of x that minimizes: f(x) = x*x - 5*x + 10.
		//
		// The library makes extensive use of generics to enable
		// optimizing a variety of structure types.  In this case,
		// the problem is a real-valued function optimization problem.
		// We can use the SingleReal class as the type parameter.
		
		class MinPointOfAQuadratic implements OptimizationProblem<SingleReal> {
			
			@Override
			public double cost(SingleReal candidate) {
				// The cost method is the most important method
				// to implement.  The cost method is what you are minimizing.
				// If the problem is a maximization problem, then you must 
				// implement cost in such a way to transform the maximization
				// problem into a minimization problem.
				double x = candidate.get();
				return x*x - 5*x + 10;
			}
			
			@Override
			public double value(SingleReal candidate) {
				// The value method computes the function you are optimizing.
				// For a minimization problem, such as this example, this is
				// the same as the cost method.  It will likely only differ from
				// cost in the maximization case, where value will compute the
				// function you are maximizing, and cost will compute a function
				// that transforms the problem to one of minimization.
				//
				// Another use-case for this method might be if the problem is
				// minimization, but where you can transform the function to one
				// that shares a global minima, but which might have a smoother
				// landscape.  In this case, value will compute the actual function
				// you're optimizing, and cost would compute the easier search
				// landscape.
				// 
				// Whichever of those use-cases applies to you, you should know that
				// none of the search algorithms currently in the library so anything
				// with the value method.  They all rely solely on the cost method.
				// The purpose of the value method is to give you, the programmer,
				// an easy means of computing the value of the solution produced by
				// simulated annealing within the context of the problem.
				return cost(candidate);
			}
			
			@Override
			public double minCost() {
				// The minCost method is OPTIONAL.  In some cases, you won't
				// have an easy means of determining this.  It should return
				// a lower bound on the cost.  The tighter the lower bound, the
				// better.  If the search ever encounters a solution that has 
				// a cost equal to the minCost, the search will terminate.
				// The default implementation simply returns Double.NEGATIVE_INFINITY
				// which is clearly a lower bound for any minimization problem.
				//
				// I'm simply demonstrating implementing this method, by returning
				// what I happen to already know as the minimum possible cost for
				// this example function.  The x that has that minimum is x=2.5,
				// which hopefully when you run the example, simulated annealing will find.
				return 3.75;
			}
			
			@Override
			public boolean isMinCost(double cost) {
				// Like minCost above, this is another OPTIONAL method.
				// The default implementation simply compares cost==minCost().
				// In most cases where you have the ability to implement minCost(),
				// you should probably also implement isMinCost(cost).  The reason
				// is that due to floating-point rounding error, cost might be
				// sufficiently near the actual true minimum cost for your purposes.
				// In this example, I check if the cost is within some epsilon
				// of the true minimum cost.
				return Math.abs(cost-minCost()) < 1e-10;
			}
		}
		
		// Construct an instance of the problem we're solving.
		MinPointOfAQuadratic problem = new MinPointOfAQuadratic();
		
		// We need a mutation operator for SimulatedAnnealing to use.
		// This example uses Gaussian Mutation.  
		// The parameter to the factory method is the standard deviation for the Gaussian.
		// In this example, the standard deviation is 0.3
		// The reason we need a type parameter is that the mutation operators
		// in the library that mutate SingleReal objects also support mutating any
		// other classes in the RealValued hierarchy.
		UndoableGaussianMutation<SingleReal> mutation = UndoableGaussianMutation.createGaussianMutation(0.3);
		
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point, i.e., a random value for x.  In this example, 
		// simulated annealing will initialize the search with a random value from the
		// interval [0, 100].
		RealValueInitializer initializer = new RealValueInitializer(0.0, 100.0);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, and the initializer.  This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// factory methods that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example SingleReal.
		SimulatedAnnealing<SingleReal> sa = SimulatedAnnealing.createInstance(problem, mutation, initializer);
		
		// Run the simulated annealer.  The parameter of 1000000 means to run
		// 1000000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<SingleReal> solution = sa.optimize(1000000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		SingleReal x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		double cost = solution.getCostDouble();
		
		// Simply prints the result.
		System.out.printf("End of run solution is: x = %.12f, with cost of %.12f\n", x.get(), cost);
		
		// The ProgressTracker contained in the SimulatedAnnealing object
		// contains the best of run solution.  Since simulated annealing
		// sometimes takes worsening moves (e.g., to try to escape local optima),
		// the best of run solution may be different than the end of run solution.
		ProgressTracker<SingleReal> tracker = sa.getProgressTracker();
		x = tracker.getSolution();
		cost = tracker.getCostDouble();
		
		// The ProgressTracker holds other data as well, such as the elapsed time
		// from when it was initialized to when the solution it contains was found
		// in nanoseconds.
		long timeBestSolutionFound = tracker.elapsed();
		
		// Simply print the best of run solution.
		System.out.printf("Best of run solution is: x = %.12f, with cost of %.12f\n", x.get(), cost);
		System.out.printf("Best solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0);
		
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