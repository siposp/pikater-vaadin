package org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.pikater.shared.database.views.tableview.test.TestTableDBView;
import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;
import org.pikater.web.vaadin.gui.server.components.dbviews.TestDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.ui_default.DefaultUI;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.ContentProvider;
import org.pikater.web.vaadin.gui.server.ui_default.indexpage.content.IContentComponent;

import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.VerticalLayout;

/**
 * <p>
 * View implementing the test feature - purely for development.
 * </p>
 * 
 * <p>
 * This class contains some useful code snippets should they ever be needed.
 * This is also the perfect place to try various things being integrated into
 * production code.
 * <p>
 * 
 * @author SkyCrawl
 * 
 * @see {@link DefaultUI}
 * @see {@link ContentProvider}
 */
public class TestView extends VerticalLayout implements IContentComponent {
	private static final long serialVersionUID = -6639847503623900929L;

	public TestView() {
		super();
		setSizeFull();

		// testJSCH();
		// testAnchor();
		// dummyDBTableLayout();
		dummyExpandableDBTableLayout();
	}

	// ----------------------------------------------------
	// VIEW INTERFACE

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public boolean isReadyToClose() {
		return false;
	}

	@Override
	public String getCloseMessage() {
		return null;
	}

	@Override
	public void beforeClose() {
	}

	// -------------------------------------------------------------------
	// TEST GUI INITIALIZATONS

	public void dummyDBTableLayout() {
		DBTableLayout dbTableLayout = new DBTableLayout();
		dbTableLayout.setReadOnly(true);
		// dbTableLayout.setCommitImmediately(false);
		dbTableLayout.setView(new TestDBViewRoot(new TestTableDBView()));
		addComponent(dbTableLayout);
	}

	public void dummyExpandableDBTableLayout() {
		addComponent(new TestExpandableView());
	}

	public void testAnchor() {
		addComponent(new Anchor("test1", new ClickListener() {
			private static final long serialVersionUID = -485684054380631770L;

			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
				MyNotifications.showInfo("Yay", "yay");
			}
		}));
		addComponent(new Anchor("test2", "function() { alert(42); }"));
	}

	// -------------------------------------------------------------------
	// TIPS AND TRICKS

	protected static void callJSFunctionOnTheClient() {
		JavaScript.getCurrent().addFunction("pikater_setAppMode",
				new JavaScriptFunction() {
					private static final long serialVersionUID = 4291049321598205127L;

					@Override
					public void call(JSONArray arguments) throws JSONException {
						// this is called on the server when the function is
						// called on the client
						System.out.println(arguments.length());
						System.out.println(arguments.getBoolean(0));
					}
				});
		JavaScript.getCurrent().execute("window.pikater_setAppMode(true)"); // calls
																			// the
																			// function
																			// on
																			// the
																			// client
	}

	protected static void callJSNIAddedFunctionOnTheClient() {
		JavaScript.getCurrent().execute(
				"window.ns_pikater.setAppMode(\"DEBUG\");"); // calls the
																// function on
																// the client
	}

	protected static void backgroundThreadDoingSomeJobPeriodically() // quartz
																		// could
																		// be
																		// used
																		// too...
	{
		// The background thread that updates clock times once every second.
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
			}
		}, new Date(), 1000);
	}

	protected static void redirectToCustomServletWithoutChangingURL() {
		new Button.ClickListener() {
			private static final long serialVersionUID = -3016596327398677231L;

			@Override
			public void buttonClick(ClickEvent event) {
				// getUI().getPage().setLocation("/NewPikater/static/hokus_pokus.txt");
				// // ./WEB-INF/static/hokus_pokus.txt
				// getUI().getPage().setLocation("/Pikater/staticDownload"); //
				// servlet mapped to /staticDownload
			}
		};
	}
}