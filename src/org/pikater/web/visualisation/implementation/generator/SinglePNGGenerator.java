package org.pikater.web.visualisation.implementation.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.implementation.generator.base.Generator;
import org.pikater.web.visualisation.implementation.renderer.ImageRenderer;

/**
 * Implements functionality to create visualisation chart for one data set in PNG format.
 * 
 * @author siposp
 *
 */
public class SinglePNGGenerator extends SingleGenerator {

	public SinglePNGGenerator(AbstractDSVisResult<?, ?> progressListener, JPADataSetLO dslo, File datasetCachedFile, PrintStream output, String XName, String YName, String ColorName) {
		super(progressListener, dslo, datasetCachedFile, output, XName, YName, ColorName);
		initRenderer();
	}

	private void initRenderer() {
		this.renderer = new ImageRenderer(Generator.DEFAULTCHARTSIZE, Generator.DEFAULTCHARTSIZE);
	}

	/**
	 * Creates a chart in PNG format for one dataset, that has previously been set for this {@link SingleGenerator}.
	 * @throws IOException when some I/O error occured when accessing data set
	 */
	@Override
	public void create() throws IOException {
		super.create();
		BufferedImage outIm = ((ImageRenderer) renderer).getImage();
		ImageIO.write(outIm, "PNG", output);
		output.close();
		dataset.close();
	}

}
