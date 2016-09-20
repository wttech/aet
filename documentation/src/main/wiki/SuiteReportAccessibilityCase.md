#### Accessibility

##### Description
| ! Important information |
|:----------------------- |
|This feature is currently in BETA version.|

This test case's result displays validation output of page accessibility analysis. Result shows total count of errors, warnings and notices about possible violations. Output presented on this report comes from [html CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) library.

Result is successful if there are no errors, warnings or notices (see screenshot below).

![Accessibility success](assets/suiteReport/accessibility-success.png)

If there is no error, but several warnings or notices appear, the result is marked as warning (see screenshot below).
 
![Accessibility warning](assets/suiteReport/accessibility-warning.png)

If any error occurs, the result is marked as risk (see screenshot below).

![Accessibility failure](assets/suiteReport/accessibility-failure.png)

##### What vulnerabilities it discovers
* When page fails accessibility tests it could mean that it couldn't access information, e.g. there are images on page without description or alt attribute, anchors elements don't have link content, page styling and design is not clear enough for people with disabilities.
* Lack of accessibility could be against the law in some countries.