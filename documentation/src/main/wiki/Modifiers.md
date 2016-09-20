### Modifiers

Modifier is module which performs particular modification on data before collection happens.

Each modifier consists of two elements:
* module name,
* parameters.

##### Module name
This name is unique identifier for each modifier (and each module in collect phase).

##### Parameters
This is set of key-value pairs using which user can pass some configuration and information to modifier. Parameters for modifiers can be divided into two groups:
* mandatory - parameters without which modification will not be possible,
* optional - passing this parameter is not obligatory, usually they trigger some functionality extensions.
