package org.pikater.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pikater.core.CoreConstant.SlotCategory;
import org.pikater.core.agents.gateway.WebToCoreEntryPoint;
import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.core.ontology.subtrees.agentinfo.AgentInfos;
import org.pikater.core.ontology.subtrees.agentinfo.Slot;
import org.pikater.core.ontology.subtrees.newoption.NewOptions;
import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.core.ontology.subtrees.newoption.restrictions.RangeRestriction;
import org.pikater.core.ontology.subtrees.newoption.restrictions.SetRestriction;
import org.pikater.core.ontology.subtrees.newoption.values.BooleanValue;
import org.pikater.core.ontology.subtrees.newoption.values.DoubleValue;
import org.pikater.core.ontology.subtrees.newoption.values.FloatValue;
import org.pikater.core.ontology.subtrees.newoption.values.IntegerValue;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkRange;
import org.pikater.core.ontology.subtrees.newoption.values.QuestionMarkSet;
import org.pikater.core.ontology.subtrees.newoption.values.interfaces.IValueData;
import org.pikater.web.experiment.server.BoxType;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.kineticcomponent.KineticComponent;

/**
 * <p>Web application's wrapper for core-defined agents. Agents correspond to "boxes" in
 * experiment editor (1:1) and each {@link KineticComponent} has its own instance (it is
 * created every time a user opens or refreshes the experiment editor in browser).</p>
 * 
 * <p>Instances of this class are immutable. Use {@link #getFrom(AgentInfos)} and
 * {@link #getDummy()} to create new.
 * 
 * @author SkyCrawl
 */
public class KnownCoreAgents implements Iterable<AgentInfo> {
	/**
	 * Inner collection holding all currently known agent definitions.
	 */
	private final Set<AgentInfo> knownAgents;

	private KnownCoreAgents() {
		this.knownAgents = new HashSet<AgentInfo>();
	}

	@Override
	public Iterator<AgentInfo> iterator() {
		return knownAgents.iterator();
	}

	/**
	 * This method is required to either return the requested object or throw an exception. 
	 */
	public AgentInfo getUnique(String ontologyClassName, String agentClassName) {
		List<AgentInfo> result = new ArrayList<AgentInfo>();
		for (AgentInfo info : knownAgents) {
			if (info.isIdentifiedBy(ontologyClassName, agentClassName)) {
				result.add(info);
			}
		}
		switch (result.size()) {
		case 0:
			throw new IllegalStateException(String.format("No agent info was found for ontology class " + "name '%s' and agent class name '%s'.", ontologyClassName, agentClassName));
		case 1:
			return result.get(0);
		default:
			throw new IllegalStateException("Duplicate AgentInfo found by ontology and agent class names.");
		}
	}

	/**
	 * Queries for all agents with the given type, returned as a {@link List}.
	 */
	public List<AgentInfo> getAllByType(BoxType type) {
		List<AgentInfo> result = new ArrayList<AgentInfo>();
		for (AgentInfo info : knownAgents) {
			if (type.toOntologyClass().getName().equals(info.getOntologyClassName())) {
				result.add(info);
			}
		}
		return result;
	}

	//---------------------------------------------------------
	// PRIVATE INTERFACE

	private boolean addAgent(AgentInfo info) {
		return knownAgents.add(info);
	}

	//---------------------------------------------------------
	// SOME "FACTORY" METHODS

	/**
	 * This method should be used when communication with core system is enabled
	 * (see {@link WebAppConfiguration}).
	 * @param agentsFromCore A list of known agents exported from core system. See
	 * {@link WebToCoreEntryPoint}.
	 */
	public static KnownCoreAgents getFrom(AgentInfos agentsFromCore) {
		KnownCoreAgents agentInfoCollection = new KnownCoreAgents();
		for (AgentInfo info : agentsFromCore.getAgentInfos()) {
			agentInfoCollection.addAgent(info);
		}
		return agentInfoCollection;
	}

	/**
	 * <p>An alternative for whenever using {@link #getFrom(AgentInfos)} is not desirable.</p>
	 * 
	 * Returns a generated list of agents/boxes. Returned definitions may be altered, removed or added
	 * at will in this method. Restrictions:
	 * <ul>
	 * <li> The generated options and slots need to be valid.
	 * <li> Agent names should have distinct names.
	 * <li> Option and slot names (if both input or both output) MUST be distinct within a  single agent. 
	 * </ul> 
	 */
	public static KnownCoreAgents getDummy() {
		KnownCoreAgents knownAgents = new KnownCoreAgents();
		for (BoxType type : BoxType.values()) {
			AgentInfo agentInfo = new AgentInfo();
			agentInfo.setOntologyClassName(type.toOntologyClass().getName());
			agentInfo.setAgentClassName(KnownCoreAgents.class.getName());
			agentInfo.setDescription(String.format("Some kind of a '%s' box.", type.name()));

			String name = null;
			switch (type) {
			case CHOOSE:
				name = "DummyChooser";
				break;
			case COMPUTE:
				name = "DummyComputeAgent";
				break;
			case PROCESS_DATA:
				name = "DummyDataProcessor";
				break;
			case OPTION:
				name = "DummyOptionModifier";
				break;
			case INPUT:
				name = "DumyInputBox";
				break;
			case MISC:
				name = "DummyBox";
				break;
			case OUTPUT:
				name = "DummyOutputBox";
				break;
			case SEARCH:
				name = "DummySearchAgent";
				break;
			case COMPOSITE:
				name = "DummyComposite";
				break;
			default:
				throw new IllegalStateException("Unknown box type: " + type.name());
			}
			agentInfo.setName(name);

			NewOptions options = new NewOptions();
			options.addOption(new NewOption("IntRange", new IntegerValue(5), new RangeRestriction(new IntegerValue(2), new IntegerValue(10))));
			options.addOption(new NewOption("IntSet", new IntegerValue(5), new SetRestriction(false, new ArrayList<IValueData>(Arrays.asList(new IntegerValue(2), new IntegerValue(3),
					new IntegerValue(5), new IntegerValue(10))))));
			options.addOption(new NewOption("Double", new DoubleValue(1)));
			options.addOption(new NewOption("Boolean", new BooleanValue(true)));
			options.addOption(new NewOption("Float", new FloatValue(1)));

			options.addOption(new NewOption("QuestionMarkRange", new QuestionMarkRange(new IntegerValue(5), new IntegerValue(10), 3)));
			options.addOption(new NewOption("QuestionMarkSet", new QuestionMarkSet(3, new ArrayList<IValueData>(Arrays.asList(new IntegerValue(5), new IntegerValue(6), new IntegerValue(9),
					new IntegerValue(10), new IntegerValue(11))))));

			Slot slotInput_test = new Slot("testSlot", SlotCategory.DATA_GENERAL, "Test input slot.");
			Slot slotOutput_test = new Slot("testSlot", SlotCategory.DATA_GENERAL, "Test output slot.");

			agentInfo.setOptions(options);
			agentInfo.setInputSlots(Arrays.asList(slotInput_test));
			agentInfo.setOutputSlots(Arrays.asList(slotOutput_test));
			knownAgents.addAgent(agentInfo);
		}
		return knownAgents;
	}
}
