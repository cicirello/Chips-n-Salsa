# Examples Directory Readme

The source for examples is contained in this directory, and currently includes the following examples:
* org.cicirello.examples.chipsnsalsa.BitVectorExample
* org.cicirello.examples.chipsnsalsa.IntegerVectorExample
* org.cicirello.examples.chipsnsalsa.RootFindingExample
* org.cicirello.examples.chipsnsalsa.PermutationExample
* org.cicirello.examples.chipsnsalsa.ParallelPermutationExample
* org.cicirello.examples.chipsnsalsa.TimedParallelExample
* org.cicirello.examples.chipsnsalsa.CustomProblemExample
* org.cicirello.examples.chipsnsalsa.CustomIntegerCostProblemExample
* org.cicirello.examples.chipsnsalsa.PostHillclimbExample
* org.cicirello.examples.chipsnsalsa.PreHillclimbExample

Read the comments in the source code itself for a description of what they demonstrate, etc.

Make sure the jar file of the library is in your classpath when you compile 
and/or run any of these. Specifically, make sure chips-n-salsa-1.0-with-dependencies.jar is 
in your classpath (or any later version--I intend to ensure later versions are backwards 
compatible).

## Building the Examples

See the README in the root of the repository (in the parent of this directory) for more details. 
The examples are built using ant and the build.xml from the build directory, which can be executed 
via the makefile in the root of the repository. 

## Running the Examples

After executing the build, the compiled examples will be in the 
exbin directory. To run all of the examples back to back, then from the root of the repository, execute: `make examples`.
If your working directory is the root of the repository, then 
you can run any of the examples from the command line with the following:

```
java -cp "exbin;dist/chips-n-salsa-1.0-with-dependencies" org.cicirello.examples.chipsnsalsa.BitVectorExample
```

Just replace org.cicirello.examples.chipsnsalsa.BitVectorExample in the above with the example program of your
choice.
