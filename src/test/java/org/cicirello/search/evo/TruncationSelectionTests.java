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
public class TruncationSelectionTests extends SharedTestSelectionOperators {
	
	@Test
	public void testTruncationSelection() {
		for (int k = 1; k <= 5; k++) {
			TruncationSelection selection = new TruncationSelection(k);
			validateIndexes_Double(selection, k > 1);
			validateIndexes_Integer(selection, k > 1);
			TruncationSelection selection2 = selection.split();
			validateIndexes_Double(selection2, k > 1);
			validateIndexes_Integer(selection2, k > 1);
			
			validateMostFitTruncationSelection_Double(selection, k);
			validateMostFitTruncationSelection_Integer(selection, k);
			validateMostFitTruncationSelection_Double(selection2, k);
			validateMostFitTruncationSelection_Integer(selection2, k);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new TruncationSelection(0)
		);
	}
	
	private void validateMostFitTruncationSelection_Double(TruncationSelection selection, int k) {
		PopFitVectorDouble pf1 = new PopFitVectorDouble(16);
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf1, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] >= 16 - k);
			}
		}
		pf1.reverse();
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf1, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] < k);
			}
		}
		
		PopFitVectorDoubleSimple pf2 = new PopFitVectorDoubleSimple(16);
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf2, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] >= 16 - k);
			}
		}
		pf2.reverse();
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf2, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] < k);
			}
		}
	}
	
	private void validateMostFitTruncationSelection_Integer(TruncationSelection selection, int k) {
		PopFitVectorInteger pf1 = new PopFitVectorInteger(16);
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf1, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] >= 16 - k);
			}
		}
		pf1.reverse();
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf1, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] < k);
			}
		}
		
		PopFitVectorIntegerSimple pf2 = new PopFitVectorIntegerSimple(16);
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf2, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] >= 16 - k);
			}
		}
		pf2.reverse();
		for (int i = 1; i <= 32; i*=2) {
			int[] selected = new int[i];
			selection.select(pf2, selected);
			for (int j = 0; j < i; j++) {
				assertTrue(selected[j] < k);
			}
		}
	}
}
