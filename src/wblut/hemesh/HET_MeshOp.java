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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.core.WB_ProgressTracker;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordinateSystem;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

public class HET_MeshOp {
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
	public static final WB_ProgressTracker tracker = WB_ProgressTracker.instance();

	/**
	 * Split edge in half.
	 *
	 * @param edge
	 *            edge to split.
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
			if (fi > 0 && fi < 1) {
				v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
				e = splitEdge(e, v, mesh).eItr().next();
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
	 * @param mesh
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
			if (fi > 0 && fi < 1) {
				v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
				e = splitEdge(e, v, mesh).eItr().next();
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
	 * @param mesh
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
		mesh.setVertex(he0new, vNew);
		mesh.setVertex(he1new, vNew);
		mesh.setHalfedge(vNew, he0new);
		mesh.setNext(he0new, he0n);
		he0new.copyProperties(he0);
		mesh.setNext(he1new, he1n);
		he1new.copyProperties(he1);
		if (he0.hasUVW() && he0n.hasUVW()) {
			he0new.setUVW(new HE_TextureCoordinate(f0, he0.getUVW(), he0n.getUVW()));
		}
		if (he1.hasUVW() && he1n.hasUVW()) {
			he1new.setUVW(new HE_TextureCoordinate(f1, he1.getUVW(), he1n.getUVW()));
		}
		mesh.setNext(he0, he0new);
		mesh.setNext(he1, he1new);
		mesh.setPair(he0, he1new);
		mesh.setPair(he0new, he1);

		if (he0.getFace() != null) {
			mesh.setFace(he0new, he0.getFace());
		}
		if (he1.getFace() != null) {
			mesh.setFace(he1new, he1.getFace());
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
	 * @return selection of new vertex and new edge
	 */
	public static HE_Selection splitEdge(final Long key, final WB_Point v, final HE_Mesh mesh) {
		final HE_Halfedge edge = mesh.getHalfedgeWithKey(key);
		return splitEdge(edge, v, mesh);
	}

	/**
	 * Split all edges in half.
	 *
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
	 */
	public static void divideEdge(final HE_Halfedge origE, final int n, final HE_Mesh mesh) {
		if (n > 1) {
			final double[] f = new double[n - 1];
			final double in = 1.0 / n;
			for (int i = 0; i < n - 1; i++) {
				f[i] = (i + 1) * in;
			}
			splitEdge(origE, f, mesh);
		}
	}

