# PELAGIOS Data Visualization Demonstrator

Welcome to the future home of the PELAGIOS Data Visualization Demonstrator!

Visit http://pelagios-project.blogspot.com for information about the
PELAGIOS project. 

## Getting Started

The PELAGIOS Data Visualization Demonstrator is built with [Gradle]
(http://www.gradle.org). (Download and installation instructions for
Gradle are [here] (http://www.gradle.org/installation.html)). To test
and compile the project, run

``gradle build``

To generate the Web application archive (.war) file, run

``gradle war``

To generate an Eclipse project, run

``gradle eclipse``

(Note: dependency download may take a while the first time you
are building the project.)

A note to developers: the project comes with an included starter class that 
will launch the application demo in an embedded Jetty Web server. Please 
ensure that your IDE compiles the application classes to the 
*/src/main/webapp/WEB-INF/classes* directory (if you use Eclipse, the
gradle script will handle this for you). Locate the class

``org.pelagios.bootstrap.StartServer``

in the */src/main/test* folder, and launch it as a Java application. Per
default, Jetty will launch on port 8080. The PELAGIOS Data Visualization
Demo will be available at

http://localhost:8080/pelagios

Modify the settings in the starter class if you want a different port
or application root path.

## Notes

In order to run the unit tests you need a set of CSV dump files
of the [Pleiades] (http://pleiades.stoa.org) gazetteer. These files
are __not__ included in this project to save repository space. 

Please obtain recent versions of the *pleiades-locations-YYYYMMDD.csv.gz*
and *pleiades-names-YYYMMDD.csv.gz* CSV dumps from 

http://atlantides.org/downloads/pleiades/dumps/

Unzip them and place them in the /src/test/resources folder. Note: presently
you also need to update the file names in the unit test source code. This
is only a temporary issue, though.

## Future Work

Split this (now rather monolithic) demo up into (more re-usable) sub-projects.
E.g. a visualization frontend, a graph backend (including dataset importers),
and a Web API project.