package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor;

import java.util.List;

import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.core.agents.gateway.exception.PikaterGatewayException;
import org.pikater.shared.database.jpa.JPABatch;
import org.pikater.shared.database.jpa.JPAUser;
import org.pikater.shared.database.jpa.daos.DAOs;
import org.pikater.shared.database.views.tableview.AbstractTableRowDBView;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBRow;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBViewUserSaved;
import org.pikater.shared.database.views.tableview.batches.BatchTableDBViewUserScheduled;
import org.pikater.shared.experiment.UniversalComputationDescription;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.config.WebAppConfiguration;
import org.pikater.web.vaadin.UserAuth;
import org.pikater.web.vaadin.gui.client.components.kineticcomponent.KineticComponentState;
import org.pikater.web.vaadin.gui.server.components.dbviews.BatchDBViewRoot;
import org.pikater.web.vaadin.gui.server.components.dbviews.base.tableview.DBTableLayout;
import org.pikater.web.vaadin.gui.server.components.forms.SaveExperimentForm;
import org.pikater.web.vaadin.gui.server.components.forms.SaveExperimentForm.ExperimentSaveMode;
import org.pikater.web.vaadin.gui.server.components.popups.MyNotifications;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.GeneralDialogs;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.DialogCommons.IDialogComponent;
import org.pikater.web.vaadin.gui.server.layouts.verticalgrouplayout.VerticalGroupLayout;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor.ExpEditorToolbox;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent.IOnExperimentSaved;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.ClickMode;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Slider;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.MessageBox;

/**
 * Toolbar for {@link ExpEditor}, displayed at the top of the page.
 * 
 * @author SkyCrawl
 */
public class Toolbar extends VerticalLayout {
	private static final long serialVersionUID = 3627484964167144046L;

	/**
	 * Constant reference to the callback parent.
	 */
	private final ExpEditor parentEditor;

	/*
	 * References to dynamic inner components.
	 */
	private ComboBox clickModeCB;

	public Toolbar(ExpEditor parentEditor, boolean debugMode) {
		super();
		setSizeFull();

		this.parentEditor = parentEditor;
		this.clickModeCB = null;

		buildMenuBar(debugMode);
		buildToolbar();
	}

	//---------------------------------------------------------------
	// PUBLIC METHODS

	/**
	 * Callback for when experiment tab is changed so that the toolbar
	 * may be "reset" to display correct values for the given tab.
	 */
	public void onTabSelectionChange(KineticComponent newActiveTabContent) {
		if (newActiveTabContent != null) {
			clickModeCB.select(newActiveTabContent.getState().clickMode.name());
			clickModeCB.setEnabled(true);
		} else {
			clickModeCB.setEnabled(false);
		}
	}

	/**
	 * Callback from client, when user changes click mode by a
	 * keyboard shortcut. 
	 */
	public void onClickModeAlteredOnClient(ClickMode newClickMode) {
		clickModeCB.select(newClickMode.name());
	}

	//---------------------------------------------------------------
	// INTERFACE BUILDING METHODS

	private void buildMenuBar(boolean debugMode) {
		MenuBar menu = new MenuBar();
		menu.setSizeFull();
		menu.setStyleName("menu");

		MenuItem experimentMenuItem = menu.addItem("Experiment", null);
		experimentMenuItem.setStyleName("menu-topLevelItem");
		experimentMenuItem.addItem("Load into a new tab...", new Command() {
			private static final long serialVersionUID = -2425266907437419549L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				loadArbitraryExperimentIntoANewTab();
			}
		});

