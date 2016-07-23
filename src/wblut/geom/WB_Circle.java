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
import wblut.math.WB_Math;

public class WB_Circle {

	private WB_Point center;

	private WB_Vector normal;

	private double radius;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();

	/**
	 *
	 */
	public WB_Circle() {
		center = geometryfactory.createPoint();
		normal = geometryfactory.createVector(0, 0, 1);
		radius = 1;
	}

	/**
	 *
	 *
	 * @param center
	 * @param radius
	 */
	public WB_Circle(final WB_Coord center, final double radius) {
		this.center = geometryfactory.createPoint(center);
		this.radius = WB_Math.fastAbs(radius);
		normal = geometryfactory.createVector(0, 0, 1);
	}

	/**
	 *
	 *
	 * @param center
	 * @param normal
	 * @param radius
	 */
	public WB_Circle(final WB_Coord center, final WB_Coord normal, final double radius) {
		this.center = geometryfactory.createPoint(center);
		this.radius = WB_Math.fastAbs(radius);
		this.normal = geometryfactory.createNormalizedVector(normal);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param r
	 */
	public WB_Circle(final double x, final double y, final double r) {
		center = geometryfactory.createPoint(x, y);
		radius = WB_Math.fastAbs(r);
		normal = geometryfactory.createVector(0, 0, 1);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getCenter() {
		return center;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getNormal() {
		return normal;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Circle)) {
			return false;
		}
		return WB_Epsilon.isEqualAbs(radius, ((WB_Circle) o).getRadius()) && center.equals(((WB_Circle) o).getCenter())
				&& normal.equals(((WB_Circle) o).getNormal());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * (31 * center.hashCode() + hashCode(radius)) + normal.hashCode();
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	private int hashCode(final double v) {
		final long tmp = Double.doubleToLongBits(v);
		return (int) (tmp ^ tmp >>> 32);
	}

	/**
	 *
	 *
	 * @param C
	 * @return
	 */
	public boolean isTangent2D(final WB_Circle C) {
		final double d = center.getDistance3D(C.getCenter());
		return WB_Epsilon.isZero(d - WB_Math.fastAbs(C.getRadius() - radius))
				|| WB_Epsilon.isZero(d - WB_Math.fastAbs(C.getRadius() + radius));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
	 */

	public WB_Circle apply(final WB_Transform T) {
		WB_Point p = geometryfactory.createPoint(center).applyAsPointSelf(T);
		WB_Point q = geometryfactory.createPoint(center).addSelf(radius, 0, 0).applyAsPointSelf(T);
		double newradius = p.getDistance2D(q);
		return geometryfactory.createCircleWithRadius(p, geometryfactory.createVector(normal).applyAsNormalSelf(T),
				newradius);
	}

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public WB_Circle applySelf(final WB_Transform T) {
		WB_Point p = geometryfactory.createPoint(center).applyAsPointSelf(T);
		WB_Point q = geometryfactory.createPoint(center).addSelf(radius, 0, 0).applyAsPointSelf(T);
		double newradius = p.getDistance2D(q);
		center.set(p);
		normal.applyAsNormalSelf(T);
		radius = newradius;
		return this;
	}

	/**
	 *
	 *
	 * @param c
	 */
	public void set(final WB_Circle c) {
		center.set(c.getCenter());
		normal.set(c.getNormal());
		radius = c.getRadius();

	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 */
	public void setCenter(final double x, final double y) {
		center.set(x, y);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setCenter(final double x, final double y, final double z) {
		center.set(x, y, z);
	}

	/**
	 *
	 *
	 * @param c
	 */
	public void setCenter(final WB_Coord c) {
		center.set(c);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setNormal(final double x, final double y, final double z) {
		normal.set(x, y, z);
	}

	/**
	 *
	 *
	 * @param c
	 */
	public void setNormal(final WB_Coord c) {
		normal.set(c);
	}

	/**
	 *
	 *
	 * @param radius
	 */
	public void setRadius(final double radius) {
		this.radius = radius;
	}

	/**
	 *
	 *
	 * @param diameter
	 */
	public void setDiameter(final double diameter) {
		this.radius = diameter * 0.5;
	}

	/**
	 * Grow circle to include point.
	 *
	 * @param p
	 *            point to include
	 */
	public void growCircleByPoint(final WB_Coord p) {
		final WB_Vector d = WB_Point.subToVector2D(p, center);
		final double dist2 = d.getSqLength2D();
		if (dist2 > radius * radius) {
			final double dist = Math.sqrt(dist2);
			final double newRadius = (radius + dist) * 0.5;
			final double k = (newRadius - radius) / dist;
			radius = newRadius;
			center.addSelf(k * d.xd(), k * d.yd());
		}
	}

	/**
	 * Project point to circle
	 *
	 * @param v
	 *            the v
	 * @return point projected to circle
	 */
	public WB_Point projectToCircle(final WB_Coord v) {
		final WB_Point vc = new WB_Point(v).sub(center);
		final double er = vc.normalizeSelf();
		if (WB_Epsilon.isZero(er)) {
			return null;
		}
		return center.addMul(radius, vc);
	}

}
