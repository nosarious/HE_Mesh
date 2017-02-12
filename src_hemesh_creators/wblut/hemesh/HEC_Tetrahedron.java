/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

/**
 * Tetrahedron.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Tetrahedron extends HEC_Creator {
	/** Outer radius. */
	private double R;
	private WB_Coord[] points;

	/**
	 * Instantiates a new HEC_Tetrahedron.
	 *
	 */
	public HEC_Tetrahedron() {
		super();
		R = 100;
	}

	/**
	 * Set edge length.
	 *
	 * @param E
	 *            edge length
	 * @return self
	 */
	public HEC_Tetrahedron setEdge(final double E) {
		R = 0.612372 * E;
		points = null;
		return this;
	}

	/**
	 * Set radius of inscribed sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Tetrahedron setInnerRadius(final double R) {
		this.R = R * 3;
		points = null;
		return this;
	}

	/**
	 * Set radius of circumscribed sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Tetrahedron setOuterRadius(final double R) {
		this.R = R;
		points = null;
		return this;
	}

	/**
	 *
	 *
	 * @param R
	 * @return
	 */
	public HEC_Tetrahedron setRadius(final double R) {
		this.R = R;
		points = null;
		return this;
	}

	/**
	 * Set radius of tangential sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Tetrahedron setMidRadius(final double R) {
		this.R = R * 1.732060;
		points = null;
		return this;
	}

	public HEC_Tetrahedron setPoints(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2, final WB_Coord p3) {
		points = new WB_Coord[4];
		points[0] = new WB_Point(p0);
		points[1] = new WB_Point(p1);
		points[2] = new WB_Point(p2);
		points[3] = new WB_Point(p3);
		return this;
	}

	/**
	 *
	 * @see wblut.hemesh.HEC_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (points == null) {
			final double[][] vertices = new double[4][3];
			final double Pi = 3.141592653589793238462643383279502884197;
			final double phiaa = -19.471220333;
			final double phia = Pi * phiaa / 180.0;
			final double the120 = Pi * 120.0 / 180.0;
			vertices[0][0] = 0;
			vertices[0][1] = 0;
			vertices[0][2] = R;
			double the = 0.0;
			for (int i = 1; i < 4; i++) {
				vertices[i][0] = R * Math.cos(the + Math.PI / 3.0) * Math.cos(phia);
				vertices[i][1] = R * Math.sin(the + Math.PI / 3.0) * Math.cos(phia);
				vertices[i][2] = R * Math.sin(phia);
				the = the + the120;

			}
			final int[][] faces = { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 1 }, { 1, 3, 2 } };
			final HEC_FromFacelist fl = new HEC_FromFacelist();
			fl.setVertices(vertices).setFaces(faces);

			return fl.createBase();
		} else {
			final int[][] faces = { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 1 }, { 1, 3, 2 } };
			final HEC_FromFacelist fl = new HEC_FromFacelist();
			fl.setVertices(points).setFaces(faces);
			return fl.createBase();

		}

	}
}
