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

package org.cicirello.search.problems.scheduling;

import org.cicirello.permutations.Permutation;

/**
 * <p>Classes that implement single machine scheduling problems should
 * implement this interface to enable heuristic, etc to interact with the
 * data that defines the jobs of the scheduling instance, such as the
 * process times, setup times (if any), due-dates, etc of the jobs.</p>
 *
 * <p>This interface includes methods for accessing all of the common
 * elements of scheduling problems: processing times, due dates, release
 * dates, weights, setup times.  It also has methods for checking whether
 * each of these (except processing times) are present.  Some scheduling
 * cost functions are influenced only by some of these, etc.  Default
 * implementations are included for all of these potential properties
 * except for processing times, where the defaults make the obvious
 * interpretation if that property is not present.  For example,
 * the default release date is 0, for available for scheduling right
 * from the start of the schedule, which is the assumption made by many
 * scheduling problems.  See the various method documentation for
 * details of defaults for the others.</p>
 *
 * <p>The design rationale for providing access to all of these 
 * properties, with defaults, is to have a generalized interface to
 * scheduling problem data to enable easily mixing and matching
 * implementations of scheduling instance generators with 
 * scheduling cost functions.  For example, with this approach,
 * we can take a scheduling problem generator for a common due date
 * problem (which has both early and late weights) and instead optimize
 * weighted tardiness (which doesn't care about early weights).
 * Therefore, we have separated the scheduling cost functions from
 * the classes that represent scheduling problem characteristics
 * in the library.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.13.2020
 */
public interface SingleMachineSchedulingProblemData {
	
	/**
	 * Gets the number of jobs of this scheduling problem instance.
	 * @return the number of jobs
	 */
	int numberOfJobs();
	
	/**
	 * Gets the processing time of a job, which is the amount of
	 * time required by the machine to process the job.
	 * @param j The index of the job, which must be in the interval: [0, numberOfJobs()).
	 * @return the processing time of job j.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs() 
	 */
	int getProcessingTime(int j);
	
	/**
	 * Computes the completion times of all of the jobs if they were scheduled in
	 * the order defined by the specified Permutation.  The completion times are
	 * computed according to the processing times of the jobs, as well as setup times
	 * and ready times if the problem has these.
	 * @param schedule A schedule (i.e., sequence of jobs on the machine).
	 * @return An array of completion times.  Let C be the array of completion times.
	 * C.length must equal schedule.length().  Additionally, C[j] must be the completion
	 * time of job j (and not the completion time of the job 
	 * in position j of the Permutation).  That is, the indexes into C correspond to the
	 * parameter j of the {@link #getProcessingTime} method, and other related methods
	 * of this interface.
	 * @throws IllegalArgumentException if schedule.length() is not equal to numberOfJobs()
	 */
	int[] getCompletionTimes(Permutation schedule);
	
	/**
	 * Gets the due date of a job, for scheduling problems that have due dates.
	 * The meaning of a due date, and its effect on the optimization cost function,
	 * may vary by problem.  See the documentation of the specific scheduling
	 * problem's cost function for details.
	 * .
	 * @param j The index of the job, which must be in the interval: [0, numberOfJobs()).
	 * @return the due date of job j.
	 * @throws UnsupportedOperationException if {@link #hasDueDates} 
	 * returns false.  This is the
	 * behavior if the scheduling problem definition doesn't have due dates.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs() 
	 */
	default int getDueDate(int j) { 
		throw new UnsupportedOperationException("This problem instance doesn't have due dates."); 
	}
	
	/**
	 * Checks whether this single machine scheduling instance has due dates.
	 * @return true iff due dates are present.  
	 */
	default boolean hasDueDates() { return false; }
	
	/**
	 * Gets the release date of a job, for scheduling problems that have release dates.
	 * The default implementation simply returns 0, which is appropriate for 
	 * problems without release dates (i.e., problems in which all jobs are available
	 * for scheduling right at the start).  You can use the {@link #hasReleaseDates}
	 * method to check whether release dates are present.
	 * .
	 * @param j The index of the job, which must be in the interval: [0, numberOfJobs()).
	 * @return the release date of job j.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs() 
	 */
	default int getReleaseDate(int j) { return 0; }
	
