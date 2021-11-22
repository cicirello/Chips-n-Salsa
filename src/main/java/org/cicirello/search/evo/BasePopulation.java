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
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import java.util.ArrayList;

/**
 * The Population interface represents a population of candidate solutions
 * to a problem for use by implementations of genetic algorithms and other
 * evolutionary algorithms. It assumes the common generational model, with a 
 * constant population size, and with offspring (from one or both of crossover 
 * and mutation) replacing the parents in the next generation. 
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class BasePopulation<T extends Copyable<T>> implements Population<T> {
	
	private ProgressTracker<T> tracker;
	private SolutionCostPair<T> mostFit;
		
	/**
	 * package-private for use by subclasses in this package only.
	 */
	BasePopulation(ProgressTracker<T> tracker) {
		this.tracker = tracker;
		mostFit = null;
	}
	
	/**
	 * package-private for use by subclasses in this package only.
	 */
	BasePopulation(BasePopulation<T> other) {
		// These must be shared, so just copy reference.
		tracker = other.tracker;
		
		// Must have its own.
		mostFit = null;
	}
	
	@Override
	public void init() {
		mostFit = null;
	}
	
	@Override
	public final SolutionCostPair<T> getMostFit() {
		return mostFit;
	}
	
	@Override
	public boolean evolutionIsPaused() {
		return tracker.didFindBest() || tracker.isStopped();
	}
	
	@Override
	public final ProgressTracker<T> getProgressTracker() {
		return tracker;
	}
	
	@Override
	public final void setProgressTracker(ProgressTracker<T> tracker) {
		this.tracker = tracker;
	}
	
	final void setMostFit(SolutionCostPair<T> mostFit) {
		this.mostFit = mostFit;
		tracker.update(mostFit);
	}
	
	@Override
	abstract public BasePopulation<T> split();
	
	/**
	 * The Population for an evolutionary algorithm where fitness values are type double. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	static final class Double<T extends Copyable<T>> extends BasePopulation<T> implements PopulationFitnessVector.Double {
		
		private final Initializer<T> initializer;
		private final SelectionOperator selection;
	
		private final ArrayList<PopulationMember.DoubleFitness<T>> pop;
		private final ArrayList<PopulationMember.DoubleFitness<T>> nextPop;
		
		private final FitnessFunction.Double<T> f;		
		private final int MU;
		private final int LAMBDA;
		
		private final int[] selected;
		
		private double bestFitness;
		
		/**
		 * Constructs the Population.
		 *
		 * @param n The size of the population, which must be positive.
		 * @param initializer An initializer to supply the population with a means of generating
		 * random initial population members.
		 * @param f The fitness function.
		 * @param selection The selection operator.
		 * @param tracker A ProgressTracker.
		 */
		public Double(int n, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
			super(tracker);
			this.initializer = initializer;
			this.selection = selection;
			
			this.f = f;
			MU = LAMBDA = n;
			pop = new ArrayList<PopulationMember.DoubleFitness<T>>(MU);
			nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(LAMBDA);
			selected = new int[LAMBDA];
			bestFitness = java.lang.Double.NEGATIVE_INFINITY;
		}
		
		/*
		 * private constructor for use by split.
		 */
		private Double(BasePopulation.Double<T> other) {
			super(other);
			
			// these are threadsafe, so just copy references
			f = other.f;
			selection = other.selection;
			MU = other.MU;
			LAMBDA = other.LAMBDA;
			
			// split these: not threadsafe
			initializer = other.initializer.split();
		
			// initialize these fresh: not threadsafe or otherwise needs its own
			pop = new ArrayList<PopulationMember.DoubleFitness<T>>(MU);
			nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(LAMBDA);
			selected = new int[LAMBDA];
			bestFitness = java.lang.Double.NEGATIVE_INFINITY;
		}
		
		@Override
		public BasePopulation.Double<T> split() {
			return new BasePopulation.Double<T>(this);
		}
		
		@Override
		public T get(int i) {
			return nextPop.get(i).getCandidate();
		}
		
		@Override
		public double getFitness(int i) {
			return pop.get(i).getFitness();
		}
		
		@Override
		public int size() {
			return MU;
		}
		
		@Override
		public int mutableSize() {
			return LAMBDA;
		}
		
		/**
		 * Gets fitness of the most fit candidate solution encountered in any generation.
		 * @return the fitness of the most fit encountered in any generation
		 */
		public double getFitnessOfMostFit() {
			return bestFitness;
		}
		
		@Override
		public void updateFitness(int i) {
			double fit = f.fitness(nextPop.get(i).getCandidate());
			nextPop.get(i).setFitness(fit);
			if (fit > bestFitness) {
				bestFitness = fit;
				setMostFit(f.getProblem().getSolutionCostPair(nextPop.get(i).getCandidate().copy())); 
			}
		}
		
		@Override
		public void select() {
			selection.select(this, selected);
			for (int i = 0; i < LAMBDA; i++) {
				nextPop.add(pop.get(selected[i]).copy());
			}
		}
		
		@Override
		public void replace() {
			for (int i = 0; i < LAMBDA; i++) {
				pop.set(i, nextPop.get(i));
			}
			nextPop.clear();
		}
		
		@Override
		public void init() {
			super.init();
			bestFitness = java.lang.Double.NEGATIVE_INFINITY;
			pop.clear();
			nextPop.clear();
			T newBest = null;
			for (int i = 0; i < MU; i++ ) {
				T c = initializer.createCandidateSolution();
				double fit = f.fitness(c);
				pop.add(new PopulationMember.DoubleFitness<T>(c, fit));
				if (fit > bestFitness) {
					bestFitness = fit;
					newBest = c;
				}
			}
			setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));
		}
	}
	
	
	/**
	 * The Population for an evolutionary algorithm where fitness values are type int. 
	 *
	 * @param <T> The type of object under optimization.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	static final class Integer<T extends Copyable<T>> extends BasePopulation<T> implements PopulationFitnessVector.Integer {
		
		private final Initializer<T> initializer;
		private final SelectionOperator selection;
	
		private final ArrayList<PopulationMember.IntegerFitness<T>> pop;
		private final ArrayList<PopulationMember.IntegerFitness<T>> nextPop;
		
		private final FitnessFunction.Integer<T> f;		
		private final int MU;
		private final int LAMBDA;
		
		private final int[] selected;
		
		private int bestFitness;
		
		/**
		 * Constructs the Population.
		 *
		 * @param n The size of the population, which must be positive.
		 * @param initializer An initializer to supply the population with a means of generating
		 * random initial population members.
		 * @param f The fitness function.
		 * @param selection The selection operator.
		 * @param tracker A ProgressTracker.
		 */
		public Integer(int n, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
			super(tracker);
			this.initializer = initializer;
			this.selection = selection;
			
			this.f = f;
			MU = LAMBDA = n;
			pop = new ArrayList<PopulationMember.IntegerFitness<T>>(MU);
			nextPop = new ArrayList<PopulationMember.IntegerFitness<T>>(LAMBDA);
			selected = new int[LAMBDA];
			bestFitness = java.lang.Integer.MIN_VALUE;
		}
		
		/*
		 * private constructor for use by split.
		 */
		private Integer(BasePopulation.Integer<T> other) {
			super(other);
			
			// these are threadsafe, so just copy references
			f = other.f;
			selection = other.selection;
			MU = other.MU;
			LAMBDA = other.LAMBDA;
			
			// split these: not threadsafe
			initializer = other.initializer.split();
		
			// initialize these fresh: not threadsafe or otherwise needs its own
			pop = new ArrayList<PopulationMember.IntegerFitness<T>>(MU);
			nextPop = new ArrayList<PopulationMember.IntegerFitness<T>>(LAMBDA);
			selected = new int[LAMBDA];
			bestFitness = java.lang.Integer.MIN_VALUE;
		}
		
		@Override
		public BasePopulation.Integer<T> split() {
			return new BasePopulation.Integer<T>(this);
		}
		
		@Override
		public T get(int i) {
			return nextPop.get(i).getCandidate();
		}
		
		@Override
		public int getFitness(int i) {
			return pop.get(i).getFitness();
		}
		
		@Override
		public int size() {
			return MU;
		}
		
		@Override
		public int mutableSize() {
			return LAMBDA;
		}
		
		/**
		 * Gets fitness of the most fit candidate solution encountered in any generation.
		 * @return the fitness of the most fit encountered in any generation
		 */
		public int getFitnessOfMostFit() {
			return bestFitness;
		}
		
		@Override
		public void updateFitness(int i) {
			int fit = f.fitness(nextPop.get(i).getCandidate());
			nextPop.get(i).setFitness(fit);
			if (fit > bestFitness) {
				bestFitness = fit;
				setMostFit(f.getProblem().getSolutionCostPair(nextPop.get(i).getCandidate().copy())); 
			}
		}
		
		@Override
		public void select() {
			selection.select(this, selected);
			for (int i = 0; i < LAMBDA; i++) {
				nextPop.add(pop.get(selected[i]).copy());
			}
		}
		
		@Override
		public void replace() {
			for (int i = 0; i < LAMBDA; i++) {
				pop.set(i, nextPop.get(i));
			}
			nextPop.clear();
		}
		
		@Override
		public void init() {
			super.init();
			bestFitness = java.lang.Integer.MIN_VALUE;
			pop.clear();
			nextPop.clear();
			T newBest = null;
			for (int i = 0; i < MU; i++ ) {
				T c = initializer.createCandidateSolution();
				int fit = f.fitness(c);
				pop.add(new PopulationMember.IntegerFitness<T>(c, fit));
				if (fit > bestFitness) {
					bestFitness = fit;
					newBest = c;
				}
			}
			setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));
		}
	}
	
}
