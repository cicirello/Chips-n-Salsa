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
 
package org.cicirello.search.operators.permutations;

import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * Code shared by multiple test classes.
 */
class SharedTestCodeOrderingCrossovers {
	
	final static int NUM_SAMPLES = 5;
	
	boolean validPermutation(Permutation p) {
		boolean[] foundIt = new boolean[p.length()];
		for (int i = 0; i < p.length(); i++) {
			if (foundIt[p.get(i)]) return false;
			foundIt[p.get(i)] = true;
		}
		return true;
	}
	
	void visualInspection(int reps, CrossoverOperator<Permutation> xover) {
		for (int i = 0; i < reps; i++) {
			Permutation p1 = new Permutation(10);
			Permutation p2 = new Permutation(10);
			
			Permutation child1 = new Permutation(p1);
			Permutation child2 = new Permutation(p2);
			xover.cross(child1, child2);
			System.out.println("Crossover Result");
			System.out.println("Parent 1: " + p1);
			System.out.println("Parent 2: " + p2);
			System.out.println("Child 1 : " + child1);
			System.out.println("Child 2 : " + child2);
			System.out.println();
		}
	}		
	
	int[] findStartAndEnd(boolean[] fixedPoints) {
		int[] counts = new int[fixedPoints.length];
		int max = -1;
		for (int i = 0; i < counts.length; i++) {
			for (int j = i; j < counts.length; j++) {
				if (fixedPoints[j]) counts[i]++;
				else break;
			}
			if (max < 0 || counts[i] > counts[max]) max = i;
		}
		assertTrue(max >= 0 && counts[max] > 0);
		return new int[] {max, max + counts[max] - 1};
	}
	
	boolean[] findFixedPoints(Permutation child1, Permutation child2, Permutation original1, Permutation original2) {
		boolean[] fixedPoints = new boolean[original1.length()];
		for (int i = 0; i < fixedPoints.length; i++) {
			fixedPoints[i] = child1.get(i) == original1.get(i) && child2.get(i) == original2.get(i);
		}
		return fixedPoints;
	}
	
	void validateOrderingUOBX(Permutation child, Permutation order, boolean[] fixedPoints) {
		int[] inv = order.getInverse();
		int last = -1;
		for (int i = 0; i < fixedPoints.length; i++) {
			if (!fixedPoints[i]) {
				if (last >= 0) {
					assertTrue(inv[child.get(i)] > inv[child.get(last)]);
				}
				last = i;
			}
		}
	}
}
