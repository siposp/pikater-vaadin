package org.pikater.web.visualisation.implementation.generator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.definition.result.AbstractDSVisResult;
import org.pikater.web.visualisation.implementation.generator.base.Generator;
import org.pikater.web.visualisation.implementation.renderer.SVGRenderer;

/**
 * Implements functionality to create visualisation chart for one data set in SVG format.
 * 
 * @author siposp
 *
 */
public class SingleSVGGenerator extends SingleGenerator {
	
	public SingleSVGGenerator(AbstractDSVisResult<?, ?> progressListener, JPADataSetLO dslo, File datasetCachedFile, PrintStream output, String XName, String YName, String ColorName) {
		super(progressListener, dslo, datasetCachedFile, output, XName, YName, ColorName);
		initRenderer();
	}

	private void initRenderer() {
		this.renderer = new SVGRenderer(output, Generator.DEFAULTCHARTSIZE, Generator.DEFAULTCHARTSIZE);
	}

	@Override
	public void create() throws IOException {
		super.create();
		output.close();
	}

}
