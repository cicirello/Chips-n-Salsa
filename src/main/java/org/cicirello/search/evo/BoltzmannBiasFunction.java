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

import org.cicirello.search.concurrent.Splittable;

/**
 * Internal package access abstract base class for Boltzmann selection cooling schedules.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class BoltzmannBiasFunction implements FitnessBiasFunction, Splittable<BoltzmannBiasFunction> {
	
	private double t;
	
	/**
	 * Constructor for base class.
	 *
	 * @param t initial temperatire
	 */
	public BoltzmannBiasFunction(double t) {
		this.t = t;
	}
	
	@Override
	final public double bias(double fitness) {
		return Math.exp(fitness / t);
	}
		
	@Override
	abstract public BoltzmannBiasFunction split();
	
	/**
	 * Initializes the cooling schedule.
	 */
	final public void init() {
		t = getT0();
	}
	
	/**
	 * Updates the cooling schedule.
	 */
	final public void update() {
		t = nextT(t);
	}
	
	/**
	 * Gets the initial temperature for the schedule.
	 */
	abstract public double getT0();
	
	/**
	 * Gets the next temperature for the schedule.
	 *
	 * @param t The current temperature.
	 */
	abstract public double nextT(double t);
}
