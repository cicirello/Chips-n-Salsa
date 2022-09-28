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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for TruncationSelection.
 */
public class TruncationSelectionInternalMethodTests extends SharedTestSelectionOperators {
	
	@Test
	public void testTruncationSelectionPartitioningBestToRight_Integer() {
		for (int k = 1; k <= 9; k++) {
			TruncationSelection selection = new TruncationSelection(k);
			for (int n = 1; n <= 8; n++) {
				int truncateCount = Math.max(n - k, 0);
				PopFitVectorIntegerSimple pop1 = new PopFitVectorIntegerSimple(n);
				int[] partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				boolean[] found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= truncateCount);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) > truncateCount);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= truncateCount);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) > truncateCount);
				}
				
				// All duplicates low fitness
				int[] fits = new int[n];
				for (int i = 0; i < n; i++) {
					if (i < truncateCount) {
						fits[i] = 1;
					} else {
						fits[i] = 2 + i;
					}
				}
				pop1 = new PopFitVectorIntegerSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[2+n];
				for (int i = 0; i < truncateCount; i++) {
					assertEquals(1, pop1.getFitness(partitioned[i]));
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 2);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[2+n];
				for (int i = 0; i < truncateCount; i++) {
					assertEquals(1, pop1.getFitness(partitioned[i]));
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 2);
				}
				
				// all duplicates high fitness
				for (int i = 0; i < n; i++) {
					if (i < truncateCount) {
						fits[i] = 1 + i;
					} else {
						fits[i] = 100;
					}
				}
				pop1 = new PopFitVectorIntegerSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) < 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertEquals(100, pop1.getFitness(partitioned[i]));
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) < 100, "k: " + k + " partitioned: " + toString(partitioned) + "  fits: " + toString(fits));
				}
				for (int i = truncateCount; i < n; i++) {
					assertEquals(100, pop1.getFitness(partitioned[i]));
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				
				// duplicates surrounding boundary
				for (int i = 0; i < n; i++) {
					if (i < truncateCount - 1) {
						fits[i] = 1 + i;
					} else if (i < truncateCount + 2) {
						fits[i] = 100;
					} else {
						fits[i] = 200 + i;
					}
				}
				pop1 = new PopFitVectorIntegerSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 100);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 100);
				}
			}
		}
	}
	
	@Test
	public void testTruncationSelectionPartitioningBestToRight_Double() {
		for (int k = 1; k <= 9; k++) {
			TruncationSelection selection = new TruncationSelection(k);
			for (int n = 1; n <= 8; n++) {
				int truncateCount = Math.max(n - k, 0);
				PopFitVectorDoubleSimple pop1 = new PopFitVectorDoubleSimple(n);
				int[] partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				boolean[] found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= truncateCount);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) > truncateCount);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= truncateCount);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) > truncateCount);
				}
				
				// All duplicates low fitness
				int[] fits = new int[n];
				for (int i = 0; i < n; i++) {
					if (i < truncateCount) {
						fits[i] = 1;
					} else {
						fits[i] = 2 + i;
					}
				}
				pop1 = new PopFitVectorDoubleSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[2+n];
				for (int i = 0; i < truncateCount; i++) {
					assertEquals(1.0, pop1.getFitness(partitioned[i]), 0.0);
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 2);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[2+n];
				for (int i = 0; i < truncateCount; i++) {
					assertEquals(1.0, pop1.getFitness(partitioned[i]), 0.0);
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 2);
				}
				
				// all duplicates high fitness
				for (int i = 0; i < n; i++) {
					if (i < truncateCount) {
						fits[i] = 1 + i;
					} else {
						fits[i] = 100;
					}
				}
				pop1 = new PopFitVectorDoubleSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) < 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertEquals(100.0, pop1.getFitness(partitioned[i]), 0.0);
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) < 100, "k: " + k + " partitioned: " + toString(partitioned) + "  fits: " + toString(fits));
				}
				for (int i = truncateCount; i < n; i++) {
					assertEquals(100.0, pop1.getFitness(partitioned[i]), 0.0);
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
				}
				
				// duplicates surrounding boundary
				for (int i = 0; i < n; i++) {
					if (i < truncateCount - 1) {
						fits[i] = 1 + i;
					} else if (i < truncateCount + 2) {
						fits[i] = 100;
					} else {
						fits[i] = 200 + i;
					}
				}
				pop1 = new PopFitVectorDoubleSimple(fits);
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 100);
				}
				
				pop1.reverse();
				partitioned = selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount);
				assertEquals(n, partitioned.length);
				found = new boolean[n];
				for (int i = 0; i < truncateCount; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) <= 100);
				}
				for (int i = truncateCount; i < n; i++) {
					assertFalse(found[partitioned[i]]);
					found[partitioned[i]] = true;
					assertTrue(pop1.getFitness(partitioned[i]) >= 100);
				}
			}
		}
	}
	
	private String toString(int[] array) {
		String s = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			s += " " + array[i];
		}
		return s;
	}
}
