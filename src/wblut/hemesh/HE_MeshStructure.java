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
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Sphere;
import wblut.geom.WB_Vector;

/**
 * Collection of mesh elements. Contains methods to manipulate selections
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_MeshStructure extends HE_MeshElement  {


	protected HE_RAS<HE_Vertex> vertices;

	protected HE_RAS<HE_Halfedge> halfedges;
	protected HE_RAS<HE_Halfedge> edges;
	protected  HE_RAS<HE_Halfedge> unpairedHalfedges;
	protected HE_RAS<HE_Face> faces;


	/**
	 * Instantiates a new HE_Selection.
	 */
	public HE_MeshStructure() {
		super();
		vertices = new HE_RASTrove<HE_Vertex>();
		halfedges = new HE_RASTrove<HE_Halfedge>();
		edges = new HE_RASTrove<HE_Halfedge>();
		unpairedHalfedges = new HE_RASTrove<HE_Halfedge>();
		faces = new HE_RASTrove<HE_Face>();
	}

	/**
	 *
	 *
	 * @param ms
	 */
	public HE_MeshStructure(final HE_MeshStructure ms) {
		this();
		for (final HE_Face f : ms.faces) {
			add(f);
		}
		for (final HE_Halfedge he : ms.halfedges) {
			halfedges.add(he);
		}

		for (final HE_Halfedge e : ms.edges) {
			edges.add(e);
		}

		for (final HE_Halfedge uhe : ms.unpairedHalfedges) {
			unpairedHalfedges.add(uhe);
		}
		for (final HE_Vertex v : ms.vertices) {
			add(v);
		}
	}

	/**
	 *
	 *
	 * @param n
	 * @param ta
	 */
	public void getFacesWithNormal(final WB_Coord n, final double ta) {
		final WB_Vector nn = geometryfactory.createNormalizedVector(n);
		final double cta = Math.cos(ta);
		HE_FaceIterator fItr = this.fItr();
		HE_Face f;
		while(fItr.hasNext()){
			f=fItr.next();
			if (nn.dot(f.getFaceNormal()) > cta) {
				add(f);
			}
		}
	}

	/**
	 * Add face.
	 *
	 * @param f
	 *            face to add
	 */
	public final void add(final HE_Face f) {
		faces.add(f);
	}

	/**
	 * Adds Halfedge.
	 *
	 * @param he
	 *            halfedge to add
	 */
	public final void add(final HE_Halfedge he) {
		if(he.getPair()==null){
			unpairedHalfedges.add(he);
		}else if(he.isEdge()){
			edges.add(he);
		}else{
			halfedges.add(he);
		}
	}

	/**
	 * Add vertex.
	 *
	 * @param v
	 *            vertex to add
	 */
	public final void add(final HE_Vertex v) {
		vertices.add(v);
	}

	/**
	 * Adds faces.
	 *
	 * @param faces
	 *            faces to add as HE_Face[]
	 */
	public final void addFaces(final HE_Face[] faces) {
		for (final HE_Face face : faces) {
			add(face);
		}
	}

	/**
	 * Adds faces.
	 *
	 * @param faces
	 *            faces to add as List<HE_Face>
	 */
	public final void addFaces(final Collection<HE_Face> faces) {
		for (HE_Face f:faces) {
			add(f);
		}
	}

	/**
	 *
	 *
	 * @param source
	 */
	public final void addFaces(final HE_MeshStructure source) {
		faces.addAll(source.faces);
	}

	/**
	 * Adds halfedges.
	 *
	 * @param halfedges
	 *            halfedges to add as HE_Halfedge[]
	 */
	public final void addHalfedges(final HE_Halfedge[] halfedges) {
		for (final HE_Halfedge halfedge : halfedges) {
			add(halfedge);
		}
	}

	/**
	 * Adds halfedges.
	 *
	 * @param halfedges
	 *            halfedges to add as List<HE_Halfedge>
	 */
	public final void addHalfedges(final Collection<HE_Halfedge> halfedges) {
		for (HE_Halfedge he:halfedges) {
			add(he);
		}
	}

	/**
	 *
	 *
	 * @param source
	 */
	public final void addHalfedges(final HE_MeshStructure source) {
		halfedges.addAll(source.halfedges);
		edges.addAll(source.edges);
		unpairedHalfedges.addAll(source.unpairedHalfedges);

	}

	/**
	 *
	 *
	 * @param source
	 */
	public final void addEdges(final HE_MeshStructure source) {
		edges.addAll(source.edges);
	}

	/**
	 * Adds vertices.
	 *
	 * @param vertices
	 *            vertices to add as HE_Vertex[]
	 */
	public final void addVertices(final HE_Vertex[] vertices) {
		for (final HE_Vertex vertex : vertices) {
			add(vertex);
		}
	}

	/**
	 *
	 *
	 * @param source
	 */
	public final void addVertices(final HE_MeshStructure source) {
		vertices.addAll(source.vertices);
	}

	/**
	 * Adds vertices.
	 *
	 * @param vertices
	 *            vertices to add as List<HE_Vertex>
	 */
	public final void addVertices(final Collection<HE_Vertex> vertices) {
		for (HE_Vertex v:vertices) {
			add(v);
		}
	}

	/**
	 * Clear entire structure.
	 */
	@Override
	public void clear() {
		clearVertices();
		clearHalfedges();
		clearFaces();
	}

	/**
	 * Clear faces.
	 */
	public final void clearFaces() {
		faces = new HE_RASTrove<HE_Face>();
	}

	/**
	 * Clear halfedges.
	 */
	public final void clearHalfedges() {
		halfedges = new HE_RASTrove<HE_Halfedge>();
		edges = new HE_RASTrove<HE_Halfedge>();
		unpairedHalfedges= new HE_RASTrove<HE_Halfedge>();

	}


	/**
	 * Clear edges.
	 */
	public final void clearEdges() {

		edges = new HE_RASTrove<HE_Halfedge>();


	}

	/**
	 * Clear vertices.
	 */
	public final void clearVertices() {
		vertices = new HE_RASTrove<HE_Vertex>();
	}

	/**
	 * Check if structure contains face.
	 *
	 * @param f
	 *            face
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Face f) {
		return faces.contains(f);
	}

	/**
	 * Check if structure contains halfedge.
	 *
	 * @param he
	 *            halfedge
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Halfedge he) {
		return edges.contains(he)||
				halfedges.contains(he)||	unpairedHalfedges.contains(he);

	}

	/**
	 * Check if structure contains vertex.
	 *
	 * @param v
	 *            vertex
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Vertex v) {
		return vertices.contains(v);
	}

	/**
	 * Get axis-aligned bounding box surrounding mesh.
	 *
	 * @return WB_AABB axis-aligned bounding box
	 */
	public final WB_AABB getAABB() {
		final double[] result = getLimits();
		final WB_Point min = geometryfactory.createPoint(result[0], result[1], result[2]);
		final WB_Point max = geometryfactory.createPoint(result[3], result[4], result[5]);
		return new WB_AABB(min, max);
	}

	/**
	 *
	 *
	 * @return
	 */
	public final WB_Sphere getBoundingSphere() {

		return WB_Sphere.getBoundingSphere(vertices);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 * @deprecated Use {@link #getFaceWithIndex(int)} instead
	 */
	@Deprecated
	public final HE_Face getFaceByIndex(final int i) {
		return getFaceWithIndex(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public final HE_Face getFaceWithIndex(final int i) {
		return faces.get(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 * @deprecated Use {@link #getHalfedgeWithIndex(int)} instead
	 */
	@Deprecated
	public final HE_Halfedge getHalfedgeByIndex(final int i) {
		return getHalfedgeWithIndex(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public final HE_Halfedge getHalfedgeWithIndex(final int i) {
		if(i>=(edges.size()+halfedges.size())) {
			return unpairedHalfedges.get(i-edges.size()-halfedges.size());
		}else if(i>=edges.size()) {
			return halfedges.get(i-edges.size());
		}
		return edges.get(i);
	}




	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public final HE_Halfedge getEdgeWithIndex(final int i) {
		return edges.get(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 * @deprecated Use {@link #getEdgeWithIndex(int)} instead
	 */
	@Deprecated
	public final HE_Halfedge getEdgeByIndex(final int i) {
		return getEdgeWithIndex(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 * @deprecated Use {@link #getVertexWithIndex(int)} instead
	 */
	@Deprecated
	public final HE_Vertex getVertexByIndex(final int i) {
		return getVertexWithIndex(i);
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public final HE_Vertex getVertexWithIndex(final int i) {
		return vertices.get(i);
	}

	/**
	 * Get range of vertex coordinates.
	 *
	 * @return array of limit values: min x, min y, min z, max x, max y, max z
	 */
	public final double[] getLimits() {
		final double[] result = new double[6];
		for (int i = 0; i < 3; i++) {
			result[i] = Double.POSITIVE_INFINITY;
		}
		for (int i = 3; i < 6; i++) {
			result[i] = Double.NEGATIVE_INFINITY;
		}
		HE_Vertex v;
		for (int i = 0; i < vertices.size(); i++) {
			v = getVertexWithIndex(i);
			result[0] = Math.min(result[0], v.xd());
			result[1] = Math.min(result[1], v.yd());
			result[2] = Math.min(result[2], v.zd());
			result[3] = Math.max(result[3], v.xd());
			result[4] = Math.max(result[4], v.yd());
			result[5] = Math.max(result[5], v.zd());
		}
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public final double[] limits() {
		final double[] result = new double[6];
		for (int i = 0; i < 3; i++) {
			result[i] = Double.POSITIVE_INFINITY;
		}
		for (int i = 3; i < 6; i++) {
			result[i] = Double.NEGATIVE_INFINITY;
		}
		HE_Vertex v;
		for (int i = 0; i < vertices.size(); i++) {
			v = getVertexWithIndex(i);
			result[0] = Math.min(result[0], v.xd());
			result[1] = Math.min(result[1], v.yd());
			result[2] = Math.min(result[2], v.zd());
			result[3] = Math.max(result[3], v.xd());
			result[4] = Math.max(result[4], v.yd());
			result[5] = Math.max(result[5], v.zd());
		}
		return result;
	}

	/**
	 * Number of edges.
	 *
	 * @return the number of edges
	 */
	public int getNumberOfEdges() {
		return edges.size();
	}

	/**
	 * Number of faces.
	 *
	 * @return the number of faces
	 */
	public final int getNumberOfFaces() {
		return faces.size();
	}

	/**
	 * Number of halfedges.
	 *
	 * @return the number of halfedges
	 */
	public final int getNumberOfHalfedges() {
		return halfedges.size()+edges.size()+unpairedHalfedges.size();
	}

	/**
	 * Number of vertices.
	 *
	 * @return the number of vertices
	 */
	public final int getNumberOfVertices() {
		return vertices.size();
	}

	/**
	 * Removes face.
	 *
	 * @param f
	 *            face to remove
	 */
	public final void remove(final HE_Face f) {
		faces.remove(f);
	}

	/**
	 * Removes halfedge.
	 *
	 * @param he
	 *            halfedge to remove
	 */
	public final void remove(final HE_Halfedge he) {
		edges.remove(he);
		halfedges.remove(he);
		unpairedHalfedges.remove(he);
	}

	/**
	 * Removes vertex.
	 *
	 * @param v
	 *            vertex to remove
	 */
	public final void remove(final HE_Vertex v) {
		vertices.remove(v);
	}

	/**
	 *
	 *
	 * @param i
	 */
	public final void removeFace(final int i) {
		if (i >= faces.size()) {
			throw new IllegalArgumentException("Face index " + i + " out of range!");
		}
		faces.removeAt(i);
	}

	/**
	 * Removes faces.
	 *
	 * @param faces
	 *            faces to remove as HE_Face[]
	 */
	public final void removeFaces(final HE_Face[] faces) {
		for (final HE_Face face : faces) {
			remove(face);
		}
	}

	/**
	 * Removes faces.
	 *
	 * @param faces
	 *            faces to remove as List<HE_Face>
	 */
	public final void removeFaces(final Collection<HE_Face> faces) {
		for (final HE_Face f : faces) {
			remove(f);
		}
	}



	/**
	 * Removes halfedges.
	 *
	 * @param halfedges
	 *            halfedges to remove as HE_Halfedge[]
	 */
	public final void removeHalfedges(final HE_Halfedge[] halfedges) {
		for (final HE_Halfedge halfedge : halfedges) {
			remove(halfedge);
		}
	}

	/**
	 * Removes halfedges.
	 *
	 * @param halfedges
	 *            halfedges to remove as FastTable<HE_Halfedge>
	 */
	public final void removeHalfedges(final Collection<HE_Halfedge> halfedges) {
		for (final HE_Halfedge he : halfedges) {
			remove(he);
		}
	}

	/**
	 *
	 *
	 * @param i
	 */
	public final void removeVertex(final int i) {
		if (i >= vertices.size()) {
			throw new IllegalArgumentException("Vertex index " + i + " out of range!");
		}
		vertices.removeAt(i);
	}

	/**
	 * Removes vertices.
	 *
	 * @param vertices
	 *            vertices to remove as HE_Vertex[]
	 */
	public final void removeVertices(final HE_Vertex[] vertices) {
		for (final HE_Vertex vertice : vertices) {
			remove(vertice);
		}
	}

	/**
	 * Removes vertices.
	 *
	 * @param vertices
	 *            vertices to remove as FastTable<HE_Vertex>
	 */
	public final void removeVertices(final Collection<HE_Vertex> vertices) {
		for (final HE_Vertex v : vertices) {
			remove(v);
		}
	}

	/**
	 *
	 *
	 * @return
	 */
	public final List<HE_Vertex> getVertices() {
		return vertices.getObjects();
	}

	/**
	 *
	 *
	 * @return
	 */
	public final List<HE_Halfedge> getHalfedges() {
		final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
		halfedges.addAll(this.halfedges);
		halfedges.addAll(this.edges);
		halfedges.addAll(this.unpairedHalfedges);
		return (halfedges);
	}

	/**
	 *
	 *
	 * @return
	 */
	public final List<HE_Halfedge> getEdges() {
		return edges.getObjects();
	}

	/**
	 *
	 *
	 * @return
	 */
	public final List<HE_Face> getFaces() {
		return faces.getObjects();
	}

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public final boolean containsFace(final long key) {
		return faces.containsKey(key);
	}

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public final boolean containsHalfedge(final long key) {
		return halfedges.containsKey(key)||edges.containsKey(key)||unpairedHalfedges.containsKey(key);
	}

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public final boolean containsEdge(final long key) {
		return edges.containsKey(key);
	}

	/**
	 *
	 *
	 * @param key
	 * @return
	 */
	public final boolean containsVertex(final long key) {
		return vertices.containsKey(key);
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public final int getIndex(final HE_Face f) {
		return faces.indexOf(f);
	}




	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public final int getIndex(final HE_Vertex v) {
		return vertices.indexOf(v);
	}


	/**
	 * Replace faces.
	 *
	 * @param faces
	 *            faces to replace with as HE_Face[]
	 */
	protected final void replaceFaces(final HE_Face[] faces) {
		clearFaces();
		for (final HE_Face face : faces) {
			add(face);
		}
	}

	/**
	 *
	 *
	 * @param mesh
	 */
	protected final void replaceFaces(final HE_Mesh mesh) {
		clearFaces();
		addFaces(mesh);
	}

	/**
	 *
	 *
	 * @param mesh
	 */
	protected final void replaceVertices(final HE_Mesh mesh) {
		clearVertices();
		addVertices(mesh);
	}

	/**
	 *
	 *
	 * @param mesh
	 */
	protected final void replaceHalfedges(final HE_Mesh mesh) {
		clearHalfedges();
		HE_HalfedgeIterator heItr=mesh.heItr();
		while(heItr.hasNext()){
			add(heItr.next());

		}

	}

	/**
	 * Replace faces.
	 *
	 * @param faces
	 *            faces to replace with as List<HE_Face>
	 */
	protected final void replaceFaces(final List<HE_Face> faces) {
		clearFaces();
		addFaces(faces);
	}

	/**
	 * Replace halfedges.
	 *
	 * @param halfedges
	 *            halfedges to replace with as HE_Halfedge[]
	 */
	protected final void replaceHalfedges(final HE_Halfedge[] halfedges) {
		clearHalfedges();

		for (final HE_Halfedge halfedge : halfedges) {
			add(halfedge);
		}
	}

	/**
	 * Replace halfedges.
	 *
	 * @param halfedges
	 *            halfedges to replace with as List<HE_Halfedge>
	 */
	protected final void replaceHalfedges(final List<HE_Halfedge> halfedges) {
		clearHalfedges();

		addHalfedges(halfedges);

	}

	/**
	 * Replace vertices.
	 *
	 * @param vertices
	 *            vertices to replace with as HE_Vertex[]
	 */
	protected  final void replaceVertices(final HE_Vertex[] vertices) {
		clearVertices();
		for (final HE_Vertex vertice : vertices) {
			add(vertice);
		}
	}

	/**
	 * Replace vertices.
	 *
	 * @param vertices
	 *            vertices to replace with as List<HE_Vertex>
	 */
	protected  final void replaceVertices(final List<HE_Vertex> vertices) {
		clearVertices();
		addVertices(vertices);
	}

	/**
	 * Vertex iterator.
	 *
	 * @return vertex iterator
	 */
	public HE_VertexIterator vItr() {
		return new HE_VertexIterator(vertices);
	}

	/**
	 * Edge iterator.
	 *
	 * @return edge iterator
	 */
	public HE_EdgeIterator eItr() {
		return new HE_EdgeIterator(edges);
	}

	/**
	 * Hslfedge iterator.
	 *
	 * @return halfedge iterator
	 */
	public HE_HalfedgeIterator heItr() {
		return new HE_HalfedgeIterator(edges, halfedges, unpairedHalfedges);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_FaceIterator fItr() {
		return new HE_FaceIterator(faces);
	}

	/**
	 * Edges as array.
	 *
	 * @return all edges as HE_Halfedge[]
	 */
	public final HE_Halfedge[] getEdgesAsArray() {
		final HE_Halfedge[] edges = new HE_Halfedge[getNumberOfEdges()];
		final Iterator<HE_Halfedge> eItr = eItr();
		int i = 0;
		while (eItr.hasNext()) {
			edges[i] = eItr.next();
			i++;
		}
		return edges;
	}

	/**
	 * Edges as arrayList.
	 *
	 * @return all vertices as FastTable<HE_Halfedge>
	 */
	public final List<HE_Halfedge> getEdgesAsList() {
		final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			edges.add(eItr.next());
		}
		return (edges);
	}

	/**
	 * Get face.
	 *
	 * @param key
	 *            face key
	 * @return face
	 * @deprecated Use {@link #getFaceWithKey(long)} instead
	 */
	@Deprecated
	public final HE_Face getFaceByKey(final long key) {
		return getFaceWithKey(key);
	}

	/**
	 * Get face.
	 *
	 * @param key
	 *            face key
	 * @return face
	 */
	public final HE_Face getFaceWithKey(final long key) {
		return faces.getWithKey(key);
	}

	/**
	 * Faces as array.
	 *
	 * @return all faces as HE_Face[]
	 */
	public final HE_Face[] getFacesAsArray() {
		final HE_Face[] faces = new HE_Face[getNumberOfFaces()];
		final Iterator<HE_Face> fItr = this.faces.iterator();
		int i = 0;
		while (fItr.hasNext()) {
			faces[i] = fItr.next();
			i++;
		}
		return faces;
	}

	/**
	 * Faces as arrayList.
	 *
	 * @return all vertices as FastTable<HE_Face>
	 */
	public final List<HE_Face> getFacesAsList() {
		final List<HE_Face> faces = new FastTable<HE_Face>();
		faces.addAll(this.faces);
		return (faces);
	}

	/**
	 * Get halfedge.
	 *
	 * @param key
	 *            halfedge key
	 * @return halfedge
	 * @deprecated Use {@link #getHalfedgeWithKey(long)} instead
	 */
	@Deprecated
	public final HE_Halfedge getHalfedgeByKey(final long key) {
		return getHalfedgeWithKey(key);
	}

	/**
	 * Get halfedge.
	 *
	 * @param key
	 *            halfedge key
	 * @return halfedge
	 */
	public final HE_Halfedge getHalfedgeWithKey(final long key) {
		HE_Halfedge he=edges.getWithKey(key);
		if (he!=null) {
			return he;
		}
		he=halfedges.getWithKey(key);
		if (he!=null) {
			return he;
		}
		return unpairedHalfedges.getWithKey(key);
	}

	/**
	 * Get edge.
	 *
	 * @param key
	 *            halfedge key
	 * @return halfedge
	 */
	public final HE_Halfedge getEdgeWithKey(final long key) {
		HE_Halfedge he=edges.getWithKey(key);
		if (he!=null) {
			return he;
		}
		he=halfedges.getWithKey(key);
		if (he!=null) {
			return he;
		}
		return unpairedHalfedges.getWithKey(key);
	}

	/**
	 * Halfedges as array.
	 *
	 * @return all halfedges as HE_Halfedge[]
	 */
	public final HE_Halfedge[] getHalfedgesAsArray() {
		final HE_Halfedge[] halfedges = new HE_Halfedge[getNumberOfHalfedges()];
		final Iterator<HE_Halfedge> heItr = this.halfedges.iterator();
		int i = 0;
		while (heItr.hasNext()) {
			halfedges[i] = heItr.next();
			i++;
		}
		final Iterator<HE_Halfedge> eItr = this.edges.iterator();
		while (eItr.hasNext()) {
			halfedges[i] = eItr.next();
			i++;
		}
		final Iterator<HE_Halfedge> uheItr = this.unpairedHalfedges.iterator();
		while (uheItr.hasNext()) {
			halfedges[i] = uheItr.next();
			i++;
		}
		return halfedges;
	}

	/**
	 * Halfedges as arrayList.
	 *
	 * @return all vertices as FastTable<HE_Halfedge>
	 */
	public final List<HE_Halfedge> getHalfedgesAsList() {
		final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
		halfedges.addAll(this.halfedges);
		halfedges.addAll(this.edges);
		halfedges.addAll(this.unpairedHalfedges);
		return (halfedges);
	}

	/**
	 * Get vertex.
	 *
	 * @param key
	 *            vertex key
	 * @return vertex
	 * @deprecated Use {@link #getVertexWithKey(long)} instead
	 */
	@Deprecated
	public final HE_Vertex getVertexByKey(final long key) {
		return getVertexWithKey(key);
	}

	/**
	 * Get vertex.
	 *
	 * @param key
	 *            vertex key
	 * @return vertex
	 */
	public final HE_Vertex getVertexWithKey(final long key) {
		return vertices.getWithKey(key);
	}

	/**
	 * Vertices as array.
	 *
	 * @return all vertices as HE_Vertex[]
	 */
	public final HE_Vertex[] getVerticesAsArray() {
		final HE_Vertex[] vertices = new HE_Vertex[getNumberOfVertices()];
		final Collection<HE_Vertex> _vertices = this.vertices;
		final Iterator<HE_Vertex> vitr = _vertices.iterator();
		int i = 0;
		while (vitr.hasNext()) {
			vertices[i] = vitr.next();
			i++;
		}
		return vertices;
	}

	/**
	 * Vertices as arrayList.
	 *
	 * @return all vertices as FastTable<HE_Vertex>
	 */
	public final List<HE_Vertex> getVerticesAsList() {
		final List<HE_Vertex> vertices = new FastTable<HE_Vertex>();
		final Collection<HE_Vertex> _vertices = this.vertices;
		vertices.addAll(_vertices);
		return (vertices);
	}


	/**
	 *
	 *
	 * @param vertices
	 * @param loop
	 * @return
	 */
	public HE_Path createPathFromIndices(final int[] vertices, final boolean loop) {
		final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
		if (vertices.length > 1) {
			HE_Halfedge he;
			for (int i = 0; i < (vertices.length - 1); i++) {
				he = searchHalfedgeFromTo(getVertexWithIndex(vertices[i]), getVertexWithIndex(vertices[i + 1]));
				if (he == null) {
					throw new IllegalArgumentException(
							"Two vertices " + vertices[i] + " and " + vertices[i + 1] + " in path are not connected.");
				}
				halfedges.add(he);
			}
			if (loop) {
				he = searchHalfedgeFromTo(getVertexWithIndex(vertices[vertices.length - 1]),
						getVertexWithIndex(vertices[0]));
				if (he == null) {
					throw new IllegalArgumentException("Vertices " + vertices[vertices.length - 1] + " and "
							+ vertices[0] + " in path are not connected: path is not a loop.");
				}
			}
		}
		final HE_Path path = new HE_Path(halfedges, loop);
		return path;
	}

	/**
	 *
	 *
	 * @param v0
	 * @param v1
	 * @return
	 */
	public HE_Halfedge searchHalfedgeFromTo(final HE_Vertex v0, final HE_Vertex v1) {
		final List<HE_Halfedge> hes = v0.getHalfedgeStar();
		for (final HE_Halfedge he : hes) {
			if (he.getEndVertex() == v1) {
				return he;
			}
		}
		return null;
	}


	/**
	 *
	 *
	 * @param he
	 * @param f
	 */
	public void setFace(final HE_Halfedge he, final HE_Face f){
		he._setFace(f);
		if(he.getPair()!=null) {
			setPair(he,he.getPair());
		}

	}

	/**
	 *
	 *
	 * @param he
	 */
	public void clearFace(final HE_Halfedge he){
		he._clearFace();
		if(he.getPair()!=null){
			setPair(he,he.getPair());
		}

	}

	/**
	 *
	 *
	 * @param he1
	 * @param he2
	 */
	public void setPair(final HE_Halfedge he1, final HE_Halfedge he2){
		if(he1.getPair()==null){
			unpairedHalfedges.remove(he1);
		}else{
			if(he1.isEdge()){
				edges.remove(he1);

			}else{
				halfedges.remove(he1);

			}
		}
		if(he2.getPair()==null){
			unpairedHalfedges.remove(he2);
		}else{
			if(he2.isEdge()){
				edges.remove(he2);

			}else{
				halfedges.remove(he2);
			}
		}

		he1._setPair(he2);
		he2._setPair(he1);

		if(he1.isEdge()){
			edges.add(he1);
			halfedges.add(he2);
		}
		else if (he2.isEdge()){
			edges.add(he2);
			halfedges.add(he1);
		}
	}

	/**
	 *
	 *
	 * @param he
	 */
	public void clearPair(final HE_Halfedge he){

		if(he.getPair()==null) {
			return;
		}
		HE_Halfedge hep=he.getPair();

		if(he.isEdge()){
			edges.remove(he);
			halfedges.remove(hep);
		}else{
			halfedges.remove(he);
			edges.remove(hep);
		}
		unpairedHalfedges.add(he);
		unpairedHalfedges.add(hep);

		he._clearPair();
		hep._clearPair();


	}

	/**
	 *
	 *
	 * @param he
	 * @param hen
	 */
	public void setNext(final HE_Halfedge he, final HE_Halfedge hen){
		he._setNext(hen);
		hen._setPrev(he);


	}

	/**
	 *
	 *
	 * @param he
	 * @param v
	 */
	public void setVertex(final HE_Halfedge he, final HE_Vertex v){
		he._setVertex(v);


	}

	/**
	 *
	 *
	 * @param f
	 * @param he
	 */
	public void setHalfedge(final HE_Face f, final HE_Halfedge he){
		f._setHalfedge(he);


	}

	/**
	 *
	 *
	 * @param f
	 */
	public void clearHalfedge(final HE_Face f){
		f._clearHalfedge();
	}


	/**
	 *
	 *
	 * @param v
	 * @param he
	 */
	public void setHalfedge(final HE_Vertex v, final HE_Halfedge he){
		v._setHalfedge(he);


	}

	/**
	 *
	 *
	 * @param v
	 */
	public void clearHalfedge(final HE_Vertex v){
		v._clearHalfedge();
	}


	/**
	 *
	 *
	 * @param he
	 */
	public void clearNext(final HE_Halfedge he){
		he._clearNext();
	}

	/**
	 *
	 *
	 * @param he
	 */
	public void clearPrev(final HE_Halfedge he){
		he._clearPrev();
	}

	/**
	 *
	 *
	 * @param he
	 */
	public void clearVertex(final HE_Halfedge he){
		he._clearVertex();
	}

	/**
	 * Fix halfedge vertex assignment.
	 */
	public void fixHalfedgeVertexAssignment() {
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			setHalfedge(he.getVertex(),he);
		}
	}

	/**
	 * Collect all unpaired halfedges.
	 *
	 * @return the unpaired halfedges
	 */
	public List<HE_Halfedge> getUnpairedHalfedges() {
		final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
		halfedges.addAll(this.unpairedHalfedges);
		return halfedges;
	}




}
