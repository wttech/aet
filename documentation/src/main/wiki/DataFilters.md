### Data Filters

Data filters are modules which narrow down the area which the comparison will be performed on.

They are nested in [[Comparators]] and apply only to the instance of the comparator which they are defined in.

Each data filter consists of two elements:
* module name,
* parameters.

##### Module name

This name is a unique identifier for each data filter (and each module in the compare phase).

##### Parameters

This is a set of key-value pairs the user can make use of to pass some configuration and information to the data filter. Parameters can be divided into two groups:
* mandatory - parameters which filtering will be not possible without,
* optional - passing this parameter is not obligatory, usually it triggers some functionality extension.
