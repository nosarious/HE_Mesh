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
public interface WB_MutableCoordinateMath extends WB_CoordinateMath {
	/**
	 *
	 *
	 * @param x
	 *
	 * @return
	 */
	public WB_Coord addSelf(final double... x);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public WB_Coord addSelf(final WB_Coord p);

	/**
	 *
	 *
	 * @param x
	 *
	 * @return
	 */
	public WB_Coord subSelf(final double... x);

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public WB_Coord subSelf(final WB_Coord v);

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord mulSelf(final double f);

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public WB_Coord divSelf(final double f);

	/**
	 *
	 *
	 * @param f
	 * @param x
	 *
	 * @return
	 */
	public WB_Coord addMulSelf(final double f, final double... x);

	/**
	 *
	 *
	 * @param f
	 * @param p
	 * @return
	 */
	public WB_Coord addMulSelf(final double f, final WB_Coord p);

	/**
	 *
	 *
	 * @param f
	 * @param g
	 * @param x
	 * @return
	 */
	public WB_Coord mulAddMulSelf(final double f, final double g,
			final double... x);

	/**
	 *
	 *
	 * @param f
	 * @param g
	 * @param p
	 * @return
	 */
	public WB_Coord mulAddMulSelf(final double f, final double g,
			final WB_Coord p);

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public WB_Coord crossSelf(final WB_Coord p);

	/**
	 *
	 *
	 * @return
	 */
	public double normalizeSelf();

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public WB_Coord trimSelf(final double d);
}
