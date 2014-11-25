<!-- --- title: GitHub overview -->

[[_TOC_]]

This project extends the [pikater-core](https://github.com/sbalcar/pikater) project with multi-user support, web based GUI and distributed infrastructure to allow simultaneous computation of many individual tasks.

As such, this application consists of 3 main parts:

1. **Pikater-core**  
Although it has been rewritten to support the extension, it still works as a standalone unit.
2. **Database framework**
3. **Web application extension**

The project also contains an inline documentation written in markdown, easily available as a wiki.
More information on each of these "components" can be found in the [documentation](#docs).




## Main features

Client:
* 2D editor to define experiments (boxes and edges style).
* Potentially many experiments may be scheduled to execution.
* Displaying the experiment results and converting/downloading them into a human-readable format, such as CSV.
* Uploading custom data sets and computation methods.

Server:
* Many features of pikater-core, such as computation method recommendation.
* Experiments planning and execution.
* Saving of trained models which can then be used in further experiments.
* Administrator functions, such as supervision of all scheduled experiments.

## Requirements

Check requirements of pikater-core. Additionally, this extension requires:
* A servlet container, e.g. Apache Tomcat.




## Life-cycle

### Installation

1. Follow installation instructions of pikater-core.
2. Install [Eclipse](https://www.eclipse.org/downloads/).
3. Install the [Vaadin plugin for Eclipse](http://vaadin.com/eclipse).
4. Clone pikater-core.
5. Clone pikater-vaadin.
6. Import both projects into Eclipse (File->Import->General->Existing projects into Workspace). This project requires pikater-core to be imported into Eclipse as well, don't change its name.
7. Resolve Ivy dependencies for pikater-core first, and then for pikater-vaadin.
8. Check integrity of pikater-vaadin's classpath - JRE binding, JUnit system libraries, etc.
9. Compile the `org.pikater.web.vaadin.gui.PikaterWidgetset.gwt.xml` widgetset. Details can be found [here](https://vaadin.com/book/vaadin7/-/page/gwt.eclipse.html).
10. Inspect/edit the `WebContent/WEB-INF/web.xml` file. It is used to configure the web application. Detailed information can be found [[here|Web-documentation#conf]].
11. Install and prepare your servlet container.

### Deployment & launching

1. Database needs to be publicly available to the machines you wish to run pikater-core and pikater-vaadin on.
2. Follow deployment & launch instructions of pikater-core.
3. Start the servlet container.
4. Bundle pikater-vaadin into a `.war` file. Eclipse: File->Export->Web->WAR file.
5. Deploy it into the servlet container.
6. Start the deployed application if not done automatically by the servlet container.
7. Go to `http://{my.domain:port}/pikater-vaadin`. A login dialog will appear.
8. Enter credentials you provided when initializing database in pikater-core's installation instructions.
9. Enjoy. See [[user guide|User-guide]].

### Maintenance

One or more administrators may need to manage pikater-vaadin using the GUI. See [[admin guide|Admin-guide]].

Other than that, the application should be self-maintained.




## Wiki

This project contains inline documentation written in markdown, easily available as a wiki.

### Installation

[[How to install|01-Installation]]

### Launching

[[How to launch|02-Launching]]

### Usage

[[How to use|03-Usage]]




## Documentation<a name="docs"/>

[[Open documentation overview|Overview]]
