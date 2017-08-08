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

import java.util.Collection;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Voronoi;
import wblut.geom.WB_VoronoiCell3D;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 * Creates the Voronoi cells of a collection of points, constrained by a box.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEMC_VoronoiBox extends HEMC_MultiCreator {
	/** Points. */
	private List<WB_Coord> points;
	/** Number of points. */
	private int numberOfPoints;
	/** Container. */
	private WB_AABB aabb;

	private boolean bruteForce;
	/** Offset. */
	private WB_ScalarParameter offset;

	/**
	 *
	 */
	public HEMC_VoronoiBox() {
		super();
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            array of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiBox setPoints(final WB_Coord[] points) {
		this.points = new FastTable<WB_Coord>();
		for (WB_Coord p : points) {
			this.points.add(p);
		}
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            collection of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiBox setPoints(final Collection<? extends WB_Coord> points) {
		this.points = new FastTable<WB_Coord>();
		this.points.addAll(points);
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of double of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiBox setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new FastTable<WB_Coord>();
		for (int i = 0; i < n; i++) {
			this.points.add(new WB_Point(points[i][0], points[i][1], points[i][2]));
		}
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of float of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiBox setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new FastTable<WB_Coord>();
		for (int i = 0; i < n; i++) {
			this.points.add(new WB_Point(points[i][0], points[i][1], points[i][2]));
		}
		return this;
	}

	/**
	 * Set voronoi cell offset.
	 *
	 * @param o
	 *            offset
	 * @return self
	 */
	public HEMC_VoronoiBox setOffset(final double o) {
		offset = new WB_ConstantScalarParameter(o);
		return this;
	}

	/**
	 * Set voronoi cell offset.
	 *
	 * @param o
	 *            offset
	 * @return self
	 */
	public HEMC_VoronoiBox setOffset(final WB_ScalarParameter o) {
		offset = o;
		return this;
	}

	/**
	 * Set enclosing box limiting cells.
	 *
	 * @param container
	 *            enclosing WB_AABB
	 * @return self
	 */
	public HEMC_VoronoiBox setContainer(final WB_AABB container) {
		this.aabb = container;
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEMC_VoronoiBox setBruteForce(final boolean b) {
		bruteForce = b;
		return this;
	}

	@Override
	void create(final HE_MeshCollection result) {
		tracker.setStatus(this, "Starting HEMC_VoronoiCells", +1);

		if (aabb == null) {
			_numberOfMeshes = 0;
			return;
		}
		if (points == null) {
			result.add(new HE_Mesh(new HEC_Box().setFromAABB(aabb)));
			_numberOfMeshes = 1;
			return;
		}

		numberOfPoints = points.size();

		List<WB_VoronoiCell3D> voronoi = bruteForce
				? WB_Voronoi.getVoronoi3DBruteForce(points, numberOfPoints, aabb, offset)
				: WB_Voronoi.getVoronoi3D(points, numberOfPoints, aabb, offset);

		for (WB_VoronoiCell3D vor : voronoi) {
			HE_Mesh m = new HE_Mesh(vor.getMesh());
			m.setInternalLabel(vor.getIndex());
			m.setLabel(vor.getIndex());
			result.add(m);

		}

		_numberOfMeshes = result.size();
		tracker.setStatus(this, "Exiting HEMC_VoronoiCells.", -1);
	}

}
