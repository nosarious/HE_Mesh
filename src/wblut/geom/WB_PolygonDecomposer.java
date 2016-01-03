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

import java.util.List;

import javolution.util.FastTable;
import wblut.math.WB_Epsilon;

class WB_PolygonDecomposer {
	static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	/**
	 * 
	 *
	 * @param poly 
	 * @return 
	 */
	public static List<WB_Polygon> convexDecomposePolygon(WB_Polygon poly) {
		if(!poly.isSimple()) {
			poly=gf.createSimplePolygon(poly);
		}
		List<WB_Polygon> polys = new FastTable<WB_Polygon>();
		if (poly == null) {
			return polys;
		}
		int size = poly.getNumberOfShellPoints();
		if (size < 4) {
			polys.add(poly);
			return polys;
		}
		decomposePolygon(poly.getPoints(), polys);
		return polys;
	}

	/**
	 * 
	 *
	 * @param polygon 
	 * @param polygons 
	 */
	private static void decomposePolygon(final List<WB_Point> polygon, final List<WB_Polygon> polygons) {
		int size = polygon.size();
		WB_Point upperIntersection = new WB_Point();
		WB_Point lowerIntersection = new WB_Point();
		double upperDistance = Double.MAX_VALUE;
		double lowerDistance = Double.MAX_VALUE;
		double closestDistance = Double.MAX_VALUE;
		int upperIndex = 0;
		int lowerIndex = 0;
		int closestIndex = 0;

		List<WB_Point> lower = new FastTable<WB_Point>();
		List<WB_Point> upper = new FastTable<WB_Point>();

		for (int i = 0; i < size; i++) {
			WB_Point p = polygon.get(i);
			WB_Point p0 = polygon.get((i - 1) < 0 ? size - 1 : i - 1);
			WB_Point p1 = polygon.get((i + 1) == size ? 0 : i + 1);
			if (isReflex(p0, p, p1)) {
				for (int j = 0; j < size; j++) {
					WB_Point q = polygon.get(j);
					WB_Point q0 = polygon.get((j - 1) < 0 ? size - 1 : j - 1);
					WB_Point q1 = polygon.get((j + 1) == size ? 0 : j + 1);
					WB_Point s = new WB_Point();
					if (left(p0, p, q) && rightOn(p0, p, q0)) {
						if (getIntersection(p0, p, q, q0, s)) {
							if (right(p1, p, s)) {
								double dist = p.getSqDistance2D(s);
								if (dist < lowerDistance) {
									lowerDistance = dist;
									lowerIntersection.set(s);
									lowerIndex = j;
								}
							}
						}
					}
					if (left(p1, p, q1) && rightOn(p1, p, q)) {
						if (getIntersection(p1, p, q, q1, s)) {
							if (left(p0, p, s)) {
								double dist = p.getSqDistance2D(s);
								if (dist < upperDistance) {
									upperDistance = dist;
									upperIntersection.set(s);
									upperIndex = j;
								}
							}
						}
					}
				}
				if (lowerIndex == ((upperIndex + 1) % size)) {
					WB_Point s = upperIntersection.add(lowerIntersection).mulSelf(0.5);
					if (i < upperIndex) {
						lower.addAll(polygon.subList(i, upperIndex + 1));
						lower.add(s);
						upper.add(s);
						if (lowerIndex != 0) {
							upper.addAll(polygon.subList(lowerIndex, size));
						}
						upper.addAll(polygon.subList(0, i + 1));
					} else {
						if (i != 0) {
							lower.addAll(polygon.subList(i, size));
						}
						lower.addAll(polygon.subList(0, upperIndex + 1));
						lower.add(s);
						upper.add(s);
						upper.addAll(polygon.subList(lowerIndex, i + 1));
					}
				} else {
					if (lowerIndex > upperIndex) {
						upperIndex += size;
					}
					closestIndex = lowerIndex;
					for (int j = lowerIndex; j <= upperIndex; j++) {
						int jmod = j % size;
						WB_Point q = polygon.get(jmod);

						if ((q == p) || (q == p0) || (q == p1)) {
							continue;
						}
						if (isVisible(polygon, i, jmod)) {
							double dist = p.getSqDistance2D(q);
							if (dist < closestDistance) {
								closestDistance = dist;
								closestIndex = jmod;
							}
						}
					}
					if (i < closestIndex) {
						lower.addAll(polygon.subList(i, closestIndex + 1));
						if (closestIndex != 0) {
							upper.addAll(polygon.subList(closestIndex, size));
						}
						upper.addAll(polygon.subList(0, i + 1));
					} else {
						if (i != 0) {
							lower.addAll(polygon.subList(i, size));
						}
						lower.addAll(polygon.subList(0, closestIndex + 1));
						upper.addAll(polygon.subList(closestIndex, i + 1));
					}
				}
				if (lower.size() < upper.size()) {
					decomposePolygon(lower, polygons);
					decomposePolygon(upper, polygons);
				} else {
					decomposePolygon(upper, polygons);
					decomposePolygon(lower, polygons);
				}
				return;
			}
		}
		if (polygon.size() < 3) {
			return;
		}
		polygons.add(gf.createSimplePolygon(polygon));
	}

	/**
	 * 
	 *
	 * @param p0 
	 * @param p 
	 * @param p1 
	 * @return 
	 */
	private static boolean isReflex(final WB_Point p0, final WB_Point p, final WB_Point p1) {
		return right(p1, p0, p);
	}

