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
 * Placeholder for quad.
 */
public class WB_Quad {
	/** First point. */
	public WB_Point p1;
	/** Second point. */
	public WB_Point p2;
	/** Third point. */
	public WB_Point p3;
	/** Fourth point. */
	public WB_Point p4;

	/**
	 * Instantiates a new WB_Quad. No copies are made.
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @param p3
	 *            third point
	 * @param p4
	 *            fourth point
	 */
	public WB_Quad(final WB_Point p1, final WB_Point p2, final WB_Point p3, final WB_Point p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	/**
	 *
	 *
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static int[] triangulateQuad(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2, final WB_Coord p3) {
		final boolean p0inside = WB_Triangle.pointInTriangleBary3D(p0, p1, p2, p3);
		final boolean p2inside = WB_Triangle.pointInTriangleBary3D(p2, p1, p0, p3);
		if (p0inside || p2inside) {
			return new int[] { 0, 1, 2, 0, 2, 3 };
		} else {
			return new int[] { 0, 1, 3, 1, 2, 3 };
		}
	}
}