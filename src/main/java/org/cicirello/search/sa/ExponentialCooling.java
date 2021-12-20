/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 * <p>This class implements the classic and most commonly encountered
 * cooling schedule for simulated annealing, the annealing schedule known
 * as exponential cooling (sometimes referred to as geometric cooling).
 * In this cooling schedule, the k-th temperature, t<sub>k</sub>, is
 * determined as follows: t<sub>k</sub> = &alpha;<sup>k</sup> * t<sub>0</sub>,
 * where t<sub>0</sub> is the initial temperature and &alpha; is the cooling rate.  
 * The new temperature is
 * usually computed incrementally from the previous with: 
 * t<sub>k</sub> = &alpha; * t<sub>k-1</sub>.  In some applications, the temperature
 * update occurs with each simulated annealing evaluation, while in others it is updated
 * periodically, such as every s steps (i.e., iterations) of simulated annealing.
 * This class supports this periodic update approach, with a default of every step.
 * See the parameters of the constructors for more information.</p>  
 *
 * <p>Additionally,
 * this class stops updating the temperature once it is less than or equal to 0.001.
 * For any foreseeable cost scale that a problem may have, a temperature value of 0.001
 * is sufficiently low such that all moves that are worse than the current state will
 * be rejected, so further cooling would be superfluous.</p>
 *
 * <p>The {@link #accept accept} methods of this class use the classic, and most common,
 * Boltzmann distribution for determining whether to accept a neighbor.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ExponentialCooling implements AnnealingSchedule {
	
	private double t;
	private final double t0;
	private final double alpha;
	private final int steps;
	private int stepCounter;
	
	/**
	 * Constructs an exponential cooling schedule for simulated annealing.
	 * @param t0 The initial temperature for the start of an annealing run.
	 * The value of t0 must be positive.
	 * @param alpha The cooling rate.  Each time
	 * the temperature is cooled, it is cooled as follows: t = t * alpha.
	 * The value of alpha must be greater than 0 and less than 1.
	 * @param steps The number of iterations of simulated annealing between
	 * cooling events.  Steps must be positive.  If 0 or a negative is passed
	 * for steps, steps is set to 1.
	 * @throws IllegalArgumentException if t0 &le; 0 or alpha &le; 0 or alpha &ge; 1.
	 */
	public ExponentialCooling(double t0, double alpha, int steps) {
		if (t0 <= 0) throw new IllegalArgumentException("Initial temperature must be positive");
		if (alpha <= 0 || alpha >= 1) throw new IllegalArgumentException("alpha must be in interval (0,1)");
		t = this.t0 = t0;
		this.alpha = alpha;
		this.steps = steps <= 0 ? 1 : steps;
	}
	
	/**
	 * Constructs an exponential cooling schedule for simulated annealing.
	 * @param t0 The initial temperature for the start of an annealing run.
	 * The value of t0 must be positive.
	 * @param alpha The cooling rate.  During each iteration of simulated annealing,
	 * the temperature is cooled as follows: t = t * alpha.
	 * The value of alpha must be greater than 0 and less than 1.
	 * @throws IllegalArgumentException if t0 &le; 0 or alpha &le; 0 or alpha &ge; 1.
	 */
	public ExponentialCooling(double t0, double alpha) {
		if (t0 <= 0) throw new IllegalArgumentException("Initial temperature must be positive");
		if (alpha <= 0 || alpha >= 1) throw new IllegalArgumentException("alpha must be in interval (0,1)");
		t = this.t0 = t0;
		this.alpha = alpha;
		this.steps = 1;
	}
	
	/*
	 * private copy constructor for internal use only
	 */
	private ExponentialCooling(ExponentialCooling other) {
		t = t0 = other.t0;
		alpha = other.alpha;
		steps = other.steps;
	}
	
	@Override
	public void init(int maxEvals) {
		t = t0;
		stepCounter = 0;
	}
	
	@Override
	public boolean accept(double neighborCost, double currentCost) {
		boolean doAccept = neighborCost <= currentCost ||
			ThreadLocalRandom.current().nextDouble() < Math.exp((currentCost-neighborCost)/t);
		stepCounter++;
		if (stepCounter == steps && t > 0.001) {
			stepCounter = 0; 
			t *= alpha;
		}
		return doAccept;
	}
	
	@Override
	public ExponentialCooling split() {
		return new ExponentialCooling(this);
	}
	
	/*
	 * package-private for unit testing
	 */
	double getTemperature() {
		return t;
	}
}