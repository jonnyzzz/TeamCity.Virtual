Docker/Vargant build runners for TeamCity

Features
========

Plugin detects and reports installed ```vagrant``` and ```docker```

Use the dedicated build runner to run your build script under virtualized environment
with help of ```Docker/Vagrant``` build runner


License
==========
Apache 2.0

see LICENSE.txt for details


Downloading Build
=================

Download the latest build from [TeamCity](http://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityVirtual_Build&guest=1)

Installation
============

TeamCity Server and TeamCity Build Agents are required to be runnung under JRE 1.7+

To install plugin, put downloaded plugin `.zip` file into `<TeamCity Data Directory>/plugins` folder and restart TeamCity Server.

Make sure downloaded `.zip` file is not corrupted and is not sources `.zip` from GitHub.

For more details, there is [documentation](http://confluence.jetbrains.net/display/TCD7/Installing+Additional+Plugins)


Supported Versions
==================

Plugin is tested to work with TeamCity 8.1.
It should work with 8.0 (and maybe 7.1.x)

Agent and server are expected to run JRE 1.7


Building
========
  - call ```ant -f fetch.xml fetch```
  - open the project in IntelliJ IDEA 13.1
  - make all artifacts


In this repo you will find
=============================
- TeamCity server and agent plugin bundle
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration `server` to run/debug plugin under TeamCity (use `http://localhost:8111/bs`)
- pre-configured IDEA settings to support references to TeamCity
- Uses `$TeamCityDistribution$` IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API

For details see https://github.com/jonnyzzz/TeamCity.PluginTemplate

Dependencies fetch via simple-maven
===================================

This is simple possible ant script that could be used to fetch 
maven dependencies in Intellij IDEA based projects.

Call ```fetch.xml``` to fetch all missing dependencies

The script also updates IDEA library files to include new-ly added libraries to IDEA project.
It's expected you'll no changed files on script re-run. If you do => post the issue with diff 

We use  [Maven Ant Tasks](http://maven.apache.org/ant-tasks/examples/dependencies.html)
inside. So `<dependency>` element under `<maven-fetch>` is the same
element as under Maven Ant Tasks

For details see https://github.com/jonnyzzz/intellij-ant-maven


Notice
======

Some code of this plugin was borrowed from [NuGet plugin](https://github.com/JetBrains/teamcity-nuget-support/)
which is licensed under Apache 2.0

Note
====

This plugin was created with [https://github.com/jonnyzzz/TeamCity.PluginTemplate](TeamCity Plugin Template)

This is my (Eugene Petrenko) private home project

You may support my home projects:
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y94ALDEKVZT3Y)


