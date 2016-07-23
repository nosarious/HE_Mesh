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

import wblut.math.WB_Math;

/**
 *
 */
public class WB_Line extends WB_Linear implements WB_Curve {

	/**
	 *
	 *
	 * @return
	 */
	public static final WB_Line X() {
		return new WB_Line(0, 0, 0, 1, 0, 0);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static final WB_Line Y() {
		return new WB_Line(0, 0, 0, 0, 1, 0);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static final WB_Line Z() {
		return new WB_Line(0, 0, 0, 0, 0, 1);
	}

	/**
	 *
	 */
	public WB_Line() {
		super();
	}

	/**
	 *
	 *
	 * @param o
	 * @param d
	 */
	public WB_Line(final WB_Coord o, final WB_Coord d) {
		super(o, d);
	}

	/**
	 *
	 *
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public WB_Line(final double ox, final double oy, final double oz, final double dx, final double dy,
			final double dz) {
		super(new WB_Point(ox, oy, oz), new WB_Vector(dx, dy, dz));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Line: " + origin.toString() + " " + direction.toString();
	}

	/**
	 *
	 *
	 * @param p1
	 * @param p2
	 */
	public void setFromPoints(final WB_Coord p1, final WB_Coord p2) {
		super.set(p1, p2);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public double getT(final WB_Coord p) {
		double t = Double.NaN;
		final WB_Coord proj = WB_GeometryOp.getClosestPoint2D(p, this);
		final double x = WB_Math.fastAbs(direction.xd());
		final double y = WB_Math.fastAbs(direction.yd());
		if (x >= y) {
			t = (proj.xd() - origin.xd()) / direction.xd();
		} else {
			t = (proj.yd() - origin.yd()) / direction.yd();
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#curvePoint(double)
	 */
	@Override
	public WB_Point curvePoint(final double u) {
		return this.getPointOnLine(u);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#curveDirection(double)
	 */
	@Override
	public WB_Vector curveDirection(final double u) {

		return new WB_Vector(direction);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#curveDerivative(double)
	 */
	@Override
	public WB_Vector curveDerivative(final double u) {
		return new WB_Vector(direction);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#loweru()
	 */
	@Override
	public double getLowerU() {
		return Double.NEGATIVE_INFINITY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#upperu()
	 */
	@Override
	public double getUpperU() {
		return Double.POSITIVE_INFINITY;
	}
}
