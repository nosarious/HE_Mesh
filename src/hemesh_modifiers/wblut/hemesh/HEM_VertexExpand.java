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

import java.util.Iterator;

import javolution.util.FastTable;
import wblut.geom.WB_Coord;
import wblut.math.WB_ConstantScalarParameter;
import wblut.math.WB_ScalarParameter;

/**
 *
 */
public class HEM_VertexExpand extends HEM_Modifier {
	/**
	 *
	 */
	private WB_ScalarParameter d;

	/**
	 *
	 */
	public HEM_VertexExpand() {
		super();
		d = null;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_VertexExpand setDistance(final double d) {
		this.d = new WB_ConstantScalarParameter(d);
		return this;
	}

	public HEM_VertexExpand setDistance(final WB_ScalarParameter d) {
		this.d = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applyInt(final HE_Mesh mesh) {
		if (d == null) {
			return mesh;
		}
		HE_Vertex v;
		Iterator<HE_Vertex> vItr = mesh.vItr();
		final FastTable<WB_Coord> normals = new FastTable<WB_Coord>();
		while (vItr.hasNext()) {
			v = vItr.next();
			normals.add(v.getVertexNormal());
		}
		final Iterator<WB_Coord> vnItr = normals.iterator();
		vItr = mesh.vItr();
		WB_Coord n;
		while (vItr.hasNext()) {
			v = vItr.next();
			n = vnItr.next();
			v.addMulSelf(d.evaluate(v.xd(), v.yd(), v.zd()), n);
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	protected HE_Mesh applyInt(final HE_Selection selection) {
		if (d == null) {
			return selection.parent;
		}
		selection.collectVertices();
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			v.addMulSelf(d.evaluate(v.xd(), v.yd(), v.zd()), v.getVertexNormal());
		}
		return selection.parent;
	}
}
