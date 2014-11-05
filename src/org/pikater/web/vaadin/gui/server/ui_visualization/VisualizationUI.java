package org.pikater.web.vaadin.gui.server.ui_visualization;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.sharedresources.IRegistrarResource;
import org.pikater.web.sharedresources.ResourceException;
import org.pikater.web.sharedresources.ResourceExpiration;
import org.pikater.web.sharedresources.ResourceRegistrar;
import org.pikater.web.vaadin.CustomConfiguredUI;
import org.pikater.web.vaadin.CustomConfiguredUIServlet.PikaterUI;
import org.pikater.web.vaadin.gui.server.IUIArguments;
import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.definition.result.DSVisOneResult;
import org.pikater.web.visualisation.definition.result.DSVisTwoResult;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;

/**
 * Displays the generated visualization pages.
 * 
 * @author SkyCrawl
 */
@Title("Visualization")
@Theme("pikater")
@Push(value = PushMode.AUTOMATIC)
@StyleSheet("visualizationUI.css")
public class VisualizationUI extends CustomConfiguredUI {
	private static final long serialVersionUID = -8917289148464357783L;
	private static final String PARAM_RESOURCE = "res";

	private DSVisUIArgs<?> arguments = null;

	@Override
	protected void init(VaadinRequest request) {
		/*
		 * First parse and check needed arguments and if they're not correct, return 404.
		 */
		try {
			UUID resourceID = ResourceRegistrar.toResourceID(request.getParameter(PARAM_RESOURCE));
			arguments = (DSVisUIArgs<?>) ResourceRegistrar.getResource(resourceID);
			arguments.getClass(); // spawn a null pointer exception if resource is not defined
		} catch (Exception e) {
			/*
			 * ResourceException covers known cases that do not need to be logged.
			 */
			returnErrorCode(HttpServletResponse.SC_NOT_FOUND);
			if (!(e instanceof ResourceException)) {
				PikaterWebLogger.logThrowable("An unexpected problem was found:", e);
				throw new RuntimeException(e); // default UI error handler will catch this and display a visible notification
			} else {
				return;
			}
		}

		/*
		 * Don't forget to call this.
		 * IMPORTANT:
		 * 1) You shouldn't update the UI in this method. You only provide the content component
		 * when you're asked to in the {@link #displayChildContent()} method.
		 * 2) When {@link #displayChildContent()} is called, this method is still not finished.
		 * You shouldn't have any initializing code after the super.init() call.
		 */
		super.init(request);
	}

	@Override
	protected void displayChildContent() {
		if (arguments instanceof DSVisOneUIArgs) {
			setContent(new SingleDatasetVisualizer((DSVisOneUIArgs) arguments));
		} else if (arguments instanceof DSVisTwoUIArgs) {
			setContent(new CompareDatasetsVisualizer((DSVisTwoUIArgs) arguments));
		} else {
			returnErrorCode(HttpServletResponse.SC_NOT_IMPLEMENTED, "Unknown arguments object was received: " + arguments.getClass().getSimpleName());
		}
	}

	//-------------------------------------------------------------------------------
	// SPECIAL TYPES

	/**
	 * Abstract {@link IUIArguments arguments} class for {@link VisualizationUI}.
	 * 
	 * @author SkyCrawl
	 *
	 * @param <T> Generated subresult type.
	 */
	public abstract static class DSVisUIArgs<T extends AbstractDSVisResult<?, ?>> implements IUIArguments, IRegistrarResource {
		private final T generatedResult;

		public DSVisUIArgs(T generatedResult) {
			this.generatedResult = generatedResult;
		}

		public T getGeneratedResult() {
			return generatedResult;
		}

		@Override
		public ResourceExpiration getLifeSpan() {
			return ResourceExpiration.ON_SESSION_END;
		}

		@Override
		public String toRedirectURL() {
			try {
				UUID thisClassesResourceID = ResourceRegistrar.registerResource(VaadinSession.getCurrent(), this);
				return CustomConfiguredUI.getRedirectURLToUI(PikaterUI.DATASET_VISUALIZATION)
						+ String.format("?%s=%s", VisualizationUI.PARAM_RESOURCE, ResourceRegistrar.fromResourceID(thisClassesResourceID));
			} catch (Exception e) {
				// ResourceRegistrar.handleError(e, resp); // whatever the case here, we want it logged
				PikaterWebLogger.logThrowable("Could not issue a redirect because:", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Arguments for single dataset visualization.
	 * 
	 * @author SkyCrawl
	 */
	public static class DSVisOneUIArgs extends DSVisUIArgs<DSVisOneResult> {
		private final JPADataSetLO dataset;

		public DSVisOneUIArgs(JPADataSetLO dataset, DSVisOneResult generatedResult) {
			super(generatedResult);
			this.dataset = dataset;
		}

		public JPADataSetLO getDataset() {
			return dataset;
		}
	}

	/**
	 * Arguments for dataset comparison.
	 * 
	 * @author SkyCrawl
	 */
	public static class DSVisTwoUIArgs extends DSVisUIArgs<DSVisTwoResult> {
		private final JPADataSetLO dataset1;
		private final JPADataSetLO dataset2;

		public DSVisTwoUIArgs(JPADataSetLO dataset1, JPADataSetLO dataset2, DSVisTwoResult generatedResult) {
			super(generatedResult);
			this.dataset1 = dataset1;
			this.dataset2 = dataset2;
		}

		public JPADataSetLO getDataset1() {
			return dataset1;
		}

		public JPADataSetLO getDataset2() {
			return dataset2;
		}
	}
}