	/**
	 * 
	 *
	 * @param a 
	 * @param b 
	 * @param p 
	 * @return 
	 */
	private static boolean left(final WB_Point a, final WB_Point b, final WB_Point p) {
		return getRelativeOrientation(p, a, b) > 0;
	}

	/**
	 * 
	 *
	 * @param a 
	 * @param b 
	 * @param p 
	 * @return 
	 */
	private static boolean leftOn(final WB_Point a, final WB_Point b, final WB_Point p) {
		return getRelativeOrientation(p, a, b) >= 0;
	}

	/**
	 * 
	 *
	 * @param a 
	 * @param b 
	 * @param p 
	 * @return 
	 */
	private static boolean right(final WB_Point a, final WB_Point b, final WB_Point p) {
		return getRelativeOrientation(p, a, b) < 0;
	}

	/**
	 * 
	 *
	 * @param a 
	 * @param b 
	 * @param p 
	 * @return 
	 */
	private static boolean rightOn(final WB_Point a, final WB_Point b, final WB_Point p) {
		return getRelativeOrientation(p, a, b) <= 0;
	}

	/**
	 * 
	 *
	 * @param a1 
	 * @param a2 
	 * @param b1 
	 * @param b2 
	 * @param p 
	 * @return 
	 */
	private static boolean getIntersection(final WB_Point a1, final WB_Point a2, final WB_Point b1, final WB_Point b2, final WB_Point p) {
		WB_Point s1 = a1.sub(a2);
		WB_Point s2 = b1.sub(b2);
		double det = cross2D(s1, s2);
		if (Math.abs(det) <= WB_Epsilon.EPSILON) {
			return false;
		} else {
			det = 1.0 / det;
			double t2 = det * (cross2D(a1, s1) - cross2D(b1, s1));
			p.set((b1.xd() * (1.0 - t2)) + (b2.xd() * t2), (b1.yd() * (1.0 - t2)) + (b2.yd() * t2));
			return true;
		}
	}

	/**
	 * 
	 *
	 * @param polygon 
	 * @param i 
	 * @param j 
	 * @return 
	 */
	private static boolean isVisible(final List<WB_Point> polygon, final int i, final int j) {
		int s = polygon.size();
		WB_Point iv0, iv, iv1;
		WB_Point jv0, jv, jv1;
		iv0 = polygon.get(i == 0 ? s - 1 : i - 1);
		iv = polygon.get(i);
		iv1 = polygon.get((i + 1) == s ? 0 : i + 1);

		jv0 = polygon.get(j == 0 ? s - 1 : j - 1);
		jv = polygon.get(j);
		jv1 = polygon.get((j + 1) == s ? 0 : j + 1);

		if (isReflex(iv0, iv, iv1)) {
			if (leftOn(iv, iv0, jv) && rightOn(iv, iv1, jv)) {
				return false;
			}
		} else {
			if (rightOn(iv, iv1, jv) || leftOn(iv, iv0, jv)) {
				return false;
			}
		}
		if (isReflex(jv0, jv, jv1)) {
			if (leftOn(jv, jv0, iv) && rightOn(jv, jv1, iv)) {
				return false;
			}
		} else {
			if (rightOn(jv, jv1, iv) || leftOn(jv, jv0, iv)) {
				return false;
			}
		}
		for (int k = 0; k < s; k++) {
			int ki1 = (k + 1) == s ? 0 : k + 1;
			if ((k == i) || (k == j) || (ki1 == i) || (ki1 == j)) {
				continue;
			}
			WB_Point k1 = polygon.get(k);
			WB_Point k2 = polygon.get(ki1);

			WB_Point in = getSegmentIntersection(iv, jv, k1, k2);
			if (in != null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 *
	 * @param point 
	 * @param linePoint1 
	 * @param linePoint2 
	 * @return 
	 */
	private static double getRelativeOrientation(final WB_Coord point, final WB_Coord linePoint1, final WB_Coord linePoint2) {
		return ((linePoint2.xd() - linePoint1.xd()) * (point.yd() - linePoint1.yd()))
				- ((point.xd() - linePoint1.xd()) * (linePoint2.yd() - linePoint1.yd()));
	}

	/**
	 * 
	 *
	 * @param v1 
	 * @param v2 
	 * @return 
	 */
	private static double cross2D(final WB_Coord v1, final WB_Coord v2) {
		return (v1.xd() * v2.yd()) - (v1.yd() * v2.xd());
	}

	/**
	 * 
	 *
	 * @param ap1 
	 * @param ap2 
	 * @param bp1 
	 * @param bp2 
	 * @return 
	 */
	private static WB_Point getSegmentIntersection(final WB_Coord ap1, final WB_Coord ap2, final WB_Coord bp1, final WB_Coord bp2) {
		WB_Coord A = WB_Point.sub(ap2, ap1);
		WB_Coord B = WB_Point.sub(bp2, bp1);
		double BxA = cross2D(B, A);
		if (Math.abs(BxA) <= WB_Epsilon.EPSILON) {
			return null;
		}

		double ambxA = cross2D(WB_Point.sub(ap1, bp1), A);
		if (Math.abs(ambxA) <= WB_Epsilon.EPSILON) {
			return null;
		}

		double tb = ambxA / BxA;
		if ((tb < 0.0) || (tb > 1.0)) {
			return null;
		}

		WB_Point ip = WB_Point.mul(B, tb).addSelf(bp1);
		double ta = WB_Point.sub(ip, ap1).dot(A) / WB_Point.dot(A, A);
		if ((ta < 0.0) || (ta > 1.0)) {
			return null;
		}
		return ip;
	}

}
