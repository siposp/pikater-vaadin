package org.pikater.web.visualisation.implementation.datasource.single;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.web.visualisation.implementation.charts.axis.Axis;
import org.pikater.web.visualisation.implementation.charts.coloring.Colorer;
import org.pikater.web.visualisation.implementation.datasource.ArffDataset;

/**
 * Class for accessing an ARFF dataset with the specified attributes for X, Y axis and for point coloring
 *
 * @author siposp
 *
 */
public class SingleArffDataset extends ArffDataset {

	private int attrXIndex;
	private int attrYIndex;
	private int attrColorIndex;

	/**
	 * Creates a new Dataset for an ARFF dataset stream, that can be used for {@link SingleChart} generation.
	 * @param stream The source inputstream for the ARFF Dataset
	 * @param XName The name of attribute, that contains data for X axis
	 * @param YName The name of attribute, that contains data for Y axis
	 * @param ZName The name of attribute, that contains data for point coloring
	 */
	public SingleArffDataset(InputStream stream, String XName, String YName, String ColorName) {
		super(stream);

		this.attrXIndex = this.translateAttributeNameToIndex(XName);
		this.attrYIndex = this.translateAttributeNameToIndex(YName);
		this.attrColorIndex = this.translateAttributeNameToIndex(ColorName);
	}

	/**
	 * Creates a new Dataset for an ARFF dataset stream, that can be used for SingleChart generation
	 * @param stream The source inputstream for the ARFF Dataset
	 * @param XIndex The index of attribute, that contains data for X axis
	 * @param YIndex The index of attribute, that contains data for Y axis
	 * @param ColorIndex The index of attribute, that contains data for point coloring
	 */
	public SingleArffDataset(InputStream stream, int XIndex, int YIndex, int ColorIndex) {
		super(stream);

		this.attrXIndex = XIndex;
		this.attrYIndex = YIndex;
		this.attrColorIndex = ColorIndex;
	}

	/**
	 * Creates a new Dataset for an ARFF dataset stored using {@link JPADataSetLO} object, that can be used for {@link SingleChart} generation
	 * @param dslo The JPADataSetLO entity of the dataset
	 * @param XIndex The index of attribute, that contains data for X axis
	 * @param YIndex The index of attribute, that contains data for Y axis
	 * @param ColorIndex The index of attribute, that contains data for point coloring
	 */
	public SingleArffDataset(JPADataSetLO dslo, int XIndex, int YIndex, int ColorIndex) {
		super(dslo);

		this.attrXIndex = XIndex;
		this.attrYIndex = YIndex;
		this.attrColorIndex = ColorIndex;
	}

	/**
	 * Creates a new Dataset for an ARFF dataset stored using {@link JPADataSetLO} object, that can be used for {@link SingleChart} generation
	 * @param dslo The JPADataSetLO entity of the dataset
	 */
	public SingleArffDataset(JPADataSetLO dslo, String XName, String YName, String ColorName) {
		super(dslo);

		this.attrXIndex = this.translateAttributeNameToIndex(XName);
		this.attrYIndex = this.translateAttributeNameToIndex(YName);
		this.attrColorIndex = this.translateAttributeNameToIndex(ColorName);
	}

	/**
	 * Creates a new Dataset for an ARFF dataset stored using {@link JPADataSetLO} object, that can be used for {@link SingleChart} generation
	 * Dataset should be pre-cached to file datasetCachedFile and the JPA entity is used only for accessing metadata
	 * @param dslo The JPADataSetLO entity of the dataset
	 */
	public SingleArffDataset(JPADataSetLO dslo, File datasetCachedFile, int XIndex, int YIndex, int ColorIndex) {
		super(dslo, datasetCachedFile);

		this.attrXIndex = XIndex;
		this.attrYIndex = YIndex;
		this.attrColorIndex = ColorIndex;
	}

	/**
	 * Creates a new Dataset for an ARFF dataset stored using {@link JPADataSetLO} object, that can be used for {@link SingleChart} generation
	 * Dataset should be pre-cached to file datasetCachedFile and the JPA entity is used only for accessing metadata
	 * @param dslo The JPADataSetLO entity of the dataset
	 */
	public SingleArffDataset(JPADataSetLO dslo, File datasetCachedFile, String XName, String YName, String ColorName) {
		super(dslo, datasetCachedFile);

		this.attrXIndex = this.translateAttributeNameToIndex(XName);
		this.attrYIndex = this.translateAttributeNameToIndex(YName);
		this.attrColorIndex = this.translateAttributeNameToIndex(ColorName);
	}

	/**
	 * Returns the point for the next instance of the original dataset. To retrieve the correct X,Y,Z values
	 * indexes of attributes are used.
	 * @return the next point from the dataset
	 * @throws IOException
	 */
	public ArffXYZPoint getNext() throws IOException {
		if (this.next()) {
			return new ArffXYZPoint(currentInstance.value(this.attrXIndex), currentInstance.value(this.attrYIndex), currentInstance.value(this.attrColorIndex));
		} else {
			return null;
		}
	}

	/**
	 * Returns the X axis for the dataset.
	 * @return {@link Axis} object
	 * @see org.pikater.web.visualisation.implementation.datasource.ArffDataset#getAxis(int)
	 */
	public Axis getXAxis() {
		return this.getAxis(attrXIndex);
	}

	/**
	 * Returns the Y axis for the dataset.
	 * @return {@link Axis} object
	 * @see org.pikater.web.visualisation.implementation.datasource.ArffDataset#getAxis(int)
	 */
	public Axis getYAxis() {
		return this.getAxis(attrYIndex);
	}

	/**
	 * Returns the Colorer for the Z axis.
	 * @return {@link Colorer} object
	 * @see org.pikater.web.visualisation.implementation.datasource.ArffDataset#getColorer(int)
	 */
	public Colorer getZColorer() {
		return this.getColorer(attrColorIndex);
	}

}
