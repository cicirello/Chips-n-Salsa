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

import org.cicirello.util.Copyable;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.math.rand.RandomVariates;

/**
 * An AlwaysMutateGeneration is the common cycle of: select, apply crossover to pairs of parents based on C,
 * apply mutation to each population member with rate M, replace. However, it is the special case when M=1.0,
 * such that every member of the population is mutated once in each generation.
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class AlwaysMutateGeneration<T extends Copyable<T>> implements Generation<T> {
	
	private final MutationOperator<T> mutation;
	private final CrossoverOperator<T> crossover;
	private final double C;
	
	AlwaysMutateGeneration(MutationOperator<T> mutation, CrossoverOperator<T> crossover, double crossoverRate) {
		if (mutation == null) {
			throw new NullPointerException("mutation must be non-null");
		}
		if (crossover == null) {
			throw new NullPointerException("crossover must be non-null");
		}
		if (crossoverRate < 0.0) {
			throw new IllegalArgumentException("crossoverRate must not be negative");
		}
		C = crossoverRate < 1.0 ? crossoverRate : 1.0;
		this.mutation = mutation;
		this.crossover = crossover;
	}
	
	AlwaysMutateGeneration(AlwaysMutateGeneration<T> other) {
		// Must be split
		mutation = other.mutation.split();
		crossover = other.crossover.split(); 
		
		// primitives
		C = other.C;
	}
	
	@Override
	public AlwaysMutateGeneration<T> split() {
		return new AlwaysMutateGeneration<T>(this);
	}
	
	@Override
	public int apply(Population<T> pop) {
		pop.select();
		// Since select() above randomizes ordering, just use a binomial
		// to get count of number of pairs of parents to cross and cross the first 
		// count pairs of parents. Pair up parents with indexes: first and (first + count).
		final int LAMBDA = pop.mutableSize();
		final int count = RandomVariates.nextBinomial(LAMBDA >> 1, C);
		for (int first = 0; first < count; first++) {
			int second = first + count;
			crossover.cross(pop.get(first), pop.get(second));
			pop.updateFitness(first);
			pop.updateFitness(second);
		}
		// Mutate all of them
		for (int j = 0; j < LAMBDA; j++) {
			mutation.mutate(pop.get(j));
			pop.updateFitness(j);
		}
		pop.replace();
		return (count << 1) + LAMBDA;
	}
}
