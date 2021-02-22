![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/wttech/aet/blob/master/misc/img/aet-logo-blue.png?raw=true"
         alt="AET Logo"/>
</p>

## Zip
This module purpose is to package all AET artifacts into zip archives.
After installing all AET modules in local repository type in the root dir:

- maven:
  ```
  mvn clean install -Pzip
  ```
- gradle
  ```
  ./gradlew # or ./gradlew :zip:make
  ```
    
Please find archives containing all AET artifacts in
`zip/target/packages-${project.version}` for maven and `zip/build/packages-${prpject.version}` for gradle

You can use maven upload this archive to remote location with:

    mvn clean install -Pzip,upload          \
        -Dtarget.zip.ServerId=remote-location   \
        -Dtarget.zip.url=scp://192.168.123.100/home/user/release
provided you have valid credentials for server with id `remote-location` in your `settings.xml` file.
