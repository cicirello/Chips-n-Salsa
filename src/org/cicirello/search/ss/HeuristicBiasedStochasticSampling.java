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

package org.cicirello.search.ss;

import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.util.Copyable;

/**
 * <p>Heuristic Biased Stochastic Sampling (HBSS) is a form of stochastic sampling 
 * search that uses a constructive heuristic to bias the random decisions.
 * In HBSS, the search generates N random candidate solutions to the 
 * problem, using a problem-specific heuristic to rank the choices, and then
 * biasing each random decision in favor
 * of choices that are preferred by the heuristic (have ranks closer to 1).
 * It evaluates each of the N candidate solutions with respect to the optimization
 * problem's cost function, and returns the best of the N candidate solutions.</p>
 *
 * <p>Although the HBSS algorithm itself is not restricted to 
 * permutation problems, the examples that follow in this documentation focus
 * on permutations for illustrative purposes.</p>
 *
 * <p>Let's consider an illustrative example.  Consider a problem whose solution
 * is to be represented with a permutation of length L of the integers 
 * {0, 1, ..., (L-1)}.  For the sake of this example, let L=4.  Thus, we are searching
 * for a permutation of the integers {0, 1, 2, 3}.  We will iteratively build up
 * a partial permutation containing a subset of the elements into a complete
 * permutation.</p>  
 *
 * <p>We begin with an empty partial permutation: p = [].  The first iteration
 * will select the first element.  To do so, we use a heuristic to evaluate each option
 * within the context of the problem.  Let S be the set of elements not yet in the 
 * permutation, which in this case is initially S = {0, 1, 2, 3}.  Let h(p, e) 
 * be a constructive
 * heuristic that takes as input the current partial permutation p, and an element e from S
 * under consideration for addition to p, and which produces a real value as output 
 * that increases as the importance of adding e to p increases.  That is, higher values
 * of the heuristic imply that the heuristic has a higher level of confidence that the
 * element e should be added next to p.  For the sake of this example, consider that the
 * heuristic values are as follows: h([], 0) = 5, h([], 1) = 7, h([], 2) = 2, and h([], 3) = 1.
 * The heuristic seems to favor element 1 the most, and has element 0 as its second choice,
 * followed by element 2 as its third choice, and then element 3 is last.
 * HBSS then ranks the choices.  Let r(e) be the rank of element e.  Thus, we have:
 * r(0) = 2, r(1) = 1, r(2) = 3, and r(3) = 4.</p>  
 *
 * <p>Now, HBSS also uses a bias function b.  We will compute b(r(e)) for 
 * each element e under consideration for addition to partial permutation p.
 * A common form of the bias function is b(r(e)) = 1 / r(e)<sup>a</sup> for 
 * some exponent a.  The reason for the fraction is that lower ranks imply
 * apparently better choice, but we need the bias to be higher for apparently better
 * choices.  The greater your confidence is in the decision making
 * ability of the heuristic, the greater the exponent a should be.  We'll use a=2
 * for this example.  So given our heuristic values from above, we have the following:
 * b(r(0)) = 1/4 = 0.25, b(r(1)) = 1, b(r(2)) = 1/9 = 0.111, 
 * and b(r(3)) = 1/16 = 0.0625.</p>
 *
 * <p>The element that is added to the partial permutation p is then determined
 * randomly such that the probability P(e) of choosing element e is proportional to
 * b(r(e)).  Let's sum the b(r(e)) across the four elements.  That sum is approximately
 * 1.4235.
 * In this example, P(0) = 0.25 / 1.4235 = 0.176.
 * P(1) = 1 / 1.4235 = 0.702.  P(2) = 0.111 / 1.4235 = 0.078.  
 * P(3) = 0.0625 / 1.4235 = 0.044.
 * So with rather high probability, HBSS will end up beginning
 * the permutation with element 1 in this example.</p>
 *
 * <p>For the sake of the example, let's assume that element 1 was chosen above.
 * We now have partial permutation p = [1], and the set of elements not yet added
 * to p is S = {0, 2, 3}.  We need to compute h([1], e) for each of the elements e
 * from S, determine their ranks r, and then compute b(r(e)).  
 * For most problems, the heuristic values
 * would have changed, which might cause the ranks of the remaining elements also
 * to change relative to each other.  
 * For example, if the problem was the traveling salesperson,
 * then the heuristic might be in terms of the distance from the last city already
 * in the partial permutation (favoring nearby cities).  As you move from city
 * to city, which cities are nearest will change.  This is why one of the inputs
 * to the heuristic must be the current partial permutation. So let's assume for the example
 * that we recompute the heuristic and get the following values:
 * h([1], 0) = 1, h([1], 2) = 4, and h([1], 3) = 3.  The corresponding ranks are:
 * r(0) = 3, r(2) = 1, and r(3) = 2. 
 * When we compute the biases, we get:
 * b(r(0)) = 1/9 = 0.111, b(r(2)) = 1, and b(r(3)) = 1/4 = 0.25.  
 * The sum of the biases is 1.361.
 * The selection probabilities are thus: 
 * P(0) = 0.111 / 1.361 = 0.082; P(2) = 1 / 1.361 = 0.735;
 * and P(3) = 0.25 / 1.361 = 0.184.  
 * There is much higher probability of selecting
 * element 2, but for this example consider that element 3 was chosen instead.  
 * Thus, p is now p = [1, 3]
 * and S = {0, 2}.</p>
 *
 * <p>One final decision is needed in this example.  
 * Let h([1, 3], 0) = 5, and h([1, 3], 2) = 6, which means
 * r(0) = 2 and r(2) = 1, and further that
 * b(r(0)) = 1/4 = 0.25, and b(r(2)) = 1, which sum to 1.25.
 * The selection probabilities are then: P(0) = 0.25 / 1.25 = 0.2, and
 * P(2) = 1 / 1.25 = 0.8.  Let's say that element 2 was chosen, so that now we have
 * p = [0, 3, 2].  Since only one element remains, it is thus added as well to get
 * p = [0, 3, 2, 1].  This permutation is then evaluated with the optimization
 * problem's cost function, and the entire process repeated N times ultimately returning
 * the best (lowest cost) of the N randomly sampled solutions.</p>
 *
 * <p>To use this implementation of HBSS, you will need to implement a constructive
 * heuristic for your problem using the {@link ConstructiveHeuristic} interface.
 * The HeuristicBiasedStochasticSampling class also provides a variety of constructors
 * enabling defining the bias function in different ways.  The most basic uses 
 * the approach of the above example, allowing specifying the exponent, and the
 * default is simply an exponent of 1.  The most general allows you to specify any
 * arbitrary bias function using the {@link BiasFunction} interface.  Make sure
 * that you remember when implementing a bias for HBSS that lower values of rank
 * imply the presumed better option.</p>
 *
 * <p>Assuming that the length of the permutation is L, and that the runtime
 * of the constructive heuristic is O(f(L)), the runtime to construct one permutation
 * using HBSS is O(L<sup>2</sup> f(L)).  If the cost, f(L), to
 * heuristically evaluate one permutation element is simply, O(1), constant
 * time, then the cost to heuristically construct 
 * one permutation with HBSS is simply O(L<sup>2</sup>).</p>
 *
 * <p>See the following publications for the original description
 * of the HBSS algorithm:<br>
 * Bresina, J.L. (1996). "Heuristic-Biased Stochastic Sampling." 
 * Proceedings of the Thirteenth National Conference on Artificial 
 * Intelligence, AAAI Press, pp. 271–278.
 * </p>
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.11.2021
 */
