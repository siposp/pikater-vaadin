package org.pikater.web.visualisation.definition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.pikater.shared.util.Tuple;
import org.pikater.web.vaadin.gui.server.layouts.matrixlayout.IMatrixDataSource;
import org.pikater.web.visualisation.definition.result.AbstractDSVisSubresult;

/**
 * Holds all generated subresults/images and provides some basic (matrix-like)
 * interface for them. The matrix may not be complete (some positions may be
 * blank).
 * 
 * @author SkyCrawl
 * 
 * @param <I>
 *            The type to index subresults with.
 * @param <R>
 *            The subresult type.
 */
public class SubresultIndexer<I, R extends AbstractDSVisSubresult<I>>
		implements IMatrixDataSource<I, R>, Iterable<R> {
	private final Set<I> leftIndexSet;
	private final Set<I> topIndexSet;
	private final Map<Tuple<I, I>, R> indexesToSubresult;

	public SubresultIndexer() {
		this.leftIndexSet = new LinkedHashSet<I>(); // LinkedHashSet - hash,
													// don't use comparing and
													// keep the insertion order
		this.topIndexSet = new LinkedHashSet<I>(); // LinkedHashSet - hash,
													// don't use comparing and
													// keep the insertion order
		this.indexesToSubresult = new HashMap<Tuple<I, I>, R>();
	}

	@Override
	public Iterator<R> iterator() {
		return indexesToSubresult.values().iterator();
	}

	public boolean isSubresultRegistered(I leftIndex, I topIndex) {
		return this.indexesToSubresult.containsKey(new Tuple<I, I>(leftIndex,
				topIndex));
	}

	public void registerSubresult(I leftIndex, I topIndex, R subresult) {
		if (!isSubresultRegistered(leftIndex, topIndex)) {
			leftIndexSet.add(leftIndex);
			topIndexSet.add(topIndex);
			indexesToSubresult.put(new Tuple<I, I>(leftIndex, topIndex),
					subresult);
		} else {
			throw new IllegalArgumentException(
					String.format(
							"Result for '%s' (left) and '%s' (top) was already registered.",
							leftIndex.toString(), topIndex.toString()));
		}
	}

	@Override
	public Collection<I> getLeftIndexSet() {
		return leftIndexSet;
	}

	@Override
	public Collection<I> getTopIndexSet() {
		return topIndexSet;
	}

	@Override
	public R getElement(I leftIndex, I topIndex) {
		return indexesToSubresult.get(new Tuple<I, I>(leftIndex, topIndex));
	}
}