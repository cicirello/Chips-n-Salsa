/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.operators.reals.GaussianMutation;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.util.Copyable;

/**
 * This is a package-access support class for evolutionary algorithms with evolvable control
 * parameters.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class EncodingWithParameters<T extends Copyable<T>>
    implements Copyable<EncodingWithParameters<T>> {

  private final T candidate;
  private final SingleReal[] params;
  private final GaussianMutation<SingleReal> mutator;
  private static final GaussianMutation<GaussianMutation> mutationMutator =
      GaussianMutation.createGaussianMutation(0.01, 0.01, 0.2);

  EncodingWithParameters(T candidate, int numParams, EnhancedSplittableGenerator generator) {
    this(candidate, numParams, 0.1, 1.0, generator);
  }

  EncodingWithParameters(
      T candidate,
      int numParams,
      double minRate,
      double maxRate,
      EnhancedSplittableGenerator generator) {
    this.candidate = candidate;
    params = new SingleReal[numParams];
    for (int i = 0; i < numParams; i++) {
      params[i] = new SingleReal(generator.nextDouble(minRate, maxRate));
    }
    mutator =
        GaussianMutation.createGaussianMutation(generator.nextDouble(0.05, 0.15), minRate, maxRate);
  }

  private EncodingWithParameters(EncodingWithParameters<T> other) {
    candidate = other.candidate.copy();
    params = new SingleReal[other.params.length];
    for (int i = 0; i < params.length; i++) {
      params[i] = other.params[i].copy();
    }
    mutator = other.mutator.copy();
  }

  /** Mutates the parameters. */
  public final void mutate() {
    for (SingleReal p : params) {
      mutator.mutate(p);
    }
    mutationMutator.mutate(mutator);
  }

  /**
   * Gets the candidate solution.
   *
   * @return the candidate solution.
   */
  public final T getCandidate() {
    return candidate;
  }

  /**
   * Gets the vector of parameters.
   *
   * @param i Index of parameter to get
   * @return the vector of parameters
   */
  public final SingleReal getParameter(int i) {
    return params[i];
  }

  /**
   * Gets number of parameters.
   *
   * @return number of parameters
   */
  public final int length() {
    return params.length;
  }

  @Override
  public EncodingWithParameters<T> copy() {
    return new EncodingWithParameters<T>(this);
  }

  @Override
  public int hashCode() {
    // hashCode and equals need to be strictly for whether the encapsulated
    // candidates are equal to function properly with the elite sets, and some
    // other stuff within the package.
    return candidate.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    // hashCode and equals need to be strictly for whether the encapsulated
    // candidates are equal to function properly with the elite sets, and some
    // other stuff within the package.
    if (other instanceof EncodingWithParameters) {
      EncodingWithParameters casted = (EncodingWithParameters) other;
      return candidate.equals(casted.candidate);
    }
    return false;
  }
}
