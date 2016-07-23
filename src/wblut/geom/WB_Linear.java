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

import wblut.math.WB_Epsilon;

public abstract class WB_Linear {

	protected WB_Point origin;

	protected WB_Vector direction;

	/**
	 *
	 */
	public WB_Linear() {
		origin = new WB_Point();
		direction = new WB_Vector(1, 0, 0);
	}

	/**
	 *
	 *
	 * @param o
	 * @param d
	 */
	public WB_Linear(final WB_Coord o, final WB_Coord d) {
		origin = new WB_Point(o);
		final WB_Vector dn = new WB_Vector(d);
		dn.normalizeSelf();
		direction = dn;
	}

	/**
	 *
	 *
	 * @param o
	 * @param d
	 */
	protected void set(final WB_Coord o, final WB_Coord d) {
		origin = new WB_Point(o);
		final WB_Vector dn = new WB_Vector(d);
		dn.normalizeSelf();
		direction = dn;
	}

	/**
	 *
	 *
	 * @param t
	 * @return
	 */
	public WB_Point getPointOnLine(final double t) {
		final WB_Point result = new WB_Point(direction);
		result.scaleSelf(t);
		result.addSelf(origin);
		return result;
	}

	/**
	 *
	 *
	 * @param t
	 * @param p
	 */
	public void getPointOnLineInto(final double t, final WB_MutableCoord p) {
		p.set(new WB_Vector(direction).mulSelf(t).addSelf(origin));
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getOrigin() {
		return origin;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getDirection() {
		return direction;
	}

	/**
	 * Get vector perpendicular to the line
	 *
	 * @return
	 */
	public WB_Vector getNormal() {
		WB_Vector n = new WB_Vector(0, 0, 1);
		n = n.cross(direction);
		final double d = n.normalizeSelf();
		if (WB_Epsilon.isZero(d)) {
			n = new WB_Vector(1, 0, 0);
		}
		return n;
	}

	/**
	 * a.x+b.y+c=0
	 *
	 * @return a for a 2D line
	 */

	public double a() {
		return -direction.yd();
	}

	/**
	 * a.x+b.y+c=0
	 *
	 * @return b for a 2D line
	 */

	public double b() {
		return direction.xd();
	}

	/**
	 * a.x+b.y+c=0
	 *
	 * @return c for a 2D line
	 */

	public double c() {
		return origin.xd() * direction.yd() - origin.yd() * direction.xd();
	}
}
