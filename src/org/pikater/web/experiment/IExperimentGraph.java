package org.pikater.web.experiment;

/**
 * Interface common for both client and server experiment graph formats. The
 * graphs have oriented edges.
 * 
 * @author SkyCrawl
 *
 * @param <I> See {@link IBoxInfoCommon}.
 * @param <B> The box type this graph type supports. "Client" and "server"
 * versions are available.
 */
public interface IExperimentGraph<I extends Object, B extends IBoxInfoCommon<I>> extends Iterable<B> {
	boolean containsBox(I boxID);

	B getBox(I boxID);

	B addBox(B box);

	void clear();

	boolean isEmpty();

	void connect(I fromBoxID, I toBoxID);

	void disconnect(I fromBoxID, I toBoxID);

	boolean hasOutputEdges(I boxID);
}