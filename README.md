netmonitor logger and georeferencer
===================================

This package reads netmonitor pages from old Nokia phones in every seconds and logs to file. These logs can be georeferenced later using GPX track files and converted to input format for OpenCellID upload.

install
-------

Any suitable hardware and Linux distributions can be used. Configuration is designed to run on a Raspberry Pi using Raspbian operating system. Copy install directory to file system. Adjust .gammurc file and paths in scripts to your configuration. PHP script needs `php5-cli` package.

Gammu needs to be compiled from source for quicker read. Attached patch decreases sleep times. Original version can also be used if speed does not matter.

There are files for root user for easy shutdown by connecting GPIO pin 3 to GND.

Files named `crontab` in home/pi and root folders need to be installed using command `crontab crontab` issued by each users.

Tested with Raspberry Pi 2 and three Nokia 6150 phones.

Georeferencer is written in Java. It uses GPX parser classes from JOSM. Copy or symlink josm.jar into `dist/lib` directory before compiling with `ant` command:

    mkdir -p dist/lib
    ln -s /usr/share/josm/josm.jar dist/lib

Another way to adjust classpath in `build.xml`.
