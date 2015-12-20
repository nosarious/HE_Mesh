package wblut.hemesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import wblut.core.WB_ProgressCounter;
import wblut.core.WB_ProgressTracker;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Triangle;
import wblut.math.WB_Epsilon;

public class HET_MeshOp {
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
	public static final WB_ProgressTracker tracker = WB_ProgressTracker.instance();

	/**
	 * Split edge in half.
	 *
	 * @param edge
	 *            edge to split.
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final HE_Halfedge edge, final HE_Mesh mesh) {
		final WB_Point v = gf.createMidpoint(edge.getVertex(), edge.getEndVertex());
		return splitEdge(edge, v, mesh);
	}

	/**
	 * Split edge in two parts.
	 *
	 * @param edge
	 *            edge to split
	 * @param f
	 *            fraction of first part (0..1)
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final HE_Halfedge edge, final double f, final HE_Mesh mesh) {
		final WB_Point v = gf.createInterpolatedPoint(edge.getVertex(), edge.getEndVertex(),
				edge.isEdge() ? f : 1.0 - f);
		return splitEdge(edge, v, mesh);
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param edge
	 *            edge to split
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 */
	public static void splitEdge(final HE_Halfedge edge, final double x, final double y, final double z,
			final HE_Mesh mesh) {
		splitEdge(edge, new WB_Point(x, y, z), mesh);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public static void splitEdge(final HE_Halfedge edge, final double[] f, final HE_Mesh mesh) {
		final double[] fArray = Arrays.copyOf(f, f.length);
		Arrays.sort(fArray);
		HE_Halfedge e = edge;
		final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex v0 = he0.getVertex();
		final HE_Vertex v1 = he1.getVertex();
		HE_Vertex v = new HE_Vertex();
		for (int i = 0; i < f.length; i++) {
			final double fi = fArray[i];
			if ((fi > 0) && (fi < 1)) {
				v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
				e = (splitEdge(e, v, mesh).eItr().next());
			}
		}
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public static void splitEdge(final HE_Halfedge edge, final float[] f, final HE_Mesh mesh) {
		final float[] fArray = Arrays.copyOf(f, f.length);
		Arrays.sort(fArray);
		HE_Halfedge e = edge;
		final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex v0 = he0.getVertex();
		final HE_Vertex v1 = he1.getVertex();
		HE_Vertex v = new HE_Vertex();
		for (int i = 0; i < f.length; i++) {
			final double fi = fArray[i];
			if ((fi > 0) && (fi < 1)) {
				v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
				e = (splitEdge(e, v, mesh).eItr().next());
			}
		}
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param edge
	 *            edge to split
	 * @param v
	 *            position of new vertex
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final HE_Halfedge edge, final WB_Coord v, final HE_Mesh mesh) {
		final HE_Selection out = new HE_Selection(mesh);
		final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
		final HE_Halfedge he1 = he0.getPair();
		final HE_Vertex vNew = new HE_Vertex(v);
		final HE_Halfedge he0new = new HE_Halfedge();
		final HE_Halfedge he1new = new HE_Halfedge();
		final HE_Halfedge he0n = he0.getNextInFace();
		final HE_Halfedge he1n = he1.getNextInFace();
		final double d0 = he0.getVertex().getDistance3D(v);
		final double d1 = he1.getVertex().getDistance3D(v);
		final double f0 = d1 / (d0 + d1);
		final double f1 = d0 / (d0 + d1);
		mesh.setVertex(he0new,vNew);
		mesh.setVertex(he1new,vNew);
		mesh.setHalfedge(vNew,he0new);
		mesh.setNext(he0new,he0n);
		he0new.copyProperties(he0);
		mesh.setNext(he1new,he1n);
		he1new.copyProperties(he1);
		if (he0.hasUVW() && he0n.hasUVW()) {
			he0new.setUVW(new HE_TextureCoordinate(f0, he0.getUVW(), he0n.getUVW()));
		}
		if (he1.hasUVW() && he1n.hasUVW()) {
			he1new.setUVW(new HE_TextureCoordinate(f1, he1.getUVW(), he1n.getUVW()));
		}
		mesh.setNext(he0,he0new);
		mesh.setNext(he1,he1new);
		mesh.setPair(he0,he1new);
		mesh.setPair(he0new,he1);

		if (he0.getFace() != null) {
			mesh.setFace(he0new,he0.getFace());
		}
		if (he1.getFace() != null) {
			mesh.setFace(he1new,he1.getFace());
		}
		vNew.setInternalLabel(1);
		mesh.add(vNew);
		mesh.add(he0new);
		mesh.add(he1new);
		out.add(he0new.isEdge() ? he0new : he1new);
		out.add(he0.isEdge() ? he0 : he1);
		out.add(vNew);
		return out;
	}

	/**
	 * Split edge in half.
	 *
	 * @param key
	 *            key of edge to split.
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final long key, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		final WB_Point v = gf.createMidpoint(edge.getVertex(), edge.getEndVertex());
		return splitEdge(edge, v, mesh);
	}

	/**
	 * Split edge in two parts.
	 *
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            fraction of first part (0..1)
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final long key, final double f, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		return splitEdge(edge, f, mesh);
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param key
	 *            key of edge to split
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 */
	public static void splitEdge(final long key, final double x, final double y, final double z, final HE_Mesh mesh) {
		splitEdge(key, new WB_Point(x, y, z), mesh);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public static void splitEdge(final long key, final double[] f, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		splitEdge(edge, f, mesh);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public static void splitEdge(final long key, final float[] f, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		splitEdge(edge, f, mesh);
	}

	/**
	 * Insert vertex in edge.
	 *
	 * @param key
	 *            key of edge to split
	 * @param v
	 *            position of new vertex
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final Long key, final WB_Point v, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		return splitEdge(edge, v, mesh);
	}

	/**
	 * Split all edges in half.
	 *
	 * @return selection of new vertices and new edges
	 */
	public static HE_Selection splitEdges(final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final HE_Halfedge[] edges = mesh.getEdgesAsArray();
		final int n = edges.length;
		for (int i = 0; i < n; i++) {
			selectionOut.add(splitEdge(edges[i], 0.5, mesh));
		}
		return selectionOut;
	}

	/**
	 * Split all edges in half, offset the center by a given distance along the
	 * edge normal.
	 *
	 * @param offset
	 *            the offset
	 * @return selection of new vertices and new edges
	 */
	public static HE_Selection splitEdges(final double offset, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final HE_Halfedge[] edges = mesh.getEdgesAsArray();
		final int n = mesh.getNumberOfEdges();
		for (int i = 0; i < n; i++) {
			final WB_Point p = new WB_Point(edges[i].getEdgeNormal());
			p.mulSelf(offset).addSelf(edges[i].getHalfedgeCenter());
			selectionOut.add(splitEdge(edges[i], p, mesh));
		}
		return selectionOut;
	}

	/**
	 * Split edge in half.
	 *
	 * @param selection
	 *            edges to split.
	 * @return selection of new vertices and new edges
	 */
	public static HE_Selection splitEdges(final HE_Selection selection, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		selection.collectEdgesByFace();
		final Iterator<HE_Halfedge> eItr = selection.heItr();
		while (eItr.hasNext()) {
			selectionOut.add(splitEdge(eItr.next(), 0.5, mesh));
		}
		selection.addHalfedges(selectionOut.getEdgesAsArray());
		return selectionOut;
	}

	/**
	 * Split edge in half, offset the center by a given distance along the edge
	 * normal.
	 *
	 * @param selection
	 *            edges to split.
	 * @param offset
	 *            the offset
	 * @return selection of new vertices and new edges
	 */
	public static HE_Selection splitEdges(final HE_Selection selection, final double offset, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		selection.collectEdgesByFace();
		final Iterator<HE_Halfedge> eItr = selection.heItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			final WB_Point p = new WB_Point(e.getEdgeNormal());
			p.mulSelf(offset).addSelf(e.getHalfedgeCenter());
			selectionOut.add(splitEdge(e, p, mesh));
		}
		selection.addHalfedges(selectionOut.getEdgesAsArray());
		return selectionOut;
	}

	/**
	 * Divide edge.
	 *
	 * @param origE
	 *            edge to divide
	 * @param n
	 *            number of parts
	 */
	public static void divideEdge(final HE_Halfedge origE, final int n, final HE_Mesh mesh) {
		if (n > 1) {
			final double[] f = new double[n - 1];
			final double in = 1.0 / n;
			for (int i = 0; i < (n - 1); i++) {
				f[i] = (i + 1) * in;
			}
			splitEdge(origE, f, mesh);
		}
	}

	public static void divideEdge(final long key, final int n, final HE_Mesh mesh) {
		divideEdge(mesh.getHalfedgeWithKey(key), n, mesh);
	}

	/**
	 * Divide face along two vertices.
	 *
	 * @param face
	 *            face to divide
	 * @param vi
	 *            first vertex
	 * @param vj
	 *            second vertex
	 * @return new face and edge
	 */
	public static HE_Selection splitFace(final HE_Face face, final HE_Vertex vi, final HE_Vertex vj,
			final HE_Mesh mesh) {
		final HE_Selection out = new HE_Selection(mesh);
		final HE_Halfedge hei = vi.getHalfedge(face);
		final HE_Halfedge hej = vj.getHalfedge(face);
		final HE_TextureCoordinate ti = (hei.hasUVW()) ? hei.getUVW() : null;
		final HE_TextureCoordinate tj = (hej.hasUVW()) ? hej.getUVW() : null;
		final double d = vi.getDistance3D(vj);
		boolean degenerate = false;
		if (WB_Epsilon.isZero(d)) {// happens when a collinear (part of a) face
			// is cut. Do not add a new edge connecting
			// these two points,rather collapse them into
			// each other and remove two-edge faces
			degenerate = true;
		}
		if ((hei.getNextInFace() != hej) || (hei.getPrevInFace() != hej)) {
			HE_Halfedge heiPrev;
			HE_Halfedge hejPrev;
			HE_Face faceNew;
			if (!degenerate) {
				HE_Halfedge he0new;
				HE_Halfedge he1new;
				heiPrev = hei.getPrevInFace();
				hejPrev = hej.getPrevInFace();
				he0new = new HE_Halfedge();
				he1new = new HE_Halfedge();
				mesh.setVertex(he0new,vj);
				if (tj != null) {
					he0new.setUVW(tj);
				}
				mesh.setVertex(he1new,vi);
				if (ti != null) {
					he1new.setUVW(ti);
				}
				mesh.setNext(he0new,hei);
				mesh.setNext(he1new,hej);
				mesh.setNext(heiPrev,he1new);
				mesh.setNext(hejPrev,he0new);
				mesh.setPair(he0new,he1new);
				he0new.setInternalLabel(1);
				he1new.setInternalLabel(1);
				mesh.setFace(he0new,face);
				faceNew = new HE_Face();
				mesh.setHalfedge(face,hei);
				mesh.setHalfedge(faceNew,hej);
				faceNew.copyProperties(face);
				assignFaceToLoop(mesh,faceNew, hej);
				mesh.add(he0new);
				mesh.add(he1new);
				mesh.add(faceNew);
				out.add(he0new.isEdge() ? he0new : he1new);
				out.add(faceNew);
				return out;
			} else {
				heiPrev = hei.getPrevInFace();
				hejPrev = hej.getPrevInFace();
				for (final HE_Halfedge hejs : vj.getHalfedgeStar()) {
					mesh.setVertex(hejs,vi);
				}
				mesh.setNext(heiPrev,hej);
				mesh.setNext(hejPrev,hei);
				faceNew = new HE_Face();
				mesh.setHalfedge(face,hei);
				mesh.setHalfedge(faceNew,hej);
				faceNew.copyProperties(face);
				assignFaceToLoop(mesh,faceNew, hej);
				mesh.add(faceNew);
				mesh.remove(vj);
				out.add(faceNew);
				if (face.getFaceOrder() == 2) {
					mesh.deleteTwoEdgeFace(face);
				}
				if (faceNew.getFaceOrder() == 2) {
					mesh.deleteTwoEdgeFace(faceNew);
					out.remove(faceNew);
				}
				return out;
			}
		}
		return null;
	}

	/**
	 * Divide face along two vertices.
	 *
	 * @param fkey
	 *            key of face
	 * @param vkeyi
	 *            key of first vertex
	 * @param vkeyj
	 *            key of second vertex
	 * @return new face and edge
	 */
	public static HE_Selection splitFace(final long fkey, final long vkeyi, final long vkeyj, final HE_Mesh mesh) {
		return splitFace(mesh.getFaceWithKey(fkey), mesh.getVertexWithKey(vkeyi), mesh.getVertexWithKey(vkeyj), mesh);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit();
		mesh.modify(cs);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final double d, final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d);
		mesh.modify(cs);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @param d
	 * @param c
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final double d, final double c, final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d).setChamfer(c);
		mesh.modify(cs);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final HE_Selection faces, final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit();
		mesh.modifySelected(cs, faces);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final HE_Selection faces, final double d, final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d);
		mesh.modifySelected(cs, faces);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @param c
	 * @return
	 */
	public static HE_Selection splitFacesCenter(final HE_Selection faces, final double d, final double c,
			final HE_Mesh mesh) {
		final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d).setChamfer(c);
		mesh.modifySelected(cs, faces);
		return cs.getCenterFaces();
	}

