## Database Structure

### Overview

Each AET project has its own database. The project database consists of 3 collections:

* Metadata,
* Artifacts (files + chunks GridFS collections).

### Metadata

The metadata collection holds the whole suite run with all necessary metadata e.g.:

```
{
    "suite": {
        "correlationId": "cognifide-example-1234567890",
        "project": "example",
        "name": "integration",
        "version": 2,
        "tests": [
            {
                "name": "example-desktop",
                "comment": "Some test-level comment",
                "urls": [
                    {
                        "name": "unique-url-name",
                        "caseUrl": "/authors.html",
                        "canonicalUrl": "https://cognifide.com/authors.html",
                        "domain": "https://cognifide.com",
                        "comment": "Some url-level comment",
                        "useProxy": "rest",
                        "steps": [
                            {
                                "header": {
                                    "parameters": {
                                        "key": "Authorization",
                                        "value": "Basic emVuT2FyZXVuOnozbkdAckQZbiE="
                                    }
                                }
                            },
                            {
                                "open": { }
                            },
                            {
                                "screen": {
                                    "name": "desktop",
                                    "parameters": {
                                        "width": 1280,
                                        "height": 1024
                                    },
                                    "pattern": "568b54a4e63e740bfc2210e5",
                                    "result": {
                                        "artifact": "bb8b54a4e63e740bfc2210e8",
                                        "status": "OK"
                                    },
                                    "comparators": [
                                        "layout:" {
                                            "result": {
                                                "artifact": "bb8b54a4e63e740bfc2210x5",
                                                "status": {
                                                    "status": "SUCCESS",
                                                    currentCreateDate: "Mar 2, 2016 2:30:36 PM",
                                                    patternCreateDate: "Mar 2, 2016 2:30:36 PM"
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "screen": {
                                    "name": "desktop",
                                    "parameters": {
                                        "width": 768,
                                        "height": 1024
                                    },
                                    "pattern": "568b54a4e63e740bfc2210e5",
                                    "result": {
                                        "artifact": "aa8b54a4e63e740bfc2210e5",
                                        "status": "OK"
                                    },
                                    "comparators": [
                                        "layout:" {
                                            "result": {
                                                "artifact": "bb8b54a4e63e740bfc2210e5",
                                                "status": {
                                                    "status": "SUCCESS",
                                                    currentCreateDate: "Mar 2, 2016 2:30:36 PM",
                                                    patternCreateDate: "Mar 2, 2016 2:30:36 PM"
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "screen": {
                                    "name": "mobile",
                                    "parameters": {
                                        "width": 320,
                                        "height": 480
                                    },
                                    "pattern": "568b54a4e63e740bfc2210e6",
                                    "result": {
                                        "artifact": "aa8b54a4e63e740bfc2210e6",
                                        "status": "OK"
                                    },
                                    "comparators": [
                                        "layout:" {
                                            "result": {
                                                "artifact": "bb8b54a4e63e740bfc2210e5",
                                                "status": {
                                                    "status": "SUCCESS",
                                                    currentCreateDate: "Mar 2, 2016 2:30:36 PM",
                                                    patternCreateDate: "Mar 2, 2016 2:30:36 PM"
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "status-codes": {
                                    "comparators": [
                                        "status-codes": {
                                            "parameters": {
                                                "filterRange": "400,600"
                                            },
                                            "result": {
                                                "artifact": "bb8b54axxxxe740bfc2210e5",
                                                "status": {
                                                    "status": "SUCCESS"
                                                }
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "js-errors": {
                                    "comparators": [
                                        "js-errors": {
                                            "dataFilters": [
                                                "js-errors-filter": {
                                                    "source": "http://w.iplsc.com/external/jquery/jquery-1.8.3.js",
                                                    "line": "2"
                                                },
                                                "result": {
                                                    "artifact": "aaab54axxxxe740bfc2210e5",
                                                    "status": {
                                                        "status": "SUCCESS"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}
```

Each step defined in the original suite is stored in this collection with all the information necessary to run it.

![aet-data-model](assets/diagrams/aet-data-model.png)

**Metadata** is a simple MongoDB collection. It contains keys (objectId) of GridFS artifacts stored in the **Artifacts** collection.

![aet-mongo-collections](assets/diagrams/aet-mongo-collections.png)

Metadata is persisted after a full suite run. At the beginning of processing, pattern keys are fetched from the last run if it was performed.

![aet-suite-storage](assets/diagrams/aet-suite-storage.png)

### Artifacts

Artifacts is a GridFS collection (it consists of two collections: .files and .chunks). It holds binary 
artifacts (e.g. screenshots, source etc.). Each file has its ObjectId which is a reference to it. 
This collection has no knowledge of its context, artifacts are not marked as pattern/result etc.
