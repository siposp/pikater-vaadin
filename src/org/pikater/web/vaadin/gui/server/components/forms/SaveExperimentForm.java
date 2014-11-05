package org.pikater.web.vaadin.gui.server.components.forms;

import java.util.ArrayList;
import java.util.List;

import org.pikater.shared.database.jpa.JPABatch;
import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.server.components.forms.fields.CustomFormCheckBox;
import org.pikater.web.vaadin.gui.server.components.forms.fields.FormFieldFactory;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogResultPreparer;
import org.pikater.web.vaadin.gui.server.layouts.formlayout.CustomFormLayout;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * A form to save/queue experiments.
 * 
 * @author SkyCrawl
 */
public class SaveExperimentForm extends CustomFormLayout implements IDialogResultPreparer {
	private static final long serialVersionUID = -692840139111911571L;

	public enum ExperimentSaveMode {
		SAVE_FOR_LATER, SAVE_FOR_EXECUTION
	}

	public enum SaveForLaterMode {
		SAVE_AS_NEW, OVERWRITE_PREVIOUS, SAVE_AS_NEW_AND_DELETE_PREVIOUS;

		public String toItemPropertyID() {
			switch (this) {
			case OVERWRITE_PREVIOUS:
				return "op";
			case SAVE_AS_NEW:
				return "san";
			case SAVE_AS_NEW_AND_DELETE_PREVIOUS:
				return "sanadp";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}

		public String toDisplayString() {
			switch (this) {
			case SAVE_AS_NEW:
				return "save as new";
			case OVERWRITE_PREVIOUS:
				return "overwrite previous";
			case SAVE_AS_NEW_AND_DELETE_PREVIOUS:
				return "save as new and delete previous";
			default:
				throw new IllegalStateException("Unknown state: " + name());
			}
		}

		public static SaveForLaterMode[] getAvailableByContext(JPABatch sourceExperiment) {
			if ((sourceExperiment == null) || sourceExperiment.isDesignatedForExecution()) {
				return new SaveForLaterMode[] { SaveForLaterMode.SAVE_AS_NEW };
			} else {
				return SaveForLaterMode.values();
			}
		}
	}

	/*
	 * Declaration of fields.
	 */
	private final OptionGroup field_saveForLaterMode;
	private final TextField field_experimentName;
	private final ComboBox field_priorityAssignedByUser;
	private final CustomFormCheckBox field_sendEmailWhenFinished;
	private final TextArea field_experimentNote;

	/*
	 * Programmatic variables.
	 */
	private final ExperimentSaveMode saveMode;

	public SaveExperimentForm(ExperimentSaveMode saveMode, final JPABatch sourceExperiment) {
		super(null);

		this.saveMode = saveMode;

		// first create the fields
		this.field_experimentName = FormFieldFactory.createTextField("Experiment name:", "Enter the name", null, false, false);
		this.field_experimentName.setWidth("100%");
		this.field_experimentNote = FormFieldFactory.createTextArea("Note:", "A short description for this experiment?", null, false, false);
		this.field_experimentNote.setWidth("100%");
		switch (saveMode) {
		case SAVE_FOR_LATER:
			this.field_saveForLaterMode = FormFieldFactory.createOptionGroup("How to save:", true, false);
			SaveForLaterMode[] availableSaveForLaterModes = SaveForLaterMode.getAvailableByContext(sourceExperiment);
			for (SaveForLaterMode mode : availableSaveForLaterModes) {
				this.field_saveForLaterMode.addItem(mode);
				this.field_saveForLaterMode.setItemCaption(mode, mode.toDisplayString());
			}
			this.field_saveForLaterMode.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 3490632054167567196L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					SaveForLaterMode mode = getSaveForLaterMode();

					// name and note fields need to be enabled/disabled as needed
					switch (mode) {
					case OVERWRITE_PREVIOUS:
						if (sourceExperiment != null) {
							field_experimentName.setValue(sourceExperiment.getName());
							field_experimentNote.setValue(sourceExperiment.getNote());
						}
						break;

					case SAVE_AS_NEW:
					case SAVE_AS_NEW_AND_DELETE_PREVIOUS:
						field_experimentName.setValue("");
						field_experimentNote.setValue("");
						break;
					default:
						throw new IllegalStateException("Unknown state: " + mode.name());
					}
				}
			});
			this.field_saveForLaterMode.select(availableSaveForLaterModes[0]);
			this.field_saveForLaterMode.setNewItemsAllowed(false);
			this.field_saveForLaterMode.setNullSelectionAllowed(false);
			this.field_priorityAssignedByUser = null;
			this.field_sendEmailWhenFinished = null;
			break;

