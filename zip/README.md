![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Zip

This module purpose is to package all AET artifacts into zip archives.
After installing all AET modules in local repository type:

    mvn clean install -Pzip
Please find archives containing all AET artifacts in
`zip/target/packages-${project.version}` folder

You could even upload this archive to remote location with:

    mvn clean install -Pzip,upload          \
        -Dtarget.zip.ServerId=remote-location   \
        -Dtarget.zip.url=scp://192.168.123.100/home/user/release
provided you have valid credentials for server with id `remote-location` in your `settings.xml` file.
