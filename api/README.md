![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## API Module
Module contains Application Program Interfaces that are shared with application modules.

### communication-api
Contains communication models e.g. `Suite`, `Test`, `Step` and other structures used for communication between AET modules.

### datastorage-api
Contains API for AET datastorage, e.g. DAO interfaces.

### jobs-api
Contains interface abstraction for AET jobs, e.g. `DataFilter`, `CollectorJob`, `ComparatorJob`. All modules containing jobs implementations (e.g. `jobs`) depends on this one. 

### validation-api
Contains suite validation interfaces.
