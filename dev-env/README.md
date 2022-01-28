# Dev AET instance with Docker Swarm
This directory contains Docker Swarm configuration that enables running AET developer environment. It base on AET Docker images from https://github.com/malaskowski/aet-docker.

Make sure you have installed:
- Java 8 (newer JDK versions are not supported)
- Maven or Gradle
- Docker
- shell command line (Linux preffered)

## Running dev environment
In order to be able to easily deploy AET artifacts on your docker instance follow these steps:

1. Make sure your Docker instance meets the [prerequisites](https://github.com/malaskowski/aet-docker#prerequisites).
2. Checkout this repository.
3. Prepare the instance (TODO: this step can be automated and wrapped with a single Gralde task):
  - From the root folder of this repo run `./gradlew :zip:makeZip` or `mvn clean install -Pzip` to build the whole AET project and all its artifacts. It may take some time when running for the first time.
  - Copy AET `bundles` and `features` artifacts from `zip` directory to the corresponding dev-environment directories.
  - Hint: Until it is automated in a better way, you may use this command to copy neccessary artifacts (run from the repo root dir) depending on build tool:
    - Maven: 
      - `rm dev-env/bundles/*.jar && cp zip/target/*.jar dev-env/bundles`
      - `rm dev-env/features/*.xml && cp zip/target/aet-*.xml dev-env/features`
    - Gradle:
      - `rm dev-env/bundles/*.jar && cp zip/build/packages/bundles/*.jar dev-env/bundles`
      - `rm dev-env/features/*.xml && cp zip/build/packages/features/aet-*.xml dev-env/features`
4. From `dev-environment` directory run `docker stack deploy -c aet-swarm-dev.yml aet-dev`. It may take ~1-2 minutes until instance is ready to work.

## Development

### AET Core (bundles)
This scenario shows how to update your AET Docker Swarm dev instance with changes made to any of AET bundles that runs on Karaf.

1. Hack, hack, hack.
2. Build the module you updated, e.g. `core/jobs`.
3. Rename the `jar` file so that it does not contain the version and replace existing `jar` in `dev-environment/bundles`. Karaf should auto-discover changes and reload the bundle in a short period.
  - If you changed configuration, don't forget to update corresponding `.cfg` file in the `configs` directory.
  - If you added new dependencies remember to update `osgi-dependencies/aet-features.xml` and updating this file in `dev-den/features` so that Karaf can download dependencies for your bundle.
  - Avoid duplicated bundles (hence the renaming of single module jar). All jar files are renamed by the build tools when building the distribution (`zip` module).

### Report
- ToDo

## Troubleshooting
See https://github.com/malaskowski/aet-docker#troubleshooting

## Directory structure
```
.
├── aet-swarm-dev.yml
├── bundles
│   ├── aet-module-1.jar
│   ├── ...
│   └── aet-module-9.jar
├── configs
│   ├── com.cognifide.aet.cleaner.CleanerScheduler-main.cfg
│   └── ...
├── features
│   └── aet-features.xml
├── report
└── secrets
    └── KARAF_EXAMPLE_SECRET
```

- `aet-swarm-dev.yml` - this file contains configuration file to run AET using Docker Swarm
- `bundles` - directory mounted to the `/aet/core/bundles` in the Karaf service, where Karaf search for AET [OSGi bundles](https://en.wikipedia.org/wiki/OSGi#Bundles) (see [Karaf's Dockerfile](https://github.com/malaskowski/aet-docker/blob/master/karaf/Dockerfile))
- `configs` - directory mounted to the `/aet/custom/configs` in the Karaf service, contains OSGi configs in form of `.cfg` files
- `features` - directory mounted to the `/aet/core/features` in the Karaf service, contains [Karaf provisioning](https://karaf.apache.org/manual/latest/provisioning) configuration files - called features
- `report` - directory that may contain AET report application, if mounted to `/usr/local/apache2/htdocs` volume in the Report service, it will override default [AET Report application](https://github.com/wttech/aet/tree/master/report)
- `secrets` - directory contains example [Docker secret](https://docs.docker.com/engine/swarm/secrets/) files. They are scanned before Karaf starts and exported as environment variables. Read more in the [secrets configuration](https://github.com/malaskowski/aet-docker#docker-secrets).

## Karaf healthcheck
Karaf's service in this sample docker instance have [healthcheck](https://docs.docker.com/compose/compose-file/#healthcheck). It simply checks the dedicated service's endpoint `/health-check` that responses with `200` when everything is ready, with error code otherwise. If the healthcheck fails, swarm will automatically restart the service.
Read more about this endpoint here: https://fabric8.io/guide/karaf.html#fabric8-karaf-health-checks

# ToDo:
- [ ] Improve building and deploying to dev instance experience by automating it (e.g. wrap with a single Gradle task that can deploy AET artifacts onto dev-environment).
- [ ] Fix AET versioning with Gradle, currently the version is magically calculated by the [axion-release-plugin](https://axion-release-plugin.readthedocs.io/en/latest) and hardcoded in some manifests (e.g. [here](https://github.com/wttech/aet/blob/master/api/communication-api/build.gradle.kts#L28)).