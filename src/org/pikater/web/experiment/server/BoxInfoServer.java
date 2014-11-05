package org.pikater.web.experiment.server;

import org.pikater.core.ontology.subtrees.agentinfo.AgentInfo;
import org.pikater.web.experiment.IBoxInfoCommon;
import org.pikater.web.experiment.client.BoxInfoClient;
import org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.ExpEditor;

/**
 * <p>The server's notion of "boxes". This format is intermediate. The following
 * chain explains the conversion cycle:</p>
 * ({@link BoxInfoClient} - user's browser) <-> ({@link BoxInfoServer} -
 * web application server) <- ({@link AgentInfo} - core system)
 * 
 * @author SkyCrawl
 */
public class BoxInfoServer implements IBoxInfoCommon<Integer> {
	/*
	 * Read-only fields (except ID, which is only set once and then it is read-only).
	 */

	private Integer generatedUniqueID;

	/**
	 * Box category.
	 */
	private final BoxType boxType;

	/**
	 * Low-level box information provider, fetched from core system.
	 */
	private final AgentInfo associatedAgent;

	/*
	 * Mutable fields.
	 */

	/**
	 * X position of the box in the experiment canvas. 
	 */
	private int posX;

	/**
	 * Y position of the box in the experiment canvas.
	 */
	private int posY;

	/**
	 * Whether this box is currently registered and visible in the experiment
	 * canvas.
	 * 
	 * If the box is deleted, this field is set to "false". Then, it may be
	 * restored using the "CTRL+Z" shortcut and this field is again
	 * set to "true".
	 */
	private boolean registered;

	public BoxInfoServer(AgentInfo associatedAgent, int posX, int posY) throws CloneNotSupportedException {
		this.generatedUniqueID = null;
		this.boxType = BoxType.fromAgentInfo(associatedAgent);
		this.associatedAgent = associatedAgent.clone(); // each box needs to have a unique instance (to preserve original instances intact)
		this.posX = posX;
		this.posY = posY;
		this.registered = true;
	}

	@Override
	public Integer getID() {
		return generatedUniqueID;
	}

	@Override
	public void setID(Integer id) {
		if (generatedUniqueID == null) {
			generatedUniqueID = id;
		} else {
			throw new IllegalStateException("ID has already been set.");
		}
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public AgentInfo getAssociatedAgent() {
		return associatedAgent;
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public BoxInfoClient toClientFormat() {
		return new BoxInfoClient(generatedUniqueID, boxType.name(), associatedAgent.getName(), posX, posY, ExpEditor.getBoxPictureURL(boxType));
	}
}