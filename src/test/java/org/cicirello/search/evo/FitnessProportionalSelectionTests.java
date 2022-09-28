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
 * JUnit test cases for FitnessProportionalSelection and StochasticUniversalSampling,
 * as well as the Biased versions of these.
 */
public class FitnessProportionalSelectionTests extends SharedTestSelectionOperators {
	
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
}
