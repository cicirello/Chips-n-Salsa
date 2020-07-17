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
import org.cicirello.search.concurrent.TimedParallelMultistarter;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.permutations.PermutationInitializer;
import org.cicirello.search.operators.permutations.InsertionMutation;
import org.cicirello.search.restarts.ParallelVariableAnnealingLength;
import org.cicirello.search.restarts.VariableAnnealingLength;
import org.cicirello.search.sa.SimulatedAnnealing;
import org.cicirello.search.SolutionCostPair;
import java.util.ArrayList;

/**
 * This example program optimizes an instance of an
 * industrial scheduling problem with jobs that have
 * sequence-dependent setup times (e.g., setup time required
 * prior to processing a job depends upon the job that
 * immediately precedes it on the machine), weights (which
 * indicate the importance of the job), and due dates.
 * The cost function minimized in the example is weighted tardiness.
 * This problem is NP-Hard even without the setup times, which
 * significantly increase problem solving difficulty.
 *
 * This example is based on experiments from a research paper:
 *       Vincent A. Cicirello. Variable Annealing Length and 
 *       Parallelism in Simulated Annealing.  Proceedings of the 
 *       Tenth International Symposium on Combinatorial Search 
 *       (SoCS 2017), pages 2-10. AAAI Press, June 2017.
 * That paper proposes two restart schedules for simulated annealing,
 * Variable Annealing Length (VAL) which varies the length of the runs
 * for the restarts rather than all restarts having a fixed length.
 * The run lengths increase exponentially, and the rationale is based on
 * a commonly encountered phenomena, that a single long run usually outperforms
 * multiple shorter restarts. However, if we end a longer run early (e.g., if
 * we run out of time), the result is usually inferior to if the annealing
 * schedule (i.e., how the temperate varies) was configured for that shorter
 * run length.  VAL tries to balance the run length tradeoff by starting with
 * a short run (tuned for that short length), and progressively increasing the 
 * run length.  The second restart schedule of that paper, P-VAL, spreads the
 * run length schedule of VAL across multiple parallel multistart searches.
 *
 * See the comments within the code for detailed explanations of how the
 * library is used to accomplish this comparison. 
 *
 * @author Vincent A. Cicirello, https://www.cicirello.org/
 */
public class SchedulingExample {
	
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
		
		// This example is based on experiments from a research paper:
		//      Vincent A. Cicirello. Variable Annealing Length and 
		//      Parallelism in Simulated Annealing.  Proceedings of the 
		//      Tenth International Symposium on Combinatorial Search 
		//      (SoCS 2017), pages 2-10. AAAI Press, June 2017.
		// That paper proposes two restart schedules for simulated annealing,
		// Variable Annealing Length (VAL) which varies the length of the runs
		// for the restarts rather than all restarts having a fixed length.
		// The run lengths increase exponentially, and the rationale is based on
		// a commonly encountered phenomena, that a single long run usually outperforms
		// multiple shorter restarts. However, if we end a longer run early (e.g., if
		// we run out of time), the result is usually inferior to if the annealing
		// schedule (i.e., how the temperate varies) was configured for that shorter
		// run length.  VAL tries to balance the run length tradeoff by starting with
		// a short run (tuned for that short length), and progressively increasing the 
		// run length.  The second restart schedule of that paper, P-VAL, spreads the
		// run length schedule of VAL across multiple parallel multistart searches.
		//
		// This example experimentally compares the parallel version of the restart
		// schedule (P-VAL) with multiple parallel runs each using the sequential
		// version of the schedule, as well as using a fixed length run length.
		// This is not the exact set of experiments from the above paper as that
		// would run far longer than a code example should run.
		
		// Some constants to define the specific details of the comparison.
		final int NUM_THREADS = 4;
		
		// For the case of fixed annealing length, we'll define a constant.
		final int FIXED_LENGTH = 100000;
		
		// This constant controls the run time (in seconds) of the comparison.
		final int RUN_TIME = 10;
		
		// We'll use a TimedParallelMultistarter so we can specify run length by
		// time.  This TimedParallelMultistarter is set up for the P-VAL restart 
		// schedule.
		TimedParallelMultistarter<Permutation> pval = new TimedParallelMultistarter<Permutation>(
			// We need to pass an instance of our desired search, which in this
			// case is simulated annealing.  We are using the insertion mutation,
			// which is what was used in the above paper, and we are using the default
			// annealing schedule, the modified Lam schedule, which is an adaptive 
			// annealing schedule.
			SimulatedAnnealing.createInstance(
				problem,
				new InsertionMutation(),
				new PermutationInitializer(NUM_JOBS)),
			// And we need to pass the TimedParallelMultistarter a 
			// list of restart schedules, one for each thread.
			// The ParallelVariableAnnealingLength class will generate
			// such a list for us.
			ParallelVariableAnnealingLength.createRestartSchedules(NUM_THREADS)
		);
		
