package org.pikater.web.visualisation.definition.result;

import java.util.Iterator;

import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogResultHandler;
import org.pikater.web.vaadin.gui.server.components.popups.dialogs.ProgressDialog.IProgressDialogTaskResult;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixDataSource;
import org.pikater.web.visualisation.definition.SubresultIndexer;

/**
 * Base class to wrap the generated visualization images and provide the most
 * essential interface. Generated images (subresults) are held within the
 * {@link SubresultIndexer indexer} which provides some (homogenous) matrix indexing.
 * 
 * @author SkyCrawl
 *
 * @param <I> The type to index subresults with.
 * @param <R> The subresult type.
 */
public abstract class AbstractDSVisResult<I, R extends AbstractDSVisSubresult<I>> implements IProgressDialogTaskResult, Iterable<R> {
	/**
	 * Object providing progress callbacks.
	 */
	private final IProgressDialogResultHandler taskResult;

	/**
	 * Indexer for the generated images.
	 */
	private final SubresultIndexer<I, R> indexer;

	/**
	 * The width of all images in the above collection.
	 */
	private final int imageWidth;

	/**
	 * The height of all images in the above collection.
	 */
	private final int imageHeight;

	public AbstractDSVisResult(IProgressDialogResultHandler taskResult, int imageWidth, int imageHeight) {
		this.taskResult = taskResult;
		this.indexer = new SubresultIndexer<I, R>();
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	//------------------------------------------------------------------------------
	// PUBLIC INTERFACE

	/*
	 * MAIN
	 */

	@Override
	public Iterator<R> iterator() {
		return indexer.iterator();
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public boolean isSubresultRegistered(I leftIndex, I topIndex) {
		return indexer.isSubresultRegistered(leftIndex, topIndex);
	}

	/**
	 * Transforms the registered images (subresults) into a matrix view
	 * for further processing.
	 *  
	 */
	public IMatrixDataSource<I, R> toMatrixView() {
		return indexer;
	}

	/*
	 * OPAQUE PROGRESS ROUTINES FORWARDING TO THE PASSED TASK RESULT OBJECT
	 */

	public void updateProgress(float percentage) {
		taskResult.updateProgress(percentage);
	}

	/**
	 * As a side effect, destroys all registered subresults.
	 */
	public void failed() {
		for (R subresult : indexer) {
			subresult.destroy();
		}
		taskResult.failed();
	}

	public void finished() {
		taskResult.finished(this);
	}

	//------------------------------------------------------------------------------
	// PROTECTED INTERFACE

	/**
	 * Use this method to include a generated subresult into the main result
	 * collection.
	 * 
	 * @param leftIndex the Y matrix index associated to the new subresult
	 * @param topIndex the X matrix index associated to the new subresult
	 * @param subresult the new subresult
	 */
	protected void registerSubresult(I leftIndex, I topIndex, R subresult) {
		indexer.registerSubresult(leftIndex, topIndex, subresult); // might throw an exception - let it be first
	}
}