	/**
	 *
	 *
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole();
		mesh.modify(csh);
		return csh.getWallFaces();
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final double d, final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d);
		mesh.modify(csh);
		return csh.getWallFaces();
	}

	/**
	 *
	 *
	 * @param d
	 * @param c
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final double d, final double c, final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d).setChamfer(c);
		mesh.modify(csh);
		return csh.getWallFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final HE_Selection faces, final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole();
		mesh.modifySelected(csh, faces);
		return csh.getWallFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final HE_Selection faces, final double d, final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d);
		mesh.modifySelected(csh, faces);
		return csh.getWallFaces();
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @param c
	 * @return
	 */
	public static HE_Selection splitFacesCenterHole(final HE_Selection faces, final double d, final double c,
			final HE_Mesh mesh) {
		final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d).setChamfer(c);
		mesh.modifySelected(csh, faces);
		return csh.getWallFaces();
	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesHybrid(final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = mesh.getNumberOfFaces();
		final WB_Coord[] faceCenters = new WB_Coord[n];
		final int[] faceOrders = new int[n];
		HE_Face f;
		int i = 0;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceCenters[i] = f.getFaceCenter();
			faceOrders[i] = f.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(mesh.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(mesh).getVerticesAsArray());
		final HE_Face[] faces = mesh.getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		int fo;
		for (i = 0; i < n; i++) {
			f = faces[i];
			fo = f.getFaceOrder() / 2;
			if (fo == 3) {
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
				int c = 0;
				do {
					textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				c = 0;
				do {
					final HE_Face fn = new HE_Face();
					fn.copyProperties(f);
					mesh.add(fn);
					he0[c] = he;
					mesh.setFace(he,fn);
					mesh.setHalfedge(fn,he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(hec[c]);
					mesh.setVertex(hec[c],he.getVertex());
					if (textures[c] != null) {
						hec[c].setUVW(textures[c]);
					}
					mesh.setPair(hec[c],he2[c]);

					mesh.setFace(hec[c],f);
					mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
					if (textures[(c + 1) % fo] != null) {
						he2[c].setUVW(textures[(c + 1) % fo]);
					}
					mesh.setNext(he2[c],he0[c]);
					mesh.setFace(he1[c],fn);
					mesh.setFace(he2[c],fn);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(f,hec[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j],he2[j]);
					mesh.setNext(hec[j],hec[(j + 1) % c]);
				}
			} else if (fo > 3) {
				vi = new HE_Vertex(faceCenters[i]);
				vi.setInternalLabel(2);
				double u = 0;
				double v = 0;
				double w = 0;
				HE_Halfedge he = f.getHalfedge();
				boolean hasTexture = true;
				do {
					if (!he.getVertex().hasUVW(f)) {
						hasTexture = false;
						break;
					}
					u += he.getVertex().getUVW(f).ud();
					v += he.getVertex().getUVW(f).vd();
					w += he.getVertex().getUVW(f).wd();
					he = he.getNextInFace();
				} while (he != f.getHalfedge());
				if (hasTexture) {
					final double ifo = 1.0 / f.getFaceOrder();
					vi.setUVW(u * ifo, v * ifo, w * ifo);
				}
				mesh.add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				he = startHE;
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					} else {
						fc = new HE_Face();
						fc.copyProperties(f);
						mesh.add(fc);
					}
					he0[c] = he;
					mesh.setFace(he,fc);
					mesh.setHalfedge(fc,he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(he3[c]);
					mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
					if (he2[c].getVertex().hasHalfedgeUVW(f)) {
						he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
					}
					mesh.setVertex(he3[c],vi);
					mesh.setNext(he2[c],he3[c]);
					mesh.setNext(he3[c],he);
					mesh.setFace(he1[c],fc);
					mesh.setFace(he2[c],fc);
					mesh.setFace(he3[c],fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(vi,he3[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j],he2[j]);
				}
			}
		}
		mesh.pairHalfedges();
		return selectionOut;
	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @param sel
	 *            the sel
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesHybrid(final HE_Selection sel, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = sel.getNumberOfFaces();
		final WB_Coord[] faceCenters = new WB_Coord[n];
		final int[] faceOrders = new int[n];
		HE_Face f;
		int i = 0;
		final Iterator<HE_Face> fItr = sel.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faceCenters[i] = f.getFaceCenter();
			faceOrders[i] = f.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(sel.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(mesh).getVerticesAsArray());
		final HE_Face[] faces = sel.getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		int fo;
		for (i = 0; i < n; i++) {
			f = faces[i];
			fo = f.getFaceOrder() / 2;
			if (fo == 3) {
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
				int c = 0;
				do {
					textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				c = 0;
				do {
					final HE_Face fn = new HE_Face();
					fn.copyProperties(f);
					mesh.add(fn);
					sel.add(fn);
					he0[c] = he;
					mesh.setFace(he,fn);
					mesh.setHalfedge(fn,he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(hec[c]);
					mesh.setVertex(hec[c],he.getVertex());
					if (textures[c] != null) {
						hec[c].setUVW(textures[c]);
					}
					mesh.setPair(hec[c],he2[c]);

					mesh.setFace(hec[c],f);
					mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
					if (textures[(c + 1) % fo] != null) {
						he2[c].setUVW(textures[(c + 1) % fo]);
					}
					mesh.setNext(he2[c],he0[c]);
					mesh.setFace(he1[c],fn);
					mesh.setFace(he2[c],fn);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(f,hec[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j],he2[j]);
					mesh.setNext(hec[j],hec[(j + 1) % c]);
				}
			} else if (fo > 3) {
				vi = new HE_Vertex(faceCenters[i]);
				vi.setInternalLabel(2);
				double u = 0;
				double v = 0;
				double w = 0;
				HE_Halfedge he = f.getHalfedge();
				boolean hasTexture = true;
				do {
					if (!he.getVertex().hasUVW(f)) {
						hasTexture = false;
						break;
					}
					u += he.getVertex().getUVW(f).ud();
					v += he.getVertex().getUVW(f).vd();
					w += he.getVertex().getUVW(f).wd();
					he = he.getNextInFace();
				} while (he != f.getHalfedge());
				if (hasTexture) {
					final double ifo = 1.0 / f.getFaceOrder();
					vi.setUVW(u * ifo, v * ifo, w * ifo);
				}
				mesh.add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				he = startHE;
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					} else {
						fc = new HE_Face();
						fc.copyProperties(f);
						mesh.add(fc);
						sel.add(fc);
					}
					he0[c] = he;
					mesh.setFace(he,fc);
					mesh.setHalfedge(fc,he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(he3[c]);
					mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
					if (he2[c].getVertex().hasHalfedgeUVW(f)) {
						he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
					}
					mesh.setVertex(he3[c],vi);
					mesh.setNext(he2[c],he3[c]);
					mesh.setNext(he3[c],he);
					mesh.setFace(he1[c],fc);
					mesh.setFace(he2[c],fc);
					mesh.setFace(he3[c],fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(vi,he3[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j],he2[j]);
				}
			}
		}
		mesh.pairHalfedges();
		return selectionOut;
	}

	/**
	 * Midedge split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesMidEdge(final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = mesh.getNumberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		int i = 0;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(mesh.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(mesh).getVerticesAsArray());
		final HE_Face[] faces = mesh.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final int fo = face.getFaceOrder() / 2;
			final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
			int c = 0;
			do {
				textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			he = startHE;
			do {
				final HE_Face f = new HE_Face();
				f.copyProperties(face);
				mesh.add(f);
				he0[c] = he;
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				mesh.setVertex(hec[c],he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c],he2[c]);
				mesh.setFace(hec[c],face);
				mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c],he0[c]);
				mesh.setFace(he0[c],f);
				mesh.setHalfedge(f,he0[c]);
				mesh.setFace(he1[c],f);
				mesh.setFace(he2[c],f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face,hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j],he2[j]);
				mesh.setNext(hec[j],hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}

	/**
	 * Mid edge split selected faces.
	 *
	 * @param selection
	 *            selection to split
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesMidEdge(final HE_Selection selection, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = selection.getNumberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = selection.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(selection.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(mesh.splitEdges(orig).getVerticesAsArray());
		final HE_Face[] faces = selection.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final int fo = face.getFaceOrder() / 2;
			final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
			int c = 0;
			do {
				textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				mesh.add(f);
				f.copyProperties(face);
				selection.add(f);
				he0[c] = he;
				mesh.setFace(he,f);
				mesh.setHalfedge(f,he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				mesh.setVertex(hec[c],he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c],he2[c]);
				mesh.setFace(hec[c],face);
				mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c],he0[c]);
				mesh.setFace(he1[c],f);
				mesh.setFace(he2[c],f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face,hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j],he2[j]);;
				mesh.setNext(hec[j],hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}

	/**
	 * Mid edge split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesMidEdgeHole(final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = mesh.getNumberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		int i = 0;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(mesh.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(mesh).getVerticesAsArray());
		final HE_Face[] faces = mesh.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final int fo = face.getFaceOrder() / 2;
			final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
			int c = 0;
			do {
				textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				f.copyProperties(face);
				mesh.add(f);
				he0[c] = he;
				mesh.setFace(he,f);
				mesh.setHalfedge(f,he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				mesh.setVertex(hec[c],he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c],he2[c]);
				mesh.setFace(hec[c],face);
				mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c],he0[c]);
				mesh.setFace(he1[c],f);
				mesh.setFace(he2[c],f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face,hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j],he2[j]);
				mesh.setNext(hec[j],hec[(j + 1) % c]);
			}
			mesh.deleteFace(face);
		}
		return selectionOut;
	}

	/**
	 *
	 *
	 * @param selection
	 * @return
	 */
	public static HE_Selection splitFacesMidEdgeHole(final HE_Selection selection, final HE_Mesh mesh) {
		final HE_Selection selectionOut = new HE_Selection(mesh);
		final int n = selection.getNumberOfFaces();
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = selection.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}
		final HE_Selection orig = new HE_Selection(mesh);
		orig.addFaces(selection.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(orig, mesh).getVerticesAsArray());
		final HE_Face[] faces = selection.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;
			final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final int fo = face.getFaceOrder() / 2;
			final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
			int c = 0;
			do {
				textures[c++] = (he.hasUVW()) ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				mesh.add(f);
				f.copyProperties(face);
				selection.add(f);
				he0[c] = he;
				mesh.setFace(he,f);
				mesh.setHalfedge(f,he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				mesh.setVertex(hec[c],he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c],he2[c]);
				mesh.setFace(hec[c],face);
				mesh.setVertex(he2[c],he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c],he0[c]);
				mesh.setFace(he1[c],f);
				mesh.setFace(he2[c],f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face,hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j],he2[j]);
				mesh.setNext(hec[j],hec[(j + 1) % c]);
			}
			mesh.deleteFace(face);
		}
		return selectionOut;
	}

	/**
	 * Quad split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesQuad(final HE_Mesh mesh) {
		final HEM_QuadSplit qs = new HEM_QuadSplit();
		mesh.modify(qs);
		return qs.getSplitFaces();
	}

	/**
	 * Quad split faces.
	 *
	 * @param d
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesQuad(final double d, final HE_Mesh mesh) {
		final HEM_QuadSplit qs = new HEM_QuadSplit().setOffset(d);
		mesh.modify(qs);
		return qs.getSplitFaces();
	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel
	 *            selection to split
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesQuad(final HE_Selection sel, final HE_Mesh mesh) {
		final HEM_QuadSplit qs = new HEM_QuadSplit();
		mesh.modifySelected(qs, sel);
		return qs.getSplitFaces();
	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel
	 *            selection to split
	 * @param d
	 * @return selection of new faces and new vertices
	 */
	public static HE_Selection splitFacesQuad(final HE_Selection sel, final double d, final HE_Mesh mesh) {
		final HEM_QuadSplit qs = new HEM_QuadSplit().setOffset(d);
		mesh.modifySelected(qs, sel);
		return qs.getSplitFaces();
	}

	/**
	 * Tri split faces.
	 *
	 * @return selection of new faces and new vertex
	 */
	public static HE_Selection splitFacesTri(final HE_Mesh mesh) {
		final HEM_TriSplit ts = new HEM_TriSplit();
		mesh.modify(ts);
		return ts.getSplitFaces();
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param d
	 *            offset along face normal
	 * @return selection of new faces and new vertex
	 */
	public static HE_Selection splitFacesTri(final double d, final HE_Mesh mesh) {
		final HEM_TriSplit ts = new HEM_TriSplit().setOffset(d);
		mesh.modify(ts);
		return ts.getSplitFaces();
	}

	/**
	 * Tri split faces.
	 *
	 * @param selection
	 *            face selection to split
	 * @return selection of new faces and new vertex
	 */
	public static HE_Selection splitFacesTri(final HE_Selection selection, final HE_Mesh mesh) {
		final HEM_TriSplit ts = new HEM_TriSplit();
		mesh.modifySelected(ts, selection);
		return ts.getSplitFaces();
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param selection
	 *            face selection to split
	 * @param d
	 *            offset along face normal
	 * @return selection of new faces and new vertex
	 */
	public static HE_Selection splitFacesTri(final HE_Selection selection, final double d, final HE_Mesh mesh) {
		final HEM_TriSplit ts = new HEM_TriSplit().setOffset(d);
		mesh.modifySelected(ts, selection);
		return ts.getSplitFaces();
	}

	/**
	 * Triangulate all faces.
	 *
	 * @return
	 */
	public static HE_Selection triangulate(final HE_Mesh mesh) {
		final HEM_Triangulate tri = new HEM_Triangulate();
		mesh.modify(new HEM_Triangulate());
		return tri.triangles;
	}

	/**
	 *
	 *
	 * @param face
	 * @return
	 */
	public static HE_Selection triangulate(final HE_Face face, final HE_Mesh mesh) {
		final HE_Selection sel = new HE_Selection(mesh);
		sel.add(face);
		return triangulate(sel, mesh);
	}

	/**
	 * Triangulate.
	 *
	 * @param sel
	 *            selection
	 * @return
	 */
	public static HE_Selection triangulate(final HE_Selection sel, final HE_Mesh mesh) {
		final HEM_Triangulate tri = new HEM_Triangulate();
		mesh.modifySelected(tri, sel);
		return tri.triangles;
	}

	/**
	 * Triangulate face.
	 *
	 * @param key
	 *            key of face
	 * @return
	 */
	public static HE_Selection triangulate(final long key, final HE_Mesh mesh) {
		return triangulate(mesh.getFaceWithKey(key), mesh);
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param face
	 *            key of face
	 */
	public static HE_Selection triangulateConcaveFace(final HE_Face face, final HE_Mesh mesh) {
		if (face.getFaceType() == WB_Classification.CONCAVE) {
			return triangulate(face, mesh);
		}
		final HE_Selection sel = new HE_Selection(mesh);
		sel.add(face);
		return sel;
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param key
	 *            key of face
	 */
	public static HE_Selection triangulateConcaveFace(final long key, final HE_Mesh mesh) {
		return triangulateConcaveFace(mesh.getFaceWithKey(key), mesh);
	}

	/**
	 * Triangulate all concave faces.
	 *
	 */
	public static HE_Selection triangulateConcaveFaces(final HE_Mesh mesh) {
		final HE_Selection out = new HE_Selection(mesh);
		final HE_Face[] f = mesh.getFacesAsArray();
		final int n = mesh.getNumberOfFaces();
		for (int i = 0; i < n; i++) {
			if (f[i].getFaceType() == WB_Classification.CONCAVE) {
				out.union(triangulate(f[i].key(), mesh));
			}
		}
		return out;
	}

	/**
	 *
	 *
	 * @param sel
	 */
	public static HE_Selection triangulateConcaveFaces(final List<HE_Face> sel, final HE_Mesh mesh) {
		final HE_Selection out = new HE_Selection(mesh);
		final int n = sel.size();
		for (int i = 0; i < n; i++) {
			if (sel.get(i).getFaceType() == WB_Classification.CONCAVE) {
				out.union(triangulate(sel.get(i).key(), mesh));
			} else {
				out.add(sel.get(i));
			}
		}
		return out;
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public static HE_Selection triangulateFaceStar(final HE_Vertex v, final HE_Mesh mesh) {
		final HE_Selection vf = new HE_Selection(mesh);
		final HE_VertexFaceCirculator vfc = new HE_VertexFaceCirculator(v);
		HE_Face f;
		while (vfc.hasNext()) {
			f = vfc.next();
			if (f != null) {
				if (f.getFaceOrder() > 3) {
					if (!vf.contains(f)) {
						vf.add(f);
					}
				}
			}
		}
		return triangulate(vf, mesh);
	}

	/**
	 *
	 *
	 * @param vertexkey
	 * @return
	 */
	public static HE_Selection triangulateFaceStar(final long vertexkey, final HE_Mesh mesh) {
		final HE_Selection vf = new HE_Selection(mesh);
		final HE_VertexFaceCirculator vfc = new HE_VertexFaceCirculator(mesh.getVertexWithKey(vertexkey));
		HE_Face f;
		while (vfc.hasNext()) {
			f = vfc.next();
			if (f != null) {
				if (f.getFaceOrder() > 3) {
					if (!vf.contains(f)) {
						vf.add(f);
					}
				}
			}
		}
		return triangulate(vf, mesh);
	}

	public static HE_Face createFaceFromHalfedgeLoop(final HE_Halfedge he, final HE_Mesh mesh) {
		if ((mesh == null) || (he == null)) {
			return null;
		}
		if (he.getFace() != null) {
			return null;
		}
		if (!mesh.contains(he)) {
			return null;
		}
		HE_Halfedge hen = he;
		final HE_Face newFace = new HE_Face();
		mesh.setHalfedge(newFace,he);
		do {
			mesh.setFace(hen,newFace);
			hen = hen.getNextInFace();
		} while (hen != he);
		mesh.add(newFace);
		return newFace;
	}

	public List<HE_Face> createFaceFromHalfedgeLoop(final List<HE_Halfedge> hes, final HE_Mesh mesh) {
		final List<HE_Face> newFaces = new ArrayList<HE_Face>();
		if ((mesh == null) || (hes == null)) {
			return newFaces;
		}
		for (final HE_Halfedge he : hes) {
			he.clearVisited();
		}
		for (final HE_Halfedge he : hes) {
			if (he.getFace() != null) {
				continue;
			}
			if (!mesh.contains(he)) {
				continue;
			}
			HE_Halfedge hen = he;
			final HE_Face newFace = new HE_Face();
			mesh.setHalfedge(newFace,he);
			do {
				mesh.setFace(hen,newFace);
				if (hes.contains(hen)) {
					hen.setVisited();
				}
				hen = hen.getNextInFace();
			} while (hen != he);
			mesh.add(newFace);
			newFaces.add(newFace);
		}
		return newFaces;
	}

	public static HE_RAS<HE_Face> selectAllFacesConnectedToUnpairedHalfedge(final HE_Halfedge unpairedHalfedge,
			final HE_Mesh mesh) {

		final HE_RAS<HE_Face> faces = new HE_RASTrove<HE_Face>();
		HE_Face face = unpairedHalfedge.getFace();
		if (face == null) {
			return faces;
		}
		final HE_FaceIterator fitr = mesh.fItr();
		while (fitr.hasNext()) {
			fitr.next().clearVisited();
		}
		final HE_RAS<HE_Face> facesToCheck = new HE_RASTrove<HE_Face>();
		facesToCheck.add(face);
		face.setVisited();
		HE_Halfedge he;
		HE_Face neighbor;
		do {
			face = facesToCheck.get(0);
			facesToCheck.remove(face);
			faces.add(face);
			final HE_FaceHalfedgeInnerCirculator heitr = new HE_FaceHalfedgeInnerCirculator(face);
			while (heitr.hasNext()) {
				he = heitr.next();
				if (he.getPair() != null) {
					neighbor = he.getPair().getFace();
					if ((neighbor != null) && !neighbor.isVisited()) {
						facesToCheck.add(neighbor);
						neighbor.setVisited();
					}
				}
			}
		} while (facesToCheck.size() > 0);
		return faces;
	}


	/**
	 * Reverse all faces. Flips normals.
	 *
	 * @return
	 */
	public static HE_Mesh flipAllFaces(final HE_Mesh mesh) {
		tracker.setStatus("HET_MeshOp", "Flipping faces.", +1);
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfEdges(), 10);
		tracker.setStatus("HET_MeshOp", "Reversing edges.", counter);
		HE_Halfedge he1;
		HE_Halfedge he2;
		HE_Vertex tmp;
		HE_Halfedge[] prevHe;
		HE_TextureCoordinate[] nextHeUVW;
		HE_Halfedge he;
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		while (eItr.hasNext()) {
			he1 = eItr.next();
			he2 = he1.getPair();
			tmp = he1.getVertex();
			mesh.setVertex(he1,he2.getVertex());
			if (he2 != null) {
				mesh.setVertex(he2,tmp);

			}
			mesh.setHalfedge(he1.getVertex(),he1);
			if (he2 != null) {
				mesh.setHalfedge(he2.getVertex(),he2);
			}
			counter.increment();
		}
		prevHe = new HE_Halfedge[mesh.getNumberOfHalfedges()];
		nextHeUVW = new HE_TextureCoordinate[mesh.getNumberOfHalfedges()];
		int i = 0;
		Iterator<HE_Halfedge> heItr = mesh.heItr();
		counter = new WB_ProgressCounter(2 * mesh.getNumberOfHalfedges(), 10);
		tracker.setStatus(mesh, "Reordering halfedges.", counter);
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			nextHeUVW[i] = (he.getNextInFace().hasHalfedgeUVW()) ? he.getNextInFace().getHalfedgeUVW() : null;
			i++;
			counter.increment();
		}
		i = 0;
		heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			mesh.setNext(he,prevHe[i]);
			if (nextHeUVW[i] == null) {
				he.clearUVW();
			} else {
				he.setUVW(nextHeUVW[i]);
			}
			i++;
			counter.increment();
		}

		tracker.setStatus("HET_MeshOp", "Faces flipped.", -1);
		return mesh;
	}

	public static void flipFaces(final HE_Mesh mesh, final HE_RAS<HE_Face> faces) {
		HE_Halfedge he;
		for (final HE_Face face : faces) {
			final int n = face.getFaceOrder();
			final HE_Vertex[] vertices = new HE_Vertex[n];
			final HE_Halfedge[] prevhe = new HE_Halfedge[n];
			final HE_FaceHalfedgeInnerCirculator heitr = new HE_FaceHalfedgeInnerCirculator(face);
			int i = 0;
			while (heitr.hasNext()) {
				he = heitr.next();
				vertices[i] = he.getNextInFace().getVertex();
				prevhe[i++] = he.getPrevInFace();
			}
			i = 0;
			while (heitr.hasNext()) {
				he = heitr.next();
				mesh.setVertex(he,vertices[i]);
				mesh.setHalfedge(vertices[i],he);
				mesh.setNext(he,prevhe[i]);
			}
		}
	}


	/**
	 *
	 *
	 * @param he
	 * @return
	 */
	public static boolean flipEdge(final HE_Mesh mesh,final HE_Halfedge he) {
		// boundary edge
		if (he.getFace() == null) {
			return false;
		}
		// not a triangle
		if (he.getFace().getFaceOrder() != 3) {
			return false;
		}
		// unpaired edge
		if (he.getPair() == null) {
			return false;
		}
		// boundary edge
		if (he.getPair().getFace() == null) {
			return false;
		}
		// not a triangle
		if (he.getPair().getFace().getFaceOrder() != 3) {
			return false;
		}
		// flip would result in overlapping triangles, this detected by
		// comparing the areas of the two triangles before and after.
		WB_Plane P=new WB_Plane(he.getHalfedgeCenter(),he.getEdgeNormal());
		final WB_Coord a =WB_GeometryOp.projectOnPlane(he.getVertex(),P);
		final WB_Coord b = WB_GeometryOp.projectOnPlane(he.getNextInFace().getVertex(),P);
		final WB_Coord c = WB_GeometryOp.projectOnPlane(he.getNextInFace().getNextInFace().getVertex(),P);
		final WB_Coord d = WB_GeometryOp.projectOnPlane(he.getPair().getNextInFace().getNextInFace().getVertex(),P);
		double Ai =WB_Triangle.getArea(a, b, c);
		Ai += WB_Triangle.getArea(a, d, b);
		double Af = WB_Triangle.getArea(a, d, c);
		Af += WB_Triangle.getArea(c, d, b);
		final double ratio = Ai / Af;
		if ((ratio > 1.000001) || (ratio < 0.99999)) {
			return false;
		}
		// get the 3 edges of triangle t1 and t2, he1t1 and he1t2 is the edge to
		// be flipped
		final HE_Halfedge he1t1 = he;
		final HE_Halfedge he1t2 = he.getPair();
		final HE_Halfedge he2t1 = he1t1.getNextInFace();
		final HE_Halfedge he2t2 = he1t2.getNextInFace();
		final HE_Halfedge he3t1 = he2t1.getNextInFace();
		final HE_Halfedge he3t2 = he2t2.getNextInFace();
		final HE_Face t1 = he1t1.getFace();
		final HE_Face t2 = he1t2.getFace();
		// Fix vertex assignment
		// First make sure the original vertices get assigned another halfedge
		mesh.setHalfedge(he1t1.getVertex(),he2t2);
		mesh.setHalfedge(he1t2.getVertex(),he2t1);
		// Now assign the new vertices to the flipped edges
		mesh.setVertex(he1t1,he3t1.getVertex());
		mesh.setVertex(he1t2,he3t2.getVertex());
		// Reconstruct triangle t1
		mesh.setNext(he2t1,he1t1);
		mesh.setNext(he1t1,he3t2);
		mesh.setNext(he3t2,he2t1);
		mesh.setFace(he3t2,t1);
		mesh.setHalfedge(t1,he1t1);
		// reconstruct triangle t2
		mesh.setNext(he2t2,he1t2);
		mesh.setNext(he1t2,he3t1);
		mesh.setNext(he3t1,he2t2);
		mesh.setFace(he3t1,t2);
		mesh.setHalfedge(t2,he1t2);
		return true;
	}



	/**
	 * Clean all mesh elements not used by any faces.
	 *
	 * @return self
	 */
	public static HE_Mesh cleanUnusedElementsByFace(final HE_Mesh mesh) {
		final HE_RAS<HE_Vertex> cleanedVertices = new HE_RASTrove<HE_Vertex>();
		final HE_RAS<HE_Halfedge> cleanedHalfedges = new HE_RASTrove<HE_Halfedge>();
		tracker.setStatus("HET_MeshOp", "Cleaning unused elements.", +1);
		HE_Halfedge he;
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
		tracker.setStatus("HET_MeshOp", "Processing faces.", counter);
		HE_Face f;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				if (!cleanedVertices.contains(he.getVertex())) {
					cleanedVertices.add(he.getVertex());
					mesh.setHalfedge(he.getVertex(),he);
				}
				if (!cleanedHalfedges.contains(he)) {
					cleanedHalfedges.add(he);
				}
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
			counter.increment();
		}
		counter = new WB_ProgressCounter(cleanedHalfedges.size(), 10);
		tracker.setStatus("HET_MeshOp", "Processing halfedges.", counter);
		final int n = cleanedHalfedges.size();
		for (int i = 0; i < n; i++) {
			he = cleanedHalfedges.get(i);
			if (!cleanedHalfedges.contains(he.getPair())) {
				mesh.clearPair(he);
				mesh.setHalfedge(he.getVertex(),he);
			}
			counter.increment();
		}
		mesh.replaceVertices(cleanedVertices.getObjects());
		mesh.replaceHalfedges(cleanedHalfedges.getObjects());
		tracker.setStatus("HET_MeshOp", "Done cleaning unused elements.", -1);
		return mesh;
	}

	/**
	 * Assign face to halfedge loop.
	 *
	 * @param face
	 *            face
	 * @param halfedge
	 *            halfedge loop
	 */
	public static void assignFaceToLoop(final HE_Mesh mesh, final HE_Face face, final HE_Halfedge halfedge) {
		HE_Halfedge he = halfedge;
		do {
			mesh.setFace(he,face);
			he = he.getNextInFace();
		} while (he != halfedge);
	}
}
