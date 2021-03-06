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

/**
 *
 */
public class WB_Triangulation2DWithPoints extends WB_Triangulation2D {

	/**
	 * 
	 */
	private List<WB_Coord> _points;

	/**
	 * 
	 */
	public WB_Triangulation2DWithPoints() {
	}

	/**
	 * 
	 *
	 * @param T
	 * @param E
	 * @param P
	 */
	public WB_Triangulation2DWithPoints(final int[] T, final int[] E, final List<? extends WB_Coord> P) {
		super(T, E);
		_points = new FastTable<WB_Coord>();
		_points.addAll(P);
	}

	/**
	 *
	 *
	 * @param T
	 * @param P
	 */
	public WB_Triangulation2DWithPoints(final int[] T, final List<? extends WB_Coord> P) {
		super(T);
		_points = new FastTable<WB_Coord>();
		_points.addAll(P);
	}

	/**
	 * 
	 *
	 * @param tri
	 */
	protected WB_Triangulation2DWithPoints(final WB_Triangulation2D tri) {
		super(tri.getTriangles(), tri.getEdges());
		_points = null;
	}

	/**
	 * 
	 *
	 * @return
	 */
	public List<WB_Coord> getPoints() {
		return _points;
	}
}