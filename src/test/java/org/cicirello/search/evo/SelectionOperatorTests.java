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
 * JUnit test cases for selection operators.
 */
public class SelectionOperatorTests {
	
	@Test
	public void testExponentialRankSelection() {
		double[] cValues = {Math.ulp(0.0), 0.25, 0.5, 0.75, 1.0-Math.ulp(1.0)};
		for (double c : cValues) {
			ExponentialRankSelection selection = new ExponentialRankSelection(c);
			validateIndexes_Double(selection, c >= 0.5);
			validateIndexes_Integer(selection, c >= 0.5);
			ExponentialRankSelection selection2 = selection.split();
			validateIndexes_Double(selection2, c >= 0.5);
			validateIndexes_Integer(selection2, c >= 0.5);
		}
		double c = 0.5;
		ExponentialRankSelection selection = new ExponentialRankSelection(c);
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		for (int n = 2; n <= 8; n *= 2) {
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			double[] weights = selection.computeWeightRunningSum(f1);
			for (int i = n-1; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				double expectedDelta = Math.pow(c,n-(i+1));
				assertEquals(expectedDelta, delta, 1E-10);
			}
			assertEquals(Math.pow(c,n-1), weights[0], 1E-10);
			
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			weights = selection.computeWeightRunningSum(f2);
			for (int i = n-1; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				double expectedDelta = Math.pow(c,n-(i+1));
				assertEquals(expectedDelta, delta, 1E-10);
			}
			assertEquals(Math.pow(c,n-1), weights[0], 1E-10);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ExponentialRankSelection(0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ExponentialRankSelection(1.0)
		);
	}
	
	@Test
	public void testExponentialRankSUS() {
		double[] cValues = {Math.ulp(0.0), 0.25, 0.5, 0.75, 1.0-Math.ulp(1.0)};
		for (double c : cValues) {
			ExponentialRankStochasticUniversalSampling selection = new ExponentialRankStochasticUniversalSampling(c);
			validateIndexes_Double(selection, c >= 0.5);
			validateIndexes_Integer(selection, c >= 0.5);
			ExponentialRankStochasticUniversalSampling selection2 = selection.split();
			validateIndexes_Double(selection2, c >= 0.5);
			validateIndexes_Integer(selection2, c >= 0.5);
		}
		double c = 0.5;
		ExponentialRankStochasticUniversalSampling selection = new ExponentialRankStochasticUniversalSampling(c);
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		for (int n = 2; n <= 8; n *= 2) {
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			double[] weights = selection.computeWeightRunningSum(f1);
			for (int i = n-1; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				double expectedDelta = Math.pow(c,n-(i+1));
				assertEquals(expectedDelta, delta, 1E-10);
			}
			assertEquals(Math.pow(c,n-1), weights[0], 1E-10);
			
			validateExpectedCountsSUSWithRanks(selection, f1);
			
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			weights = selection.computeWeightRunningSum(f2);
			for (int i = n-1; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				double expectedDelta = Math.pow(c,n-(i+1));
				assertEquals(expectedDelta, delta, 1E-10);
			}
			assertEquals(Math.pow(c,n-1), weights[0], 1E-10);
			
			validateExpectedCountsSUSWithRanks(selection, f2);
		}
		
		PopFitVectorDoubleSimple f1 = new PopFitVectorDoubleSimple(100);
		validateExpectedCountsSUSWithRanks(selection, f1);
		PopFitVectorIntegerSimple f2 = new PopFitVectorIntegerSimple(100);
		validateExpectedCountsSUSWithRanks(selection, f2);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ExponentialRankStochasticUniversalSampling(0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ExponentialRankStochasticUniversalSampling(1.0)
		);
	}
	
	@Test
	public void testLinearRankSelection() {
		double[] cValues = {1.0, 1.25, 1.5, 1.75, 2.0};
		for (double c : cValues) {
			LinearRankSelection selection = new LinearRankSelection(c);
			validateIndexes_Double(selection);
			validateIndexes_Integer(selection);
			LinearRankSelection selection2 = selection.split();
			validateIndexes_Double(selection2);
			validateIndexes_Integer(selection2);
		}
		LinearRankSelection selection = new LinearRankSelection(2.0);
		// Following two checks may sporadically fail due to random chance.
		// Increase 2nd parameter to decrease probability of such failure (keep it even though).
		validateHigherFitnessSelectedMoreOften_Double(selection, 40);
		validateHigherFitnessSelectedMoreOften_Integer(selection, 40);
		
		LinearRankSelection selectionUniform = new LinearRankSelection(1.0);
		
		for (int n = 2; n <= 8; n *= 2) {
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			double[] weights = selection.computeWeightRunningSum(f1);
			assertEquals(0.0, weights[0], 1E-10);
			assertEquals(n, weights[n-1], 1E-10);
			double expectedDelta = 2.0;
			assertEquals(expectedDelta, weights[n-1] - weights[n-2], 1E-10);
			for (int i = n-2; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				assertTrue(delta <= expectedDelta);
				expectedDelta = delta;
			}
			
			weights = selectionUniform.computeWeightRunningSum(f1);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(i+1.0, weights[i], 1E-10);
			}
			
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			weights = selection.computeWeightRunningSum(f2);
			assertEquals(0.0, weights[0], 1E-10);
			assertEquals(n, weights[n-1], 1E-10);
			expectedDelta = 2.0;
			assertEquals(expectedDelta, weights[n-1] - weights[n-2], 1E-10);
			for (int i = n-2; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				assertTrue(delta <= expectedDelta);
				expectedDelta = delta;
			}
			
			weights = selectionUniform.computeWeightRunningSum(f2);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(i+1.0, weights[i], 1E-10);
			}
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new LinearRankSelection(2.0+Math.ulp(2.0))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new LinearRankSelection(1.0-Math.ulp(1.0))
		);
	}
	
	@Test
	public void testLinearRankSUS() {
		double[] cValues = {1.0, 1.25, 1.5, 1.75, 2.0};
		for (double c : cValues) {
			LinearRankStochasticUniversalSampling selection = new LinearRankStochasticUniversalSampling(c);
			validateIndexes_Double(selection);
			validateIndexes_Integer(selection);
			LinearRankStochasticUniversalSampling selection2 = selection.split();
			validateIndexes_Double(selection2);
			validateIndexes_Integer(selection2);
		}
		LinearRankStochasticUniversalSampling selection = new LinearRankStochasticUniversalSampling(2.0);
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		LinearRankStochasticUniversalSampling selectionUniform = new LinearRankStochasticUniversalSampling(1.0);
		
		for (int n = 2; n <= 8; n *= 2) {
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			double[] weights = selection.computeWeightRunningSum(f1);
			assertEquals(0.0, weights[0], 1E-10);
			assertEquals(n, weights[n-1], 1E-10);
			double expectedDelta = 2.0;
			assertEquals(expectedDelta, weights[n-1] - weights[n-2], 1E-10);
			for (int i = n-2; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				assertTrue(delta <= expectedDelta);
				expectedDelta = delta;
			}
			
			weights = selectionUniform.computeWeightRunningSum(f1);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(i+1.0, weights[i], 1E-10);
			}
			
			validateExpectedCountsSUSWithRanks(selection, f1);
			validateExpectedCountsSUSWithRanks(selectionUniform, f1);
			
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			weights = selection.computeWeightRunningSum(f2);
			assertEquals(0.0, weights[0], 1E-10);
			assertEquals(n, weights[n-1], 1E-10);
			expectedDelta = 2.0;
			assertEquals(expectedDelta, weights[n-1] - weights[n-2], 1E-10);
			for (int i = n-2; i > 0; i--) {
				double delta = weights[i] - weights[i-1];
				assertTrue(delta <= expectedDelta);
				expectedDelta = delta;
			}
			
			weights = selectionUniform.computeWeightRunningSum(f2);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(i+1.0, weights[i], 1E-10);
			}
			
			validateExpectedCountsSUSWithRanks(selection, f2);
			validateExpectedCountsSUSWithRanks(selectionUniform, f2);
		}
		
		PopFitVectorDoubleSimple f1 = new PopFitVectorDoubleSimple(100);
		validateExpectedCountsSUSWithRanks(selection, f1);
		validateExpectedCountsSUSWithRanks(selectionUniform, f1);
		PopFitVectorIntegerSimple f2 = new PopFitVectorIntegerSimple(100);
		validateExpectedCountsSUSWithRanks(selection, f2);
		validateExpectedCountsSUSWithRanks(selectionUniform, f2);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new LinearRankStochasticUniversalSampling(2.0+Math.ulp(2.0))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new LinearRankStochasticUniversalSampling(1.0-Math.ulp(1.0))
		);
	}
	
	@Test
	public void testRandomSelection() {
		RandomSelection selection = new RandomSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		RandomSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
	}
	
	@Test
	public void testTournamentSelection() {
		for (int k = 2; k <= 4; k++) {
			TournamentSelection selection = new TournamentSelection(k);
			validateIndexes_Double(selection);
			validateIndexes_Integer(selection);
			TournamentSelection selection2 = selection.split();
			validateIndexes_Double(selection2);
			validateIndexes_Integer(selection2);
		}
		
		TournamentSelection selection = new TournamentSelection(4);
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new TournamentSelection(1)
		);
	}
	
	@Test
	public void testBinaryTournamentSelection() {
		TournamentSelection selection = new TournamentSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		TournamentSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
	}
	
	@Test
	public void testFitnessProportionalSelection() {
		FitnessProportionalSelection selection = new FitnessProportionalSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		FitnessProportionalSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateComputeRunningSum(selection);
		validateComputeRunningSum(selection2);
	}
	
	@Test
	public void testShiftedFitnessProportionalSelection() {
		ShiftedFitnessProportionalSelection selection = new ShiftedFitnessProportionalSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		ShiftedFitnessProportionalSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateComputeRunningSumShifted(selection);
		validateComputeRunningSumShifted(selection2);
	}
	
	@Test
	public void testBiasedFitnessProportionalSelection() {
		BiasedFitnessProportionalSelection selection = new BiasedFitnessProportionalSelection(x -> x*x);
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		BiasedFitnessProportionalSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateBiasedComputeRunningSum(selection);
		validateBiasedComputeRunningSum(selection2);
	}
	
	@Test
	public void testBiasedShiftedFitnessProportionalSelection() {
		BiasedShiftedFitnessProportionalSelection selection = new BiasedShiftedFitnessProportionalSelection(x -> x*x);
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		BiasedShiftedFitnessProportionalSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateBiasedComputeRunningSumShifted(selection);
		validateBiasedComputeRunningSumShifted(selection2);
	}
	
	@Test
	public void testSUS() {
		StochasticUniversalSampling selection = new StochasticUniversalSampling();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		StochasticUniversalSampling selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateComputeRunningSum(selection);
		validateComputeRunningSum(selection2);
		
		validateExpectedCountsSUS(selection, new PopFitVectorDouble(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorInteger(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorDoubleSimple(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorIntegerSimple(16), x -> x);
		
		validateExpectedCountsSUS(selection2, new PopFitVectorDouble(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorInteger(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorDoubleSimple(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorIntegerSimple(16), x -> x);
	}
	
	@Test
	public void testShiftedSUS() {
		ShiftedStochasticUniversalSampling selection = new ShiftedStochasticUniversalSampling();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		ShiftedStochasticUniversalSampling selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateComputeRunningSumShifted(selection);
		validateComputeRunningSumShifted(selection2);
		
		validateExpectedCountsSUS(selection, new PopFitVectorDouble(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorInteger(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorDoubleSimple(16), x -> x);
		validateExpectedCountsSUS(selection, new PopFitVectorIntegerSimple(16), x -> x);
		
		validateExpectedCountsSUS(selection2, new PopFitVectorDouble(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorInteger(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorDoubleSimple(16), x -> x);
		validateExpectedCountsSUS(selection2, new PopFitVectorIntegerSimple(16), x -> x);
	}
	
	@Test
	public void testBiasedSUS() {
		BiasedStochasticUniversalSampling selection = new BiasedStochasticUniversalSampling(x -> x*x);
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		BiasedStochasticUniversalSampling selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateBiasedComputeRunningSum(selection);
		validateBiasedComputeRunningSum(selection2);
		
		validateExpectedCountsSUS(selection, new PopFitVectorDouble(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorInteger(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorDoubleSimple(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorIntegerSimple(16), x -> x*x);
		
		validateExpectedCountsSUS(selection2, new PopFitVectorDouble(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorInteger(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorDoubleSimple(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorIntegerSimple(16), x -> x*x);
	}
	
	@Test
	public void testBiasedShiftedSUS() {
		BiasedShiftedStochasticUniversalSampling selection = new BiasedShiftedStochasticUniversalSampling(x -> x*x);
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		BiasedShiftedStochasticUniversalSampling selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		validateBiasedComputeRunningSumShifted(selection);
		validateBiasedComputeRunningSumShifted(selection2);
		
		validateExpectedCountsSUS(selection, new PopFitVectorDouble(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorInteger(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorDoubleSimple(16), x -> x*x);
		validateExpectedCountsSUS(selection, new PopFitVectorIntegerSimple(16), x -> x*x);
		
		validateExpectedCountsSUS(selection2, new PopFitVectorDouble(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorInteger(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorDoubleSimple(16), x -> x*x);
		validateExpectedCountsSUS(selection2, new PopFitVectorIntegerSimple(16), x -> x*x);
	}
	
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
	
	@Test
	public void testSortedIndexes() {
		class TestSortedIndexesSelectionOp extends AbstractWeightedSelection {
			@Override public void selectAll(double[] normalizedWeights, int[] selected) {
				// doesn't matter.... not used in test
			}
			@Override public TestSortedIndexesSelectionOp split() {
				return this;
			}
		}
		TestSortedIndexesSelectionOp selection = new TestSortedIndexesSelectionOp();
		for (int n = 1; n <= 8; n *= 2) {
			int[] expected = new int[n];
			for (int i = 0; i < n; i++) {
				expected[i] = i;
			}
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			int[] indexes = selection.sortedIndexes(f1);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			indexes = selection.sortedIndexes(f2);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorDoubleSimple f3 = new PopFitVectorDoubleSimple(n);
			indexes = selection.sortedIndexes(f3);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorIntegerSimple f4 = new PopFitVectorIntegerSimple(n);
			indexes = selection.sortedIndexes(f4);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			
			for (int i = 0; i < n; i++) {
				expected[n-1-i] = i;
			}
			f1.reverse();
			f2.reverse();
			f3.reverse();
			f4.reverse();
			indexes = selection.sortedIndexes(f1);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f2);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f3);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f4);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
		}
	}
	
	@Test
	public void testComputeWeightRunningSumRanks() {
		class TestSelectionOp extends AbstractWeightedSelection {
			@Override public void selectAll(double[] normalizedWeights, int[] selected) {
				// doesn't matter.... not used in test
			}
			@Override public TestSelectionOp split() {
				return this;
			}
		}
		TestSelectionOp selection = new TestSelectionOp();
		for (int n = 1; n <= 8; n *= 2) {
			int[] indexes = new int[n];
			int[] reversed = new int[n];
			for (int i = 0; i < n; i++) {
				indexes[i] = i;
				reversed[n-1-i] = i;
			}
			double[] weightsRunningSum = selection.computeWeightRunningSumRanks(indexes, x -> x + 1);
			for (int i = 0; i < n; i++) {
				double expected = (i+1)*(i+2)/2;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(reversed, x -> x + 1);
			for (int i = 0; i < n; i++) {
				double expected = n*(n+1)/2 - (n-i-1)*(n-i)/2;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(indexes, x -> (x + 1)*(x + 1));
			for (int i = 0; i < n; i++) {
				int j = i+1;
				double expected = j*(j+1)*(2*j+1)/6;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(reversed, x -> (x + 1)*(x + 1));
			for (int i = 0; i < n; i++) {
				int j = n-1-i;
				double expected = n*(n+1)*(2*n+1)/6 - j*(j+1)*(2*j+1)/6;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
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
	
	private void validateExpectedCountsSUS(StochasticUniversalSampling selection, PopulationFitnessVector.Double pf, FitnessBiasFunction bias) {
		int[] selected = new int[pf.size()];
		selection.select(pf, selected);
		int[] expectedMin = new int[pf.size()];
		int[] expectedMax = new int[pf.size()];
		int[] counts = new int[pf.size()];
		double totalFitness = 0;
		for (int i = 0; i < pf.size(); i++) {
			totalFitness = totalFitness + bias.bias(pf.getFitness(i));
			counts[selected[i]]++;
		}
		for (int i = 0; i < pf.size(); i++) {
			expectedMin[i] = (int)(pf.size() * bias.bias(pf.getFitness(i)) / totalFitness);
			expectedMax[i] = (int)Math.ceil(pf.size() * bias.bias(pf.getFitness(i)) / totalFitness);
			assertTrue(counts[i] >= expectedMin[i], "i:"+i+" count:"+counts[i]+" min:"+expectedMin[i]);
			assertTrue(counts[i] <= expectedMax[i], "i:"+i+" count:"+counts[i]+" max:"+expectedMax[i]);
		}
	}
	
	private void validateExpectedCountsSUSWithRanks(StochasticUniversalSampling selection, PopulationFitnessVector.Integer pf) {
		int[] selected = new int[pf.size()];
		selection.select(pf, selected);
		int[] expectedMin = new int[pf.size()];
		int[] expectedMax = new int[pf.size()];
		int[] counts = new int[pf.size()];
		for (int i = 0; i < pf.size(); i++) {
			counts[selected[i]]++;
		}
		double[] runningSum = selection.computeWeightRunningSum(pf);
		expectedMin[0] = (int)(pf.size() * runningSum[0] / runningSum[runningSum.length-1]);
		expectedMax[0] = (int)Math.ceil(pf.size() * runningSum[0] / runningSum[runningSum.length-1]);
		for (int i = 1; i < pf.size(); i++) {
			expectedMin[i] = (int)(pf.size() * (runningSum[i]-runningSum[i-1]) / runningSum[runningSum.length-1]);
			expectedMax[i] = (int)Math.ceil(pf.size() * (runningSum[i]-runningSum[i-1]) / runningSum[runningSum.length-1]);
		}
		for (int i = 0; i < pf.size(); i++) {
			assertTrue(counts[i] >= expectedMin[i], "i:"+i+" count:"+counts[i]+" min:"+expectedMin[i]);
			assertTrue(counts[i] <= expectedMax[i], "i:"+i+" count:"+counts[i]+" max:"+expectedMax[i]);
		}
	}
	
	private void validateExpectedCountsSUSWithRanks(StochasticUniversalSampling selection, PopulationFitnessVector.Double pf) {
		int[] selected = new int[pf.size()];
		selection.select(pf, selected);
		int[] expectedMin = new int[pf.size()];
		int[] expectedMax = new int[pf.size()];
		int[] counts = new int[pf.size()];
		for (int i = 0; i < pf.size(); i++) {
			counts[selected[i]]++;
		}
		double[] runningSum = selection.computeWeightRunningSum(pf);
		expectedMin[0] = (int)(pf.size() * runningSum[0] / runningSum[runningSum.length-1]);
		expectedMax[0] = (int)Math.ceil(pf.size() * runningSum[0] / runningSum[runningSum.length-1]);
		for (int i = 1; i < pf.size(); i++) {
			expectedMin[i] = (int)(pf.size() * (runningSum[i]-runningSum[i-1]) / runningSum[runningSum.length-1]);
			expectedMax[i] = (int)Math.ceil(pf.size() * (runningSum[i]-runningSum[i-1]) / runningSum[runningSum.length-1]);
		}
		for (int i = 0; i < pf.size(); i++) {
			assertTrue(counts[i] >= expectedMin[i], "i:"+i+" count:"+counts[i]+" min:"+expectedMin[i]);
			assertTrue(counts[i] <= expectedMax[i], "i:"+i+" count:"+counts[i]+" max:"+expectedMax[i]);
		}
	}
	
	private void validateExpectedCountsSUS(StochasticUniversalSampling selection, PopulationFitnessVector.Integer pf, FitnessBiasFunction bias) {
		int[] selected = new int[pf.size()];
		selection.select(pf, selected);
		int[] expectedMin = new int[pf.size()];
		int[] expectedMax = new int[pf.size()];
		int[] counts = new int[pf.size()];
		double totalFitness = 0;
		for (int i = 0; i < pf.size(); i++) {
			totalFitness = totalFitness + bias.bias(pf.getFitness(i));
			counts[selected[i]]++;
		}
		for (int i = 0; i < pf.size(); i++) {
			expectedMin[i] = (int)(pf.size() * bias.bias(pf.getFitness(i)) / totalFitness);
			expectedMax[i] = (int)Math.ceil(pf.size() * bias.bias(pf.getFitness(i)) / totalFitness);
			assertTrue(counts[i] >= expectedMin[i], "i:"+i+" count:"+counts[i]+" min:"+expectedMin[i]);
			assertTrue(counts[i] <= expectedMax[i], "i:"+i+" count:"+counts[i]+" max:"+expectedMax[i]);
		}
	}
	
	private void validateComputeRunningSum(AbstractWeightedSelection selection) {
		double[] weights = selection.computeWeightRunningSum(new PopFitVectorDoubleSimple(5));
		double[] expected = {1, 3, 6, 10, 15};
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
		
		weights = selection.computeWeightRunningSum(new PopFitVectorIntegerSimple(5));
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
	}
	
	private void validateComputeRunningSumShifted(AbstractWeightedSelection selection) {
		int[][] cases = {
			{1, 2, 3, 4, 5},
			{5, 4, 3, 2, 1}, 
			{11, 12, 13, 14, 15}, 
			{15, 14, 13, 12, 11},
			{-15, -14, -13, -12, -11},
			{-11, -12, -13, -14, -15}, 
			{-2, -1, 0, 1, 2}, 
			{2, 1, 0, -1, -2} 
		};
		double[][] expected = {
			{1, 3, 6, 10, 15},
			{5, 9, 12, 14, 15}
		};
		
		for (int c = 0; c < cases.length; c++) {
			PopFitVectorDoubleSimple pop1 = new PopFitVectorDoubleSimple(cases[c]);
			double[] weights = selection.computeWeightRunningSum(pop1);
			assertEquals(5, weights.length);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(expected[c%2][i], weights[i], 1E-10);
			}
			
			PopFitVectorIntegerSimple pop2 = new PopFitVectorIntegerSimple(cases[c]);
			weights = selection.computeWeightRunningSum(pop2);
			assertEquals(5, weights.length);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(expected[c%2][i], weights[i], 1E-10);
			}
		}
	}
	
	private void validateBiasedComputeRunningSum(AbstractWeightedSelection selection) {
		double[] weights = selection.computeWeightRunningSum(new PopFitVectorDoubleSimple(5));
		double[] expected = {1, 5, 14, 30, 55};
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
		
		weights = selection.computeWeightRunningSum(new PopFitVectorIntegerSimple(5));
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
	}
	
	private void validateBiasedComputeRunningSumShifted(AbstractWeightedSelection selection) {
		int[][] cases = {
			{1, 2, 3, 4, 5},
			{5, 4, 3, 2, 1}, 
			{11, 12, 13, 14, 15}, 
			{15, 14, 13, 12, 11},
			{-15, -14, -13, -12, -11},
			{-11, -12, -13, -14, -15}, 
			{-2, -1, 0, 1, 2}, 
			{2, 1, 0, -1, -2} 
		};
		double[][] expected = {
			{1, 5, 14, 30, 55},
			{25, 41, 50, 54, 55}
		};
		for (int c = 0; c < cases.length; c++) {
			double[] weights = selection.computeWeightRunningSum(new PopFitVectorDoubleSimple(cases[c]));
			assertEquals(5, weights.length);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(expected[c%2][i], weights[i], 1E-10);
			}
			
			weights = selection.computeWeightRunningSum(new PopFitVectorIntegerSimple(cases[c]));
			assertEquals(5, weights.length);
			for (int i = 0; i < weights.length; i++) {
				assertEquals(expected[c%2][i], weights[i], 1E-10);
			}
		}
	}
	
	private void validateHigherFitnessSelectedMoreOften_Double(SelectionOperator selection) {
		validateHigherFitnessSelectedMoreOften_Double(selection, 20);
	}
	
	private void validateHigherFitnessSelectedMoreOften_Double(SelectionOperator selection, int selectSize) {
		// This part of the test attempts to confirm that greater weight is placed on
		// higher fitness population members. A sporadic failure is not necessarily a 
		// real failure, but it should fail with low probability.
		PopFitVectorDouble pf_d = new PopFitVectorDouble(10);
		int[] selected = new int[selectSize];
		selection.select(pf_d, selected);
		int countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] >= pf_d.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
		
		pf_d.reverse();
		selected = new int[selectSize];
		selection.select(pf_d, selected);
		countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] < pf_d.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
	}
	
	private void validateHigherFitnessSelectedMoreOften_Integer(SelectionOperator selection) {
		validateHigherFitnessSelectedMoreOften_Integer(selection, 20);
	}
	
	private void validateHigherFitnessSelectedMoreOften_Integer(SelectionOperator selection, int selectSize) {
		// This part of the test attempts to confirm that greater weight is placed on
		// higher fitness population members. A sporadic failure is not necessarily a 
		// real failure, but it should fail with low probability.
		PopFitVectorInteger pf_int = new PopFitVectorInteger(10);
		int[] selected = new int[selectSize];
		selection.select(pf_int, selected);
		int countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] >= pf_int.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
		
		pf_int.reverse();
		selected = new int[selectSize];
		selection.select(pf_int, selected);
		countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] < pf_int.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
	}
	
	private void validateIndexes_Double(SelectionOperator selection) {
		validateIndexes_Double(selection, true);
	}
	
	private void validateIndexes_Double(SelectionOperator selection, boolean checkDifferent) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorDouble pf = new PopFitVectorDouble(s);
			int[] count = new int[s];
			for (int i = Math.max(1, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (checkDifferent && s >= 8) {
				int numDifferentSelected = 0;
				for (int i = 0; i < s; i++) {
					if (count[i] > 0) {
						numDifferentSelected++;
					}
				}
				assertTrue(numDifferentSelected > 1);
			}
		}
	}
	
	private void validateIndexes_Integer(SelectionOperator selection) {
		validateIndexes_Integer(selection, true);
	}
	
	private void validateIndexes_Integer(SelectionOperator selection, boolean checkDifferent) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorInteger pf = new PopFitVectorInteger(s);
			int[] count = new int[s];
			for (int i = Math.max(1, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (checkDifferent && s >= 8) {
				int numDifferentSelected = 0;
				for (int i = 0; i < s; i++) {
					if (count[i] > 0) {
						numDifferentSelected++;
					}
				}
				assertTrue(numDifferentSelected > 1);
			}
		}
	}
	
	private static class PopFitVectorDouble implements PopulationFitnessVector.Double {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorDouble(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 << i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public double getFitness(int i) {
			return fitnesses[i];
		}
		
		public void reverse() {
			int half = fitnesses.length / 2;
			for (int i = 0; i < half; i++) {
				int temp = fitnesses[i];
				fitnesses[i] = fitnesses[fitnesses.length-1-i];
				fitnesses[fitnesses.length-1-i] = temp;
			}
		}
	}
	
	private static class PopFitVectorInteger implements PopulationFitnessVector.Integer {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorInteger(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 << i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public int getFitness(int i) {
			return fitnesses[i];
		}
		
		public void reverse() {
			int half = fitnesses.length / 2;
			for (int i = 0; i < half; i++) {
				int temp = fitnesses[i];
				fitnesses[i] = fitnesses[fitnesses.length-1-i];
				fitnesses[fitnesses.length-1-i] = temp;
			}
		}
	}
	
	private static class PopFitVectorDoubleSimple implements PopulationFitnessVector.Double {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorDoubleSimple(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 + i;
			}
		}
		
		public PopFitVectorDoubleSimple(int[] fitnesses) {
			this.fitnesses = fitnesses.clone();
			s = fitnesses.length;
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public double getFitness(int i) {
			return fitnesses[i];
		}
		
		public void reverse() {
			int half = fitnesses.length / 2;
			for (int i = 0; i < half; i++) {
				int temp = fitnesses[i];
				fitnesses[i] = fitnesses[fitnesses.length-1-i];
				fitnesses[fitnesses.length-1-i] = temp;
			}
		}
	}
	
	private static class PopFitVectorIntegerSimple implements PopulationFitnessVector.Integer {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorIntegerSimple(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 + i;
			}
		}
		
		public PopFitVectorIntegerSimple(int[] fitnesses) {
			this.fitnesses = fitnesses.clone();
			s = fitnesses.length;
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public int getFitness(int i) {
			return fitnesses[i];
		}
		
		public void reverse() {
			int half = fitnesses.length / 2;
			for (int i = 0; i < half; i++) {
				int temp = fitnesses[i];
				fitnesses[i] = fitnesses[fitnesses.length-1-i];
				fitnesses[fitnesses.length-1-i] = temp;
			}
		}
	}
}
