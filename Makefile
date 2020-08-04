.PHONY: build
build:
	ant -f build/

.PHONY: examples
examples:
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.BitVectorExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.IntegerVectorExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.RootFindingExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.PermutationExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.ParallelPermutationExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.TimedParallelExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.CustomProblemExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.CustomIntegerCostProblemExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.PostHillclimbExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.PreHillclimbExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.SchedulingExample
	java -cp "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar" org.cicirello.examples.chipsnsalsa.SchedulingWithVBSS
	
.PHONY: clean
clean:
	ant -f build/ clean
