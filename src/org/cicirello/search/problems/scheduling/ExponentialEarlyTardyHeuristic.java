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
 * <p>This class implements a constructive heuristic, known as EXPET, for scheduling problems
 * involving minimizing the sum of weighted earliness plus weighted tardiness.  EXPET
 * is an acronym for Exponential Early Tardy.</p>
 *
 * <p>To define the EXPET heuristic, first let he[j] be the 
 * weighted longest processing time heuristic
 * of job j, defined as he[j] = -we[j] / p[j], where we[j] is the earliness weight
 * for job j, and p[j] is the processing time of job j.  
 * Next, let ht[j] be the weighted shortest
 * processing time heuristic of job j, defined as ht[j] = wt[j] / p[j], where wt[j] is the
 * tardiness weight of job j.  Define the slack s[j] of job j as:
 * s[j] = d[j] - T - p[j], where d[j] is the job's due date and T is the
 * current time.  Let k &ge; 1 be a lookahead parameter that can be tuned based on 
 * problem instance characteristics, and p&#772; is the average processing
 * time of remaining unscheduled jobs.</p>
 *
 * <p>Now we can define the EXPET heuristic, h[j] for job j, as follows.  
 * Case 1: If s[j] &le; 0, 
 * h[j] = ht[j]. Case 2: if s[j] &ge; k*p&#772;, h[j] = he[j].  
 * Case 3: If 0 &lt; s[j] &le; k*p&#772;*ht[j]/(ht[j]-he[j]), then
 * h[j] = ht[j] * exp((s[j](ht[j]-he[j]))/(he[j]*k*p&#772;)).
 * Case 4: If k*p&#772;*ht[j]/(ht[j]-he[j]) &lt; s[j] &lt; k*p&#772;, then
 * h[j] = he[j]<sup>-2</sup> * (ht[j] - s[j](ht[j]-he[j])/(k*p&#772;))<sup>3</sup>.
 * For jobs with negative slack, the
 * EXPET heuristic is equivalent to weighted shortest processing time.  For jobs with slack
 * greater than some multiple k of the average processing time, EXPET is equivalent to 
 * weighted longest processing time.</p>
 *
 * <p>We make one additional adjustment to the heuristic as it was originally described.
 * Since this library's implementations of stochastic 
 * sampling algorithms assumes that constructive
 * heuristics always produce positive values, we must 
 * adjust the values produced by the EXPET heuristic.
 * Specifically, we actually compute h'[j] = h[j] + shift, where
 * shift = {@link #MIN_H} - min(we[j] / p[j]).
 * The {@link #MIN_H}
 * is a small non-zero value.  In this way, we shift all of the h[j] values
 * by a constant amount such that all h[j] values are positive.</p>
 *
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
	 * Must be at least 1.
	 * @throws IllegalArgumentException if k &lt; 1.
	 */
	public ExponentialEarlyTardyHeuristic(SingleMachineSchedulingProblem problem, double k) {
		super(problem);
		if (k < 1) throw new IllegalArgumentException("k must be at least 1");
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