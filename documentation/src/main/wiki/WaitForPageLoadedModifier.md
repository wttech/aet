#### Wait For Page Loaded Modifier

Wait For Page Loaded Modifier waits until page is loaded (all DOM elements are loaded - this does not wait for dynamically loaded elements by e.g. JavaScript) or fixed amount of time is up. The idea of waiting for page is counting amount of elements [by `findElements(By.xpath("//*"))`] on current page state in loop. If number of elements has increased since last checkout, continue loop (or break if timeout). Else if number of elements is still, assume the page is loaded and finish waiting.

Module name: **wait-for-page-loaded**

##### Parameters

No parameters.


| ! Important information |
|:----------------------- |
| Timeout for waiting is 10000 milliseconds.<br/><br/> Page is checked every 1000 miliseconds. |

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="wait-for-page-loaded-test">
        <collect>
            ...
            <open />
            ...
            <wait-for-page-loaded />
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
