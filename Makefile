ifeq ($(OS),Windows_NT)
	ClassPathOSAdjusted = "exbin;dist/chips-n-salsa-2.0-jar-with-dependencies.jar"
else
	ClassPathOSAdjusted = "exbin:dist/chips-n-salsa-2.0-jar-with-dependencies.jar"
endif

.PHONY: build
build:
	ant -f build/

.PHONY: clean
clean:
	ant -f build/ clean
