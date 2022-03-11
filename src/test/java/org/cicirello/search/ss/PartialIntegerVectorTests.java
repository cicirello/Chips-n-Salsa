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
 
package org.cicirello.search.ss;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.representations.BoundedIntegerVector;

/**
 * JUnit tests for the PartialIntegerVector class.
 */
public class PartialIntegerVectorTests {
	
	@Test
	public void testConstructor1() {
		for (int n = 0; n < 4; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				PartialIntegerVector v = new PartialIntegerVector(n, min, max);
				if (n > 0) assertFalse(v.isComplete());
				else assertTrue(v.isComplete());
				assertEquals(0, v.size());
				assertEquals(n>0?width:0,v.numExtensions());
				for (int i = 0; i < v.numExtensions(); i++) {
					assertEquals(min + i, v.getExtension(i));
				}
				IntegerVector c = v.toComplete();
				assertTrue(c instanceof BoundedIntegerVector);
				assertEquals(n, c.length());
				for (int i = 0; i < n; i++) {
					assertTrue( c.get(i) >= min && c.get(i) <= max);
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(-1, 2, 2)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(1, 3, 2)
		);
	}
	
	@Test
	public void testConstructor2true() {
		for (int n = 0; n < 4; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				PartialIntegerVector v = new PartialIntegerVector(n, min, max, true);
				if (n > 0) assertFalse(v.isComplete());
				else assertTrue(v.isComplete());
				assertEquals(0, v.size());
				assertEquals(n>0?width:0,v.numExtensions());
				for (int i = 0; i < v.numExtensions(); i++) {
					assertEquals(min + i, v.getExtension(i));
				}
				IntegerVector c = v.toComplete();
				assertTrue(c instanceof BoundedIntegerVector);
				assertEquals(n, c.length());
				for (int i = 0; i < n; i++) {
					assertTrue( c.get(i) >= min && c.get(i) <= max);
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(-1, 2, 2, true)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(1, 3, 2, true)
		);
	}
	
	@Test
	public void testConstructor2false() {
		for (int n = 0; n < 4; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				PartialIntegerVector v = new PartialIntegerVector(n, min, max, false);
				if (n > 0) assertFalse(v.isComplete());
				else assertTrue(v.isComplete());
				assertEquals(0, v.size());
				assertEquals(n>0?width:0,v.numExtensions());
				for (int i = 0; i < v.numExtensions(); i++) {
					assertEquals(min + i, v.getExtension(i));
				}
				IntegerVector c = v.toComplete();
				assertFalse(c instanceof BoundedIntegerVector);
				assertEquals(n, c.length());
				for (int i = 0; i < n; i++) {
					assertTrue( c.get(i) >= min && c.get(i) <= max);
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(-1, 2, 2, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PartialIntegerVector(1, 3, 2, false)
		);
	}
	
	@Test
	public void testExtend() {
		for (int n = 1; n < 5; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				final PartialIntegerVector v = new PartialIntegerVector(n, min, max);
				for (int i = 0; i < n; i++) {
					assertFalse(v.isComplete());
					assertEquals(i, v.size());
					assertEquals(width, v.numExtensions());
					for (int j = 0; j < v.numExtensions(); j++) {
						assertEquals(min + j, v.getExtension(j));
					}
					ArrayIndexOutOfBoundsException thrown = assertThrows( 
						ArrayIndexOutOfBoundsException.class,
						() -> v.getExtension(v.numExtensions())
					);
					thrown = assertThrows( 
						ArrayIndexOutOfBoundsException.class,
						() -> v.extend(v.numExtensions())
					);
					v.extend(i % width);
					assertEquals(i+1, v.size());
					for (int j = 0; j <= i; j++) {
						assertEquals(min + j % width, v.get(j));
					}
					assertEquals(v.get(v.size()-1), v.getLast());
					thrown = assertThrows( 
						ArrayIndexOutOfBoundsException.class,
						() -> v.get(v.size())
					);
				}
				assertTrue(v.isComplete());
			}
		}
	}
	
	@Test
	public void testToCompleteWithBoundsEnforcement() {
		for (int n = 1; n < 5; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				PartialIntegerVector v = new PartialIntegerVector(n, min, max);
				for (int i = 0; i < n; i++) {
					v.extend(i % width);
					IntegerVector c = v.toComplete();
					assertTrue(c instanceof BoundedIntegerVector);
					assertEquals(n, c.length());
					for (int j = 0; j <= i; j++) {
						assertEquals(min + j % width, c.get(j));
					}
					for (int j = i+1; j < n; j++) {
						assertTrue(c.get(j) >= min && c.get(j) <= max);
					}
				}
			}
		}
	}
	
	@Test
	public void testToCompleteNoBoundsEnforcement() {
		for (int n = 1; n < 5; n++) {
			for (int width = 1; width <= 3; width++) {
				int min = 3;
				int max = min + width - 1;
				PartialIntegerVector v = new PartialIntegerVector(n, min, max, false);
				for (int i = 0; i < n; i++) {
					v.extend(i % width);
					IntegerVector c = v.toComplete();
					assertFalse(c instanceof BoundedIntegerVector);
					assertEquals(n, c.length());
					for (int j = 0; j <= i; j++) {
						assertEquals(min + j % width, c.get(j));
					}
					for (int j = i+1; j < n; j++) {
						assertTrue(c.get(j) >= min && c.get(j) <= max);
					}
				}
			}
		}
	}
}