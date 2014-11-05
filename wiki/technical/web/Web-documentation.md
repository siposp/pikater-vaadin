<!-- --- title: Technical documentation part three: Web application documentation-->

[[_TOC_]]




## Technologies

### Summary

The focus of this documentation is not to explain the technologies used or provide documentation to them. If you wish to read more about them, follow the links or take a look at the "[Other useful links](#webLinks)" section below. A light introduction, however:
* [Google Web Toolkit](http://www.gwtproject.org/) (GWT).  
GWT is mainly a client-side (browser) technology. It allows developing web applications in java - the code is then compiled and translated into javascript.  
* [Vaadin](https://vaadin.com/book/-/page/preface.html) framework based on GWT to implement GUI.  
Vaadin is both client-side (extending GWT) and server-side technology, and moves web application development to server-side. Events like user clicking on a button are transferred and handled on the server (AJAX), as opposed to standalone GWT which (by default) handles events on the client. Requests to server are, with some exceptions of course, in the form of AJAX calls. For more information, refer to the Book of Vaadin.  
GWT and Vaadin form the infrastructure and looks of our web application.
* [KineticJS](https://github.com/ericdrowell/KineticJS) to draw experiments on the client.  
KineticJS is javascript library used for drawing custom content (styled shapes, curves, text, animations, etc.) in a HTML5's canvas element. It is used in the GWT part of our application to define a widget (GWT term) that draws experiments in the browser.
* [KineticGWT](https://github.com/neothemachine/KineticGWT) for nice & easy integration of KineticJS in GWT.  
KineticGWT is a GWT wrapper for KineticJS and is used to simplify development - it uses JSNI (inner GWT technology) to provide java interface and call javascript on the background.
* [Quartz scheduler](http://quartz-scheduler.org/) as a mechanism of executing background tasks. It is only used by the web application at this moment but can easily be used by core system as well.

### Decisions

The main purpose was to find a solution that would allow us to:
* avoid having to write in plain HTML/Javascript,
* use some predefined GUI components, as customizable as possible,
* easily integrate an HTML5 framework to draw experiments, potentially narrowing down HTML5 capabilities to a library with small API that does a lot of work by itself.

**GWT and Vaadin**  

We did some research and with respect to the above conditions ruled out `PHP` after a while, also because there was no previous experience with it amongst the team. An idea sprang forward that since the original `pikater` project was written in Java, it would be nice to continue writing in Java for as much compatibility and compactness as possible. So we stumbled across `RIA (Rich internet applications)` and `GWT`. It seemed to satisfy the above conditions very well and gave us a lot more than we were looking for:
* a framework to manipulate with DOM,
* a framework to work with browsers which should be supported by all major browsers,
* RPC like mechanism to communicate in both directions (`client-server` and `server-client`).

It would seem like a very good choice but it hides a potentially big problem - GWT is a client side technology and as such, it has very limited functionalities and extension code-base.  

It was made clear from the very start that some data-structures (e.g. experiments) or implementations would have to be defined in `core`, `server` and `client` versions, each serving a different purpose. If only one or two of them were implemented, they would be difficult to work with at some point. In practice, we would have to overcome this issue by moving all the "complex" code to the server, having to define/redefine a lot of communication code, with a lot of effort. Whether many implementations of the same things or large communication protocol, none of these two options were ideal so we looked for a way to get over this issue and stumbled upon Vaadin, which seemed like a dream come true. A framework that:
* extends GWT,
* is free except for some "enterprise" features,
* allows server-side development (GUI component definition and event handling),
* is designed to be very customizable and extendable and yet again adds a lot of other value.

Nothing is as good as it first seems, however and while Vaadin solved a lot of problems for us, it might introduce some others:
* performance,
* hidden bugs/incompatibilities/quirks and so on,
* the danger of stopped development some time in the future,
* the danger of it becoming a paid service.

Performance was looked at first and foremost. We looked at Vaadin forums but didn't find any such problems reported by the many people who used the framework. Also, looking at the basics and the aforementioned forums, we found that performance seemed an important point for Vaadin's developers. Since then, performance only posed a problem in very specialized cases (some of which we eventually encountered) but even if the solution would not be perfect, there were still ways around the problem.

When it comes to bugs and incompatibilities, those are mostly found in the most surprising moments and there was nothing much we could do about it but risk.

And finally, stopped development didn't seem to be a viable option for many years to come since Vaadin is being actively developed, actively contributes to GWT development, has quite a history by now, is backed by several organizations and has a business model of its own. For the same reasons (also note that Vaadin greatly builds upon GWT which is free and will still be free in the future), one can hardly imagine the basic framework becoming a paid product for many years to come.

**KineticJS and KineticGWT**  

At this point, the use of GWT and Vaadin is decided and decisions about experiment drawing interface come next. This particular choice was much more simple.

Since HTML5 was still a new hype at that time, many of these frameworks were under development and many of the more complete and nicer implementations were not free. We could only find a few open source ones out of which one stood out: `KineticJS`. It was being very actively developed on GitHub, steadily gaining more reputation and more importantly, it had a nice documentation. Also, it was quite extensive (among the frameworks we found), allowed much more than simple graph drawing and there was even a GWT module for it - something that leverages GWT's JSNI technology and provides Java API.

While the choice might be different now, it was a very good choice then, full of prospects.

**Quartz**  

We needed background tasks to generate:
* scatter plots for dataset visualization and comparison,
* handle uploaded files (datasets and agents) before being passed to core system and do it independently of Vaadin not to slow it down.

In due time, we discovered that Vaadin has no decent background task mechanism (quite logically when you think about it) and there was another decision in front of us: "A background task framework. Yes or no?".

We ended up deciding for "yes" because it was a good idea to execute tasks in a specifically configured "environment" with fixed amount of threads and resource management so users would not be able to suffocate the web application in a spike. Quartz framework is a Java framework doing exactly that and much more. It could also be used to easily define cron jobs in the web application with a fixed schedule and might even be useful in core system from time to time.




## Features<a name="features"></a>

Basic feature set may be seen [[here|Home]]. In this section, we will go a little deeper than that.

### Regular user<a name="userFeatures"></a>

The alpha and omega of this application is authentication. Before accessing any page, authentication is required. When a regular user authenticates, the following features are accessible:  
* Account management  
Subfeatures:
	* changing email and/or password,
	* logging out.
* Dataset management  
Subfeatures:
	* displaying all user-uploaded datasets,
	* visualizing them,
	* comparing them,
	* downloading/deleting them and uploading new.
* Custom agent management  
Subfeatures:
	* displaying all user-uploaded agents,
	* downloading/deleting them.
* Experiment management  
Subfeatures:
	* displaying all user-defined experiments,
	* displaying experiment results (even when they are still being computed),
	* exporting experiment results to CSV format for further external processing,
	* defining new experiments, saving/loading them, queuing them for execution and aborting them if they are still being computed,
	* importing previously computed experiment computation models to be used again.

### Administrator<a name="adminFeatures"></a>

Since administrators may also be regular users, all of the above features are accessible for them too. On top of that, these features are exclusive for them:
* User management  
Subfeatures:
	* listing all user accounts,
	* activating/suspending them,
	* changing user priority (applies when queuing experiments),
	* promoting users to administrators, degrading administrators,
	* resetting user passwords.
* Dataset management  
Subfeatures:
	* listing all global and user-uploaded datasets,
	* approving/disapproving them for use in experiments,
	* deleting them.
* Agent management  
Subfeatures:
	* listing all user-uploaded agents,
	* approving/disapproving them for use in experiments,
	* deleting them.
* Experiment management  
Subfeatures:
	* listing all queued/failed/finished experiments,
	* changing their priority and aborting them, if still being computed.




## Configuration<a name="conf"></a>

All available options can be found and customized in: `WebContent/WEB-INF/web.xml`.
Most of them are standard web application or Vaadin configuration options and will not be documented in this document. Should you wish to read more about them, follow the links provided in the file.

Custom configuration options are defined at the bottom of the file:

**Online/offline mode**  
The application is in "online" mode when it communicates with an instance of system core, so:
* scheduled experiments are immediately delegated to system core for execution,
* newly uploaded agents are immediately delegated to system core for processing,
* newly uploaded datasets are immediately delegated to system core for registration.
* various other changes are immediately delegated to system core.

Online mode means the application performing to its fullest potential.  
On the contrary, in offline mode only database is available so one can only view/edit the database state. Changes are saved to database and a dialog pops up informing the user that they will not be fully handled until a core system instance picks them up. As of the experiment editor, it will contain dummy boxes.

**Filter debug mode**  
This option was added for the single purpose of [Filter](http://docs.oracle.com/javaee/6/api/javax/servlet/Filter.html) development. If set to true, the `org.pikater.web.filters.AbstractFilter#isDebugMode()` returns true.

Example of usage can be seen in (also, it is the only defined filter at this moment):

```java:/src/org/pikater/web/filters/RequestCodificatorFilter.java```




## Processes

### Startup

Should be clear from the following diagram:

{{{{{{ blue-modern
	Web app->Web app: initialize logger, inner state & Quartz
	alt web-app is configured to run in online mode
		Web app->+Core system: RPC to test availability
		Core system-->-Web app: response
	end
	Web app->+Database: init connection to test availability
	Database-->-Web app: response
}}}}}}

**Notes**
* If any errors occur during this phase, the application will not start. Errors will need to be resolved before it can be successfully launched.
* Database availability test doesn't work at this moment. There are some strange issues with using connection drivers during application startup. They work without a problem outside of that scope, however.

You may refer to the latest source code as well: 

```java:/src/org/pikater/web/StartupAndQuitListener.java```

### Page request handling

Has quite standard implementation:

{{{{{{ blue-modern
	Client->+Web app: request page
	alt request is filtered out
		Web app->Web app: output response (mostly an error)
	else request is not filtered out
		alt user is not logged in
			Web app->Web app: output login dialog
		else user is logged in
			Web app->+Requested-page: request page
			alt access is allowed
				Requested-page->Requested-page: output requested page
			else access is not allowed
				Requested-page->Requested-page: output error message
			end
			Requested-page-->-Web app: return response
		end
	end
	Web app-->-Client: return response
}}}}}}

Request filtering is done using filters (see [above](#conf)).

Since the web application is an AJAX application (javascript calls do not refresh the whole page), subsequent calls from javascript do not apply to the scheme above. They are simply handled in RPC manner on the server. For more information about how this is done, refer to Book of Vaadin.

### Progress synchronization

General information about dialogs and specific progress dialog use cases can be seen [[here|User-guide]].  
Three entities have to be recognized when progress dialogs are used: `client (browser)`, `web application` and `background task`. The following diagram describes synchronization and communication between these entities:

{{{{{{ blue-modern
	Client->Web-app: feature request
	Web-app->Client: start polling for changes
	Web-app->Task: start
	loop until task is finished, failed or aborted
    	Client->Web-app: poll for changes each 500ms
    	Task->Web-app: update progress (API calls)
	end
	alt task aborted by user
    	Web-app->Task: abort
    	Web-app->Client: disable polling
		Web-app->Web-app: close dialog
	else task failed (error or API calls)
		Web-app->Client: disable polling
		Web-app->Web-app: update dialog and display a notification
	else task finished (API calls)
    	Web-app->Client: disable polling
		Web-app->Web-app: update dialog and ready the results
	end
}}}}}}

While Vaadin supports automatic pushing of changes from server to client, it seems rather uncomfortable to let it potentially generate so much server to client traffic. Furthermore, automatic pushing only works when changes are made in a thread that is used to handle a request from client (changes are pushed as a response) - this is not the case for our background task which is a Vaadin-independent thread. It is a Quartz job (see above).

As such, the client itself polls for changes once in 500ms (current default value). It can be observed that the web application acts as a mediator and coordinator, just as it should.

**Limitation**  
Quote from user guide: `When you leave the page while a progress dialog with a background task is running, all progress is lost`.  
This happens because dialogs are Vaadin components attached to and dependent on a UI instance (a page instance). When the page is navigated away, it is destroyed on server which means the dialog is also destroyed and the underlying task and its progress as well.





## Read more

Some technical information may also be found at the following documents.

### Admin guide

See [[admin guide|Admin-guide]].

### User guide<a name="webGUI"></a>

See [[user guide|User-guide]].

### Useful links<a name="webLinks"/></a>

* Servlet 3.0 documentation found at http://tomcat.apache.org/tomcat-7.0-doc/servletapi/

