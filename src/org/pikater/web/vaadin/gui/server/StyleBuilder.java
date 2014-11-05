package org.pikater.web.vaadin.gui.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>This class was inspired by GWT's {@link com.google.gwt.dom.builder.shared.
 * StyleBuilder}. Its primary purpose was to avoid having to define a lot of
 * customized CSS code for many individual instances of the same component.</p>
 * 
 * <p>If CSS directly affects a component's functionality or just overrides
 * one property, there's not really a need to devise a special new style name
 * for such alterations.</p>
 * 
 * @author SkyCrawl
 */
public class StyleBuilder {
	private final Map<String, String> propertyToValue;

	public StyleBuilder() {
		this.propertyToValue = new HashMap<String, String>();
	}

	public void setProperty(String property, String value) {
		propertyToValue.put(property, value);
	}

	/**
	 * Build styles defined by this class into a single CSS-compatible string.
	 */
	public String build() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : propertyToValue.entrySet()) {
			sb.append(String.format("%s: %s; ", entry.getKey(), entry.getValue()));
		}
		return sb.toString();
	}
}
