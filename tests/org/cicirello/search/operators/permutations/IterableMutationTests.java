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
 
package org.cicirello.search.operators.permutations;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import java.util.HashSet;

/**
 * JUnit tests for iterable mutation operators.
 */
public class IterableMutationTests {
	
	@Test
	public void testAdjSwapIterator() {
		AdjacentSwapMutation m = new AdjacentSwapMutation();
		for (int n = 0; n <= 6; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int i = 0; i < n-1; i++) {
				Permutation p = original.copy();
				p.swap(i, i+1);
				expectedNeighbors.add(p);
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testSwapIterator() {
		SwapMutation m = new SwapMutation();
		for (int n = 0; n <= 6; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int i = 0; i < n; i++) {
				for (int j = i+1; j < n; j++) {
					Permutation p = original.copy();
					p.swap(i, j);
					expectedNeighbors.add(p);
				}
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testReversalIterator() {
		ReversalMutation m = new ReversalMutation();
		for (int n = 0; n <= 6; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int i = 0; i < n; i++) {
				for (int j = i+1; j < n; j++) {
					Permutation p = original.copy();
					p.reverse(i, j);
					expectedNeighbors.add(p);
				}
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testInsertionIterator() {
		InsertionMutation m = new InsertionMutation();
		for (int n = 0; n <= 6; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i==j) continue;
					Permutation p = original.copy();
					p.removeAndInsert(i, j);
					expectedNeighbors.add(p);
				}
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testBlockMoveIterator() {
		BlockMoveMutation m = new BlockMoveMutation();
		for (int n = 0; n <= 8; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int i = 0; i < n; i++) {
				for (int j = i+1; j < n; j++) {
					for (int s = 1; j+s-1 < n; s++) {
						Permutation p = original.copy();
						p.removeAndInsert(j, s, i);
						expectedNeighbors.add(p);
					}
				}
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testBlockInterchangeIterator() {
		BlockInterchangeMutation m = new BlockInterchangeMutation();
		for (int n = 0; n <= 8; n++) {
			// generate the set of actual neigbors of a random permutation of length n
			HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
			Permutation original = new Permutation(n);
			for (int h = 0; h < n; h++) {
				for (int i = h; i < n; i++) {
					for (int j = i+1; j < n; j++) {
						for (int k = j; k < n; k++) {
							Permutation p = original.copy();
							p.swapBlocks(h, i, j, k);
							expectedNeighbors.add(p);
						}
					}
				}
			}
			// validate the MutationIterator:
			// (1) Verify that it generates the correct set of neighbors
			// (2) Verify that rollback() will rollback to original
			// (3) Verify that setSavepoint and rollback work correctly in combination
			validate(m, original, expectedNeighbors);
		}
	}
	
	@Test
	public void testWindowedSwapIterator() {
		for (int n = 0; n <= 6; n++) {
			for (int w = 1; w <= n+1; w++) {
				WindowLimitedSwapMutation m = new WindowLimitedSwapMutation(w);
				// generate the set of actual neigbors of a random permutation of length n
				HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
				Permutation original = new Permutation(n);
				for (int i = 0; i < n; i++) {
					for (int j = i+1; j < n && j-i <= w; j++) {
						Permutation p = original.copy();
						p.swap(i, j);
						expectedNeighbors.add(p);
					}
				}
				// validate the MutationIterator:
				// (1) Verify that it generates the correct set of neighbors
				// (2) Verify that rollback() will rollback to original
				// (3) Verify that setSavepoint and rollback work correctly in combination
				validate(m, original, expectedNeighbors);
			}
		}
	}
	
	@Test
	public void testWindowedReversalIterator() {
		for (int n = 0; n <= 6; n++) {
			for (int w = 1; w <= n+1; w++) {
				WindowLimitedReversalMutation m = new WindowLimitedReversalMutation(w);
				// generate the set of actual neigbors of a random permutation of length n
				HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
				Permutation original = new Permutation(n);
				for (int i = 0; i < n; i++) {
					for (int j = i+1; j < n && j-i <= w; j++) {
						Permutation p = original.copy();
						p.reverse(i, j);
						expectedNeighbors.add(p);
					}
				}
				// validate the MutationIterator:
				// (1) Verify that it generates the correct set of neighbors
				// (2) Verify that rollback() will rollback to original
				// (3) Verify that setSavepoint and rollback work correctly in combination
				validate(m, original, expectedNeighbors);
			}
		}
	}
	
	@Test
	public void testWindowedInsertionIterator() {
		for (int n = 0; n <= 6; n++) {
			for (int w = 1; w <= n+1; w++) {
				WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation(w);
				// generate the set of actual neigbors of a random permutation of length n
				HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
				Permutation original = new Permutation(n);
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (i==j || Math.abs(j-i) > w) continue;
						Permutation p = original.copy();
						p.removeAndInsert(i, j);
						expectedNeighbors.add(p);
					}
				}
				// validate the MutationIterator:
				// (1) Verify that it generates the correct set of neighbors
				// (2) Verify that rollback() will rollback to original
				// (3) Verify that setSavepoint and rollback work correctly in combination
				validate(m, original, expectedNeighbors);
			}
		}
	}
	
	@Test
	public void testWindowedBlockMoveIterator() {
		for (int n = 0; n <= 8; n++) {
			for (int w = 1; w <= n+1; w++) {
				WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation(w);
				// generate the set of actual neigbors of a random permutation of length n
				HashSet<Permutation> expectedNeighbors = new HashSet<Permutation>();
				Permutation original = new Permutation(n);
				for (int i = 0; i < n; i++) {
					for (int j = i+1; j < n; j++) {
						for (int s = 1; j+s-1 < n; s++) {
							if (Math.abs(j+s-1-i) > w) continue;
							Permutation p = original.copy();
							p.removeAndInsert(j, s, i);
							expectedNeighbors.add(p);
						}
					}
				}
				// validate the MutationIterator:
				// (1) Verify that it generates the correct set of neighbors
				// (2) Verify that rollback() will rollback to original
				// (3) Verify that setSavepoint and rollback work correctly in combination
				validate(m, original, expectedNeighbors);
			}
		}
	}
	
	
	
	
	private void validate(IterableMutationOperator<Permutation> mutation, Permutation original, HashSet<Permutation> expectedNeighbors) {
		Permutation p = original.copy();
		MutationIterator iter = mutation.iterator(p);
		HashSet<Permutation> neighbors = new HashSet<Permutation>();
		int count = 0;
		while (iter.hasNext()) {
			iter.nextMutant();
			neighbors.add(p.copy());
			count++;
		}
		final MutationIterator iterNoMoreMutantsTest = iter;
		IllegalStateException thrown = assertThrows( 
			"verify nextMutant throws exception if no more mutants",
			IllegalStateException.class,
			() -> iterNoMoreMutantsTest.nextMutant()
		);
		iter.rollback();
		assertEquals("verify number of neighbors", expectedNeighbors.size(), neighbors.size());
		assertEquals("verify set of neighbors are as expected, original="+original, expectedNeighbors, neighbors);
		assertEquals("verify rolled back to original", original, p);
		assertEquals("verify number of neighbors", expectedNeighbors.size(), count);
		iter.rollback();
		assertEquals("verify extra rollback does nothing", original, p);
		for (int i = 0; i < count; i++) {
			iter = mutation.iterator(p);
			Permutation saved = p.copy();
			int j = 0;
			while (iter.hasNext()) {
				iter.nextMutant();
				if (j==i) {
					iter.setSavepoint();
					saved = p.copy();
				}
				j++;
			}
			iter.rollback();
			assertEquals("verify rolled back to last savepoint, original="+original+" i="+i, saved, p);
		}
		// test rollback immediately after setSavepoint
		for (int i = 0; i < count; i++) {
			iter = mutation.iterator(p);
			Permutation saved = p.copy();
			int j = 0;
			while (iter.hasNext()) {
				iter.nextMutant();
				if (j==i) {
					iter.setSavepoint();
					saved = p.copy();
					break;
				}
				j++;
			}
			iter.rollback();
			assertEquals("rollback immediately after setSavepoint, verify rolled back to last savepoint, original="+original+" i="+i, saved, p);
			final MutationIterator iterRolledWithoutIteratingOverAllTest = iter;
			thrown = assertThrows( 
				"verify nextMutant throws exception if rolled back",
				IllegalStateException.class,
				() -> iterRolledWithoutIteratingOverAllTest.nextMutant()
			);
			assertFalse("verify no next if rolled back", iter.hasNext());
		}
		// test rollback two steps after setSavepoint
		for (int i = 0; i < count-1; i++) {
			iter = mutation.iterator(p);
			Permutation saved = p.copy();
			int j = 0;
			while (iter.hasNext()) {
				iter.nextMutant();
				if (j==i) {
					iter.setSavepoint();
					saved = p.copy();
				} else if (j > i) {
					break;
				}
				j++;
			}
			iter.rollback();
			assertEquals("rollback two steps after setSavepoint, verify rolled back to last savepoint, original="+original+" i="+i, saved, p);
		}
	}
}