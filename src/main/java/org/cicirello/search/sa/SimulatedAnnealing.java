/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

package org.cicirello.search.sa;

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SimpleLocalMetaheuristic;
import org.cicirello.search.SingleSolutionMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.UndoableMutationOperator;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class is an implementation of the metaheuristic known as simulated annealing. Simulated
 * annealing operates via a mechanism modeled after the process of heating a metal and allowing it
 * to cool slowly. Heating enables the material to be shaped as desired, while cooling at a slow
 * rate minimizes internal stress thus enabling greater stability in the final state. Simulated
 * annealing is a form of local search inspired by this process. It is controlled by a temperature
 * parameter, T, that typically begins high, and is then typically cooled during the search (i.e., T
 * usually decreases during the search). A component of the algorithm known as the annealing
 * schedule controls how the temperature T changes during the search. Simulated annealing usually
 * begins with a random initial candidate solution to the problem. Each iteration of simulated
 * annealing then involves generating a random neighbor of the current candidate solution, and
 * deciding whether or not to accept it (if accepted, the algorithm moves to the neighbor). The
 * decision of whether or not to accept the neighbor is probabilistic. If the neighbor's cost is at
 * least as good as the current cost, then the neighbor is definitely accepted. If the neighbor's
 * cost is worse than (i.e., higher than) the current cost, then the neighbor is accepted with
 * probability, P(accept) = e<sup>(currentCost-neighborCost)/T</sup>. This is known as the Boltzmann
 * distribution. At high temperatures, there is a higher probability of accepting neighboring
 * solutions than at lower temperatures. The probability of accepting neighbors is also higher for
 * lower cost neighbors than for higher cost neighbors.
 *
 * <p>The constructors of this class enable specifying an annealing schedule via a class that
 * implements the {@link AnnealingSchedule} interface, and the library provides all of the common
 * annealing schedules, such as exponential cooling, and linear cooling, as well as a few less
 * common, such as parameter-free versions of those schedules, as well as multiple adaptive
 * annealing schedules. See the {@link AnnealingSchedule} documentation for a list of the classes
 * that implement this interface. You may also implement your own annealing schedule by implementing
 * the {@link AnnealingSchedule} interface.
 *
 * <p>You must also provide the constructors of this class with a mutation operator for generating
 * random neighbors via a class that implements the {@link UndoableMutationOperator} interface, as
 * well as an instance of a class that implements the {@link Initializer} interface to provide
 * simulated annealing with a means of generating an initial random starting solution. The library
 * provides several mutation operators for commonly optimized structures, as well as {@link
 * Initializer} objects for commonly optimized structures. You are not limited to the
 * implementations of {@link UndoableMutationOperator} and {@link Initializer} provided in the
 * library, and may implement classes that implement these interfaces as necessary for your
 * application.
 *
 * <p>This simulated annealing implementation supports an optional post-processing via a hill
 * climber. To use this feature, you must use one of the constructors that accepts a hill climber as
 * a parameter. This hill climber is then used to locally optimize the end of run solution generated
 * by simulated annealing. This hill climber must implement the {@link SimpleLocalMetaheuristic}
 * interface, such as the most commonly used hill climbers (steepest descent and first descent)
 * implemented by the {@link org.cicirello.search.hc.SteepestDescentHillClimber
 * SteepestDescentHillClimber} and {@link org.cicirello.search.hc.FirstDescentHillClimber
 * FirstDescentHillClimber} classes.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class SimulatedAnnealing<T extends Copyable<T>> implements SingleSolutionMetaheuristic<T> {

  private final SimpleLocalMetaheuristic<T> hc;
  private final IntegerCostOptimizationProblem<T> pOptInt;
  private final OptimizationProblem<T> pOpt;
  private final Initializer<T> initializer;
  private final UndoableMutationOperator<T> mutation;
  private final AnnealingSchedule anneal;
  private int elapsedEvals;
  private ProgressTracker<T> tracker;
  private final SingleRun<T> sr;

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      ProgressTracker<T> tracker) {
    this(problem, mutation, initializer, anneal, tracker, null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      ProgressTracker<T> tracker) {
    this(problem, mutation, initializer, anneal, tracker, null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems that runs a
   * hill climber as a post-processing step.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      ProgressTracker<T> tracker,
      SimpleLocalMetaheuristic<T> hc) {
    if (problem == null
        || mutation == null
        || anneal == null
        || initializer == null
        || tracker == null) {
      throw new NullPointerException();
    }
    this.initializer = initializer;
    this.mutation = mutation;
    this.anneal = anneal;
    this.tracker = tracker;
    pOpt = problem;
    pOptInt = null;
    // default on purpose: elapsedEvals = 0;
    sr = initSingleRunDouble();

    this.hc = hc;
    if (hc != null) {
      if (problem != hc.getProblem()) {
        throw new IllegalArgumentException("hc must be configured with the same problem.");
      }
      if (hc.getProgressTracker() != tracker) {
        hc.setProgressTracker(tracker);
      }
    }
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems that runs
   * a hill climber as a post-processing step.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      ProgressTracker<T> tracker,
      SimpleLocalMetaheuristic<T> hc) {
    if (problem == null
        || mutation == null
        || anneal == null
        || initializer == null
        || tracker == null) {
      throw new NullPointerException();
    }
    this.initializer = initializer;
    this.mutation = mutation;
    this.anneal = anneal;
    this.tracker = tracker;
    pOptInt = problem;
    pOpt = null;
    // default on purpose: elapsedEvals = 0;
    sr = initSingleRunInt();

    this.hc = hc;
    if (hc != null) {
      if (problem != hc.getProblem()) {
        throw new IllegalArgumentException("hc must be configured with the same problem.");
      }
      if (hc.getProgressTracker() != tracker) {
        hc.setProgressTracker(tracker);
      }
    }
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021).
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    this(problem, mutation, initializer, new SelfTuningLam(), tracker, null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021).
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker) {
    this(problem, mutation, initializer, new SelfTuningLam(), tracker, null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021), and which runs a hill climber as a post-processing step.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, new SelfTuningLam(), tracker, hc);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021), and which runs a hill climber as a post-processing step.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param tracker A ProgressTracker object, which is used to keep track of the best solution found
   *     during the run, the time when it was found, and other related data.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      ProgressTracker<T> tracker,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, new SelfTuningLam(), tracker, hc);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems. A {@link
   * ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal) {
    this(problem, mutation, initializer, anneal, new ProgressTracker<T>(), null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems. A {@link
   * ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal) {
    this(problem, mutation, initializer, anneal, new ProgressTracker<T>(), null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems that runs a
   * hill climber as a post-processing step. A {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, anneal, new ProgressTracker<T>(), hc);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems that runs
   * a hill climber as a post-processing step. A {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param anneal An annealing schedule.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      AnnealingSchedule anneal,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, anneal, new ProgressTracker<T>(), hc);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021). A {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    this(problem, mutation, initializer, new SelfTuningLam(), new ProgressTracker<T>(), null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021). A {@link ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @throws NullPointerException if any of the parameters are null.
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer) {
    this(problem, mutation, initializer, new SelfTuningLam(), new ProgressTracker<T>(), null);
  }

  /**
   * Creates a SimulatedAnnealing search instance for real-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021), and which runs a hill climber as a post-processing step. A {@link
   * ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      OptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, new SelfTuningLam(), new ProgressTracker<T>(), hc);
  }

  /**
   * Creates a SimulatedAnnealing search instance for integer-valued optimization problems, with a
   * default annealing schedule of {@link SelfTuningLam}, which is the Self-Tuning Lam annealing
   * schedule of Cicirello (2021), and which runs a hill climber as a post-processing step. A {@link
   * ProgressTracker} is created for you.
   *
   * @param problem An instance of an optimization problem to solve.
   * @param mutation A mutation operator supporting the undo operation.
   * @param initializer The source of random initial states for simulated annealing.
   * @param hc The Hill Climber that is used to locally optimize simulated annealing's end of run
   *     solution. If hc is null, then no post-processing step is performed, and the search is
   *     strictly simulated annealing. If hc.getProgressTracker() is not equal to tracker, then hc's
   *     ProgressTracker is reset to tracker. That is, the ProgressTracker must be shared between
   *     the simulated annealer and the Hill Climber.
   * @throws NullPointerException if any of the parameters are null (except for hc, which may be
   *     null).
   * @throws IllegalArgumentException if hc is not null and problem is not equal to hc.getProblem()
   */
  public SimulatedAnnealing(
      IntegerCostOptimizationProblem<T> problem,
      UndoableMutationOperator<T> mutation,
      Initializer<T> initializer,
      SimpleLocalMetaheuristic<T> hc) {
    this(problem, mutation, initializer, new SelfTuningLam(), new ProgressTracker<T>(), hc);
  }

  /*
   * private copy constructor in support of the split method.
   * note: copies references to thread-safe components, and splits potentially non-threadsafe components
   */
  private SimulatedAnnealing(SimulatedAnnealing<T> other) {
    // these are threadsafe, so just copy references
    pOpt = other.pOpt;
    pOptInt = other.pOptInt;

    // this one must be shared.
    tracker = other.tracker;

    // split these: not threadsafe
    initializer = other.initializer.split();
    mutation = other.mutation.split();
    anneal = other.anneal.split();
    hc = other.hc != null ? other.hc.split() : null;

    sr = pOptInt != null ? initSingleRunInt() : initSingleRunDouble();
  }

  /**
   * Reaneals starting from the previous best found solution contained in the tracker object. In
   * reannealing, simulated annealing starts from a prior found solution rather than from a random
   * one. The annealing schedule is reinitialized (e.g., high starting temperature, etc) as if it
   * was a fresh run. If no prior run had been performed, then this method starts the run from a
   * randomly generated solution.
   *
   * @param maxEvals The maximum number of simulated annealing evaluations (i.e., iterations) to
   *     execute.
   * @return the current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this simulated annealer's {@link ProgressTracker}, which contains the best of all runs.
   *     Returns null if the run did not execute, such as if the ProgressTracker already contains
   *     the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> reoptimize(int maxEvals) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    T start = tracker.getSolution();
    if (start == null) start = initializer.createCandidateSolution();
    else start = start.copy();
    return sr.optimizeSingleRun(maxEvals, start);
  }

  /**
   * Executes a run of simulated annealing beginning at a randomly generated solution. If this
   * method is called multiple times, each call begins at a new randomly generated starting
   * solution, and reinitializes the annealing schedule (e.g., starting temperature, etc) as if it
   * was a fresh run.
   *
   * @param maxEvals The maximum number of simulated annealing evaluations (i.e., iterations) to
   *     execute during this run.
   * @return The current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this simulated annealer's {@link ProgressTracker}, which contains the best of all runs.
   *     Returns null if the run did not execute, such as if the ProgressTracker already contains
   *     the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int maxEvals) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    return sr.optimizeSingleRun(maxEvals, initializer.createCandidateSolution());
  }

  /**
   * Executes a run of simulated annealing beginning at a specified starting solution. If this
   * method is called multiple times, each call begins by reinitializing the annealing schedule
   * (e.g., starting temperature, etc) as if it was a fresh run.
   *
   * @param maxEvals The maximum number of simulated annealing evaluations (i.e., iterations) to
   *     execute during this run.
   * @param start The desired starting solution.
   * @return The current solution at the end of this run and its cost, which may or may not be the
   *     best of run solution, and which may or may not be the same as the solution contained in
   *     this simulated annealer's {@link ProgressTracker}, which contains the best of all runs.
   *     Returns null if the run did not execute, such as if the ProgressTracker already contains
   *     the theoretical best solution.
   */
  @Override
  public final SolutionCostPair<T> optimize(int maxEvals, T start) {
    if (tracker.didFindBest() || tracker.isStopped()) return null;
    return sr.optimizeSingleRun(maxEvals, start.copy());
  }

  @Override
  public final Problem<T> getProblem() {
    return (pOptInt != null) ? pOptInt : pOpt;
  }

  @Override
  public final ProgressTracker<T> getProgressTracker() {
    return tracker;
  }

  @Override
  public final void setProgressTracker(ProgressTracker<T> tracker) {
    if (tracker != null) this.tracker = tracker;
  }

  @Override
  public SimulatedAnnealing<T> split() {
    return new SimulatedAnnealing<T>(this);
  }

  /**
   * Gets the total number of simulated annealing evaluations (iterations) performed by this
   * SimulatedAnnealing object. This is the total number of such evaluations across all calls to the
   * optimize and reoptimize methods. This may differ from the combined number of maxEvals passed as
   * a parameter to those methods. For example, those methods terminate if they find the theoretical
   * best solution, and also immediately return if a prior call found the theoretical best. In such
   * cases, the total run length may be less than the requested maxEvals.
   *
   * <p>If the simulated annealer has been configured with hill climbing as a post-processing step,
   * then the total run length includes both the simulated annealing iterations as well as the
   * number of hill climbing neighbor evaluations.
   *
   * @return the total number of simulated annealing evaluations
   */
  @Override
  public long getTotalRunLength() {
    if (hc == null) {
      return elapsedEvals;
    } else {
      return elapsedEvals + hc.getTotalRunLength();
    }
  }

  private interface SingleRun<T extends Copyable<T>> {
    SolutionCostPair<T> optimizeSingleRun(int maxEvals, T current);
  }

  private SingleRun<T> initSingleRunInt() {
    return (int maxEvals, T current) -> {
      // compute cost of start
      int currentCost = pOptInt.cost(current);

      // initialize best cost, etc
      int bestCost = tracker.getCost();
      if (currentCost < bestCost) {
        boolean isMinCost = pOptInt.isMinCost(currentCost);
        bestCost = tracker.update(currentCost, current, isMinCost);
        if (tracker.didFindBest()) {
          // found theoretical best so no point in proceeding
          return new SolutionCostPair<T>(current, currentCost, isMinCost);
        }
      }

      // initialize the annealing schedule
      anneal.init(maxEvals);

      // main simulated annealing loop
      for (int i = 1; i <= maxEvals; i++) {
        if (tracker.isStopped()) {
          // some other thread signaled to stop
          elapsedEvals += (i - 1);
          return new SolutionCostPair<T>(current, currentCost, pOptInt.isMinCost(currentCost));
        }
        mutation.mutate(current);
        int neighborCost = pOptInt.cost(current);
        if (anneal.accept(neighborCost, currentCost)) {
          // accepting the neighbor
          currentCost = neighborCost;
          if (currentCost < bestCost) {
            boolean isMinCost = pOptInt.isMinCost(currentCost);
            bestCost = tracker.update(currentCost, current, isMinCost);
            if (tracker.didFindBest()) {
              // found theoretical best so no point in proceeding
              elapsedEvals += i;
              return new SolutionCostPair<T>(current, currentCost, isMinCost);
            }
          }
        } else {
          // reject the neighbor and revert back to previous state
          mutation.undo(current);
        }
      }
      elapsedEvals += maxEvals;
      return hc == null
          ? new SolutionCostPair<T>(current, currentCost, pOptInt.isMinCost(currentCost))
          : hc.optimize(current);
    };
  }

  private SingleRun<T> initSingleRunDouble() {
    return (int maxEvals, T current) -> {
      // compute cost of start
      double currentCost = pOpt.cost(current);

      // initialize best cost, etc
      double bestCost = tracker.getCostDouble();
      if (currentCost < bestCost) {
        boolean isMinCost = pOpt.isMinCost(currentCost);
        bestCost = tracker.update(currentCost, current, isMinCost);
        if (tracker.didFindBest()) {
          // found theoretical best so no point in proceeding
          return new SolutionCostPair<T>(current, currentCost, isMinCost);
        }
      }

      // initialize the annealing schedule
      anneal.init(maxEvals);

      // main simulated annealing loop
      for (int i = 1; i <= maxEvals; i++) {
        if (tracker.isStopped()) {
          // some other thread signaled to stop
          elapsedEvals += (i - 1);
          return new SolutionCostPair<T>(current, currentCost, pOpt.isMinCost(currentCost));
        }
        mutation.mutate(current);
        double neighborCost = pOpt.cost(current);
        if (anneal.accept(neighborCost, currentCost)) {
          // accepting the neighbor
          currentCost = neighborCost;
          if (currentCost < bestCost) {
            boolean isMinCost = pOpt.isMinCost(currentCost);
            bestCost = tracker.update(currentCost, current, isMinCost);
            if (tracker.didFindBest()) {
              // found theoretical best so no point in proceeding
              elapsedEvals += i;
              return new SolutionCostPair<T>(current, currentCost, isMinCost);
            }
          }
        } else {
          // reject the neighbor and revert back to previous state
          mutation.undo(current);
        }
      }
      elapsedEvals += maxEvals;
      return hc == null
          ? new SolutionCostPair<T>(current, currentCost, pOpt.isMinCost(currentCost))
          : hc.optimize(current);
    };
  }
}
