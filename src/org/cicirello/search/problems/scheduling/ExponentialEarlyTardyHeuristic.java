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
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.IncrementalEvaluation;

/**
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.31.2020
 */
public final class ExponentialEarlyTardyHeuristic extends SchedulingHeuristic {
	
	private final double[] wlpt;
	private final double[] wspt;
	private final double k;
	private final double shift;
	private final int totalProcessTime;
	
	/**
	 * Constructs a ExponentialEarlyTardyHeuristic heuristic.  Uses a default value of k=1.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public ExponentialEarlyTardyHeuristic(SingleMachineSchedulingProblem problem) {
		this(problem, 1.0);
	}
	
	/**
	 * Constructs a ExponentialEarlyTardyHeuristic heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @param k A parameter of the heuristic (see class documentation).
	 * Must be positive.
	 * @throws IllegalArgumentException if k &le; 0.
	 */
	public ExponentialEarlyTardyHeuristic(SingleMachineSchedulingProblem problem, double k) {
		super(problem);
		if (k <= 0) throw new IllegalArgumentException("k must be positive");
		// pre-compute WLPT and WSPT, and cache results.
		wlpt = new double[data.numberOfJobs()];
		wspt = new double[data.numberOfJobs()];
		double minimum = 0;
		for (int i = 0; i < wlpt.length; i++) {
			wlpt[i] = -data.getEarlyWeight(i) / (double)data.getProcessingTime(i);
			if (wlpt[i] < minimum) minimum = wlpt[i];
			wspt[i] = data.getWeight(i) / (double)data.getProcessingTime(i);
		}
		// shift heuristic values by minimum to ensure all positive values.
		shift = MIN_H - minimum;
		this.k = k;
		totalProcessTime = sumOfProcessingTimes();
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		double slack = ((IncrementalAverageProcessingCalculator)incEval).slack(element, p);
		if (slack <= 0) {
			return wspt[element] + shift;
		}
		double kpBar = k * ((IncrementalAverageProcessingCalculator)incEval).averageProcessingTime();
		if (slack >= kpBar) {
			return wlpt[element] + shift;
		}
		double bound1 = kpBar * wspt[element] / (wspt[element] - wlpt[element]);
		if (slack <= bound1) {
			return wspt[element] * 
				Math.exp(slack * (wspt[element] - wlpt[element]) / (wlpt[element]*kpBar))
				+ shift;
		} else {
			double numTerm = wspt[element] - slack * (wspt[element] - wlpt[element]) / kpBar;
			return numTerm * numTerm * numTerm / (wlpt[element] * wlpt[element]) + shift;
		}
	}
	
	@Override
	public IncrementalEvaluation<Permutation> createIncrementalEvaluation() {
		return new IncrementalAverageProcessingCalculator(totalProcessTime);
	}
}