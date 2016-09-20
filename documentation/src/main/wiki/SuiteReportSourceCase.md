#### Source

##### Description
Source test cases' results display compared sources. (see screenshot below).

![Source failure](assets/suiteReport/source-failure.png)

1. Test case's name (red font means failure),
2. "Accept test case" button (available only when differences have been detected),
3. "Show full source" switch - when the switch is off only differences are shown on the screen, otherwise full source is shown,
4. Pattern - source file to which collected source is compared. When there is no pattern, first collected source is saved as pattern automatically,
5. Source - source file which is compared with pattern,
6. Sample block with visible differences (e.g. changed characters).

Test case's result is marked as successful when there is no difference between source and pattern (see screenshot below).

![Source success](assets/suiteReport/source-success.png)

##### What vulnerabilities it discovers
* Differences found by source comparison may indicate undesired changes in a page layout (html structure) and content, 
e.g. when a new functionality is implemented in a system it might have an impact on other system component(s). 
This may occur as a changed page source.
* Content changes can be divided into two groups: wanted (intended) and unwanted (result of a mistake or an error). 
In order to filter out wanted changes and detect changes that are a result of a mistake or an error, the user can use one of following filters in the suite definition:
  * [[The Extract Element Data Modifier|ExtractElementDataFilter]] (e.g. to find changes only in the main menu that has the parameter id='main-menu' set),
  * [[The Remove Lines Data Filter|RemoveLinesDataFilter]] (to remove lines that changes every time - e.g. a current timestamp),
  * [[The Remove Nodes Data Filter|RemoveNodesDataFilter]] (e.g. to remove content displayed by the dynamic news carousel component).
