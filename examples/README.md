# Examples Directory

The source for several example programs is contained in this directory.
Read the comments in the source code of the example programs 
for a description of what they demonstrate, etc. Running the 
examples without reading the source code, and comments, 
is not advised, since the output of the example programs 
requires the context of the code samples to be meaningful.

__Basic Examples__: The examples directory includes several examples. The following 
examples are of basic usage using simulated annealing to optimize
functions over different structure types: bit vectors, integer vectors,
permutations, and a real-value function optimization example, the root finding 
example:
* org.cicirello.examples.chipsnsalsa.BitVectorExample
* org.cicirello.examples.chipsnsalsa.IntegerVectorExample
* org.cicirello.examples.chipsnsalsa.RootFindingExample
* org.cicirello.examples.chipsnsalsa.PermutationExample

__Parallel Examples__: The next couple examples are of parallel search:
* org.cicirello.examples.chipsnsalsa.ParallelPermutationExample
* org.cicirello.examples.chipsnsalsa.TimedParallelExample

__Custom Problem Examples__: The following examples demonstrate defining a custom optimization
problem (whereas the above examples mostly use benchmark problems
included in the library):
* org.cicirello.examples.chipsnsalsa.CustomProblemExample
* org.cicirello.examples.chipsnsalsa.CustomIntegerCostProblemExample

__Hybrid Search Examples__: The following examples show how multiple search algorithms can be
integrated (e.g., combining hill climbing with simulated annealing):
* org.cicirello.examples.chipsnsalsa.PostHillclimbExample
* org.cicirello.examples.chipsnsalsa.PreHillclimbExample

__Industrial Scheduling Problem Examples__: The following examples use scheduling 
problems, and are also a bit more complex than some of the other example programs.
* org.cicirello.examples.chipsnsalsa.SchedulingExample
* org.cicirello.examples.chipsnsalsa.SchedulingWithVBSS

## Building the Examples

See the README in the root of the repository (in the parent of this directory) for more details. 
The examples are built using ant and the build.xml from the build directory, which can be executed 
via the makefile in the root of the repository.  Simply execute either `make` or `make build` 
from the root of the repository. 

## Running the Examples

After executing the build, the compiled examples will be in the 
exbin directory (created by the build process). To run all of the 
examples back to back, then from the root of the repository, 
execute: `make examples`. If you would rather run the examples
one at a time, then see the contents of the Makefile for the
relevant commands (e.g., to get the classpath set correctly, etc).
