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
import org.cicirello.permutations.distance.CyclicEdgeDistance;
import org.cicirello.search.problems.PermutationInAHaystack;
import org.cicirello.search.operators.permutations.InsertionMutation;
import org.cicirello.search.operators.permutations.PermutationInitializer;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.hc.SteepestDescentHillClimber;
import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;

/**
 * This example demonstrates how to use a hybrid of simulated annealing and hill climbing for an
 * optimization problem, where we are searching for a permutation that optimizes
 * some objective function.  At the completion of the simulated annealing run, the search will apply
 * a hill climber to further locally optimize the solution before returning. 
 * The example is specifically of the Permutation in a Haystack problem, 
 * which is an optimization problem used in benchmarking algorithms for permutation optimization.
 * In the Permutation in a Haystack problem, we are searching for a permutation of a specified length
 * that minimizes the distance to a target permutation.  The optimal solution is obviously
 * the target permutation itself.  The choice of distance function enables controlling the topology
 * of the search space.  The library includes a large number of mutation operators for permutations.
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class PostHillclimbExample {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// The problem that this example uses searches for
		// a permutation that minimizes a cost function.
		// This variable controls the length of the permutation
		// under optimization.
		final int N = 50;
		
		// In this example, our optimization problem is a benchmarking problem
		// known as the "Permutation in a Haystack".  To generate an instance
		// of this problem, we need to choose a distance metric on permutations as
		// well as decide the length of permutations we are optimizing.  This example
		// uses a distance metric known as Cyclic Edge Distance.
		PermutationInAHaystack problem = 
			new PermutationInAHaystack(new CyclicEdgeDistance(), N);
			
		// Simulated annealing needs to be able to initialize the search with a random 
		// starting point.  The parameter indicates the length of the permutations to generate.
		PermutationInitializer initializer = new PermutationInitializer(N);
		
		// We need a mutation operator for simulated annealing.
		// There are a large number of mutation operators for mutations
		// available in the library to choose from.  
		// This example uses Insertion Mutation, which
		// randomly removes an element from the permutation, and then
		// reinserts it at a different randomly chosen index. 
		//
		// Further, the InsertionMutation also implements the IterableMutationOperator 
		// interface, so it can be used for the hill climber as well as for the 
		// simulated annealer, although there is no requirement to use the same 
		// mutation operator for both.
		InsertionMutation mutation = new InsertionMutation();
		
		// The library has the two most common hill climbers available, First Descent, and
		// Steepest Descent.
		SteepestDescentHillClimber<Permutation> hc = new SteepestDescentHillClimber<Permutation>(problem, mutation, initializer);
		
		// Create the simulated annealer, giving it the problem to solve, the 
		// mutation operator, the initializer, and the hill climber.  
		// This example uses the default
		// annealing schedule, the Modified Lam schedule.  There are other
		// constructors that allow specifying a different annealing schedule.
		// The library uses generics so that simulated annealing can easily be applied
		// to optimizing a variety of types of structures.  So we need to specify the
		// type under optimization, in this example Permutation.
		SimulatedAnnealing<Permutation> sa = new SimulatedAnnealing<Permutation>(problem, mutation, initializer, hc);
		
		// Run the simulated annealer.  The parameter of 1000 means to run
		// 1000 iterations (i.e., an iteration involves generating a random neighbor with
		// the mutation operator, and deciding whether or not to keep it or revert back).
		SolutionCostPair<Permutation> solution = sa.optimize(1000);
		
		// The returned object of the optimize method above contains
		// the end of run solution.  The getSolution method accesses it.
		Permutation x = solution.getSolution();
		
		// The SolutionCostPair object returned by optimize also contains
		// the cost of that solution (in terms of the optimization criteria).
		// In this example, the cost will be equal to the cyclic edge distance
		// between the solution Permutation and the target permutation (the permutation
		// with the N elements in numerical order).
		int cost = solution.getCost();
		
		// Simply prints the result.  Permutation overrides toString, so we can
		// use it where a String is expected, such as in the concatenation below.
		System.out.println("End of run solution is:\n" + x + ",\nwith cost of " + cost);
		
		// The ProgressTracker contained in the SimulatedAnnealing object
		// contains the best of run solution.  Since simulated annealing
		// sometimes takes worsening moves (e.g., to try to escape local optima),
		// the best of run solution may be different than the end of run solution.
		ProgressTracker<Permutation> tracker = sa.getProgressTracker();
		x = tracker.getSolution();
		cost = tracker.getCost();
		
		// Simply print the best of run solution.
		System.out.println("\nBest of run solution is:\n" + x + ",\nwith cost of " + cost);
		
	}
	
}