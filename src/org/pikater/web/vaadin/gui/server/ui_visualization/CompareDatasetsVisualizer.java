package org.pikater.web.vaadin.gui.server.ui_visualization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.pikater.web.vaadin.gui.server.StyleBuilder;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixLayoutHeaderProvider;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixDataSource;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.MatrixLayout;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI.DSVisTwoUIArgs;
import org.pikater.web.vaadin.gui.server.ui_visualization.thumbnail.ChartThumbnail;
import org.pikater.web.visualisation.definition.AttrMapping;
import org.pikater.web.visualisation.definition.result.DSVisTwoSubresult;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Master component viewing the results of dataset comparison.
 * 
 * @author SkyCrawl
 */
public class CompareDatasetsVisualizer extends VerticalLayout {
	private static final long serialVersionUID = 3682122092659178186L;

	private final Map<AttrMapping, String> indexToLabel;

	public CompareDatasetsVisualizer(final DSVisTwoUIArgs arguments) {
		super();
		setSizeFull();
		setStyleName("visualizationComponent");
		setSpacing(true);

		// define the header
		Label lbl_visType = new Label("DATASET COMPARISON");
		Label lbl_leftDataset = new Label(String.format("LEFT ATTRIBUTES: '%s'", arguments.getDataset1().getFileName()));
		Label lbl_topDataset = new Label(String.format("TOP ATTRIBUTES: '%s'", arguments.getDataset2().getFileName()));

		// define data source
		final IMatrixDataSource<AttrMapping, DSVisTwoSubresult> resultMatrixView = arguments.getGeneratedResult().toMatrixView();
		this.indexToLabel = new HashMap<AttrMapping, String>();
		registerLabelsFor(1, resultMatrixView.getLeftIndexSet());
		registerLabelsFor(2, resultMatrixView.getTopIndexSet());

		// create the matrix layout
		Panel matrixContainer = new Panel();
		MatrixLayout<AttrMapping, ChartThumbnail> matrixLayout = new MatrixLayout<AttrMapping, ChartThumbnail>(new IMatrixDataSource<AttrMapping, ChartThumbnail>() {
			@Override
			public Collection<AttrMapping> getLeftIndexSet() {
				return resultMatrixView.getLeftIndexSet(); // just keep insertion order
			}

			@Override
			public Collection<AttrMapping> getTopIndexSet() {
				return resultMatrixView.getTopIndexSet(); // just keep insertion order
			}

			@Override
			public ChartThumbnail getElement(final AttrMapping leftIndex, final AttrMapping topIndex) {
				DSVisTwoSubresult subresult = resultMatrixView.getElement(leftIndex, topIndex);
				if (subresult != null) {
					/* 
					 * If exactly this pair has a result generated (they may not since attribute
					 * mappings are defined in pairs to begin with...). 
					 */

					return new ChartThumbnail(subresult, arguments.getGeneratedResult().getImageWidth(), arguments.getGeneratedResult().getImageHeight()) {
						private static final long serialVersionUID = 6546809395856687897L;

						@Override
						protected String getContentCaption() {
							return String.format("%s X %s", getLabelForAttributes(leftIndex), getLabelForAttributes(topIndex));
						}
					};
				} else {
					return null;
				}
			}

		}, new IMatrixLayoutHeaderProvider<AttrMapping>() {
			@Override
			public Label getCaptionComponent(AttrMapping index) {
				Label result = new Label(getLabelForAttributes(index));
				result.setDescription(String.format(index.toString()));
				return result;
			}
		}, new IFlowLayoutStyleProvider() {
			@Override
			public void setStylesForInnerComponent(Component c, StyleBuilder builder) {
				builder.setProperty("margin", "10px");
			}
		});
		matrixLayout.setSizeFull();
		matrixContainer.setSizeFull();
		matrixContainer.setContent(matrixLayout);

		// and display it
		addComponent(lbl_visType);
		addComponent(lbl_leftDataset);
		addComponent(lbl_topDataset);
		addComponent(matrixContainer);
		setExpandRatio(matrixContainer, 1);
	}

	private void registerLabelsFor(int number, Collection<AttrMapping> attributesList) {
		int index = 0;
		for (AttrMapping attributes : attributesList) {
			this.indexToLabel.put(attributes, String.format("D%d: am%d", number, index));
			index++;
		}
	}

	private String getLabelForAttributes(AttrMapping attributes) {
		if (indexToLabel.containsKey(attributes)) {
			return indexToLabel.get(attributes);
		} else {
			throw new IllegalStateException("The index has not yet been labeled (look into the 'getContentCaption()' method above).");
		}
	}
}