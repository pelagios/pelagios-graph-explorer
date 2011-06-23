# PELAGIOS Data Visualization Toolkit

Welcome to the future home of the PELAGIOS Data Visualization Toolkit!

Visit http://pelagios-project.blogspot.com for information about the
PELAGIOS project. 

## Build Instructions

The PELAGIOS Data Visualization Toolkit is built with [Gradle]
(http://www.gradle.org). Run

``gradle eclipse`` 

to generate an eclipse project. (Note: dependency download may take a 
while.)

## Notes

In order to run the unit tests you need a set of CSV dump files
of the [Pleiades] (http://pleiades.stoa.org) gazetteer. These files
are not included in this project to save repository space. 

Please obtain recent versions of the *pleiades-locations-YYYYMMDD.csv.gz*
and *pleiades-names-YYYMMDD.csv.gz* CSV dumps from 

http://atlantides.org/downloads/pleiades/dumps/

Unzip them and place them in the /src/test/resources folder. Note: presently
you also need to update the file names in the unit test source code. This
is only a temporary issue, though.