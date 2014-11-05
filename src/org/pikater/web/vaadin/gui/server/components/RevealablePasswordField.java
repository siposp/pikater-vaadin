package org.pikater.web.vaadin.gui.server.components;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/** 
 * Vaadin doesn't support a password field that reveals the password temporarily,
 * so we're going to use both {@link PasswordField} and {@link TextField} to
 * simulate such behaviour. The goal here is to dynamically embed the right
 * component and switch between them.
 * 
 * @author SkyCrawl
 */
public class RevealablePasswordField {
	private final Property<String> property;
	private final ObjectProperty<AbstractTextField> plainTextField;
	private final ObjectProperty<AbstractTextField> passwordField;

	private boolean plainText;

	public RevealablePasswordField(Property<String> property, boolean displayPlainText) {
		this.property = property;

		TextField tf = new TextField(property);
		PasswordField pf = new PasswordField(property);

		this.plainTextField = new ObjectProperty<AbstractTextField>(tf);
		this.passwordField = new ObjectProperty<AbstractTextField>(pf);

		this.plainText = displayPlainText;
	}

	public boolean isPlainText() {
		return plainText;
	}

	public void setPlainText(boolean plainText) {
		this.plainText = plainText;
	}

	public ObjectProperty<AbstractTextField> getComponent() {
		ObjectProperty<AbstractTextField> prop = plainText ? plainTextField : passwordField;
		prop.getValue().setValue(getValue()); // refresh when GUI asks for the component
		return prop;
	}

	public Property<String> getProperty() {
		return property;
	}

	public String getValue() {
		return property.getValue();
	}

	public void setValue(String newValue) {
		property.setValue(newValue);
	}
}