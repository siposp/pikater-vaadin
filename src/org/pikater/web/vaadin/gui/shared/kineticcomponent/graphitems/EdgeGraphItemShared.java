package org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems;

import java.io.Serializable;

/**
 * A special class for edges of client kinetic canvas, made especially
 * for communication between server and client, carrying only the most
 * essential information.
 * 
 * @author SkyCrawl
 */
public class EdgeGraphItemShared implements Serializable {
	private static final long serialVersionUID = 4411734178488130587L;

	public Integer fromBoxID;
	public Integer toBoxID;

	/**
	 * @deprecated Default public constructor keeps Vaadin happy.
	 */
	@Deprecated
	public EdgeGraphItemShared() {
	}

	public EdgeGraphItemShared(Integer fromBoxID, Integer toBoxID) {
		this.fromBoxID = fromBoxID;
		this.toBoxID = toBoxID;
	}
}