package tests.pikater.core.ontology.subtrees.batchDescription;

import java.io.FileNotFoundException;

import org.pikater.core.ontology.subtrees.batchDescription.ComputationDescription;
import org.pikater.core.ontology.subtrees.batchDescription.examples.SearchOnly;
import org.pikater.core.ontology.subtrees.batchDescription.examples.SimpleTraining;
import org.pikater.shared.experiment.UniversalComputationDescription;
import org.pikater.shared.logging.core.ConsoleLogger;

import xmlGenerator.Input01;
import xmlGenerator.Input01_model;
import xmlGenerator.Input02;
import xmlGenerator.Input04;
import xmlGenerator.Input03;
import xmlGenerator.Input05;
import xmlGenerator.Input06;
import xmlGenerator.Input07;
import xmlGenerator.Input08;
import xmlGenerator.Input09;
import xmlGenerator.Input10;

/**
 * 
 * Tests of BatchDescription an Universal format
 *
 */
public class TestBatchDescription {

	public static void test() {

		testComputatingDescription(
				SimpleTraining.createDescription(),
				"SimpleTraining");

		testComputatingDescription(
				SearchOnly.createDescription(),
				"SearchOnly");

		testComputatingDescription(
				Input01.createDescription(),
				"Input01");
		
		testComputatingDescription(
				Input01_model.createDescription(),
				"Input01_model");

		testComputatingDescription(
				Input02.createDescription(),
				"Input02");

		testComputatingDescription(
				Input03.createDescription(),
				"Input03");

		testComputatingDescription(
				Input04.createDescription(),
				"Input04");
		
		testComputatingDescription(
				Input05.createDescription(),
				"Input05");

		testComputatingDescription(
				Input06.createDescription(),
				"Input06");
		
		testComputatingDescription(
				Input07.createDescription(),
				"Input07");

		testComputatingDescription(
				Input08.createDescription(),
				"Input08");

		testComputatingDescription(
				Input09.createDescription(),
				"Input09");
		
		testComputatingDescription(
				Input10.createDescription(),
				"Input10");

	}

	/**
	 * Tests to convert {@link ComputationDescription} to universal format
	 * and back.
	 * 
	 *  @param comDescription {@link  ComputationDescription} batch to test
	 *  @param note
	 * 
	 */
	private static void testComputatingDescription(
			ComputationDescription comDescription, String note) {

		UniversalComputationDescription udescriptinSimpleTraining = comDescription.exportUniversalComputationDescription();
		ComputationDescription comDescription2 =
				ComputationDescription.importUniversalComputationDescription(
						udescriptinSimpleTraining);

		String xml1 = comDescription.exportXML();
		String xml2 = comDescription2.exportXML();

		try {
			comDescription.exportXML("comDescription.xml");
			comDescription2.exportXML("comDescription2.xml");
		} catch (FileNotFoundException e) {
			ConsoleLogger.logThrowable("Unexpected error occured:", e);
		}

	
		if (xml1.equals(xml2)) {
			System.out.println("OK - " + note);
		} else {
			System.out.println("Error - " + note);
		}

	}

}
