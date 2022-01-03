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

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.util.Copyable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * JUnittest cases for EliteSet.
 */
public class EliteSetTests {
	
	// double-valued fitness
	
	@Test
	public void testIncreasingFitnessDouble() {
		for (int numElite = 1; numElite <= 5; numElite++) {
			ArrayList<PopulationMember.DoubleFitness<TestObject>> addThese = new ArrayList<PopulationMember.DoubleFitness<TestObject>>();
			for (int i = 1; i <= 10; i++) {
				addThese.add(new PopulationMember.DoubleFitness<TestObject>(new TestObject(i), i));
				addThese.add(new PopulationMember.DoubleFitness<TestObject>(new TestObject(i), i));
			}
			EliteSet.DoubleFitness<TestObject> eliteSet = new EliteSet.DoubleFitness<TestObject>(addThese, numElite);
			int count = 0;
			boolean[] inSet = new boolean[11];
			for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// Try adding them again to make sure doesn't change anything (e.g., shouldn't allow duplicates).
			eliteSet.offerAll(addThese);
			count = 0;
			inSet = new boolean[11];
			for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// explicit iterator
			count = 0;
			inSet = new boolean[11];
			final Iterator<PopulationMember.DoubleFitness<TestObject>> iter = eliteSet.iterator();
			while (iter.hasNext()) {
				PopulationMember.DoubleFitness<TestObject> e = iter.next();
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			NoSuchElementException thrown = assertThrows( 
				NoSuchElementException.class,
				() -> iter.next()
			);
		}
	}
	
	@Test
	public void testDecreasingFitnessDouble() {
		for (int numElite = 1; numElite <= 5; numElite++) {
			ArrayList<PopulationMember.DoubleFitness<TestObject>> addThese = new ArrayList<PopulationMember.DoubleFitness<TestObject>>();
			for (int i = 10; i >= 1; i--) {
				addThese.add(new PopulationMember.DoubleFitness<TestObject>(new TestObject(i), i));
				addThese.add(new PopulationMember.DoubleFitness<TestObject>(new TestObject(i), i));
			}
			EliteSet.DoubleFitness<TestObject> eliteSet = new EliteSet.DoubleFitness<TestObject>(addThese, numElite);
			int count = 0;
			boolean[] inSet = new boolean[11];
			for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// Try adding them again to make sure doesn't change anything (e.g., shouldn't allow duplicates).
			eliteSet.offerAll(addThese);
			count = 0;
			inSet = new boolean[11];
			for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// test clear
			eliteSet.clear();
			final Iterator<PopulationMember.DoubleFitness<TestObject>> iter = eliteSet.iterator();
			assertFalse(iter.hasNext());
		}
	}
	
	@Test
	public void testNewEliteIsLeastFitOfElite_Double() {
		int[] fitnesses = {1, 3, 5, 7, 9};
		ArrayList<PopulationMember.DoubleFitness<TestObject>> addThese = new ArrayList<PopulationMember.DoubleFitness<TestObject>>();
		for (int fitness : fitnesses) {
			addThese.add(new PopulationMember.DoubleFitness<TestObject>(new TestObject(fitness), fitness));
		}
		EliteSet.DoubleFitness<TestObject> eliteSet = new EliteSet.DoubleFitness<TestObject>(addThese, 3);
		int count = 0;
		boolean[] inSet = new boolean[10];
		for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
			count++;
			assertFalse(inSet[e.getCandidate().id]);
			inSet[e.getCandidate().id] = true;
		}
		assertEquals(3, count);
		assertTrue(inSet[5]);
		assertTrue(inSet[7]);
		assertTrue(inSet[9]);
		eliteSet.offer(new PopulationMember.DoubleFitness<TestObject>(new TestObject(6), 6));
		count = 0;
		inSet = new boolean[10];
		for (PopulationMember.DoubleFitness<TestObject> e : eliteSet) {
			count++;
			assertFalse(inSet[e.getCandidate().id]);
			inSet[e.getCandidate().id] = true;
		}
		assertEquals(3, count);
		assertTrue(inSet[6]);
		assertTrue(inSet[7]);
		assertTrue(inSet[9]);
	}
	
	// int-valued fitness
	
