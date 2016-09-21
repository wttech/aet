![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# How to contribute to AET
Thank you for taking the time to contribute!
We appreciate all commits and improvements, feel free to join AET community and contribute.

## How to start
Please refer to [AET Documentation](https://cognifide.atlassian.net/wiki/display/AET/) to see how to setup local environment.
Submit a ticket for your issue, assuming one does not already exist.

- Clearly describe the issue including steps to reproduce when it is a bug.
- Make sure you fill in the earliest version that you know has the issue.

Fork the repository on GitHub.

## AET Contributor License Agreement
Project License: [Apache License Version 2.0](https://github.com/Cognifide/AET/blob/master/LICENSE)

- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

## Commit Messages
When writing a commit message, please follow the guidelines in [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

## Pull Requests
Please add the following lines to your pull request description:

```
___

I hereby agree to the terms of the AET Contributor License Agreement.
```

## Documentation

**Please do not update wiki pages directly because your changes will be lost.**

All AET documentation is in the same repository as codebase in [documentation](https://github.com/Cognifide/aet/tree/master/documentation) module.
This documentation after update is ported to [AET wiki](https://github.com/Cognifide/aet/wiki).
When updating documentation please update proper markdown pages in [documentation](https://github.com/Cognifide/aet/tree/master/documentation) module following [instructions](https://github.com/Cognifide/aet/blob/master/documentation/README.md) and include it with your pull request.
After your pull request is merged, wiki pages will be updated.

## Tests naming convention
Tests written in AET should be named with `methodName_whenStateUnderTest_expectBehavior` convention proposed as the first example in [7 Popular Unit Test Naming Conventions](https://dzone.com/articles/7-popular-unit-test-naming).

### Examples:
**Unit tests**
`canTakeScreenshot_whenNoUrlProvided_expectNoUrlException`

**Integration tests**
In integration tests method name is omitted, and test class name should suggest what part of system we test:
`whenPageReturned404_expectNoScreenshotCollected`

## Coding Conventions
Below is a short list of things that will help us keep AET quality and accept pull requests:
- Follow [Google Style Guide](https://github.com/google/styleguide) code formatting,
- write tests (integration/functional and Unit Tests) following defined convention,
- write javadoc, especially for interfaces and abstract methods,
- update [documentation](https://github.com/Cognifide/aet/tree/master/documentation) and include changes in the same pull request which modifies the code,
- provide description of any new features,
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages. 
