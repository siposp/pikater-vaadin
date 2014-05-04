package org.pikater.core.ontology.actions;

import org.pikater.core.ontology.mailing.SendEmail;
import org.pikater.core.ontology.messages.TranslateFilename;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;

/**
 * Created by Stepan on 4.5.14.
 */
public class MailingOntology extends BeanOntology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5355736320938592917L;

	private MailingOntology() {
        super("MailingOntology");

        try {
            add(SendEmail.class);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static MailingOntology theInstance = new MailingOntology();

    public static Ontology getInstance() {
        return theInstance;
    }
}
