package org.pikater.web.vaadin.gui.client.kineticengine.graph;

import java.util.HashSet;
import java.util.Set;

import net.edzard.kinetic.Box2d;
import net.edzard.kinetic.Colour;
import net.edzard.kinetic.Group;
import net.edzard.kinetic.Kinetic;
import net.edzard.kinetic.Layer;
import net.edzard.kinetic.Line;
import net.edzard.kinetic.Line.LineCap;
import net.edzard.kinetic.Line.LineStyle;
import net.edzard.kinetic.Node;
import net.edzard.kinetic.Path;
import net.edzard.kinetic.PathSVG;
import net.edzard.kinetic.Rectangle;
import net.edzard.kinetic.Rectangle.RectanglePoint;
import net.edzard.kinetic.Vector2d;

import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine;
import org.pikater.web.vaadin.gui.client.kineticengine.KineticEngine.EngineComponent;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.graphitems.EdgeGraphItemShared;
import org.pikater.web.vaadin.gui.shared.kineticcomponent.visualstyle.KineticEdgeSettings;

/**
 * The class representing an edge in our {@link KineticEngine}.
 * 
 * @author SkyCrawl
 */
public class EdgeGraphItemClient extends
		AbstractGraphItemClient<KineticEdgeSettings> {
	// **********************************************************************************************
	// INNER KINETIC COMPONENTS

	/**
	 * The group to contain the edge and the drag marks, not the baseline
	 * though.
	 */
	private final Group groupContainer;

	/**
	 * The main shape representing this edge.
	 */
	private final PathSVG arrow;

	/**
	 * The small squares to trigger "edge dragging".
	 */
	private final Rectangle fromBoxDragMark;
	private final Rectangle toBoxDragMark;

	/**
	 * The line to be shown instead of this edge when an endpoint box is dragged
	 * or this edge is being connected (dragged) to a different endpoint.
	 */
	private final Line baseLine;

	// **********************************************************************************************
	// PROGRAMMATIC VARIABLES AND TYPES

	public enum EdgeState {
		EDGE, BASELINE
	}

	public static enum EndPoint {
		FROM, TO;

		public EndPoint getInverted() {
			switch (this) {
				case FROM:
					return TO;
				case TO:
					return FROM;
				default:
					throw new IllegalStateException();
			}
		}
	}

	/**
	 * Boxes that this edge connects.
	 */
	private BoxGraphItemClient fromBox;
	private BoxGraphItemClient toBox;

	/**
	 * The state indicating whether this edge is currently being displayed as an
	 * edge or as a baseline.
	 */
	private EdgeState internalState;

	/**
	 * Reference kept for updating the edge.
	 */
	private KineticEdgeSettings currentSettings;

	/**
	 * Regular constructor.
	 */
	public EdgeGraphItemClient(KineticEngine kineticEngine) {
		super(kineticEngine);

		// first programmatic fields
		this.fromBox = null;
		this.toBox = null;
		this.currentSettings = null;

		// setup the edge if not connected
		this.baseLine = Kinetic.createLine(Vector2d.origin, Vector2d.origin);
		this.baseLine.setListening(false);
		this.baseLine.setStroke(Colour.red);
		this.baseLine.setStrokeWidth(2);
		this.baseLine.setLineStyle(LineStyle.DASHED);
		this.baseLine.setLineCap(LineCap.ROUND);

		// setup the edge if connected, part 1
		this.arrow = Kinetic.createPathSVG(Vector2d.origin, "");
		this.arrow.setListening(false);
		this.arrow.setStrokeWidth(3.0);

		// setup the edge if connected, part 2
		this.fromBoxDragMark = Kinetic.createRectangle(new Box2d(
				Vector2d.origin, Vector2d.origin));
		this.fromBoxDragMark.setDraggable(false);

		// setup the edge if connected, part 3
		this.toBoxDragMark = Kinetic.createRectangle(new Box2d(Vector2d.origin,
				Vector2d.origin));
		this.toBoxDragMark.setDraggable(false);

		// create the group, bind it all together
		this.groupContainer = Kinetic.createGroup();
		this.groupContainer.setDraggable(false);
		this.groupContainer.add(this.arrow);
		this.groupContainer.add(this.fromBoxDragMark);
		this.groupContainer.add(this.toBoxDragMark);
	}

	// **********************************************************************************************
	// INHERITED INTERFACE

	@Override
	public void applySettings(KineticEdgeSettings settings) {
		this.fromBoxDragMark.setSize(settings.getDragMarkSize());
		this.toBoxDragMark.setSize(settings.getDragMarkSize());
		this.fromBoxDragMark.setFill(settings.getDragMarkFill());
		this.toBoxDragMark.setFill(settings.getDragMarkFill());

		this.currentSettings = settings; // must remember the reference

		if (internalState == EdgeState.EDGE) {
			updateEdge();
		}
	}

	@Override
	protected void applyVisualStyle(VisualStyle style) {
		switch (style) {
			case SELECTED:
				arrow.setStroke(Colour.gold);
				break;
			case NOT_SELECTED:
				arrow.setStroke(Colour.black);
				break;

			case HIGHLIGHTED_EDGE:
				arrow.setStroke(Colour.red);
				break;
			case NOT_HIGHLIGHTED_EDGE:
				arrow.setStroke(Colour.black);
				break;

			case HIGHLIGHTED_SLOT:
			case NOT_HIGHLIGHTED_SLOT:
				break;

			default:
				throw new IllegalStateException("Unknown state: "
						+ style.name());
		}
	}

	@Override
	protected void setRegisteredInKinetic(boolean registered) {
		if (registered) {
			Layer layer = getKineticEngine().getContainer(
					EngineComponent.LAYER_EDGES).cast();
			layer.add(groupContainer);
			layer.add(baseLine);
		} else {
			groupContainer.remove();
			baseLine.remove();
		}
	}

	@Override
	protected void destroyInnerNodes() {
		baseLine.destroy();
		groupContainer.destroyChildren();
		groupContainer.destroy();
	}

	@Override
	protected void invertSelectionProgrammatically() {
		if (isSelected()) {
			groupContainer.moveTo(getKineticEngine().getSelectionContainer()); // select
		} else {
			groupContainer.moveTo(getKineticEngine().getContainer(
					EngineComponent.LAYER_EDGES)); // deselect
		}
	}

	@Override
	public EngineComponent getComponentToDraw() {
		return internalState == EdgeState.BASELINE ? EngineComponent.LAYER_EDGES
				: (isSelected() ? EngineComponent.LAYER_SELECTION
						: EngineComponent.LAYER_EDGES);
	}

	@Override
	public Node getMasterNode() {
		switch (internalState) {
			case BASELINE:
				return baseLine;
			case EDGE:
				return groupContainer;
			default:
				throw new IllegalStateException();
		}
	}

	// **********************************************************************************************
	// SOME BASIC ENDPOINT RELATED GETTERS & ROUTINES

	public BoxGraphItemClient getEndPoint(EndPoint endPoint) {
		switch (endPoint) {
			case FROM:
				return fromBox;
			case TO:
				return toBox;
			default:
				throw new IllegalStateException();
		}
	}

	public Rectangle getDragMark(EndPoint endPoint) {
		switch (endPoint) {
			case FROM:
				return fromBoxDragMark;
			case TO:
				return toBoxDragMark;
			default:
				throw new IllegalStateException();
		}
	}

	public boolean isExactlyOneEndSelected() {
		return fromBox.isSelected() ^ toBox.isSelected(); // exclusive or (aka
															// "xor")
	}

	public boolean areBothEndsSelected() {
		return fromBox.isSelected() && toBox.isSelected();
	}

	public boolean areBothEndsDefined() {
		return (fromBox != null) && (toBox != null);
	}

	public int getSelectedEndpointsCount() {
		return (fromBox.isSelected() ? 1 : 0) + (toBox.isSelected() ? 1 : 0);
	}

	public BoxGraphItemClient getOtherEndpoint(BoxGraphItemClient oneEndpoint) {
		if ((oneEndpoint == null) && (fromBox == null) && (toBox == null)) {
			throw new NullPointerException(
					"The given argument and both endpoints are null. No idea what to return.");
		}
		if (oneEndpoint == fromBox) {
			return toBox;
		} else if (oneEndpoint == toBox) {
			return fromBox;
		} else {
			throw new IllegalArgumentException(
					"The given argument didn't match any of the endpoints.");
		}
	}

	public BoxGraphItemClient getSelectedEndpoint() {
		if (!isExactlyOneEndSelected()) {
			throw new IllegalStateException(
					"Exactly one endpoint needs to be selected.");
		}
		return fromBox.isSelected() ? fromBox : toBox;
	}

	public BoxGraphItemClient getNotSelectedEndpoint() {
		if (!isExactlyOneEndSelected()) {
			throw new IllegalStateException(
					"Exactly one endpoint needs to be selected.");
		}
		return fromBox.isSelected() ? toBox : fromBox;
	}

	// **********************************************************************************************
	// OTHER PUBLIC METHODS

	public void setInternalState(EdgeState newState) {
		if (newState == EdgeState.BASELINE) {
			internalState = EdgeState.BASELINE;
			groupContainer.hide();
			baseLine.show();
		} else // edge mode
		{
			internalState = EdgeState.EDGE;
			baseLine.hide();
			groupContainer.show();
		}
	}

	/**
	 * When using this method, keep in mind that eventually
	 * {@link #setEdgeRegisteredInEndpoints(boolean registered)} needs to be
	 * called to keep consistency.
	 */
	public void setEndpoint(EndPoint endPoint, BoxGraphItemClient box) {
		switch (endPoint) {
			case FROM:
				if (fromBox != null) {
					fromBox.setEdgeRegistered(this, false);
				}
				fromBox = box;
				break;
			case TO:
				if (toBox != null) {
					toBox.setEdgeRegistered(this, false);
				}
				this.toBox = box;
				break;
			default:
				throw new IllegalStateException("Unknown state: "
						+ endPoint.name());
		}
	}

	/**
	 * Only call this method after both endpoints have been correctly set with
	 * the {@link #setEndpoint(EndPoint endPoint, BoxGraphItemClient box)}
	 * method.
	 */
	public void setEdgeRegisteredInEndpoints(boolean registered) {
		if (!areBothEndsDefined()) {
			throw new IllegalStateException(
					"One of the endpoint boxes has not been set for this edge.");
		} else {
			this.fromBox.setEdgeRegistered(this, registered);
			this.toBox.setEdgeRegistered(this, registered);
		}
	}

	public static EdgeGraphItemShared[] toShared(EdgeGraphItemClient... edges) {
		Set<EdgeGraphItemShared> resultSet = new HashSet<EdgeGraphItemShared>();
		for (int i = 0; i < edges.length; i++) {
			if (edges[i].areBothEndsDefined()) {
				resultSet.add(new EdgeGraphItemShared(edges[i].getEndPoint(
						EndPoint.FROM).getInfo().boxID, edges[i].getEndPoint(
						EndPoint.TO).getInfo().boxID));
			}
		}
		return resultSet.toArray(new EdgeGraphItemShared[0]);
	}

	// **********************************************************************************************
	// ENDPOINT OR EDGE DRAGGING RELATED METHODS

	public void endPointDrag_toBaseLine(BoxGraphItemClient draggedEndPoint) {
		// IMPORTANT: don't violate the call order
		baseLine.setEnd(getOtherEndpoint(draggedEndPoint)
				.getAbsolutePointPosition(RectanglePoint.CENTER));
		endPointDrag_updateBaseLine(draggedEndPoint);
		setInternalState(EdgeState.BASELINE);
	}

	public void endPointDrag_updateBaseLine(BoxGraphItemClient draggedEndPoint) {
		updateBaseLineStart(draggedEndPoint
				.getAbsolutePointPosition(RectanglePoint.CENTER));
	}

	public void endPointDrag_toEdge() {
		toEdge_onFinish(false);
	}

	public void edgeDrag_toBaseLine(Vector2d initialDragPosition,
			BoxGraphItemClient staticBox) {
		// IMPORTANT: don't violate the call order
		baseLine.setEnd(staticBox
				.getAbsolutePointPosition(RectanglePoint.CENTER));
		edgeDrag_updateBaseLine(initialDragPosition);
		setInternalState(EdgeState.BASELINE);
	}

	public void edgeDrag_updateBaseLine(Vector2d newPosition) {
		updateBaseLineStart(newPosition);
	}

	public void edgeDrag_toEdge(boolean updateEdge) {
		toEdge_onFinish(updateEdge);
	}

	// **********************************************************************************************
	// MISCELLANEOUS PRIVATE INTERFACE

	private void updateBaseLineStart(Vector2d position) {
		baseLine.setStart(position);
		Vector2d delta = position.sub(baseLine.getEnd()); // alias for the same
															// Vector2d instance
		if (delta.x == 0 || delta.y == 0) {
			baseLine.setStrokeWidth(2);
			baseLine.setLineStyle(LineStyle.NORMAL);
		} else {
			baseLine.setStrokeWidth(0.5);
			baseLine.setLineStyle(LineStyle.DASHED);
		}
	}

	/**
	 * Called to switch internal state to 'edge', after both endpoints have been
	 * specified.
	 */
	private void toEdge_onFinish(boolean updateEdge) {
		// change the state
		setInternalState(EdgeState.EDGE);

		// update arrow positions
		if (updateEdge) {
			updateEdge();
		}
		setEdgeRegisteredInEndpoints(true);
	}

	// **********************************************************************************************
	// EDGE COMPUTATION INTERFACE

	/**
	 * Recomputes the arrow path. If changes are to be visible in the kinetic
	 * environment, the {@link KineticEngine#draw(EngineComponent component)}
	 * needs to be called.
	 */
	public void updateEdge() {
		if (internalState == EdgeState.BASELINE) {
			throw new IllegalStateException(
					"Edge is in baseline mode. Switch to edge mode before executing this method.");
		}

		// compute new endpoints
		Vector2d delta = toBox.getAbsolutePointPosition(RectanglePoint.CENTER)
				.sub(fromBox.getAbsolutePointPosition(RectanglePoint.CENTER)); // the
																				// vector
																				// from
																				// A
																				// to
																				// B
																				// <=>
																				// B-A
																				// <=>
																				// delta
		double angle = Math.toDegrees(Math.atan2(delta.y, delta.x));
		double absAngle = Math.abs(angle);
		RectanglePoint b1Endpoint;
		if (absAngle < 60) {
			b1Endpoint = RectanglePoint.WESTCENTER;
		} else if (absAngle < 120) {
			b1Endpoint = angle < 0 ? RectanglePoint.NORTHCENTER
					: RectanglePoint.SOUTHCENTER;
		} else {
			b1Endpoint = RectanglePoint.EASTCENTER;
		}

		// update arrow
		Vector2d fromPos = fromBox.getAbsolutePointPosition(b1Endpoint);
		Vector2d toPos = toBox.getAbsolutePointPosition(b1Endpoint
				.getInverted());
		groupContainer.setPosition(Vector2d.origin); // have to reset in case we
														// performed a drag
														// operation earlier
		computeArrowPath(fromPos, toPos);

		// update the drag marks
		Vector2d dragMarkHalfSize = currentSettings.getDragMarkSize()
				.scale(0.5);
		this.fromBoxDragMark.setPosition(fromPos.sub(dragMarkHalfSize));
		this.toBoxDragMark.setPosition(toPos.sub(dragMarkHalfSize));
	}

	private void computeArrowPath(Vector2d fromPos, Vector2d toPos) {
		// arrowhead precomputation
		Vector2d u = new Vector2d(toPos.x - fromPos.x, toPos.y - fromPos.y);
		u.normalize();
		Vector2d v = new Vector2d(-u.y, u.x);
		u.scale(currentSettings.getArrowHeight() * Math.sqrt(3));
		v.scale(currentSettings.getArrowWidth());

		// compute the arrowhead's end points
		Vector2d leftArrowEnd = new Vector2d(toPos.x, toPos.y);
		leftArrowEnd.sub(u);
		leftArrowEnd.add(v);
		Vector2d rightArrowEnd = new Vector2d(toPos.x, toPos.y);
		rightArrowEnd.sub(u);
		rightArrowEnd.sub(v);

		// update the arrow
		this.arrow.setData(new Path().moveTo(fromPos).lineTo(toPos)
				.moveTo(leftArrowEnd).lineTo(toPos).lineTo(rightArrowEnd)
				.closePath().toSVGPath());
		this.arrow.setPosition(0, 0); // reset any selection drags to avoid the
										// X and Y properties to act as an
										// offset
	}
}
