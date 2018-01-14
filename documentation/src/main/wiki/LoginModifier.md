#### Login Modifier

Login Modifier allows to login into pages that have access secured with login form. If input element isn't available (for example it's not loaded yet) then Login Modifier will wait up to 10s for login input, then for password input and at the end for submit button to appear. If the login form isn't ready or logging in fails for any other reason, we will try again 3 times by default. 

Login modifier checks if *login-token-key* cookie is present to decide if authentication was successful. If cookie with specified name is present, it assumes that credentials were valid.

Module name: **login**

| ! Important information |
|:----------------------- |
| In order to use this modifier it must be declared before open module in test suite XML definition. |

##### Parameters

| Parameter | Value | Mandatory | Default value |
| --------- | ----- | --------- | ------------- |
| `login` | User's login | no | admin |
| `password` | Password | no | admin |
| `login-page` | Url to login page | yes | |
| `login-input-selector` | Xpath expression for login input | no | //input[@name='j_username'] |
| `password-input-selector` | Xpath expression for password input | no | //input[@name='j_password'] |
| `submit-button-selector` | Xpath expression for submit button | no | //*[@type='submit'] |
| `login-token-key` | Name for cookie we get after successfull login | no | login-token |
| `timeout` | Number of milliseconds (between 0 and 10000) that modifier will wait to login page response after submiting credentials. It is also used between reattempts to log in. | no | 5000 |
| `force-login` | Enforces login even when login cookie is present. | no | false |
| `retrial-number` | Number of reattempts to log in. It's a way to deal with unpredictable problem with logging in. | no | 3 |  

##### Example Usage

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="login-test">
        <collect>
            <login login="user"
                password="password"
                login-page="http://192.168.180.19:5503/libs/cq/core/content/login.html"
                login-input-selector="//input[@name='j_username']"
                password-input-selector="//input[@name='j_password']"
                submit-button-selector="//*[@type='submit']" />
            <open />
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
