ifeq ($(OS),Windows_NT)
	ClassPathOSAdjusted = "exbin;dist/chips-n-salsa-1.0-jar-with-dependencies.jar"
else
	ClassPathOSAdjusted = "exbin:dist/chips-n-salsa-1.0-jar-with-dependencies.jar"
endif

.PHONY: build
build:
	ant -f build/

.PHONY: examples
examples:
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.BitVectorExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.IntegerVectorExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.RootFindingExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.PermutationExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.ParallelPermutationExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.TimedParallelExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.CustomProblemExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.CustomIntegerCostProblemExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.PostHillclimbExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.PreHillclimbExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.SchedulingExample
	java -cp $(ClassPathOSAdjusted) org.cicirello.examples.chipsnsalsa.SchedulingWithVBSS

.PHONY: clean
clean:
	ant -f build/ clean
