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

We use  [Maven Ant Tasks](http://maven.apache.org/ant-tasks/examples/dependencies.html)
inside. So `<dependency>` element under `<maven-fetch>` is the same
element as under Maven Ant Tasks