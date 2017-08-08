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
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Polygon;

/**
 *
 */
public class HEC_Polygon extends HEC_Creator {

	/**
	 *
	 */
	private List<WB_Polygon> polygon;

	/**
	 *
	 */
	private double thickness;
	private double offset;

	/**
	 *
	 */
	public HEC_Polygon() {
		super();
		override = true;
	}

	/**
	 *
	 *
	 * @param poly
	 * @param d
	 */
	public HEC_Polygon(final WB_Polygon poly, final double d) {
		this();
		override = true;
		polygon = new FastTable<WB_Polygon>();
		polygon.add(poly);
		thickness = d;
		offset = 0;
	}

	public HEC_Polygon(final Collection<? extends WB_Polygon> polygons, final double d) {
		this();
		override = true;
		setPolygon(polygons);
		thickness = d;
		offset = 0;
	}

	public HEC_Polygon(final WB_Polygon[] polygons, final double d) {
		this();
		override = true;
		setPolygon(polygons);
		thickness = d;
		offset = 0;
	}

	/**
	 *
	 *
	 * @param poly
	 * @return
	 */
	public HEC_Polygon setPolygon(final WB_Polygon poly) {
		polygon = new FastTable<WB_Polygon>();
		polygon.add(poly);
		return this;
	}

	/**
	 *
	 * @param polygons
	 * @return
	 */
	public HEC_Polygon setPolygon(final Collection<? extends WB_Polygon> polygons) {
		polygon = new FastTable<WB_Polygon>();
		polygon.addAll(polygons);
		return this;
	}

	/**
	 *
	 * @param polygons
	 * @return
	 */
	public HEC_Polygon setPolygon(final WB_Polygon[] polygons) {
		polygon = new FastTable<WB_Polygon>();
		for (WB_Polygon poly : polygons) {
			polygon.add(poly);
		}
		return this;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEC_Polygon setThickness(final double d) {
		thickness = d;
		return this;
	}

	public HEC_Polygon setOffset(final double d) {
		offset = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (polygon == null) {
			return new HE_Mesh();
		}
		HE_Mesh result = new HE_Mesh();
		WB_GeometryFactory gf = new WB_GeometryFactory();
		for (WB_Polygon poly : polygon) {

			result.add(new HE_Mesh(gf.createPrism(poly, thickness, offset)));

		}
		return result;

	}
}
