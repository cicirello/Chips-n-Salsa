/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 
package org.cicirello.search.sa;

import org.cicirello.search.concurrent.Splittable;
import java.util.Arrays;

/**
 * An AcceptanceTracker can be used to extract fine-grained information
 * about the behavior of an annealing schedule across several runs of
 * simulated annealing. Specifically, it can be used to compute the
 * rate of neighbor acceptance, across a set of runs of SA, as it changes
 * from the beginning of the run to the end of the run.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.5.2021
 */
public final class AcceptanceTracker implements AnnealingSchedule {
	
	private final AnnealingSchedule schedule;
	private int[] acceptanceCounts;
	private int numRuns;
	private int iteration;
	
	/**
	 * Constructs the AcceptanceTracker.
	 * @param schedule The AnnealingSchedule object.
	 */
	public AcceptanceTracker(AnnealingSchedule schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * Computes the acceptance rate for a specific iteration number
	 * computed across all runs since either the last call to
	 * {@link #reset} or since the last run of simulated annealing with
	 * a different run length.
	 *
	 * @param iterationIndex The iteration number of interest, which must
	 * be in the interval: 0 &le; iterationIndex &lt; maxEvals, where
	 * maxEvals is the run length of simulation annealing.
	 *
	 * @throws ArrayIndexOutOfBoundsException if iterationIndex is negative
	 * or too high.
	 * @throws NullPointerException if reset has not been called and no runs
	 * of simulated annealing have been performed.
	 */
	public double getAcceptanceRate(int iterationIndex) {
		return acceptanceCounts[iterationIndex] / ((double)numRuns);
	}
	
	/**
	 * Resets the AcceptanceTracker.
	 * @param maxEvals The length of the simulated annealing run.
	 * @throws IllegalArgumentException if maxEvals &le; 0.
	 */
	public void reset(int maxEvals) {
		if (maxEvals <= 0) throw new IllegalArgumentException("maxEvals must be positive");
		if (acceptanceCounts == null || acceptanceCounts.length != maxEvals) {
			acceptanceCounts = new int[maxEvals];
		} else {
			Arrays.fill(acceptanceCounts, 0);
		}
		numRuns = 0;
	}
	
	@Override
	public void init(int maxEvals) {
		schedule.init(maxEvals);
		if (acceptanceCounts == null || acceptanceCounts.length != maxEvals) {
			reset(maxEvals);
		}
		numRuns++;
		iteration = 0;
	}
	
	@Override
	public boolean accept(double neighborCost, double currentCost) {
		boolean didAccept = schedule.accept(neighborCost, currentCost);
		if (iteration < acceptanceCounts.length) {
			if (didAccept) {
				acceptanceCounts[iteration]++;
			}
			iteration++;
		}
		return didAccept;
	}
	
	@Override
	public AcceptanceTracker split() {
		return new AcceptanceTracker(schedule.split());
	}
}