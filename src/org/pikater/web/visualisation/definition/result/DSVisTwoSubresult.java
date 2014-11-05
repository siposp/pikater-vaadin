package org.pikater.web.visualisation.definition.result;

import org.pikater.shared.util.Tuple;
import org.pikater.web.ImageType;
import org.pikater.web.visualisation.definition.AttrMapping;

/**
 * A single subresult (image) of {@link DSVisTwoResult}, implementing
 * its own indexing.
 * 
 * @author SkyCrawl
 */
public class DSVisTwoSubresult extends AbstractDSVisSubresult<AttrMapping> {
	private final Tuple<AttrMapping, AttrMapping> attrInfo;

	public DSVisTwoSubresult(ImageType imageType, Tuple<AttrMapping, AttrMapping> attrInfo) {
		super(imageType);

		this.attrInfo = attrInfo;
	}

	public Tuple<AttrMapping, AttrMapping> getAttrInfo() {
		return attrInfo;
	}

	@Override
	public AttrMapping toLeftIndex() {
		return attrInfo.getValue1();
	}

	@Override
	public AttrMapping toTopIndex() {
		return attrInfo.getValue2();
	}
}