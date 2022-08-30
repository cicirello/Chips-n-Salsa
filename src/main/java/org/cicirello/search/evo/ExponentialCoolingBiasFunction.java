/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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
 
package org.cicirello.search.evo;

/**
 * Internal package access class for exponential cooling Boltzmann schedule.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class ExponentialCoolingBiasFunction extends BoltzmannBiasFunction {
		
	private double t;
	private final double t0;
	private final double r;
	private final double tMin;
	
	/**
	 * Constructs the exponential cooling schedule.
	 *
	 * @param t0 The initial temperature
	 * @param r The update value
	 * @param tMin The minimum temperature
	 */
	public ExponentialCoolingBiasFunction(double t0, double r, double tMin) {
		t = this.t0 = t0;
		this.r = r;
		this.tMin = tMin;
	}
	
	@Override
	public void init() {
		t = t0;
	}
	
	@Override
	public void update() {
		t *= r;
		if (t < tMin) {
			t = tMin;
		}
	}
	
	@Override
	public double bias(double fitness) {
		return Math.exp(fitness / t);
	}
	
	@Override
	public ExponentialCoolingBiasFunction split() {
		return new ExponentialCoolingBiasFunction(t0, r, tMin);
	}
}
