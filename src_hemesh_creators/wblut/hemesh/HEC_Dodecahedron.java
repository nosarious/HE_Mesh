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

import wblut.geom.WB_Point;

/**
 * Dodecahedron.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Dodecahedron extends HEC_Creator {
	/** Outer Radius. */
	private double R;

	/**
	 * Instantiates a new dodecahedron.
	 *
	 */
	public HEC_Dodecahedron() {
		super();
		R = 100f;
	}

	/**
	 * Instantiates a new dodecahedron.
	 *
	 * @param R
	 *            outer radius
	 */
	public HEC_Dodecahedron(final double R) {
		super();
		this.R = R;
		center = new WB_Point();
	}

	/**
	 * Set edge length.
	 *
	 * @param E
	 *            edge length
	 * @return self
	 */
	public HEC_Dodecahedron setEdge(final double E) {
		R = 1.40126 * E;
		return this;
	}

	/**
	 * Set radius inscribed sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Dodecahedron setInnerRadius(final double R) {
		this.R = R * 1.258406;
		return this;
	}

	/**
	 * Set radius circumscribed sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Dodecahedron setOuterRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set radius circumscribed sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Dodecahedron setRadius(final double R) {
		this.R = R;
		return this;
	}

	/**
	 * Set radius tangential sphere.
	 *
	 * @param R
	 *            radius
	 * @return self
	 */
	public HEC_Dodecahedron setMidRadius(final double R) {
		this.R = R * 1.070465;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		HEC_Creator dode = new HEC_Plato().setType(2).setEdge(R / 1.40126);
		return dode.createBase();
	}
}
