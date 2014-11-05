package org.pikater.web.visualisation.definition;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;

/**
 * Utility class dedicated to visualization. Specifies what columns (attributes)
 * of datasets should be compared and how. It is also used to index generated
 * images.
 * 
 * @author SkyCrawl
 */
public class AttrMapping {
	private final JPAAttributeMetaData attrX;
	private final JPAAttributeMetaData attrY;
	private final JPAAttributeMetaData attrTarget;

	public AttrMapping(JPAAttributeMetaData attrX, JPAAttributeMetaData attrY,
			JPAAttributeMetaData attrTarget) {
		this.attrX = attrX;
		this.attrY = attrY;
		this.attrTarget = attrTarget;
	}

	public JPAAttributeMetaData getAttrX() {
		return attrX;
	}

	public JPAAttributeMetaData getAttrY() {
		return attrY;
	}

	public JPAAttributeMetaData getAttrTarget() {
		return attrTarget;
	}

	/**
	 * This is going to be displayed in headers of the result matrix view.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Y: ").append(attrY.getName()).append("</br>");
		sb.append("X: ").append(attrX.getName()).append("</br>");
		sb.append("=>: ").append(attrTarget.getName());
		return sb.toString();
	}

	// --------------------------------------------------------
	// INSTANCE COMPARING INTERFACE - generated with Eclipse

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrTarget == null) ? 0 : attrTarget.hashCode());
		result = prime * result + ((attrX == null) ? 0 : attrX.hashCode());
		result = prime * result + ((attrY == null) ? 0 : attrY.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttrMapping other = (AttrMapping) obj;
		if (attrTarget == null) {
			if (other.attrTarget != null)
				return false;
		} else if (!attrTarget.equals(other.attrTarget))
			return false;
		if (attrX == null) {
			if (other.attrX != null)
				return false;
		} else if (!attrX.equals(other.attrX))
			return false;
		if (attrY == null) {
			if (other.attrY != null)
				return false;
		} else if (!attrY.equals(other.attrY))
			return false;
		return true;
	}
}