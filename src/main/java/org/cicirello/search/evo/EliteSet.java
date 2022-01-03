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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.cicirello.util.Copyable;

/**
 * Abstract package-private class for use by classes within the evo package for maintaining a set of
 * elite population members for evolutionary algorithms with elitism. This class supports
 * double-valued fitness.
 */
abstract class EliteSet<T extends Copyable<T>> {
	
	/**
	 * Package-private class for use by classes within the evo package for maintaining a set of
	 * elite population members for evolutionary algorithms with elitism. This class supports
	 * double-valued fitness.
	 */
	static final class DoubleFitness<T extends Copyable<T>> extends EliteSet<T> implements Iterable<PopulationMember.DoubleFitness<T>> {
		
		private final PopulationMember.DoubleFitness<T>[] elite;
		private int size;
		private final HashSet<T> isElite;
		
		/*
		 * package-private for use by classes in evo package for maintaining a set of elite population members.
		 */
		@SuppressWarnings("unchecked")
		DoubleFitness(ArrayList<PopulationMember.DoubleFitness<T>> initialPop, int numElite) {
			isElite = new HashSet<T>();
			elite = (PopulationMember.DoubleFitness<T>[]) new PopulationMember.DoubleFitness[numElite];
			size = 0;
			offerAll(initialPop);
		}
		
		/*
		 * package-private for use by classes in evo package for adding to a set of elite population members.
		 */
		void offerAll(ArrayList<PopulationMember.DoubleFitness<T>> pop) {
			for (PopulationMember.DoubleFitness<T> popMember : pop) {
				offer(popMember);
			}
		}
		
		/*
		 * package-private for use by classes in evo package for adding to a set of elite population members.
		 */
		void offer(PopulationMember.DoubleFitness<T> popMember) {
			if (size < elite.length) {
				if (!isElite.contains(popMember.candidate)) {
					elite[size] = popMember;
					percolateUp(size);
					size++;
					isElite.add(popMember.candidate);
				}
			} else if (popMember.getFitness() > elite[0].getFitness()) {
				if (!isElite.contains(popMember.candidate)) {
					isElite.remove(elite[0].candidate);
					isElite.add(popMember.candidate);
					elite[0] = popMember;
					percolateDown(0);
				}
			}
		}
		
		@Override
		public Iterator<PopulationMember.DoubleFitness<T>> iterator() {
			return new EliteIterator();
		}
		
		/*
		 * private helper for min-heap used for elite set
		 */
		private void percolateDown(int index) {
			int child = (index << 1) + 1;
			if (child < size) {
				int minIndex = elite[child].getFitness() < elite[index].getFitness() ? child : index;
				child++;
				if (child < size && elite[child].getFitness() < elite[minIndex].getFitness()) {
					minIndex = child;
				}
				if (index != minIndex) {
					PopulationMember.DoubleFitness<T> temp = elite[index];
					elite[index] = elite[minIndex];
					elite[minIndex] = temp;
					percolateDown(minIndex);
				}
			}
		}
		
		/*
		 * private helper for min-heap used for elite set
		 */
		private void percolateUp(int index) {
			while (index > 0) {
				int parent = (index - 1) >> 1;
				if (elite[index].getFitness() < elite[parent].getFitness()) {
					PopulationMember.DoubleFitness<T> temp = elite[index];
					elite[index] = elite[parent];
					elite[parent] = temp;
					index = parent;
				} else {
					break;
				}
			}
		}
		
		/*
		 * internal iterator class
		 */
		private final class EliteIterator implements Iterator<PopulationMember.DoubleFitness<T>> {
			
			private int nextIndex;
			
			private EliteIterator() {
				nextIndex = 0;
			}
			
			@Override 
			public boolean hasNext() {
				return nextIndex < size;
			}
			
