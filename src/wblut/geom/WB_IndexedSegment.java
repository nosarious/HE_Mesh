/*
 *
 */
package wblut.geom;

/**
 *
 */
public class WB_IndexedSegment extends WB_Segment {

	/**
	 *
	 */
	private int i1;
	/**
	 *
	 */
	private int i2;
	/**
	 *
	 */

	// private final WB_Coordinate[] points;

	/**
	 *
	 *
	 * @param i1
	 * @param i2
	 * @param points
	 */
	public WB_IndexedSegment(final int i1, final int i2, final WB_Coord[] points) {
		super(points[i1], points[i2]);
		this.i1 = i1;
		this.i2 = i2;

	}

	protected WB_IndexedSegment(int i1, int i2, WB_Coord p1, WB_Coord p2) {
		super(p1, p2);
		this.i1 = i1;
		this.i2 = i2;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int i1() {
		return i1;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int i2() {
		return i2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment#negate()
	 */
	@Override
	public WB_IndexedSegment negate() {
		return new WB_IndexedSegment(i2, i1, endpoint, origin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment#reverse()
	 */
	@Override
	public void reverse() {
		direction = new WB_Vector(direction).mulSelf(-1);
		WB_Point tmpp = origin;
		origin = endpoint;
		endpoint = tmpp;
		final int tmp = i2;
		i2 = i1;
		i1 = tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment#getPoint(int)
	 */
	@Override
	public WB_Coord getPoint(final int i) {
		if (i == 0) {
			return getOrigin();
		}
		if (i == 1) {
			return getEndpoint();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment#getType()
	 */
	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.SEGMENT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Geometry apply(final WB_Transform T) {
		return geometryfactory.createSegment(new WB_Point(getOrigin()).applyAsPoint(T),
				new WB_Point(getEndpoint()).applyAsPoint(T));
	}
}
