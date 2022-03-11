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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.CyclicEdgeDistance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

/**
 * JUnit test cases for ThreeOptMutation.
 */
public class ThreeOptMutationTests {
	
	@Test
	public void testThreeOptMutation() {
		ThreeOptMutation mutation = new ThreeOptMutation();
		CyclicEdgeDistance distance = new CyclicEdgeDistance();
		for (int n = 5; n <= 10; n++) {
			Permutation p = new Permutation(n);
			for (int i = 0; i < 10; i++) {
				Permutation mutant = new Permutation(p);
				mutation.mutate(mutant);
				int d = distance.distance(p, mutant);
				assertTrue(d >= 2);
				assertTrue(d <= 3);
				assertNotEquals(p, mutant);
				mutation.undo(mutant);
				assertEquals(p, mutant);
			}
		}
		// only 2-changes in this case
		int n = 4;
		Permutation p = new Permutation(n);
		for (int i = 0; i < 10; i++) {
			Permutation mutant = new Permutation(p);
			mutation.mutate(mutant);
			assertEquals(2, distance.distance(p, mutant));
			assertNotEquals(p, mutant);
			mutation.undo(mutant);
			assertEquals(p, mutant);
		}
		// n < 4 should have no change
		for (n = 0; n < 4; n++) {
			p = new Permutation(n);
			Permutation mutant = new Permutation(p);
			mutation.mutate(mutant);
			assertEquals(p, mutant);
			mutation.undo(mutant);
			assertEquals(p, mutant);
		}
	}
	
	@Test
	public void testSplit() {
		ThreeOptMutation mutation = (new ThreeOptMutation()).split();
		CyclicEdgeDistance distance = new CyclicEdgeDistance();
		for (int n = 5; n <= 7; n++) {
			Permutation p = new Permutation(n);
			for (int i = 0; i < 5; i++) {
				Permutation mutant = new Permutation(p);
				mutation.mutate(mutant);
				int d = distance.distance(p, mutant);
				assertTrue(d >= 2);
				assertTrue(d <= 3);
				assertNotEquals(p, mutant);
				mutation.undo(mutant);
				assertEquals(p, mutant);
			}
		}
		// only 2-changes in this case
		int n = 4;
		Permutation p = new Permutation(n);
		for (int i = 0; i < 5; i++) {
			Permutation mutant = new Permutation(p);
			mutation.mutate(mutant);
			assertEquals(2, distance.distance(p, mutant));
			assertNotEquals(p, mutant);
			mutation.undo(mutant);
			assertEquals(p, mutant);
		}
	}

