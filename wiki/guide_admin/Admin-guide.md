<!-- --- title: Administrator's handbook -->

[[_TOC_]]

## Life-cycle

The project's GitHub repository can be found at https://github.com/krajj7/pikater.

### Installation

First and foremost, note several things:

1. The repository has several branches. Two of them are important:
	* `master` - contains the latest core system version.
	* `Eclipse-Vaadin-project` - contains the latest GUI (web application) and documentation versions.
2. This project requires:
    * Java version 7,
	* a JPA-compatible database (at this moment, only PostgreSQL database is supported) running locally or externally,
	* a SMTP server open for local connections.

#### Database<a name="dbInstall"></a>

1. Install PostgreSQL database, that can be downloaded from the address of [PostgreSQL download site](http://www.postgresql.org/download/) or use package manager of your system. Pikater was tested with version 9.3 and it is highly recommended to use this version.
2. Create a new database with a name of your desire. Using UTF-8 character encoding is recommended.
3. When you clone the project on your machines in later steps, change the following files accordingly to your fresh database install:
    * `src/beans.xml`
    * `src/META-INF/persistence.xml`
4. After cloning the project you can use the `org.pikater.shared.database.util.initialisation.DatabaseInitialisation` utility to generate the configuration files as well as to create the first administrator account with the credentials you provide.

#### Core system

Clone the `master` branch on target machine.

#### Extension

Import the project in Eclipse:

1. Install [Eclipse](https://www.eclipse.org/downloads/).
2. Install the [Vaadin plugin for Eclipse](http://vaadin.com/eclipse).
3. Clone the `Eclipse-Vaadin-project` branch on target machine.
4. Create a new Vaadin project, as specified [here](https://vaadin.com/book/-/page/getting-started.first-project.html). **You must name it `Pikater-Extension` and use Vaadin version `7.1.14`**.
5. Overwrite the created project with the cloned pikater branch.
6. Check your project's Java binding - it might have been corrupted in the last step. Check the `.classpath` file.
5. Compile the `org.pikater.web.vaadin.gui.PikaterWidgetset.gwt.xml` widgetset.
6. Inspect/edit the `WebContent/WEB-INF/web.xml` file. It is used to configure the web application. Detailed information can be found [[here|Web-documentation#conf]].
7. Install and prepare a servlet container (e.g. Apache Tomcat).

### Deployment & launching

#### Database

Make it publicly available to the machines you wish to run core system or extension on.

#### Core system

Deployment was already done in installation.  
To launch the core system, execute the `Launch-core.launch` configuration.

#### Extension

The extension requires an instance of core system running locally so install and launch it first.

To deploy and launch the extension:

1. Start the servlet container.
2. Export the extension into a `.war` file from Eclipse, deploy it into the servlet container.
3. Start the deployed application.
4. Access the page, e.g. go to `http://localhost:8080/Pikater` (for Apache Tomcat; by default) or `http://my.domain/Pikater`. A login dialog will appear.
5. Enter credentials you defined in step 4 of [database installation](#dbInstall) or another of your accounts and start using. See [[user guide|User-guide]].

### Maintenance

To see a list of what administrators can (and should) do within the GUI, refer to [[web application documentation|Web-documentation#adminFeatures]].
To learn how these features can be exercised, refer to [[user guide|User-guide]], particularly to [[default page|User-guide#defaultPage]].

## Wiki

This project contains inline documentation written in markdown, easily available as a wiki.

### Installation

1. Clone the `Eclipse-Vaadin-project` branch.
2. [[How to install|docs/guide_admin/wiki/01-Installation]]

### Launching

[[How to launch|docs/guide_admin/wiki/02-Launching]]

### Usage

[[How to use|docs/guide_admin/wiki/03-Usage]]

## Logging

The application doesn't use unified logging technology, implementation or interface at this moment. Individual application components use their own. They are all defined in the `org.pikater.shared.logging` package. Each application component has its own subpackage defined.

An attempt has been made to centralize logging into the servlet container but has not been fully implemented/used after all. For more information, refer to [[default package description|Default-package-description]].
