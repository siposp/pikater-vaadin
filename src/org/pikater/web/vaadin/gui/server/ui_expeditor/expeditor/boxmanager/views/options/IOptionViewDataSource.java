package org.pikater.web.vaadin.gui.server.ui_expeditor.expeditor.boxmanager.views.options;

import org.pikater.core.ontology.subtrees.newoption.base.NewOption;
import org.pikater.core.ontology.subtrees.newoption.base.Value;
import org.pikater.core.ontology.subtrees.newoption.restrictions.TypeRestriction;
import org.pikater.web.experiment.server.BoxInfoServer;

/**
 * Implementations of this interface provide all required information to
 * {@link OptionValueForm box option editor}.
 * 
 * @author SkyCrawl
 */
public interface IOptionViewDataSource {
	BoxInfoServer getBox();

	NewOption getOption();

	Value getValue();

	TypeRestriction getAllowedTypes();
}