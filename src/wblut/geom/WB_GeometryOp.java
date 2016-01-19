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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;


public class WB_GeometryOp {

	private static final WB_GeometryFactory gf = WB_GeometryFactory.instance();


	/**
	 *
	 *
	 * @param S
	 * @param P
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Segment S, final WB_Plane P) {
		final WB_Vector ab = WB_Vector.subToVector3D(S.getEndpoint(), S.getOrigin());
		double t = (P.d() - P.getNormal().dot(S.getOrigin())) / P.getNormal().dot(ab);
		if ((t >= -WB_Epsilon.EPSILON) && (t <= (1.0 + WB_Epsilon.EPSILON))) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = S.getParametricPointOnSegment(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		return NOINTERSECTION(t, t);
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @param P
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Coord a, final WB_Coord b, final WB_Plane P) {
		final WB_Vector ab = new WB_Vector(a, b);
		double t = (P.d() - P.getNormal().dot(a)) / P.getNormal().dot(ab);
		if ((t >= -WB_Epsilon.EPSILON) && (t <= (1.0 + WB_Epsilon.EPSILON))) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = new WB_Point(a.xd() + (t * (b.xd() - a.xd())), a.yd() + (t * (b.yd() - a.yd())),
					a.zd() + (t * (b.zd() - a.zd())));
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		return NOINTERSECTION(t, t);
	}

	// RAY-PLANE

	/**
	 *
	 *
	 * @param R
	 * @param P
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Ray R, final WB_Plane P) {
		final WB_Coord ab = R.getDirection();
		double t = (P.d() - P.getNormal().dot(R.getOrigin())) / P.getNormal().dot(ab);
		if (t >= -WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, Double.POSITIVE_INFINITY);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = R.getPointOnLine(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		return NOINTERSECTION(t, t);
	}


	/**
	 *
	 *
	 * @param R
	 * @param aabb
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Ray R, final WB_AABB aabb) {
		final WB_Coord d = R.getDirection();
		final WB_Coord p = R.getOrigin();
		double tmin = 0.0;
		double tmax = Double.POSITIVE_INFINITY;
		if (WB_Epsilon.isZero(d.xd())) {
			if ((p.xd() < aabb.getMinX()) || (p.xd() > aabb.getMaxX())) {
				return NOINTERSECTION();
			}
		} else {
			final double ood = 1.0 / d.xd();
			double t1 = (aabb.getMinX() - p.xd()) * ood;
			double t2 = (aabb.getMaxX() - p.xd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				return NOINTERSECTION();
			}
		}
		if (WB_Epsilon.isZero(d.yd())) {
			if ((p.yd() < aabb.getMinY()) || (p.yd() > aabb.getMaxY())) {
				return NOINTERSECTION();
			}
		} else {
			final double ood = 1.0 / d.yd();
			double t1 = (aabb.getMinY() - p.yd()) * ood;
			double t2 = (aabb.getMaxY() - p.yd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				return NOINTERSECTION();
			}
		}
		if (WB_Epsilon.isZero(d.zd())) {
			if ((p.zd() < aabb.getMinZ()) || (p.zd() > aabb.getMaxZ())) {
				return NOINTERSECTION();
			}
		} else {
			final double ood = 1.0 / d.zd();
			double t1 = (aabb.getMinZ() - p.zd()) * ood;
			double t2 = (aabb.getMaxZ() - p.zd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				return NOINTERSECTION();
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = tmin;
		i.t2 = 0;
		i.object = R.getPointOnLine(tmin);
		i.dimension = 0;
		i.sqDist = getSqDistance3D(p, (WB_Point) i.object);
		return i;
	}

	// LINE-PLANE

	/**
	 *
	 *
	 * @param L
	 * @param P
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Line L, final WB_Plane P) {
		final WB_Coord ab = L.getDirection();
		final double denom = P.getNormal().dot(ab);
		if (!WB_Epsilon.isZero(denom)) {
			final double t = (P.d() - P.getNormal().dot(L.getOrigin())) / denom;
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = L.getPointOnLine(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		} else {
			return NOINTERSECTION();
		}
	}

	// PLANE-PLANE

	/**
	 *
	 *
	 * @param P1
	 * @param P2
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Plane P1, final WB_Plane P2) {
		final WB_Vector N1 = P1.getNormal();
		final WB_Vector N2 = P2.getNormal();
		final WB_Vector N1xN2 = new WB_Vector(N1.cross(N2));
		if (WB_Epsilon.isZeroSq(N1xN2.getSqLength3D())) {
			return NOINTERSECTION();
		} else {
			final double d1 = P1.d();
			final double d2 = P2.d();
			final double N1N2 = N1.dot(N2);
			final double det = 1 - (N1N2 * N1N2);
			final double c1 = (d1 - (d2 * N1N2)) / det;
			final double c2 = (d2 - (d1 * N1N2)) / det;
			final WB_Point O = new WB_Point(N1.mul(c1).addSelf(N2.mul(c2)));
			new WB_Line(O, N1xN2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Line(O, N1xN2);
			i.dimension = 1;
			i.sqDist = 0;
			return i;
		}
	}

	// PLANE-PLANE-PLANE

	/**
	 *
	 *
	 * @param P1
	 * @param P2
	 * @param P3
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Plane P1, final WB_Plane P2, final WB_Plane P3) {
		final WB_Vector N1 = P1.getNormal().get();
		final WB_Vector N2 = P2.getNormal().get();
		final WB_Vector N3 = P3.getNormal().get();
		final double denom = N1.dot(N2.cross(N3));
		if (WB_Epsilon.isZero(denom)) {
			return NOINTERSECTION();
		} else {
			final WB_Vector N1xN2 = N1.cross(N2);
			final WB_Vector N2xN3 = N2.cross(N3);
			final WB_Vector N3xN1 = N3.cross(N1);
			final double d1 = P1.d();
			final double d2 = P2.d();
			final double d3 = P3.d();
			final WB_Point p = new WB_Point(N2xN3).mulSelf(d1);
			p.addSelf(N3xN1.mul(d2));
			p.addSelf(N1xN2.mul(d3));
			p.divSelf(denom);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = p;
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
	}

	// AABB-AABB

	/**
	 *
	 *
	 * @param one
	 * @param other
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_AABB one, final WB_AABB other) {
		if ((one.getMaxX() < other.getMinX()) || (one.getMinX() > other.getMaxX())) {
			return false;
		}
		if ((one.getMaxY() < other.getMinY()) || (one.getMinY() > other.getMaxY())) {
			return false;
		}
		if ((one.getMaxZ() < other.getMinZ()) || (one.getMinZ() > other.getMaxZ())) {
			return false;
		}
		return true;
	}


	/**
	 *
	 *
	 * @param AABB
	 * @param P
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_AABB AABB, final WB_Plane P) {
		final WB_Point c = AABB.getMax().add(AABB.getMin()).mulSelf(0.5);
		final WB_Point e = AABB.getMax().sub(c);
		final double r = (e.xd() * WB_Math.fastAbs(P.getNormal().xd())) + (e.yd() * WB_Math.fastAbs(P.getNormal().yd()))
				+ (e.zd() * WB_Math.fastAbs(P.getNormal().zd()));
		final double s = P.getNormal().dot(c) - P.d();
		return WB_Math.fastAbs(s) <= r;
	}


	/**
	 *
	 *
	 * @param AABB
	 * @param S
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_AABB AABB, final WB_Sphere S) {
		final double d2 = getSqDistance3D(S.getCenter(), AABB);
		return d2 <= (S.getRadius() * S.getRadius());
	}


	/**
	 *
	 *
	 * @param T
	 * @param S
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Triangle T, final WB_Sphere S) {
		final WB_Point p = getClosestPoint3D(S.getCenter(), T);
		return (p.subToVector3D(S.getCenter())).getSqLength3D() <= (S.getRadius() * S.getRadius());
	}

	// TRIANGLE-AABB

	/**
	 *
	 *
	 * @param T
	 * @param AABB
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Triangle T, final WB_AABB AABB) {
		double p0, p1, p2, r;
		final WB_Point c = AABB.getMax().add(AABB.getMin()).mulSelf(0.5);
		final double e0 = (AABB.getMaxX() - AABB.getMinX()) * 0.5;
		final double e1 = (AABB.getMaxY() - AABB.getMinY()) * 0.5;
		final double e2 = (AABB.getMaxZ() - AABB.getMinZ()) * 0.5;
		final WB_Point v0 = new WB_Point(T.p1());
		final WB_Point v1 = new WB_Point(T.p2());
		final WB_Point v2 = new WB_Point(T.p3());
		v0.subSelf(c);
		v1.subSelf(c);
		v2.subSelf(c);
		final WB_Vector f0 = v1.subToVector3D(v0);
		final WB_Vector f1 = v2.subToVector3D(v1);
		final WB_Vector f2 = v0.subToVector3D(v2);
		// a00
		final WB_Vector a = new WB_Vector(0, -f0.zd(), f0.yd());// u0xf0
		if (a.isZero()) {
			a.set(0, v0.yd(), v0.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a01
		a.set(0, -f1.zd(), f1.yd());// u0xf1
		if (a.isZero()) {
			a.set(0, v1.yd(), v1.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a02
		a.set(0, -f2.zd(), f2.yd());// u0xf2
		if (a.isZero()) {
			a.set(0, v2.yd(), v2.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a10
		a.set(f0.zd(), 0, -f0.xd());// u1xf0
		if (a.isZero()) {
			a.set(v0.xd(), 0, v0.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a11
		a.set(f1.zd(), 0, -f1.xd());// u1xf1
		if (a.isZero()) {
			a.set(v1.xd(), 0, v1.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a12
		a.set(f2.zd(), 0, -f2.xd());// u1xf2
		if (a.isZero()) {
			a.set(v2.xd(), 0, v2.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a20
		a.set(-f0.yd(), f0.xd(), 0);// u2xf0
		if (a.isZero()) {
			a.set(v0.xd(), v0.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a21
		a.set(-f1.yd(), f1.xd(), 0);// u2xf1
		if (a.isZero()) {
			a.set(v1.xd(), v1.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a22
		a.set(-f2.yd(), f2.xd(), 0);// u2xf2
		if (a.isZero()) {
			a.set(v2.xd(), v2.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd())) + (e2 * WB_Math.fastAbs(a.zd()));
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		if ((WB_Math.max(v0.xd(), v1.xd(), v2.xd()) < -e0) || (WB_Math.max(v0.xd(), v1.xd(), v2.xd()) > e0)) {
			return false;
		}
		if ((WB_Math.max(v0.yd(), v1.yd(), v2.yd()) < -e1) || (WB_Math.max(v0.yd(), v1.yd(), v2.yd()) > e1)) {
			return false;
		}
		if ((WB_Math.max(v0.zd(), v1.zd(), v2.zd()) < -e2) || (WB_Math.max(v0.zd(), v1.zd(), v2.zd()) > e2)) {
			return false;
		}
		WB_Vector n = f0.cross(f1);
		WB_Plane P;
		if (!n.isZero()) {
			P = new WB_Plane(n, n.dot(v0));
		} else {
			n = f0.cross(f2);
			n = f0.cross(n);
			if (!n.isZero()) {
				P = new WB_Plane(n, n.dot(v0));
			} else {
				final WB_Vector t = new WB_Point(T.p3()).subToVector3D(T.p1());
				final double a1 = WB_Vector.dot(T.p1(), t);
				final double a2 = WB_Vector.dot(T.p2(), t);
				final double a3 = WB_Vector.dot(T.p3(), t);
				if (a1 < WB_Math.min(a2, a3)) {
					if (a2 < a3) {
						return checkIntersection3D(new WB_Segment(T.p1(), T.p3()), AABB);
					} else {
						return checkIntersection3D(new WB_Segment(T.p1(), T.p2()), AABB);
					}
				} else if (a2 < WB_Math.min(a1, a3)) {
					if (a1 < a3) {
						return checkIntersection3D(new WB_Segment(T.p2(), T.p3()), AABB);
					} else {
						return checkIntersection3D(new WB_Segment(T.p2(), T.p1()), AABB);
					}
				} else {
					if (a1 < a2) {
						return checkIntersection3D(new WB_Segment(T.p3(), T.p2()), AABB);
					} else {
						return checkIntersection3D(new WB_Segment(T.p3(), T.p1()), AABB);
					}
				}
			}
		}
		return checkIntersection3D(AABB, P);
	}

	// SEGMENT-AABB

	/**
	 *
	 *
	 * @param S
	 * @param AABB
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Segment S, final WB_AABB AABB) {
		final WB_Vector e = AABB.getMax().subToVector3D(AABB.getMin());
		final WB_Vector d = WB_Vector.subToVector3D(S.getEndpoint(), S.getOrigin());
		final WB_Point m = new WB_Point((S.getEndpoint().xd() + S.getOrigin().xd()) - AABB.getMinX() - AABB.getMaxX(),
				(S.getEndpoint().yd() + S.getOrigin().yd()) - AABB.getMinY() - AABB.getMaxY(),
				(S.getEndpoint().zd() + S.getOrigin().zd()) - AABB.getMinZ() - AABB.getMaxZ());
		double adx = WB_Math.fastAbs(d.xd());
		if (WB_Math.fastAbs(m.xd()) > (e.xd() + adx)) {
			return false;
		}
		double ady = WB_Math.fastAbs(d.yd());
		if (WB_Math.fastAbs(m.yd()) > (e.yd() + ady)) {
			return false;
		}
		double adz = WB_Math.fastAbs(d.zd());
		if (WB_Math.fastAbs(m.zd()) > (e.zd() + adz)) {
			return false;
		}
		adx += WB_Epsilon.EPSILON;
		ady += WB_Epsilon.EPSILON;
		adz += WB_Epsilon.EPSILON;
		if (WB_Math.fastAbs((m.yd() * d.zd()) - (m.zd() * d.yd())) > ((e.yd() * adz) + (e.zd() * ady))) {
			return false;
		}
		if (WB_Math.fastAbs((m.zd() * d.xd()) - (m.xd() * d.zd())) > ((e.xd() * adz) + (e.zd() * adx))) {
			return false;
		}
		if (WB_Math.fastAbs((m.xd() * d.yd()) - (m.yd() * d.xd())) > ((e.xd() * ady) + (e.yd() * adx))) {
			return false;
		}
		return true;
	}

	// SPHERE-SPHERE

	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Sphere S1, final WB_Sphere S2) {
		final WB_Vector d = S1.getCenter().subToVector3D(S2.getCenter());
		final double d2 = d.getSqLength3D();
		final double radiusSum = S1.getRadius() + S2.getRadius();
		return d2 <= (radiusSum * radiusSum);
	}

	// RAY-SPHERE

	/**
	 *
	 *
	 * @param R
	 * @param S
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Ray R, final WB_Sphere S) {
		final WB_Vector m = WB_Vector.subToVector3D(R.getOrigin(), S.getCenter());
		final double c = m.dot(m) - (S.getRadius() * S.getRadius());
		if (c <= 0) {
			return true;
		}
		final double b = m.dot(R.getDirection());
		if (b >= 0) {
			return false;
		}
		final double disc = (b * b) - c;
		if (disc < 0) {
			return false;
		}
		return true;
	}


	/**
	 *
	 *
	 * @param R
	 * @param AABB
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Ray R, final WB_AABB AABB) {
		double t0 = 0;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / R.direction.xd();
		double tnear = (AABB.getMinX() - R.origin.xd()) * irx;
		double tfar = (AABB.getMaxX() - R.origin.xd()) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / R.direction.yd();
		tnear = (AABB.getMinY() - R.origin.yd()) * iry;
		tfar = (AABB.getMaxY() - R.origin.yd()) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / R.direction.zd();
		tnear = (AABB.getMinZ() - R.origin.zd()) * irz;
		tfar = (AABB.getMaxZ() - R.origin.zd()) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}


	/**
	 *
	 *
	 * @param R
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Ray R, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(R, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param aabb
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_AABB aabb, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(aabb, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param L
	 * @param AABB
	 * @return
	 */
	public static boolean checkIntersection3D(final WB_Line L, final WB_AABB AABB) {
		double t0 = Double.NEGATIVE_INFINITY;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / L.direction.xd();
		double tnear = (AABB.getMinX() - L.origin.xd()) * irx;
		double tfar = (AABB.getMaxX() - L.origin.xd()) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / L.direction.yd();
		tnear = (AABB.getMinY() - L.origin.yd()) * iry;
		tfar = (AABB.getMaxY() - L.origin.yd()) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / L.direction.zd();
		tnear = (AABB.getMinZ() - L.origin.zd()) * irz;
		tfar = (AABB.getMaxZ() - L.origin.zd()) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}


