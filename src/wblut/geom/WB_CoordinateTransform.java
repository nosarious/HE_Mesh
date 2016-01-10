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


public interface WB_CoordinateTransform {

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord apply(final WB_Transform T);



	/**
	 *
	 *
	 * @param result
	 * @param T
	 */
	public void applyInto(WB_MutableCoord result, final WB_Transform T);


	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsNormal(final WB_Transform T);



	/**
	 *
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsNormalInto(final WB_MutableCoord result,
			final WB_Transform T);


	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsPoint(final WB_Transform T);



	/**
	 *
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsPointInto(final WB_MutableCoord result,
			final WB_Transform T);


	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Coord applyAsVector(final WB_Transform T);



	/**
	 *
	 *
	 * @param result
	 * @param T
	 */
	public void applyAsVectorInto(final WB_MutableCoord result,
			final WB_Transform T);


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
	 * @deprecated Use {@link #rotateAboutAxis2P(double,double,double,double,double,double,double)} instead
	 */
	@Deprecated
	public WB_Coord rotateAbout2PointAxis(final double angle,
			final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z);



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
	public WB_Coord rotateAboutAxis2P(final double angle,
			final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z);


	/**
	 *
	 *
	 * @param angle
	 * @param p1
	 * @param p2
	 * @return
	 * @deprecated Use {@link #rotateAboutAxis2P(double,WB_Coord,WB_Coord)} instead
	 */
	@Deprecated
	public WB_Coord rotateAbout2PointAxis(final double angle,
			final WB_Coord p1, final WB_Coord p2);



	/**
	 *
	 *
	 * @param angle
	 * @param p1
	 * @param p2
	 * @return
	 */
	public WB_Coord rotateAboutAxis2P(final double angle,
			final WB_Coord p1, final WB_Coord p2);


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
	public WB_Coord rotateAboutAxis(final double angle, final double px,
			final double py, final double pz, final double ax, final double ay,
			final double az);


	/**
	 *
	 *
	 * @param angle
	 * @param p
	 * @param a
	 * @return
	 */
	public WB_Coord rotateAboutAxis(final double angle,
			final WB_Coord p, final WB_Coord a);


	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord scale(final double f);


	/**
	 *
	 *
	 * @param fx
	 * @param fy
	 * @param fz
	 * @return
	 */
	public WB_Coord scale(final double fx, final double fy, final double fz);


	/**
	 *
	 *
	 * @param result
	 * @param f
	 */
	public void scaleInto(WB_MutableCoord result, final double f);


	/**
	 *
	 *
	 * @param result
	 * @param fx
	 * @param fy
	 * @param fz
	 */
	public void scaleInto(WB_MutableCoord result, final double fx,
			final double fy, final double fz);



	/**
	 *
	 *
	 * @param angle
	 * @param x
	 * @param y
	 * @param z

	 * @return
	 */
	public WB_Coord rotateAboutOrigin(final double angle,
			final double x, final double y, final double z);



	/**
	 *
	 *
	 * @param angle
	 * @param v
	 * @return
	 */
	public WB_Coord rotateAboutOrigin(final double angle,final WB_Coord v);

}


