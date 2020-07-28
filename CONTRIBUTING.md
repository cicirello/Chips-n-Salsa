# How to Contribute

Thank you for your interest in contributing to Chips-n-Salsa!  Read on for information on how to contribute.

## Bug Reports

Bug reports should be submitted via the [issues tracker](https://github.com/cicirello/Chips-n-Salsa/issues) following these steps:
1. First, check whether there already is an open issue for your bug.
2. If an open issue for your bug doesn't already exist, then submit an issue.
3. Please include enough detail to reproduce the issue, preferably with code that reproduces the issue.

## Feature Requests

Requests for new functionality or enhancements to existing functionality can also be submitted via 
the [issue tracker](https://github.com/cicirello/Chips-n-Salsa/issues).  Just like with bug reports,
please verify that there isn't already an existing issue covering the same request.

## Bug Fixes

If you would like to more actively contribute with a bug fix, then follow this procedure:
1. Fork the repository.
2. Create a branch for the patch.
3. Include a JUnit test (or tests) that demonstrate the bug (i.e., that fail without your patch, but passes with your patch).
4. All existing JUnit tests must continue to pass.
5. Submit a pull request.

## New Features and Other Contributions

If you would like to contribute new features and functionality, such as but not limited to implementations of additional 
algorithms, or operators for existing library algorithms, or additional problem definitions, etc, then follow the 
following procedure:
1. Fork the repository.
2. Create a branch for the feature.
3. Implement the new functionality.
4. Include JUnit tests that thoroughly unit test the new functioality.
5. All existing JUnit tests must continue to pass.
6. Submit a pull request.

## A Few Notes on JUnit Tests

The JUnit tests are organized into a package hierarchy that mirrors that of the source code of the library itself.
Please adhere to this structure with any JUnit tests that you write.
