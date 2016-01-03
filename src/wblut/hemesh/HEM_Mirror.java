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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.geom.WB_Classification;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 *
 */
public class HEM_Mirror extends HEM_Modifier {


	private WB_Plane P;

	private boolean keepCenter = false;

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

	/** Mirror the largest part? Ignores the reverse setting.
	 *
	 * @param b
	 * @return
	 */
	public HEM_Mirror setKeepLargest(final Boolean b) {
		keepLargest = b;
		return this;
	}

	/**
	 * Reset the center of the mirrored mesh to the center of the original mesh.
	 *
	 * @param b 
	 * @return 
	 */
	public HEM_Mirror setKeepCenter(final Boolean b) {
		keepCenter = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatus(this, "Starting HEM_Mirror.", +1);
		cut = new HE_Selection(mesh);
		// no plane defined
		if (P == null) {
			tracker.setStatus(this, "No mirror plane defined. Exiting HEM_Mirror.", -1);
			return mesh;
		}
		// empty mesh
		if (mesh.getNumberOfVertices() == 0) {
			tracker.setStatus(this, "No vertices in mesh. Exiting HEM_Mirror.", -1);
			return mesh;
		}
		WB_Plane lP = P.get();
		if (reverse) {
			lP.flipNormal();
		}
		lP = new WB_Plane(lP.getNormal(), lP.d() + offset);
		HEM_SliceSurface ss;
		ss = new HEM_SliceSurface().setPlane(lP);
		mesh.modify(ss);
		cut = ss.cut;
		final HE_Selection newFaces = new HE_Selection(mesh);
		HE_Face face;
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
		tracker.setStatus(this, "Classifying mesh faces.", counter);
		Iterator<HE_Face> fItr = mesh.fItr();
		List<WB_Classification> sides=new FastTable<WB_Classification>();


		int front = 0;
		int back = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			final WB_Classification cptp = WB_GeometryOp.classifyPolygonToPlane3D(face.toPolygon(), lP);
			sides.add(cptp);
			if ((cptp == WB_Classification.FRONT)) {
				front++;
			} else {
				back++;
			}

		}
		boolean flip=false;
		if (keepLargest) {
			if (back > front) {
				flip=true;
			}
		}
		fItr = mesh.fItr();
		int id=0;
		while (fItr.hasNext()) {
			face = fItr.next();
			final WB_Classification cptp = sides.get(id++);
			if ((cptp ==((flip)?WB_Classification.BACK: WB_Classification.FRONT)) || (cptp == WB_Classification.ON)) {
				newFaces.add(face);
			} else {
				if (cut.contains(face)) {
					cut.remove(face);
				}
			}
			counter.increment();
		}
		mesh.replaceFaces(newFaces.getFacesAsArray());
		cut.cleanSelection();

		final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getFaceOrder() < 3) {
				facesToRemove.add(face);
			}
		}

		mesh.removeFaces(facesToRemove);
		mesh.cleanUnusedElementsByFace();
		mesh.capHalfedges();

		final HE_Mesh mirrormesh = mesh.get();
		counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
		tracker.setStatus(this, "Mirroring vertices.", counter);
		final List<HE_Vertex> vertices = mirrormesh.getVerticesAsList();
		HE_Vertex v, origv;
		for (int i = 0; i < vertices.size(); i++) {
			v = vertices.get(i);
			final WB_Point p = WB_GeometryOp.getClosestPoint3D(v, lP);
			final WB_Vector dv = v.subToVector3D(p);
			if (dv.getLength3D() <= WB_Epsilon.EPSILON) {
				final List<HE_Halfedge> star = v.getHalfedgeStar();
				origv = mesh.getVertexWithIndex(i);
				for (final HE_Halfedge he : star) {
					mesh.setVertex(he,origv);
				}
				mirrormesh.remove(v);
			} else {
				v.addMulSelf(-2, dv);
			}
			counter.increment();
		}
		mirrormesh.flipAllFaces();
		mesh.uncapBoundaryHalfedges();
		mirrormesh.uncapBoundaryHalfedges();
		tracker.setStatus(this, "Adding Mirrored mesh.",0);
		mesh.add(mirrormesh);
		mesh.pairHalfedges();
		mesh.capHalfedges();
		if (!keepCenter) {
			mesh.resetCenter();
		}
		tracker.setStatus(this, "Exiting HEM_Mirror.", -1);
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
}
