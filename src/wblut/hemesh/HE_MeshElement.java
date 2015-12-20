/*
 *
 */
package wblut.hemesh;

import wblut.core.WB_ProgressTracker;
import wblut.geom.WB_GeometryFactory;

/**
 *
 */
public abstract class HE_MeshElement extends HE_Element {


	protected boolean visited;
	protected final static WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();
	protected static final WB_ProgressTracker tracker = WB_ProgressTracker.instance();

	/**
	 *
	 */
	public HE_MeshElement() {
		super();
		visited = false;
	}



	public void clearVisited() {
		visited = false;
	}

	public void setVisited() {
		visited = true;
	}

	public boolean isVisited() {
		return visited;
	}




	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) (_key ^ (_key >>> 32));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof HE_MeshElement)) {
			return false;
		}
		return ((HE_MeshElement) other).getKey() == _key;
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_MeshElement el) {
		super.copyProperties(el);
		visited = el.visited;
	}

	/**
	 *
	 */
	@Override
	protected abstract void clear();
}