		// Now we'll use another TimedParallelMultistarter for the case of
		// multiple threads all using the sequential version of the schedule VAL.
		TimedParallelMultistarter<Permutation> val = new TimedParallelMultistarter<Permutation>(
			// We configure simulated annealing the same as above for consistency.
			SimulatedAnnealing.createInstance(
				problem,
				new InsertionMutation(),
				new PermutationInitializer(NUM_JOBS)),
			// In this case, we want all of the parallel instances to use
			// the same restart schedule, so we'll just pass one run length
			// schedule, an instance of the VAL schedule.
			new VariableAnnealingLength(),
			// This last parameter is the number of parallel instances to execute.
			// We didn't need this when we configured pval above because it was
			// implied by the length of the list of restart schedules.
			NUM_THREADS
		);
		
		// Now we'll use another TimedParallelMultistarter for the case of
		// multiple threads all using a fixed run length.
		TimedParallelMultistarter<Permutation> fal = new TimedParallelMultistarter<Permutation>(
			// We configure simulated annealing the same as above for consistency.
			SimulatedAnnealing.createInstance(
				problem,
				new InsertionMutation(),
				new PermutationInitializer(NUM_JOBS)),
			// The fixed annealing length
			FIXED_LENGTH,
			// This last parameter is the number of parallel instances to execute.
			// We didn't need this when we configured pval above because it was
			// implied by the length of the list of restart schedules.
			NUM_THREADS
		);
		
		System.out.println();
		System.out.println("This example program uses parallel multistart");
		System.out.println("simulated annealing to solve an industrial schedule");
		System.out.println("problem. See source code comments for detailed");
		System.out.println("explanation.");
		
		// Executes the searches.  The run time is
		// RUN_TIME time units in length.  Since this example didn't
		// set the time units, the default time units of 1000 ms is used.
		// So the total runtime of each of the calls to optimize below 
		// is RUN_TIME seconds.
		System.out.println();
		System.out.println("Executing a " + RUN_TIME + " second long run of");
		System.out.println("parallel multistart simulated annealing using a");  
		System.out.println("fixed run length for the restarts.");  
		System.out.println("Please wait....");
		SolutionCostPair<Permutation> solutionF = fal.optimize(RUN_TIME);
		
		System.out.println();
		System.out.println("Executing a " + RUN_TIME + " second long run of");
		System.out.println("parallel multistart simulated annealing using ");  
		System.out.println("identical copies of the Variable Annealing Length"); 
		System.out.println("(VAL) schedule of run lengths for the restarts.");  
		System.out.println("Please wait....");
		SolutionCostPair<Permutation> solutionV = val.optimize(RUN_TIME);
		
		System.out.println();
		System.out.println("Executing a " + RUN_TIME + " second long run of");
		System.out.println("parallel multistart simulated annealing using ");  
		System.out.println("the Parallel Variable Annealing Length (P-VAL)"); 
		System.out.println("schedule of run lengths for the restarts.");  
		System.out.println("Please wait....");
		SolutionCostPair<Permutation> solutionP = pval.optimize(RUN_TIME);
		
		// Don't forget to close the TimedParallelMultistarters when you no longer
		// need to do any further optimization to allow it to shut down the threadpool.
		pval.close();
		val.close();
		fal.close();
		
		// Gets the history of solutions found during the search, at
		// each time interval.  In this case, since the time unit is 1000 ms, 
		// gets the history of solutions at 1 second intervals.
		ArrayList<SolutionCostPair<Permutation>> historyF = fal.getSearchHistory();
		ArrayList<SolutionCostPair<Permutation>> historyV = val.getSearchHistory();
		ArrayList<SolutionCostPair<Permutation>> historyP = pval.getSearchHistory();
		
		System.out.println();
		System.out.println("Here is a table of the results.");
		System.out.println("The numbers in the table is the cost function");
		System.out.println("That we are minimizing.");
		System.out.println("Seconds\tFixedLengthRuns\tVAL-allsame\t      PVAL");
		for (int i = 0; i < RUN_TIME; i++) {
			System.out.printf("%7d", (i+1));
			if (i < historyF.size()) System.out.printf("\t%15d", historyF.get(i).getCost());
			else System.out.printf("\t%15s", "----");
			if (i < historyV.size()) System.out.printf("\t%11d", historyV.get(i).getCost());
			else System.out.printf("\t%11s", "----");
			if (i < historyP.size()) System.out.printf("\t%10d", historyP.get(i).getCost());
			else System.out.printf("\t%10s", "----");
			System.out.println();
		}
		
	}
	
}