package org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems;

import java.io.Serializable;

import org.pikater.web.experiment.IBoxInfoCommon;

/**
 * A special class for boxes of client kinetic canvas, made especially
 * for communication between server and client, carrying only the most
 * essential information.
 * 
 * @author SkyCrawl
 */
public class BoxGraphItemShared implements Serializable, IBoxInfoCommon<Integer> {
	private static final long serialVersionUID = -7700316337241284638L;

	public Integer boxID;
	public int positionX;
	public int positionY;

	/**
	 * @deprecated Default public constructor keeps Vaadin happy.
	 */
	@Deprecated
	public BoxGraphItemShared() {
		this(0, 0, 0);
	}

	public BoxGraphItemShared(Integer boxID, int positionX, int positionY) {
		this.boxID = boxID;
		this.positionX = positionX;
		this.positionY = positionY;
	}

	@Override
	public Integer getID() {
		return boxID;
	}

	@Override
	public void setID(Integer id) {
		boxID = id;
	}

	@Override
	public int getPosX() {
		return positionX;
	}

	@Override
	public void setPosX(int posX) {
		this.positionX = posX;
	}

	@Override
	public int getPosY() {
		return positionY;
	}

	@Override
	public void setPosY(int posY) {
		this.positionY = posY;
	}
}