public final class HeuristicBiasedStochasticSampling<T extends Copyable<T>> extends AbstractStochasticSampler<T> {
	
	private final BiasFunction bias;
	private final ConstructiveHeuristic<T> heuristic;
	private final double[] biases;
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.  A ProgressTracker 
	 * is created for you.  The default bias function returns 1/rank.
	 * @param heuristic The constructive heuristic.
	 * @throws NullPointerException if heuristic is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic) {
		this(heuristic, false, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.    
	 * The default bias function returns 1/rank.
	 * @param heuristic The constructive heuristic.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, ProgressTracker<T> tracker) {
		this(heuristic, false, tracker);
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.    
	 * @param heuristic The constructive heuristic.
	 * @param exponentialBias if true, the bias function evaluates as exp(-rank).
	 * if false, the bias function is the default of 1/rank.
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, boolean exponentialBias) {
		this(heuristic, exponentialBias, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.    
	 * @param heuristic The constructive heuristic.
	 * @param exponentialBias if true, the bias function evaluates as exp(-rank).
	 * if false, the bias function is the default of 1/rank.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, boolean exponentialBias, ProgressTracker<T> tracker) {
		this(heuristic, exponentialBias ? rank -> Math.exp(-rank) : rank -> 1.0/rank, tracker);
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.  A ProgressTracker 
	 * is created for you.
	 * @param heuristic The constructive heuristic.
	 * @param exponent The bias function is defined as: bias(rank) = 1 / pow(rank, exponent).
	 * @throws NullPointerException if heuristic is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, double exponent) {
		this(heuristic, exponent, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.
	 * @param heuristic The constructive heuristic.
	 * @param exponent The bias function is defined as: bias(rank) = 1 / pow(rank, exponent)
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, double exponent, ProgressTracker<T> tracker) {
		this(heuristic, rank -> Math.pow(1.0/rank, exponent), tracker);
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object. A ProgressTracker 
	 * is created for you.
	 * @param heuristic The constructive heuristic.
	 * @param bias The bias function.  If null, the default bias is used.
	 * @throws NullPointerException if heuristic is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, BiasFunction bias) {
		this(heuristic, bias, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs a HeuristicBiasedStochasticSampling search object.
	 * @param heuristic The constructive heuristic.
	 * @param bias The bias function.  If null, then default bias is used.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if heuristic or tracker is null
	 */
	public HeuristicBiasedStochasticSampling(ConstructiveHeuristic<T> heuristic, BiasFunction bias, ProgressTracker<T> tracker) {
		super(heuristic.getProblem(), tracker);
		this.bias = bias;
		this.heuristic = heuristic;
		biases = precomputeBiases(heuristic.completeLength());
	}
	
	/*
	 * private for use by split method
	 */
	private HeuristicBiasedStochasticSampling(HeuristicBiasedStochasticSampling<T> other) {
		super(other);
		bias = other.bias;
		heuristic = other.heuristic;
		biases = other.biases;
	}
	
	@Override
	public HeuristicBiasedStochasticSampling<T> split() {
		return new HeuristicBiasedStochasticSampling<T>(this);
	}
	
	/**
	 * <p>Implement this interface to implement the bias function
	 * used by HBSS. Specifically, when making a randomized
	 * decision among possible permutation elements to add to
	 * the permutation, HBSS choose randomly but biased by a function
	 * of the ranks that would result if we were to sort the elements
	 * in order by their heuristic values.  If rank is the rank
	 * of permutation element e according to the heuristic, 
	 * then e will be added to the permutation
	 * with a probability proportional to bias(rank).  
	 * How you implement this depends upon how much confidence you
	 * have in the specific heuristic you are randomizing.  Just make
	 * sure that you remember that the lowest rank, rank of 1, is the element 
	 * perceived to be best by the heuristic.  So the bias function must
	 * map lower ranks to higher values, and the output must be positive.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 * @version 7.9.2020
	 */
	public static interface BiasFunction {
		
		/**
		 * This method is the bias function.
		 * @param rank The rank of one of the elements under
		 * consideration for addition to the permutation, where rank
		 * means position in a list of the elements if they were sorted
		 * by heuristic value.
		 * @return the bias function applied to that rank
		 */
		double bias(int rank);
	}
	
	/*
	 * package-private: used internally, but want to access from test class for unit testing
	 */
	double[] precomputeBiases(int n) {
		// Note 1: Since the bias computation only depends on ranks, and the rank
		// values don't change, only the elements that have the ranks change,
		// we can precompute these to avoid recomputing them over and over again.
		// Note 2: This actually precomputes a rolling sum of the biases.
		double[] biases = new double[n];
		if (n > 0) {
			biases[0] = bias.bias(1);
			for (int rank = 2; rank <= n; rank++) {
				biases[rank-1] = bias.bias(rank) + biases[rank-2];
			}
		}
		return biases;
	}
	
	/*
	 * package-private: used internally, but want to access from test class for unit testing
	 */
	int select(double[] values, int k, double u) {
		return select(values, 0, k-1, u);
	}
	
	private int select(double[] values, int first, int last, double u) {
		if (last <= first) {
			return first;
		}
		int mid = (first + last) >> 1;
		if (u < values[mid]) return select(values, first, mid, u);
		else return select(values, mid+1, last, u);
	}
	
	/*
	 * package-private: used internally, but want to access from test class for unit testing
	 */
	int randomizedSelect(int[] indexes, double[] v, int k, int chosenRank) {
		return randomizedSelect(indexes, v, 0, k-1, chosenRank);
	}
	
	private int randomizedSelect(int[] indexes, double[] v, int first, int last, int chosenRank) {
		if (first == last) return indexes[first];
		int pivot = randomizedPartition(indexes, v, first, last);
		int k = pivot - first + 1;
		if (chosenRank == k) return indexes[pivot];
		else if (chosenRank < k) return randomizedSelect(indexes, v, first, pivot-1, chosenRank);
		else return randomizedSelect(indexes, v, pivot+1, last, chosenRank-k);
	}
	
	private int randomizedPartition(int[] indexes, double[] v, int first, int last) {
		int pivot = first + RandomIndexer.nextBiasedInt(last - first + 1);
		int temp = indexes[pivot];
		indexes[pivot] = indexes[last];
		indexes[last] = temp;
		int x = indexes[last];
		pivot = first - 1;
		for (int j = first; j < last; j++) {
			if (v[indexes[j]] >= v[x]) {
				pivot++;
				temp = indexes[pivot];
				indexes[pivot] = indexes[j];
				indexes[j] = temp;
			}
		}
		pivot++;
		temp = indexes[pivot];
		indexes[pivot] = indexes[last];
		indexes[last] = temp;
		return pivot;
	}
	
	@Override
	SolutionCostPair<T> sample() {
		IncrementalEvaluation<T> incEval = heuristic.createIncrementalEvaluation();
		int n = heuristic.completeLength();
		Partial<T> p = heuristic.createPartial(n);
		double[] v = new double[n];
		int[] extensions = new int[n];
		ThreadLocalRandom r = ThreadLocalRandom.current();
		while (!p.isComplete()) {
			int k = p.numExtensions();
			if (k==1) {
				incEval.extend(p, p.getExtension(0));
				p.extend(0);
			} else {
				int chosenRank = 1 + select(biases, k, r.nextDouble(biases[k-1]));
				for (int i = 0; i < k; i++) {
					v[i] = heuristic.h(p, p.getExtension(i), incEval);
					extensions[i] = i;
				}
				int which = randomizedSelect(extensions, v, k, chosenRank);
				incEval.extend(p, p.getExtension(which));
				p.extend(which);
			}
		}
		T complete = p.toComplete();
		return evaluateAndPackageSolution(complete);
	}
}