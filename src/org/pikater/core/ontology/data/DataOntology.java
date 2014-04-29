package org.pikater.core.ontology.data;


import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class DataOntology extends BeanOntology {

	private static final long serialVersionUID = -2595494753081736728L;

	private DataOntology() {
		super("DataOntology");

		String thisPackage = DataOntology.class.getPackage().getName();
		try {
			add(thisPackage);
		} catch (BeanOntologyException e) {
			e.printStackTrace();
		}
	}

	static DataOntology theInstance = new DataOntology();

	public static Ontology getInstance() {
		return theInstance;
	}
}