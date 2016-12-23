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

import wblut.math.WB_M33;

/**
 * Interface for implementing non-mutable mathematical operations.If the
 * operations should change the calling object use
 * {@link wblut.geom.WB_MutableCoordinateMath}.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_CoordinateMath {
	/**
	 * Add coordinate values.
	 *
	 * @param x
	 * @return new WB_coordinate
	 */
	public WB_Coord add(final double... x);

	/**
	 * Add coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param x
	 */
	public void addInto(final WB_MutableCoord result, final double... x);

	/**
	 * Add coordinate values.
	 *
	 * @param p
	 * @return new WB_coordinate
	 */
	public WB_Coord add(final WB_Coord p);

	/**
	 * Add coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param p
	 */
	public void addInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Add multiple of coordinate values.
	 *
	 * @param f
	 *            multiplier
	 * @param x
	 * @return new WB_coordinate
	 */
	public WB_Coord addMul(final double f, final double... x);

	/**
	 * Add multiple of coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 *            multiplier
	 * @param x
	 */
	public void addMulInto(final WB_MutableCoord result, final double f, final double... x);

	/**
	 * Add multiple of coordinate values.
	 *
	 * @param f
	 * @param p
	 * @return new WB_coordinate
	 */
	public WB_Coord addMul(final double f, final WB_Coord p);

	/**
	 * Add multiple of coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param p
	 */
	public void addMulInto(final WB_MutableCoord result, final double f, final WB_Coord p);

	/**
	 * Subtract coordinate values.
	 *
	 * @param x
	 * @return new WB_coordinate
	 */
	public WB_Coord sub(final double... x);

	/**
	 * Subtract coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param x
	 */
	public void subInto(final WB_MutableCoord result, final double... x);

	/**
	 * Subtract coordinate values.
	 *
	 * @param p
	 * @return new WB_coordinate
	 */
	public WB_Coord sub(final WB_Coord p);

	/**
	 * Subtract coordinate values and store in mutable coordinate.
	 *
	 * @param result
	 * @param p
	 */
	public void subInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Multiply by factor.
	 *
	 * @param f
	 * @return new WB_coordinate
	 */
	public WB_Coord mul(final double f);

	/**
	 * Multiply by factor and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 */
	public void mulInto(final WB_MutableCoord result, final double f);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g.
	 *
	 * @param f
	 * @param g
	 * @param x
	 * @return new WB_coordinate
	 */
	public WB_Coord mulAddMul(final double f, final double g, final double... x);

	/**
	 * Multiply this coordinate by factor f and add other coordinate values
	 * multiplied by g.
	 *
	 * @param f
	 * @param g
	 * @param p
	 * @return new WB_coordinate
	 */
	public WB_Coord mulAddMul(final double f, final double g, final WB_Coord p);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param x
	 */
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final double... x);

	/**
	 * Multiply this coordinate by factor f, add other coordinate values
	 * multiplied by g and store result in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 * @param g
	 * @param p
	 */
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final WB_Coord p);

	/**
	 * Divide by factor.
	 *
	 * @param f
	 * @return new WB_coordinate
	 */
	public WB_Coord div(final double f);

	/**
	 * Divide by factor and store in mutable coordinate.
	 *
	 * @param result
	 * @param f
	 */
	public void divInto(final WB_MutableCoord result, final double f);

	/**
	 * Cross product of this coordinate with other coordinate.
	 *
	 * @param p
	 * @return new WB_coordinate
	 */
	public WB_Coord cross(final WB_Coord p);

	/**
	 * Store cross product of this coordinate with other coordinate in mutable
	 * coordinate. coordinate.
	 *
	 * @param result
	 * @param p
	 */
	public void crossInto(final WB_MutableCoord result, final WB_Coord p);

	/**
	 * Dot product.
	 *
	 * @param p
	 * @return dot product
	 */
	public double dot(final WB_Coord p);

	/**
	 * 2D dot product.
	 *
	 * @param p
	 * @return 2D dot product
	 */
	public double dot2D(final WB_Coord p);

	/**
	 * Absolute value of dot product.
	 *
	 * @param p
	 * @return absolute value of dot product
	 */
	public double absDot(final WB_Coord p);

	/**
	 * Absolute value of 2D dot product.
	 *
	 * @param p
	 * @return absolute value of 2D dot product
	 */
	public double absDot2D(final WB_Coord p);

	/**
	 * Tensor product.
	 *
	 * @param v
	 * @return tensor product
	 */
	public WB_M33 tensor(final WB_Coord v);

	/**
	 * Scalar triple: this.(v x w)
	 *
	 * @param v
	 * @param w
	 * @return scalar triple
	 */
	public double scalarTriple(final WB_Coord v, final WB_Coord w);

}
