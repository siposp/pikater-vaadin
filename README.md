
This project extends the [Pikater core](https://github.com/sbalcar/pikater) project with a web based GUI and multi-user support.

## Main features

* All features of the parent project.
* Administrative functions such as supervision of all scheduled experiments.
* Uploading custom datasets and computation methods for experiments.
* 2D editor to define experiments (boxes-and-edges style).
* Experiments may be queued for execution at any time, in any number.
* Saving of trained models which can then be used in future experiments.
* Viewing/comparing experiment results and converting/downloading them into a human-readable format, such as CSV.

## Life-cycle

### Requirements

Take a look at requirements of the parent project.

This extension requires:
1. An instance of the parent project, running locally on the same machine as the extension.
2. A running servlet container (e.g. Apache Tomcat), again on the same machine.

### Installation & deployment

1. Install the parent project.
2. Install your servlet container.
3. Clone both the parent project and extension into the same folder.
4. Import the extension project in Eclipse (`File->Import`).
5. Install the [Vaadin plugin for Eclipse](http://vaadin.com/eclipse), if not done yet.
6. Resolve Ivy dependencies for the extension.
7. Compile the `org.pikater.web.vaadin.gui.PikaterWidgetset.gwt.xml` widgetset using the plugin installed in step 5.
8. Inspect/edit the `WebContent/WEB-INF/web.xml` file. It is used to configure the web application. Detailed information can be found [here](wiki/technical/web/Web-documentation#conf).
9. Bundle the extension into a ".war" file and deploy it into the servlet container (with the parent project already running).
10. Go to `http://{my.domain}:{my.port}/pikater-vaadin`, you will be asked for credentials. Use the first administrator account you generated when installing the parent project and database.

### Maintenance

One or more administrators may need to manage the extension from time to time. See [admin guide](wiki/guide-admin/Admin-guide.md).

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
