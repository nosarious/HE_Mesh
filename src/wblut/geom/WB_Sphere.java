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

import java.util.Collection;

import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_Sphere implements WB_Geometry {
	/** Center. */
	WB_Point center;
	/** Radius. */
	double radius;

	/**
	 * 
	 */
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();

	/**
	 * 
	 */
	public WB_Sphere() {
		this.center = geometryfactory.createPoint();
		this.radius = WB_Math.fastAbs(0);
	}

	/**
	 * Instantiates a new WB_Circle.
	 *
	 * @param center
	 * @param radius
	 */
	public WB_Sphere(final WB_Coord center, final double radius) {
		this.center = geometryfactory.createPoint(center);
		this.radius = WB_Math.fastAbs(radius);
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
		if (!(o instanceof WB_Sphere)) {
			return false;
		}
		return (WB_Epsilon.isEqualAbs(radius, ((WB_Sphere) o).getRadius()))
				&& (center.equals(((WB_Sphere) o).getCenter()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (31 * center.hashCode()) + hashCode(radius);
	}

	/**
	 * 
	 *
	 * @param v
	 * @return
	 */
	private int hashCode(final double v) {
		final long tmp = Double.doubleToLongBits(v);
		return (int) (tmp ^ (tmp >>> 32));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Geometry#getType()
	 */
	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.SPHERE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Sphere apply(final WB_Transform T) {
		return geometryfactory.createSphereWithRadius(center.applyAsPoint(T), radius);
	}

	/**
	 * Get copy.
	 *
	 * @return copy
	 */
	public WB_Sphere get() {
		return new WB_Sphere(center, radius);
	}

	/**
	 * Gets the center.
	 *
	 * @return the center
	 */
	public WB_Point getCenter() {
		return center;
	}

	/**
	 * Sets the center.
	 *
	 * @param c
	 *            the new center
	 */
	public void setCenter(final WB_Coord c) {
		this.center = new WB_Point(c);
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the radius.
	 *
	 * @param r
	 *            the new radius
	 */
	public void setRadius(final double r) {
		this.radius = r;
	}

	/**
	 * Grow sphere to include point.
	 *
	 * @param p
	 *            point to include
	 */
	public void growSpherebyPoint(final WB_Coord p) {
		final WB_Vector d = WB_Point.subToVector3D(p, center);
		final double dist2 = d.getSqLength3D();
		if (dist2 > (radius * radius)) {
			final double dist = Math.sqrt(dist2);
			final double newRadius = (radius + dist) * 0.5;
			final double k = (newRadius - radius) / dist;
			radius = newRadius;
			center.addSelf(k * d.xd(), k * d.yd(), k * d.zd());
		}
	}

	/**
	 * Project point to sphere.
	 *
	 * @param v
	 *            the v
	 * @return point projected to sphere
	 */
	public WB_Point projectToSphere(final WB_Coord v) {
		final WB_Point vc = new WB_Point(v).sub(center);
		final double er = vc.normalizeSelf();
		if (WB_Epsilon.isZero(er)) {
			return null;
		}
		return center.addMul(radius, vc);
	}

	/**
	 * 
	 *
	 * @param points 
	 * @return 
	 */
	public static WB_Sphere getBoundingSphere(WB_Coord[] points) {
		WB_Point center = new WB_Point(points[0]);
		double radius = WB_Epsilon.EPSILON;
		double radius2 = radius * radius;
		double dist, dist2, alpha, ialpha2;

		for (int i = 0; i < 3; i++) {
			for (WB_Coord point : points) {
				dist2 = WB_Point.getSqDistance3D(point, center);
				if (dist2 > radius2) {
					dist = Math.sqrt(dist2);
					if (i < 2) {
						alpha = dist / radius;
						ialpha2 = 1.0 / (alpha * alpha);
						radius = 0.5 * (alpha + 1 / alpha) * radius;
						center = geometryfactory.createMidpoint(center.mulSelf(1.0 + ialpha2),
								WB_Point.mul(point, 1.0 - ialpha2));
					} else {
						radius = (radius + dist) * 0.5;
						center.mulAddMulSelf(radius / dist, (dist - radius) / dist, point);
					}
					radius2 = radius * radius;
				}
			}
		}

		return new WB_Sphere(center, radius);
	}

	/**
	 * 
	 *
	 * @param points 
	 * @return 
	 */
	public static WB_Sphere getBoundingSphere(Collection<? extends WB_Coord> points) {
		WB_Point center = new WB_Point(points.iterator().next());
		double radius = WB_Epsilon.EPSILON;
		double radius2 = radius * radius;
		double dist, dist2, alpha, ialpha2;

		for (int i = 0; i < 3; i++) {
			for (WB_Coord point : points) {
				dist2 = WB_Point.getSqDistance3D(point, center);
				if (dist2 > radius2) {
					dist = Math.sqrt(dist2);
					if (i < 2) {
						alpha = dist / radius;
						ialpha2 = 1.0 / (alpha * alpha);
						radius = 0.5 * (alpha + 1 / alpha) * radius;
						center = geometryfactory.createMidpoint(center.mulSelf(1.0 + ialpha2),
								WB_Point.mul(point, 1.0 - ialpha2));
					} else {
						radius = (radius + dist) * 0.5;
						center.mulAddMulSelf(radius / dist, (dist - radius) / dist, point);
					}
					radius2 = radius * radius;
				}
			}
		}

		return new WB_Sphere(center, radius);
	}
}
