package net.edzard.kinetic.event;

public class NamedEventType
{
	private final String eventType;
	private final String eventName;
	
	protected NamedEventType()
	{
		this.eventType = null;
		this.eventName = null;
	}
	
	protected NamedEventType(String eventType)
	{
		this.eventType = eventType;
		this.eventName = null;
	}
	
	protected NamedEventType(String eventType, String eventName)
	{
		this.eventType = eventType;
		this.eventName = eventName;
	}
	
	public NamedEventType withName(String eventName)
	{
		return new NamedEventType(eventType, eventName);
	}
	
	@Override
	public String toString()
	{
		if(eventType == null)
		{
			throw new IllegalStateException("Don't use the default constructor of this class unless you extend it. "
					+ "And if you extend it without specifying an explicit constructor, don't use this method on that object.");
		}
		if(eventName != null)
		{
			StringBuilder sb = new StringBuilder(eventType);
			sb.append('.');
			sb.append(eventName);
			return sb.toString();
		}
		else
		{
			return eventType;
		}
	}
}
