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

package org.cicirello.search.internal;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.evo.FitnessFunction;
import org.cicirello.search.evo.SelectionOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.sa.AnnealingSchedule;
import org.cicirello.search.ss.ConstructiveHeuristic;
import org.cicirello.util.Copyable;

/**
 * Internal library class for validating references are non-null.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ReferenceValidator {

  /** Utility class: private constructor to prevent instantiation. */
  private ReferenceValidator() {}

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static void nullCheck(AnnealingSchedule reference) {
    if (reference == null) {
      throw new NullPointerException("The AnnealingSchedule must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(ConstructiveHeuristic<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The ConstructiveHeuristic must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(CrossoverOperator<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The CrossoverOperator must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(FitnessFunction.Double<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The FitnessFunction.Double must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(FitnessFunction.Integer<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The FitnessFunction.Integer must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(Initializer<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The Initializer must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(MutationOperator<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The MutationOperator must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(Problem<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The Problem must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static <T extends Copyable<T>> void nullCheck(ProgressTracker<T> reference) {
    if (reference == null) {
      throw new NullPointerException("The ProgressTracker must be non-null.");
    }
  }

  /**
   * Checks for null reference.
   *
   * @param reference the object reference to check
   * @throws NullPointerException if reference is null
   */
  public static void nullCheck(SelectionOperator reference) {
    if (reference == null) {
      throw new NullPointerException("The SelectionOperator must be non-null.");
    }
  }
}
