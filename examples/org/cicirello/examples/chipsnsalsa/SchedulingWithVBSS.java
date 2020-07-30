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

import static org.cicirello.examples.chipsnsalsa.ExamplesShared.*;
import org.cicirello.search.problems.scheduling.SingleMachineSchedulingProblemData;
import org.cicirello.search.problems.scheduling.WeightedStaticSchedulingWithSetups;
import org.cicirello.search.problems.scheduling.SingleMachineSchedulingProblem;
import org.cicirello.search.problems.scheduling.WeightedTardiness;
import org.cicirello.search.problems.scheduling.ATCS;
import org.cicirello.search.ss.ValueBiasedStochasticSampling;
import org.cicirello.search.hc.FirstDescentHillClimber;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.permutations.InsertionMutation;
import org.cicirello.search.operators.InitializeBySimpleMetaheuristic;
import org.cicirello.search.ss.HeuristicPermutationGenerator;
import org.cicirello.search.SolutionCostPair;

/**
 * <p>This example program optimizes an instance of an
 * industrial scheduling problem with jobs that have
 * sequence-dependent setup times (e.g., setup time required
 * prior to processing a job depends upon the job that
 * immediately precedes it on the machine), weights (which
 * indicate the importance of the job), and due dates.
 * The cost function minimized in the example is weighted tardiness.
 * This problem is NP-Hard even without the setup times, which
 * significantly increase problem solving difficulty.</p>
 *
 * <p>This example demonstrates using one of the stochastic
 * sampling search algorithms, VBSS, as well as a hybrid of VBSS
 * and hill climbing.</p>
 *
 * <p>See the comments within the code for detailed explanations of how the
 * library is used to accomplish this comparison.</p> 
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class SchedulingWithVBSS {
	
	public static void main(String[] args) {
		printCopyrightAndLicense();
		
		// This example program optimizes an instance of an
		// industrial scheduling problem with jobs that have
		// sequence-dependent setup times (e.g., setup time required
		// prior to processing a job depends upon the job that
		// immediately precedes it on the machine), weights (which
		// indicate the importance of the job), and due dates.
		//
		// These constants are defining the characteristics of the 
		// problem instance used in the example, such as tightness of the
		// due dates, range of the due dates, and the severity of the
		// setup times.  The random problem instance generator defines
		// those parameters in the interval [0.0, 1.0].  
		//
		// The seed parameter just enables generating the same instance,
		// useful in testing (it is the seed for the random number generator).
		final int NUM_JOBS = 100;
		final double DUE_DATE_TIGHTNESS = 0.5;
		final double DUE_DATE_RANGE = 0.5;
		final double SETUP_SEVERITY = 0.5;
		final int SEED = 42;
		
		// You can implement the interface SingleMachineSchedulingProblemData 
		// to define other forms of scheduling problem, or to implement alternative
		// scheduling problem generators, with various characteristics.
		//
		// The WeightedStaticSchedulingWithSetups class is an implementation of that
		// interface that is provided in the library.  It generates random
		// scheduling problem instances for static scheduling problems (i.e., all
		// jobs released at start of problem), and with weights, due dates,
		// and sequence-dependent setup times.  The setup times makes scheduling
		// especially difficult for most cost functions.  In fact, even the simplest
		// cost function, makespan (which is simply to minimize completion time of
		// last job), becomes an NP-Hard problem given setup times.
		SingleMachineSchedulingProblemData schedulingInstance = new WeightedStaticSchedulingWithSetups(
			NUM_JOBS, 
			DUE_DATE_TIGHTNESS, 
			DUE_DATE_RANGE, 
			SETUP_SEVERITY, 
			SEED
		);
			
		// In addition to specifying the details of the scheduling problem instance
		// above, we also need to define the cost function that we are optimizing.
		// We do that using a class that implements the SingleMachineSchedulingProblem
		// interface. SingleMachineSchedulingProblem is a subinterface of 
		// IntegerCostOptimizationProblem, since we assume that our cost function
		// is integer valued (as is the case for most scheduling problem implementations).
		//
		// The library provides implementations of all of the most common
		// scheduling cost functions.  This example uses Weighted Tardiness.
		// Minimizing weighted tardiness is an NP-Hard problem even without
		// setup times.  This instance includes setup times which significantly 
		// increases difficulty of minimizing the cost function.
		SingleMachineSchedulingProblem problem = new WeightedTardiness(schedulingInstance);
		
		// In this example program, we are going to use a stochastic sampling
		// algorithm called VBSS (Value-Biased Stochastic Sampling).  VBSS
		// requires a constructive heuristic.  We will use a heuristic called 
		// ATCS, which is specifically designed for this particular scheduling
		// cost function.  We'll specifically use ATCS with the self-tuned
		// parameters (there is another constructor that enables setting the
		// heuristic's parameters manually).
		ATCS heuristic = new ATCS(problem);
		
		// We'll now construct the instance of the VBSS search.
		// We pass the search a ConstructiveHeuristic, in this case the ATCS heuristic.
		// We also optionally pass an exponent for a polynomial bias function
		// (the default if left out is linear bias).  You are not limited to 
		// polynomial bias functions, as there is
		// a constructor that enables passing a custom bias function.
		ValueBiasedStochasticSampling vbss = new ValueBiasedStochasticSampling(heuristic, 8);
		
		// In the example, we're also going to demonstrate a hybrid of VBSS
		// and hill climbing.  We use a first descent hill climber in the example.
		// Specifically, we're going to locally optimize using hill climbing each
		// sample generated by VBSS.  The hill climber classes are generic
		// and require the representation type, in this case Permutation for 
		// single-machine scheduling problems.
		FirstDescentHillClimber<Permutation> hybrid = new FirstDescentHillClimber<Permutation>(
			// We pass the constructor the problem we're solving.
			problem,
			// The hill climber need a mutation operator. We use
			// InsertionMutation in this case, where each mutation involves
			// removing an element from the permutation and reinserting it in 
			// a different position.
			new InsertionMutation(),
			// Finally, the hill climber needs an Initializer to generate
			// the complete solution that it is to optimize.  In this example,
			// the hill climber is going to locally optimize solutions generated
			// by VBSS.  We can use the InitializeBySimpleMetaheuristic class
			// to give the hill climber an instance of VBSS to use.
			// If we only wanted this hybrid of hill climbing with VBSS, we could
			// just pass our ValueBiasedStochasticSampling from above.  If we did
			// that here, then they would both share a single ProgressTracker, making
			// performance comparison difficult.  So that we can examine problem
			// solving performance separately, we'll create an independent instance
			// of VBSS, but we'll use the same parameters.
			new InitializeBySimpleMetaheuristic<Permutation>(new ValueBiasedStochasticSampling(heuristic, 8))
		);
		
		// We'll also compare to the solution generated by the deterministic application
		// of the constructive heuristic.  We can use the HeuristicPermutationGenerator
		// class for that.  We just pass it the heuristic.
		HeuristicPermutationGenerator atcs = new HeuristicPermutationGenerator(heuristic);
				
		System.out.println();
		System.out.println("This example program demonstrates a stochastic");
		System.out.println("sampling search algorithm, known as VBSS, to ");
		System.out.println("solve an industrial schedule problem. It compares");
		System.out.println("VBSS to a hybrid of VBSS and hill climbing for");
		System.out.println("different numbers of samples, as well as to a");
		System.out.println("single run of the base heuristic.  See source code");
		System.out.println("comments for detailed explanation.");
		System.out.println();
		
		// Execute the deterministic heuristic and output cost function value.
		SolutionCostPair<Permutation> atcsSolution = atcs.optimize();
		System.out.println("Cost of deterministic solution: " + atcsSolution.getCost());
		
		System.out.println();
		System.out.println("Here is a table of the results.");
		System.out.println("The numbers in the table is the cost function");
		System.out.println("that we are minimizing.");
		System.out.println();
		System.out.printf("%7s\t%10s\t%10s\n", "Samples", "VBSS", "hybrid");
		for (int totalSamples = 1, executedSamples = 0; totalSamples <= 100; totalSamples *= 10) {
			int samples = totalSamples - executedSamples;
			// Run each of the searches the number of samples to reach desired total.
			// We'll ignore the return value of a SolutionCostPair, which returns the best of
			// that set of samples, and use the ProgressTracker instead in this example, which
			// has the best of all samples across all calls to optimize.
			vbss.optimize(samples);
			hybrid.optimize(samples);
			System.out.printf("%7d\t%10d\t%10d\n", 
				totalSamples,
				vbss.getProgressTracker().getCost(),
				hybrid.getProgressTracker().getCost()
			);
			executedSamples = totalSamples;
		}
		
	}
	
}