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
import org.cicirello.util.Copyable;

abstract class EliteSet<T extends Copyable<T>> {
	
	final HashSet<T> isElite;
	
	EliteSet() {
		isElite = new HashSet<T>();
	}
	
	void offer(PopulationMember.DoubleFitness<T> popMember) {
		throw new UnsupportedOperationException("This EliteSet is not configured for double valued fitness.");
	}
	
	void offer(PopulationMember.IntegerFitness<T> popMember) {
		throw new UnsupportedOperationException("This EliteSet is not configured for int valued fitness.");
	}
	
	static <U extends Copyable<U>> EliteSet<U> createEliteSet(ArrayList<PopulationMember.DoubleFitness<U>> initialPop, int numElite) {
		return new DoubleFitness<U>(initialPop, numElite);
	}
	
	private static final class DoubleFitness<U extends Copyable<U>> extends EliteSet<U> {
		
		private final PopulationMember.DoubleFitness<U>[] elite;
		private int size;
		
		@SuppressWarnings("unchecked")
		private DoubleFitness(ArrayList<PopulationMember.DoubleFitness<U>> initialPop, int numElite) {
			super();
			elite = (PopulationMember.DoubleFitness<U>[]) new PopulationMember.DoubleFitness[numElite];
			for (PopulationMember.DoubleFitness<U> popMember : initialPop) {
				offer(popMember);
			}
		}
		
		@Override
		void offer(PopulationMember.DoubleFitness<U> popMember) {
			if (size < elite.length) {
				if (!isElite.contains(popMember.candidate)) {
					elite[size] = popMember;
					size++;
					isElite.add(popMember.candidate);
					percolateUp(size);
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
		
		private void percolateDown(int index) {
			int child = (index << 1) + 1;
			if (child < size) {
				int minIndex = elite[child].getFitness() < elite[index].getFitness() ? child : index;
				child++;
				if (child < size && elite[child].getFitness() < elite[minIndex].getFitness()) {
					minIndex = child;
				}
				if (index != minIndex) {
					PopulationMember.DoubleFitness<U> temp = elite[index];
					elite[index] = elite[minIndex];
					elite[minIndex] = temp;
					percolateDown(minIndex);
				}
			}
		}
		
		private void percolateUp(int index) {
			while (index > 0) {
				int parent = (index - 1) >> 1;
				if (elite[index].getFitness() < elite[parent].getFitness()) {
					PopulationMember.DoubleFitness<U> temp = elite[index];
					elite[index] = elite[parent];
					elite[parent] = temp;
					index = parent;
				} else {
					break;
				}
			}
		}
	}
}
