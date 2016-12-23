/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_MutableCoordinateTransform extends WB_CoordinateTransform {
	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applySelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsNormalSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsPointSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsVectorSelf(final WB_Transform T);

	/**
	 *
	 *
	 * @param angle
	 * @param p1x
	 * @param p1y
	 * @param p1z
	 * @param p2x
	 * @param p2y
	 * @param p2z
	 * @return
	 */
	public WB_Coord rotateAboutAxis2PSelf(final double angle,
			final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z);

	/**
	 *
	 *
	 * @param angle
	 * @param p1
	 * @param p2
	 * @return
	 */
	public WB_Coord rotateAboutAxis2PSelf(final double angle,
			final WB_Coord p1, final WB_Coord p2);

	/**
	 *
	 *
	 * @param angle
	 * @param p
	 * @param a
	 * @return
	 */
	public WB_Coord rotateAboutAxisSelf(final double angle,
			final WB_Coord p, final WB_Coord a);

	/**
	 *
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 * @return
	 */
	public WB_Coord rotateAboutAxisSelf(final double angle,
			final double px, final double py, final double pz, final double ax,
			final double ay, final double az);

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord scaleSelf(final double f);

	/**
	 *
	 *
	 * @param fx
	 * @param fy
	 * @param fz
	 * @return
	 */
	public WB_Coord scaleSelf(final double fx, final double fy,
			final double fz);

	/**
	 *
	 *
	 * @param angle
	 * @param x
	 * @param y
	 * @param z

	 * @return
	 */
	public WB_Coord rotateAboutOriginSelf(final double angle,
			final double x, final double y, final double z);



	/**
	 *
	 *
	 * @param angle
	 * @param v
	 * @return
	 */
	public WB_Coord rotateAboutOriginSelf(final double angle,final WB_Coord v);

}
