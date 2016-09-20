### Open

Open module is special operand for collect phase. It is responsible for opening web page for given url and preparing browser environment to perform chain of collections and modifications.

Second usage of this module is to allow user easily perform actions before page is being opened, such as modify headers, cookies etc.

| ! Open module |
|:------------- |
| Each collect phase **must** contain open module. |

| ! Note |
|:------ |
| In some cases it is recommended to use **[[Sleep Modifier|SleepModifier]]** or **[[Wait For Page Loaded Modifier|WaitForPageLoadedModifier]]** after open module. |

Module name: **open**

#### Parameters

No parameters

#### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="open-test">
      <collect>
        ...
        <!-- example action before page is opened -->
        <open/>
        <!-- collect page data -->
        ...
      </collect>
      <compare>
        ...
      </compare>
      <urls>
        ...
      </urls>
    </test>
    ...
    <reports>
      ...
    </reports>
</suite>
```
