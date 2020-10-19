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
| `login` | User's login, can be passed plaintext or read from environment variable using `"${ENV_VARIABLE_NAME}"`. See the example below. | no | admin |
| `password` | Password, can be passed plaintext or read from environment variable using `"${ENV_VARIABLE_NAME}"`. See the example below. | no | admin |
| `login-page` | Full url to login page (starting with `http` or `https`) or login page path that is in the `domain` specified for the suite | yes | |
| `login-input-selector` | Xpath expression for login input | no | //input[@name='j_username'] |
| `password-input-selector` | Xpath expression for password input | no | //input[@name='j_password'] |
| `submit-button-selector` | Xpath expression for submit button | no | //*[@type='submit'] |
| `login-token-key` | Name for cookie we get after successfull login | no | login-token |
| `timeout` | Number of milliseconds (between 0 and 10000) that modifier will wait to login page response after submiting credentials. It is also used between reattempts to log in. | no | 5000 |
| `force-login` | Enforces login even when login cookie is present. | no | false |
| `retrial-number` | Number of reattempts to log in. It's a way to deal with unpredictable problem with logging in. | no | 3 |  

##### Example Usage

> Pass `login` and `password` as plain text and `login-page` as full URL:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project">
    <test name="login-test">
        <collect>
            <login login="user"
                password="password"
                login-page="http://example.com/login.html"
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

> Pass `login` and `password` as plain text and `login-page` as URL inside the `domain` specified for the suite:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project" domain="http://example.com">
    <test name="login-test">
        <collect>
            <login login="user"
                password="password"
                login-page="/login.html"
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

> Pass `login` and `password` as environment variables:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<suite name="test-suite" company="cognifide" project="project" domain="http://example.com">
    <test name="login-test">
        <collect>
            <login login="${LOGIN_EXAMPLE_ADMIN_PANEL}"
                password="${PASS_EXAMPLE_ADMIN_PANEL}"
                login-page="/login.html"
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