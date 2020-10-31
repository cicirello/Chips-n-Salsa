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
 * <p>This class implements logarithmic cooling, a classic annealing
 * schedule.  This annealing schedule is of theoretical interest, but in
 * general is not practical.  The classic convergence results 
 * for simulated annealing depend upon logarithmic cooling.  In particular,
 * with logarithmic cooling, if the temperature begins sufficiently high,
 * then in the limit simulated annealing converges to the globally optimal
 * solution.  However, the temperature cools so slowly with logarithmic cooling
 * that for any practical search length, the search remains essentially a 
 * random walk, and does not come anywhere close to the limit behavior.
 * We have included the logarithmic cooling schedule in the library
 * in the interests of being complete.</p>
 *
 * <p>In logarithmic cooling, the k-th temperature is defined as:
 * t<sub>k</sub> = c / ln(k + d), where c and d are constants.  The value of
 * c should be set based on cost differences between random neighbors, and
 * d is usually set equal to 1.  In our case, since we start k at 0, a value of d=1
 * would lead to a division by 0.  Additionally, to simplify initialization for
 * the programmer using this annealing schedule, our implementation sets d=e
 * (i.e., to the base of the natural logarithm).  Logarithms are so slow growing that
 * small differences in the value of d don't change the behavior of the schedule in
 * any significant way.  When k=0, the denominator is
 * thus ln(e)=1.  Thus, c can be set to the desired initial temperature.
 * Therefore, our implementation redefines the cooling schedule as: 
 * t<sub>k</sub> = t<sub>0</sub> / ln(k + e), where e is the base of the natural
 * logarithm.</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.2.2019
 */
public final class LogarithmicCooling implements AnnealingSchedule {
	
	private double t;
	private final double c;
	private int stepCounter;
	
	/**
	 * Constructs a logarithmic cooling schedule with a specified initial temperature.
	 * @param t0 The initial temperature, which must be positive
	 * @throws IllegalArgumentException if t0 &le; 0.0
	 */
	public LogarithmicCooling(double t0) {
		if (t0 <= 0) throw new IllegalArgumentException("initial temperature must be positive");
		t = this.c = t0;
	}
	
	/*
	 * private copy constructor for internal use only
	 */
	private LogarithmicCooling(LogarithmicCooling other) {
		t = c = other.c;
	}
	
	/**
	 * {@inheritDoc}
	 * @param maxEvals This cooling schedule doesn't depend upon run length, so
	 * this parameter is ignored.
	 */
	@Override
	public void init(int maxEvals) {
		stepCounter = 0;
		t = c;
	}
	
	@Override
	public boolean accept(double neighborCost, double currentCost) {
		boolean doAccept = neighborCost <= currentCost ||
			ThreadLocalRandom.current().nextDouble() < Math.exp((currentCost-neighborCost)/t);
		stepCounter++;
		t = c / StrictMath.log(StrictMath.E + stepCounter);
		return doAccept;
	}
	
	@Override
	public LogarithmicCooling split() {
		return new LogarithmicCooling(this);
	}
	
	/*
	 * package-private for unit testing
	 */
	double getTemperature() {
		return t;
	}
}