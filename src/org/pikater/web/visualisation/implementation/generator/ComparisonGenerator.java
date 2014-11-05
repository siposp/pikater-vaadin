package org.pikater.web.visualisation.implementation.generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.implementation.charts.ComparisonChart;
import org.pikater.web.visualisation.implementation.charts.axis.Axis;
import org.pikater.web.visualisation.implementation.charts.coloring.Colorer;
import org.pikater.web.visualisation.implementation.datasource.single.ArffXYZPoint;
import org.pikater.web.visualisation.implementation.datasource.single.SingleArffDataset;
import org.pikater.web.visualisation.implementation.exceptions.ChartException;
import org.pikater.web.visualisation.implementation.generator.base.Generator;

/**
 * Implements functionality to create visualisation chart for two data sets.
 * 
 * @author siposp
 *
 */
public abstract class ComparisonGenerator extends Generator {

	protected SingleArffDataset dataset1;
	protected SingleArffDataset dataset2;

	protected ComparisonGenerator(AbstractDSVisResult<?, ?> progressHandler, PrintStream output, JPADataSetLO dslo1, JPADataSetLO dslo2, File datasetCachedFile1, File datasetCachedFile2, String XName1,
			String XName2, String YName1, String YName2, String ColorName1, String ColorName2) {
		super(progressHandler, output);
		dataset1 = new SingleArffDataset(dslo1, datasetCachedFile1, XName1, YName1, ColorName1);
		dataset2 = new SingleArffDataset(dslo2, datasetCachedFile2, XName2, YName2, ColorName2);
		init();
	}

	private void init() {
		this.instNum = dataset1.getNumberOfInstances() + dataset2.getNumberOfInstances();
	}

	/**
	 * <p>
	 * Creates the comparison chart for two datasets, that have previously been set for this {@link ComparisonGenerator}.
	 * </p>
	 * @Override
	 * {@link Generator#create()}
	 */
	@Override
	public void create() throws IOException, ChartException {
		try {
			renderer.begin();

			Axis yAxis = Axis.join(dataset1.getYAxis(), dataset2.getYAxis());
			yAxis.setCaption(dataset1.getYAxis().getCaption() + " / " + dataset2.getYAxis().getCaption());
			Axis xAxis = Axis.join(dataset1.getXAxis(), dataset2.getXAxis());
			xAxis.setCaption(dataset1.getXAxis().getCaption() + " / " + dataset2.getXAxis().getCaption());

			Colorer colorer1 = dataset1.getZColorer();
			Colorer colorer2 = dataset2.getZColorer();

			Colorer colorer = colorer1.merge(colorer2);

			ComparisonChart cch = new ComparisonChart(Generator.DEFAULTCHARTSIZE, Generator.DEFAULTCHARTSIZE, renderer, xAxis, yAxis);

			cch.startChart();

			ArffXYZPoint next = null;

			while ((next = dataset1.getNext()) != null) {
				cch.renderSquarePoint(next.getX(), next.getY(), next.getZ(), 30, colorer);
				signalOneProcessedRow();
			}

			while ((next = dataset2.getNext()) != null) {
				cch.renderPoint(next.getX(), next.getY(), next.getZ(), 15, colorer);
				signalOneProcessedRow();
			}

			renderer.end();
		} finally {
			dataset1.close();
			dataset2.close();
		}
	}

}
