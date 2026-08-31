/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiFunction;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.bits.SinglePointCrossover;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.representations.BitVector;
import org.junit.jupiter.api.*;

public class GAWithBNPReplacementTests {

    private static final int NUM_GENERATIONS = 1;
    private BiFunction<BitVector, BitVector, Double> xorDistanceFunction;

    @BeforeEach
    public void setup() {
        xorDistanceFunction =
                (i1, i2) -> {
                    int distance = 0;
                    for (int i = 0; i < i1.length(); i++) {
                        if (i1.getBit(i) != i2.getBit(i)) {
                            distance++;
                        }
                    }
                    return Double.valueOf(distance);
                };
    }

    @Test
    public void testInvalidNumGenerations() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<>();

        // invalid negative number of generations in replacement
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    var replacement = new BNPReplacement<BitVector>(-1, xorDistanceFunction);
                });

        // replacement is executed more times than expected
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
                    var ga =
                            new GeneticAlgorithm(
                                    n,
                                    new AllZerosInitializer(L),
                                    new InverseCostFitnessFunction<>(problem),
                                    M,
                                    new SinglePointCrossover(),
                                    0.0,
                                    new FitnessShifter(new FitnessProportionalSelection()),
                                    replacement,
                                    tracker);
                    ga.optimize(NUM_GENERATIONS + 1);
                });
    }

    // With Initializer and ProgressTracker
    @Test
    public void testDoubleFitnessM0() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(L * 1.0, solution.getCostDouble(), 1E-10);
        assertEquals(L * 1.0, tracker.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allZeros());
        assertTrue(tracker.getSolution().allZeros());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));

        GeneticAlgorithm ga2 = ga.split();
        assertTrue(ga.getProgressTracker() == ga2.getProgressTracker());
        assertTrue(ga.getProblem() == ga2.getProblem());
        assertTrue(ga != ga2);
    }

    @Test
    public void testDoubleFitnessM1() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 1.0 - Math.ulp(1.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(0.0, solution.getCostDouble(), 1E-10);
        assertEquals(0.0, tracker.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allOnes());
        assertTrue(tracker.getSolution().allOnes());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        1.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        null,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        (FitnessFunction.Double<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        (ProgressTracker<BitVector>) null));
    }

    @Test
    public void testIntegerFitnessM0() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(L, solution.getCost());
        assertEquals(L, tracker.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allZeros());
        assertTrue(tracker.getSolution().allZeros());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));

        GeneticAlgorithm ga2 = ga.split();
        assertTrue(ga.getProgressTracker() == ga2.getProgressTracker());
        assertTrue(ga.getProblem() == ga2.getProblem());
        assertTrue(ga != ga2);
    }

    @Test
    public void testIntegerFitnessM1() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 1.0 - Math.ulp(1.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(0, solution.getCost());
        assertEquals(0, tracker.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allOnes());
        assertTrue(tracker.getSolution().allOnes());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        1.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        null,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        (FitnessFunction.Integer<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        (ProgressTracker<BitVector>) null));
    }

    // With Initializer but no ProgressTracker
    @Test
    public void testDoubleFitnessNoTrackerM0() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(L * 1.0, solution.getCostDouble(), 1E-10);
        assertEquals(L * 1.0, tracker.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allZeros());
        assertTrue(tracker.getSolution().allZeros());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
    }

    @Test
    public void testDoubleFitnessNoTrackerM1() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 1.0 - Math.ulp(1.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(0.0, solution.getCostDouble(), 1E-10);
        assertEquals(0.0, tracker.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allOnes());
        assertTrue(tracker.getSolution().allOnes());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        1.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        null,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        (FitnessFunction.Double<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement));
    }

    @Test
    public void testIntegerFitnessNoTrackerM0() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(L, solution.getCost());
        assertEquals(L, tracker.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allZeros());
        assertTrue(tracker.getSolution().allZeros());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
    }

    @Test
    public void testIntegerFitnessNoTrackerM1() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 1.0 - Math.ulp(1.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        new AllZerosInitializer(L),
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertEquals(0, solution.getCost());
        assertEquals(0, tracker.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
        assertTrue(b.allOnes());
        assertTrue(tracker.getSolution().allOnes());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        1.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        null,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        (FitnessFunction.Integer<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        new AllZerosInitializer(L),
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement));
    }

    // With bitLength and ProgressTracker
    @Test
    public void testDoubleFitnessBitLength() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 0.5;
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
        assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
        assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        (FitnessFunction.Double<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        (ProgressTracker<BitVector>) null));
    }

    @Test
    public void testIntegerFitnessBitLength() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 0.5;
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCost() && solution.getCost() > 0);
        assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
        assertEquals(tracker.getCost(), solution.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        (FitnessFunction.Integer<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        tracker));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement,
                                        (ProgressTracker<BitVector>) null));
    }

    // With bitLength but no ProgressTracker
    @Test
    public void testDoubleFitnessBitLengthNoTracker() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 0.5;
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
        assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
        assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        (FitnessFunction.Double<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new InverseCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
    }

    @Test
    public void testIntegerFitnessBitLengthNoTracker() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = 0.5;
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.0,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCost() && solution.getCost() > 0);
        assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
        assertEquals(tracker.getCost(), solution.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());

        IllegalArgumentException thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        0.0,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownIllegal =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                new GeneticAlgorithm(
                                        0,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        NullPointerException thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        (FitnessFunction.Integer<BitVector>) null,
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        null,
                                        0.0,
                                        new FitnessShifter(new FitnessProportionalSelection()),
                                        replacement));
        thrownNull =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                new GeneticAlgorithm(
                                        n,
                                        L,
                                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                                        M,
                                        new SinglePointCrossover(),
                                        0.0,
                                        null,
                                        replacement));
    }

    // C=0.5
    @Test
    public void testDoubleFitnessC05() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.5,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
        assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
        assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
    }

    @Test
    public void testIntegerFitnessC05() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.5,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement,
                        tracker);
        assertTrue(tracker == ga.getProgressTracker());
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCost() && solution.getCost() > 0);
        assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
        assertEquals(tracker.getCost(), solution.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
    }

    @Test
    public void testDoubleFitnessC05NoTracker() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new InverseCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.5,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
        assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
        assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
    }

    @Test
    public void testIntegerFitnessC05NoTracker() {
        int n = 10;
        int L = 32;
        OneMax problem = new OneMax();
        double M = Math.ulp(0.0);
        var replacement = new BNPReplacement<BitVector>(NUM_GENERATIONS, xorDistanceFunction);
        GeneticAlgorithm ga =
                new GeneticAlgorithm(
                        n,
                        L,
                        new NegativeIntegerCostFitnessFunction<BitVector>(problem),
                        M,
                        new SinglePointCrossover(),
                        0.5,
                        new FitnessShifter(new FitnessProportionalSelection()),
                        replacement);
        ProgressTracker<BitVector> tracker = ga.getProgressTracker();
        assertTrue(problem == ga.getProblem());
        SolutionCostPair<BitVector> solution = ga.optimize(NUM_GENERATIONS);
        assertTrue(L > solution.getCost() && solution.getCost() > 0);
        assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
        assertEquals(tracker.getCost(), solution.getCost());
        BitVector b = solution.getSolution();
        assertEquals(L, b.length());
        assertEquals(L, tracker.getSolution().length());
    }

    private static class AllZerosInitializer implements Initializer<BitVector> {

        private int L;

        public AllZerosInitializer(int L) {
            this.L = L;
        }

        @Override
        public BitVector createCandidateSolution() {
            return new BitVector(L);
        }

        @Override
        public AllZerosInitializer split() {
            return new AllZerosInitializer(L);
        }
    }
}
