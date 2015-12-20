/*
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_CoordinateMetric {
	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getAngle(final WB_Coord p);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getAngleNorm(final WB_Coord p);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getDistance2D(final WB_Coord p);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getDistance3D(final WB_Coord p);

	/**
	 *
	 *
	 * @return
	 */
	public double getLength2D();

	/**
	 *
	 *
	 * @return
	 */
	public double getLength3D();

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getOrthoNormal2D();

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getOrthoNormal3D();

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getSqDistance2D(final WB_Coord p);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getSqDistance3D(final WB_Coord p);

	/**
	 *
	 *
	 * @return
	 */
	public double getSqLength2D();

	/**
	 *
	 *
	 * @return
	 */
	public double getSqLength3D();


	/**
	 *
	 *
	 * @return
	 */
	public double getHeading2D();

	/**
	 *
	 *
	 * @return
	 */
	public boolean isZero();
}
