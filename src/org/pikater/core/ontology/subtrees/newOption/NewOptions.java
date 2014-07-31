package org.pikater.core.ontology.subtrees.newOption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pikater.core.ontology.subtrees.newOption.base.NewOption;

import com.thoughtworks.xstream.XStream;

import jade.content.Concept;

public class NewOptions implements Concept, Iterable<NewOption>
{
	private static final long serialVersionUID = -8578686409784032991L;
	
	private List<NewOption> options;
	
	public NewOptions()
	{
		this.options = new ArrayList<NewOption>();
	}
	public NewOptions(List<NewOption> list)
	{
		setOptions(list);
	}
	
	public List<NewOption> getOptions()
	{
		return options;
	}
	public void setOptions(List<NewOption> list)
	{
		this.options = list;
	}
	
	@Override
	public Iterator<NewOption> iterator()
	{
		return options.iterator();
	}

	public boolean containsOptionWithName(String optionName)
	{
        return getOptionByName(optionName) != null;
	}
    public NewOption getOptionByName(String optionName)
    {
        for (NewOption option : options)
        {
            if (option.getName().equals(optionName))
            {
                return option;
            }
        }
        return null;
    }
    public void addOption(NewOption option)
    {
        this.options.add(option);
    }
    public void addOptions(List<NewOption> options)
    {
        this.options.addAll(options);
    }
    
    public String exportToWeka() {
    	return NewOptions.exportToWeka(options);
    }
    
    public NewOptions clone() {
    	
    	NewOptions optionsOnt = new NewOptions();
    	for (NewOption optionI : this.options) {
    		optionsOnt.addOption(optionI.clone());
    	}
    	
    	return optionsOnt;
    }
    
	public static String exportToWeka(List<NewOption> options) {
		
		String wekaString = "";
		
		for (NewOption optionI : options) {
			wekaString += optionI.exportToWeka() + " ";
		}
		
		return wekaString;
	}
	
	public String exportXML() {

		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);
		
		String xml = xstream.toXML(this);

		return xml;
	}
	
	public static NewOptions importXML(String xml) {

		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);

		NewOptions optionsNew = (NewOptions) xstream
				.fromXML(xml);

		return optionsNew;
	}
}