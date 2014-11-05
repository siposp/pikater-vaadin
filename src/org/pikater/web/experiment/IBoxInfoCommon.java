package org.pikater.web.experiment;

/**
 * Interface common for both client and server box definitions. 
 * 
 * @author SkyCrawl
 *
 * @param <I> The type for IDs. Typically Integers or Strings.
 */
public interface IBoxInfoCommon<I extends Object> {
	I getID();

	void setID(I id);

	/**
	 * Returns the box's X position in the experiment canvas.
	 */
	int getPosX();

	/**
	 * Sets the box's X position in the experiment canvas.
	 */
	void setPosX(int posX);

	/**
	 * Returns the box's Y position in the experiment canvas.
	 */
	int getPosY();

	/**
	 * Sets the box's Y position in the experiment canvas.
	 */
	void setPosY(int posY);
}
