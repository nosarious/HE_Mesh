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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Classification;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.math.WB_Epsilon;

/**
 * Planar cut of a mesh. Faces on positive side of cut plane are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Slice extends HEM_Modifier {
	/** Cut plane. */
	private WB_Plane P;
	/**
	 * HEM_slice keeps the part of the mesh on the positive side of the plane.
	 * Reverse planar cut.
	 */
	private boolean reverse = false;
	/**
	 * Cap holes?. Capping holes does not work properly with
	 * self-intersection...
	 */
	private boolean capHoles = true;
	/** The simple cap. */
	private boolean simpleCap = true;
	/** Keep center of cut mesh?. */
	private boolean keepCenter = false;
	/** Store cut faces. */
	public HE_Selection cut;


	/** Stores new edges. */
	public HE_Selection cutEdges;

	private List<HE_Path> paths;
	/** Store cap faces. */
	public HE_Selection cap;
	/** The offset. */
	private double offset;


	/**
	 * Set offset.
	 *
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEM_Slice setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_Slice.
	 */
	public HEM_Slice() {
		super();
	}

	/**
	 * Set cut plane.
	 *
	 * @param P
	 *            cut plane
	 * @return self
	 */
	public HEM_Slice setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	/**
	 * Sets the plane.
	 *
	 * @param ox
	 *            the ox
	 * @param oy
	 *            the oy
	 * @param oz
	 *            the oz
	 * @param nx
	 *            the nx
	 * @param ny
	 *            the ny
	 * @param nz
	 *            the nz
	 * @return the hE m_ slice
	 */
	public HEM_Slice setPlane(final double ox, final double oy, final double oz, final double nx, final double ny,
			final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set reverse option.
	 *
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Slice setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Set option to cap holes.
	 *
	 * @param b
	 *            true, false;
	 * @return self
	 */
	public HEM_Slice setCap(final Boolean b) {
		capHoles = b;
		return this;
	}

	/**
	 * Sets the simple cap.
	 *
	 * @param b
	 *            the b
	 * @return the hE m_ slice
	 */
	public HEM_Slice setSimpleCap(final Boolean b) {
		simpleCap = b;
		return this;
	}

	/**
	 * Set option to reset mesh center.
	 *
	 * @param b
	 *            true, false;
	 * @return self
	 */
	public HEM_Slice setKeepCenter(final Boolean b) {
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
		tracker.setStatus(this, "Starting HEM_Slice.", +1);
		cut = new HE_Selection(mesh);
		cap = new HE_Selection(mesh);
		cutEdges = new HE_Selection(mesh);
		mesh.resetEdgeInternalLabels();
		paths = new FastTable<HE_Path>();
		// no plane defined
		if (P == null) {
			tracker.setStatus(this, "No cutplane defined. Exiting HEM_Slice.", -1);
			return mesh;
		}
		// empty mesh
		if (mesh.getNumberOfVertices() == 0) {
			tracker.setStatus(this, "Empty mesh. Exiting HEM_Slice.", -1);
			return mesh;
		}
		WB_Plane lP = P.get();
		if (reverse) {
			lP.flipNormal();
		}
		lP = new WB_Plane(lP.getNormal(), lP.d() + offset);

		if (!WB_GeometryOp.checkIntersection3D(mesh.getAABB(), lP)) {
			tracker.setStatus(this,
					"Plane doesn't intersect bounding box. Exiting HEM_SliceSurface.", -1);
			return mesh;
		}
		tracker.setStatus(this, "Creating bounding box tree.", 0);
		final WB_AABBTree tree = new WB_AABBTree(mesh, Math.max(64, (int)Math.sqrt(mesh.getNumberOfFaces())));
		final HE_Selection faces = new HE_Selection(mesh);
		tracker.setStatus(this, "Retrieving intersection candidates.", 0);
		faces.addFaces(HE_Intersection.getPotentialIntersectedFaces(tree, lP));
		faces.collectVertices();
		faces.collectEdgesByFace();
		WB_Classification tmp;
		final HashMap<Long, WB_Classification> vertexClass = new HashMap<Long, WB_Classification>();
		WB_ProgressCounter counter = new WB_ProgressCounter(faces.getNumberOfVertices(), 10);

		tracker.setStatus(this, "Classifying vertices.", counter);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = faces.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = WB_GeometryOp.classifyPointToPlane3D(v, lP);
			vertexClass.put(v.key(), tmp);
			counter.increment();
		}
		counter = new WB_ProgressCounter(faces.getNumberOfEdges(), 10);

		tracker.setStatus(this, "Classifying edges.", counter);
		List<HE_Vertex> faceVertices = new ArrayList<HE_Vertex>();
		final HE_Selection split = new HE_Selection(mesh);
		final FastMap<Long, Double> edgeInt = new FastMap<Long, Double>();
		final Iterator<HE_Halfedge> eItr = faces.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.ON) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					cutEdges.add(e);
					e.setInternalLabel(1);
					e.getPair().setInternalLabel(1);
				} else {
					edgeInt.put(e.key(), 0.0);
				}
			} else if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.BACK) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.FRONT) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			} else {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.BACK) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			}
			counter.increment();
		}
		counter = new WB_ProgressCounter(edgeInt.size(), 10);

		tracker.setStatus(this, "Indexing edge intersection.", counter);
		for (final Map.Entry<Long, Double> en : edgeInt.entrySet()) {
			final HE_Halfedge ce = mesh.getHalfedgeWithKey(en.getKey());
			final double u = en.getValue();
			if (ce.getFace() != null) {
				split.add(ce.getFace());
			}
			if (ce.getPair().getFace() != null) {
				split.add(ce.getPair().getFace());
			}
			if (u < WB_Epsilon.EPSILON) {
				split.add(ce.getStartVertex());
			} else if (u > (1.0 - WB_Epsilon.EPSILON)) {
				split.add(ce.getEndVertex());
			} else {
				split.add(mesh.splitEdge(ce, u).vItr().next());
			}
			counter.increment();
		}
		counter = new WB_ProgressCounter(split.getNumberOfFaces(), 10);

		tracker.setStatus(this, "Splitting faces.", counter);
		HE_Face f;
		Iterator<HE_Face> fItr = split.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceVertices = f.getFaceVertices();
			int firstVertex = -1;
			int secondVertex = -1;
			final int n = faceVertices.size();
			for (int j = 0; j < n; j++) {
				v = faceVertices.get(j);
				if (split.contains(v)) {
					if (firstVertex == -1) {
						firstVertex = j;
						j++;// if one cut point is found, skip next point.
						// There should be at least one other vertex in
						// between for a proper cut.
					} else {
						secondVertex = j;
						break;
					}
				}
			}
			if ((firstVertex != -1) && (secondVertex != -1)) {
				final int fo = f.getFaceOrder();
				int diff = Math.abs(firstVertex - secondVertex);
				if (diff == (fo - 1)) {
					diff = 1;
				}
				if (diff > 1) {
					cut.add(f);
					final HE_Selection out = mesh.splitFace(f, faceVertices.get(firstVertex),
							faceVertices.get(secondVertex));
					WB_GeometryOp.classifyPointToPlane3D(f.getFaceCenter(), lP);

					if (out.getNumberOfFaces() > 0) {
						final HE_Face nf = out.fItr().next();
						cut.add(nf);
					}
					if (out.getNumberOfEdges() > 0) {
						final HE_Halfedge ne = out.eItr().next();
						ne.setInternalLabel(1);
						cutEdges.add(ne);
					}
				}
			}
			counter.increment();
		}
		buildPaths(cutEdges);
		final HE_Selection newFaces = new HE_Selection(mesh);
		HE_Face face;
		counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
		tracker.setStatus(this, "Classifying faces.", counter);
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			final WB_Classification cptp = WB_GeometryOp.classifyPointToPlane3D(face.getFaceCenter(), lP);
			if ((cptp == WB_Classification.FRONT) || (cptp == WB_Classification.ON)) {
				newFaces.add(face);
			} else {
				if (cut.contains(face)) {
					cut.remove(face);
				}
			}
			counter.increment();
		}
		tracker.setStatus(this, "Removing unwanted faces.", 0);
		mesh.replaceFaces(newFaces.getFacesAsArray());
		mesh.cleanUnusedElementsByFace();
		cut.cleanSelection();
		if (capHoles) {
			tracker.setStatus(this, "Capping holes.", 0);
			if (simpleCap) {
				cap.addFaces(mesh.capHoles());
			} else {
				final List<HE_Path> cutpaths = getPaths();
				tracker.setStatus(this, "Triangulating cut paths.", 0);
				final long[][] triKeys = HET_PlanarPathTriangulator.getTriangleKeys(cutpaths, lP);
				HE_Face tri;
				HE_Vertex v0, v1, v2;
				HE_Halfedge he0, he1, he2;
				for (int i = 0; i < triKeys.length; i++) {
					tri = new HE_Face();
					v0 = mesh.getVertexWithKey(triKeys[i][0]);
					v1 = mesh.getVertexWithKey(triKeys[i][1]);
					v2 = mesh.getVertexWithKey(triKeys[i][2]);
					he0 = new HE_Halfedge();
					he1 = new HE_Halfedge();
					he2 = new HE_Halfedge();
					mesh.setHalfedge(tri,he0);
					mesh.setVertex(he0,v0);
					mesh.setVertex(he1,v1);
					mesh.setVertex(he2,v2);
					mesh.setNext(he0,he1);
					mesh.setNext(he1,he2);
					mesh.setNext(he2,he0);
					mesh.setFace(he0,tri);
					mesh.setFace(he1,tri);
					mesh.setFace(he2,tri);
					cap.add(tri);
					mesh.add(tri);
					mesh.add(he0);
					mesh.add(he1);
					mesh.add(he2);
				}
			}
		}
		mesh.pairHalfedges();
		mesh.capHalfedges();
		if (!keepCenter) {
			mesh.resetCenter();
		}
		tracker.setStatus(this, "Ending HEM_Slice.", -1);
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

	/**
	 * 
	 *
	 * @param cutEdges 
	 */
	private void buildPaths(final HE_Selection cutEdges) {
		tracker.setStatus(this, "Building slice paths.", 0);
		if (cutEdges.getNumberOfEdges() == 0) {
			return;
		}
		final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();
		for (final HE_Halfedge he : cutEdges.getEdgesAsList()) {
			final HE_Face f = he.getFace();
			if (WB_GeometryOp.classifyPointToPlane3D(f.getFaceCenter(), P) == WB_Classification.FRONT) {
				edges.add(he.getPair());
			} else {
				edges.add(he);
			}
		}
		WB_ProgressCounter counter = new WB_ProgressCounter(edges.size(), 10);

		tracker.setStatus(this, "Processing slice edges.", counter);
		while (edges.size() > 0) {
			final List<HE_Halfedge> pathedges = new FastTable<HE_Halfedge>();
			HE_Halfedge current = edges.get(0);
			pathedges.add(current);
			boolean loop = false;
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getVertex() == current.getEndVertex()) {
					if (i > 0) {
						current = edges.get(i);
						pathedges.add(current);
						i = -1;
					} else {
						loop = true;
						break;
					}
				}
			}
			if (!loop) {
				final List<HE_Halfedge> reversepathedges = new FastTable<HE_Halfedge>();
				current = edges.get(0);
				for (int i = 0; i < edges.size(); i++) {
					if (edges.get(i).getEndVertex() == current.getVertex()) {
						if (i > 0) {
							current = edges.get(i);
							reversepathedges.add(current);
							i = 0;
						}
					}
				}
				final List<HE_Halfedge> finalpathedges = new FastTable<HE_Halfedge>();
				for (int i = reversepathedges.size() - 1; i > -1; i--) {
					finalpathedges.add(reversepathedges.get(i));
				}
				finalpathedges.addAll(pathedges);
				paths.add(new HE_Path(finalpathedges, loop));
				edges.removeAll(finalpathedges);
			} else {
				paths.add(new HE_Path(pathedges, loop));
				edges.removeAll(pathedges);
			}
			counter.increment(pathedges.size());
		}
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public List<HE_Path> getPaths() {
		return paths;
	}
}
