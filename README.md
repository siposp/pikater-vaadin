
This project (hence called **"the extension"**) extends the [Pikater core](https://github.com/sbalcar/pikater) project (hence called **"the parent"**) with a web based GUI and multi-user support.
The extension contains the parent as a submodule for easier integration.

## Main features

* All of parent's features.
* Administrative functions such as supervision of all scheduled experiments.
* Uploading custom datasets and computation methods for experiments.
* 2D editor to define experiments (boxes-and-edges style).
* Experiments may be queued for execution at any time, in any number.
* Saving of trained models which can then be used in future experiments.
* Viewing/comparing experiment results and converting/downloading them into a human-readable format, such as CSV.

## Life-cycle

### Requirements

Take a look at requirements of the parent.

The extension requires:
1. a parent instance,
2. a servlet container (e.g. Apache Tomcat),
3. both of which need to be running on the same machine.

### Installation & deployment

1. Install the parent.
2. Install your servlet container.
3. Clone the extension and import it in Eclipse (`File->Import`).
5. Install the [Vaadin plugin for Eclipse](http://vaadin.com/eclipse), if not done yet.
6. Resolve Ivy dependencies for the extension.
7. Compile the `org.pikater.web.vaadin.gui.PikaterWidgetset.gwt.xml` widgetset using the plugin installed in step 5.
8. Inspect/edit the `WebContent/WEB-INF/web.xml` file. It is used to configure the web application. Detailed information can be found [here](wiki/technical/web/Web-documentation#conf).
9. Launch the parent.
9. Bundle the extension into a `.war` file and deploy it into the servlet container.
10. Go to `http://{my.domain}:{my.port}/pikater-vaadin`, you will be asked for credentials. Use the first administrator account you generated when installing the parent (and database).

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
