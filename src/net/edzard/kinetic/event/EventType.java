package net.edzard.kinetic.event;

public class EventType
{
	/**
	 * Can be used with any node except stage.
	 * @author Ed
	 */
	public static class Basic extends NamedEventType
	{
		public static final NamedEventType MOUSEENTER = new NamedEventType("mouseenter");
		public static final NamedEventType MOUSELEAVE = new NamedEventType("mouseleave");
		public static final NamedEventType MOUSEOVER = new NamedEventType("mouseover");
		public static final NamedEventType MOUSEOUT = new NamedEventType("mouseout");
		public static final NamedEventType MOUSEMOVE = new NamedEventType("mousemove");
		public static final NamedEventType MOUSEDOWN = new NamedEventType("mousedown");
		public static final NamedEventType MOUSEUP = new NamedEventType("mouseup");
		public static final NamedEventType CLICK = new NamedEventType("click");
		public static final NamedEventType DBLCLICK = new NamedEventType("dblclick");
		public static final NamedEventType DRAGSTART = new NamedEventType("dragstart");
		public static final NamedEventType DRAGMOVE = new NamedEventType("dragmove");
		public static final NamedEventType DRAGEND = new NamedEventType("dragend");
	}
	
	/**
	 * Can be used with any node except stage. Touch device events only.
	 */
	public static class Touch extends NamedEventType
	{
		public static final NamedEventType TOUCHSTART = new NamedEventType("touchstart");
		public static final NamedEventType TOUCHMOVE = new NamedEventType("touchmove");
		public static final NamedEventType TOUCHEND = new NamedEventType("touchend");
		public static final NamedEventType TAP = new NamedEventType("tap");
		public static final NamedEventType DBLTAP = new NamedEventType("dbltap");
	}
	
	/**
	 * Kinetic stage events only. Don't use with other types.
	 */
	public static class StageBasic extends NamedEventType
	{
		public static final NamedEventType CONTENTMOUSEOVER = new NamedEventType("contentMouseover");
		public static final NamedEventType CONTENTMOUSEMOVE = new NamedEventType("contentMousemove");
		public static final NamedEventType CONTENTMOUSEOUT = new NamedEventType("contentMouseout");
		public static final NamedEventType CONTENTMOUSEDOWN = new NamedEventType("contentMousedown");
		public static final NamedEventType CONTENTMOUSEUP = new NamedEventType("contentMousedown");
		public static final NamedEventType CONTENTCLICK = new NamedEventType("contentClick");
		public static final NamedEventType CONTENTDBLCLICK = new NamedEventType("contentDblclick");
	}
	
	/**
	 * Kinetic stage events only. Don't use with other types. Touch device events only. 
	 */
	public static class StageTouch extends NamedEventType
	{
		public static final NamedEventType CONTENTTOUCHSTART = new NamedEventType("contentTouchstart");
		public static final NamedEventType CONTENTTOUCHMOVE = new NamedEventType("contentTouchmove");
		public static final NamedEventType CONTENTTOUCHEND = new NamedEventType("contentTouchend");
		public static final NamedEventType CONTENTTAP = new NamedEventType("contentTap");
		public static final NamedEventType CONTENTDBLTAP = new NamedEventType("contentDblTap");
	}
}
