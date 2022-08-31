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
 * JUnit test cases for Boltzmann selection and variations.
 */
public class BoltzmannSelectionTests {
	
	@Test
	public void testConstantBoltzmannBiasFunction() {
		ConstantBoltzmannBiasFunction bias = new ConstantBoltzmannBiasFunction(1.0);
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f), bias.bias(f));
			bias.update();
		}
		bias.init();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f), bias.bias(f));
			bias.update();
		}
		
		bias = new ConstantBoltzmannBiasFunction(0.5);
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(2*f), bias.bias(f));
			bias.update();
		}
		bias.init();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(2*f), bias.bias(f));
			bias.update();
		}
		
		bias = new ConstantBoltzmannBiasFunction(2.0);
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.5*f), bias.bias(f));
			bias.update();
		}
		bias.init();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.5*f), bias.bias(f));
			bias.update();
		}
		
		assertSame(bias, bias.split());
	}
	
	@Test
	public void testLinearCoolingSchedule() {
		LinearCoolingBiasFunction bias = new LinearCoolingBiasFunction(4.0, 1.0, 0.1);
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.25*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f/3.0), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.5*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f), bias.bias(f));
		}
		LinearCoolingBiasFunction biasSplit = bias.split();
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), bias.bias(f));
		}
		
		// Use split copy
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.25*f), biasSplit.bias(f));
		}
		biasSplit.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f/3.0), biasSplit.bias(f));
		}
		biasSplit.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.5*f), biasSplit.bias(f));
		}
		biasSplit.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f), biasSplit.bias(f));
		}
		biasSplit.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), biasSplit.bias(f));
		}
		biasSplit.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), biasSplit.bias(f));
		}
		
		// reinitialize and use original
		bias.init();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.25*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f/3.0), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(0.5*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(10*f), bias.bias(f));
		}
	}
	
	@Test
	public void testExponentialCoolingSchedule() {
		ExponentialCoolingBiasFunction bias = new ExponentialCoolingBiasFunction(1.0, 0.5, 0.1);
		double mult = 1.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		ExponentialCoolingBiasFunction biasSplit = bias.split();
		bias.update();
		mult = 10;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		
		// Use split copy
		mult = 1.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		biasSplit.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		biasSplit.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		biasSplit.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		biasSplit.update();
		mult = 10;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		biasSplit.update();
		mult = 10;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), biasSplit.bias(f));
		}
		
		// reinitialize and use original
		bias.init();
		mult = 1.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult *= 2.0;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult = 10;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
		bias.update();
		mult = 10;
		for (double f = 0.0; f <= 5.1; f += 1.0) {
			assertEquals(Math.exp(mult*f), bias.bias(f));
		}
	}
	
	@Test
	public void testBoltzmannSelectionExponential() {
		double[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(1.0, 0.1, 0.5, false);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 0.0;
		double div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		BoltzmannSelection split = selection.split();
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// Use split copy
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// reinitialize and use original
		selection.init(500);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(10.0, 0.0, 1.0, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(0.09, 0.1, 1.0, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(5.0, 0.1, 0.0, false)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(5.0, 0.1, 1.0, false)
		);
	}
	
	@Test
	public void testBoltzmannSelectionExponentialInteger() {
		int[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(1.0, 0.1, 0.5, false);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 0.0;
		double div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		BoltzmannSelection split = selection.split();
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// Use split copy
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// reinitialize and use original
		selection.init(500);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div *= 0.5;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
	}
	
	@Test
	public void testBoltzmannSelectionLinear() {
		double[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(4.0, 0.1, 1.0, true);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 0.0;
		double div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		BoltzmannSelection split = selection.split();
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// Use split copy
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// reinitialize and use original
		selection.init(500);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(10.0, 0.0, 1.0, true)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(0.09, 0.1, 1.0, true)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(5.0, 0.1, 0.0, true)
		);
	}
	
	@Test
	public void testBoltzmannSelectionLinearInteger() {
		int[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(4.0, 0.1, 1.0, true);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 0.0;
		double div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		BoltzmannSelection split = selection.split();
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// Use split copy
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		
		// reinitialize and use original
		selection.init(500);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 4;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div -= 1.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		div = 0.1;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			expected += Math.exp(fitnesses[i]/div);
			assertEquals(expected, weightedSum[i]);
		}
	}
	
	@Test
	public void testBoltzmannSelectionConstant() {
		double[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(1.0);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		BoltzmannSelection split = selection.split();
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		split.init(1000);
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		
		selection = new BoltzmannSelection(0.5);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		
		selection = new BoltzmannSelection(2.0);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(0.0)
		);
	}
	
	@Test
	public void testBoltzmannSelectionConstantInteger() {
		int[] fitnesses = {0, 1, 2, 3, 4, 5};
		PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());
		
		BoltzmannSelection selection = new BoltzmannSelection(1.0);
		double[] weightedSum = selection.computeWeightRunningSum(vector);
		double expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		BoltzmannSelection split = selection.split();
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		split.init(1000);
		weightedSum = split.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i+1]);
		}
		
		selection = new BoltzmannSelection(0.5);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(2*fitnesses[i+1]);
		}
		
		selection = new BoltzmannSelection(2.0);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		selection.init(1000);
		weightedSum = selection.computeWeightRunningSum(vector);
		expected = 1;
		for (int i = 0; i < fitnesses.length; i++) {
			assertEquals(expected, weightedSum[i]);
			if (i < fitnesses.length - 1) expected += Math.exp(0.5*fitnesses[i+1]);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new BoltzmannSelection(0.0)
		);
	}
}
