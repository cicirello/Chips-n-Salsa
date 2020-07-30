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
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.PartialPermutation;

/**
 * <p>DynamicATCS is an implementation of a variation of the ATCS 
 * (Apparent Tardiness Cost with Setups) heuristic, which dynamically 
 * updates the average processing and setup times as it constructs the schedule.
 * For an implementation of the original version of ATCS, without the dynamic
 * parameter updates, see the {@link ATCS} class. 
 * ATCS is defined as:
 * h(j) = (w[j]/p[j]) exp( -max(0,d[j] - T - p[j]) / (k<sub>1</sub> p&#772;) ) 
 * exp( -s[i][j] / (k<sub>2</sub> s&#772;)),
 * where w[j] is the weight of job j, p[j] is its processing time,
 * d[j] is the job's due date, T is the
 * current time, and s[i][j] is any setup time of the job if it follows job i.  
 * The k<sub>1</sub> and k<sub>2</sub> are parameters that can be tuned based on 
 * problem instance characteristics, p&#772; is the average processing
 * time of remaining unscheduled jobs, and s&#772; is the average setup time
 * of the remaining unscheduled jobs.  The authors of the ATCS heuristic
 * simply computed p&#772; and s&#772; once at the start, while our implementation
 * updates these dynamically along the way as jobs are scheduled.</p>  
 *
 * <p>The constant {@link #MIN_H} defines the minimum value
 * the heuristic will return, preventing h(j)=0 in support of stochastic
 * sampling algorithms for which h(j)=0 is problematic.  This implementation 
 * returns max( {@link #MIN_H}, h(j)), where {@link #MIN_H}
 * is a small non-zero value.</p>  
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.30.2020
 */
public final class DynamicATCS extends WeightedShortestProcessingTime {
	
	private final double k1;
	private final double k2;
	private final int pSum;
	private final int sSum;
	
	/**
	 * Constructs an DynamicATCS heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @param k1 A parameter to the heuristic, which must be positive.
	 * @param k2 A parameter to the heuristic, which must be positive.	 
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 * @throws IllegalArgumentException if k &le; 0.0.
	 */
	public DynamicATCS(SingleMachineSchedulingProblem problem, double k1, double k2) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
		if (k1 <= 0.0 || k2 <= 0.0) {
			throw new IllegalArgumentException("k1 and k2 must be positive");
		}
		pSum = sumOfProcessingTimes();
		sSum = sumOfSetupTimes();
		this.k1 = k1;
		this.k2 = k2;
	}
	
	/**
	 * Constructs an DynamicATCS heuristic.  Sets the values of k1 and k2 
	 * according to the procedure described by the authors of the ATCS
	 * heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public DynamicATCS(SingleMachineSchedulingProblem problem) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
		
		int n = data.numberOfJobs();
		pSum = sumOfProcessingTimes();
		sSum = sumOfSetupTimes();
		double cv = 0;
		double eta = 0;
		double cmax = pSum;
		if (sSum > 0) {
			double meanS = ((double)sSum)/(n*n);
			eta = n * meanS / pSum;
			
			cv = setupVariance(meanS) / (meanS * meanS);
			if (cv < 1E-10) cv = 0;
			else if (cv > 0.3333333333) cv = 0.3333333333;
			final double BETA_MIN = n < 153 ? -0.097 * Math.log(n) + 0.6876 : 0.2;
			final double BETA = cv == 0 ? 1.0 : 1.0 - (1.0 - BETA_MIN) * cv / 0.3333333333;
			cmax += n * meanS * BETA;
		}
		double[] d_stats = computeDueDateStats();
		double R = (d_stats[1] - d_stats[0]) / cmax;
		double tau = 1.0 - d_stats[2] / cmax;
		if (R <= 0.5) k1 = 4.5 + R;
		else if (R <= 2.5) {
			// R should never be > 1 for any realistic instance
			// wider range in if condition is to handle degenerate scheduling
			// problem instance ensuring k1 >= 1.
			k1 = 6 - R - R;
		} else {
			k1 = 1;
		}
		double temp = eta > 0 && tau > 0 ? 0.5*tau/Math.sqrt(eta) : 1;
		k2 = temp >= 1 ? temp : 1;
	}
	
	@Override
	public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
		double value = super.h(p, element, incEval);
		if (value > MIN_H) {
			double num = data.getDueDate(element) - data.getProcessingTime(element) - ((IncrementalStatsCalculator)incEval).currentTime();
			if (num > 0) {
				double denom = k1 * ((IncrementalStatsCalculator)incEval).averageProcessingTime();
				value *= Math.exp(-num / denom);
				if (value <= MIN_H) return MIN_H;
			}
			if (HAS_SETUPS && sSum > 0) {
				num = p.size() == 0 ? 
						data.getSetupTime(element) 
						: data.getSetupTime(p.getLast(), element); 
				if (num > 0) {
					double denom = k2 * ((IncrementalStatsCalculator)incEval).averageSetupTime();
					value *= Math.exp(-num / denom);
					if (value <= MIN_H) return MIN_H;
				}
			}
		}		
		return value;
	}
	
	@Override
	public IncrementalEvaluation createIncrementalEvaluation() {
		return new IncrementalStatsCalculator(pSum, sSum);
	}
	
	private double[] computeDueDateStats() {
		int min = data.getDueDate(0);
		int max = min; 
		int sum = min;
		int n = data.numberOfJobs();
		for (int i = 1; i < n; i++) {
			int d = data.getDueDate(i);
			if (d < min) min = d;
			else if (d > max) max = d;
			sum += d;
		}
		return new double[] { min, max, ((double)sum)/n };
	}
	
	private double setupVariance(double mean) {
		int n = data.numberOfJobs();
		double total = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int s = data.getSetupTime(i,j);
				total += (s*s);
			}
		}
		return total / (n*n) - mean*mean;
	}
	
	
	/*
	 * package-private rather than private to enable test case access
	 */
	class IncrementalStatsCalculator extends IncrementalAverageProcessingCalculator {
		
		// total setup time of remaining jobs
		private int totalS;
		
		public IncrementalStatsCalculator(int pSum, int sSum) {
			super(pSum);
			totalS = sSum;
		}
		
		@Override
		public void extend(PartialPermutation p, int element) {
			super.extend(p, element);
			int x = p.numExtensions();	
			for (int i = 0; i < x; i++) {
				int j = p.getExtension(i);
				if (p.size()==0) {
					totalS -= data.getSetupTime(j);
				} else {
					totalS -= data.getSetupTime(p.getLast(), j);
				}
				if (j!=element) totalS -= data.getSetupTime(j, element);
			}
		}
		
		/**
		 * Gets the total setup time of unscheduled jobs.
		 * @return total setup time of unscheduled jobs
		 */
		public int totalSetupTime() { return totalS; }
		
		/**
		 * Gets the average setup time of unscheduled jobs.
		 * @return average setup time of unscheduled jobs
		 */
		public double averageSetupTime() {
			if (n==0) return 0;
			return ((double)totalS) / (n*n);
		}
	}
	
}