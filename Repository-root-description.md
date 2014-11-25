<!-- --- title: Repository description -->

## Source code

The `src` folder.

## Compiled code

The `build` folder.

## Application dependencies

The `ivy.xml` file ([Apache Ivy](http://ant.apache.org/ivy) dependency  management).

## Web application resources

They are contained in the `WebContent` folder.  
Resources are divided into several categories:

1. `WebContent/VAADIN/widgetsets`  
Client side web application compiled into Javascript.
2. `WebContent/VAADIN/themes`  
Web application resources - CSS, images, HTML code snippets (layout templates) and so on. In reality, this folder does not contain all used/referenced CSS and Javascript. Some of it is defined in the `src` folder, inline with the Vaadin components that use them, for simplicity. Vaadin then takes responsibility for publishing them (along with theme resources) to the above compilation folder.
3. `WebContent/WEB-INF`  
Standard web application folder where configuration and java resources for the application reside. In other words, files and folders that are not accessible from the client.  
Configuration of the web application is documented [[here|Web-documentation]]. Furthermore, the folder contains some `.pxm` files. They are the source files for some of the application's images/icons and were created by the [PixelMator](http://www.pixelmator.com) application for Max OS X.

Note, that the `ivy.xml` file mentioned above is also a web application resource (specific to Vaadin).

## Documentation

This project contains an inline documentation written in markdown and easily available as a wiki. For more information, check the [[technical documentation|Technical-documentation]].  

### MarkDown files

Home page is represented by the `Home.md` file. From there, links can be followed to other documentation files, most of which are contained in the `wiki` folder and a few others are located in the `src` folder.

### Sonar code analysis

Uses the `sonar-project.properties` configuration file.

Our compliments to [SonarQube](http://www.sonarqube.org/) to make this happen. Installation, usage and documentation can be found in the provided link.

### Javadoc

The repository contains Javadoc generated to all source code in the `src` folder and is located in the `wiki/javadoc` folder. MarkDown files link to it as well.

### Wiki

Several other files in the repository's root are related to the aforementioned Wiki. Namely:

1. `custom.css`  
Contains custom CSS styles that override the Wiki's default styles to make it a bit nicer to work with.
2. `custom.js`  
Contains custom Javascript that enables support for videos.
3. `wiki-config.rb`  
Contains configuration for the wiki.
4. `wiki-launch.sh`  
Startup script for the wiki. See [[how to launch wiki|02-Launching]].