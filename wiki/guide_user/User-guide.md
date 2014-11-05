<!-- --- title: Web application - user guide -->

[[_TOC_]]

## Foreword

**A word of caution:**  
Application has been developed, tested and compiled only for Google Chrome. While it may work in other browsers (partially or fully), unexpected visual and application errors may occur.




## Components

It is best to start with documenting individual used components because they are used at some point on various pages.

### Dialogs<a name="dialogs"></a>

All dialogs are defined in the `org.pikater.web.vaadin.gui.server.components.popups.dialogs` package.  
Several types of dialogs are used:

1. **Information dialogs (error, warning and information)**  
Information dialogs are plain simple and do not require further commentary.
2. **Component dialogs**  
Component dialogs are used when a user input is needed and it is not (for example) a simple String. Mostly, the required user input has multiple parts (forms or wizards are displayed) or it is a database table row or a part of it (table views are displayed) or a combination of the two.  
Unless the component is a wizard, the dialog has two buttons: one to accept the entered input and one to close the dialog. Wizards have their own counterparts of these two buttons and define two more: a `Next` and `Back` button.
3. **Progress dialogs**  
Some actions trigger a potentially lengthy operations (e.g. dataset visualization). When triggered, this dialog will appear and track the underlying task's progress. The underlying task can be aborted anytime with the `Abort` button. When pressed, the dialog will be closed.  
_Note that when you leave the page while a progress dialog with a background task is running, all progress is lost_.

Common properties:

**Keyboard listener**  
A keyboard listener is attached to every dialog and is active when the dialog is in focus (by default it is). The following keys are mapped:

1. `ESC` key  
Closes the dialog.
2. `ENTER` key  
Triggers user input validation and processing and if successfully finished, closes the dialog.  

**Modality**  
By default, all dialogs are modal - they block all other GUI interactions until they are closed.

### Notifications

The application uses notifications to inform the user of various events (e.g. file upload finished, an error and so on). They appear as bubbles in the top right corner of the browser viewport and carry a simple caption and a message.

All notifications are defined in the `org.pikater.web.vaadin.gui.server.components.popups` package.  
Several types of notifications are defined:

1. Success
2. Notification
3. Warning
4. Error

Common properties:

**Auto-closing**  
Notifications are closed automatically after a certain timeout (5 seconds at the moment).

**Click-to-close**  
Notifications can be closed manually before the timeout expires with a single mouse click.

**Stackable**  
Notifications can be stacked one after another, almost indefinitely.

### Table views

Table views are very common throughout the whole application, especially index page (see below) and dialogs.

#### Regular<a name="regularTableViews"></a>

Let's start with a summary image:

[[table_views_desc.png|alt=Table view description]]

**Main functionalities**  
* Viewing/editing various database information
* Control over when changes are applied  
This is done with the `Immediate changes switch` checkbox and `Save changes` button. If the first is checked, the latter will not be visible and vice versa. If the checkbox is checked, we say the table is in `immediate mode`. **If the table is in immediate mode, all changes are immediately synchronized with the database. Otherwise, changes have to be manually accepted by clicking the `Save changes` button**. If they are not, they will not take effect.
* Column collapsing/expanding.  
This is done with the `Column collapse/expand button`. When clicked, a drop down select list will appear, allowing the user to filter visible columns.
* Column reordering  
This is done by dragging the column headers around the table.
* Sorting rows by a certain column  
This is done by clicking on a column header. Both ascending and descending order are supported. If, for instance, ascending order is triggered, another click will switch to descending order and vice versa.
* Setting number of displayed results  
This is done with the drop-down page size control (up to 100).
* Nice and easy page navigation using standard paging controls.

**Column types**  
At this moment, table views define four column types:
* simple string column,
* yes/no column,
* representative column,
* action column.

All of them may be read-only/editable (in case of action column, enabled or disabled). This option is not in user-control, however - it may vary between tables and even between rows.

**Table view modes**

