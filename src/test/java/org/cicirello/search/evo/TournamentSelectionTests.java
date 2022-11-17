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

/** JUnit test cases for TournamentSelection. */
public class TournamentSelectionTests extends SharedTestSelectionOperators {

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

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new TournamentSelection(1));
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
}
