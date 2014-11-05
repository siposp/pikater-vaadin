
This project extends the [Pikater](https://github.com/peskk3am/pikater4) project with multi-user support, web based GUI and distributed infrastructure to allow simultaneous computation of many individual tasks.

## Main features

Client:
* 2D editor to define experiments (boxes and edges style).
* Potentially many experiments may be scheduled to execution.
* Displaying the experiment results and converting/downloading them into a human-readable format, such as CSV.
* Uploading custom data sets and computation methods.

Server:
* Many features of the original Pikater project, such as computation method recommendation.
* Experiments planning and execution.
* Saving of trained models which can then be used in further experiments.
* Administrator functions, such as supervision of all scheduled experiments.

## Life-cycle

### Installation

First and foremost, note several things:

1. The repository has several branches. Two of them are important:
	* `master` - contains the latest core system version.
	* `vaadin` - contains the latest GUI (web application) and documentation versions.
2. This project requires:
    * Java version 7,
	* a JPA-compatible database (at this moment, only PostgreSQL database is supported) running locally or externally,
	* a SMTP server open for local connections, running on the same machine as pikater core.

#### Database<a name="dbInstall"></a>

TODO: this should all be covered in the original pikater project

1. Install PostgreSQL database. Pikater was developed for version 9.3 and we highly recommended using it. Either use package managers or visit the [official download site](http://www.postgresql.org/download).
2. Create a new database with a name of your desire, UTF-8 encoding is recommended.
3. Clone the project.
4. Change the following files accordingly:
    * `Pikater/src/beans.xml`
    * `Pikater/src/META-INF/persistence.xml`
5. Use the `Pikater/src/org.pikater.shared.database.util.initialisation.DatabaseInitialisation` utility to generate the configuration files as well as to create the first administrator account.

#### Core system

Clone the `master` branch on target machine.

#### Extension

Import the project in Eclipse:

1. Install [Eclipse](https://www.eclipse.org/downloads/).
2. Install the [Vaadin plugin for Eclipse](http://vaadin.com/eclipse).
3. Clone the `vaadin` branch on target machine.
4. Import the cloned project in Eclipse (`File->Import`).
5. Reconfigure the linked `Apache Tomcat v7.0` server environment if needed.
6. Compile the `org.pikater.web.vaadin.gui.PikaterWidgetset.gwt.xml` widgetset using the plugin installed in step 2.
7. Inspect/edit the `WebContent/WEB-INF/web.xml` file. It is used to configure the web application. Detailed information can be found [here](wiki/technical/web/Web-documentation#conf).

### Deployment & launching

#### Database

Make it publicly available to the machines you wish to run core system or extension on.

#### Core system

Deployment was already done in installation.  
To launch the core system, use the `Pikater/Launch-core.launch` launch configuration.

#### Extension

The extension requires an instance of core system running locally so install and launch it first.

To deploy and launch the extension:

1. Start the servlet container.
2. Export the extension into a `.war` file from Eclipse, deploy it into the servlet container.
3. Start the deployed application.
4. Access the page, e.g. go to `http://localhost:8080/Pikater` (for Apache Tomcat; by default) or `http://my.domain/Pikater`. A login dialog will appear.
5. Enter credentials you defined in step 4 of [database installation](#dbInstall) or another of your accounts and start using. See [user guide](wiki/user-guide/User-guide.md).

### Maintenance

If the extension is used, one or more administrators may need to manage it using the GUI. See [admin guide](wiki/guide-admin/Admin-guide.md).

Otherwise, the application should be self-maintained.




## Wiki

This project contains inline documentation written in markdown, easily available as a wiki.

### Installation

[How to install](wiki/guide_admin/wiki/01-Installation.md)

### Launching

[How to launch](wiki/guide_admin/wiki/02-Launching.md)

### Usage

[How to use](wiki/guide_admin/wiki/03-Usage.md)




## Documentation<a name="docs"/>

[Open documentation overview](wiki/Overview.md)