1. Default  
In this mode, the table's capabilities are used to the fullest. It is in `immediate mode`.
2. Not immediate  
In this mode, the table's capabilities are equivalent to `default` mode, except that changes are not applied immediately and automatically. It is NOT in `immediate mode`.
3. Read-only  
In this mode, the table's capabilities are reduced to displaying values (none of them can be edited). Both `Immediate changes switch` checkbox and `Save changes` button (see above) are disabled and invisible. Actions are still permitted, however - for example downloading something related to the view.

#### Expandable

Expandable table views are in fact cascaded [regular table views](#regularTableViews). One table view "defines" or "constructs" another based on which row is selected. Using this GUI component, one can view nested tables in database.

**Main functionalities**  
They are the same as functionalities of regular table views, except **navigation**.  

For example, let's say we have a "user" that has multiple addresses defined. With a "users" table view, we can display all users (one row corresponds to one user). When we select a row, we can then construct another table view displaying all addresses of the user we just selected.  

Expandable table views are displayed as "wizards":

[[expandable_table_views_desc.png|alt=Expandable table view description]]

At first, the `base` table view can be seen. Once a row is selected and the `Next` button clicked, the expandable table view:

1. constructs the next step,
2. displays it,
3. updates the `step overview bar`.

Each step (table view) has a representative caption in `step overview bar` and the currently viewed step's caption is highlighted with bold font-style.

Individual steps can be navigated back and forth as intended, unless they are the `base` or `final` views - `Back` and `Next` buttons, respectively, are then disabled.

Another use case or purpose of expandable table views would be to **choose a database entity or item** (a row) for further processing. Regular table views may be used to select a row within a single view. Expandable table views may be used to select a row of nested views. This is taken advantage of in [dialogs](#dialogs). In addition to buttons mentioned earlier, expandable table views then also define the `Finish` button, that appears in `final` view (replaces the `Next` button in fact).





## Pages

### Summary

#### Bookmarking

All pages/features may be bookmarked but there are some limitations:
* Active content  
Active content is never saved. That includes interactions with table views and generally any user-entered information.
* Temporary pages  
[Visualization pages](#visPage) are only accessible for as long as the user who created them is logged in and their session doesn't expire. When that happens, the generated images are deleted and can not be accessed anymore.

This means that bookmarking visualization pages is not a good idea. Instead, visualization results should be thoroughly inspected when generated and all images of interest downloaded to user's machine. Thus, downloading the whole page will also not be effective unless the browser downloads all images.

#### Sharing

With respect to the preceding chapter, the following limitations apply:
* Since authentication is always needed, ANY application link will only be useful to a registered user.
* Sharing application links will only preserve the currently viewed/used feature, except links to [visualization pages](#visPage). As long as a visualization page is accessible (see above), they may be viewed by other users.

### Default page<a name="defaultPage"></a>

#### Summary

URL: `http://my.domain/Pikater/index`  
This is the "overview" page from which other pages may be accessed.

Administrators will find all their [[features|Web-documentation#adminFeatures]] on this page.  
Regular users will find all database view/edit features on it, which means all features listed [[here|Web-documentation#userFeatures]], except:
* `defining new experiments, saving/loading them and queuing them for execution`,
* `importing previously computed experiment computation models to be used again`.

Those particular features are dedicated to [experiment editor](#experimentEditor).

#### Documentation

Again, let's start with a picture:

[[default_ui_description.png|alt=Default UI description]]

As you can see, this page is quite simple. Menu items are divided to administrator and regular user feature groups and correspond to application [[features|Web-documentation#features]] practically 1:1.

Individual features are mostly made up (except `View & edit profile`) of the above mentioned dialogs and table views and do not require further commentaries.  
Special use cases are described in the [FAQ](#faq) section.

### Visualization page<a name="visPage"></a>

#### Summary

URL: `http://my.domain/Pikater/visualization`  
This page is generated as a result of dataset visualization or comparison.

#### Documentation

Again, a picture:

[[visualization_ui_description.png|alt=Visualization UI description]]

The generated content is made up of [scatter plots](http://en.wikipedia.org/wiki/Scatter_plot). Each plot:
* corresponds to one Y index (attribute or attribute mapping),
* corresponds to one X index (attribute or attribute mapping),
* will be displayed in a zoomable window by double cliking on the image.

By default, no selection is active. X and Y indexes can be selected by clicking on them. If selected, only plots that correspond to them will be displayed. Example of this can be seen here:

[[visualization_ui_selection.png|alt=Visualization UI selection]]

Selection can be reset either by (again) clicking on selected indexes or clicking on the `Reset selection` header.

### Experiment editor<a name="experimentEditor"></a>

#### Summary

URL: `http://my.domain/Pikater/editor`  

Experiment editor is used to define, save, load and queue experiments - simple graphs of boxes and edges with various configurations.

The editor GUI is made up of several parts:
* top menu,
* settings panel (right under top menu),
* left side-panel with all available boxes,
* right side-panel with box configuration,
* stage for experiments (at the center).

Only one of the side-panels may be open at a time. To switch between them, use the top menu item `View` or `ALT + LEFT_ARROW` and `ALT + RIGHT_ARROW` keyboard shortcuts.

Everything needed to use the editor is explained in the below videos. It is recommended to watch them exactly in the given order.

#### How to use boxes

<div class="video-container">
	<code class="vc-mime">video/mp4</code>
	<a class="vc-source">https://dl.dropboxusercontent.com/u/100302745/pikater_videos/01_boxes.mp4</a>
</div>

#### How to use edges

<div class="video-container">
	<code class="vc-mime">video/mp4</code>
	<a class="vc-source">https://dl.dropboxusercontent.com/u/100302745/pikater_videos/02_edges.mp4</a>
</div>

#### How to configure boxes

<div class="video-container">
	<code class="vc-mime">video/mp4</code>
	<a class="vc-source">https://dl.dropboxusercontent.com/u/100302745/pikater_videos/03_config.mp4</a>
</div>

#### How to save, load and queue

<div class="video-container">
	<code class="vc-mime">video/mp4</code>
	<a class="vc-source">https://dl.dropboxusercontent.com/u/100302745/pikater_videos/04_save-load-queue.mp4</a>
</div>

#### How to change visual style

<div class="video-container">
	<code class="vc-mime">video/mp4</code>
	<a class="vc-source">https://dl.dropboxusercontent.com/u/100302745/pikater_videos/05_style.mp4</a>
</div>





## FAQ<a name="faq"></a>

**Please note that this section is not a guide to all the application's features. It is recommended to read the above documentation to discover the application's capabilities, conventions and potentially (for some users) hidden features.**

**In this section, we will only cover most common or untrivial use cases that are not explained by the above documentation. Additional information is also provided for some use cases.**

### How do I create an account?

1. Open one of the pages described above, a login dialog will appear (if client is not logged in yet).
2. Click on the `Create account` button. Another dialog will appear, this time with an account-register form.
3. Fill the form.
4. Click `Ok`. The account register form will be closed and login dialog will be visible again.
5. Fill credentials of your new account and click `Ok`. You will be logged in using your new account.

Note that experiments queued under your new account will have the lowest possible priority. Consider contacting an administrator to raise it.

### How do I change my password?<a name="changePassword"></a>

1. Open the default page.
2. Click on `View & edit profile` in the left menu.
3. Click on the `Change password` button. A dialog will appear.
4. Enter your current password. Enter the new password (twice, for safety).
5. Click on `Ok`. The dialog will close.
6. Click on `Save changes`.

### How do I reset my password?

Only administrators can do that.  
New passwords are sent to the account's email address.

### How do I change my email?

1. Open the default page.
2. Click on `View & edit profile` in the left menu.
3. Enter new email into the appropriate field.
4. Click on `Save changes`.

### How do I track experiment progress?

1. Open the default page.
2. Click on `My experiments` in the left menu.
3. Find your experiment. You will its status. You may expand it to view individual tasks and results that have already been computed for it.

### How do I export experiment results to CSV?

1. Open the default page.
2. Click on `My experiments` in the left menu.
3. Find your experiment.
4. Expand the `RESULTS` column (see [regular table views](#regularTableViews)).
5. Click on the `Download` button located on the same row as your experiment. A progress dialog will appear.  
6. When the result `.csv` is generated, the progress dialog's `Abort` button will change into a `Finished` button. Click on it. The file will be downloaded.

### How do I change priority of running experiments?

Note that this is an administrator feature.

1. Open the default page.
2. Click on `All experiments` in the left menu.
3. Find your experiment.
4. Expand the `PRIORITY` column (see [regular table views](#regularTableViews)).
5. Change the `PRIORITY` value on the same row as your experiment, if enabled. If it is not, the experiment is not running anymore.

### How do I generate scatter plots for datasets?

1. Open the default page.
2. Click on `My datasets` in the left menu.
3. Find your dataset.
4. Expand the `VISUALIZE` column (see [regular table views](#regularTableViews)).
5. Click on the `Visualize` button located on the same row as your dataset. A dialog will appear.  
_If the button is disabled, metadata have not yet been computed for your dataset or are not available at all. If you have a suspicion yours is the latter case, contact the administrators_.
6. Select target attribute.
7. Include or exclude attributes to be used in visualization.
8. Click `Ok`.
9. If no compatible attributes are found (from the selected), a notification will display and user will have to respecify the attributes (it is possible a dataset can not be visualized at all).  
Otherwise, a progress dialog will appear.
10. When scatter plots are generated, the progress dialog's `Abort` button will change into a `Finished` button. Click on it. You will be redirected to visualization page.

### How do I generate scatter plots that compare two datasets?

1. Open the default page.
2. Click on `My datasets` in the left menu.
3. Find your dataset.
4. Expand the `COMPARE` column (see [regular table views](#regularTableViews)).
5. Click on the `Compare to` button located on the same row as your dataset. A dialog will appear.  
_If the button is disabled, metadata have not yet been computed for your dataset or are not available at all. If you have a suspicion yours is the latter case, contact the administrators_.
6. Select target and included attributes to be used for the dataset from step 3.
7. Click `Next`.
8. Find the dataset to compare to, select its row and click `Next`.
9. Select target and included attributes to be used for the "compare to" dataset.
10. Click `Finish`.
11. If no compatible attribute mappings are found (from the selected), a notification will display and user will have to respecify the attributes (it is possible the specified datasets can not be visualized at all).  
Otherwise, a progress dialog will appear.
12. When scatter plots are generated, the progress dialog's `Abort` button will change into a `Finished` button. Click on it. You will be redirected to visualization page.

Note that similar guide can be applied to comparing experiment results to input datasets, as demanded by the project's specification.

### How do I upload a new dataset?

1. Open the default page.
2. Click on `My datasets` in the left menu.
3. Click on `Upload new dataset`.
4. Follow the wizard, everything all relevant information will be displayed in the right time.

Note that:
* Multiple files may be uploaded simultaneously on the same page. Each one will have to be entered through the wizard, however.
* Each user may only upload 3 files at a time across all pages.
* The more files are being uploaded, the slower the server's responses will be.
* Metadata have to be computed for your dataset before it can be used in experiments, visualization and metadata viewer. Metadata can be viewed, when available, by expanding the dataset view to the next step (see above).

### How do I upload a new user agent (computation method)?

1. Open the default page.
2. Click on `My agents` in the left menu.
3. Click on `Upload a new agent`.
4. Enter the required information. `Agent class` needs to be a fully qualified Java class name, including package, e.g. `org.example.TestAgent`. The uploaded `.jar` file needs to include implementation for the given class name.

Note that:
* Multiple files may be uploaded simultaneously on the same page. Each one will have to be entered through the dialog, however.
* Each user may only upload 3 files at a time across all pages.
* The more files are being uploaded, the slower the server's responses will be.
* It may take some time before your agent can be used in experiments. It has to be registered in the core system. When that happens and there is no problem with your agent, it will display in experiment editor after a page refresh.






