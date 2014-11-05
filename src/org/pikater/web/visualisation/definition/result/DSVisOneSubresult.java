package org.pikater.web.visualisation.definition.result;

import org.pikater.shared.database.jpa.JPAAttributeMetaData;
import org.pikater.web.ImageType;
import org.pikater.web.visualisation.definition.AttrMapping;

/**
 * A single subresult (image) of {@link DSVisOneResult}, implementing
 * its own indexing. 
 * 
 * @author SkyCrawl
 */
public class DSVisOneSubresult extends AbstractDSVisSubresult<JPAAttributeMetaData> {
	private final AttrMapping attrInfo;

	public DSVisOneSubresult(AttrMapping attrInfo, ImageType imageType) {
		super(imageType);

		this.attrInfo = attrInfo;
	}

	public AttrMapping getAttrInfo() {
		return attrInfo;
	}

	@Override
	public JPAAttributeMetaData toLeftIndex() {
		return attrInfo.getAttrY();
	}

	@Override
	public JPAAttributeMetaData toTopIndex() {
		return attrInfo.getAttrX();
	}
}