	/**
	 *
	 *
	 * @param key
	 * @param n
	 * @param mesh
	 */
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
	 * @param mesh
	 * @return new face and edge
	 */
	public static HE_Selection splitFace(final HE_Face face, final HE_Vertex vi, final HE_Vertex vj,
			final HE_Mesh mesh) {
		final HE_Selection out = new HE_Selection(mesh);
		final HE_Halfedge hei = vi.getHalfedge(face);
		final HE_Halfedge hej = vj.getHalfedge(face);
		final HE_TextureCoordinate ti = hei.hasUVW() ? hei.getUVW() : null;
		final HE_TextureCoordinate tj = hej.hasUVW() ? hej.getUVW() : null;
		final double d = vi.getDistance3D(vj);
		boolean degenerate = false;
		if (WB_Epsilon.isZero(d)) {// happens when a collinear (part of a) face
			// is cut. Do not add a new edge connecting
			// these two points,rather collapse them into
			// each other and remove two-edge faces
			degenerate = true;
		}
		if (hei.getNextInFace() != hej || hei.getPrevInFace() != hej) {
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
				mesh.setVertex(he0new, vj);
				if (tj != null) {
					he0new.setUVW(tj);
				}
				mesh.setVertex(he1new, vi);
				if (ti != null) {
					he1new.setUVW(ti);
				}
				mesh.setNext(he0new, hei);
				mesh.setNext(he1new, hej);
				mesh.setNext(heiPrev, he1new);
				mesh.setNext(hejPrev, he0new);
				mesh.setPair(he0new, he1new);
				he0new.setInternalLabel(1);
				he1new.setInternalLabel(1);
				mesh.setFace(he0new, face);
				faceNew = new HE_Face();
				mesh.setHalfedge(face, hei);
				mesh.setHalfedge(faceNew, hej);
				faceNew.copyProperties(face);
				assignFaceToLoop(mesh, faceNew, hej);
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
					mesh.setVertex(hejs, vi);
				}
				mesh.setNext(heiPrev, hej);
				mesh.setNext(hejPrev, hei);
				faceNew = new HE_Face();
				mesh.setHalfedge(face, hei);
				mesh.setHalfedge(faceNew, hej);
				faceNew.copyProperties(face);
				assignFaceToLoop(mesh, faceNew, hej);
				mesh.add(faceNew);
				mesh.remove(vj);
				out.add(faceNew);
				if (face.getFaceOrder() == 2) {
					HET_Fixer.deleteTwoEdgeFace(mesh, face);
				}
				if (faceNew.getFaceOrder() == 2) {
					HET_Fixer.deleteTwoEdgeFace(mesh, faceNew);
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
	 * @param mesh
	 * @return new face and edge
	 */
	public static HE_Selection splitFace(final long fkey, final long vkeyi, final long vkeyj, final HE_Mesh mesh) {
		return splitFace(mesh.getFaceWithKey(fkey), mesh.getVertexWithKey(vkeyi), mesh.getVertexWithKey(vkeyj), mesh);
	}

	/**
	 *
	 *
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
					textures[c++] = he.hasUVW() ? he.getUVW() : null;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				c = 0;
				do {
					final HE_Face fn = new HE_Face();
					fn.copyProperties(f);
					mesh.add(fn);
					he0[c] = he;
					mesh.setFace(he, fn);
					mesh.setHalfedge(fn, he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();

					mesh.setVertex(hec[c], he.getVertex());
					if (textures[c] != null) {
						hec[c].setUVW(textures[c]);
					}
					mesh.setPair(hec[c], he2[c]);

					mesh.setFace(hec[c], f);
					mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
					if (textures[(c + 1) % fo] != null) {
						he2[c].setUVW(textures[(c + 1) % fo]);
					}
					mesh.setNext(he2[c], he0[c]);
					mesh.setFace(he1[c], fn);
					mesh.setFace(he2[c], fn);
					mesh.add(he2[c]);
					mesh.add(hec[c]);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(f, hec[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j], he2[j]);
					mesh.setNext(hec[j], hec[(j + 1) % c]);
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
					mesh.setFace(he, fc);
					mesh.setHalfedge(fc, he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(he3[c]);
					mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
					if (he2[c].getVertex().hasHalfedgeUVW(f)) {
						he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
					}
					mesh.setVertex(he3[c], vi);
					mesh.setNext(he2[c], he3[c]);
					mesh.setNext(he3[c], he);
					mesh.setFace(he1[c], fc);
					mesh.setFace(he2[c], fc);
					mesh.setFace(he3[c], fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(vi, he3[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j], he2[j]);
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
	 * @param mesh
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
					textures[c++] = he.hasUVW() ? he.getUVW() : null;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				c = 0;
				do {
					final HE_Face fn = new HE_Face();
					fn.copyProperties(f);
					mesh.add(fn);
					sel.add(fn);
					he0[c] = he;
					mesh.setFace(he, fn);
					mesh.setHalfedge(fn, he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					hec[c] = new HE_Halfedge();

					mesh.setVertex(hec[c], he.getVertex());
					if (textures[c] != null) {
						hec[c].setUVW(textures[c]);
					}
					mesh.setPair(hec[c], he2[c]);

					mesh.setFace(hec[c], f);
					mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
					if (textures[(c + 1) % fo] != null) {
						he2[c].setUVW(textures[(c + 1) % fo]);
					}
					mesh.setNext(he2[c], he0[c]);
					mesh.setFace(he1[c], fn);
					mesh.setFace(he2[c], fn);
					mesh.add(he2[c]);
					mesh.add(hec[c]);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(f, hec[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j], he2[j]);
					mesh.setNext(hec[j], hec[(j + 1) % c]);
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
					mesh.setFace(he, fc);
					mesh.setHalfedge(fc, he);
					he1[c] = he.getNextInFace();
					he2[c] = new HE_Halfedge();
					he3[c] = new HE_Halfedge();
					mesh.add(he2[c]);
					mesh.add(he3[c]);
					mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
					if (he2[c].getVertex().hasHalfedgeUVW(f)) {
						he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
					}
					mesh.setVertex(he3[c], vi);
					mesh.setNext(he2[c], he3[c]);
					mesh.setNext(he3[c], he);
					mesh.setFace(he1[c], fc);
					mesh.setFace(he2[c], fc);
					mesh.setFace(he3[c], fc);
					c++;
					he = he.getNextInFace().getNextInFace();
				} while (he != startHE);
				mesh.setHalfedge(vi, he3[0]);
				for (int j = 0; j < c; j++) {
					mesh.setNext(he1[j], he2[j]);
				}
			}
		}
		mesh.pairHalfedges();
		return selectionOut;
	}

	/**
	 * Midedge split faces.
	 *
	 * @param mesh
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
				textures[c++] = he.hasUVW() ? he.getUVW() : null;
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

				mesh.setVertex(hec[c], he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c], he2[c]);
				mesh.setFace(hec[c], face);
				mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c], he0[c]);
				mesh.setFace(he0[c], f);
				mesh.setHalfedge(f, he0[c]);
				mesh.setFace(he1[c], f);
				mesh.setFace(he2[c], f);
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face, hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j], he2[j]);
				mesh.setNext(hec[j], hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}

	/**
	 * Mid edge split selected faces.
	 *
	 * @param selection
	 *            selection to split
	 * @param mesh
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
				textures[c++] = he.hasUVW() ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				mesh.add(f);
				f.copyProperties(face);
				selection.add(f);
				he0[c] = he;
				mesh.setFace(he, f);
				mesh.setHalfedge(f, he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();

				mesh.setVertex(hec[c], he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c], he2[c]);
				mesh.setFace(hec[c], face);
				mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c], he0[c]);
				mesh.setFace(he1[c], f);
				mesh.setFace(he2[c], f);
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face, hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j], he2[j]);
				;
				mesh.setNext(hec[j], hec[(j + 1) % c]);
			}
		}
		return selectionOut;
	}

	/**
	 * Mid edge split faces.
	 *
	 * @param mesh
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
				textures[c++] = he.hasUVW() ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				f.copyProperties(face);
				mesh.add(f);
				he0[c] = he;
				mesh.setFace(he, f);
				mesh.setHalfedge(f, he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();

				mesh.setVertex(hec[c], he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c], he2[c]);
				mesh.setFace(hec[c], face);
				mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c], he0[c]);
				mesh.setFace(he1[c], f);
				mesh.setFace(he2[c], f);
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face, hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j], he2[j]);
				mesh.setNext(hec[j], hec[(j + 1) % c]);
			}
			mesh.deleteFace(face);
		}
		return selectionOut;
	}

	/**
	 *
	 *
	 * @param selection
	 * @param mesh
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
				textures[c++] = he.hasUVW() ? he.getUVW() : null;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			c = 0;
			do {
				final HE_Face f = new HE_Face();
				mesh.add(f);
				f.copyProperties(face);
				selection.add(f);
				he0[c] = he;
				mesh.setFace(he, f);
				mesh.setHalfedge(f, he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				hec[c] = new HE_Halfedge();

				mesh.setVertex(hec[c], he.getVertex());
				if (textures[c] != null) {
					hec[c].setUVW(textures[c]);
				}
				mesh.setPair(hec[c], he2[c]);
				mesh.setFace(hec[c], face);
				mesh.setVertex(he2[c], he.getNextInFace().getNextInFace().getVertex());
				if (textures[(c + 1) % fo] != null) {
					he2[c].setUVW(textures[(c + 1) % fo]);
				}
				mesh.setNext(he2[c], he0[c]);
				mesh.setFace(he1[c], f);
				mesh.setFace(he2[c], f);
				mesh.add(he2[c]);
				mesh.add(hec[c]);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			mesh.setHalfedge(face, hec[0]);
			for (int j = 0; j < c; j++) {
				mesh.setNext(he1[j], he2[j]);
				mesh.setNext(hec[j], hec[(j + 1) % c]);
			}
			mesh.deleteFace(face);
		}
		return selectionOut;
	}

	/**
	 * Quad split faces.
	 *
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
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
	 * @param mesh
	 * @return
	 */
	public static HE_Selection triangulate(final HE_Mesh mesh) {
		final HEM_TriangulateMT tri = new HEM_TriangulateMT();
		mesh.modify(tri);
		return tri.triangles;
	}

	/**
	 *
	 *
	 * @param face
	 * @param mesh
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
	 * @param mesh
	 * @return
	 */
	public static HE_Selection triangulate(final HE_Selection sel, final HE_Mesh mesh) {
		final HEM_TriangulateMT tri = new HEM_TriangulateMT();
		mesh.modifySelected(tri, sel);
		return tri.triangles;
	}

	/**
	 * Triangulate face.
	 *
	 * @param key
	 *            key of face
	 * @param mesh
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
	 * @param mesh
	 * @return
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
	 * @param mesh
	 * @return
	 */
	public static HE_Selection triangulateConcaveFace(final long key, final HE_Mesh mesh) {
		return triangulateConcaveFace(mesh.getFaceWithKey(key), mesh);
	}

	/**
	 * Triangulate all concave faces.
	 *
	 * @param mesh
	 * @return
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
	 * @param mesh
	 * @return
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
	 * @param mesh
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
	 * @param mesh
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

	/**
	 *
	 *
	 * @param he
	 * @param mesh
	 * @return
	 */
	public static HE_Face createFaceFromHalfedgeLoop(final HE_Halfedge he, final HE_Mesh mesh) {
		if (mesh == null || he == null) {
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
		mesh.setHalfedge(newFace, he);
		do {
			mesh.setFace(hen, newFace);
			hen = hen.getNextInFace();
		} while (hen != he);
		mesh.add(newFace);
		return newFace;
	}

	/**
	 *
	 *
	 * @param hes
	 * @param mesh
	 * @return
	 */
	public static List<HE_Face> createFaceFromHalfedgeLoop(final List<HE_Halfedge> hes, final HE_Mesh mesh) {
		final List<HE_Face> newFaces = new ArrayList<HE_Face>();
		if (mesh == null || hes == null) {
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
			mesh.setHalfedge(newFace, he);
			do {
				mesh.setFace(hen, newFace);
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

	/**
	 *
	 *
	 * @param unpairedHalfedge
	 * @param mesh
	 * @return
	 */
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
					if (neighbor != null && !neighbor.isVisited()) {
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
	 * @param mesh
	 * @return
	 */
	public static HE_Mesh flipFaces(final HE_Mesh mesh) {
		tracker.setStatus("HET_MeshOp", "Flipping faces.", +1);
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfEdges(), 10);
		tracker.setStatus("HET_MeshOp", "Reversing edges.", counter);
		HE_Halfedge he1;
		HE_Halfedge he2;
		HE_Vertex tmp;
		HE_Halfedge[] prevHe;
		HE_TextureCoordinate[] nextHeUVW;
		HE_Halfedge he;
		mesh.clearVisitedElements();
		prevHe = new HE_Halfedge[mesh.getNumberOfHalfedges()];
		nextHeUVW = new HE_TextureCoordinate[mesh.getNumberOfHalfedges()];
		int i = 0;
		HE_HalfedgeIterator heItr = mesh.heItr();
		counter = new WB_ProgressCounter(2 * mesh.getNumberOfHalfedges(), 10);
		tracker.setStatus(mesh, "Reordering halfedges.", counter);
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			nextHeUVW[i] = he.getNextInFace().hasHalfedgeUVW() ? he.getNextInFace().getHalfedgeUVW() : null;
			i++;
			counter.increment();
		}
		i = 0;
		heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			mesh.setNext(he, prevHe[i]);
			if (nextHeUVW[i] == null) {
				he.clearUVW();
			} else {
				he.setUVW(nextHeUVW[i]);
			}
			i++;
			counter.increment();
		}
		counter = new WB_ProgressCounter(2 * mesh.getNumberOfEdges(), 10);
		tracker.setStatus(mesh, "Flipping edges.", counter);

		final HE_EdgeIterator eItr = mesh.eItr();
		while (eItr.hasNext()) {
			he1 = eItr.next();
			he2 = he1.getPair();
			tmp = he1.getVertex();
			mesh.setVertex(he1, he2.getVertex());

			mesh.setVertex(he2, tmp);

			mesh.setHalfedge(he1.getVertex(), he1);

			mesh.setHalfedge(he2.getVertex(), he2);

			counter.increment();
		}

		tracker.setStatus("HET_MeshOp", "Faces flipped.", -1);
		return mesh;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param faces
	 */
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
				mesh.setVertex(he, vertices[i]);
				mesh.setHalfedge(vertices[i], he);
				mesh.setNext(he, prevhe[i]);
			}
		}
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param he
	 * @return
	 */
	public static boolean flipEdge(final HE_Mesh mesh, final HE_Halfedge he) {
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
		WB_Plane P = new WB_Plane(he.getHalfedgeCenter(), he.getEdgeNormal());
		final WB_Coord a = WB_GeometryOp.projectOnPlane(he.getVertex(), P);
		final WB_Coord b = WB_GeometryOp.projectOnPlane(he.getNextInFace().getVertex(), P);
		final WB_Coord c = WB_GeometryOp.projectOnPlane(he.getNextInFace().getNextInFace().getVertex(), P);
		final WB_Coord d = WB_GeometryOp.projectOnPlane(he.getPair().getNextInFace().getNextInFace().getVertex(), P);
		double Ai = WB_Triangle.getArea(a, b, c);
		Ai += WB_Triangle.getArea(a, d, b);
		double Af = WB_Triangle.getArea(a, d, c);
		Af += WB_Triangle.getArea(c, d, b);
		final double ratio = Ai / Af;
		if (ratio > 1.000001 || ratio < 0.99999) {
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
		mesh.setHalfedge(he1t1.getVertex(), he2t2);
		mesh.setHalfedge(he1t2.getVertex(), he2t1);
		// Now assign the new vertices to the flipped edges
		mesh.setVertex(he1t1, he3t1.getVertex());
		mesh.setVertex(he1t2, he3t2.getVertex());
		// Reconstruct triangle t1
		mesh.setNext(he2t1, he1t1);
		mesh.setNext(he1t1, he3t2);
		mesh.setNext(he3t2, he2t1);
		mesh.setFace(he3t2, t1);
		mesh.setHalfedge(t1, he1t1);
		// reconstruct triangle t2
		mesh.setNext(he2t2, he1t2);
		mesh.setNext(he1t2, he3t1);
		mesh.setNext(he3t1, he2t2);
		mesh.setFace(he3t1, t2);
		mesh.setHalfedge(t2, he1t2);
		return true;
	}

	/**
	 * Clean all mesh elements not used by any faces.
	 *
	 * @param mesh
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
					mesh.setHalfedge(he.getVertex(), he);
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
				mesh.setHalfedge(he.getVertex(), he);
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
	 * @param mesh
	 * @param face
	 *            face
	 * @param halfedge
	 *            halfedge loop
	 */
	public static void assignFaceToLoop(final HE_Mesh mesh, final HE_Face face, final HE_Halfedge halfedge) {
		HE_Halfedge he = halfedge;
		do {
			mesh.setFace(he, face);
			he = he.getNextInFace();
		} while (he != halfedge);
	}

	/**
	 *
	 *
	 * @param face
	 * @param line
	 * @return
	 */
	public static HE_FaceIntersection getIntersection(final HE_Face face, final WB_Line line) {
		final WB_Plane P = face.getPlane();
		HE_FaceIntersection p = null;
		final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(line, P);
		if (lpi.intersection) {
			p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
			if (WB_Epsilon.isZero(WB_GeometryOp.getDistanceToClosestPoint3D(p.point, face.toPolygon()))) {
				return p;
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param face
	 * @param ray
	 * @return
	 */
	public static HE_FaceIntersection getIntersection(final HE_Face face, final WB_Ray ray) {
		final WB_Plane P = face.getPlane();
		HE_FaceIntersection p = null;
		final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(ray, P);
		if (lpi.intersection) {
			p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
			if (WB_Epsilon.isZero(WB_GeometryOp.getDistanceToClosestPoint3D(p.point, face.toPolygon()))) {
				return new HE_FaceIntersection(face, p.point);
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param face
	 * @param segment
	 * @return
	 */
	public static HE_FaceIntersection getIntersection(final HE_Face face, final WB_Segment segment) {
		final WB_Plane P = face.getPlane();
		HE_FaceIntersection p = null;
		final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(segment, P);
		if (lpi.intersection) {
			p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
			if (WB_Epsilon.isZero(WB_GeometryOp.getDistanceToClosestPoint3D(p.point, face.toPolygon()))) {
				return p;
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param e
	 * @param P
	 * @return
	 */
	public static double getIntersection(final HE_Halfedge e, final WB_Plane P) {
		final WB_IntersectionResult i = WB_GeometryOp.getIntersection3D(e.getStartVertex(), e.getEndVertex(), P);
		if (i.intersection == false) {
			return -1.0;// intersection beyond endpoints
		}
		return i.t1;// intersection on edge
	}

	/**
	 *
	 *
	 * @param tree
	 * @param ray
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final WB_AABBTree tree, final WB_Ray ray) {
		final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, ray);
			if (sect != null) {
				p.add(sect);
			}
		}
		return p;
	}

	public static boolean contains(final HE_Mesh mesh, final WB_Coord p) {

		return contains(new WB_AABBTree(mesh, 1), p);
	}

	public static boolean contains(final WB_AABBTree tree, final WB_Coord p) {
		final List<HE_FaceIntersection> ints = new FastTable<HE_FaceIntersection>();
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final WB_Vector dir = new WB_Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
		final WB_Ray R = new WB_Ray(p, dir);
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(R, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, R);
			if (sect != null) {
				ints.add(sect);
			}
		}
		return ints.size() % 2 == 1;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param segment
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final WB_AABBTree tree, final WB_Segment segment) {
		final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, segment);
			if (sect != null) {
				p.add(sect);
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param line
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final WB_AABBTree tree, final WB_Line line) {
		final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, line);
			if (sect != null) {
				p.add(sect);
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param P
	 * @return
	 */
	public static List<WB_Segment> getIntersection(final WB_AABBTree tree, final WB_Plane P) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(P, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		final List<WB_Segment> cuts = new FastTable<WB_Segment>();
		for (final HE_Face face : candidates) {
			cuts.addAll(WB_GeometryOp.getIntersection3D(face.toPolygon(), P));
		}
		return cuts;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param P
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final WB_AABBTree tree, final WB_Plane P) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(P, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		return candidates;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param T
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final WB_AABBTree tree, final WB_Triangle T) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(T, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		return candidates;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param R
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final WB_AABBTree tree, final WB_Ray R) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(R, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		return candidates;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param L
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final WB_AABBTree tree, final WB_Line L) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(L, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		return candidates;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param segment
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final WB_AABBTree tree, final WB_Segment segment) {
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		return candidates;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param ray
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final WB_AABBTree tree, final WB_Ray ray) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, ray);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(ray.getOrigin());
				if (d2 < d2min) {
					p = sect;
					d2min = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param ray
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final WB_AABBTree tree, final WB_Ray ray) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, ray);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(ray.getOrigin());
				if (d2 > d2max) {
					p = sect;
					d2max = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param line
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final WB_AABBTree tree, final WB_Line line) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, line);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(line.getOrigin());
				if (d2 < d2min) {
					p = sect;
					d2min = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param line
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final WB_AABBTree tree, final WB_Line line) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, line);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(line.getOrigin());
				if (d2 > d2max) {
					p = sect;
					d2max = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param segment
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final WB_AABBTree tree, final WB_Segment segment) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2min = Double.POSITIVE_INFINITY;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, segment);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(segment.getOrigin());
				if (d2 < d2min) {
					p = sect;
					d2min = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param tree
	 * @param segment
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final WB_AABBTree tree, final WB_Segment segment) {
		HE_FaceIntersection p = null;
		final List<HE_Face> candidates = new FastTable<HE_Face>();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(segment, tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		double d2, d2max = -1;
		for (final HE_Face face : candidates) {
			final HE_FaceIntersection sect = getIntersection(face, segment);
			if (sect != null) {
				d2 = sect.point.getSqDistance3D(segment.getOrigin());
				if (d2 > d2max) {
					p = sect;
					d2max = d2;
				}
			}
		}
		return p;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param ray
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh, final WB_Ray ray) {
		return getIntersection(new WB_AABBTree(mesh, 10), ray);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param segment
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh, final WB_Segment segment) {
		return getIntersection(new WB_AABBTree(mesh, 10), segment);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param line
	 * @return
	 */
	public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh, final WB_Line line) {
		return getIntersection(new WB_AABBTree(mesh, 10), line);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param P
	 * @return
	 */
	public static List<WB_Segment> getIntersection(final HE_Mesh mesh, final WB_Plane P) {
		return getIntersection(new WB_AABBTree(mesh, 10), P);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param P
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final HE_Mesh mesh, final WB_Plane P) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), P);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param R
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final HE_Mesh mesh, final WB_Ray R) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), R);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param L
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final HE_Mesh mesh, final WB_Line L) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), L);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param segment
	 * @return
	 */
	public static List<HE_Face> getPotentialIntersectedFaces(final HE_Mesh mesh, final WB_Segment segment) {
		return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), segment);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param ray
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final HE_Mesh mesh, final WB_Ray ray) {
		return getClosestIntersection(new WB_AABBTree(mesh, 10), ray);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param ray
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final HE_Mesh mesh, final WB_Ray ray) {
		return getFurthestIntersection(new WB_AABBTree(mesh, 10), ray);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param line
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final HE_Mesh mesh, final WB_Line line) {
		return getClosestIntersection(new WB_AABBTree(mesh, 10), line);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param line
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final HE_Mesh mesh, final WB_Line line) {
		return getFurthestIntersection(new WB_AABBTree(mesh, 10), line);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param segment
	 * @return
	 */
	public static HE_FaceIntersection getClosestIntersection(final HE_Mesh mesh, final WB_Segment segment) {
		return getClosestIntersection(new WB_AABBTree(mesh, 10), segment);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param segment
	 * @return
	 */
	public static HE_FaceIntersection getFurthestIntersection(final HE_Mesh mesh, final WB_Segment segment) {
		return getFurthestIntersection(new WB_AABBTree(mesh, 10), segment);
	}

	public static WB_Classification classifyFaceToPlane3D(final HE_Face f, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;
		HE_Halfedge he = f.getHalfedge();
		do {
			switch (WB_GeometryOp.classifyPointToPlane3D(P, he.getVertex())) {
			case FRONT:
				numInFront++;
				break;
			case BACK:
				numBehind++;
				break;
			default:
			}
			if (numBehind != 0 && numInFront != 0) {
				return WB_Classification.CROSSING;
			}
			he = he.getNextInFace();
		} while (he != f.getHalfedge());
		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	public static WB_Classification classifyEdgeToPlane3D(final HE_Halfedge edge, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;

		switch (WB_GeometryOp.classifyPointToPlane3D(edge.getStartVertex(), P)) {
		case FRONT:
			numInFront++;
			break;
		case BACK:
			numBehind++;
			break;
		default:
		}
		switch (WB_GeometryOp.classifyPointToPlane3D(edge.getEndVertex(), P)) {
		case FRONT:
			numInFront++;
			break;
		case BACK:
			numBehind++;
			break;
		default:
		}

		if (numBehind != 0 && numInFront != 0) {
			return WB_Classification.CROSSING;
		}

		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	public static WB_Classification classifyVertexToPlane3D(final HE_Vertex v, final WB_Plane P) {
		return WB_GeometryOp.classifyPointToPlane3D(v, P);

	}

	public static WB_Classification getVertexType(final HE_Vertex vertex) {
		HE_Halfedge he = vertex.getHalfedge();
		if (he == null) {
			return WB_Classification.UNKNOWN;
		}

		int nconcave = 0;
		int nconvex = 0;
		int nflat = 0;
		do {
			HE_Face f = he.getFace();
			if (f == null) {
				f = he.getPair().getFace();
			}
			final WB_Point v = new WB_Point(he.getNextInFace().getVertex());
			v.subSelf(he.getVertex());
			he = he.getNextInVertex();
			HE_Face fn = he.getFace();
			if (fn == null) {
				fn = he.getPair().getFace();
			}
			final WB_Vector c = WB_Vector.cross(f.getFaceNormal(), fn.getFaceNormal());
			final double d = v.dot(c);
			if (Math.abs(d) < WB_Epsilon.EPSILON) {
				nflat++;
			} else if (d < 0) {
				nconcave++;
			} else {
				nconvex++;
			}
		} while (he != vertex.getHalfedge());
		if (nconcave > 0) {
			if (nconvex > 0) {
				return WB_Classification.SADDLE;
			} else {
				if (nflat > 0) {
					return WB_Classification.FLATCONCAVE;
				} else {
					return WB_Classification.CONCAVE;
				}
			}
		} else if (nconvex > 0) {
			if (nflat > 0) {
				return WB_Classification.FLATCONVEX;
			} else {
				return WB_Classification.CONVEX;
			}
		}
		return WB_Classification.FLAT;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_CoordinateSystem getVertexCS(final HE_Vertex v) {
		WB_Coord vn = getVertexNormal(v);

		final WB_Vector normal = vn == null ? null : new WB_Vector(getVertexNormal(v));
		if (normal == null) {
			return null;
		}
		WB_Vector t2 = new WB_Vector();
		if (Math.abs(normal.xd()) < Math.abs(normal.yd())) {
			t2.setX(1.0);
		} else {
			t2.setY(1.0);
		}
		final WB_Vector t1 = normal.cross(t2);
		final double n = t1.getLength3D();
		if (n < WB_Epsilon.EPSILON) {
			return null;
		}
		t1.mulSelf(1.0 / n);
		t2 = normal.cross(t1);
		return gf.createCSFromOXYZ(v, t1, t2, normal);
	}

	// Common area-weighted mean normal
	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord getVertexNormal(final HE_Vertex v) {
		if (v.getHalfedge() == null) {
			return null;
		}

		return getVertexAngleNormal(v);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord getVertexAverageNormal(final HE_Vertex v) {
		WB_Vector normal = new WB_Vector();
		final WB_Vector[] temp = new WB_Vector[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = new WB_Vector();
		}
		HE_Halfedge he = v.getHalfedge();
		final HE_Vertex d = he.getEndVertex();
		do {
			he = he.getNextInVertex();
			if (he.getFace() == null) {
				continue;
			}
			final double area = computeNormal3D(v, he.getEndVertex(), he.getPrevInFace().getVertex(), temp[0], temp[1],
					temp[2]);
			normal.addMulSelf(area, temp[2]);
		} while (he.getEndVertex() != d);
		final double n = normal.getLength3D();
		normal.mulSelf(1.0 / n);
		return normal;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord getVertexAreaNormal(final HE_Vertex v) {
		WB_Vector normal = new WB_Vector();
		final WB_Vector[] temp = new WB_Vector[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = new WB_Vector();
		}
		HE_Halfedge he = v.getHalfedge();
		final HE_Vertex d = he.getEndVertex();
		do {
			he = he.getNextInVertex();
			if (he.getFace() == null) {
				continue;
			}
			final double area = computeNormal3D(v, he.getEndVertex(), he.getPrevInFace().getVertex(), temp[0], temp[1],
					temp[2]);
			normal.addMulSelf(area, temp[2]);
		} while (he.getEndVertex() != d);
		final double n = normal.getLength3D();
		normal.mulSelf(1.0 / n);
		return normal;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord getVertexAngleNormal(final HE_Vertex vertex) {
		HE_Halfedge he = vertex.getHalfedge();
		WB_Vector v = new WB_Vector();
		do {
			if (he.getFace() != null) {
				v.addMulSelf(he.getAngle(), he.getFace().getFaceNormal());
			}
			he = he.getNextInVertex();
		} while (he != vertex.getHalfedge());
		v.normalizeSelf();
		return v;

	}

	/**
	 * Returns the discrete Gaussian curvature and the mean normal. These
	 * discrete operators are described in "Discrete Differential-Geometry
	 * Operators for Triangulated 2-Manifolds", Mark Meyer, Mathieu Desbrun,
	 * Peter Schrï¿½der, and Alan H. Barr.
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 * @param meanCurvatureVector
	 * @return
	 */
	public static double getGaussianCurvature(final HE_Vertex vertex, final WB_Vector meanCurvatureVector) {
		meanCurvatureVector.set(0, 0, 0);
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		double mixed = 0.0;
		double gauss = 0.0;
		HE_Halfedge ot = vertex.getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			/*
			 * if (ot.getPair().getFace() == null) { meanCurvatureVector.set(0,
			 * 0, 0); return 0.0; }
			 */
			final HE_Vertex p1 = ot.getEndVertex();
			final HE_Vertex p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(vertex, p1);
			vect2 = new WB_Vector(p1, p2);
			vect3 = new WB_Vector(p2, vertex);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			final double c31 = vect3.dot(vect1);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			if (c31 > 0.0) {
				mixed += 0.5 * area;
			} else if (c12 > 0.0 || c23 > 0.0) {
				mixed += 0.25 * area;
			} else {
				if (area > 0.0 && area > -WB_Epsilon.EPSILON * (c12 + c23)) {
					mixed -= 0.125 * 0.5 * (c12 * vect3.dot(vect3) + c23 * vect1.dot(vect1)) / area;
				}
			}
			gauss += Math.abs(Math.atan2(2.0 * area, -c31));
			meanCurvatureVector.addMulSelf(0.5 / area, vect3.mulAddMul(c12, -c23, vect1));
		} while (ot.getEndVertex() != d);
		meanCurvatureVector.mulSelf(0.5 / mixed);
		return (2.0 * Math.PI - gauss) / mixed;
	}

	/**
	 * Returns the discrete Gaussian curvature. These discrete operators are
	 * described in "Discrete Differential-Geometry Operators for Triangulated
	 * 2-Manifolds", Mark Meyer, Mathieu Desbrun, Peter Schröder, and Alan H.
	 * Barr. http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 *
	 * @return
	 */
	public static double getGaussianCurvature(final HE_Vertex vertex) {
		final WB_Vector meanCurvatureVector = new WB_Vector(0, 0, 0);
		if (vertex.isBoundary()) {
			return 0.0;

		}
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		double mixed = 0.0;
		double gauss = 0.0;
		HE_Halfedge ot = vertex.getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			/*
			 * if (ot.getPair().getFace() == null) { meanCurvatureVector.set(0,
			 * 0, 0); return 0.0; }
			 */
			final HE_Vertex p1 = ot.getEndVertex();
			final HE_Vertex p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(vertex, p1);
			vect2 = new WB_Vector(p1, p2);
			vect3 = new WB_Vector(p2, vertex);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			final double c31 = vect3.dot(vect1);

			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			// This angle is obtuse
			if (c31 > 0.0) {
				mixed += 0.5 * area;
				// One of the other angles is obtuse
			} else if (c12 > 0.0 || c23 > 0.0) {
				mixed += 0.25 * area;
			} else {

				if (area > 0.0 && area > -WB_Epsilon.EPSILON * (c12 + c23)) {
					mixed -= 0.125 * 0.5 * (c12 * vect3.dot(vect3) + c23 * vect1.dot(vect1)) / area;
				}
			}
			gauss += Math.abs(Math.atan2(2.0 * area, -c31));
			meanCurvatureVector.addMulSelf(0.5 / area, vect3.mulAddMul(c12, -c23, vect1));
		} while (ot.getEndVertex() != d);
		meanCurvatureVector.mulSelf(0.5 / mixed);
		// Discrete gaussian curvature
		return (2.0 * Math.PI - gauss) / mixed;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_CoordinateSystem getCurvatureDirections(final HE_Vertex v) {
		final WB_CoordinateSystem tangent = getVertexCS(v);
		if (tangent == null) {
			return null;
		}
		final WB_Vector vect1 = findOptimalSolution(v, tangent.getZ(), tangent.getX(), tangent.getY());
		if (vect1 == null) {
			return null;
		}
		double e1, e2;
		if (Math.abs(vect1.yd()) < WB_Epsilon.EPSILON) {
			if (Math.abs(vect1.xd()) < Math.abs(vect1.zd())) {
				e1 = 0.0;
				e2 = 1.0;
			} else {
				e1 = 1.0;
				e2 = 0.0;
			}
		} else {
			e2 = 1.0;
			final double delta = Math
					.sqrt((vect1.xd() - vect1.zd()) * (vect1.xd() - vect1.zd()) + 4.0 * vect1.yd() * vect1.yd());
			double K1;
			if (vect1.xd() + vect1.zd() < 0.0) {
				K1 = 0.5 * (vect1.xd() + vect1.zd() - delta);
			} else {
				K1 = 0.5 * (vect1.xd() + vect1.zd() + delta);
			}
			e1 = (K1 - vect1.xd()) / vect1.yd();
			final double n = Math.sqrt(e1 * e1 + e2 * e2);
			e1 /= n;
			e2 /= n;
		}
		final WB_Vector t1 = tangent.getX();
		final WB_Vector t2 = tangent.getY();
		final WB_Vector X = t1.mulAddMul(e1, e2, t2);
		final WB_Vector Y = t1.mulAddMul(-e2, e1, t2);
		return gf.createCSFromOXYZ(v, X, Y, tangent.getZ());
	}

	/**
	 *
	 *
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param tempD1
	 * @param tempD2
	 * @param ret
	 * @return
	 */
	private static double computeNormal3D(final WB_Coord p0, final WB_Coord p1, final WB_Coord p2, WB_Vector tempD1,
			WB_Vector tempD2, final WB_Vector ret) {
		tempD1 = WB_Point.subToVector3D(p1, p2);
		tempD2 = WB_Point.subToVector3D(p2, p0);
		tempD1.crossInto(ret, tempD2);
		double norm = ret.getLength3D();
		if (norm * norm > WB_Epsilon.SQEPSILON
				* (tempD1.xd() * tempD1.xd() + tempD1.yd() * tempD1.yd() + tempD1.zd() * tempD1.zd()
						+ tempD2.xd() * tempD2.xd() + tempD2.yd() * tempD2.yd() + tempD2.zd() * tempD2.zd())) {
			ret.mulSelf(1.0 / norm);
		} else {
			ret.set(0, 0, 0);
			norm = 0.0;
		}
		return 0.5 * norm;
	}

	/**
	 *
	 *
	 * @param normal
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static WB_Vector findOptimalSolution(final HE_Vertex v, final WB_Vector normal, final WB_Vector t1,
			final WB_Vector t2) {
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		final WB_Vector g0 = new WB_Vector();
		final WB_Vector g1 = new WB_Vector();
		final WB_Vector g2 = new WB_Vector();
		final WB_Vector h = new WB_Vector();
		HE_Halfedge ot = v.getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			final WB_Coord p1 = ot.getEndVertex();
			final WB_Coord p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(v, p1);
			vect2 = new WB_Vector(p1, p2);
			vect3 = new WB_Vector(p2, v);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			final double len2 = vect1.dot(vect1);
			if (len2 < WB_Epsilon.SQEPSILON) {
				continue;
			}
			final double kappa = 2.0 * vect1.dot(normal) / len2;
			double d1 = vect1.dot(t1);
			double d2 = vect1.dot(t2);
			final double n = Math.sqrt(d1 * d1 + d2 * d2);
			if (n < WB_Epsilon.EPSILON) {
				continue;
			}
			d1 /= n;
			d2 /= n;
			final double omega = 0.5 * (c12 * vect3.dot(vect3) + c23 * vect1.dot(vect1)) / area;
			g0.addSelf(omega * d1 * d1 * d1 * d1, omega * 2.0 * d1 * d1 * d1 * d2, omega * d1 * d1 * d2 * d2);
			g1.addSelf(omega * 4.0 * d1 * d1 * d2 * d2, omega * 2.0 * d1 * d2 * d2 * d2, omega * d2 * d2 * d2 * d2);
			h.addSelf(omega * kappa * d1 * d1, omega * kappa * 2.0 * d1 * d2, omega * kappa * d2 * d2);
		} while (ot.getEndVertex() != d);
		g1.setX(g0.yd());
		g2.setX(g0.zd());
		g2.setY(g1.zd());
		WB_M33 G = new WB_M33(g0.xd(), g1.xd(), g2.xd(), g0.yd(), g1.yd(), g2.yd(), g0.zd(), g1.zd(), g2.zd());
		G = G.inverse();
		if (G == null) {
			return null;
		}
		return WB_M33.mulToPoint(G, h);
	}

	public static WB_Coord getFaceNormal(final HE_Face face) {
		HE_Halfedge he = face.getHalfedge();
		if (he == null) {
			return null;
		}
		// calculate normal with Newell's method

		final WB_Vector _normal = new WB_Vector();
		HE_Vertex p0;
		HE_Vertex p1;
		do {
			p0 = he.getVertex();
			p1 = he.getNextInFace().getVertex();
			_normal.addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()), (p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
					(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		_normal.normalizeSelf();
		return _normal;
	}

	public static WB_Coord getNonNormFaceNormal(final HE_Face face) {
		HE_Halfedge he = face.getHalfedge();
		if (he == null) {
			return null;
		}
		// calculate normal with Newell's method
		final WB_Vector _normal = new WB_Vector();
		HE_Vertex p0;
		HE_Vertex p1;
		do {
			p0 = he.getVertex();
			p1 = he.getNextInFace().getVertex();
			_normal.addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()), (p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
					(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		return _normal;
	}

	public static double getFaceArea(final HE_Face face) {
		HE_Halfedge he = face.getHalfedge();
		if (he == null) {
			return 0;
		}
		final WB_Coord n = getFaceNormal(face);
		if (WB_Vector.getLength3D(n) < 0.5) {
			return 0;
		}
		final double x = WB_Math.fastAbs(n.xd());
		final double y = WB_Math.fastAbs(n.yd());
		final double z = WB_Math.fastAbs(n.zd());
		double area = 0;
		int coord = 3;
		if (x >= y && x >= z) {
			coord = 1;
		} else if (y >= x && y >= z) {
			coord = 2;
		}
		do {
			switch (coord) {
			case 1:
				area += he.getVertex().yd()
						* (he.getNextInFace().getVertex().zd() - he.getPrevInFace().getVertex().zd());
				break;
			case 2:
				area += he.getVertex().xd()
						* (he.getNextInFace().getVertex().zd() - he.getPrevInFace().getVertex().zd());
				break;
			case 3:
				area += he.getVertex().xd()
						* (he.getNextInFace().getVertex().yd() - he.getPrevInFace().getVertex().yd());
				break;
			}
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		switch (coord) {
		case 1:
			area *= 0.5 / x;
			break;
		case 2:
			area *= 0.5 / y;
			break;
		case 3:
			area *= 0.5 / z;
		}
		return WB_Math.fastAbs(area);
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Classification getFaceType(final HE_Face face) {
		HE_Halfedge he = face.getHalfedge();
		if (he == null) {
			return WB_Classification.UNKNOWN;
		}

		do {
			if (he.getHalfedgeType() == WB_Classification.CONCAVE) {
				return WB_Classification.CONCAVE;
			}
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		return WB_Classification.CONVEX;
	}

}
