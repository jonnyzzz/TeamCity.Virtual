<<<<<<< HEAD
This is a IDEA project template to create a server-side and agent-side plugin for TeamCity
For background information checkout any open-sourced plugins or see http://confluence.jetbrains.net/display/TCD65/Developing+TeamCity+Plugins
 

In this sample you will find
=============================
- TeamCity server and agent plugin bindle
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration `server` to run/debug plugin under TeamCity (use `http://localhost:8111/bs`)
- pre-configured IDEA settings to support references to TeamCity
- Uses `$TeamCityDistribution$` IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API


What's next? 
=============
 - Fork this repository
 - Define `$TeamCityDistribution$` IDEA path variable with path to TeamCity home (unpacked `.tar.gz` or installed `.exe` distribution).
 - Add tomcat application server named `Tomcat 7` into IDEA settings from TeamCity distribution path
 - Change project name in IDEA
 - Update `teamcity-server-plugin.xml` to put plugin name, plugin display name and description
 - Rename `server/src/META-INF/build-server-plugin-PLUGIN_NAME.xml` to put your plugin name here and update `server/server.iml`
 - Rename `agent/src/META-INF/build-server-agent-PLUGIN_NAME.xml` to put your plugin name here and update `agent/agent.iml`
 - Update plugin .jar file name in `plugin` and `common-jar` artifacts
 - Update plugin .zip file name in `plugin-zip` artifact
 - Update test suite and tests with your plugin name
 - Have fun!


Steps to fork template to a given repository
===========================================
 - call git init or create new repo and local copy
 - git remote add template `git://github.com/jonnyzzz/TeamCity.PluginTemplate.git`
 - git fetch template
 - git merge template/serverAndAgent
 - git remote rm template

Those steps makes you repo contain default template indise. 
It's most easiest way to start.


License
=======
You may do what ever you like with those sources. 
or I could also say the license is MIT.
=======
simple-maven
============

This is simple possible ant script that could be used to fetch 
maven dependencies in Intellij IDEA based projects.

Scripts also contains simple helpers that could be used with TeamCity builds

License
=======
MIT


How to use
==========
Fork this repo and merge it into your repo
Complete ```fetch.xml``` with actual dependencies

The sctips also updates IDEA library files to include new-ly added librariries to IDEA project.
It's expected you'll no changed files on script re-run. If you do => post the issue with diff 

We use  [Maven Ant Tasks](http://maven.apache.org/ant-tasks/examples/dependencies.html)
inside. So `<dependency>` element under `<maven-fetch>` is the same
element as under Maven Ant Tasks
>>>>>>> deps/master
