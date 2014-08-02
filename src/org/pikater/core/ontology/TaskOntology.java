package org.pikater.core.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;

import org.pikater.core.ontology.subtrees.batchDescription.EvaluationMethod;
import org.pikater.core.ontology.subtrees.batchDescription.durarion.LongTermDuration;
import org.pikater.core.ontology.subtrees.data.Datas;
import org.pikater.core.ontology.subtrees.newOption.base.NewOption;
import org.pikater.core.ontology.subtrees.newOption.base.ValueType;
import org.pikater.core.ontology.subtrees.newOption.restrictions.IRestriction;
import org.pikater.core.ontology.subtrees.newOption.values.BooleanValue;
import org.pikater.core.ontology.subtrees.task.Task;


public class TaskOntology extends BeanOntology {

	private static final long serialVersionUID = 4471377586541937606L;

	private TaskOntology() {
        super("TaskOntology");

        String taskPackage = Task.class.getPackage().getName();
        String dataPackage = Datas.class.getPackage().getName();
        String durationPackage = LongTermDuration.class.getPackage().getName();
        
        String optionPackage = NewOption.class.getPackage().getName();
        String restrictionPackage = IRestriction.class.getPackage().getName();
        String typePackage = ValueType.class.getPackage().getName();
        String valuePackage = BooleanValue.class.getPackage().getName();
        
        try {

            add(taskPackage);
            add(dataPackage);
            
            add(durationPackage);
            
            add(EvaluationMethod.class);
            
            add(optionPackage);
            add(restrictionPackage);
            add(typePackage);
            add(valuePackage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static TaskOntology theInstance = new TaskOntology();

    public static Ontology getInstance() {
        return theInstance;
    }
}