	/**
	 *
	 *
	 * @param L
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Line L, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(L, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param S
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Segment S, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(S, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param P
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Plane P, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(current.getAABB(), P)) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param T
	 * @param tree
	 * @return
	 */
	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Triangle T, final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(T, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return result;
	}

	// TODO: implement for polygons with holes

	/**
	 *
	 *
	 * @param poly
	 * @param P
	 * @return
	 */
	public static ArrayList<WB_Segment> getIntersection3D(final WB_Polygon poly, final WB_Plane P) {
		final ArrayList<WB_Segment> result = new ArrayList<WB_Segment>();

		final ArrayList<WB_Coord> splitVerts = new ArrayList<WB_Coord>();
		final int numVerts = poly.getNumberOfShellPoints();
		if (numVerts > 0) {
			WB_Coord a = poly.getPoint(numVerts - 1);
			WB_Classification aSide = WB_GeometryOp.classifyPointToPlane3D(a, P);
			WB_Coord b;
			WB_Classification bSide;
			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i;
				b = poly.getPoint(n);
				bSide = WB_GeometryOp.classifyPointToPlane3D(b, P);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = WB_GeometryOp.getIntersection3D(b, a, P);
						splitVerts.add((WB_Point) i.object);
					}
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = WB_GeometryOp.getIntersection3D(a, b, P);
						splitVerts.add((WB_Point) i.object);
					}
				}
				if (aSide == WB_Classification.ON) {
					splitVerts.add(a);
				}
				a = b;
				aSide = bSide;
			}
		}
		for (int i = 0; i < splitVerts.size(); i += 2) {
			if (((i + 1) < splitVerts.size()) && (splitVerts.get(i + 1) != null)) {
				result.add(new WB_Segment(splitVerts.get(i), splitVerts.get(i + 1)));
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Segment S1, final WB_Segment S2) {
		final WB_Vector d1 = new WB_Vector(S1.getEndpoint());
		d1.subSelf(S1.getOrigin());
		final WB_Vector d2 = new WB_Vector(S2.getEndpoint());
		d2.subSelf(S2.getOrigin());
		final WB_Vector r = new WB_Vector(S1.getOrigin());
		r.subSelf(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);
		if (WB_Epsilon.isZero(a) && WB_Epsilon.isZero(e)) {
			// Both segments are degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength3D();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S1.getOrigin(), S2.getOrigin());
			}
			return i;
		}
		if (WB_Epsilon.isZero(a)) {
			// First segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength3D();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S1.getOrigin(), getClosestPoint3D(S1.getOrigin(), S2));
			}
			return i;
		}
		if (WB_Epsilon.isZero(e)) {
			// Second segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength3D();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S2.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S2.getOrigin(), getClosestPoint3D(S2.getOrigin(), S1));
			}
			return i;
		}
		double t1 = 0;
		double t2 = 0;
		final double c = d1.dot(r);
		final double b = d1.dot(d2);
		final double denom = (a * e) - (b * b);
		if (!WB_Epsilon.isZero(denom)) {
			// Non-parallel segments
			t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
		} else {
			// Parallel segments, non-parallel code handles case where
			// projections of segments are disjoint.
			final WB_Line L1 = new WB_Line(S1.getOrigin(), S1.getDirection());
			double s1 = 0;
			double e1 = pointAlongLine(S1.getEndpoint(), L1);
			double s2 = pointAlongLine(S2.getOrigin(), L1);
			double e2 = pointAlongLine(S2.getEndpoint(), L1);
			double tmp;
			if (e2 < s2) {
				tmp = s2;
				s2 = e2;
				e2 = tmp;
			}
			if (s2 < s1) {
				tmp = s2;
				s2 = s1;
				s1 = tmp;
				tmp = e2;
				e2 = e1;
				e1 = tmp;
			}
			if (s2 < e1) {
				// Projections are overlapping
				final WB_Point start = L1.getPointOnLine(s2);
				WB_Point end = L1.getPointOnLine(Math.min(e1, e2));
				if (WB_Epsilon.isZeroSq(getSqDistance3D(S2.getOrigin(), L1))) {
					// Segments are overlapping
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = getSqDistance3D(start, end);
					i.intersection = true;
					if (WB_Epsilon.isZeroSq(i.sqDist)) {
						i.dimension = 0;
						i.object = start;
					} else {
						i.dimension = 1;
						i.object = new WB_Segment(start, end);
					}
					return i;
				} else {
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = getSqDistance3D(start, end);
					i.intersection = false;
					i.dimension = 1;
					start.addSelf(end);
					start.scaleSelf(0.5);
					end = new WB_Point(getClosestPoint3D(start, S2));
					i.object = new WB_Segment(start, end);
					return i;
				}
			}
			t1 = 0;
		}
		final double tnom = (b * t1) + f;
		if (tnom < 0) {
			t1 = WB_Math.clamp(-c / a, 0, 1);
		} else if (tnom > e) {
			t2 = 1;
			t1 = WB_Math.clamp((b - c) / a, 0, 1);
		} else {
			t2 = tnom / e;
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		final WB_Point p1 = S1.getParametricPointOnSegment(t1);
		final WB_Point p2 = S2.getParametricPointOnSegment(t2);
		i.sqDist = getSqDistance3D(p1, p2);
		i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Plane P) {
		final WB_Vector n = P.getNormal();
		final double t = n.dot(p) - P.d();
		return new WB_Point(p.xd() - (t * n.xd()), p.yd() - (t * n.yd()), p.zd() - (t * n.zd()));
	}


	/**
	 *
	 *
	 * @param P
	 * @param p
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Plane P, final WB_Coord p) {
		return getClosestPoint3D(P, p);
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Segment S) {
		final WB_Vector ab = WB_Vector.subToVector3D(S.getEndpoint(), S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(S.getOrigin());
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return new WB_Point(S.getEndpoint());
			} else {
				t = t / denom;
				return new WB_Point(S.getParametricPointOnSegment(t));
			}
		}
	}


	/**
	 *
	 *
	 * @param S
	 * @param p
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Segment S, final WB_Coord p) {
		return getClosestPoint3D(p, S);
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getClosestPointT3D(final WB_Coord p, final WB_Segment S) {
		final WB_Vector ab = WB_Vector.subToVector3D(S.getEndpoint(), S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= WB_Epsilon.EPSILON) {
			return 0;
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= (denom - WB_Epsilon.EPSILON)) {
				t = 1;
				return 1;
			} else {
				t = t / denom;
				return t;
			}
		}
	}


	/**
	 *
	 *
	 * @param S
	 * @param p
	 * @return
	 */
	public static double getClosestPointT3D(final WB_Segment S, final WB_Coord p) {
		return getClosestPointT3D(p, S);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToSegment3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return new WB_Point(b);
			} else {
				t = t / denom;
				return new WB_Point(a.xd() + (t * ab.xd()), a.yd() + (t * ab.yd()), a.zd() + (t * ab.zd()));
			}
		}
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Line L) {
		final WB_Vector ca = new WB_Vector(p.xd() - L.getOrigin().yd(), p.yd() - L.getOrigin().xd(),
				p.zd() - L.getOrigin().zd());
		return L.getPointOnLine(ca.dot(L.getDirection()));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToLine3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return getClosestPoint3D(p, new WB_Line(a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Ray R) {
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return new WB_Point(R.getOrigin());
		} else {
			return new WB_Point(R.getPointOnLine(t));
		}
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToRay3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return getClosestPoint3D(p, new WB_Ray(a, new WB_Vector(a, b)));
	}


	/**
	 *
	 *
	 * @param p
	 * @param AABB
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_AABB AABB) {
		final WB_Point result = new WB_Point();
		double v = p.xd();
		if (v < AABB.getMinX()) {
			v = AABB.getMinX();
		}
		if (v > AABB.getMaxX()) {
			v = AABB.getMaxX();
		}
		result.setX(v);
		v = p.yd();
		if (v < AABB.getMinY()) {
			v = AABB.getMinY();
		}
		if (v > AABB.getMaxY()) {
			v = AABB.getMaxY();
		}
		result.setY(v);
		v = p.zd();
		if (v < AABB.getMinZ()) {
			v = AABB.getMinZ();
		}
		if (v > AABB.getMaxZ()) {
			v = AABB.getMaxZ();
		}
		result.setZ(v);
		return result;
	}


	/**
	 *
	 *
	 * @param p
	 * @param AABB
	 * @param result
	 */
	public static void getClosestPoint3D(final WB_Coord p, final WB_AABB AABB, final WB_MutableCoord result) {
		double v = p.xd();
		if (v < AABB.getMinX()) {
			v = AABB.getMinX();
		}
		if (v > AABB.getMaxX()) {
			v = AABB.getMaxX();
		}
		result.setX(v);
		v = p.yd();
		if (v < AABB.getMinY()) {
			v = AABB.getMinY();
		}
		if (v > AABB.getMaxY()) {
			v = AABB.getMaxY();
		}
		result.setY(v);
		v = p.zd();
		if (v < AABB.getMinZ()) {
			v = AABB.getMinZ();
		}
		if (v > AABB.getMaxZ()) {
			v = AABB.getMaxZ();
		}
		result.setZ(v);
	}

	// POINT-TRIANGLE

	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Triangle T) {
		final WB_Vector ab = new WB_Point(T.p2()).subToVector3D(T.p1());
		final WB_Vector ac = new WB_Point(T.p3()).subToVector3D(T.p1());
		final WB_Vector ap = new WB_Vector(T.p1(), p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return new WB_Point(T.p1());
		}
		final WB_Vector bp = new WB_Vector(T.p2(), p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return new WB_Point(T.p2());
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(T.p1()).addSelf(ab.mulSelf(v));
		}
		final WB_Vector cp = new WB_Vector(T.p3(), p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return new WB_Point(T.p3());
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(T.p1()).addSelf(ac.mulSelf(w));
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(T.p2()).addSelf((new WB_Point(T.p3()).subToVector3D(T.p2())).mulSelf(w));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return new WB_Point(T.p1()).addSelf(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static WB_Point getClosestPointToTriangle3D(final WB_Coord p, final WB_Coord a, final WB_Coord b,
			final WB_Coord c) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, c);
		final WB_Vector ap = new WB_Vector(a, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return new WB_Point(a);
		}
		final WB_Vector bp = new WB_Vector(b, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return new WB_Point(b);
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(a).addSelf(ab.mulSelf(v));
		}
		final WB_Vector cp = new WB_Vector(c, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return new WB_Point(c);
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(a).addSelf(ac.mulSelf(w));
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(b).addSelf(new WB_Vector(b, c).mulSelf(w));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return new WB_Point(a).addSelf(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
	}


	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coord p, final WB_Triangle T) {
		final WB_Vector ab = new WB_Point(T.p2()).subToVector3D(T.p1());
		final WB_Vector ac = new WB_Point(T.p3()).subToVector3D(T.p1());
		final WB_Vector ap = new WB_Vector(T.p1(), p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return new WB_Point(T.p1());
		}
		final WB_Vector bp = new WB_Vector(T.p2(), p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return new WB_Point(T.p2());
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(T.p1()).addSelf(ab.mulSelf(v));
		}
		final WB_Vector cp = new WB_Vector(T.p3(), p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return new WB_Point(T.p3());
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(T.p1()).addSelf(ac.mulSelf(w));
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(T.p2()).addSelf((new WB_Point(T.p3()).subToVector3D(T.p2())).mulSelf(w));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		// WB_Vector bc = new WB_Point(T.p3()).subToVector3D(T.p2());
		if (WB_Epsilon.isZero(u - 1)) {
			return new WB_Point(T.p1());
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return new WB_Point(T.p2());
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return new WB_Point(T.p3());
		}
		final WB_Point A = getClosestPointToSegment3D(p, T.p2(), T.p3());
		final double dA2 = getSqDistance3D(p, A);
		final WB_Point B = getClosestPointToSegment3D(p, T.p1(), T.p3());
		final double dB2 = getSqDistance3D(p, B);
		final WB_Point C = getClosestPointToSegment3D(p, T.p1(), T.p2());
		final double dC2 = getSqDistance3D(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}
	}

	// POINT-POLYGON

	/**
	 *
	 *
	 * @param p
	 * @param tris
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final List<? extends WB_Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		return closest;
	}

	// LINE-LINE

	/**
	 *
	 *
	 * @param L1
	 * @param L2
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint3D(final WB_Line L1, final WB_Line L2) {
		final double a = WB_Vector.dot(L1.getDirection(), L1.getDirection());
		final double b = WB_Vector.dot(L1.getDirection(), L2.getDirection());
		final WB_Vector r = WB_Vector.subToVector3D(L1.getOrigin(), L2.getOrigin());
		final double c = WB_Vector.dot(L1.getDirection(), r);
		final double e = WB_Vector.dot(L2.getDirection(), L2.getDirection());
		final double f = WB_Vector.dot(L2.getDirection(), r);
		double denom = (a * e) - (b * b);
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
			final double d2 = getSqDistance3D(L1.getOrigin(), p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.object = new WB_Segment(L1.getOrigin(), p2);
			i.dimension = 1;
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = ((b * f) - (c * e)) * denom;
		final double t2 = ((a * f) - (b * c)) * denom;
		final WB_Point p1 = new WB_Point(L1.getPointOnLine(t1));
		final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
		final double d2 = getSqDistance3D(p1, p2);
		if (WB_Epsilon.isZeroSq(d2)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t1;
			i.t2 = t2;
			i.dimension = 0;
			i.object = p1;
			i.sqDist = d2;
			return i;
		} else {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t1;
			i.t2 = t2;
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
			i.sqDist = d2;
			return i;
		}
	}

	// POINT-TETRAHEDRON

	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Tetrahedron T) {
		WB_Point closestPt = new WB_Point(p);
		double bestSqDist = Double.POSITIVE_INFINITY;
		if (pointOtherSideOfPlane(p, T.p4, T.p1, T.p2, T.p3)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p2, T.p3);
			final double sqDist = (q.subToVector3D(p)).getSqLength3D();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}
		if (pointOtherSideOfPlane(p, T.p2, T.p1, T.p3, T.p4)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p3, T.p4);
			final double sqDist = (q.subToVector3D(p)).getSqLength3D();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}
		if (pointOtherSideOfPlane(p, T.p3, T.p1, T.p4, T.p2)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p4, T.p2);
			final double sqDist = (q.subToVector3D(p)).getSqLength3D();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}
		if (pointOtherSideOfPlane(p, T.p1, T.p2, T.p4, T.p3)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p2, T.p4, T.p3);
			final double sqDist = (q.subToVector3D(p)).getSqLength3D();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}
		return new WB_Point(closestPt);
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean pointOtherSideOfPlane(final WB_Coord p, final WB_Coord q, final WB_Coord a, final WB_Coord b,
			final WB_Coord c) {
		final double signp = new WB_Vector(a, p).dot(new WB_Vector(a, b).crossSelf(new WB_Vector(a, c)));
		final double signq = new WB_Vector(a, q).dot(new WB_Vector(a, b).crossSelf(new WB_Vector(a, c)));
		return (signp * signq) <= 0;
	}


	protected static class TriangleIntersection {

		public WB_Point p0; // the first point of the line

		public WB_Point p1; // the second point of the line

		public double s0; // the distance along the line to the first
		// intersection with the triangle

		public double s1; // the distance along the line to the second
		// intersection with the triangle
	}


	/**
	 *
	 *
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param q1
	 * @param q2
	 * @param q3
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Coord p1, final WB_Coord p2,final WB_Coord p3, final WB_Coord q1, final WB_Coord q2,final WB_Coord q3) {
		// Taken from
		// http://jgt.akpeters.com/papers/Moller97/tritri.html#ISECTLINE
		// Compute plane equation of first triangle: n1 * x + d1 = 0.
		final WB_Plane P1 = gf.createPlane(p1,p2,p3);
		final WB_Vector n1 = P1.getNormal();
		final double d1 = -P1.d();
		// Evaluate second triangle with plane equation 1 to determine signed
		// distances to the plane.
		double du0 = n1.dot(q1) + d1;
		double du1 = n1.dot(q2) + d1;
		double du2 = n1.dot(q3) + d1;
		// Coplanarity robustness check.
		if (Math.abs(du0) < WB_Epsilon.EPSILON) {
			du0 = 0;
		}
		if (Math.abs(du1) < WB_Epsilon.EPSILON) {
			du1 = 0;
		}
		if (Math.abs(du2) < WB_Epsilon.EPSILON) {
			du2 = 0;
		}
		final double du0du1 = du0 * du1;
		final double du0du2 = du0 * du2;
		if ((du0du1 > 0) && (du0du2 > 0)) {
			return NOINTERSECTION();
			// same sign on all of them + != 0 ==> no
		}
		// intersection
		final WB_Plane P2 = gf.createPlane(q1,q2,q3);
		final WB_Vector n2 = P2.getNormal();
		final double d2 = -P2.d();
		// Compute plane equation of second triangle: n2 * x + d2 = 0
		// Evaluate first triangle with plane equation 2 to determine signed
		// distances to the plane.
		double dv0 = n2.dot(p1) + d2;
		double dv1 = n2.dot(p2) + d2;
		double dv2 = n2.dot(p3) + d2;
		// Coplanarity robustness check.
		if (Math.abs(dv0) < WB_Epsilon.EPSILON) {
			dv0 = 0;
		}
		if (Math.abs(dv1) < WB_Epsilon.EPSILON) {
			dv1 = 0;
		}
		if (Math.abs(dv2) < WB_Epsilon.EPSILON) {
			dv2 = 0;
		}
		final double dv0dv1 = dv0 * dv1;
		final double dv0dv2 = dv0 * dv2;
		if ((dv0dv1 > 0) && (dv0dv2 > 0)) {
			return NOINTERSECTION();
			// same sign on all of them + != 0 ==> no
		}
		// Compute direction of intersection line.
		final WB_Vector ld = n1.cross(n2);
		// Compute an index to the largest component of line direction.
		double max = Math.abs(ld.xd());
		int index = 0;
		final double b = Math.abs(ld.yd());
		final double c = Math.abs(ld.zd());
		if (b > max) {
			max = b;
			index = 1;
		}
		if (c > max) {
			index = 2;
		}
		// This is the simplified projection onto the line of intersection.
		double vp0 = p1.xd();
		double vp1 = p2.xd();
		double vp2 = p3.xd();
		double up0 = q1.xd();
		double up1 = q2.xd();
		double up2 = q3.xd();
		if (index == 1) {
			vp0 = p1.yd();
			vp1 = p2.yd();
			vp2 = p3.yd();
			up0 = q1.yd();
			up1 = q2.yd();
			up2 = q3.yd();
		} else if (index == 2) {
			vp0 = p1.zd();
			vp1 = p2.zd();
			vp2 = p3.zd();
			up0 = q1.zd();
			up1 = q2.zd();
			up2 = q3.zd();
		}
		// Compute interval for triangle 1.
		final TriangleIntersection isectA = compute_intervals_isectline(p1,p2,p3, vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1,
				dv0dv2);
		if (isectA == null) {
			if (coplanarTriangles(n1, p1,p2,p3, q1,q2,q3)) {
				return NOINTERSECTION();
			} else {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				return i;
			}
		}
		int smallest1 = 0;
		if (isectA.s0 > isectA.s1) {
			final double cc = isectA.s0;
			isectA.s0 = isectA.s1;
			isectA.s1 = cc;
			smallest1 = 1;
		}
		// Compute interval for triangle 2.
		final TriangleIntersection isectB = compute_intervals_isectline(q1,q2,q3, up0, up1, up2, du0, du1, du2, du0du1,
				du0du2);
		int smallest2 = 0;
		if (isectB.s0 > isectB.s1) {
			final double cc = isectB.s0;
			isectB.s0 = isectB.s1;
			isectB.s1 = cc;
			smallest2 = 1;
		}
		if ((isectA.s1 < isectB.s0) || (isectB.s1 < isectA.s0)) {
			return NOINTERSECTION();
		}
		// At this point we know that the triangles intersect: there's an
		// intersection line, the triangles are not
		// coplanar, and they overlap.
		final WB_Point[] intersectionVertices = new WB_Point[2];
		if (isectB.s0 < isectA.s0) {
			if (smallest1 == 0) {
				intersectionVertices[0] = isectA.p0;
			} else {
				intersectionVertices[0] = isectA.p1;
			}
			if (isectB.s1 < isectA.s1) {
				if (smallest2 == 0) {
					intersectionVertices[1] = isectB.p1;
				} else {
					intersectionVertices[1] = isectB.p0;
				}
			} else {
				if (smallest1 == 0) {
					intersectionVertices[1] = isectA.p1;
				} else {
					intersectionVertices[1] = isectA.p0;
				}
			}
		} else {
			if (smallest2 == 0) {
				intersectionVertices[0] = isectB.p0;
			} else {
				intersectionVertices[0] = isectB.p1;
			}
			if (isectB.s1 > isectA.s1) {
				if (smallest1 == 0) {
					intersectionVertices[1] = isectA.p1;
				} else {
					intersectionVertices[1] = isectA.p0;
				}
			} else {
				if (smallest2 == 0) {
					intersectionVertices[1] = isectB.p1;
				} else {
					intersectionVertices[1] = isectB.p0;
				}
			}
		}
		final WB_IntersectionResult ir = new WB_IntersectionResult();
		ir.intersection = true;
		ir.object = gf.createSegment(intersectionVertices[0], intersectionVertices[1]);
		return ir;
	}


	/**
	 *
	 *
	 * @param v
	 * @param u
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Triangle v, final WB_Triangle u) {
		// Taken from
		// http://jgt.akpeters.com/papers/Moller97/tritri.html#ISECTLINE
		// Compute plane equation of first triangle: n1 * x + d1 = 0.
		final WB_Plane P1 = gf.createPlane(v);
		final WB_Vector n1 = P1.getNormal();
		final double d1 = -P1.d();
		// Evaluate second triangle with plane equation 1 to determine signed
		// distances to the plane.
		double du0 = n1.dot(u.p1()) + d1;
		double du1 = n1.dot(u.p2()) + d1;
		double du2 = n1.dot(u.p3()) + d1;
		// Coplanarity robustness check.
		if (Math.abs(du0) < WB_Epsilon.EPSILON) {
			du0 = 0;
		}
		if (Math.abs(du1) < WB_Epsilon.EPSILON) {
			du1 = 0;
		}
		if (Math.abs(du2) < WB_Epsilon.EPSILON) {
			du2 = 0;
		}
		final double du0du1 = du0 * du1;
		final double du0du2 = du0 * du2;
		if ((du0du1 > 0) && (du0du2 > 0)) {
			return NOINTERSECTION();
			// same sign on all of them + != 0 ==> no
		}
		// intersection
		final WB_Plane P2 = gf.createPlane(u);
		final WB_Vector n2 = P2.getNormal();
		final double d2 = -P2.d();
		// Compute plane equation of second triangle: n2 * x + d2 = 0
		// Evaluate first triangle with plane equation 2 to determine signed
		// distances to the plane.
		double dv0 = n2.dot(v.p1()) + d2;
		double dv1 = n2.dot(v.p2()) + d2;
		double dv2 = n2.dot(v.p3()) + d2;
		// Coplanarity robustness check.
		if (Math.abs(dv0) < WB_Epsilon.EPSILON) {
			dv0 = 0;
		}
		if (Math.abs(dv1) < WB_Epsilon.EPSILON) {
			dv1 = 0;
		}
		if (Math.abs(dv2) < WB_Epsilon.EPSILON) {
			dv2 = 0;
		}
		final double dv0dv1 = dv0 * dv1;
		final double dv0dv2 = dv0 * dv2;
		if ((dv0dv1 > 0) && (dv0dv2 > 0)) {
			return NOINTERSECTION();
			// same sign on all of them + != 0 ==> no
		}
		// Compute direction of intersection line.
		final WB_Vector ld = n1.cross(n2);
		// Compute an index to the largest component of line direction.
		double max = Math.abs(ld.xd());
		int index = 0;
		final double b = Math.abs(ld.yd());
		final double c = Math.abs(ld.zd());
		if (b > max) {
			max = b;
			index = 1;
		}
		if (c > max) {
			index = 2;
		}
		// This is the simplified projection onto the line of intersection.
		double vp0 = v.p1().xd();
		double vp1 = v.p2().xd();
		double vp2 = v.p3().xd();
		double up0 = u.p1().xd();
		double up1 = u.p2().xd();
		double up2 = u.p3().xd();
		if (index == 1) {
			vp0 = v.p1().yd();
			vp1 = v.p2().yd();
			vp2 = v.p3().yd();
			up0 = u.p1().yd();
			up1 = u.p2().yd();
			up2 = u.p3().yd();
		} else if (index == 2) {
			vp0 = v.p1().zd();
			vp1 = v.p2().zd();
			vp2 = v.p3().zd();
			up0 = u.p1().zd();
			up1 = u.p2().zd();
			up2 = u.p3().zd();
		}
		// Compute interval for triangle 1.
		final TriangleIntersection isectA = compute_intervals_isectline(v, vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1,
				dv0dv2);
		if (isectA == null) {
			if (coplanarTriangles(n1, v, u)) {
				return NOINTERSECTION();
			} else {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				return i;
			}
		}
		int smallest1 = 0;
		if (isectA.s0 > isectA.s1) {
			final double cc = isectA.s0;
			isectA.s0 = isectA.s1;
			isectA.s1 = cc;
			smallest1 = 1;
		}
		// Compute interval for triangle 2.
		final TriangleIntersection isectB = compute_intervals_isectline(u, up0, up1, up2, du0, du1, du2, du0du1,
				du0du2);
		int smallest2 = 0;
		if (isectB.s0 > isectB.s1) {
			final double cc = isectB.s0;
			isectB.s0 = isectB.s1;
			isectB.s1 = cc;
			smallest2 = 1;
		}
		if ((isectA.s1 < isectB.s0) || (isectB.s1 < isectA.s0)) {
			return NOINTERSECTION();
		}
		// At this point we know that the triangles intersect: there's an
		// intersection line, the triangles are not
		// coplanar, and they overlap.
		final WB_Point[] intersectionVertices = new WB_Point[2];
		if (isectB.s0 < isectA.s0) {
			if (smallest1 == 0) {
				intersectionVertices[0] = isectA.p0;
			} else {
				intersectionVertices[0] = isectA.p1;
			}
			if (isectB.s1 < isectA.s1) {
				if (smallest2 == 0) {
					intersectionVertices[1] = isectB.p1;
				} else {
					intersectionVertices[1] = isectB.p0;
				}
			} else {
				if (smallest1 == 0) {
					intersectionVertices[1] = isectA.p1;
				} else {
					intersectionVertices[1] = isectA.p0;
				}
			}
		} else {
			if (smallest2 == 0) {
				intersectionVertices[0] = isectB.p0;
			} else {
				intersectionVertices[0] = isectB.p1;
			}
			if (isectB.s1 > isectA.s1) {
				if (smallest1 == 0) {
					intersectionVertices[1] = isectA.p1;
				} else {
					intersectionVertices[1] = isectA.p0;
				}
			} else {
				if (smallest2 == 0) {
					intersectionVertices[1] = isectB.p1;
				} else {
					intersectionVertices[1] = isectB.p0;
				}
			}
		}
		final WB_IntersectionResult ir = new WB_IntersectionResult();
		ir.intersection = true;
		ir.object = gf.createSegment(intersectionVertices[0], intersectionVertices[1]);
		return ir;
	}


	/**
	 *
	 *
	 * @param v
	 * @param vv0
	 * @param vv1
	 * @param vv2
	 * @param d0
	 * @param d1
	 * @param d2
	 * @param d0d1
	 * @param d0d2
	 * @return
	 */
	protected static TriangleIntersection compute_intervals_isectline(final WB_Triangle v, final double vv0,
			final double vv1, final double vv2, final double d0, final double d1, final double d2, final double d0d1,
			final double d0d2) {
		if (d0d1 > 0) {
			// plane
			return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
		} else if (d0d2 > 0) {
			return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
		} else if (((d1 * d2) > 0) || (d0 != 0)) {
			return intersect(v.p1(), v.p2(), v.p3(), vv0, vv1, vv2, d0, d1, d2);
		} else if (d1 != 0) {
			return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
		} else if (d2 != 0) {
			return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
		} else {
			return null; // triangles are coplanar
		}
	}

	/**
	 *
	 *
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param vv0
	 * @param vv1
	 * @param vv2
	 * @param d0
	 * @param d1
	 * @param d2
	 * @param d0d1
	 * @param d0d2
	 * @return
	 */
	protected static TriangleIntersection compute_intervals_isectline(final WB_Coord p1,  final WB_Coord p2, final WB_Coord p3, final double vv0,
			final double vv1, final double vv2, final double d0, final double d1, final double d2, final double d0d1,
			final double d0d2) {
		if (d0d1 > 0) {
			// plane
			return intersect(p3, p1, p2, vv2, vv0, vv1, d2, d0, d1);
		} else if (d0d2 > 0) {
			return intersect(p2, p1, p3, vv1, vv0, vv2, d1, d0, d2);
		} else if (((d1 * d2) > 0) || (d0 != 0)) {
			return intersect(p1, p2, p3, vv0, vv1, vv2, d0, d1, d2);
		} else if (d1 != 0) {
			return intersect(p2, p1, p3, vv1, vv0, vv2, d1, d0, d2);
		} else if (d2 != 0) {
			return intersect(p3, p1, p2, vv2, vv0, vv1, d2, d0, d1);
		} else {
			return null; // triangles are coplanar
		}
	}


	/**
	 *
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param vv0
	 * @param vv1
	 * @param vv2
	 * @param d0
	 * @param d1
	 * @param d2
	 * @return
	 */
	protected static TriangleIntersection intersect(final WB_Coord v0, final WB_Coord v1, final WB_Coord v2,
			final double vv0, final double vv1, final double vv2, final double d0, final double d1, final double d2) {
		final TriangleIntersection intersection = new TriangleIntersection();
		double tmp = d0 / (d0 - d1);
		intersection.s0 = vv0 + ((vv1 - vv0) * tmp);
		WB_Vector diff = new WB_Vector(v0, v1);
		diff.mulSelf(tmp);
		intersection.p0 = WB_Point.add(v0, diff);
		tmp = d0 / (d0 - d2);
		intersection.s1 = vv0 + ((vv2 - vv0) * tmp);
		diff = new WB_Vector(v0, v2);
		diff.mulSelf(tmp);
		intersection.p1 = WB_Point.add(v0, diff);
		return intersection;
	}


	/**
	 *
	 *
	 * @param n
	 * @param v
	 * @param u
	 * @return
	 */
	protected static boolean coplanarTriangles(final WB_Vector n, final WB_Triangle v, final WB_Triangle u) {
		// First project onto an axis-aligned plane that maximizes the are of
		// the triangles.
		int i0;
		int i1;
		final double[] a = new double[] { Math.abs(n.xd()), Math.abs(n.yd()), Math.abs(n.zd()) };
		if (a[0] > a[1]) // X > Y
		{
			if (a[0] > a[2]) { // X is greatest
				i0 = 1;
				i1 = 2;
			} else { // Z is greatest
				i0 = 0;
				i1 = 1;
			}
		} else // X < Y
		{
			if (a[2] > a[1]) { // Z is greatest
				i0 = 0;
				i1 = 1;
			} else { // Y is greatest
				i0 = 0;
				i1 = 2;
			}
		}
		// Test all edges of triangle 1 against the edges of triangle 2.
		final double[] v0 = new double[] { v.p1().xd(), v.p1().yd(), v.p1().zd() };
		final double[] v1 = new double[] { v.p2().xd(), v.p2().yd(), v.p2().zd() };
		final double[] v2 = new double[] { v.p3().xd(), v.p3().yd(), v.p3().zd() };
		final double[] u0 = new double[] { u.p1().xd(), u.p1().yd(), u.p1().zd() };
		final double[] u1 = new double[] { u.p2().xd(), u.p2().yd(), u.p2().zd() };
		final double[] u2 = new double[] { u.p3().xd(), u.p3().yd(), u.p3().zd() };
		boolean tf = triangleEdgeTest(v0, v1, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		tf = triangleEdgeTest(v1, v2, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		tf = triangleEdgeTest(v2, v0, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		// Finally, test whether one triangle is contained in the other one.
		tf = pointInTri(v0, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		return pointInTri(u0, v0, v1, v2, i0, i1);
	}

	/**
	 *
	 *
	 * @param n
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param q1
	 * @param q2
	 * @param q3
	 * @return
	 */
	protected static boolean coplanarTriangles(final WB_Vector n, final WB_Coord p1, final WB_Coord p2, final WB_Coord p3, final WB_Coord q1, final WB_Coord q2, final WB_Coord q3) {
		// First project onto an axis-aligned plane that maximizes the are of
		// the triangles.
		int i0;
		int i1;
		final double[] a = new double[] { Math.abs(n.xd()), Math.abs(n.yd()), Math.abs(n.zd()) };
		if (a[0] > a[1]) // X > Y
		{
			if (a[0] > a[2]) { // X is greatest
				i0 = 1;
				i1 = 2;
			} else { // Z is greatest
				i0 = 0;
				i1 = 1;
			}
		} else // X < Y
		{
			if (a[2] > a[1]) { // Z is greatest
				i0 = 0;
				i1 = 1;
			} else { // Y is greatest
				i0 = 0;
				i1 = 2;
			}
		}
		// Test all edges of triangle 1 against the edges of triangle 2.
		final double[] v0 = new double[] { p1.xd(), p1.yd(), p1.zd() };
		final double[] v1 = new double[] { p2.xd(), p2.yd(), p2.zd() };
		final double[] v2 = new double[] { p3.xd(), p3.yd(), p3.zd() };
		final double[] u0 = new double[] { q1.xd(), q1.yd(), q1.zd() };
		final double[] u1 = new double[] { q2.xd(), q2.yd(), q2.zd() };
		final double[] u2 = new double[] { q3.xd(), q3.yd(), q3.zd() };
		boolean tf = triangleEdgeTest(v0, v1, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		tf = triangleEdgeTest(v1, v2, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		tf = triangleEdgeTest(v2, v0, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		// Finally, test whether one triangle is contained in the other one.
		tf = pointInTri(v0, u0, u1, u2, i0, i1);
		if (tf) {
			return true;
		}
		return pointInTri(u0, v0, v1, v2, i0, i1);
	}


	/**
	 *
	 *
	 * @param v0
	 * @param v1
	 * @param u0
	 * @param u1
	 * @param u2
	 * @param i0
	 * @param i1
	 * @return
	 */
	protected static boolean triangleEdgeTest(final double[] v0, final double[] v1, final double[] u0,
			final double[] u1, final double[] u2, final int i0, final int i1) {
		final double ax = v1[i0] - v0[i0];
		final double ay = v1[i1] - v0[i1];
		// Test edge u0:u1 against v0:v1
		boolean tf = edgeEdgeTest(v0, u0, u1, i0, i1, ax, ay);
		if (tf) {
			return true;
		}
		// Test edge u1:u2 against v0:v1
		tf = edgeEdgeTest(v0, u1, u2, i0, i1, ax, ay);
		if (tf) {
			return true;
		}
		// Test edge u2:u0 against v0:v1
		return edgeEdgeTest(v0, u2, u0, i0, i1, ax, ay);
	}


	/**
	 *
	 *
	 * @param v0
	 * @param u0
	 * @param u1
	 * @param i0
	 * @param i1
	 * @param ax
	 * @param ay
	 * @return
	 */
	protected static boolean edgeEdgeTest(final double[] v0, final double[] u0, final double[] u1, final int i0,
			final int i1, final double ax, final double ay) {
		final double bx = u0[i0] - u1[i0];
		final double by = u0[i1] - u1[i1];
		final double cx = v0[i0] - u0[i0];
		final double cy = v0[i1] - u0[i1];
		final double f = (ay * bx) - (ax * by);
		final double d = (by * cx) - (bx * cy);
		if (((f > 0) && (d >= 0) && (d <= f)) || ((f < 0) && (d <= 0) && (d >= f))) {
			final double e = (ax * cy) - (ay * cx);
			if (f > 0) {
				if ((e >= 0) && (e <= f)) {
					return true;
				}
			} else {
				if ((e <= 0) && (e >= f)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 *
	 *
	 * @param v0
	 * @param u0
	 * @param u1
	 * @param u2
	 * @param i0
	 * @param i1
	 * @return
	 */
	protected static boolean pointInTri(final double[] v0, final double[] u0, final double[] u1, final double[] u2,
			final int i0, final int i1) {
		double a = u1[i1] - u0[i1];
		double b = -(u1[i0] - u0[i0]);
		double c = (-a * u0[i0]) - (b * u0[i1]);
		final double d0 = (a * v0[i0]) + (b * v0[i1]) + c;
		a = u2[i1] - u1[i1];
		b = -(u2[i0] - u1[i0]);
		c = (-a * u1[i0]) - (b * u1[i1]);
		final double d1 = (a * v0[i0]) + (b * v0[i1]) + c;
		a = u0[i1] - u2[i1];
		b = -(u0[i0] - u2[i0]);
		c = (-a * u2[i0]) - (b * u2[i1]);
		final double d2 = (a * v0[i0]) + (b * v0[i1]) + c;
		return ((d0 * d1) > 0) && ((d0 * d2) > 0);
	}


	/**
	 *
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static WB_IntersectionResult NOINTERSECTION(final double t1, final double t2) {
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.sqDist = Float.POSITIVE_INFINITY;
		i.t1 = t1;
		i.t2 = t2;
		return i;
	}

	/**
	 *
	 *
	 * @return
	 */
	private static WB_IntersectionResult NOINTERSECTION() {
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.sqDist = Float.POSITIVE_INFINITY;
		i.t1 = Double.NaN;
		i.t2 = Double.NaN;
		return i;
	}


	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static WB_IntersectionResult getIntersection2D(final WB_Segment S1, final WB_Segment S2) {
		final double a1 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(), S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(), S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && ((a1 * a2) < 0)) {
			final double a3 = WB_Triangle.twiceSignedTriArea2D(S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = (a3 + a2) - a1;
			if ((a3 * a4) < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPointOnSegment(t1);
				i.dimension = 0;
				i.sqDist = 0;
				return i;
			}
		}
		return NOINTERSECTION();
	}


	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @param i
	 */
	public static void getIntersection2DInto(final WB_Segment S1, final WB_Segment S2, final WB_IntersectionResult i) {
		final double a1 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(), S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(), S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && ((a1 * a2) < 0)) {
			final double a3 = WB_Triangle.twiceSignedTriArea2D(S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = (a3 + a2) - a1;
			if ((a3 * a4) < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPointOnSegment(t1);
				i.dimension = 0;
				i.sqDist = 0;
			}
		} else {
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
		}
	}


	/**
	 *
	 *
	 * @param S
	 * @param L
	 * @return
	 */
	public static WB_Segment[] splitSegment2D(final WB_Segment S, final WB_Line L) {
		final WB_Segment[] result = new WB_Segment[2];
		final WB_IntersectionResult ir2D = getClosestPoint2D(S, L);
		if (!ir2D.intersection) {
			return null;
		}
		if (ir2D.dimension == 0) {
			if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.FRONT) {
				result[0] = new WB_Segment(S.getOrigin(), (WB_Point) ir2D.object);
				result[1] = new WB_Segment((WB_Point) ir2D.object, S.getEndpoint());
			} else if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.BACK) {
				result[1] = new WB_Segment(S.getOrigin(), (WB_Point) ir2D.object);
				result[0] = new WB_Segment((WB_Point) ir2D.object, S.getEndpoint());
			}
		}
		return result;
	}


	/**
	 *
	 *
	 * @param u0
	 * @param u1
	 * @param v0
	 * @param v1
	 * @return
	 */
	public static double[] getIntervalIntersection2D(final double u0, final double u1, final double v0,
			final double v1) {
		if ((u0 >= u1) || (v0 >= v1)) {
			throw new IllegalArgumentException("Interval degenerate or reversed.");
		}
		final double[] result = new double[3];
		if ((u1 < v0) || (u0 > v1)) {
			return result;
		}
		if (u1 > v0) {
			if (u0 < v1) {
				result[0] = 2;
				if (u0 < v0) {
					result[1] = v0;
				} else {
					result[1] = u0;
				}
				if (u1 > v1) {
					result[2] = v1;
				} else {
					result[2] = u1;
				}
			} else {
				result[0] = 1;
				result[1] = u0;
			}
		} else {
			result[0] = 1;
			result[1] = u1;
		}
		return result;
	}


	/**
	 *
	 *
	 * @param poly
	 * @param L
	 * @return
	 */
	public static WB_Polygon[] splitPolygon2D(final WB_Polygon poly, final WB_Line L) {
		final ArrayList<WB_Coord> frontVerts = new ArrayList<WB_Coord>(20);
		final ArrayList<WB_Coord> backVerts = new ArrayList<WB_Coord>(20);
		final int numVerts = poly.numberOfShellPoints;
		if (numVerts > 0) {
			WB_Coord a = poly.getPoint(numVerts - 1);
			WB_Classification aSide = L.classifyPointToLine2D(a);
			WB_Coord b;
			WB_Classification bSide;
			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i = new WB_IntersectionResult();
				b = poly.getPoint(n);
				bSide = L.classifyPointToLine2D(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = getClosestPoint2D(L, new WB_Segment(a, b));
						WB_Coord p1 = null;
						if (i.dimension == 0) {
							p1 = (WB_Point) i.object;
						} else if (i.dimension == 1) {
							p1 = ((WB_Segment) i.object).getOrigin();
						}
						frontVerts.add(p1);
						backVerts.add(p1);
					}
					frontVerts.add(b);
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = getClosestPoint2D(L, new WB_Segment(a, b));

						final WB_Point p1 = (WB_Point) i.object;
						frontVerts.add(p1);
						backVerts.add(p1);
					} else if (aSide == WB_Classification.ON) {
						backVerts.add(a);
					}
					backVerts.add(b);
				} else {
					frontVerts.add(b);
					if (aSide == WB_Classification.BACK) {
						backVerts.add(b);
					}
				}
				a = b;
				aSide = bSide;
			}
		}
		final WB_Polygon[] result = new WB_Polygon[2];
		result[0] = gf.createSimplePolygon(frontVerts);
		result[1] = gf.createSimplePolygon(backVerts);
		return result;
	}


	/**
	 *
	 *
	 * @param C0
	 * @param C1
	 * @return
	 */
	public static ArrayList<WB_Point> getIntersection2D(final WB_Circle C0, final WB_Circle C1) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		final WB_Point u = WB_Point.sub(C1.getCenter(), C0.getCenter());
		final double d2 = u.getSqLength3D();
		final double d = Math.sqrt(d2);
		if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
			result.add(gf.createInterpolatedPoint(C0.getCenter(), C1.getCenter(),
					C0.getRadius() / (C0.getRadius() + C1.getRadius())));
			return result;
		}
		if ((d > (C0.getRadius() + C1.getRadius())) || (d < WB_Math.fastAbs(C0.getRadius() - C1.getRadius()))) {
			return result;
		}
		final double r02 = C0.getRadius() * C0.getRadius();
		final double r12 = C1.getRadius() * C1.getRadius();
		final double a = ((r02 - r12) + d2) / (2 * d);
		final double h = Math.sqrt(r02 - (a * a));
		final WB_Point c = u.mul(a / d).addSelf(C0.getCenter());
		final double p0x = c.xd() + ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
		final double p0y = c.yd() - ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
		final double p1x = c.xd() - ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
		final double p1y = c.yd() + ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
		final WB_Point p0 = new WB_Point(p0x, p0y);
		result.add(p0);
		final WB_Point p1 = new WB_Point(p1x, p1y);
		if (!WB_Epsilon.isZeroSq(getSqDistance2D(p0, p1))) {
			result.add(new WB_Point(p1x, p1y));
		}
		return result;
	}


	/**
	 *
	 *
	 * @param L
	 * @param C
	 * @return
	 */
	public static ArrayList<WB_Point> getIntersection2D(final WB_Line L, final WB_Circle C) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		final double b = 2 * ((L.getDirection().xd() * (L.getOrigin().xd() - C.getCenter().xd()))
				+ (L.getDirection().yd() * (L.getOrigin().yd() - C.getCenter().yd())));
		final double c = (WB_GeometryOp.getSqLength3D(C.getCenter()) + WB_Vector.getSqLength3D(L.getOrigin()))
				- (2 * ((C.getCenter().xd() * L.getOrigin().xd()) + (C.getCenter().yd() * L.getOrigin().yd())))
				- (C.getRadius() * C.getRadius());
		double disc = (b * b) - (4 * c);
		if (disc < -WB_Epsilon.EPSILON) {
			return result;
		}
		if (WB_Epsilon.isZero(disc)) {
			result.add(L.getPointOnLine(-0.5 * b));
			return result;
		}
		disc = Math.sqrt(disc);
		result.add(L.getPointOnLine(0.5 * (-b + disc)));
		result.add(L.getPointOnLine(0.5 * (-b - disc)));
		return result;
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static boolean checkIntersection2DProper(final WB_Coord a, final WB_Coord b, final WB_Coord c,
			final WB_Coord d) {
		if ((WB_Predicates.orient2D(a, b, c) == 0) || (WB_Predicates.orient2D(a, b, d) == 0)
				|| (WB_Predicates.orient2D(c, d, a) == 0) || (WB_Predicates.orient2D(c, d, b) == 0)) {
			return false;
		} else if (((WB_Predicates.orient2D(a, b, c) * WB_Predicates.orient2D(a, b, d)) > 0)
				|| ((WB_Predicates.orient2D(c, d, a) * WB_Predicates.orient2D(c, d, b)) > 0)) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final WB_Segment S) {
		final WB_Vector ab = new WB_Vector(S.getOrigin(), S.getEndpoint());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(S.getOrigin());
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return new WB_Point(S.getEndpoint());
			} else {
				t = t / denom;
				return new WB_Point(S.getParametricPointOnSegment(t));
			}
		}
	}


	/**
	 *
	 *
	 * @param S
	 * @param p
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Segment S, final WB_Coord p) {
		return getClosestPoint2D(p, S);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToSegment2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return new WB_Point(b);
			} else {
				t = t / denom;
				return new WB_Point(a.xd() + (t * ab.xd()), a.yd() + (t * ab.yd()));
			}
		}
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final WB_Line L) {
		if (WB_Epsilon.isZero(L.getDirection().xd())) {
			return new WB_Point(L.getOrigin().xd(), p.yd());
		}
		if (WB_Epsilon.isZero(L.getDirection().yd())) {
			return new WB_Point(p.xd(), L.getOrigin().yd());
		}
		final double m = L.getDirection().yd() / L.getDirection().xd();
		final double b = L.getOrigin().yd() - (m * L.getOrigin().xd());
		final double x = (((m * p.yd()) + p.xd()) - (m * b)) / ((m * m) + 1);
		final double y = ((m * m * p.yd()) + (m * p.xd()) + b) / ((m * m) + 1);
		return new WB_Point(x, y);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToLine2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Line L = new WB_Line();
		L.setFromPoints(a, b);
		return getClosestPoint2D(p, L);
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final WB_Ray R) {
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return new WB_Point(R.getOrigin());
		} else {
			return R.getPointOnLine(t);
		}
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static WB_Point getClosestPointToRay2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Ray R = new WB_Ray();
		R.setFromPoints(a, b);
		return getClosestPoint2D(p, R);
	}


	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint2D(final WB_Segment S1, final WB_Segment S2) {
		final WB_Point d1 = WB_Point.sub(S1.getEndpoint(), S1.getOrigin());
		final WB_Point d2 = WB_Point.sub(S2.getEndpoint(), S2.getOrigin());
		final WB_Point r = WB_Point.sub(S1.getOrigin(), S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);
		if (WB_Epsilon.isZero(a) || WB_Epsilon.isZero(e)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Segment(S1.getOrigin(), S2.getOrigin());
			i.dimension = 1;
			i.sqDist = r.getSqLength3D();
			return i;
		}
		double t1 = 0;
		double t2 = 0;
		if (WB_Epsilon.isZero(a)) {
			t2 = WB_Math.clamp(f / e, 0, 1);
		} else {
			final double c = d1.dot(r);
			if (WB_Epsilon.isZero(e)) {
				t1 = WB_Math.clamp(-c / a, 0, 1);
			} else {
				final double b = d1.dot(d2);
				final double denom = (a * e) - (b * b);
				if (!WB_Epsilon.isZero(denom)) {
					t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
				} else {
					t1 = 0;
				}
				final double tnom = (b * t1) + f;
				if (tnom < 0) {
					t1 = WB_Math.clamp(-c / a, 0, 1);
				} else if (tnom > e) {
					t2 = 1;
					t1 = WB_Math.clamp((b - c) / a, 0, 1);
				} else {
					t2 = tnom / e;
				}
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = (t1 > 0) && (t1 < 1) && (t2 > 0) && (t2 < 1);
		i.t1 = t1;
		i.t2 = t2;
		final WB_Point p1 = S1.getParametricPointOnSegment(t1);
		final WB_Point p2 = S2.getParametricPointOnSegment(t2);
		i.sqDist = getSqDistance2D(p1, p2);
		if (WB_Epsilon.isZeroSq(i.sqDist)) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;
	}


	/**
	 *
	 *
	 * @param L1
	 * @param L2
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint2D(final WB_Line L1, final WB_Line L2) {
		final double a = WB_Vector.dot(L1.getDirection(), L1.getDirection());
		final double b = WB_Vector.dot(L1.getDirection(), L2.getDirection());
		final WB_Point r = WB_Point.sub(L1.getOrigin(), L2.getOrigin());
		final double c = WB_Vector.dot(L1.getDirection(), r);
		final double e = WB_Vector.dot(L2.getDirection(), L2.getDirection());
		final double f = WB_Vector.dot(L2.getDirection(), r);
		double denom = (a * e) - (b * b);
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
			final double d2 = getSqDistance2D(L1.getOrigin(), p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.dimension = 1;
			i.object = new WB_Segment(L1.getOrigin(), p2);
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = ((b * f) - (c * e)) * denom;
		final double t2 = ((a * f) - (b * c)) * denom;
		final WB_Point p1 = new WB_Point(L1.getPointOnLine(t1));
		final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
		final double d2 = getSqDistance2D(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;
	}


	/**
	 *
	 *
	 * @param L
	 * @param S
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint2D(final WB_Line L, final WB_Segment S) {
		final WB_IntersectionResult i = getClosestPoint2D(L, new WB_Line(S.getOrigin(), S.getDirection()));
		if (i.dimension == 0) {
			return i;
		}
		if (i.t2 <= WB_Epsilon.EPSILON) {
			i.t2 = 0;
			i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S.getOrigin());
			i.sqDist = ((WB_Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		if (i.t2 >= (S.getLength() - WB_Epsilon.EPSILON)) {
			i.t2 = 1;
			i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S.getEndpoint());
			i.sqDist = ((WB_Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		return i;
	}


	/**
	 *
	 *
	 * @param S
	 * @param L
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint2D(final WB_Segment S, final WB_Line L) {
		return getClosestPoint2D(L, S);
	}


	/**
	 *
	 *
	 * @param L
	 * @param S
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint3D(final WB_Line L, final WB_Segment S) {
		final WB_IntersectionResult i = getClosestPoint3D(L, new WB_Line(S.getOrigin(), S.getDirection()));
		if (i.dimension == 0) {
			return i;
		}
		if (i.t2 <= WB_Epsilon.EPSILON) {
			i.t2 = 0;
			i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S.getOrigin());
			i.sqDist = ((WB_Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		if (i.t2 >= (S.getLength() - WB_Epsilon.EPSILON)) {
			i.t2 = 1;
			i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S.getEndpoint());
			i.sqDist = ((WB_Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		return i;
	}

	// POINT-TRIANGLE

	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final WB_Triangle T) {
		final WB_Vector ab = T.p2.subToVector3D(T.p1);
		final WB_Vector ac = T.p3.subToVector3D(T.p1);
		final WB_Vector ap = new WB_Vector(T.p1, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return T.p1.get();
		}
		final WB_Vector bp = new WB_Vector(T.p2, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return T.p2.get();
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab.mulSelf(v));
		}
		final WB_Vector cp = new WB_Vector(T.p3, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return T.p3.get();
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac.mulSelf(w));
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2)).mulSelf(w));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1.add(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static WB_Point getClosestPointToTriangle2D(final WB_Coord p, final WB_Coord a, final WB_Coord b,
			final WB_Coord c) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, c);
		final WB_Vector ap = new WB_Vector(a, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return new WB_Point(a);
		}
		final WB_Vector bp = new WB_Vector(b, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return new WB_Point(b);
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(a).addMulSelf(v, ab);
		}
		final WB_Vector cp = new WB_Vector(c, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return new WB_Point(c);
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(a).addMulSelf(w, ac);
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(b).addMulSelf(w, new WB_Vector(b, c));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return new WB_Point(a).addMulSelf(w, ac).addMulSelf(v, ab);
	}


	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery2D(final WB_Coord p, final WB_Triangle T) {
		final WB_Vector ab = T.p2.subToVector3D(T.p1);
		final WB_Vector ac = T.p3.subToVector3D(T.p1);
		final WB_Vector ap = new WB_Vector(T.p1, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if ((d1 <= 0) && (d2 <= 0)) {
			return T.p1.get();
		}
		final WB_Vector bp = new WB_Vector(T.p2, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if ((d3 >= 0) && (d4 <= d3)) {
			return T.p2.get();
		}
		final double vc = (d1 * d4) - (d3 * d2);
		if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab.mulSelf(v));
		}
		final WB_Vector cp = new WB_Vector(T.p3, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if ((d6 >= 0) && (d5 <= d6)) {
			return T.p3.get();
		}
		final double vb = (d5 * d2) - (d1 * d6);
		if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac.mulSelf(w));
		}
		final double va = (d3 * d6) - (d5 * d4);
		if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2)).mulSelf(w));
		}
		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3.sub(T.p2);
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1.get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2.get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3.get();
		}
		final WB_Point A = getClosestPointToSegment2D(p, T.p2, T.p3);
		final double dA2 = getSqDistance2D(p, A);
		final WB_Point B = getClosestPointToSegment2D(p, T.p1, T.p3);
		final double dB2 = getSqDistance2D(p, B);
		final WB_Point C = getClosestPointToSegment2D(p, T.p1, T.p2);
		final double dC2 = getSqDistance2D(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}
	}

	// POINT-POLYGON

	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final WB_Polygon poly) {
		final int[] tris = poly.getTriangles();
		final int n = tris.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;

		for (int i = 0; i < n; i += 3) {

			tmp = getClosestPointToTriangle2D(p, poly.getPoint(tris[i]), poly.getPoint(tris[i + 1]),
					poly.getPoint(tris[i + 2]));
			final double d2 = getDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		return closest;
	}


	/**
	 *
	 *
	 * @param p
	 * @param tris
	 * @return
	 */
	public static WB_Point getClosestPoint2D(final WB_Coord p, final ArrayList<? extends WB_Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = getDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		return closest;
	}


	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery2D(final WB_Coord p, final WB_Polygon poly) {
		final int[] tris = poly.getTriangles();
		final int n = tris.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;

		for (int i = 0; i < n; i += 3) {

			tmp = getClosestPointToTriangle2D(p, poly.getPoint(tris[i]), poly.getPoint(tris[i + 1]),
					poly.getPoint(tris[i + 2]));
			final double d2 = getSqDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment S;
			for (int i = 0, j = poly.getNumberOfShellPoints() - 1; i < poly.getNumberOfShellPoints(); j = i, i++) {
				S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
				tmp = getClosestPoint2D(p, S);
				final double d2 = getSqDistance2D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}
			}
		}
		return closest;
	}


	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @param tris
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery2D(final WB_Coord p, final WB_Polygon poly,
			final ArrayList<WB_Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = getSqDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment S;
			for (int i = 0, j = poly.getNumberOfShellPoints() - 1; i < poly.getNumberOfShellPoints(); j = i, i++) {
				S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
				tmp = getClosestPoint2D(p, S);
				final double d2 = getSqDistance2D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}
			}
		}
		return closest;
	}


	/**
	 *
	 *
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static WB_IntersectionResult getClosestPoint3D(final WB_Segment S1, final WB_Segment S2) {
		final WB_Point d1 = WB_Point.sub(S1.getEndpoint(), S1.getOrigin());
		final WB_Point d2 = WB_Point.sub(S2.getEndpoint(), S2.getOrigin());
		final WB_Point r = WB_Point.sub(S1.getOrigin(), S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);
		if (WB_Epsilon.isZero(a) || WB_Epsilon.isZero(e)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Segment(S1.getOrigin(), S2.getOrigin());
			i.dimension = 1;
			i.sqDist = r.getSqLength3D();
			return i;
		}
		double t1 = 0;
		double t2 = 0;
		if (WB_Epsilon.isZero(a)) {
			t2 = WB_Math.clamp(f / e, 0, 1);
		} else {
			final double c = d1.dot(r);
			if (WB_Epsilon.isZero(e)) {
				t1 = WB_Math.clamp(-c / a, 0, 1);
			} else {
				final double b = d1.dot(d2);
				final double denom = (a * e) - (b * b);
				if (!WB_Epsilon.isZero(denom)) {
					t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
				} else {
					t1 = 0;
				}
				final double tnom = (b * t1) + f;
				if (tnom < 0) {
					t1 = WB_Math.clamp(-c / a, 0, 1);
				} else if (tnom > e) {
					t2 = 1;
					t1 = WB_Math.clamp((b - c) / a, 0, 1);
				} else {
					t2 = tnom / e;
				}
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = (t1 > 0) && (t1 < 1) && (t2 > 0) && (t2 < 1);
		i.t1 = t1;
		i.t2 = t2;
		final WB_Point p1 = S1.getParametricPointOnSegment(t1);
		final WB_Point p2 = S2.getParametricPointOnSegment(t2);
		i.sqDist = getSqDistance2D(p1, p2);
		if (WB_Epsilon.isZeroSq(i.sqDist)) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;
	}

	// POINT-POLYGON

	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static WB_Point getClosestPoint3D(final WB_Coord p, final WB_Polygon poly) {
		final int[] T = poly.getTriangles();
		final int n = T.length;
		double dmin2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;

		for (int i = 0; i < n; i += 3) {

			WB_Point q = new WB_Point(p);
			if (n > 1) {
				q = projectOnPlane(p,
						new WB_Plane(poly.getPoint(T[i]), poly.getPoint(T[i + 1]), poly.getPoint(T[i + 2])));
			}
			tmp = getClosestPointToTriangle3D(q, poly.getPoint(T[i]), poly.getPoint(T[i + 1]), poly.getPoint(T[i + 2]));
			final double d2 = getSqDistance3D(tmp, q);
			if (d2 < dmin2) {
				closest = tmp;
				dmin2 = d2;
			}
		}
		return closest;
	}

	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static double getDistanceToClosestPoint3D(final WB_Coord p, final WB_Polygon poly) {
		final int[] T = poly.getTriangles();
		final int n = T.length;
		double dmin2 = Double.POSITIVE_INFINITY;
		WB_Point tmp;

		for (int i = 0; i < n; i += 3) {

			WB_Point q = new WB_Point(p);
			if (n > 1) {
				q = projectOnPlane(p,
						new WB_Plane(poly.getPoint(T[i]), poly.getPoint(T[i + 1]), poly.getPoint(T[i + 2])));
			}
			tmp = getClosestPointToTriangle3D(q, poly.getPoint(T[i]), poly.getPoint(T[i + 1]), poly.getPoint(T[i + 2]));
			final double d2 = getSqDistance3D(tmp, q);
			if (d2 < dmin2) {
				dmin2 = d2;
			}
		}
		return Math.sqrt(dmin2);
	}


	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coord p, final WB_Polygon poly) {
		final int[] T = poly.getTriangles();
		final int n = T.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;

		for (int i = 0; i < n; i++) {

			tmp = getClosestPointToTriangle3D(p, poly.getPoint(T[i]), poly.getPoint(T[i + 1]), poly.getPoint(T[i + 2]));
			final double d2 = getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment S;
			for (int i = 0, j = poly.getNumberOfPoints() - 1; i < poly.getNumberOfPoints(); j = i, i++) {
				S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
				tmp = getClosestPoint3D(p, S);
				final double d2 = getSqDistance3D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}
			}
		}
		return closest;
	}

	// TODO: correct for polygons with holes

	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @param tris
	 * @return
	 */
	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coord p, final WB_Polygon poly,
			final List<? extends WB_Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}
		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment S;
			for (int i = 0, j = poly.getNumberOfPoints() - 1; i < poly.getNumberOfPoints(); j = i, i++) {
				S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
				tmp = getClosestPoint3D(p, S);
				final double d2 = getSqDistance3D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}
			}
		}
		return closest;
	}


	/**
	 *
	 *
	 * @param ray
	 * @param poly
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Ray ray, final WB_Polygon poly) {
		final WB_IntersectionResult ir = getIntersection3D(ray, poly.getPlane());
		if (ir.intersection == false) {
			return ir;
		}
		final WB_Point p = (WB_Point) ir.object;
		if (WB_Epsilon.isZero(getDistanceToClosestPoint3D(p, poly))) {
			return ir;
		}
		ir.intersection = false;
		return ir;
	}


	/**
	 *
	 *
	 * @param line
	 * @param poly
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Line line, final WB_Polygon poly) {
		final WB_IntersectionResult ir = getIntersection3D(line, poly.getPlane());
		if (ir.intersection == false) {
			return ir;
		}
		final WB_Point p = (WB_Point) ir.object;
		if (WB_Epsilon.isZero(getDistanceToClosestPoint3D(p, poly))) {
			return ir;
		}
		ir.intersection = false;
		return ir;
	}


	/**
	 *
	 *
	 * @param segment
	 * @param poly
	 * @return
	 */
	public static WB_IntersectionResult getIntersection3D(final WB_Segment segment, final WB_Polygon poly) {
		final WB_IntersectionResult ir = getIntersection3D(segment, poly.getPlane());
		if (ir.intersection == false) {
			return ir;
		}
		final WB_Point p = (WB_Point) ir.object;
		if (WB_Epsilon.isZero(getDistanceToClosestPoint3D(p, poly))) {
			return ir;
		}
		ir.intersection = false;
		return ir;
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean between2D(final WB_Coord a, final WB_Coord b, final WB_Coord c) {
		if (coincident2D(a, c)) {
			return true;
		} else if (coincident2D(b, c)) {
			return true;
		} else {
			if (getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if ((0 < d) && (d < 1)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean betweenStrict2D(final WB_Coord a, final WB_Coord b, final WB_Coord c) {
		if (coincident2D(a, c)) {
			return true;
		} else if (coincident2D(b, c)) {
			return true;
		} else {
			if (getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if ((0 < d) && (d < 1)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean coincident2D(final WB_Coord a, final WB_Coord b) {
		if (getSqDistance2D(a, b) < WB_Epsilon.SQEPSILON) {
			return true;
		}
		return false;
	}


	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @param p
	 * @return
	 */
	public static double projectedDistanceNorm(final WB_Coord a, final WB_Coord b, final WB_Coord p) {
		double x1, x2, y1, y2;
		x1 = b.xd() - a.xd();
		x2 = p.xd() - a.xd();
		y1 = b.yd() - a.yd();
		y2 = p.yd() - a.yd();
		return ((x1 * x2) + (y1 * y2)) / ((x1 * x1) + (y1 * y1));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double pointAlongLine(final WB_Coord p, final WB_Line L) {
		final WB_Coord ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(p);
		ac.subSelf(L.getOrigin());
		return ac.dot(ab);
	}


	/**
	 *
	 *
	 * @param p
	 * @param tree
	 * @return
	 */
	public static boolean contains(final WB_Coord p, final WB_AABBTree tree) {
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (contains(p, current.getAABB())) {
				if (current.isLeaf()) {
					return true;
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}
		}
		return false;
	}


	/**
	 *
	 *
	 * @param p
	 * @param AABB
	 * @return
	 */
	public static boolean contains(final WB_Coord p, final WB_AABB AABB) {
		return (p.xd() >= AABB.getMinX()) && (p.yd() >= AABB.getMinY()) && (p.zd() >= AABB.getMinZ())
				&& (p.xd() < AABB.getMaxX()) && (p.yd() < AABB.getMaxY()) && (p.zd() < AABB.getMaxZ());
	}


	/**
	 *
	 *
	 * @param p1
	 * @param p2
	 * @param A
	 * @param B
	 * @return
	 */
	public static boolean sameSide(final WB_Coord p1, final WB_Coord p2, final WB_Coord A, final WB_Coord B) {
		final WB_Point t1 = new WB_Point(B).subSelf(A);
		WB_Point t2 = new WB_Point(p1).subSelf(A);
		WB_Point t3 = new WB_Point(p2).subSelf(A);
		t2 = t1.cross(t2);
		t3 = t1.cross(t3);
		final double t = t2.dot(t3);
		if (t >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}


	/**
	 *
	 *
	 * @param p
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static boolean contains(final WB_Coord p, final WB_Coord A, final WB_Coord B, final WB_Coord C) {
		if (WB_Epsilon.isZeroSq(getSqDistanceToLine3D(A, B, C))) {
			return false;
		}
		if (sameSide(p, A, B, C) && sameSide(p, B, A, C) && sameSide(p, C, A, B)) {
			return true;
		}
		return false;
	}


	/**
	 *
	 *
	 * @param p
	 * @param T
	 * @return
	 */
	public static boolean contains(final WB_Coord p, final WB_Triangle T) {
		return contains(p, T.p1(), T.p2(), T.p3());
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static WB_Point projectOnPlane(final WB_Coord p, final WB_Plane P) {
		final WB_Point projection = new WB_Point(p);
		final WB_Vector po = new WB_Vector(P.getOrigin(), p);
		final WB_Vector n = P.getNormal();
		return projection.subSelf(n.mulSelf(n.dot(po)));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double distanceToLine2D(final WB_Coord p, final WB_Line L) {
		return Math.sqrt(getSqDistanceToLine2D(p, L));
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getDistance2D(final WB_Coord p, final WB_Segment S) {
		return Math.sqrt(getSqDistance2D(p, S));
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getDistance2D(final WB_Coord p, final WB_Coord q) {
		return Math.sqrt(getSqDistance2D(p, q));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getDistance2D(final WB_Coord p, final WB_Line L) {
		return Math.sqrt(getSqDistance2D(p, L));
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getDistance2D(final WB_Coord p, final WB_Ray R) {
		return Math.sqrt(getSqDistance2D(p, R));
	}


	/**
	 *
	 *
	 * @param S
	 * @param T
	 * @return
	 */
	public static double getDistance3D(final WB_Segment S, final WB_Segment T) {
		return Math.sqrt(WB_GeometryOp.getIntersection3D(S, T).sqDist);
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Segment S) {
		return Math.sqrt(getSqDistance3D(p, S));
	}


	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Polygon poly) {
		return Math.sqrt(getSqDistance3D(p, poly));
	}


	/**
	 *
	 *
	 * @param p
	 * @param AABB
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_AABB AABB) {
		return Math.sqrt(getSqDistance3D(p, AABB));
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Coord q) {
		return Math.sqrt(getSqDistance3D(p, q));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Line L) {
		return Math.sqrt(getSqDistance3D(p, L));
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Plane P) {
		return P.getNormal().dot(p) - P.d();
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getDistance3D(final double p[], final WB_Plane P) {
		final WB_Vector n = P.getNormal();
		return ((n.xd() * p[0]) + (n.yd() * p[1]) + (n.zd() * p[2])) - P.d();
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getDistance3D(final WB_Coord p, final WB_Ray R) {
		return Math.sqrt(getSqDistance3D(p, R));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToLine2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToLine2D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToLine3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToLine3D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getDistanceToLine3D(final WB_Coord p, final WB_Line L) {
		return Math.sqrt(getSqDistanceToLine3D(p, L));
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getDistanceToPlane3D(final WB_Coord p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return (d < 0) ? -d : d;
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getDistanceToPlane3D(final double[] p, final WB_Plane P) {
		final WB_Vector v = P.getNormal();
		final double d = ((v.xd() * p[0]) + (v.yd() * p[1]) + (v.zd() * p[2])) - P.d();
		return (d < 0) ? -d : d;
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getDistanceToPoint2D(final WB_Coord p, final WB_Coord q) {
		return Math.sqrt(getSqDistanceToPoint2D(p, q));
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getDistanceToPoint3D(final WB_Coord p, final WB_Coord q) {
		return Math.sqrt(getSqDistanceToPoint3D(p, q));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToRay2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToRay2D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToRay3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToRay3D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getDistanceToRay3D(final WB_Coord p, final WB_Ray R) {
		return Math.sqrt(getSqDistanceToRay3D(p, R));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToSegment2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToSegment2D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDistanceToSegment3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		return Math.sqrt(getSqDistanceToSegment3D(p, a, b));
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getDistanceToSegment3D(final WB_Coord p, final WB_Segment S) {
		return Math.sqrt(getSqDistanceToSegment3D(p, S));
	}


	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getSqLength2D(final WB_Coord p) {
		return ((p.xd() * p.xd()) + (p.yd() * p.yd()));
	}


	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getSqLength3D(final WB_Coord p) {
		return ((p.xd() * p.xd()) + (p.yd() * p.yd()) + (p.zd() * p.zd()));
	}


	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getLength2D(final WB_Coord p) {
		return Math.sqrt((p.xd() * p.xd()) + (p.yd() * p.yd()));
	}


	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getLength3D(final WB_Coord p) {
		return Math.sqrt((p.xd() * p.xd()) + (p.yd() * p.yd()) + (p.zd() * p.zd()));
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getSqDistance2D(final WB_Coord p, final WB_Segment S) {
		final WB_Vector ab = new WB_Vector(S.getOrigin(), S.getEndpoint());
		final WB_Vector ac = new WB_Vector(p).sub(S.getOrigin());
		final WB_Vector bc = new WB_Vector(p).sub(S.getEndpoint());
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		if (e >= f) {
			return bc.dot2D(bc);
		}
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getSqDistance2D(final WB_Coord p, final WB_Coord q) {
		return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q.yd() - p.yd())));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getSqDistance2D(final WB_Coord p, final WB_Line L) {
		final WB_Coord ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot2D(ab);
		final double f = WB_Vector.dot2D(ab, ab);
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getSqDistance2D(final WB_Coord p, final WB_Ray R) {
		final WB_Coord ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = WB_Vector.dot2D(ab, ab);
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param S
	 * @param T
	 * @return
	 */
	public static double getSqDistance3D(final WB_Segment S, final WB_Segment T) {
		return WB_GeometryOp.getIntersection3D(S, T).sqDist;
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Segment S) {
		final WB_Vector ab = WB_Vector.subToVector3D(S.getEndpoint(), S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		final WB_Vector bc = new WB_Vector(S.getEndpoint(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param poly
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Polygon poly) {
		final int[] T = poly.getTriangles();
		final int n = T.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Coord tmp;

		for (int i = 0; i < n; i++) {

			tmp = WB_GeometryOp.getClosestPointToTriangle3D(p, poly.getPoint(T[i]), poly.getPoint(T[i + 1]),
					poly.getPoint(T[i + 2]));
			final double d2 = getDistance3D(tmp, p);
			if (d2 < dmax2) {
				dmax2 = d2;
				if (WB_Epsilon.isZeroSq(dmax2)) {
					break;
				}
			}
		}
		return dmax2;
	}


	/**
	 *
	 *
	 * @param p
	 * @param AABB
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_AABB AABB) {
		double sqDist = 0;
		double v = p.xd();
		if (v < AABB.getMinX()) {
			sqDist += (AABB.getMinX() - v) * (AABB.getMinX() - v);
		}
		if (v > AABB.getMaxX()) {
			sqDist += (v - AABB.getMaxX()) * (v - AABB.getMaxX());
		}
		v = p.yd();
		if (v < AABB.getMinY()) {
			sqDist += (AABB.getMinY() - v) * (AABB.getMinY() - v);
		}
		if (v > AABB.getMaxY()) {
			sqDist += (v - AABB.getMaxY()) * (v - AABB.getMaxY());
		}
		v = p.zd();
		if (v < AABB.getMinZ()) {
			sqDist += (AABB.getMinZ() - v) * (AABB.getMinZ() - v);
		}
		if (v > AABB.getMaxZ()) {
			sqDist += (v - AABB.getMaxZ()) * (v - AABB.getMaxZ());
		}
		return sqDist;
	}

	// POINT-POINT

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Coord q) {
		return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q.yd() - p.yd()))
				+ ((q.zd() - p.zd()) * (q.zd() - p.zd())));
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Line L) {
		final WB_Coord ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = WB_Vector.dot(ab, ab);
		return ac.dot(ac) - ((e * e) / f);
	}

	// POINT-PLANE

	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}


	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord p, final WB_Ray R) {
		final WB_Coord ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = WB_Vector.dot(ab, ab);
		return ac.dot(ac) - ((e * e) / f);
	}

	// POINT-SEGMENT

	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToLine2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot2D(ab);
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getSqDistanceToLine2D(final WB_Coord p, final WB_Line L) {
		final WB_Point ab = gf.createPoint(L.getDirection().xd(), L.getDirection().yd());
		final WB_Point ac = gf.createPoint(p.xd() - L.getOrigin().xd(), p.yd() - L.getOrigin().yd());
		final double e = ac.dot2D(ab);
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToLine3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static double getSqDistanceToLine3D(final WB_Coord p, final WB_Line L) {
		final WB_Coord ab = L.getDirection();
		final WB_Vector ac = gf.createVectorFromTo(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = WB_Vector.dot(ab, ab);
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double getSqDistanceToPlane3D(final WB_Coord p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getSqDistanceToPoint2D(final WB_Coord p, final WB_Coord q) {
		return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q.yd() - p.yd())));
	}

	// POINT-RAY

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double getSqDistanceToPoint3D(final WB_Coord p, final WB_Coord q) {
		return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q.yd() - p.yd()))
				+ ((q.zd() - p.zd()) * (q.zd() - p.zd())));
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToRay2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToRay3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = gf.createVectorFromTo(a, b);
		final WB_Vector ac = gf.createVectorFromTo(a, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - ((e * e) / f);
	}

	// POINT-AABB

	/**
	 *
	 *
	 * @param p
	 * @param R
	 * @return
	 */
	public static double getSqDistanceToRay3D(final WB_Coord p, final WB_Ray R) {
		final WB_Coord ab = R.getDirection();
		final WB_Vector ac = gf.createVectorFromTo(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = WB_Vector.dot(ab, ab);
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToSegment2D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final WB_Vector bc = new WB_Vector(b, p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		if (e >= f) {
			return bc.dot2D(bc);
		}
		return ac.dot2D(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getSqDistanceToSegment3D(final WB_Coord p, final WB_Coord a, final WB_Coord b) {
		final WB_Vector ab = gf.createVectorFromTo(a, b);
		final WB_Vector ac = gf.createVectorFromTo(a, p);
		final WB_Vector bc = gf.createVectorFromTo(b, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param S
	 * @return
	 */
	public static double getSqDistanceToSegment3D(final WB_Coord p, final WB_Segment S) {
		final WB_Point ab = gf.createPoint(S.getEndpoint()).sub(S.getOrigin());
		final WB_Point ac = gf.createPoint(p).sub(S.getOrigin());
		final WB_Point bc = gf.createPoint(p).sub(S.getEndpoint());
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - ((e * e) / f);
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static double signedDistanceToPlane3D(final WB_Coord p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d;
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double angleBetween(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz) {
		final WB_Vector v0 = new WB_Vector(ux, uy, uz);
		final WB_Vector v1 = new WB_Vector(vx, vy, vz);
		v0.normalizeSelf();
		v1.normalizeSelf();
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return Math.acos(d);
	}


	/**
	 *
	 *
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @return
	 */
	public static double angleBetween(final double cx, final double cy, final double cz, final double px,
			final double py, final double pz, final double qx, final double qy, final double qz) {
		final WB_Vector v0 = new WB_Vector(px - cx, py - cy, pz - cz);
		final WB_Vector v1 = new WB_Vector(qx - cx, qy - cy, qz - cz);
		v0.normalizeSelf();
		v1.normalizeSelf();
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return Math.acos(d);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double angleBetweenNorm(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz) {
		final WB_Vector v0 = new WB_Vector(ux, uy, uz);
		final WB_Vector v1 = new WB_Vector(vx, vy, vz);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return Math.acos(d);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public static int calculateHashCode(final double x, final double y) {
		int result = 17;
		final long a = Double.doubleToLongBits(x);
		result += (31 * result) + (int) (a ^ (a >>> 32));
		final long b = Double.doubleToLongBits(y);
		result += (31 * result) + (int) (b ^ (b >>> 32));
		return result;
	}


	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int calculateHashCode(final double x, final double y, final double z) {
		int result = 17;
		final long a = Double.doubleToLongBits(x);
		result += (31 * result) + (int) (a ^ (a >>> 32));
		final long b = Double.doubleToLongBits(y);
		result += (31 * result) + (int) (b ^ (b >>> 32));
		final long c = Double.doubleToLongBits(z);
		result += (31 * result) + (int) (c ^ (c >>> 32));
		return result;
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public static int calculateHashCode(final double x, final double y, final double z, final double w) {
		int result = 17;
		final long a = Double.doubleToLongBits(x);
		result += (31 * result) + (int) (a ^ (a >>> 32));
		final long b = Double.doubleToLongBits(y);
		result += (31 * result) + (int) (b ^ (b >>> 32));
		final long c = Double.doubleToLongBits(z);
		result += (31 * result) + (int) (c ^ (c >>> 32));
		final long d = Double.doubleToLongBits(w);
		result += (31 * result) + (int) (d ^ (d >>> 32));
		return result;
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double cosAngleBetween(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz) {
		final WB_Vector v0 = new WB_Vector(ux, uy, uz);
		final WB_Vector v1 = new WB_Vector(vx, vy, vz);
		v0.normalizeSelf();
		v1.normalizeSelf();
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return d;
	}


	/**
	 *
	 *
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @return
	 */
	public static double cosAngleBetween(final double cx, final double cy, final double cz, final double px,
			final double py, final double pz, final double qx, final double qy, final double qz) {
		final WB_Vector v0 = new WB_Vector(px - cx, py - cy, pz - cz);
		final WB_Vector v1 = new WB_Vector(qx - cx, qy - cy, qz - cz);
		v0.normalizeSelf();
		v1.normalizeSelf();
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return d;
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double cosAngleBetweenNorm(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz) {
		final WB_Vector v0 = new WB_Vector(ux, uy, uz);
		final WB_Vector v1 = new WB_Vector(vx, vy, vz);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return d;
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double[] cross(final double ux, final double uy, final double uz, final double vx, final double vy,
			final double vz) {
		return new double[] { (uy * vz) - (uz * vy), (uz * vx) - (ux * vz), (ux * vy) - (uy * vx) };
	}


	/**
	 *
	 *
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @return
	 */
	public static double[] cross(final double cx, final double cy, final double cz, final double px, final double py,
			final double pz, final double qx, final double qy, final double qz) {
		return cross(px - cx, py - cy, pz - cz, qx - cx, qy - cy, qz - cz);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double dot(final double ux, final double uy, final double uz, final double vx, final double vy,
			final double vz) {
		final double k0 = ux * vx;
		final double k1 = uy * vy;
		final double k2 = uz * vz;
		final double exp0 = WB_Math.getExp(k0);
		final double exp1 = WB_Math.getExp(k1);
		final double exp2 = WB_Math.getExp(k2);
		if (exp0 < exp1) {
			if (exp0 < exp2) {
				return (k1 + k2) + k0;
			} else {
				return (k0 + k1) + k2;
			}
		} else {
			if (exp1 < exp2) {
				return (k0 + k2) + k1;
			} else {
				return (k0 + k1) + k2;
			}
		}
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param vx
	 * @param vy
	 * @return
	 */
	public static double dot2D(final double ux, final double uy, final double vx, final double vy) {
		return (ux * vx) + (uy * vy);
	}

	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param uw
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param vw
	 * @return
	 */
	public static double dot4D(final double ux, final double uy, final double uz, final double uw, final double vx,
			final double vy, final double vz, final double vw) {
		return (ux * vx) + (uy * vy) + (uz * vz) + (uw * vw);
	}


	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param qx
	 * @param qy
	 * @return
	 */
	public static double getDistance2D(final double px, final double py, final double qx, final double qy) {
		return Math.sqrt(((qx - px) * (qx - px)) + ((qy - py) * (qy - py)));
	}


	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @return
	 */
	public static double getDistance3D(final double px, final double py, final double pz, final double qx,
			final double qy, final double qz) {
		return Math.sqrt(((qx - px) * (qx - px)) + ((qy - py) * (qy - py)) + ((qz - pz) * (qz - pz)));
	}

	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param pw
	 * @param qx
	 * @param qy
	 * @param qz
	 * @param qw
	 * @return
	 */
	public static double getDistance4D(final double px, final double py, final double pz, final double pw,
			final double qx, final double qy, final double qz, final double qw) {
		return Math.sqrt(
				((qx - px) * (qx - px)) + ((qy - py) * (qy - py)) + ((qz - pz) * (qz - pz)) + ((qw - pw) * (qw - pw)));
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @return
	 */
	public static double getLength2D(final double ux, final double uy) {
		return Math.sqrt((ux * ux) + (uy * uy));
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @return
	 */
	public static double getLength3D(final double ux, final double uy, final double uz) {
		return Math.sqrt((ux * ux) + (uy * uy) + (uz * uz));
	}

	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param uw
	 * @return
	 */
	public static double getLength4D(final double ux, final double uy, final double uz, final double uw) {
		return Math.sqrt((ux * ux) + (uy * uy) + (uz * uz) + (uw * uw));
	}


	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param qx
	 * @param qy
	 * @return
	 */
	public static double getSqDistance2D(final double px, final double py, final double qx, final double qy) {
		return ((qx - px) * (qx - px)) + ((qy - py) * (qy - py));
	}


	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @return
	 */
	public static double getSqDistance3D(final double px, final double py, final double pz, final double qx,
			final double qy, final double qz) {
		return ((qx - px) * (qx - px)) + ((qy - py) * (qy - py)) + ((qz - pz) * (qz - pz));
	}

	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param pw
	 * @param qx
	 * @param qy
	 * @param qz
	 * @param qw
	 * @return
	 */
	public static double getSqDistance4D(final double px, final double py, final double pz, final double pw,
			final double qx, final double qy, final double qz, final double qw) {
		return ((qx - px) * (qx - px)) + ((qy - py) * (qy - py)) + ((qz - pz) * (qz - pz)) + ((qw - pw) * (qw - pw));
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @return
	 */
	public static double getSqLength2D(final double ux, final double uy) {
		return (ux * ux) + (uy * uy);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @return
	 */
	public static double getSqLength3D(final double ux, final double uy, final double uz) {
		return (ux * ux) + (uy * uy) + (uz * uz);
	}

	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param uw
	 * @return
	 */
	public static double getSqLength4D(final double ux, final double uy, final double uz, final double uw) {
		return (ux * ux) + (uy * uy) + (uz * uz) + (uw * uw);
	}

	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param qx
	 * @param qy
	 * @param t
	 * @return
	 */
	public static double[] interpolate(final double px, final double py, final double qx, final double qy,
			final double t) {
		return new double[] { px + (t * (qx - px)), py + (t * (qy - py)) };
	}


	/**
	 *
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param qx
	 * @param qy
	 * @param qz
	 * @param t
	 * @return
	 */
	public static double[] interpolate(final double px, final double py, final double pz, final double qx,
			final double qy, final double qz, final double t) {
		return new double[] { px + (t * (qx - px)), py + (t * (qy - py)), pz + (t * (qz - pz)) };
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @return
	 */
	public static boolean isZero2D(final double ux, final double uy, final double uz) {
		return (getSqLength2D(ux, uy) < WB_Epsilon.SQEPSILON);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @return
	 */
	public static boolean isZero3D(final double ux, final double uy, final double uz) {
		return (getSqLength3D(ux, uy, uz) < WB_Epsilon.SQEPSILON);
	}

	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param uw
	 * @return
	 */
	public static boolean isZero4D(final double ux, final double uy, final double uz, final double uw) {
		return (getSqLength4D(ux, uy, uz, uw) < WB_Epsilon.SQEPSILON);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param wx
	 * @param wy
	 * @param wz
	 * @return
	 */
	public static double scalarTriple(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz, final double wx, final double wy, final double wz) {
		final double[] c = cross(vx, vy, vz, wx, wy, wz);
		return dot(ux, uy, uz, c[0], c[1], c[2]);
	}


	/**
	 *
	 *
	 * @param ux
	 * @param uy
	 * @param uz
	 * @param vx
	 * @param vy
	 * @param vz
	 * @return
	 */
	public static double[][] tensor3D(final double ux, final double uy, final double uz, final double vx,
			final double vy, final double vz) {
		return new double[][] { { ux * vx, ux * vy, ux * vz }, { uy * vx, uy * vy, uy * vz },
			{ uz * vx, uz * vy, uz * vz } };
	}


	/**
	 *
	 *
	 * @param points
	 * @param dir
	 * @return
	 */
	public static int[] getExtremePointsAlongDirection(final WB_Coord[] points, final WB_Coord dir) {
		final int[] result = new int[] { -1, -1 };
		double minproj = Double.POSITIVE_INFINITY;
		double maxproj = Double.NEGATIVE_INFINITY;
		double proj;
		for (int i = 0; i < points.length; i++) {
			proj = WB_Vector.dot(points[i], dir);
			if (proj < minproj) {
				minproj = proj;
				result[0] = i;
			}
			if (proj > maxproj) {
				maxproj = proj;
				result[1] = i;
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @param points
	 * @param dir
	 * @return
	 */
	public static int[] getExtremePointsAlongDirection(final Collection<? extends WB_Coord> points,
			final WB_Coord dir) {
		final int[] result = new int[] { -1, -1 };
		double minproj = Double.POSITIVE_INFINITY;
		double maxproj = Double.NEGATIVE_INFINITY;
		double proj;
		int i = 0;
		for (WB_Coord point : points) {
			proj = WB_Vector.dot(point, dir);
			if (proj < minproj) {
				minproj = proj;
				result[0] = i;
			}
			if (proj > maxproj) {
				maxproj = proj;
				result[1] = i;
			}
			i++;
		}
		return result;
	}


	/**
	 *
	 *
	 * @param p
	 * @param L
	 * @return
	 */
	public static WB_Classification classifyPointToLine2D(final WB_Coord p, final WB_Line L) {
		final double dist = ((-L.getDirection().yd() * p.xd()) + (L.getDirection().xd() * p.yd())
				+ (L.getOrigin().xd() * L.getDirection().yd())) - (L.getOrigin().yd() * L.getDirection().xd());
		if (dist > WB_Epsilon.EPSILON) {
			return WB_Classification.FRONT;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}


	/**
	 *
	 *
	 * @param p
	 * @param C
	 * @return
	 */
	public static WB_Classification classifyPointToCircle2D(final WB_Coord p, final WB_Circle C) {
		final double dist = WB_GeometryOp.getDistanceToPoint2D(p, C.getCenter());
		if (WB_Epsilon.isZero(dist - C.getRadius())) {
			return WB_Classification.ON;
		} else if (dist < C.getRadius()) {
			return WB_Classification.INSIDE;
		} else {
			return WB_Classification.OUTSIDE;
		}
	}


	/**
	 *
	 *
	 * @param C1
	 * @param C2
	 * @return
	 */
	public static WB_Classification classifyCircleToCircle2D(final WB_Circle C1, final WB_Circle C2) {
		if (C1.equals(C2)) {
			return WB_Classification.ON;
		}
		final double dist = WB_GeometryOp.getDistanceToPoint2D(C1.getCenter(), C2.getCenter());
		final double rsum = C1.getRadius() + C2.getRadius();
		final double rdiff = Math.abs(C1.getRadius() - C2.getRadius());
		if (dist >= rsum) {
			return WB_Classification.OUTSIDE;
		} else if (dist <= rdiff) {
			if (C1.getRadius() < C2.getRadius()) {
				return WB_Classification.INSIDE;
			} else {
				return WB_Classification.CONTAINING;
			}
		}
		return WB_Classification.CROSSING;
	}


	/**
	 *
	 *
	 * @param C
	 * @param L
	 * @return
	 */
	public static WB_Classification classifyCircleToLine2D(final WB_Circle C, final WB_Line L) {
		final double d = WB_GeometryOp.distanceToLine2D(C.getCenter(), L);
		if (WB_Epsilon.isZero(d - C.getRadius())) {
			return WB_Classification.TANGENT;
		} else if (d < C.getRadius()) {
			return WB_Classification.CROSSING;
		}
		return WB_Classification.OUTSIDE;
	}


	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param L
	 * @return
	 */
	public static WB_Classification sameSideOfLine2D(final WB_Coord p, final WB_Coord q, final WB_Line L) {
		final WB_Predicates pred = new WB_Predicates();
		final WB_Point pL = L.getPointOnLine(1.0);
		final double pside = Math.signum(pred.orientTri(L.getOrigin(), pL, p));
		final double qside = Math.signum(pred.orientTri(L.getOrigin(), pL, q));
		if ((pside == 0) || (qside == 0) || (pside == qside)) {
			return WB_Classification.SAME;
		}
		return WB_Classification.DIFF;
	}


	/**
	 *
	 *
	 * @param seg
	 * @param L
	 * @return
	 */
	public static WB_Classification classifySegmentToLine2D(final WB_Segment seg, final WB_Line L) {
		final WB_Classification a = classifyPointToLine2D(seg.getOrigin(), L);
		final WB_Classification b = classifyPointToLine2D(seg.getEndpoint(), L);
		if (a == WB_Classification.ON) {
			if (b == WB_Classification.ON) {
				return WB_Classification.ON;
			} else if (b == WB_Classification.FRONT) {
				return WB_Classification.FRONT;
			} else {
				return WB_Classification.BACK;
			}
		}
		if (b == WB_Classification.ON) {
			if (a == WB_Classification.FRONT) {
				return WB_Classification.FRONT;
			} else {
				return WB_Classification.BACK;
			}
		}
		if ((a == WB_Classification.FRONT) && (b == WB_Classification.BACK)) {
			return WB_Classification.CROSSING;
		}
		if ((a == WB_Classification.BACK) && (b == WB_Classification.FRONT)) {
			return WB_Classification.CROSSING;
		}
		if (a == WB_Classification.FRONT) {
			return WB_Classification.FRONT;
		}
		return WB_Classification.BACK;
	}


	/**
	 *
	 *
	 * @param P
	 * @param L
	 * @return
	 */
	public static WB_Classification classifyPolygonToLine2D(final WB_Polygon P, final WB_Line L) {
		int numFront = 0;
		int numBack = 0;
		for (int i = 0; i < P.getNumberOfPoints(); i++) {
			if (classifyPointToLine2D(P.getPoint(i), L) == WB_Classification.FRONT) {
				numFront++;
			} else if (classifyPointToLine2D(P.getPoint(i), L) == WB_Classification.BACK) {
				numBack++;
			}
			if ((numFront > 0) && (numBack > 0)) {
				return WB_Classification.CROSSING;
			}
		}
		if (numFront > 0) {
			return WB_Classification.FRONT;
		}
		if (numBack > 0) {
			return WB_Classification.BACK;
		}
		return null;
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static WB_Classification classifyPointToPlaneFast3D(final WB_Coord p, final WB_Plane P) {
		return classifyPointToPlaneFast3D(P, p);
	}


	/**
	 *
	 *
	 * @param p
	 * @param P
	 * @return
	 */
	public static WB_Classification classifyPointToPlane3D(final WB_Coord p, final WB_Plane P) {
		return classifyPointToPlane3D(P, p);
	}


	/**
	 *
	 *
	 * @param P
	 * @param p
	 * @return
	 */
	public static WB_Classification classifyPointToPlaneFast3D(final WB_Plane P, final WB_Coord p) {
		final double signp = WB_GeometryOp.signedDistanceToPlane3D(p, P);
		if (WB_Epsilon.isZero(signp)) {
			return WB_Classification.ON;
		}
		if (signp > 0) {
			return WB_Classification.FRONT;
		}
		return WB_Classification.BACK;
	}


	/**
	 *
	 *
	 * @param P
	 * @param p
	 * @return
	 */
	public static WB_Classification classifyPointToPlane3D(final WB_Plane P, final WB_Coord p) {
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getDistanceToPlane3D(p, P))) {
			return WB_Classification.ON;
		}
		final WB_Predicates predicates = new WB_Predicates();
		final double signp = predicates.orientTetra(P.getOrigin(), P.getOrigin().addMul(100, P.getU()),
				P.getOrigin().addMul(100, P.getV()), p);
		if (signp == 0) {
			return WB_Classification.ON;
		}
		if (signp < 0) {
			return WB_Classification.FRONT;
		}
		return WB_Classification.BACK;
	}


	/**
	 *
	 *
	 * @param T
	 * @param p
	 * @return
	 */
	public static WB_Classification classifyPointToTetrahedron3D(final WB_Tetrahedron T, final WB_Coord p) {
		final WB_Plane pl012 = gf.createPlane(T.p1(), T.p2(), T.p3());
		final WB_Plane pl013 = gf.createPlane(T.p1(), T.p2(), T.p4());
		final WB_Plane pl023 = gf.createPlane(T.p1(), T.p3(), T.p4());
		final WB_Plane pl123 = gf.createPlane(T.p2(), T.p3(), T.p4());
		int on = 0;
		int front = 0;
		int back = 0;
		final WB_Classification c012 = classifyPointToPlane3D(pl012, p);
		if (c012 == WB_Classification.ON) {
			on++;
		} else if (c012 == WB_Classification.FRONT) {
			front++;
		} else {
			back++;
		}
		final WB_Classification c013 = classifyPointToPlane3D(pl013, p);
		if (c013 == WB_Classification.ON) {
			on++;
		} else if (c013 == WB_Classification.FRONT) {
			front++;
		} else {
			back++;
		}
		final WB_Classification c023 = classifyPointToPlane3D(pl023, p);
		if (c023 == WB_Classification.ON) {
			on++;
		} else if (c023 == WB_Classification.FRONT) {
			front++;
		} else {
			back++;
		}
		final WB_Classification c123 = classifyPointToPlane3D(pl123, p);
		if (c123 == WB_Classification.ON) {
			on++;
		} else if (c123 == WB_Classification.FRONT) {
			front++;
		} else {
			back++;
		}
		if ((front == 4) || (back == 4)) {
			return WB_Classification.INSIDE;
		}
		if (((front + on) == 4) || ((back + on) == 4)) {
			return WB_Classification.ON;
		}
		return WB_Classification.OUTSIDE;
	}


	/**
	 *
	 *
	 * @param poly
	 * @param P
	 * @return
	 */
	public static WB_Classification classifyPolygonToPlane3D(final WB_Polygon poly, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;
		for (int i = 0; i < poly.getNumberOfPoints(); i++) {
			switch (classifyPointToPlane3D(P, poly.getPoint(i))) {
			case FRONT:
				numInFront++;
				break;
			case BACK:
				numBehind++;
				break;
			default:
			}
			if ((numBehind != 0) && (numInFront != 0)) {
				return WB_Classification.CROSSING;
			}
		}
		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	/**
	 *
	 *
	 * @param segment
	 * @param P
	 * @return
	 */
	public static WB_Classification classifySegmentToPlane3D(final WB_Segment segment, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;

		switch (classifyPointToPlane3D(segment.getOrigin(),P)) {
		case FRONT:
			numInFront++;
			break;
		case BACK:
			numBehind++;
			break;
		default:
		}
		switch (classifyPointToPlane3D(segment.getEndpoint(),P)) {
		case FRONT:
			numInFront++;
			break;
		case BACK:
			numBehind++;
			break;
		default:
		}


		if ((numBehind != 0) && (numInFront != 0)) {
			return WB_Classification.CROSSING;
		}

		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}


	/**
	 *
	 *
	 * @param poly
	 * @param P
	 * @return
	 */
	public static WB_Classification classifyPolygonToPlaneFast3D(final WB_Polygon poly, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;
		double d;
		for (int i = 0; i < poly.getNumberOfPoints(); i++) {
			d = WB_GeometryOp.signedDistanceToPlane3D(poly.getPoint(i), P);
			if (d > WB_Epsilon.EPSILON) {
				numInFront++;
			} else if (d < -WB_Epsilon.EPSILON) {
				numBehind++;
			}
			if ((numBehind != 0) && (numInFront != 0)) {
				return WB_Classification.CROSSING;
			}
		}
		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}


	/**
	 *
	 *
	 * @param v0
	 * @param v1
	 * @return
	 */
	public static double cotan(final WB_Coord v0, final WB_Coord v1){
		return(WB_Vector.dot(v0,v1)/WB_Vector.cross(v0, v1).getLength3D());


	}

	/**
	 *
	 *
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double cotan(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2){
		WB_Vector v0=WB_Vector.subToVector3D(p0, p1);
		WB_Vector v1=WB_Vector.subToVector3D(p0, p2);
		return(WB_Vector.dot(v0,v1)/WB_Vector.cross(v0, v1).getLength3D());


	}


	public static boolean isParallel(final WB_Coord v0, final WB_Coord v1){
		return Math.abs(1.0-(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())/(getLength3D(v0)*getLength3D(v1))))<WB_Epsilon.EPSILON;
	}

	public static boolean isParallel2D(final WB_Coord v0, final WB_Coord v1){
		return Math.abs(1.0-(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())/(getLength2D(v0)*getLength2D(v1))))<WB_Epsilon.EPSILON;
	}

	public static boolean isOrthogonal(final WB_Coord v0, final WB_Coord v1){
		return WB_Epsilon.isZero(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())/(getLength3D(v0)*getLength3D(v1)));
	}

	public static boolean isOrthogonal2D(final WB_Coord v0, final WB_Coord v1){
		return WB_Epsilon.isZero(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())/(getLength2D(v0)*getLength2D(v1)));
	}

	public static boolean isParallel(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(1.0-(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())/(getLength3D(v0)*getLength3D(v1))))<epsilon;
	}

	public static boolean isParallel2D(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(1.0-(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())/(getLength2D(v0)*getLength2D(v1))))<epsilon;
	}

	public static boolean isOrthogonal(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())/(getLength3D(v0)*getLength3D(v1)))<epsilon;
	}

	public static boolean isOrthogonal2D(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())/(getLength2D(v0)*getLength2D(v1)))<epsilon;
	}


	public static boolean isParallelNorm(final WB_Coord v0, final WB_Coord v1){
		return Math.abs(1.0-(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())))<WB_Epsilon.EPSILON;
	}

	public static boolean isParallelNorm2D(final WB_Coord v0, final WB_Coord v1){
		return Math.abs(1.0-(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())))<WB_Epsilon.EPSILON;
	}

	public static boolean isOrthogonalNorm(final WB_Coord v0, final WB_Coord v1){
		return WB_Epsilon.isZero(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd()));
	}

	public static boolean isOrthogonalNorm2D(final WB_Coord v0, final WB_Coord v1){
		return WB_Epsilon.isZero(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd()));
	}

	public static boolean isParallelNorm(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(1.0-(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd())))<epsilon;
	}

	public static boolean isParallelNorm2D(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(1.0-(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd())))<epsilon;
	}

	public static boolean isOrthogonalNorm(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(dot(v0.xd(),v0.yd(),v0.zd(),v1.xd(),v1.yd(),v1.zd()))<epsilon;
	}

	public static boolean isOrthogonalNorm2D(final WB_Coord v0, final WB_Coord v1, final double epsilon){
		return Math.abs(dot2D(v0.xd(),v0.yd(),v1.xd(),v1.yd()))<epsilon;
	}
}
