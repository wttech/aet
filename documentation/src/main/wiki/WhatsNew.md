### What's new
AET 1.4 brings new reporting capabilities and improved performance. We have decided to label the new features as BETA and 
improve them based your feedback as it means a lot to us. Stay tuned!

#### Architecture performance improvements
We keep evolving the AET architecture and optimizing reporting performance. We have introduced new web application reports, 
which are now generated on the client-side. Also, we have optimized database model queries and decreased number of calls by a factor of 10. 
If you’ve ever been working on a release and discovered that the AET database has become overloaded, we have removed data storage duplications so you 
don’t need to worry any longer!

#### New reporting interface BETA
The reporting interface has been turned into a modern and responsive client-side application. 
Now it’s easy to search and filter results from your test case. If you are tired of re-running your tests after 
accepting patterns don’t worry, now all changes are applied on the fly. Additionally you can share a link to a test result 
that you are concerned about and share with your team just by copying the test URL and pasting to JIRA.

#### Accessibility report BETA
AET 1.4 introduces an accessibility report that displays validation output of page accessibility analysis using the 
[HTML_CodeSniffer](http://squizlabs.github.io/HTML_CodeSniffer/) library. Results show the total count of errors, warnings and notice type violations.

#### Client-side performance BETA
Now it’s possible to test client-side performance in an agile and incremental fashion. Brand new reports show overall scores along with a 
list of rules with individual grades. The performance analysis is based on the [YSlow](http://yslow.org/) tool.