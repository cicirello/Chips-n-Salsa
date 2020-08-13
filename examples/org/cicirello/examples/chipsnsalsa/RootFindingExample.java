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

import org.cicirello.search.problems.PolynomialRootFinding;
import org.cicirello.search.operators.reals.UndoableGaussianMutation;
import org.cicirello.search.operators.reals.RealValueInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use simulated annealing for a real-valued
 * optimization problem.  The example is specifically of polynomial root finding.
 * That is, given a polynomial p(x), determine one of its roots (i.e., find an x
 * such that p(x)=0).  As an optimization problem, we can search for an x that 
 * minimizes abs(p(x)).
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class RootFindingExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// Construct an instance of the Polynomial Root Finding problem
		// by passing the constructor an array of coefficients for the polynomial.
		// This example is: 12500 - 2500 X - 5 pow(X, 2) + pow(X, 3).
		// It happens to have 3 roots: 50, -50, and 5.
		double[] coefficients = { 12500, -2500, -5, 1 };
		PolynomialRootFinding problem = new PolynomialRootFinding(coefficients);
		
		// This block simply prints an explanation of program.
		System.out.println("This example program uses simulated annealing to");
		System.out.println("approximately find one of the roots of the following");
		System.out.print("polynomial: " + coefficients[0]);
		for (int i = 1; i < coefficients.length; i++) {
			System.out.print(" + " + coefficients[i] + "X");
			if (i > 1) System.out.print("^" + i);
		}
		System.out.println();
		System.out.println("The three roots of this polynomial are 50, -50, and 5.");
		System.out.println("You should get an approximation of one of these.\n");
		
		// We need a mutation operator for simulated annealing.
		// This example uses Gaussian Mutation.  
		// The parameter to the factory method is the standard deviation for the Gaussian.
		// In this example, the standard deviation is 0.1.
		UndoableGaussianMutation<SingleReal> mutation = UndoableGaussianMutation.createGaussianMutation(0.1);
		
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point.  The parameters indicate the range.  In this example, 
		// simulated annealing will initialize the search with a random value from the
		// interval [-100, 100].
		RealValueInitializer initializer = new RealValueInitializer(-100.0, 100.0);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, and the initializer.  This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// constructors that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example SingleReal.
		SimulatedAnnealing<SingleReal> sa = new SimulatedAnnealing<SingleReal>(problem, mutation, initializer);
		
		// Run the simulated annealer.  The parameter of 100000 means to run
		// 100000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<SingleReal> solution = sa.optimize(100000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		SingleReal x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		// For this problem, this should be close to 0 if one of the roots
		// of the polynomial was found precisely.
		double cost = solution.getCostDouble();
		
		// Simply prints the result.
		System.out.printf("End of run solution is: x = %.10f, with cost of %.10f\n", x.get(), cost);
		
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
		System.out.printf("Best of run solution is: x = %.10f, with cost of %.10f\n", x.get(), cost);
		System.out.printf("Best solution was found %.10f seconds into the run.\n", timeBestSolutionFound / 1000000000.0);
	}
	
}