	@Test
	public void testInternalHelpers() {
		ThreeOptMutation mutation = new ThreeOptMutation();
		CyclicEdgeDistance distance = new CyclicEdgeDistance();
		int[] indexes = new int[3];
		for (int n = 5; n <= 8; n++) {
			Permutation p = new Permutation(n);
			HashSet<Edge> edges = getEdgeSet(p);
			HashMap<EdgeCombo, HashSet<EdgeCombo>> mapping2 = new HashMap<EdgeCombo, HashSet<EdgeCombo>>();
			HashMap<EdgeCombo, HashSet<EdgeCombo>> mapping3 = new HashMap<EdgeCombo, HashSet<EdgeCombo>>();
			int count2 = 0;
			int count3 = 0;
			int expectedCount3 = 4*(n*(n-1)*(n-2)/6 - n*(n-4) - n) + n*(n-4);
			for (int i = 0; i < n; i++) {
				for (int j = i+1; j < n; j++) {
					for (int k = j+1; k < n; k++) {
						for (int which = 0; which < 4; which++) {
							indexes[0]=i;
							indexes[1]=j;
							indexes[2]=k;
							Permutation mutant = new Permutation(p);
							mutation.threeOrTwoChange(indexes, which, mutant);
							int d = distance.distance(p, mutant);
							assertTrue(d >= 2 && d <= 3);
							HashSet<Edge> mutantEdges = getEdgeSet(mutant);
							EdgeCombo changedFrom = new EdgeCombo(mutantEdges, edges);
							EdgeCombo changedTo = new EdgeCombo(edges, mutantEdges);
							if (d == 2) {
								count2++;
								if (!mapping2.containsKey(changedFrom)) {
									HashSet<EdgeCombo> toSet = new HashSet<EdgeCombo>();
									toSet.add(changedTo);
									mapping2.put(changedFrom, toSet);
								} else {
									mapping2.get(changedFrom).add(changedTo);
								}
							} else if (d == 3) {
								count3++;
								if (!mapping3.containsKey(changedFrom)) {
									HashSet<EdgeCombo> toSet = new HashSet<EdgeCombo>();
									toSet.add(changedTo);
									mapping3.put(changedFrom, toSet);
								} else {
									mapping3.get(changedFrom).add(changedTo);
								}
							}
							
							mutation.undoThreeOrTwoChange(indexes, which, mutant);
							assertEquals(0, distance.distance(p, mutant));
							assertEquals(p, mutant);
						}
					}
				}
			}
			assertEquals(expectedCount3, count3);
			int expectedNumRemovedCombos3 = (n*(n-1)*(n-2)/6 - n*(n-4) - n) + n*(n-4);
			assertEquals(expectedNumRemovedCombos3, mapping3.size());
			Set<EdgeCombo> keys = mapping3.keySet();
			int totalNumReplacements = 0;
			for (EdgeCombo key : keys) {
				int s = mapping3.get(key).size();
				assertTrue(s==4 || s==1);
				totalNumReplacements += s;
			}
			assertEquals(expectedCount3, totalNumReplacements);
			
			assertEquals(n*(n-3)/2, mapping2.size());
			keys = mapping2.keySet();
			totalNumReplacements = 0;
			for (EdgeCombo key : keys) {
				int s = mapping2.get(key).size();
				assertTrue(s==1);
				totalNumReplacements += s;
			}
			assertEquals(n*(n-3)/2, totalNumReplacements);
		}
	}
	
	private HashSet<Edge> getEdgeSet(Permutation p) {
		HashSet<Edge> s = new HashSet<Edge>();
		for (int i = 1; i < p.length(); i++) {
			s.add(new Edge(p.get(i-1), p.get(i)));
		}
		if (p.length()>2) s.add(new Edge(p.get(0), p.get(p.length()-1)));
		return s;
	}
	
	private static class EdgeCombo {
		ArrayList<Edge> combo;
		
		EdgeCombo() {
			combo = new ArrayList<Edge>(3);
		}
		
		EdgeCombo(HashSet<Edge> original, HashSet<Edge> mutant) {
			this();
			for (Edge e : mutant) {
				if (!original.contains(e)) {
					addEdge(e);
				}
			}
			sort();
		}
		
		void addEdge(Edge e) {
			combo.add(e);
		}
		
		void sort() {
			Collections.sort(combo);
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof EdgeCombo)) return false;
			EdgeCombo other = (EdgeCombo)o;
			if (combo.size() != other.combo.size()) return false;
			for (int i = 0; i < combo.size(); i++) {
				if (!combo.get(i).equals(other.combo.get(i))) return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			int h = 0;
			for (Edge e : combo) {
				h = 31*h + e.hashCode();
			}
			return h;
		}
	}
	
	private static class Edge implements Comparable<Edge> {
		int a;
		int b;
		Edge(int a, int b) {
			if (a < b) {
				this.a = a;
				this.b = b;
			} else {
				this.a = b;
				this.b = a;
			}
		}
		
		@Override
		public int hashCode() {
			return 31*a + b;
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Edge)) return false;
			return a == ((Edge)other).a && b == ((Edge)other).b;
		}
		
		@Override
		public int compareTo(Edge other) {
			if (equals(other)) return 0;
			else if (a < other.a || a==other.a && b < other.b) return -1;
			else return 1;
		}
	}

}