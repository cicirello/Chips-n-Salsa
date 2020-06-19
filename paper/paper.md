---
title: 'Chips-n-Salsa: A Java Library of Customizable, Hybridizable, Iterative, Parallel, Stochastic, and Self-Adaptive Local Search Algorithms'
tags:
  - stochastic local search
  - parallel metaheuristics
  - self-adaptive search
  - simulated annealing
  - discrete optimization
  - combinatorial optimization
  - Java
authors:
  - name: Vincent A. Cicirello
    orcid: 0000-0003-1072-8559
    affiliation: 1
affiliations:
  - name: Computer Science, School of Business, Stockton University, Galloway, NJ 08205
    index: 1
date: 19 June 2020
bibliography: paper.bib
---

# Summary

Discrete optimization problems are often both of practical importance in the real-world as well as computationally intractable. For example, the optimization variants of the traveling salesperson, bin packing, and longest common subsequence problems are NP-Hard, as are the resource constrained scheduling problem, and even many single-machine sequencing problems [@Garey1979]. Polynomial time algorithms for such problems are unlikely to exist, and the best known algorithms that are guaranteed to optimally solve these problems have a worst-case exponential runtime. It is thus common to turn to stochastic local search and other forms of metaheuristic when dealing with these and related problems. Stochastic local search algorithms begin at a randomly generated search state, and then apply a sequence of neighbor transitions to nearby similar search states. Stochastic local search includes perturbative [@Hoos2005] algorithms like simulated annealing [@Kirkpatrick1983] and hill climbers [@Rusell2009], where each search state is a complete candidate feasible solution, and a mutation operator (or neighborhood operator) makes a small modification (usually randomly) to move to another local candidate solution; and also includes constructive [@Hoos2005] algorithms like stochastic samplers [@Cicirello2005; @Bresina1996; @Langley1992], where each search state is a partial solution that is iteratively transformed into a complete feasible solution. Stochastic local search algorithms are not guaranteed to find optimal solutions. However, they often find sufficiently optimal solutions in much less time than systematic search. They also are characterized by an anytime property [@Zilberstein1996], such that the quality of the solution improves with runtime.

Chips-n-Salsa is a Java library of customizable, hybridizable, iterative, parallel, stochastic, and self-adaptive local search algorithms. Although its focus is on discrete optimization, it also supports continuous optimization problems. It includes classes for representing solutions to problems in a variety of ways. For example, it includes BitVector and IntegerVector classes, indexable vectors of bits and integers, respectively, along with corresponding mutation operators for use by local search for these types. The library also utilizes our significant prior research on permutation optimization problems [@Cicirello2019; @Cicirello2018a; @Cicirello2017; @Cicirello2016; @Cicirello2015; @Cicirello2014; @Cicirello2013; @Cicirello2010; @Cicirello2007; @Cicirello2006], and provides an extensive set of mutation operators for permutations [@Cicirello2016; @Cicirello2013], including window-limited mutation [@Cicirello2014] operators. It uses the efficiently implemented Permutation class of the JavaPermutationTools (JPT) [@Cicirello2018b] library for representing solutions to such problems. For continuous problems, Chips-n-Salsa provides a RealVector class as well as mutation operators that include Gaussian mutation [@Hinterding1995] and Cauchy mutation [@Szu1987]. The library also includes optimization problems useful for benchmarking new metaheuristic implementations, such as the well-known OneMax problem [@Ackley1985], BoundMax (a generalization of OneMax from bits to integers), the Permutation in a Haystack problem [@Cicirello2016], as well as polynomial root finding.

The Chips-n-Salsa library has the following features and functionality:

* Stochastic Local Search Algorithms: The library most extensively supports simulated annealing [@Kirkpatrick1983], providing implementations of all of the common annealing schedules, as well as more advanced annealing schedules.  However, it also includes implementations of common forms of hill climbing [@Rusell2009], as well as iterative sampling [@Langley1992]. Additional algorithms from prior research will be integrated with the library in the near future, such as HBSS [@Bresina1996] and VBSS [@Cicirello2005]. We carefully chose the random number generators for the library based on our prior research [@Cicirello2018a] to minimize the runtime impact from one of the more costly operations of a metaheuristic.
* Customizable: Chips-n-Salsa extensively uses Java's generic types enabling using the library to optimize other types of representations beyond what is provided in the library. It is also designed to allow easily integrating new mutation operators, as well as to enable customizing all stages of the local search (e.g., choice of annealing schedule for simulated annealing).
* Hybridizable: Chips-n-Salsa supports integrating multiple forms of local search (e.g., hybrids of hill climbing with simulated annealing), creating hybrid mutation operators (e.g., combining multiple mutation operators), as well as support for running more than one type of search for the same problem concurrently using multiple threads as a form of algorithm portfolio [@Gomes2001].
* Iterative: Chips-n-Salsa supports multistart metaheuristics, including implementations of several restart schedules [@Luby1993; @Cicirello2017] for varying the run lengths across the restarts. 
* Parallel: Chips-n-Salsa supports parallel execution of multiple instances of the same, or different, stochastic local search algorithms for an instance of a problem to accelerate the search process [@Cicirello2017]. 
* Self-Adaptive: Chips-n-Salsa includes implementations of adaptive annealing schedules for simulated annealing, such as the Modified Lam schedule [@Lam1988; @Swartz1993; @Boyan1998; @Cicirello2007], implementations of self-tuning variations of the simpler annealing schedules, and restart schedules that adapt to run length [@Cicirello2017].

The repository (https://github.com/cicirello/Chips-n-Salsa) contains source code of the library, and programs that provide example usage of key functionality.  API and other documentation is hosted on the web (https://chips-n-salsa.cicirello.org/). 

# Statement of Need

The target audience of Chips-n-Salsa includes those conducting computational research in diverse domains, wherever applications of NP-Hard optimization problems is important. Existing local search and metaheuristic implementations that are freely available are often intimately tied to a specific problem, or at best to a specific representation; whereas Chips-n-Salsa is not only problem-independent, but also representation-independent. Its support for parallel execution enables easily exploiting multicore processor architectures to speed up problem solving.  The self-adaptive and self-tuning features streamline the development process, eliminating the often tedious stage, control parameter optimization, of applying stochastic local search to a new problem.

# References
