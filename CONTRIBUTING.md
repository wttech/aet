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
AET has it's own [AET wiki](https://github.com/Cognifide/aet/wiki) containing:
- user documentation,
- developer documentation,
- system administrator documentation,
- system architecture documentation.

Current wiki content contains the information that is up-to-date with the AET `master` branch codebase.
If a user wants to refer to the older AET version documentation, there are dedicated one-page documentations 
[released together with AET code](https://github.com/Cognifide/aet/tree/master/documentation#generating-released-version-documentation)
e.g. [AET 2.0.0 Documentation](https://github.com/Cognifide/aet/wiki/Documentation-2.0.0).

By the design GitHub Wiki is a [separate git repository](https://help.github.com/articles/adding-and-editing-wiki-pages-locally/).
To make it easier to update documentation together with new features or fixes, AET keep all the documentation is in the same repository as the codebase, in the [documentation](https://github.com/Cognifide/aet/tree/master/documentation) module.

After the feature branch (codebase and wiki pages) is merged to AET `master` branch - 
wiki repository is synchronised with the content of [documentation](https://github.com/Cognifide/aet/tree/master/documentation) module
(this is manual action).

### How to contribute to AET documentation
When updating documentation please create new markdown page or update existing one in the
[documentation](https://github.com/Cognifide/aet/tree/master/documentation) module following 
[instructions](https://github.com/Cognifide/aet/tree/master/documentation#writing-documentation). 
Remember to include any updates together with any code changes in pull request.

Have in mind few important rules:
- When you add a new markdown page, add a reference to the [`_Sidebar.md`](https://github.com/Cognifide/aet/blob/master/documentation/src/main/wiki/_Sidebar.md)
file which defines how the Wiki's right navigation menu looks like.
- Please do not update wiki pages directly because your changes will be lost during the next wiki upgrade.
- If you spot some misspell (typo) remember that you can propose an improvement [directly from the browser](https://help.github.com/articles/editing-files-in-your-repository/).
Please do so, we believe that having good an up-to-date documentation is as important as having stable code.
- AET has its [FAQ](https://github.com/Cognifide/aet/wiki/FAQ). If you believe that something is missing there, please create a pull request or a [new GitHub issue](https://github.com/Cognifide/aet/issues/new).

## Changelog
When changing or fixing some important part of AET, please remember to update [Changelog](https://github.com/Cognifide/aet/blob/master/CHANGELOG.md).
Your entry should be enlisted in `Unreleased` section. It will be moved to appropriate release notes during release process.
Please use convention `- [PR-ABC](https://github.com/Cognifide/aet/pull/ABC) - short description of the PR.`.

## Tests naming convention
Tests written in AET should be named with `methodName_whenStateUnderTest_expectBehavior` convention proposed as the first example in [7 Popular Unit Test Naming Conventions](https://dzone.com/articles/7-popular-unit-test-naming).

### Examples:
**Unit tests**
`canTakeScreenshot_whenNoUrlProvided_expectNoUrlException`

**Integration tests**
In integration tests method name is omitted, and test class name should suggest what part of system we test:
`whenPageReturned404_expectNoScreenshotCollected`

## Coding Conventions

We follow Google code style guide for [JAVA](https://google.github.io/styleguide/javaguide.html), [JavaScript](https://google.github.io/styleguide/jsguide.html) and [HTML](https://google.github.io/styleguide/htmlcssguide.html) files.

We also follow [John Papa Angular style guide](https://github.com/johnpapa/angular-styleguide/tree/master/a1).

Below is a short list of things that will help us keep AET quality and accept pull requests:
- Follow Google Style Guide code formatting from AET Github (originally adapted from [Google Style Guide](https://github.com/google/styleguide))
  - [Google Style Guide for Eclipse](https://github.com/Cognifide/aet/tree/master/eclipse-java-google-style.xml) 
  - [Google Style Guide for IntelliJ](https://github.com/Cognifide/aet/tree/master/intellij-java-google-style.xml)
- write tests (integration/functional and Unit Tests) following defined convention,
- write javadoc, especially for interfaces and abstract methods,
- update [documentation](https://github.com/Cognifide/aet/tree/master/documentation) and include changes in the same pull request which modifies the code,
- provide description of any new features,
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages. 
