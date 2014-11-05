package org.pikater.web.vaadin.gui.server.ui_visualization;

import java.util.Collection;
import java.util.Comparator;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.shared.util.collections.CustomOrderSet;
import org.pikater.web.vaadin.gui.server.StyleBuilder;
import org.pikater.web.vaadin.gui.server.layouts.flowlayout.IFlowLayoutStyleProvider;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixLayoutHeaderProvider;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixDataSource;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.MatrixLayout;
import org.pikater.web.vaadin.gui.server.ui_visualization.VisualizationUI.DSVisOneUIArgs;
import org.pikater.web.vaadin.gui.server.ui_visualization.thumbnail.ChartThumbnail;
import org.pikater.web.visualisation.definition.result.DSVisOneSubresult;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Master component viewing the results of single dataset visualization.
 * 
 * @author SkyCrawl
 */
public class SingleDatasetVisualizer extends VerticalLayout {
	private static final long serialVersionUID = 8665905099485047156L;

	public SingleDatasetVisualizer(final DSVisOneUIArgs arguments) {
		super();
		setSizeFull();
		setStyleName("visualizationComponent");
		setSpacing(true);

		// define the header
		Label lbl_visType = new Label("SINGLE DATASET VISUALIZATION");
		Label lbl_Dataset = new Label(String.format("DATASET: '%s'", arguments.getDataset().getFileName()));

		// define the matrix view
		Panel matrixContainer = new Panel();
		final IMatrixDataSource<JPAAttributeMetaData, DSVisOneSubresult> resultMatrixView = arguments.getGeneratedResult().toMatrixView();
		MatrixLayout<JPAAttributeMetaData, ChartThumbnail> matrixLayout = new MatrixLayout<JPAAttributeMetaData, ChartThumbnail>(new IMatrixDataSource<JPAAttributeMetaData, ChartThumbnail>() {
			private final Comparator<JPAAttributeMetaData> comp = new Comparator<JPAAttributeMetaData>() {
				@Override
				public int compare(JPAAttributeMetaData o1, JPAAttributeMetaData o2) {
					return o1.getName().compareTo(o2.getName());
				}
			};

			@Override
			public Collection<JPAAttributeMetaData> getLeftIndexSet() {
				return new CustomOrderSet<JPAAttributeMetaData>(resultMatrixView.getLeftIndexSet(), comp);
			}

			@Override
			public Collection<JPAAttributeMetaData> getTopIndexSet() {
				return new CustomOrderSet<JPAAttributeMetaData>(resultMatrixView.getTopIndexSet(), comp);
			}

			@Override
			public ChartThumbnail getElement(final JPAAttributeMetaData leftIndex, final JPAAttributeMetaData topIndex) {
				return new ChartThumbnail(resultMatrixView.getElement(leftIndex, topIndex), arguments.getGeneratedResult().getImageWidth(), arguments.getGeneratedResult().getImageHeight()) {
					private static final long serialVersionUID = 4316865427446770238L;

					@Override
					protected String getContentCaption() {
						return String.format("%s X %s", leftIndex.getName(), topIndex.getName());
					}
				};
			}

		}, new IMatrixLayoutHeaderProvider<JPAAttributeMetaData>() {
			@Override
			public Label getCaptionComponent(JPAAttributeMetaData index) {
				return new Label(index.getName().toUpperCase());
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
		addComponent(lbl_Dataset);
		addComponent(matrixContainer);
		setExpandRatio(matrixContainer, 1);
	}
}