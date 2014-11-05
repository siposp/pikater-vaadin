package org.pikater.web.vaadin.gui.server.layouts.matrixlayout;

import com.vaadin.ui.Label;

/**
 * Provides captions for indexes of {@link MatrixLayout}.
 * 
 * @author SkyCrawl
 *
 * @param <I> The index type of the matrix.
 */
public interface IMatrixLayoutHeaderProvider<I extends Object> {
	/**
	 * Gets caption for the given index of {@link MatrixLayout}.
	 */
	Label getCaptionComponent(I index);
}
