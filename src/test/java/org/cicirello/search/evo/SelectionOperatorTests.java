/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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

/**
 * JUnit 4 test cases for selection operators.
 */
public class SelectionOperatorTests {
	
	@Test
	public void testRandomSelection() {
		RandomSelection selection = new RandomSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		RandomSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
	}
	
	
	private void validateIndexes_Double(SelectionOperator selection) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorDouble pf = new PopFitVectorDouble(s);
			int[] count = new int[s];
			for (int i = Math.max(0, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (s >= 4) {
				boolean different = false;
				for (int i = 1; i < s && !different; i++) {
					if (count[i] != count[i-1]) {
						different = true;
					}
				}
				assertTrue(different);
			}
		}
	}
	
	private void validateIndexes_Integer(SelectionOperator selection) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorInteger pf = new PopFitVectorInteger(s);
			int[] count = new int[s];
			for (int i = Math.max(0, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (s >= 4) {
				boolean different = false;
				for (int i = 1; i < s && !different; i++) {
					if (count[i] != count[i-1]) {
						different = true;
					}
				}
				assertTrue(different);
			}
		}
	}
	
	private static class PopFitVectorDouble implements PopulationFitnessVector.Double {
		
		private int s;
		
		public PopFitVectorDouble(int size) {
			s = size;
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public double getFitness(int i) {
			return 1.0;
		}
	}
	
	private static class PopFitVectorInteger implements PopulationFitnessVector.Integer {
		
		private int s;
		
		public PopFitVectorInteger(int size) {
			s = size;
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public int getFitness(int i) {
			return 1;
		}
	}
}