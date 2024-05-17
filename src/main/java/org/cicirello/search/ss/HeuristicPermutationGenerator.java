/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.internal.ReferenceValidator;

/**
 * This class generates solutions to permutation optimization problems using a constructive
 * heuristic. Unless the heuristic given to it is randomized, this class is completely deterministic
 * and has no randomized behavior. Thus, executing the {@link #optimize} method multiple times
 * should produce the same result each time. When using a constructive heuristic, you begin with an
 * empty solution, in this case an empty permutation, and you then use a constructive heuristic to
 * choose which element to add to the partial solution, in this case to the partial permutation.
 * This is repeated until you derive a complete solution (i.e., a complete permutation).
 *
 * <p>Constructive heuristics are not just for permutations. See the {@link
 * HeuristicSolutionGenerator} class for a more general implementation.
 *
 * <p>Assuming that the length of the permutation is N, and that the runtime of the heuristic is
 * O(f(N)), the runtime to construct one permutation using a constructive heuristic is
 * O(N<sup>2</sup> f(N)). If the cost, f(N), to heuristically evaluate one permutation element is
 * simply, O(1), constant time, then the cost to heuristically construct a permutation is simply
 * O(N<sup>2</sup>).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class HeuristicPermutationGenerator extends HeuristicSolutionGenerator<Permutation> {

  /**
   * private constructor: use factory methods instead. Constructs an HeuristicPermutationGenerator
   * for generating solutions to an optimization problem using a constructive heuristic.
   *
   * @param heuristic The constructive heuristic.
   * @param tracker A ProgressTracker
   * @throws NullPointerException if heuristic or tracker is null
   */
  private HeuristicPermutationGenerator(
      ConstructiveHeuristic<Permutation> heuristic, ProgressTracker<Permutation> tracker) {
    super(heuristic, tracker);
  }

  /**
   * Creates a HeuristicPermutationGenerator for generating solutions to an optimization problem
   * using a constructive heuristic. A ProgressTracker is created for you.
   *
   * @param heuristic The constructive heuristic.
   * @return the HeuristicPermutationGenerator
   * @throws NullPointerException if heuristic is null
   */
  public static HeuristicPermutationGenerator createHeuristicPermutationGenerator(
      ConstructiveHeuristic<Permutation> heuristic) {
    ReferenceValidator.nullCheck(heuristic);
    return new HeuristicPermutationGenerator(heuristic, new ProgressTracker<Permutation>());
  }

  /**
   * Creates a HeuristicPermutationGenerator for generating solutions to an optimization problem
   * using a constructive heuristic.
   *
   * @param heuristic The constructive heuristic.
   * @param tracker A ProgressTracker
   * @return the HeuristicPermutationGenerator
   * @throws NullPointerException if heuristic or tracker is null
   */
  public static HeuristicPermutationGenerator createHeuristicPermutationGenerator(
      ConstructiveHeuristic<Permutation> heuristic, ProgressTracker<Permutation> tracker) {
    ReferenceValidator.nullCheck(heuristic);
    ReferenceValidator.nullCheck(tracker);
    return new HeuristicPermutationGenerator(heuristic, tracker);
  }

  /*
   * private for use by split method
   */
  private HeuristicPermutationGenerator(HeuristicPermutationGenerator other) {
    super(other);
  }

  @Override
  public HeuristicPermutationGenerator split() {
    return new HeuristicPermutationGenerator(this);
  }
}
