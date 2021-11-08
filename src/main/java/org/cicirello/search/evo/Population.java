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

package org.cicirello.search.evo;

import org.cicirello.util.Copyable;
import org.cicirello.search.concurrent.Splittable;
import org.cicirello.search.operators.Initializer;

/**
 * The Population class represents a population of candidate solutions
 * to a problem for use by implementations of genetic algorithms and other
 * evolutionary algorithms. It assumes the common generational model, with a 
 * constant population size, and with offspring (from one or both of crossover 
 * and mutation) replacing the parents in the next generation. It also assumes
 * fitness values that are of type double.
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class Population<T extends Copyable<T>> implements Splittable<Population<T>> {
	
	private final Initializer<T> initializer;
	private final PopulationMember.DoubleFitness<T>[] pop;
	private final FitnessFunction<T> f;
	
	/**
	 * Constructs the Population.
	 *
	 * @param n The size of the population, which must be positive.
	 * @param initializer An initializer to supply the population with a means of generating
	 * random initial population members.
	 * @param f The fitness function.
	 *
	 * @throws IllegalArgumentException if n is not positive.
	 */
	public Population(int n, Initializer<T> initializer, FitnessFunction<T> f) {
		if (n < 1) throw new IllegalArgumentException("population size must be positive");
		this.initializer = initializer;
		this.f = f;
		pop = initPop(n);
	}
	
	/*
	 * private constructor for use by split.
	 */
	private Population(Population<T> other) {
		// these are threadsafe, so just copy references
		f = other.f;
		
		// split these: not threadsafe
		initializer = other.initializer.split();
		
		// initialize these fresh: not threadsafe
		pop = initPop(other.pop.length);
	}
	
	@Override
	public Population<T> split() {
		return new Population<T>(this);
	}
	
	/**
	 * Gets a member of the population subject to genetic operators
	 * during the current generation.
	 *
	 * @param i An index into the population (indexes begin at 0).
	 * @return The member of the population at index i.
	 * @throws ArrayIndexOutOfBoundsException if i is outside the interval [0, size()).
	 */
	public PopulationMember.DoubleFitness<T> get(int i) {
		return pop[i];
	}
	
	/**
	 * Gets the size of the population that is subject to application of
	 * genetic operators during the current generation.
	 *
	 * @return The size of the population.
	 */
	public int size() {
		return pop.length;
	}
	
	private PopulationMember.DoubleFitness<T>[] initPop(int n) {
		@SuppressWarnings("unchecked")
		PopulationMember.DoubleFitness<T>[] pop = (PopulationMember.DoubleFitness<T>[])new PopulationMember.DoubleFitness<?>[n];
		for (int i = 0; i < n; i++ ) {
			T c = initializer.createCandidateSolution();
			pop[i] = new PopulationMember.DoubleFitness<T>(c, f.fitness(c));
		}
		return pop;
	}		
	
}
