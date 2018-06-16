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

#### Tips and tricks 
A test can provide different results depending on whether first the browser 
window is opened or it's target resolution is defined.

##### First "open" then "resolution"
```xml
<test>
    <collect>
      ...
      <open/>
      <resolution width="1280" height="768"/>
      ...
      <screen/>
    </collect>
    ...
</test>
```
The screenshot taken during this test captures how the page looks like when the user opens the page 
using the default resolution and then switches to 1280x768px.

##### First "resolution" then "open"
```xml
<test>
    <collect>
      ...
      <resolution width="1280" height="768"/>
      <open/>
      ...
      <screen/>
    </collect>
    ...
</test>
```
The screenshot taken during this test captures how the page looks like when the user opens the page 
using the resolution 1280x768px from the very beginning.
