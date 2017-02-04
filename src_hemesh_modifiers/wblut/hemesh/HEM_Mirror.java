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

import java.util.List;

import wblut.geom.WB_GeometryOp3D;
import wblut.geom.WB_Plane;
import wblut.math.WB_Epsilon;

/**
 *
 */
public class HEM_Mirror extends HEM_Modifier {

	private WB_Plane P;

	private boolean keepLargest;

	private boolean reverse = false;

	public HE_Selection cut;

	private double offset;

	/**
	 *
	 */
	public HEM_Mirror() {
		super();
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Mirror setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 *
	 *
	 * @param P
	 * @return
	 */
	public HEM_Mirror setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	/**
	 * Set plane by origin and normal.
	 *
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param nx
	 * @param ny
	 * @param nz
	 * @return
	 */
	public HEM_Mirror setPlane(final double ox, final double oy, final double oz, final double nx, final double ny,
			final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 *
	 *
	 * @param b
	 * @return
	 */
	public HEM_Mirror setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Mirror the largest part? Ignores the reverse setting.
	 *
	 * @param b
	 * @return
	 */
	public HEM_Mirror setKeepLargest(final Boolean b) {
		keepLargest = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (P == null) {
			return mesh;
		}
		HEM_Slice slice = new HEM_Slice();
		slice.setPlane(P);
		slice.setOffset(offset);
		slice.setReverse(reverse);
		slice.setCap(false);
		mesh.modify(slice);

		HE_Mesh mirrormesh = mesh.get();
		mirrormesh.vItr();
		HE_Vertex v, origv;
		for (int i = 0; i < mirrormesh.getNumberOfVertices(); i++) {
			v = mirrormesh.getVertexWithIndex(i);
			if (WB_Epsilon.isZero(WB_GeometryOp3D.getDistance3D(v, P))) {
				origv = mesh.getVertexWithIndex(i);
				List<HE_Halfedge> star = v.getHalfedgeStar();
				for (HE_Halfedge he : star) {
					mirrormesh.setVertex(he, origv);
				}
			} else {
				v.set(P.extractPoint2D(P.localPoint(v).scaleSelf(1, 1, -1)));
			}

		}

		HET_MeshOp.flipFaces(mirrormesh);

		mesh.add(mirrormesh);

		mesh.cleanUnusedElementsByFace();
		mesh.pairHalfedges();

		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return apply(selection.parent);
	}

	public static void main(final String[] args) {

		HEC_Cylinder creator = new HEC_Cylinder();
		creator.setFacets(32).setSteps(16).setRadius(50).setHeight(400);
		HE_Mesh mesh = new HE_Mesh(creator);
		HEM_Mirror modifier = new HEM_Mirror();
		WB_Plane P = new WB_Plane(0, 0, 0, 0, 1, 1);
		modifier.setPlane(P);
		modifier.setOffset(0);
		modifier.setReverse(false);
		mesh.modify(modifier);

	}
}
