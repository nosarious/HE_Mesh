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

public class WB_PolygonDecomposer {
	static WB_GeometryFactory gf = new WB_GeometryFactory();

	/**
	 *
	 *
	 * @param poly
	 * @return
	 */
	public static List<WB_Polygon> decomposePolygon2D(WB_Polygon poly) {
		if (!poly.isSimple()) {
			poly = gf.createSimplePolygon(poly);
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
			WB_Point p0 = polygon.get(i - 1 < 0 ? size - 1 : i - 1);
			WB_Point p1 = polygon.get(i + 1 == size ? 0 : i + 1);
			if (WB_GeometryOp.isReflex2D(p0, p, p1)) {
				for (int j = 0; j < size; j++) {
					WB_Point q = polygon.get(j);
					WB_Point q0 = polygon.get(j - 1 < 0 ? size - 1 : j - 1);
					WB_Point q1 = polygon.get(j + 1 == size ? 0 : j + 1);
					WB_Point s = new WB_Point();
					if (WB_GeometryOp.isLeftStrict2D(p0, p, q) && WB_GeometryOp.isRight2D(p0, p, q0)) {
						if (WB_GeometryOp.getLineIntersectionInto2D(p0, p, q, q0, s)) {
							if (WB_GeometryOp.isRightStrict2D(p1, p, s)) {
								double dist = p.getSqDistance2D(s);
								if (dist < lowerDistance) {
									lowerDistance = dist;
									lowerIntersection.set(s);
									lowerIndex = j;
								}
							}
						}
					}
					if (WB_GeometryOp.isLeftStrict2D(p1, p, q1) && WB_GeometryOp.isRight2D(p1, p, q)) {
						if (WB_GeometryOp.getLineIntersectionInto2D(p1, p, q, q1, s)) {
							if (WB_GeometryOp.isLeftStrict2D(p0, p, s)) {
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
				if (lowerIndex == (upperIndex + 1) % size) {
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

						if (q == p || q == p0 || q == p1) {
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
		iv1 = polygon.get(i + 1 == s ? 0 : i + 1);

		jv0 = polygon.get(j == 0 ? s - 1 : j - 1);
		jv = polygon.get(j);
		jv1 = polygon.get(j + 1 == s ? 0 : j + 1);

		if (WB_GeometryOp.isReflex2D(iv0, iv, iv1)) {
			if (WB_GeometryOp.isLeft2D(iv, iv0, jv) && WB_GeometryOp.isRight2D(iv, iv1, jv)) {
				return false;
			}
		} else {
			if (WB_GeometryOp.isRight2D(iv, iv1, jv) || WB_GeometryOp.isLeft2D(iv, iv0, jv)) {
				return false;
			}
		}
		if (WB_GeometryOp.isReflex2D(jv0, jv, jv1)) {
			if (WB_GeometryOp.isLeft2D(jv, jv0, iv) && WB_GeometryOp.isRight2D(jv, jv1, iv)) {
				return false;
			}
		} else {
			if (WB_GeometryOp.isRight2D(jv, jv1, iv) || WB_GeometryOp.isLeft2D(jv, jv0, iv)) {
				return false;
			}
		}
		for (int k = 0; k < s; k++) {
			int ki1 = k + 1 == s ? 0 : k + 1;
			if (k == i || k == j || ki1 == i || ki1 == j) {
				continue;
			}
			WB_Point k1 = polygon.get(k);
			WB_Point k2 = polygon.get(ki1);

			WB_Point in = WB_GeometryOp.getSegmentIntersection2D(iv, jv, k1, k2);
			if (in != null) {
				return false;
			}
		}

		return true;
	}

}
