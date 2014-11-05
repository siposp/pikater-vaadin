package org.pikater.web.visualisation.implementation.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.implementation.exceptions.ChartException;
import org.pikater.web.visualisation.implementation.generator.base.Generator;
import org.pikater.web.visualisation.implementation.renderer.ImageRenderer;

/**
 * Implements functionality to create visualisation chart in PNG format for two data sets.
 * 
 * @author siposp
 *
 */
public class ComparisonPNGGenerator extends ComparisonGenerator {

	public ComparisonPNGGenerator(AbstractDSVisResult<?, ?> progressLstener, PrintStream output, JPADataSetLO dslo1, JPADataSetLO dslo2, File datasetCachedFile1, File datasetCachedFile2,
			String XName1, String XName2, String YName1, String YName2, String ColorName1, String ColorName2) {
		super(progressLstener, output, dslo1, dslo2, datasetCachedFile1, datasetCachedFile2, XName1, XName2, YName1, YName2, ColorName1, ColorName2);
		initRenderer();
	}

	private void initRenderer() {
		renderer = new ImageRenderer(Generator.DEFAULTCHARTSIZE, Generator.DEFAULTCHARTSIZE);
	}

	/**
	 * <p>
	 * Creates the comparison chart for two datasets, that have previously been set for this {@link ComparisonPNGGenerator}.
	 * </p>
	 * <p>
	 * The resulted image is in PNG format.
	 * </p>
	 * @Override
	 * {@link ComparisonGenerator#create()}
	 */
	@Override
	public void create() throws IOException, ChartException {
		try {
			super.create();
			BufferedImage outIm = ((ImageRenderer) renderer).getImage();
			ImageIO.write(outIm, "PNG", output);
		} finally {
			output.close();
		}
	}

}
