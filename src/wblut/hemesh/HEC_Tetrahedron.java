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

/**
 * Tetrahedron.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Tetrahedron extends HEC_Creator {
	/** Outer radius. */
	private double R;

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
		return this;
	}

	/**
	 * Code adapted from http://www.cs.umbc.edu/~squire/
	 *
	 * (non-Javadoc)
	 *
	 * @return the h e_ mesh
	 * @see wblut.hemesh.HEC_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		final double[][] vertices = new double[4][3];
		final double Pi = 3.141592653589793238462643383279502884197;
		final double phiaa = -19.471220333;
		final double phia = (Pi * phiaa) / 180.0;
		final double the120 = (Pi * 120.0) / 180.0;
		vertices[0][0] = 0;
		vertices[0][1] = 0;
		vertices[0][2] = R;
		double the = 0.0;
		for (int i = 1; i < 4; i++) {
			vertices[i][0] = R * Math.cos(the + (Math.PI / 3.0)) * Math.cos(phia);
			vertices[i][1] = R * Math.sin(the + (Math.PI / 3.0)) * Math.cos(phia);
			vertices[i][2] = R * Math.sin(phia);
			the = the + the120;
		}
		final int[][] faces = { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 1 }, { 1, 3, 2 } };
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();
	}
}
