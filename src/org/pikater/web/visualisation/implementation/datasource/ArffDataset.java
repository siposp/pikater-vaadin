package org.pikater.web.visualisation.implementation.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.pikater.shared.database.exceptions.AttributeException;
import org.pikater.shared.database.jpa.JPADataSetLO;
import org.pikater.shared.database.postgre.largeobject.PGLargeObjectReader;
import org.pikater.web.PikaterWebLogger;
import org.pikater.web.visualisation.implementation.charts.axis.Axis;
import org.pikater.web.visualisation.implementation.charts.axis.CategoricalAxis;
import org.pikater.web.visualisation.implementation.charts.axis.ValueAxis;
import org.pikater.web.visualisation.implementation.charts.coloring.CategoricalColorer;
import org.pikater.web.visualisation.implementation.charts.coloring.Colorer;
import org.pikater.web.visualisation.implementation.charts.coloring.LinearColorer;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 * Abstract class implementing some functions for accessing the dataset 
 * 
 * @author siposp
 */
public abstract class ArffDataset {

	private InputStream arffStream;
	private static int READER_CAPACITY = 100;

	protected ArffDataset(InputStream stream) {
		this.arffStream = stream;
		init();
	}

	private JPADataSetLO dslo = null;

	protected ArffDataset(JPADataSetLO source) {
		this.dslo = source;
		initFromJPA();
	}

	protected ArffDataset(JPADataSetLO dslo, File cacheFile) {
		this.dslo = dslo;
		try {
			this.arffStream = new FileInputStream(cacheFile);
		} catch (FileNotFoundException e) {
			this.arffStream = null;
		}
		init();
	}

	private void initFromJPA() {
		try {
			this.arffStream = PGLargeObjectReader.getForLargeObject(dslo.getOID()).getInputStream();
			init();
		} catch (SQLException e) {
			PikaterWebLogger.logThrowable("Could not get a handle on the given large object:", e);
		}
	}

	ArffReader arffReader = null;
	Instances data = null;
	protected boolean datasetInitialized = false;
	protected Instance currentInstance = null;

	private void init() {
		try {
			arffReader = new ArffReader(new BufferedReader(new InputStreamReader(this.arffStream)), READER_CAPACITY);
			data = arffReader.getStructure();
			//data.setClassIndex(data.numAttributes()-1);
			datasetInitialized = true;
		} catch (IOException ioe) {
			datasetInitialized = false;
		}
	}

	/**
	 * Returns the structure of the current dataset
	 * @return {@link Instances} object of the dataset
	 */
	public Instances getInstances() {
		return this.data;
	}

	/**
	 * Returns the index of the attribute for a given name
	 * @param attrName name of the attribute
	 * @return the index of the attribute or -1 if dataset was not yet initialized
	 */
	public int translateAttributeNameToIndex(String attrName) {
		if (data != null) {
			return data.attribute(attrName).index();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the name of the attribute for a given index
	 * @param index of the attribute
	 * @return name of the attribute
	 */
	public String translateAttributeIndexToName(int index) {
		if (data != null) {
			return data.attribute(index).name();
		} else {
			return null;
		}
	}

	/**
	 * Returns the value of the given attribute in current row.
	 * @param attributeIndex index of attribute which value is returned
	 * @return value of the attribute
	 */
	public double getAttributeValue(int attributeIndex) {
		return this.currentInstance.value(attributeIndex);
	}

	/**
	 * Returns the number of attributes.
	 * @return number of attributes
	 */
	public int getNumberOfAttributes() {
		if (data != null) {
			return data.numAttributes();
		} else {
			throw new IllegalStateException("Dataset not initialized.");
		}
	}

	/**
	 * Tries to read a not yet attended item from the dataset.
	 * @return true if the next item was read
	 * @throws IOException
	 */
	public boolean next() throws IOException {
		currentInstance = null;
		if (datasetInitialized) {
			currentInstance = arffReader.readInstance(data);
			if (currentInstance != null) {
				return true;
			}
		} else {
			throw new IllegalStateException("Dataset not initialized.");
		}
		return false;
	}

	/**
	 * <p>Creates the appropriate axis for the dataset's attribute.</p>
	 * <p>
	 * Type of the returned axis depends on the attribute's type in the following way:  
	 * <ul>
	 * <li>if the attribute contains nominal data (several categories) then {@link CategoricalAxis} is returned</li>
	 * <li>if the attribute contains other than nominal data (most probably real numbers) then {@link ValueAxis} is returned.</li>
	 * </ul>
	 * </p>
	 * @param attrIndex index of attribute for which the axis is created
	 * @return the created axis
	 */
	public Axis getAxis(int attrIndex) {
		Attribute attribute = data.attribute(attrIndex);
		Axis res;
		if (attribute.isNominal()) {
			String[] values = new String[attribute.numValues()];
			for (int i = 0; i < values.length; i++) {
				values[i] = attribute.value(i);
			}
			res = new CategoricalAxis(values);
		} else {
			if (dslo != null) {
				try {
					res = new ValueAxis(dslo.getAttributeMinValue(attribute.name()), dslo.getAttributeMaxValue(attribute.name()));
				} catch (AttributeException e) {
					//however, this shouldn't happen, because stream contains the dataset metadata were created for
					PikaterWebLogger.logThrowable("Unexpected error encountered:", e);
					res = new ValueAxis();
				}
			} else {
				res = new ValueAxis();
			}
		}
		res.setCaption(attribute.name());
		return res;
	}

	/**
	 * <p>Creates the appropriate colorer for the dataset's attribute. This colorer is used
	 * to generate the proper color of the point representing the data entry.
	 * </p>
	 * <p>
	 * Type of the returned colorer depends on the attribute's type in the following way:
	 * <ul>
	 * <li>if the attribute contains nominal data (several categories) then {@link CategoricalColorer} is returned</li>
	 * <li>if the attribute contains other than nominal data (most probably real numbers) the {@link LinearColorer} is returned</li>
	 * </ul>
	 * </p>
	 * @param attrIndex index of attribute for which the colorer is created
	 * @return the created colorer
	 */
	public Colorer getColorer(int attrIndex) {
		Attribute attribute = data.attribute(attrIndex);
		if (attribute.isNominal()) {
			return new CategoricalColorer(attribute.numValues());
		} else {
			if (dslo != null) {
				try {
					return new LinearColorer(dslo.getAttributeMinValue(attribute.name()), dslo.getAttributeMaxValue(attribute.name()));
				} catch (AttributeException e) {
					//this shouldn't happen, because stored datasets don't change
					PikaterWebLogger.logThrowable("Unexpected error encountered:", e);
					return new LinearColorer();
				}
			} else {
				return new LinearColorer();
			}
		}
	}

	/**
	 * Returns the number of data entries in the file
	 * @return number of data entries
	 */
	public int getNumberOfInstances() {
		if (dslo != null) {
			return dslo.getGlobalMetaData().getNumberofInstances();
		} else {
			return -1;
		}
	}

	/**
	 * Closes the stream used for accessing the dataset file
	 * @throws IOException
	 */
	public void close() throws IOException {
		arffStream.close();
	}
}
