package org.pikater.web.vaadin.gui.server.components.led;

import org.pikater.web.vaadin.gui.server.components.anchor.Anchor;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Panel;

/**
 * A CSS implementation of a led sign. The {@link LedIndicatorTheme} 
 * decides its color. Supports clicking.
 * 
 * @author SkyCrawl
 */
@StyleSheet("ledIndicator.css")
public class LedIndicator extends Panel {
	private static final long serialVersionUID = 1770927441412413922L;

	private final Anchor actualLed;

	/** 
	 * @param theme the color theme of this led indicator
	 */
	public LedIndicator(LedIndicatorTheme theme) {
		this(theme, null);
	}

	/** 
	 * @param theme the color theme of this led indicator
	 */
	public LedIndicator(LedIndicatorTheme theme, ClickListener listener) {
		super();
		this.actualLed = new Anchor(null, listener);
		this.actualLed.setWidth("10px");
		this.actualLed.setHeight("10px");
		this.actualLed.removeStyleName("v-label");

		setSizeUndefined();
		setContent(actualLed);

		setTheme(theme);
		setStyleName("ledIndicator");
		setHoverEnabled(listener != null);
	}

	/**
	 * Enabled/disable changing the led indicator's style when
	 * a mouse pointer hovers over it.
	 */
	public void setHoverEnabled(boolean enabled) {
		if (enabled) {
			actualLed.addStyleName("hoverEnabled");
		} else {
			actualLed.removeStyleName("hoverEnabled");
		}
	}

	/**
	 * Set the color theme of this led indicator.
	 */
	public void setTheme(LedIndicatorTheme theme) {
		actualLed.setStyleName(theme.toStyleName());
	}

	//----------------------------------------------------
	// INNER TYPES

	/**
	 * Color theme for {@link LedIndicator}.
	 * 
	 * @author SkyCrawl
	 */
	public enum LedIndicatorTheme {
		GREEN, RED;

		public String toStyleName() {
			switch (this) {
			case GREEN:
				return "themeGreen";
			case RED:
				return "themeRed";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}
	}
}
