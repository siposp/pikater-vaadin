package org.pikater.web.experiment.client;

import java.io.Serializable;

import org.pikater.web.experiment.IBoxInfoCommon;
import org.pikater.web.experiment.server.BoxInfoServer;

/**
 * A special (GWT&Vaadin)-compliant box implementation. As such, fields are
 * required to be public and a public default constructor is needed.
 * 
 * @author SkyCrawl
 */
public class BoxInfoClient implements Serializable, IBoxInfoCommon<Integer> {
	private static final long serialVersionUID = 3875674558654733345L;

	/*
	 * General info.
	 */
	public Integer boxID;
	public String boxTypeName;
	public String displayName;

	/*
	 * Position.
	 */
	public int positionX;
	public int positionY;

	/*
	 * Picture info.
	 */
	public String pictureURL;

	/**
	 * Default public constructor keeps Vaadin happy. Use the other constructor
	 * instead.
	 */
	@Deprecated
	public BoxInfoClient() {
	}

	/**
	 * See {@link BoxInfoServer} for an example of what values need to be passed
	 * here.
	 */
	public BoxInfoClient(Integer boxID, String boxTypeName, String displayName,
			int posX, int posY, String pictureURL) {
		this.boxID = boxID;
		this.boxTypeName = boxTypeName;
		this.displayName = displayName;
		this.positionX = posX;
		this.positionY = posY;
		this.pictureURL = pictureURL;
	}

	@Override
	public Integer getID() {
		return boxID;
	}

	@Override
	public void setID(Integer id) {
		this.boxID = id;
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
