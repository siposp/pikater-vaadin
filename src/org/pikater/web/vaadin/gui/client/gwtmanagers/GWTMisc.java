package org.pikater.web.vaadin.gui.client.gwtmanagers;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

/**
 * A utility class to hold various global data structures and provide
 * some global methods.
 * 
 * @author SkyCrawl
 */
public class GWTMisc {
	// *************************************************************************************
	// PUBLIC FIELDS

	/**
	 * Just something to avoid bug/implementation confusion.
	 */
	public static final Command alertNotImplemented = new Command() {
		@Override
		public void execute() {
			Window.alert("Not implemented yet.");
		}
	};

	/**
	 * Basic attributes to print when calling node serialization into JSON.
	 */
	public static final JsArrayString jsonAttrsToSerialize = (JsArrayString) JsArrayString.createArray();
	static {
		jsonAttrsToSerialize.push("attrs");
		jsonAttrsToSerialize.push("x");
		jsonAttrsToSerialize.push("y");
		jsonAttrsToSerialize.push("id");
		jsonAttrsToSerialize.push("className");
		jsonAttrsToSerialize.push("children");
	}

	// *************************************************************************************
	// PUBLIC METHODS

	/**
	 * GWT compatible version of getting simple class names.
	 */
	public static String getSimpleName(Class<? extends Object> cls) {
		String name = cls.getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	/*
	// just a backup if it ever needs to be used
	public static String getImageResource(String relativePathToResources)
	{
		return GWT.getModuleBaseURL() + relativePathToResources;
	}
	*/
}