	@Test
	public void testIncreasingFitnessInteger() {
		for (int numElite = 1; numElite <= 5; numElite++) {
			ArrayList<PopulationMember.IntegerFitness<TestObject>> addThese = new ArrayList<PopulationMember.IntegerFitness<TestObject>>();
			for (int i = 1; i <= 10; i++) {
				addThese.add(new PopulationMember.IntegerFitness<TestObject>(new TestObject(i), i));
				addThese.add(new PopulationMember.IntegerFitness<TestObject>(new TestObject(i), i));
			}
			EliteSet.IntegerFitness<TestObject> eliteSet = new EliteSet.IntegerFitness<TestObject>(addThese, numElite);
			int count = 0;
			boolean[] inSet = new boolean[11];
			for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// Try adding them again to make sure doesn't change anything (e.g., shouldn't allow duplicates).
			eliteSet.offerAll(addThese);
			count = 0;
			inSet = new boolean[11];
			for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// explicit iterator
			count = 0;
			inSet = new boolean[11];
			final Iterator<PopulationMember.IntegerFitness<TestObject>> iter = eliteSet.iterator();
			while (iter.hasNext()) {
				PopulationMember.IntegerFitness<TestObject> e = iter.next();
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			NoSuchElementException thrown = assertThrows( 
				NoSuchElementException.class,
				() -> iter.next()
			);
		}
	}
	
	@Test
	public void testDecreasingFitnessInteger() {
		for (int numElite = 1; numElite <= 5; numElite++) {
			ArrayList<PopulationMember.IntegerFitness<TestObject>> addThese = new ArrayList<PopulationMember.IntegerFitness<TestObject>>();
			for (int i = 10; i >= 1; i--) {
				addThese.add(new PopulationMember.IntegerFitness<TestObject>(new TestObject(i), i));
				addThese.add(new PopulationMember.IntegerFitness<TestObject>(new TestObject(i), i));
			}
			EliteSet.IntegerFitness<TestObject> eliteSet = new EliteSet.IntegerFitness<TestObject>(addThese, numElite);
			int count = 0;
			boolean[] inSet = new boolean[11];
			for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// Try adding them again to make sure doesn't change anything (e.g., shouldn't allow duplicates).
			eliteSet.offerAll(addThese);
			count = 0;
			inSet = new boolean[11];
			for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
				count++;
				assertFalse(inSet[e.getCandidate().id]);
				inSet[e.getCandidate().id] = true;
				assertTrue(e.getCandidate().id > 10 - numElite);
			}
			assertEquals(numElite, count);
			
			// test clear
			eliteSet.clear();
			final Iterator<PopulationMember.IntegerFitness<TestObject>> iter = eliteSet.iterator();
			assertFalse(iter.hasNext());
		}
	}
	
	@Test
	public void testNewEliteIsLeastFitOfElite_Integer() {
		int[] fitnesses = {1, 3, 5, 7, 9};
		ArrayList<PopulationMember.IntegerFitness<TestObject>> addThese = new ArrayList<PopulationMember.IntegerFitness<TestObject>>();
		for (int fitness : fitnesses) {
			addThese.add(new PopulationMember.IntegerFitness<TestObject>(new TestObject(fitness), fitness));
		}
		EliteSet.IntegerFitness<TestObject> eliteSet = new EliteSet.IntegerFitness<TestObject>(addThese, 3);
		int count = 0;
		boolean[] inSet = new boolean[10];
		for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
			count++;
			assertFalse(inSet[e.getCandidate().id]);
			inSet[e.getCandidate().id] = true;
		}
		assertEquals(3, count);
		assertTrue(inSet[5]);
		assertTrue(inSet[7]);
		assertTrue(inSet[9]);
		eliteSet.offer(new PopulationMember.IntegerFitness<TestObject>(new TestObject(6), 6));
		count = 0;
		inSet = new boolean[10];
		for (PopulationMember.IntegerFitness<TestObject> e : eliteSet) {
			count++;
			assertFalse(inSet[e.getCandidate().id]);
			inSet[e.getCandidate().id] = true;
		}
		assertEquals(3, count);
		assertTrue(inSet[6]);
		assertTrue(inSet[7]);
		assertTrue(inSet[9]);
	}
	
	private static class TestObject implements Copyable<TestObject> {
		
		private int id;
		
		private TestObject(int id) {
			this.id = id;
		}
		
		@Override
		public TestObject copy() {
			return new TestObject(id);
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		
		@Override
		public boolean equals(Object other) {
			return id == ((TestObject)other).id;
		}
	}
}
