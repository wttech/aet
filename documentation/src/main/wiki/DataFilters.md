### Data Filters

Data filters are modules which narrow area on which comparison will be performed.

They are nested in [[Comparators]] and apply only to instance of comparator in which they are defined.

Each data filter consists of two elements:
* module name,
* parameters.

##### Module name

This name is unique identifier for each data filter (and each module in compare phase).

##### Parameters

This is set of key-value pairs using which user can pass some configuration and information to data filter. Parameters can be divided into two groups:

* mandatory - parameters without which filtering will be not possible,
* optional - passing this parameter is not obligatory, usually they trigger some functionality extension.
