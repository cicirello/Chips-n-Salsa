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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/*
 * Shared by the test classes for the various selection operators.
 */
public class SharedTestSelectionOperators {

  void validateIndexes_Double(SelectionOperator selection) {
    validateIndexes_Double(selection, true);
  }

  void validateIndexes_Double(SelectionOperator selection, boolean checkDifferent) {
    selection.init(17);
    for (int s = 1; s <= 8; s *= 2) {
      PopFitVectorDouble pf = new PopFitVectorDouble(s);
      int[] count = new int[s];
      for (int i = Math.max(1, s - 2); i <= s + 2; i++) {
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

  void validateIndexes_Integer(SelectionOperator selection) {
    validateIndexes_Integer(selection, true);
  }

  void validateIndexes_Integer(SelectionOperator selection, boolean checkDifferent) {
    selection.init(17);
    for (int s = 1; s <= 8; s *= 2) {
      PopFitVectorInteger pf = new PopFitVectorInteger(s);
      int[] count = new int[s];
      for (int i = Math.max(1, s - 2); i <= s + 2; i++) {
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

  void validateHigherFitnessSelectedMoreOften_Double(SelectionOperator selection) {
    validateHigherFitnessSelectedMoreOften_Double(selection, 20);
  }

  void validateHigherFitnessSelectedMoreOften_Double(SelectionOperator selection, int selectSize) {
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
    assertTrue(countLarger > selected.length / 2);

    pf_d.reverse();
    selected = new int[selectSize];
    selection.select(pf_d, selected);
    countLarger = 0;
    for (int i = 0; i < selected.length; i++) {
      if (selected[i] < pf_d.size() / 2) countLarger++;
    }
    assertTrue(countLarger > selected.length / 2);
  }

  void validateHigherFitnessSelectedMoreOften_Integer(SelectionOperator selection) {
    validateHigherFitnessSelectedMoreOften_Integer(selection, 20);
  }

  void validateHigherFitnessSelectedMoreOften_Integer(SelectionOperator selection, int selectSize) {
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
    assertTrue(countLarger > selected.length / 2);

    pf_int.reverse();
    selected = new int[selectSize];
    selection.select(pf_int, selected);
    countLarger = 0;
    for (int i = 0; i < selected.length; i++) {
      if (selected[i] < pf_int.size() / 2) countLarger++;
    }
    assertTrue(countLarger > selected.length / 2);
  }

  static class PopFitVectorDouble implements PopulationFitnessVector.Double {

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
    public int size() {
      return s;
    }

    @Override
    public double getFitness(int i) {
      return fitnesses[i];
    }

    public void reverse() {
      int half = fitnesses.length / 2;
      for (int i = 0; i < half; i++) {
        int temp = fitnesses[i];
        fitnesses[i] = fitnesses[fitnesses.length - 1 - i];
        fitnesses[fitnesses.length - 1 - i] = temp;
      }
    }
  }

  static class PopFitVectorInteger implements PopulationFitnessVector.Integer {

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
    public int size() {
      return s;
    }

    @Override
    public int getFitness(int i) {
      return fitnesses[i];
    }

    public void reverse() {
      int half = fitnesses.length / 2;
      for (int i = 0; i < half; i++) {
        int temp = fitnesses[i];
        fitnesses[i] = fitnesses[fitnesses.length - 1 - i];
        fitnesses[fitnesses.length - 1 - i] = temp;
      }
    }
  }

  static class PopFitVectorDoubleSimple implements PopulationFitnessVector.Double {

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
    public int size() {
      return s;
    }

    @Override
    public double getFitness(int i) {
      return fitnesses[i];
    }

    public void reverse() {
      int half = fitnesses.length / 2;
      for (int i = 0; i < half; i++) {
        int temp = fitnesses[i];
        fitnesses[i] = fitnesses[fitnesses.length - 1 - i];
        fitnesses[fitnesses.length - 1 - i] = temp;
      }
    }
  }

  static class PopFitVectorIntegerSimple implements PopulationFitnessVector.Integer {

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
    public int size() {
      return s;
    }

    @Override
    public int getFitness(int i) {
      return fitnesses[i];
    }

    public void reverse() {
      int half = fitnesses.length / 2;
      for (int i = 0; i < half; i++) {
        int temp = fitnesses[i];
        fitnesses[i] = fitnesses[fitnesses.length - 1 - i];
        fitnesses[fitnesses.length - 1 - i] = temp;
      }
    }
  }
}