	/**
	 * Checks whether this single machine scheduling instance has release dates.
	 * @return true iff release dates are present.  
	 */
	default boolean hasReleaseDates() { return false; }
	
	/**
	 * Gets the weight of a job, for weighted scheduling problems, where a job's
	 * weight indicates its importance or priority (higher weight implies higher
	 * priority).  Many common scheduling problem cost functions use weights
	 * (e.g., weighted tardiness, weighted lateness, etc).
	 * The default implementation simply returns 1, which is appropriate for 
	 * problems without weights (i.e., the unweighted variation of most scheduling
	 * cost functions is equivalent to the weighted version if all weights are 1).  
	 * You can use the {@link #hasWeights} method to check whether weights are present.
	 * 
	 * @param j The index of the job, which must be in the interval: [0, numberOfJobs()).
	 * @return the weight of job j.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs() 
	 */
	default int getWeight(int j) { return 1; }
	
	/**
	 * Checks whether this single machine scheduling instance has weights.
	 * @return true iff weights are present.  
	 */
	default boolean hasWeights() { return false; }
	
	/**
	 * Gets the early weight of a job, for weighted scheduling problems that have
	 * early weights.  For example, there are scheduling problems with a pair
	 * of weights for each job, where there is a different weight penalty for 
	 * completing a job early as there is for completing it late.
	 * The default implementation simply returns 1 
	 * (i.e., the unweighted variation of cost functions involving early weights
	 * is equivalent to the weighted version if all early weights are 1).  
	 * You can use the {@link #hasEarlyWeights} method to check whether 
	 * early weights are present.
	 * 
	 * @param j The index of the job, which must be in the interval: [0, numberOfJobs()).
	 * @return the early weight of job j.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs() 
	 */
	default int getEarlyWeight(int j) { return 1; }
	
	/**
	 * Checks whether this single machine scheduling instance has early weights.
	 * @return true iff early weights are present.  
	 */
	default boolean hasEarlyWeights() { return false; }

	/**
	 * Gets the setup time of a job if it was to immediately follow 
	 * a specified job on the machine, for problems that have sequence
	 * dependent setups.  The default simply returns 0, which is
	 * consistent with problems that don't have setup times (in those cases it
	 * is usually assumed rolled into the process time).
	 * You can use the {@link #hasSetupTimes} method to check 
	 * whether setup times are present.
	 * 
	 * @param i The index of the previous job in the schedule,
	 * which must be in the interval: [0, numberOfJobs()].  Pass
	 * i = numberOfJobs() for the setup time of job j if it is
	 * the first job in the schedule.
	 * @param j The index of the job whose setup time you want, 
	 * which must be in the interval: [0, numberOfJobs()).
	 * @return the setup time of job j if it immediately follows job i.
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs()
	 * i &lt; 0 or i &gt; numberOfJobs()
	 */
	default int getSetupTime(int i, int j) { return 0; };
	
	/**
	 * Gets the setup time of a job if it is the first job
	 * processed on the machine.  
	 * The default simply returns 0, which is
	 * consistent with problems that don't have setup times (in those cases it
	 * is usually assumed rolled into the process time).
	 * You can use the {@link #hasSetupTimes} method to check 
	 * whether setup times are present.
	 * 
	 * @param j The index of the job whose setup time you want, 
	 * which must be in the interval: [0, numberOfJobs()).
	 * @return the setup time of job j if it is the first job processed by the machine
	 * @throws IndexOutOfBoundsException if j &lt; 0 or j &ge; numberOfJobs()
	 */
	default int getSetupTime(int j) { return 0; };
	
	/**
	 * Checks whether this single machine scheduling instance has setup times.
	 * @return true iff setup times are present.  
	 */
	default boolean hasSetupTimes() { return false; }
}