			@Override
			public PopulationMember.DoubleFitness<T> next() {
				if (hasNext()) {
					PopulationMember.DoubleFitness<T> result = elite[nextIndex];
					nextIndex++;
					return result;
				}
				throw new NoSuchElementException("No more elements in this iterator.");
			}
		}
	}
	
	/**
	 * Package-private class for use by classes within the evo package for maintaining a set of
	 * elite population members for evolutionary algorithms with elitism. This class supports
	 * int-valued fitness.
	 */
	static final class IntegerFitness<T extends Copyable<T>> extends EliteSet<T> implements Iterable<PopulationMember.IntegerFitness<T>> {
		
		private final PopulationMember.IntegerFitness<T>[] elite;
		private int size;
		private final HashSet<T> isElite;
		
		/*
		 * package-private for use by classes in evo package for maintaining a set of elite population members.
		 */
		@SuppressWarnings("unchecked")
		IntegerFitness(ArrayList<PopulationMember.IntegerFitness<T>> initialPop, int numElite) {
			isElite = new HashSet<T>();
			elite = (PopulationMember.IntegerFitness<T>[]) new PopulationMember.IntegerFitness[numElite];
			size = 0;
			offerAll(initialPop);
		}
		
		/*
		 * package-private for use by classes in evo package for adding to a set of elite population members.
		 */
		void offerAll(ArrayList<PopulationMember.IntegerFitness<T>> pop) {
			for (PopulationMember.IntegerFitness<T> popMember : pop) {
				offer(popMember);
			}
		}
		
		/*
		 * package-private for use by classes in evo package for adding to a set of elite population members.
		 */
		void offer(PopulationMember.IntegerFitness<T> popMember) {
			if (size < elite.length) {
				if (!isElite.contains(popMember.candidate)) {
					elite[size] = popMember;
					percolateUp(size);
					size++;
					isElite.add(popMember.candidate);
				}
			} else if (popMember.getFitness() > elite[0].getFitness()) {
				if (!isElite.contains(popMember.candidate)) {
					isElite.remove(elite[0].candidate);
					isElite.add(popMember.candidate);
					elite[0] = popMember;
					percolateDown(0);
				}
			}
		}
		
		@Override
		public Iterator<PopulationMember.IntegerFitness<T>> iterator() {
			return new EliteIterator();
		}
		
		/*
		 * private helper for min-heap used for elite set
		 */
		private void percolateDown(int index) {
			int child = (index << 1) + 1;
			if (child < size) {
				int minIndex = elite[child].getFitness() < elite[index].getFitness() ? child : index;
				child++;
				if (child < size && elite[child].getFitness() < elite[minIndex].getFitness()) {
					minIndex = child;
				}
				if (index != minIndex) {
					PopulationMember.IntegerFitness<T> temp = elite[index];
					elite[index] = elite[minIndex];
					elite[minIndex] = temp;
					percolateDown(minIndex);
				}
			}
		}
		
		/*
		 * private helper for min-heap used for elite set
		 */
		private void percolateUp(int index) {
			while (index > 0) {
				int parent = (index - 1) >> 1;
				if (elite[index].getFitness() < elite[parent].getFitness()) {
					PopulationMember.IntegerFitness<T> temp = elite[index];
					elite[index] = elite[parent];
					elite[parent] = temp;
					index = parent;
				} else {
					break;
				}
			}
		}
		
		/*
		 * internal iterator class
		 */
		private final class EliteIterator implements Iterator<PopulationMember.IntegerFitness<T>> {
			
			private int nextIndex;
			
			private EliteIterator() {
				nextIndex = 0;
			}
			
			@Override 
			public boolean hasNext() {
				return nextIndex < size;
			}
			
			@Override
			public PopulationMember.IntegerFitness<T> next() {
				if (hasNext()) {
					PopulationMember.IntegerFitness<T> result = elite[nextIndex];
					nextIndex++;
					return result;
				}
				throw new NoSuchElementException("No more elements in this iterator.");
			}
		}
	}
}