		case SAVE_FOR_EXECUTION:
			/*
			 * This is dependent on {@link JPAUser#setPriorityMax()} and
			 * {@link UniversalComputationDescription#setPriority()}.
			 */
			List<Integer> userPriorityOptions = new ArrayList<Integer>();
			for (int i = 0; i < 10; i++) {
				userPriorityOptions.add(i);
			}
			this.field_saveForLaterMode = null;
			JPAUser user = UserAuth.getUserEntity(VaadinSession.getCurrent());
			this.field_priorityAssignedByUser = FormFieldFactory.createComboBox("Priority:", userPriorityOptions, user.getPriorityMax(), true, false);
			this.field_priorityAssignedByUser.setDescription("The more, the higher priority.");
			this.field_priorityAssignedByUser.setWidth("100%");
			this.field_sendEmailWhenFinished = FormFieldFactory.createCheckBox("Email when finished:", "", true, false);
			break;

		default:
			throw new IllegalStateException("Unknown state: " + saveMode.name());
		}

		// and then add the fields with the right order
		switch (saveMode) {
		case SAVE_FOR_LATER:
			addField("how to save", field_saveForLaterMode);
			addField("name", field_experimentName);
			addField("note", field_experimentNote);
			break;

		case SAVE_FOR_EXECUTION:
			addField("name", field_experimentName);
			addField("priority", field_priorityAssignedByUser);
			addField("note", field_experimentNote);
			addField("email when finished", field_sendEmailWhenFinished);
			break;

		default:
			throw new IllegalStateException("Unknown state: " + saveMode.name());
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public IOnSubmit getSubmitAction() {
		return null;
	}

	public ExperimentSaveMode getSaveMode() {
		return saveMode;
	}

	public SaveForLaterMode getSaveForLaterMode() {
		if (getSaveMode() == ExperimentSaveMode.SAVE_FOR_LATER) {
			return (SaveForLaterMode) field_saveForLaterMode.getValue();
		} else {
			throw new IllegalStateException("This form's mode is not set to: " + ExperimentSaveMode.SAVE_FOR_LATER.name());
		}
	}

	public String getExperimentName() {
		return field_experimentName.getValue();
	}

	public Integer getPriorityAssignedByUser() {
		return (Integer) field_priorityAssignedByUser.getValue();
	}

	public boolean getSendEmailWhenFinished() {
		return field_sendEmailWhenFinished.isSelected();
	}

	public String getExperimentNote() {
		return field_experimentNote.getValue();
	}

	//--------------------------------------------------------------------
	// METHODS DEFINING THIS FORM'S BEHAVIOUR AS A PART OF A DIALOG

	@Override
	public void addArgs(List<Object> arguments) {
		switch (saveMode) {
		case SAVE_FOR_LATER: // only some information needs to be provided
			arguments.add(getExperimentName());
			arguments.add(getExperimentNote());
			break;
		case SAVE_FOR_EXECUTION: // full information needs to be provided
			arguments.add(getExperimentName());
			arguments.add(getPriorityAssignedByUser());
			arguments.add(getSendEmailWhenFinished());
			arguments.add(getExperimentNote());
			break;
		default:
			throw new IllegalStateException("Unknown state: " + saveMode.name());
		}
	}
}