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
 
package org.cicirello.search.sa;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>This class implements the Modified Lam annealing schedule, which dynamically
 * adjusts simulated annealing's temperature parameter up and down to either decrease
 * or increase the neighbor acceptance rate as necessary to attempt to match 
 * a theoretically determined ideal.  The Modified Lam annealing schedule is
 * a practical realization of Lam and Delosme's (1988) schedule, 
 * refined first by Swartz (1993) and
 * then further by Boyan (1998). For complete details of the Modified Lam schedule, along
 * with its origins and rationale, see the following references:</p>
 *
 * <ul>
 * <li>Lam, J., and Delosme, J. 1988. Performance of a new annealing schedule. 
 * In Proc. 25th ACM/IEEE DAC, 306–311.</li>
 * <li>Swartz, W. P. 1993. Automatic Layout of Analog and Digital Mixed 
 * Macro/Standard Cell Integrated Circuits. Ph.D. Dissertation, Yale University.</li>
 * <li>Boyan, J. A. 1998. Learning Evaluation Functions for Global Optimization. 
 * Ph.D. Dissertation, Carnegie Mellon University, Pittsburgh, PA.</li>
 * </ul>
 *
 * <p>This Java implementation is significantly faster than the 
 * implementation that would result from a direct implementation as described
 * in the above references of Swartz and Boyan above.  Specifically, as described
 * in those references, the update of the target rate of acceptance involves an
 * exponentiation.  This update occurs once for each iteration of simulated annealing.
 * However, the target rate can actually be computed incrementally from the prior
 * rate.  If the simulated annealing run is n evaluations in length, then the
 * direct implementation of the Boyan/Swartz Modified Lam schedule performs
 * n/2 exponentiations in total across all updates of the target rate; 
 * while our Java implementation will instead perform only 2 exponentiations and n/2
 * multiplications total across all updates of the target rate.</p>
 *
 * <p>For a version of the Modified Lam schedule that is the result of a direct
 * implementation of Swartz's and Boyan's description of the annealing schedule,
 * see the {@link ModifiedLamOriginal} class.</p>
 *
 * <p>The {@link #accept} methods of this class use the classic, and most common,
 * Boltzmann distribution for determining whether to accept a neighbor.</p>
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.24.2020
 */
public final class ModifiedLam implements AnnealingSchedule {
	
	private double t;
	private double acceptRate;
	private double targetRate;
	private double phase1;
	private double phase2;
	private int iterationCount;
	
	private double termPhase1;
	private double multPhase1;
	private double multPhase3;
	
	private int lastMaxEvals;
	
	/**
	 * Default constructor.  The Modified Lam annealing schedule,
	 * unlike other annealing schedules, has no control parameters
	 * other than the run length (the maxEvals parameter of the
	 * {@link #init} method), so no parameters need be passed to
	 * the constructor.
	 */
	public ModifiedLam() {
		lastMaxEvals = -1;
	}
	
	@Override
	public void init(int maxEvals) {
		t = 0.5;
		acceptRate = 0.5;
		targetRate = 1.0;
		iterationCount = 0;
		termPhase1 = 0.56;
		if (lastMaxEvals != maxEvals) {
			// These don't change during the run, and only depend
			// on maxEvals.  So initialize only if run length
			// has changed.
			phase1 = 0.15 * maxEvals;
			phase2 = 0.65 * maxEvals;
			multPhase1 = Math.pow(560, -1.0/phase1);
			multPhase3 = Math.pow(440, -1.0/(maxEvals-phase2));
			lastMaxEvals = maxEvals;
		}
	}
	
	@Override
	public boolean accept(double neighborCost, double currentCost) {
		boolean doAccept = neighborCost <= currentCost ||
			ThreadLocalRandom.current().nextDouble() < Math.exp((currentCost-neighborCost)/t);
		updateSchedule(doAccept);
		return doAccept;
	}
	
	@Override
	public ModifiedLam split() {
		return new ModifiedLam();
	}
	
	private void updateSchedule(boolean doAccept) {
		if (doAccept) acceptRate = 0.998 * acceptRate + 0.002;	
		else acceptRate = 0.998 * acceptRate;
		
		iterationCount++;
		
		if (iterationCount <= phase1) {
			// Original Modified Lam schedule indicates that targetRate should 
			// be set in phase 1 (first 15% of run) 
			// to: 0.44 + 0.56 * Math.pow(560, -1.0*iterationCount/phase1);
			// That involves a pow for each phase 1 iteration.  We instead compute it
			// incrementally with 1 call to pow in the init, and 1 multiplication per
			// phase 1 update.
			termPhase1 *= multPhase1;
			targetRate = 0.44 + termPhase1;
		} else if (iterationCount > phase2) {
			// Original Modified Lam schedule indicates that targetRate should 
			// be set in phase 3 (last 35% of run) 
			// to: 0.44 * Math.pow(440, -(1.0*iterationCount/maxEvals - 0.65)/0.35);
			// That involves a pow for each phase 3 iteration.  We instead compute it
			// incrementally with 1 call to pow in the init, and 1 multiplication per
			// phase 3 update.
			// Also note that at the end of phase 2, targetRate will equal 0.44, where phase 3 begins.
			targetRate *= multPhase3;
		} else {
			// Phase 2 (50% of run beginning after phase 1): constant targetRate at 0.44.
			targetRate = 0.44;
		}
		
		if (acceptRate > targetRate) t *= 0.999;
		else t *= 1.001001001001001; // 1.001001001001001 == 1.0 / 0.999 
	}
	
	/*
	 * package-private for unit testing
	 */
	double getTargetRate() {
		return targetRate;
	}
	
	/*
	 * package-private for unit testing
	 */
	double getAcceptRate() {
		return acceptRate;
	}
	
	/*
	 * package-private for unit testing
	 */
	double getTemperature() {
		return t;
	}
}