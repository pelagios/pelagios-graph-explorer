# PELAGIOS Graph Explorer

Welcome to the future home of the PELAGIOS Graph Explorer!

Visit http://pelagios-project.blogspot.com for information about the
PELAGIOS project.

## What is the PELAGIOS Graph Explorer?

todo... 

![PELAGIOS Graph Explorer - Dev Screenshot](http://dl.dropbox.com/u/6192626/graph-explorer.jpg)

## License

The PELAGIOS Graph Explorer is licensed under the 
[GNU General Public License v3.0] (http://www.gnu.org/licenses/gpl.html).

## Getting Started

The PELAGIOS Graph Explorer is built with [Gradle]
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

__Notes to developers:__ before running the project the first time, 
you will need to initialize the embedded [Neo4j] (http://www.neo4j.org)
graph database with a set of PELAGIOS data dumps. This is done by executing
the 

``org.pelagios.bootstrap.InitDatabase`` 

class, located in the */src/test/java* folder. A set of sample dumps will
be included in the project soon. In the mean time, please contact [us] 
(http://pelagios-project.blogspot.com) directly. 

The project comes with an included starter class that will launch the 
application demo in an embedded Jetty Web server. Please 
ensure that your IDE compiles the application classes to the 
*/src/main/webapp/WEB-INF/classes* directory (if you use Eclipse, the
gradle script will handle this for you). Locate the class

``org.pelagios.bootstrap.StartGraphExplorer``

in the */src/test/java* folder, and launch it as a Java application. Per
default, Jetty will launch on port 8080. The PELAGIOS Data Visualization
Demo will be available at

http://localhost:8080/pelagios

Modify the settings in the starter class if you want a different port
or application root path.