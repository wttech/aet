## Migrate AET Suite to AET 2.0
### Preparing the suite XML
You should perform the following changes in your project suite XML:

* remove the `environment="win7-ff16` property from the `suite` tag,

  ![Win7-ff16 property's position](assets/aetSuiteMigration/win7-ff16.png)
  
* remove the whole `<reports>` selection from XML,

  ![Reports' location](assets/aetSuiteMigration/reports.png)
  
| ! Important information |
|:----------------------- |
|From this AET version, html reports are no longer generated as static files but are served by dynamic web application. If you still need a xunit report please update your aet run command with the parameter `xUnit=true`. See more in [[Client Application|ClientApplication]]. |

  
* check your `screen` collector definition, remove deprecated parameters: `maximize`, `width`, `height` (see  the [[Screen Collector|ScreenColector]] documentation for recommended changes),

  ![Screen collector's position](assets/aetSuiteMigration/screen-collector.png)
  
* stop using the `w3c` comparator, use `w3c-html5` instead,

  ![w3c position](assets/aetSuiteMigration/w3c.png)
  
* [OPTIONAL] in `w3c-html5` use the `ignore-warnings` parameter instead of the `errors-only`parameter in `w3c-html5`).