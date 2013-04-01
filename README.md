Subversion to Git converter [![Build Status](https://travis-ci.org/frankbille/svn2git.png?branch=master)](https://travis-ci.org/frankbille/svn2git)
===========================


WORK IN PROGRESS
----------------

This software is in early alpha phase. It is possible to do a simple conversion,
but it there are still a lot of features missing.

To use you have two options:

1. Download the latest binary, which is built everytime a push is made:  
   http://github-files.s3.amazonaws.com/svn2git/svn2git-gui/target/svn2git.jar
   
   Run application  
   ```java -jar svn2git.jar```

2. Checkout the source code, build using [Maven][maven] and run the built artifact
   1. Checkout the code  
      ```git clone https://github.com/frankbille/svn2git.git```
   2. Go into the checked out folder  
      ```cd svn2git```
   3. Build project using [Maven][maven]  
      ```mvn package```
   4. Run application  
      ```java -jar svn2git-gui/target/svn2git.jar```


Licenses and attributions
-------------------------

* SVNKit - [The TMate License][tmate]
* SnakeYAML - [Apache Software License 2.0][asl]
* Apache Commons IO - [Apache Software License 2.0][asl]
* Apache Commons Lang - [Apache Software License 2.0][asl]
* JGoodies Forms - [BSD License][bsd]
* Some icons by [Yusuke Kamiyamane][fugue]. Licensed under a [Creative Commons Attribution 3.0 License][cca].

[maven]: http://maven.apache.org
[tmate]: http://svnkit.com/license.html
[asl]: http://www.apache.org/licenses/LICENSE-2.0.html
[bsd]: http://www.opensource.org/licenses/bsd-license.html
[fugue]: http://p.yusukekamiyamane.com/
[cca]: http://creativecommons.org/licenses/by/3.0/
