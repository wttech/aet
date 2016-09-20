#### Client side performance

##### Description
| ! Important information |
|:----------------------- |
|This feature is currently in BETA version.|

Client side performance report results display validation output of performance analysis. Result shows overall score and the list of rules, which are graded individually. Each rule contains a description and optionally the list of components associated with the rule. Results presented on this report come from [YSlow](http://yslow.org/) tool. 

![Client Side Performance](assets/suiteReport/client-side-performance.png)

1. Test case name with status color.
2. Overall performance score (computed from subscores).
3. List of subscores (Each subscore is assigned a letter grade A through F, with A being the highest grade).
4. Description of subscore (only for score with grade worse than A).

If overall score is A, the report is marked as success.

If overall score is B to E, the report is marked as warning.

If overall score is F, the report is marked as risk.

##### What vulnerabilities it discovers
* Poor client side performance has negative impact on user experience.
* Pages with grade F are slow and probably user will not want to use them. They are especially difficult to load on mobile devices.
* Client side performance is taken into account by most of search engines. Good performance means better ranking.