		MenuItem activeTabMenuItem = menu.addItem("Active tab", null);
		activeTabMenuItem.setStyleName("menu-topLevelItem");
		activeTabMenuItem.addItem("Save for later...", new Command() {
			private static final long serialVersionUID = -8383604249370403859L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						saveExperimentInActiveTab(activeComponent, ExperimentSaveMode.SAVE_FOR_LATER);
					}
				}, true);
			}
		});
		activeTabMenuItem.addItem("Queue for execution...", new Command() {
			private static final long serialVersionUID = -8383604249370403859L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						saveExperimentInActiveTab(activeComponent, ExperimentSaveMode.SAVE_FOR_EXECUTION);
					}
				}, true);
			}
		});

		MenuItem viewMenuItem = menu.addItem("View", null);
		viewMenuItem.setStyleName("menu-topLevelItem");
		/*
		viewMenuItem.addItem("Go full screen (only FF, Chrome & Opera)", new Command()
		{
			private static final long serialVersionUID = 4109313735302030716L;

			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				// TODO: maybe in future
				MyNotifications.showWarning(null, "Not implemented yet.");
			}
		});
		viewMenuItem.addSeparator();
		*/
		for (final ExpEditorToolbox toolbox : ExpEditorToolbox.values()) {
			viewMenuItem.addItem(String.format("Open %s", toolbox.toDisplayName().toLowerCase()), new Command() {
				private static final long serialVersionUID = -3102966401422712532L;

				@Override
				public void menuSelected(MenuItem selectedItem) {
					parentEditor.openToolbox(toolbox);
				}
			});
		}

		addComponent(menu);
	}

	private void buildToolbar() {
		/*
		 * Create visual style group layout.
		 */

		CheckBox chb_allowBoxIcons = new CheckBox("box icons visible", KineticComponentState.getBoxIconsVisibleByDefault());
		chb_allowBoxIcons.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -7751913095102968151L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						activeComponent.getState().boxIconsVisible = (Boolean) event.getProperty().getValue();
					}
				}, true);
			}
		});

		Slider slider_boxSize = new Slider(0, 2, 2);
		slider_boxSize.setWidth("200px");
		slider_boxSize.setImmediate(true);
		slider_boxSize.setValue(KineticComponentState.getDefaultScale());
		slider_boxSize.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 6756349322188831368L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						activeComponent.getState().boxScale = (Double) event.getProperty().getValue();
					}
				}, true);
			}
		});

		HorizontalLayout hLayout_slider = new HorizontalLayout();
		hLayout_slider.setSpacing(true);
		hLayout_slider.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		hLayout_slider.addComponent(new Label("Box size (%):"));
		hLayout_slider.addComponent(slider_boxSize);

		VerticalGroupLayout groupLayout_visualStyle = new VerticalGroupLayout();
		groupLayout_visualStyle.setHeight("100%");
		groupLayout_visualStyle.setInnerLayout("Visual style:", new VerticalLayout());
		groupLayout_visualStyle.getInnerLayout().addComponent(hLayout_slider);
		groupLayout_visualStyle.getInnerLayout().addComponent(chb_allowBoxIcons);

		/*
		 * Create various settings group layout.
		 */

		this.clickModeCB = new ComboBox();
		for (ClickMode clickMode : ClickMode.values()) {
			this.clickModeCB.addItem(clickMode.name());
		}
		this.clickModeCB.setImmediate(true);
		this.clickModeCB.setNullSelectionAllowed(false);
		this.clickModeCB.setTextInputAllowed(false);
		this.clickModeCB.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5032992287714560567L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						activeComponent.getState().clickMode = ClickMode.valueOf((String) clickModeCB.getValue());
					}
				}, true);
			}
		});
		this.clickModeCB.setEnabled(false);

		CheckBox chb_bindSelectionWithBoxManager = new CheckBox("bind box manager with selection changes", KineticComponentState.getBoxManagerBoundWithSelectionByDefault());
		chb_bindSelectionWithBoxManager.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -7751913095102968151L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				executeForNonNullActiveTab(new IActiveKineticComponentAction() {
					@Override
					public void doAction(KineticComponent activeComponent) {
						activeComponent.getState().boxManagerBoundWithSelection = (Boolean) event.getProperty().getValue();
					}
				}, true);
			}
		});

		HorizontalLayout hLayout_boxClickMode = new HorizontalLayout();
		hLayout_boxClickMode.setSpacing(true);
		hLayout_boxClickMode.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		hLayout_boxClickMode.addComponent(new Label("Box click mode:"));
		hLayout_boxClickMode.addComponent(this.clickModeCB);

		VerticalGroupLayout groupLayout_variousSettings = new VerticalGroupLayout();
		groupLayout_variousSettings.setHeight("100%");
		groupLayout_variousSettings.setInnerLayout("Various settings:", new VerticalLayout());
		groupLayout_variousSettings.getInnerLayout().addComponent(hLayout_boxClickMode);
		groupLayout_variousSettings.getInnerLayout().addComponent(chb_bindSelectionWithBoxManager);

		HorizontalLayout toolbarLayout = new HorizontalLayout();
		toolbarLayout.setStyleName("toolbar");
		toolbarLayout.setSpacing(true);
		toolbarLayout.addComponent(groupLayout_visualStyle);
		toolbarLayout.addComponent(groupLayout_variousSettings);

		addComponent(toolbarLayout);
	}

	//---------------------------------------------------------------
	// UNTRIVIAL MENU ACTIONS (AFTER CHECKS)

	private void saveExperimentInActiveTab(final KineticComponent activeComponent, final ExperimentSaveMode saveMode) {
		try {
			activeComponent.exportExperiment(saveMode == ExperimentSaveMode.SAVE_FOR_EXECUTION, new KineticComponent.IOnExperimentExported() {
				private JPAUser experimentOwner;
				private JPABatch sourceExperiment;
				private String experimentXML;

				@Override
				public void handleExperiment(final UniversalComputationDescription exportedExperiment, final IOnExperimentSaved experimentSavedCallback) {
					// first check for the right conditions
					switch (saveMode) {
					case SAVE_FOR_LATER:
						if (!activeComponent.isContentModified()) {
							MyNotifications.showInfo("Nothing to save", "Content is not modified.");
							return;
						}
						break;
					case SAVE_FOR_EXECUTION:
						if (!exportedExperiment.isValid()) {
							MyNotifications.showInfo("Experiment not valid", "Can not schedule it.");
							return;
						}
						break;
					default:
						throw new IllegalStateException("Unknown state: " + saveMode.name());
					}

					// and then actually do something
					final boolean sourceExperimentExists = activeComponent.getPreviouslyLoadedExperimentID() != null;
					if (sourceExperimentExists) {
						sourceExperiment = DAOs.batchDAO.getByID(activeComponent.getPreviouslyLoadedExperimentID());
					} else {
						sourceExperiment = null;
					}
					final SaveExperimentForm seForm = new SaveExperimentForm(saveMode, sourceExperiment);
					MessageBox dialog = GeneralDialogs.componentDialog("Save experiment from active tab", seForm, new DialogCommons.IDialogResultHandler() {
						@Override
						public boolean handleResult(Object[] args) {
							experimentOwner = UserAuth.getUserEntity(VaadinSession.getCurrent());
							experimentXML = exportedExperiment.toXML();
							switch (saveMode) {
							case SAVE_FOR_LATER:
								String name = (String) args[0];
								String note = (String) args[1];
								switch (seForm.getSaveForLaterMode()) {
								case SAVE_AS_NEW:
									saveExperiment(new JPABatch(name, note, experimentXML, experimentOwner), experimentSavedCallback);
									break;
								case OVERWRITE_PREVIOUS:
									sourceExperiment.setName(name);
									sourceExperiment.setNote(note);
									sourceExperiment.setXML(experimentXML);
									updateExperiment(sourceExperiment, experimentSavedCallback);
									break;
								case SAVE_AS_NEW_AND_DELETE_PREVIOUS:
									saveExperiment(new JPABatch(name, note, experimentXML, experimentOwner), experimentSavedCallback);
									if (sourceExperimentExists) {
										DAOs.batchDAO.deleteBatchEntity(sourceExperiment);
									}
									break;
								default:
									throw new IllegalStateException("Unknown state: " + seForm.getSaveForLaterMode().name());
								}
								return true;
							case SAVE_FOR_EXECUTION:
								saveForExecution(args, experimentSavedCallback);
								return true;
							default:
								throw new IllegalStateException("Unknown state: " + saveMode.name());
							}
						}
					});
					dialog.setWidth("400px");
				}

				private void saveForExecution(Object[] args, IOnExperimentSaved experimentSavedCallback) {
					String name = (String) args[0];
					Integer userAssignedPriority = (Integer) args[1];
					Boolean sendEmailWhenFinished = (Boolean) args[2];
					String note = (String) args[3];

					JPABatch newExperiment = new JPABatch(name, note, experimentXML, experimentOwner, userAssignedPriority, sendEmailWhenFinished);
					saveExperiment(newExperiment, experimentSavedCallback);
					if (WebAppConfiguration.isCoreEnabled()) {
						try {
							WebToCoreEntryPoint.notify_newBatch(newExperiment.getId(), experimentOwner.getId());
						} catch (PikaterGatewayException e) {
							PikaterWebLogger.logThrowable("Could not send notification about a new batch to core.", e);
							GeneralDialogs.warning("Failed to notify core", "Your experiment has been saved and designated "
									+ "for execution but notification was not successfully passed to pikater core.");
						}
					} else {
						GeneralDialogs.info("Core not available at this moment", "Your experiment has been saved and designated "
								+ "for execution but the actual execution may be pending until a running pikater core picks your experiment up.");
					}
				}

				private void saveExperiment(JPABatch newExperiment, IOnExperimentSaved experimentSavedCallback) {
					DAOs.batchDAO.storeEntity(newExperiment);
					experimentSavedCallback.experimentSaved(newExperiment);
				}

				private void updateExperiment(JPABatch experiment, IOnExperimentSaved experimentSavedCallback) {
					DAOs.batchDAO.updateEntity(experiment);
					experimentSavedCallback.experimentSaved(experiment);
				}
			});
		} catch (Exception t) {
			PikaterWebLogger.logThrowable("Could not save experiment", t);
			MyNotifications.showApplicationError();
		}
	}

	private void loadArbitraryExperimentIntoANewTab() {
		MessageBox dialog = GeneralDialogs.componentDialog("Choose experiment to load into a new tab", new LoadExperimentComponent() {
			private static final long serialVersionUID = -3405189166030944018L;

			@Override
			public boolean handleResult(Object[] args) {
				parentEditor.loadExperimentIntoNewTab((JPABatch) args[0]);
				return true;
			}
		});
		dialog.setWidth("600px");
	}

	//---------------------------------------------------------------
	// MISCELLANEOUS PRIVATE INTERFACE

	/**
	 * Experiment tabsheet may be empty (no tabs present). This method
	 * avoids errors when trying to execute tab-related code.
	 */
	private void executeForNonNullActiveTab(IActiveKineticComponentAction action, boolean displayWarningIfNull) {
		KineticComponent activeComponent = parentEditor.getActiveKineticComponent();
		if (activeComponent != null) {
			action.doAction(activeComponent);
		} else if (displayWarningIfNull) {
			MyNotifications.showWarning(null, "No tabs have been created.");
		}
	}

	//---------------------------------------------------------------
	// PRIVATE TYPES

	private interface IActiveKineticComponentAction {
		void doAction(KineticComponent activeComponent);
	}

	/**
	 * Special component used in dialogs with which experiments may be
	 * chosen to load. Both types of experiments may be loaded - either
	 * saved experiments not yet queued or experiments queued in the past.
	 * 
	 * @author SkyCrawl
	 */
	private abstract class LoadExperimentComponent extends TabSheet implements IDialogComponent {
		private static final long serialVersionUID = -7500075637433995569L;

		private JPABatch experimentToLoad;

		public LoadExperimentComponent() {
			super();
			setStyleName("loadExperimentDialogContent");

			JPAUser currentUser = UserAuth.getUserEntity(VaadinSession.getCurrent());

			final DBTableLayout savedExperimentsLayout = new DBTableLayout();
			savedExperimentsLayout.setSizeFull();
			savedExperimentsLayout.setReadOnly(true);
			savedExperimentsLayout.getTable().setMultiSelect(false); // required below
			savedExperimentsLayout.getTable().addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = -2896004727434158169L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					/*
					 * Table in 'savedExperimentsLayout' is required to have multi-select disabled.
					 */
					AbstractTableRowDBView[] selectedRowViews = savedExperimentsLayout.getTable().getViewsOfSelectedRows();
					if (selectedRowViews.length == 0) {
						experimentToLoad = null;
					} else {
						experimentToLoad = ((BatchTableDBRow) selectedRowViews[0]).getBatch();
					}
				}
			});
			savedExperimentsLayout.setView(new BatchDBViewRoot<BatchTableDBViewUserSaved>(new BatchTableDBViewUserSaved(currentUser)));

			final DBTableLayout scheduledExperimentsLayout = new DBTableLayout();
			scheduledExperimentsLayout.setSizeFull();
			scheduledExperimentsLayout.setReadOnly(true);
			scheduledExperimentsLayout.getTable().setMultiSelect(false); // required below
			scheduledExperimentsLayout.getTable().addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = 5024393452584477476L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					/*
					 * Table in 'scheduledExperimentsLayout' is required to have multi-select disabled.
					 */
					AbstractTableRowDBView[] selectedRowViews = scheduledExperimentsLayout.getTable().getViewsOfSelectedRows();
					if (selectedRowViews.length == 0) {
						experimentToLoad = null;
					} else {
						experimentToLoad = ((BatchTableDBRow) selectedRowViews[0]).getBatch();
					}
				}
			});
			scheduledExperimentsLayout.setView(new BatchDBViewRoot<BatchTableDBViewUserScheduled>(new BatchTableDBViewUserScheduled(currentUser)));

			addTab(savedExperimentsLayout, "Saved experiments");
			addTab(scheduledExperimentsLayout, "Scheduled experiments");

			this.experimentToLoad = null;
		}

		@Override
		public boolean isResultReadyToBeHandled() {
			return experimentToLoad != null;
		}

		@Override
		public void addArgs(List<Object> arguments) {
			arguments.add(experimentToLoad);
		}
	}
}
