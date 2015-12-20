/*
 *
 */
package wblut.hemesh;

import static wblut.math.WB_Epsilon.isZero;
import static wblut.math.WB_Epsilon.isZeroSq;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryType;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_IndexedSegment;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_MeshCreator;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_MTRandom;

/**
 * Half-edge mesh data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Mesh extends HE_MeshStructure implements WB_HasColor, WB_Mesh {
	/**
	 *
	 */
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
	/**
	 *
	 */
	private WB_Coord center;
	/**
	 *
	 */
	private boolean isCenterUpdated;
	/**
	 *
	 */
	protected int label;
	/**
	 *
	 */
	private int meshcolor;

	/**
	 * Instantiates a new HE_Mesh.
	 *
	 */
	public HE_Mesh() {
		super();
		center = new WB_Point();
		isCenterUpdated = false;
		label = -1;
	}

	/**
	 * Constructor.
	 *
	 * @param creator
	 *            HE_Creator that generates this mesh
	 */
	public HE_Mesh(final HEC_Creator creator) {
		super();
		setNoCopy(creator.create());

		isCenterUpdated = false;
		label = -1;
	}

	/**
	 *
	 *
	 * @param mesh
	 */
	public HE_Mesh(final WB_Mesh mesh) {
		this(new HEC_FromMesh(mesh));
	}

	/**
	 *
	 *
	 * @param mesh
	 */
	public HE_Mesh(final WB_MeshCreator mesh) {
		this(new HEC_FromMesh(mesh.getMesh()));
	}

	/**
	 * Modify the mesh.
	 *
	 * @param modifier
	 *            HE_Modifier to apply
	 * @return self
	 */
	public HE_Mesh modify(final HEM_Modifier modifier) {
		return modifier.apply(this);
	}

	/**
	 * Modify selection. Elements should be part of this mesh.
	 *
	 * @param modifier
	 *            HE_Modifier to apply
	 * @param selection
	 *            the selection
	 * @return self
	 */
	public HE_Mesh modifySelected(final HEM_Modifier modifier, final HE_Selection selection) {
		return modifier.apply(selection);
	}

	/**
	 * Subdivide the mesh.
	 *
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @return self
	 */
	public HE_Mesh subdivide(final HES_Subdividor subdividor) {
		return subdividor.apply(this);
	}

	/**
	 * Subdivide selection of the mesh.
	 *
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @param selection
	 *            HE_Selection
	 * @return self
	 */
	public HE_Mesh subdivideSelected(final HES_Subdividor subdividor, final HE_Selection selection) {
		return subdividor.apply(selection);
	}

	/**
	 * Subdivide the mesh a number of times.
	 *
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @param rep
	 *            subdivision iterations. WARNING: higher values will lead to
	 *            unmanageable number of faces.
	 * @return self
	 */
	public HE_Mesh subdivide(final HES_Subdividor subdividor, final int rep) {
		for (int i = 0; i < rep; i++) {
			subdivide(subdividor);
		}
		return this;
	}

	/**
	 * Subdivide a selection of the mesh a number of times.
	 *
	 * @param subdividor
	 *            HE_Subdividor to apply
	 * @param selection
	 *            HE_Selection initial selection
	 * @param rep
	 *            subdivision iterations
	 * @return self
	 */
	public HE_Mesh subdivideSelected(final HES_Subdividor subdividor, final HE_Selection selection, final int rep) {
		for (int i = 0; i < rep; i++) {
			subdivideSelected(subdividor, selection);
		}
		return this;
	}

	/**
	 * Simplify.
	 *
	 * @param simplifier
	 *            the simplifier
	 * @return the h e_ mesh
	 */
	public HE_Mesh simplify(final HES_Simplifier simplifier) {
		return simplifier.apply(this);
	}

	/**
	 * Simplify.
	 *
	 * @param simplifier
	 *            the simplifier
	 * @param selection
	 *            the selection
	 * @return the h e_ mesh
	 */
	public HE_Mesh simplifySelected(final HES_Simplifier simplifier, final HE_Selection selection) {
		return simplifier.apply(selection);
	}

	/**
	 * Deep copy of mesh.
	 *
	 * @return copy as new HE_Mesh
	 */
	public HE_Mesh get() {
		return new HE_Mesh(new HEC_Copy(this));
	}

	/**
	 * Add all mesh elements to this mesh. No copies are made.
	 *
	 * @param mesh
	 *            mesh to add
	 */
	public void add(final HE_Mesh mesh) {
		addVertices(mesh);
		addFaces(mesh);
		addHalfedges(mesh);
	}

	/**
	 * Add all mesh elements to this mesh. No copies are made. Tries to join
	 * geometry.
	 *
	 * @param mesh
	 *            mesh to add
	 */
	public void fuse(final HE_Mesh mesh) {
		addVertices(mesh.getVerticesAsArray());
		addFaces(mesh.getFacesAsArray());
		addHalfedges(mesh.getHalfedgesAsArray());
		setNoCopy(new HE_Mesh(new HEC_FromPolygons().setPolygons(this.getPolygonList())));
	}

	/**
	 * Replace mesh with deep copy of target.
	 *
	 * @param target
	 *            HE_Mesh to be duplicated
	 */
	public void set(final HE_Mesh target) {
		final HE_Mesh result = target.get();
		replaceVertices(result);
		replaceFaces(result);
		replaceHalfedges(result);
	}

	/**
	 * Replace mesh with shallow copy of target.
	 *
	 * @param target
	 *            HE_Mesh to be duplicated
	 */
	void setNoCopy(final HE_Mesh target) {
		replaceVertices(target);
		replaceFaces(target);
		replaceHalfedges(target);
		center = target.center;
		isCenterUpdated = target.isCenterUpdated;
	}

	/**
	 * Return all vertex positions as an array .
	 *
	 * @return 2D array of float. First index gives vertex. Second index gives
	 *         x-,y- or z-coordinate.
	 */
	public float[][] getVerticesAsFloat() {
		final float[][] result = new float[getNumberOfVertices()][3];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i][0] = v.xf();
			result[i][1] = v.yf();
			result[i][2] = v.zf();
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex positions as an array .
	 *
	 * @return 2D array of double. First index gives vertex. Second index gives
	 *         x-,y- or z-coordinate.
	 */
	public double[][] getVerticesAsDouble() {
		final double[][] result = new double[getNumberOfVertices()][3];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i][0] = v.xd();
			result[i][1] = v.yd();
			result[i][2] = v.zd();
			i++;
		}
		return result;
	}

	/**
	 * Vertex key to index.
	 *
	 * @return the map
	 */
	public Map<Long, Integer> vertexKeyToIndex() {
		final Map<Long, Integer> map = new FastMap<Long, Integer>();
		int i = 0;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			map.put(vItr.next().key(), i);
			i++;
		}
		return map;
	}

	/**
	 * Return all vertex positions.
	 *
	 * @return array of WB_Point, values are copied.
	 */
	public WB_Point[] getVerticesAsNewPoint() {
		final WB_Point[] result = new WB_Point[getNumberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = new WB_Point(v);
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex positions.
	 *
	 * @return array of WB_Coordinate, no copies are made.
	 */
	public WB_Coord[] getVerticesAsPoint() {
		final WB_Coord[] result = new WB_Coord[getNumberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = v;
			i++;
		}
		return result;
	}

	/**
	 * Return all vertex normal.
	 *
	 * @return array of WB_Coordinate.
	 */
	public WB_Coord[] getVertexNormals() {
		final WB_Coord[] result = new WB_Coord[getNumberOfVertices()];
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result[i] = v.getVertexNormal();
			i++;
		}
		return result;
	}

	/**
	 *
	 * @return
	 */
	public Map<Long, WB_Coord> getKeyedVertexNormals() {
		final Map<Long, WB_Coord> result = new FastMap<Long, WB_Coord>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result.put(v.key(), v.getVertexNormal());
		}
		return result;
	}

	/**
	 * Return the faces as array of vertex indices.
	 *
	 * @return 2D array of int. First index gives face. Second index gives
	 *         vertices.
	 */
	@Override
	public int[][] getFacesAsInt() {
		final int[][] result = new int[getNumberOfFaces()][];
		final TLongIntMap vertexKeys = new TLongIntHashMap(10, 0.5f, -1L, -1);
		final Iterator<HE_Vertex> vItr = vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final Iterator<HE_Face> fItr = fItr();
		HE_Halfedge he;
		HE_Face f;
		i = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = new int[f.getFaceOrder()];
			he = f.getHalfedge();
			int j = 0;
			do {
				result[i][j] = vertexKeys.get(he.getVertex().key());
				he = he.getNextInFace();
				j++;
			} while (he != f.getHalfedge());
			i++;
		}
		return result;
	}

	/**
	 * Return all face normals.
	 *
	 * @return array of WB_Coordinate.
	 */
	public WB_Coord[] getFaceNormals() {
		final WB_Coord[] result = new WB_Coord[getNumberOfFaces()];
		int i = 0;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.getFaceNormal();
			i++;
		}
		return result;
	}

	/**
	 * Return all face normals.
	 *
	 * @return FastMap of WB_Coordinate.
	 */
	public Map<Long, WB_Coord> getKeyedFaceNormals() {
		final Map<Long, WB_Coord> result = new FastMap<Long, WB_Coord>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result.put(f.key(), f.getFaceNormal());
		}
		return result;
	}

	/**
	 * Return all face centers.
	 *
	 * @return array of WB_Coordinate.
	 */
	public WB_Coord[] getFaceCenters() {
		final WB_Coord[] result = new WB_Coord[getNumberOfFaces()];
		int i = 0;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.getFaceCenter();
			i++;
		}
		return result;
	}

	/**
	 * Return all face centers.
	 *
	 * @return FastMap of WB_Coordinate.
	 */
	public Map<Long, WB_Coord> getKeyedFaceCenters() {
		final Map<Long, WB_Coord> result = new FastMap<Long, WB_Coord>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			result.put(f.key(), f.getFaceCenter());
		}
		return result;
	}

	/**
	 * Return all edge normals.
	 *
	 * @return array of WB_Coordinate.
	 */
	public WB_Coord[] getEdgeNormals() {
		final WB_Coord[] result = new WB_Coord[getNumberOfEdges()];
		int i = 0;
		HE_Halfedge e;
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = e.getEdgeNormal();
			i++;
		}
		return result;
	}

	/**
	 * Return all edge normals.
	 *
	 * @return FastMap of WB_Coordinate.
	 */
	public Map<Long, WB_Coord> getKeyedEdgeNormals() {
		final Map<Long, WB_Coord> result = new FastMap<Long, WB_Coord>();
		HE_Halfedge e;
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result.put(e.key(), e.getEdgeNormal());
		}
		return result;
	}

	/**
	 * Return all edge centers.
	 *
	 * @return array of WB_Coordinate.
	 */
	public WB_Coord[] getEdgeCenters() {
		final WB_Coord[] result = new WB_Coord[getNumberOfEdges()];
		int i = 0;
		HE_Halfedge e;
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = e.getHalfedgeCenter();
			i++;
		}
		return result;
	}

	/**
	 * Return all edge centers.
	 *
	 * @return FastMap of WB_Coordinate.
	 */
	public Map<Long, WB_Coord> getKeyedEdgeCenters() {
		final Map<Long, WB_Coord> result = new FastMap<Long, WB_Coord>();
		HE_Halfedge e;
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			e = eItr.next();
			result.put(e.key(), e.getHalfedgeCenter());
		}
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_FaceListMesh toFaceListMesh() {
		return WB_GeometryFactory.instance().createMesh(getVerticesAsPoint(), getFacesAsInt());
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values
	 *            2D array of float. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromFloat(final float[][] values) {
		int i = 0;
		final WB_Point c = new WB_Point();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			c.addSelf(v);
			i++;
		}
		c.divSelf(i);
		center = c;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values
	 *            array of WB_Coordinate.
	 */
	public void setVerticesFromPoint(final WB_Coord[] values) {
		int i = 0;
		final WB_Point c = new WB_Point();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i]);
			c.addSelf(v);
			i++;
		}
		c.divSelf(i);
		center = c;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values
	 *            2D array of double. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromDouble(final double[][] values) {
		int i = 0;
		final WB_Point c = new WB_Point();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			c.addSelf(v);
			i++;
		}
		c.divSelf(i);
		center = c;
	}

	/**
	 * Set vertex positions to values in array.
	 *
	 * @param values
	 *            2D array of int. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromInt(final int[][] values) {
		int i = 0;
		final WB_Point c = new WB_Point();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			c.addSelf(v);
			i++;
		}
		c.divSelf(i);
		center = c;
	}

	/**
	 * Return the mesh as polygon soup.
	 *
	 * @return array of WB_polygon
	 *
	 */
	public WB_Polygon[] getPolygons() {
		final WB_Polygon[] result = new WB_Polygon[getNumberOfFaces()];
		final Iterator<HE_Face> fItr = fItr();
		HE_Face f;
		int i = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			result[i] = f.toPolygon();
			i++;
		}
		return result;
	}

	/**
	 * Gets the polygon list.
	 *
	 * @return the polygon list
	 */
	public List<WB_Polygon> getPolygonList() {
		final List<WB_Polygon> result = new FastTable<WB_Polygon>();
		final Iterator<HE_Face> fItr = fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			result.add(f.toPolygon());
		}
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<WB_Triangle> getTriangles() {
		final List<WB_Triangle> result = new FastTable<WB_Triangle>();
		final HE_Mesh trimesh = this.get();
		trimesh.triangulate();
		final Iterator<HE_Face> fItr = trimesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			result.add(WB_GeometryFactory.instance().createTriangle(f.getHalfedge().getVertex(),
					f.getHalfedge().getNextInFace().getVertex(), f.getHalfedge().getPrevInFace().getVertex()));
		}
		return result;
	}

	/**
	 * Gets the segments.
	 *
	 * @return the segments
	 */
	public WB_Segment[] getSegments() {
		final WB_Segment[] result = new WB_Segment[getNumberOfEdges()];
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		int i = 0;
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = new WB_Segment(e.getVertex(), e.getEndVertex());
			i++;
		}
		return result;
	}

	/**
	 * Gets the indexed segments.
	 *
	 * @return the indexed segments
	 */
	public WB_IndexedSegment[] getIndexedSegments() {
		final WB_IndexedSegment[] result = new WB_IndexedSegment[getNumberOfEdges()];
		final WB_Coord[] points = getVerticesAsPoint();
		final TLongIntMap map = new TLongIntHashMap(10, 0.5f, -1L, -1);
		map.putAll(vertexKeyToIndex());
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		int i = 0;
		while (eItr.hasNext()) {
			e = eItr.next();
			result[i] = new WB_IndexedSegment(map.get(e.getVertex().key()), map.get(e.getEndVertex().key()), points);
			i++;
		}
		return result;
	}

	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public WB_Frame getFrame() {
		final WB_Frame frame = new WB_Frame(getVerticesAsPoint());
		final TLongIntMap map = new TLongIntHashMap(10, 0.5f, -1L, -1);
		map.putAll(vertexKeyToIndex());
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			frame.addStrut(map.get(e.getVertex().key()), map.get(e.getEndVertex().key()));
		}
		return frame;
	}

	/**
	 * Apply transform to entire mesh.
	 *
	 * @param T
	 *            WB_Transform to apply
	 *
	 * @return self
	 */
	public HE_Mesh transform(final WB_Transform T) {
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			T.applySelfAsPoint(vItr.next());
		}
		return this;
	}

	/**
	 * Translate entire mesh.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return self
	 */
	public HE_Mesh move(final double x, final double y, final double z) {
		center = new WB_Point(center).addSelf(x, y, z);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().addSelf(x, y, z);
		}
		return this;
	}

	/**
	 * Translate entire mesh.
	 *
	 * @param v
	 *            the v
	 * @return self
	 */
	public HE_Mesh move(final WB_Coord v) {
		return move(v.xd(), v.yd(), v.zd());
	}

	/**
	 * Translate entire mesh to given position.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return self
	 */
	public HE_Mesh moveTo(final double x, final double y, final double z) {
		if (!isCenterUpdated) {
			getCenter();
		}
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().addSelf(x - center.xd(), y - center.yd(), z - center.zd());
		}
		center = new WB_Point(x, y, z);
		return this;
	}

	/**
	 * Translate entire mesh to given position.
	 *
	 * @param v
	 *            the v
	 * @return self
	 */
	public HE_Mesh moveTo(final WB_Coord v) {
		return moveTo(v.xd(), v.yd(), v.zd());
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle
	 *            angle
	 * @param p1x
	 *            x-coordinate of first point on axis
	 * @param p1y
	 *            y-coordinate of first point on axis
	 * @param p1z
	 *            z-coordinate of first point on axis
	 * @param p2x
	 *            x-coordinate of second point on axis
	 * @param p2y
	 *            y-coordinate of second point on axis
	 * @param p2z
	 *            z-coordinate of second point on axis
	 * @return self
	 */
	public HE_Mesh rotateAbout2PointAxis(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		center = raa.applyAsPoint(center);
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle
	 *            angle
	 * @param p1
	 *            first point on axis
	 * @param p2
	 *            second point on axis
	 * @return self
	 */
	public HE_Mesh rotateAbout2PointAxis(final double angle, final WB_Coord p1, final WB_Coord p2) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		center = raa.applyAsPoint(center);
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis.
	 *
	 * @param angle
	 *            angle
	 * @param p
	 *            rotation point
	 * @param a
	 *            axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Coord p, final WB_Coord a) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		center = raa.applyAsPoint(center);
		return this;
	}

	public HE_Mesh rotateAboutAxis(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(px, py, pz), new WB_Vector(ax, ay, az));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		center = raa.applyAsPoint(center);
		return this;
	}

	/**
	 * Scale entire mesh around center point.
	 *
	 * @param scaleFactorx
	 *            x-coordinate of scale factor
	 * @param scaleFactory
	 *            y-coordinate of scale factor
	 * @param scaleFactorz
	 *            z-coordinate of scale factor
	 * @param c
	 *            center
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactorx, final double scaleFactory, final double scaleFactorz,
			final WB_Coord c) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(c.xd() + (scaleFactorx * (v.xd() - c.xd())), c.yd() + (scaleFactory * (v.yd() - c.yd())),
					c.zd() + (scaleFactorz * (v.zd() - c.zd())));
		}
		center = new WB_Point(c.xd() + (scaleFactorx * (-c.xd() + center.xd())),
				c.yd() + (scaleFactory * (-c.yd() + center.yd())), c.zd() + (scaleFactorz * (-c.zd() + center.zd())));
		;
		return this;
	}

	/**
	 * Scale entire mesh around center point.
	 *
	 * @param scaleFactor
	 *            scale
	 * @param c
	 *            center
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactor, final WB_Coord c) {
		return scale(scaleFactor, scaleFactor, scaleFactor, c);
	}

	/**
	 * Scale entire mesh around bodycenter.
	 *
	 * @param scaleFactorx
	 *            x-coordinate of scale factor
	 * @param scaleFactory
	 *            y-coordinate of scale factor
	 * @param scaleFactorz
	 *            z-coordinate of scale factor
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactorx, final double scaleFactory, final double scaleFactorz) {
		if (!isCenterUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(center.xd() + (scaleFactorx * (v.xd() - center.xd())),
					center.yd() + (scaleFactory * (v.yd() - center.yd())),
					center.zd() + (scaleFactorz * (v.zd() - center.zd())));
		}
		;
		return this;
	}

	/**
	 * Scale entire mesh around bodycenter.
	 *
	 * @param scaleFactor
	 *            scale
	 * @return self
	 */
	public HE_Mesh scale(final double scaleFactor) {
		return scale(scaleFactor, scaleFactor, scaleFactor);
	}

	/**
	 * Get the center (average of all vertex positions).
	 *
	 * @return the center
	 */
	@Override
	public WB_Coord getCenter() {
		if (isCenterUpdated) {
			return center;
		} else {
			resetCenter();
			return center;
		}
	}

	/**
	 * Reset the center to the average of all vertex positions).
	 *
	 */
	public void resetCenter() {
		final WB_Point c = new WB_Point(0, 0, 0);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			c.addSelf(vItr.next());
		}
		c.divSelf(getNumberOfVertices());
		center = c;
		isCenterUpdated = true;
	}



	/**
	 * Cycle halfedges.
	 *
	 * @param halfedges
	 *            halfedges to cycle
	 */
	public void cycleHalfedges(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {
			for (int j = 0; j < (n - 1); j++) {
				he = halfedges.get(j);
				setNext(he,halfedges.get(j + 1));
			}
			he = halfedges.get(n - 1);
			setNext(he,halfedges.get(0));
		}
	}

	/**
	 * Cycle halfedges.
	 *
	 * @param halfedges
	 *            halfedges to cycle
	 */
	public void cycleHalfedgesReverse(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {
			he = halfedges.get(0);
			setNext(he,halfedges.get(n - 1));

			for (int j = 1; j < n; j++) {
				he = halfedges.get(j);
				setNext(he,halfedges.get(j - 1));

			}
		}
	}



	/**
	 * Collect all boundary halfedges.
	 *
	 * @return boundary halfedges
	 */
	public List<HE_Halfedge> getBoundaryHalfedges() {
		final List<HE_Halfedge> boundaryHalfedges = new FastTable<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				boundaryHalfedges.add(he);
			}
		}
		return boundaryHalfedges;
	}

	/**
	 * Try to pair all unpaired halfedges.
	 */
	public List<HE_Halfedge> pairHalfedgesOnePass() {
		tracker.setStatus(this, "Pairing halfedges.", +1);
		class VertexInfo {
			FastTable<HE_Halfedge> out;
			FastTable<HE_Halfedge> in;

			VertexInfo() {
				out = new FastTable<HE_Halfedge>();
				in = new FastTable<HE_Halfedge>();
			}
		}
		final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(1024, 0.5f, -1L);
		tracker.setStatus(this, "Collecting unpaired halfedges.", 0);
		final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
		HE_Vertex v;
		VertexInfo vi;
		WB_ProgressCounter counter = new WB_ProgressCounter(unpairedHalfedges.size(), 10);
		tracker.setStatus(this, "Classifying unpaired halfedges.", counter);
		for (final HE_Halfedge he : unpairedHalfedges) {
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			v = he.getNextInFace().getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.in.add(he);
			counter.increment();
		}
		HE_Halfedge he;
		HE_Halfedge he2;
		counter = new WB_ProgressCounter(vertexLists.size(), 10);
		tracker.setStatus(this, "Pairing unpaired halfedges per vertex.", counter);
		final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
		VertexInfo vInfo;
		final List<HE_Halfedge> mismatchedHalfedges = new FastTable<HE_Halfedge>();
		while (vitr.hasNext()) {
			vitr.advance();
			vInfo = vitr.value();
			for (int i = 0; i < vInfo.out.size(); i++) {
				he = vInfo.out.get(i);
				if (he.getPair() == null) {
					for (int j = 0; j < vInfo.in.size(); j++) {
						he2 = vInfo.in.get(j);
						if (he2.getPair() == null) {
							if ((he.getVertex() == he2.getNextInFace().getVertex())
									&& (he2.getVertex() == he.getNextInFace().getVertex())) {
								setPair(he,he2);
								break;
							}
						}
					}
					for (int j = 0; j < vInfo.out.size(); j++) {
						he2 = vInfo.out.get(j);
						if ((he2 != he) && (he2.getPair() == null)) {
							if (he.getNextInFace().getVertex() == he2.getNextInFace().getVertex()) {
								mismatchedHalfedges.add(he);
								mismatchedHalfedges.add(he2);
								break;
							}
						}
					}
				}
			}
			counter.increment();
		}
		tracker.setStatus(this, "Processed unpaired halfedges.", -1);
		return mismatchedHalfedges;
	}

	public void pairHalfedges() {
		pairHalfedgesOnePass();
	}

	boolean isNeighbor(final HE_RAS<HE_Face> partition1, final HE_RAS<HE_Face> partition2) {
		HE_Halfedge he1, he2;
		HE_Vertex v1;
		HE_FaceHalfedgeInnerCirculator heitr1, heitr2;
		for (final HE_Face f1 : partition1) {
			heitr1 = new HE_FaceHalfedgeInnerCirculator(f1);
			while (heitr1.hasNext()) {
				he1 = heitr1.next();
				if (he1.getPair() == null) {
					v1 = he1.getNextInFace().getVertex();
					for (final HE_Face f2 : partition2) {
						heitr2 = new HE_FaceHalfedgeInnerCirculator(f2);
						while (heitr2.hasNext()) {
							he2 = heitr2.next();
							if ((he2.getPair() == null) && (he2.getNextInFace().getVertex() == v1)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Pair halfedges.
	 *
	 * @param unpairedHalfedges
	 *            the unpaired halfedges
	 */
	public void pairHalfedges(final List<HE_Halfedge> unpairedHalfedges) {
		tracker.setStatus(this, "Pairing halfedges.", +1);
		class VertexInfo {
			FastTable<HE_Halfedge> out;
			FastTable<HE_Halfedge> in;

			VertexInfo() {
				out = new FastTable<HE_Halfedge>();
				in = new FastTable<HE_Halfedge>();
			}
		}
		final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(1024, 0.5f, -1L);
		HE_Vertex v;
		VertexInfo vi;
		WB_ProgressCounter counter = new WB_ProgressCounter(unpairedHalfedges.size(), 10);
		tracker.setStatus(this, "Classifying unpaired halfedges.", counter);
		for (final HE_Halfedge he : unpairedHalfedges) {
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			v = he.getNextInFace().getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.in.add(he);
			counter.increment();
		}
		HE_Halfedge he;
		HE_Halfedge he2;
		counter = new WB_ProgressCounter(vertexLists.size(), 10);
		tracker.setStatus(this, "Pairing unpaired halfedges per vertex.", counter);
		final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
		VertexInfo vInfo;
		while (vitr.hasNext()) {
			vitr.advance();
			vInfo = vitr.value();
			for (int i = 0; i < vInfo.out.size(); i++) {
				he = vInfo.out.get(i);
				if (he.getPair() == null) {
					for (int j = 0; j < vInfo.in.size(); j++) {
						he2 = vInfo.in.get(j);
						if (he2.getPair() == null) {
							if ((he.getVertex() == he2.getNextInFace().getVertex())
									&& (he2.getVertex() == he.getNextInFace().getVertex())) {
								setPair(he,he2);
								break;
							}
						}
					}
					for (int j = 0; j < vInfo.out.size(); j++) {
						he2 = vInfo.out.get(j);
						if ((he2 != he) && (he2.getPair() == null)) {
							if (he.getNextInFace().getVertex() == he2.getNextInFace().getVertex()) {
								System.out.println("Two identical halfedges found!");
								break;
							}
						}
					}
				}
			}
			counter.increment();
		}
		tracker.setStatus(this, "Processed unpaired halfedges.", -1);
	}

	/**
	 * Cap all remaining unpaired halfedges. Only use after pairHalfedges();
	 */
	public void capHalfedges() {

		tracker.setStatus(this, "Capping unpaired halfedges.", +1);
		final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
		final int nuh = unpairedHalfedges.size();
		final HE_Halfedge[] newHalfedges = new HE_Halfedge[nuh];
		HE_Halfedge he1, he2;
		WB_ProgressCounter counter = new WB_ProgressCounter(nuh, 10);
		tracker.setStatus(this, "Capping unpaired halfedges.", counter);
		for (int i = 0; i < nuh; i++) {
			he1 = unpairedHalfedges.get(i);
			he2 = new HE_Halfedge();
			setVertex(he2,he1.getNextInFace().getVertex());
			setPair(he1,he2);
			newHalfedges[i] = he2;
			add(he2);
			counter.increment();
		}
		counter = new WB_ProgressCounter(nuh, 10);
		tracker.setStatus(this, "Cycling new halfedges.", counter);
		for (int i = 0; i < nuh; i++) {
			he1 = newHalfedges[i];
			if (he1.getNextInFace() == null) {
				for (int j = 0; j < nuh; j++) {
					he2 = newHalfedges[j];
					if (!he2.isVisited()) {
						if (he2.getVertex() == he1.getPair().getVertex()) {
							setNext(he1,he2);
							he2.setVisited();
							break;
						}
					}
				}
			}
			counter.increment();
		}
		tracker.setStatus(this, "Processed unpaired halfedges.", -1);
	}

	/**
	 * Uncap halfedges.
	 */
	public void uncapBoundaryHalfedges() {
		tracker.setStatus(this, "Uncapping boundary halfedges.", +1);
		WB_ProgressCounter counter = new WB_ProgressCounter(getNumberOfHalfedges(), 10);
		tracker.setStatus(this, "Detecting and uncapping boundary edges.", 0);

		List<HE_Halfedge> halfedges=getHalfedges();
		final HE_RAS<HE_Halfedge> keep = new HE_RASTrove<HE_Halfedge>();
		for (HE_Halfedge he: halfedges) {

			if (he.getFace() == null) {
				setHalfedge(he.getVertex(),he.getNextInVertex());
				clearPair(he);
			} else {
				keep.add(he);
			}
			counter.increment();
		}
		clearHalfedges();
		addHalfedges(keep);
		tracker.setStatus(this, "Removing outer boundary halfedges.", -1);

	}

	/**
	 * Cap holes.
	 *
	 * @return all new faces as FastTable<HE_Face>
	 */
	public List<HE_Face> capHoles() {
		tracker.setStatus(this, "Capping simple planar holes.", +1);
		final List<HE_Face> caps = new FastTable<HE_Face>();
		final List<HE_Halfedge> unpairedEdges = getUnpairedHalfedges();
		HE_RAS<HE_Halfedge> loopedHalfedges;
		HE_Halfedge start;
		HE_Halfedge he;
		HE_Halfedge hen;
		HE_Face nf;
		HE_RAS<HE_Halfedge> newHalfedges;
		HE_Halfedge phe;
		HE_Halfedge nhe;
		WB_ProgressCounter counter = new WB_ProgressCounter(unpairedEdges.size(), 10);
		tracker.setStatus(this, "Finding loops and closing holes.", counter);
		while (unpairedEdges.size() > 0) {
			loopedHalfedges = new HE_RASTrove<HE_Halfedge>();
			start = unpairedEdges.get(0);
			loopedHalfedges.add(start);
			he = start;
			hen = start;
			boolean stuck = false;
			do {
				for (int i = 0; i < unpairedEdges.size(); i++) {
					hen = unpairedEdges.get(i);
					if (hen.getVertex() == he.getNextInFace().getVertex()) {
						loopedHalfedges.add(hen);
						break;
					}
				}
				if (hen.getVertex() != he.getNextInFace().getVertex()) {
					stuck = true;
				}
				he = hen;
			} while ((hen.getNextInFace().getVertex() != start.getVertex()) && (!stuck));
			unpairedEdges.removeAll(loopedHalfedges);
			nf = new HE_Face();
			add(nf);
			caps.add(nf);
			newHalfedges = new HE_RASTrove<HE_Halfedge>();
			for (int i = 0; i < loopedHalfedges.size(); i++) {
				phe = loopedHalfedges.get(i);
				nhe = new HE_Halfedge();
				add(nhe);
				newHalfedges.add(nhe);
				setVertex(nhe,phe.getNextInFace().getVertex());
				setFace(nhe,nf);
				setPair(nhe,phe);
				if (nf.getHalfedge() == null) {
					setHalfedge(nf,nhe);
				}
			}
			cycleHalfedgesReverse(newHalfedges.getObjects());
			counter.increment(newHalfedges.size());
		}
		// caps = triangulateConcaveFaces(caps).getFacesAsList();
		// System.out.println(caps.size());
		tracker.setStatus(this, "Capped simple planar holes.", -1);
		return caps;
	}

	/**
	 * Clean all mesh elements not used by any faces.
	 *
	 * @return self
	 */
	public HE_Mesh cleanUnusedElementsByFace() {

		return HET_MeshOp.cleanUnusedElementsByFace(this);
	}

	/**
	 * Reverse all faces. Flips normals.
	 *
	 * @return
	 */
	public HE_Mesh flipAllFaces() {
		return HET_MeshOp.flipAllFaces(this);
	}


	/**
	 *
	 *
	 * @param he
	 * @return
	 */
	public boolean flipEdge(final HE_Halfedge he) {
		return HET_MeshOp.flipEdge(this,he);
	}

	/**
	 * Collapse halfedge. Start vertex is removed. Degenerate faces are removed.
	 * This function can result in non-manifold meshes.
	 *
	 * @param he
	 *            the he
	 * @return true, if successful
	 */
	public boolean collapseHalfedge(final HE_Halfedge he) {
		if (contains(he)) {
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f,hen);
			}
			if (fp != null) {
				setHalfedge(fp,hePairn);
			}
			setNext(hep,hen);
			setNext(hePairp,hePairn);
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i),vp);
			}
			setHalfedge(vp,hen);
			remove(he);
			remove(hePair);
			remove(v);
			deleteTwoEdgeFace(f);
			deleteTwoEdgeFace(fp);
			return true;
		}
		return false;
	}

	/**
	 * Collapse halfedge bp.
	 *
	 * @param he
	 *            the he
	 * @return true, if successful
	 */
	public boolean collapseHalfedgeBP(final HE_Halfedge he) {
		if (contains(he)) {
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			if (v.isBoundary()) {
				return false;
			}
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i),vp);
			}
			setHalfedge(vp,hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f,hen);
			}
			if (fp != null) {
				setHalfedge(fp,hePairn);
			}
			setNext(hep,hen);
			setNext(hePairp,hePairn);
			remove(he);
			remove(hePair);
			remove(v);
			deleteTwoEdgeFace(f);
			deleteTwoEdgeFace(fp);
			return true;
		}
		return false;
	}

	/**
	 * Collapse edge. End vertices are averaged. Degenerate faces are removed.
	 * This function can result in non-manifold meshes.
	 *
	 * @param e
	 *            edge to collapse
	 * @return true, if successful
	 */
	public boolean collapseEdge(final HE_Halfedge e) {
		if (contains(e)) {
			final HE_Halfedge he = (e.isEdge()) ? e : e.getPair();
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			vp.addSelf(v).mulSelf(0.5);
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i),vp);
			}
			setHalfedge(vp,hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f,hen);
			}
			if (fp != null) {
				setHalfedge(fp,hePairn);
			}
			setNext(hep,hen);
			setNext(hePairp,hePairn);
			remove(he);
			remove(hePair);
			remove(v);
			deleteTwoEdgeFace(f);
			deleteTwoEdgeFace(fp);
			return true;
		}
		return false;
	}

	/**
	 *
	 *
	 * @param e
	 * @param strict
	 * @return
	 */
	public boolean collapseEdgeBP(final HE_Halfedge e, final boolean strict) {
		if (contains(e)) {
			final HE_Halfedge he = (e.isEdge()) ? e : e.getPair();
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			if (v.isBoundary()) {
				if (vp.isBoundary()) {
					if ((!he.isInnerBoundary()) || strict) {
						return false;
					}
					vp.addSelf(v).mulSelf(0.5);
				} else {
					vp.set(v);
				}
			} else {
				if (!vp.isBoundary()) {
					vp.addSelf(v).mulSelf(0.5);
				}
			}
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i),vp);
			}
			setHalfedge(vp,hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f,hen);
			}
			if (fp != null) {
				setHalfedge(fp,hePairn);
			}
			setNext(hep,hen);
			setNext(hePairp,hePairn);
			remove(he);
			remove(hePair);
			remove(e);
			remove(v);
			deleteTwoEdgeFace(f);
			deleteTwoEdgeFace(fp);
			return true;
		}
		return false;
	}

	/**
	 * Remove a face if it has only two vertices and stitch the mesh together.
	 *
	 * @param f
	 *            face to check
	 */
	public void deleteTwoEdgeFace(final HE_Face f) {
		if (contains(f)) {
			final HE_Halfedge he = f.getHalfedge();
			final HE_Halfedge hen = he.getNextInFace();
			if (he == hen.getNextInFace()) {
				final HE_Halfedge hePair = he.getPair();
				final HE_Halfedge henPair = hen.getPair();
				remove(f);
				remove(he);
				setHalfedge(he.getVertex(),he.getNextInVertex());
				remove(hen);
				setHalfedge(hen.getVertex(),hen.getNextInVertex());
				setPair(hePair,henPair);

			}
		}
	}

	public void deleteTwoEdgeFaces() {
		HE_FaceIterator fItr=fItr();
		HE_Face f;
		while(fItr.hasNext())
		{
			f=fItr.next();
			final HE_Halfedge he = f.getHalfedge();
			final HE_Halfedge hen = he.getNextInFace();
			if (he == hen.getNextInFace()) {
				final HE_Halfedge hePair = he.getPair();
				final HE_Halfedge henPair = hen.getPair();
				remove(f);
				remove(he);
				setHalfedge(he.getVertex(),he.getNextInVertex());
				remove(hen);
				setHalfedge(hen.getVertex(),hen.getNextInVertex());
				setPair(hePair,henPair);

			}
		}
	}

	/**
	 *
	 *
	 * @param v
	 */
	public void deleteTwoEdgeVertex(final HE_Vertex v) {
		if (contains(v) && (v.getVertexOrder() == 2)) {
			final HE_Halfedge he0 = v.getHalfedge();
			final HE_Halfedge he1 = he0.getNextInVertex();
			final HE_Halfedge he0n = he0.getNextInFace();
			final HE_Halfedge he1n = he1.getNextInFace();
			final HE_Halfedge he0p = he0.getPair();
			final HE_Halfedge he1p = he1.getPair();
			setNext(he0p,he1n);
			setNext(he1p,he0n);
			if (he0.getFace() != null) {
				setHalfedge(he0.getFace(),he1p);
			}
			if (he1.getFace() != null) {
				setHalfedge(he1.getFace(),he0p);
			}
			setHalfedge(he0n.getVertex(),he0n);
			setHalfedge(he1n.getVertex(),he1n);
			setPair(he0p,he1p);
			remove(he0);
			remove(he1);
			remove(v);
		}
	}

	/**
	 *
	 */
	public void deleteTwoEdgeVertices() {
		final HE_VertexIterator vitr = vItr();
		HE_Vertex v;
		final List<HE_Vertex> toremove = new FastTable<HE_Vertex>();
		while (vitr.hasNext()) {
			v = vitr.next();
			if (v.getVertexOrder() == 2) {
				toremove.add(v);
			}
		}
		for (final HE_Vertex vtr : toremove) {
			deleteTwoEdgeVertex(vtr);
		}
	}



	/**
	 * Collapse all zero-length edges.
	 *
	 */
	public void collapseDegenerateEdges() {
		final FastTable<HE_Halfedge> edgesToRemove = new FastTable<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (isZeroSq(WB_GeometryOp.getSqDistance3D(e.getVertex(), e.getEndVertex()))) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			collapseEdge(edgesToRemove.get(i));
		}
	}

	/**
	 *
	 *
	 * @param d
	 */
	public void collapseDegenerateEdges(final double d) {
		final FastTable<HE_Halfedge> edgesToRemove = new FastTable<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		final double d2 = d * d;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (WB_GeometryOp.getSqDistance3D(e.getVertex(), e.getEndVertex()) < d2) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			collapseEdge(edgesToRemove.get(i));
		}
	}

	/**
	 * Delete face and remove all references.
	 *
	 * @param f
	 *            face to delete
	 */
	public void deleteFace(final HE_Face f) {
		HE_Halfedge he = f.getHalfedge();
		do {
			clearFace(he);
			he = he.getNextInFace();
		} while (he != f.getHalfedge());
		remove(f);
	}

	/**
	 * Delete edge. Adjacent faces are fused.
	 *
	 * @param e
	 *            edge to delete
	 * @return fused face (or null)
	 */
	public HE_Face deleteEdge(final HE_Halfedge e) {
		HE_Face f = null;
		final HE_Halfedge he1 = e.isEdge() ? e : e.getPair();
		final HE_Halfedge he2 = he1.getPair();
		final HE_Halfedge he1n = he1.getNextInFace();
		final HE_Halfedge he2n = he2.getNextInFace();
		final HE_Halfedge he1p = he1.getPrevInFace();
		final HE_Halfedge he2p = he2.getPrevInFace();
		HE_Vertex v = he1.getVertex();
		if (v.getHalfedge() == he1) {
			setHalfedge(v,he1.getNextInVertex());
		}
		v = he2.getVertex();
		if (v.getHalfedge() == he2) {
			setHalfedge(v,he2.getNextInVertex());
		}
		setNext(he1p,he2n);
		setNext(he2p,he1n);
		if ((he1.getFace() != null) && (he2.getFace() != null)) {
			f = new HE_Face();
			add(f);
			setHalfedge(f,he1p);
			HE_Halfedge he = he1p;
			do {
				setFace(he,f);
				he = he.getNextInFace();
			} while (he != he1p);
		}
		if (he1.getFace() != null) {
			remove(he1.getFace());
		}
		if (he2.getFace() != null) {
			remove(he2.getFace());
		}
		remove(he1);
		remove(he2);
		return f;
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
	public HE_Selection splitEdge(final HE_Halfedge edge, final WB_Coord v) {
		return HET_MeshOp.splitEdge(edge, v, this);
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
	public HE_Selection splitEdge(final Long key, final WB_Coord v) {
		final HE_Halfedge edge = getHalfedgeWithKey(key);
		return splitEdge(edge, v);
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
	public void splitEdge(final HE_Halfedge edge, final double x, final double y, final double z) {
		splitEdge(edge, new WB_Point(x, y, z));
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
	public void splitEdge(final long key, final double x, final double y, final double z) {
		splitEdge(key, new WB_Point(x, y, z));
	}

	/**
	 * Split edge in half.
	 *
	 * @param edge
	 *            edge to split.
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final HE_Halfedge edge) {
		return HET_MeshOp.splitEdge(edge, this);
	}

	/**
	 * Split edge in half.
	 *
	 * @param key
	 *            key of edge to split.
	 * @return selection of new vertex and new edge
	 */
	public HE_Selection splitEdge(final long key) {
		return HET_MeshOp.splitEdge(key, this);
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
	public HE_Selection splitEdge(final HE_Halfedge edge, final double f) {
		return HET_MeshOp.splitEdge(edge, f, this);
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
	public HE_Selection splitEdge(final long key, final double f) {
		return HET_MeshOp.splitEdge(key, f, this);
	}

	/**
	 * Split all edges in half.
	 *
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges() {
		return HET_MeshOp.splitEdges(this);
	}

	/**
	 * Split all edges in half, offset the center by a given distance along the
	 * edge normal.
	 *
	 * @param offset
	 *            the offset
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges(final double offset) {
		return HET_MeshOp.splitEdges(offset, this);
	}

	/**
	 * Split edge in half.
	 *
	 * @param selection
	 *            edges to split.
	 * @return selection of new vertices and new edges
	 */
	public HE_Selection splitEdges(final HE_Selection selection) {
		return HET_MeshOp.splitEdges(selection, this);
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
	public HE_Selection splitEdges(final HE_Selection selection, final double offset) {
		return HET_MeshOp.splitEdges(selection, offset, this);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final HE_Halfedge edge, final double[] f) {
		HET_MeshOp.splitEdge(edge, f, this);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final long key, final double[] f) {
		HET_MeshOp.splitEdge(key, f, this);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param edge
	 *            edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final HE_Halfedge edge, final float[] f) {
		HET_MeshOp.splitEdge(edge, f, this);
	}

	/**
	 * Split edge in multiple parts.
	 *
	 * @param key
	 *            key of edge to split
	 * @param f
	 *            array of fractions (0..1)
	 */
	public void splitEdge(final long key, final float[] f) {
		HET_MeshOp.splitEdge(key, f, this);
	}

	/**
	 * Divide edge.
	 *
	 * @param origE
	 *            edge to divide
	 * @param n
	 *            number of parts
	 */
	public void divideEdge(final HE_Halfedge edge, final int n) {
		HET_MeshOp.divideEdge(edge, n, this);
	}

	/**
	 * Divide edge.
	 *
	 * @param key
	 *            key of edge to divide
	 * @param n
	 *            number of parts
	 */
	public void divideEdge(final long key, final int n) {
		HET_MeshOp.divideEdge(key, n, this);
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
	public HE_Selection splitFace(final HE_Face face, final HE_Vertex vi, final HE_Vertex vj) {
		return HET_MeshOp.splitFace(face, vi, vj, this);
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
	public HE_Selection splitFace(final long fkey, final long vkeyi, final long vkeyj) {
		return HET_MeshOp.splitFace(fkey, vkeyi, vkeyj, this);
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param d
	 *            offset along face normal
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri(final double d) {
		return HET_MeshOp.splitFacesTri(d, this);
	}

	/**
	 * Tri split faces.
	 *
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri() {
		return HET_MeshOp.splitFacesTri(this);
	}

	/**
	 * Tri split faces.
	 *
	 * @param selection
	 *            face selection to split
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri(final HE_Selection selection) {
		return HET_MeshOp.splitFacesTri(selection, this);
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
	public HE_Selection splitFacesTri(final HE_Selection selection, final double d) {
		return HET_MeshOp.splitFacesTri(selection, d, this);
	}

	/**
	 * Quad split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad() {
		return HET_MeshOp.splitFacesQuad(this);
	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel
	 *            selection to split
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad(final HE_Selection sel) {
		return HET_MeshOp.splitFacesQuad(sel, this);
	}

	/**
	 * Quad split faces.
	 *
	 * @param d
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad(final double d) {
		return HET_MeshOp.splitFacesQuad(d, this);
	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel
	 *            selection to split
	 * @param d
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad(final HE_Selection sel, final double d) {
		return HET_MeshOp.splitFacesQuad(sel, d, this);
	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesHybrid() {
		return HET_MeshOp.splitFacesHybrid(this);
	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @param sel
	 *            the sel
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesHybrid(final HE_Selection sel) {
		return HET_MeshOp.splitFacesHybrid(sel, this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Selection splitFacesCenter() {
		return HET_MeshOp.splitFacesCenter(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Selection splitFacesCenterHole() {
		return HET_MeshOp.splitFacesCenterHole(this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @return
	 */
	public HE_Selection splitFacesCenter(final HE_Selection faces) {
		return HET_MeshOp.splitFacesCenter(faces, this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @return
	 */
	public HE_Selection splitFacesCenterHole(final HE_Selection faces) {
		return HET_MeshOp.splitFacesCenterHole(faces, this);
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HE_Selection splitFacesCenter(final double d) {
		return HET_MeshOp.splitFacesCenter(d, this);
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HE_Selection splitFacesCenterHole(final double d) {
		return HET_MeshOp.splitFacesCenterHole(d, this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @return
	 */
	public HE_Selection splitFacesCenter(final HE_Selection faces, final double d) {
		return HET_MeshOp.splitFacesCenter(faces, d, this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @return
	 */
	public HE_Selection splitFacesCenterHole(final HE_Selection faces, final double d) {
		return HET_MeshOp.splitFacesCenterHole(faces, d, this);
	}

	/**
	 *
	 *
	 * @param d
	 * @param c
	 * @return
	 */
	public HE_Selection splitFacesCenter(final double d, final double c) {
		return HET_MeshOp.splitFacesCenter(d, c, this);
	}

	/**
	 *
	 *
	 * @param d
	 * @param c
	 * @return
	 */
	public HE_Selection splitFacesCenterHole(final double d, final double c) {
		return HET_MeshOp.splitFacesCenterHole(d, c, this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @param c
	 * @return
	 */
	public HE_Selection splitFacesCenter(final HE_Selection faces, final double d, final double c) {
		return HET_MeshOp.splitFacesCenter(faces, d, c, this);
	}

	/**
	 *
	 *
	 * @param faces
	 * @param d
	 * @param c
	 * @return
	 */
	public HE_Selection splitFacesCenterHole(final HE_Selection faces, final double d, final double c) {
		return HET_MeshOp.splitFacesCenterHole(faces, d, c, this);
	}

	/**
	 * Midedge split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesMidEdge() {
		return HET_MeshOp.splitFacesMidEdge(this);
	}

	/**
	 * Mid edge split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesMidEdgeHole() {
		return HET_MeshOp.splitFacesMidEdgeHole(this);
	}

	/**
	 * Mid edge split selected faces.
	 *
	 * @param selection
	 *            selection to split
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesMidEdge(final HE_Selection selection) {
		return HET_MeshOp.splitFacesMidEdge(selection, this);
	}

	/**
	 *
	 *
	 * @param selection
	 * @return
	 */
	public HE_Selection splitFacesMidEdgeHole(final HE_Selection selection) {
		return HET_MeshOp.splitFacesMidEdgeHole(selection, this);
	}

	/**
	 * Triangulate all concave faces.
	 *
	 */
	public HE_Selection triangulateConcaveFaces() {
		return HET_MeshOp.triangulateConcaveFaces(this);
	}

	/**
	 *
	 *
	 * @param sel
	 */
	public HE_Selection triangulateConcaveFaces(final List<HE_Face> sel) {
		return HET_MeshOp.triangulateConcaveFaces(sel, this);
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param key
	 *            key of face
	 */
	public HE_Selection triangulateConcaveFace(final long key) {
		return HET_MeshOp.triangulateConcaveFace(key, this);
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param face
	 *            key of face
	 */
	public HE_Selection triangulateConcaveFace(final HE_Face face) {
		return HET_MeshOp.triangulateConcaveFace(face, this);
	}

	/**
	 * Expand vertex to new edge.
	 *
	 * @param v
	 *            vertex to expand
	 * @param f1
	 *            first face
	 * @param f2
	 *            second face
	 * @param vn
	 *            position of new vertex
	 */
	public void expandVertexToEdge(final HE_Vertex v, final HE_Face f1, final HE_Face f2, final WB_Coord vn) {
		if (f1 == f2) {
			return;
		}
		HE_Halfedge he = v.getHalfedge();
		HE_Halfedge he1 = new HE_Halfedge();
		HE_Halfedge he2 = new HE_Halfedge();
		do {
			if (he.getFace() == f1) {
				he1 = he;
			}
			if (he.getFace() == f2) {
				he2 = he;
			}
			he = he.getNextInVertex();
		} while (he != v.getHalfedge());
		final HE_Vertex vNew = new HE_Vertex(vn);
		setHalfedge(vNew,he1);
		add(vNew);
		he = he1;
		do {
			setVertex(he,vNew);
			he = he.getNextInVertex();
		} while (he != he2);
		final HE_Halfedge he1p = he1.getPrevInFace();
		final HE_Halfedge he2p = he2.getPrevInFace();
		final HE_Halfedge he1new = new HE_Halfedge();
		final HE_Halfedge he2new = new HE_Halfedge();
		add(he1new);
		add(he2new);
		setVertex(he1new,v);
		setVertex(he2new,vNew);
		setNext(he1p,he1new);
		setNext(he1new,he1);
		setNext(he2p,he2new);
		setNext(he2new,he2);
		setPair(he1new,he2new);
		setFace(he1new,f1);
		setFace(he2new,f2);
	}

	/**
	 * Check consistency of datastructure.
	 *
	 * @return true or false
	 */
	public boolean validate() {
		return HET_Diagnosis.validate(this);
	}

	/**
	 * Check if point lies inside mesh.
	 *
	 * @param p
	 *            point to check
	 * @param isConvex
	 *            do fast check, convex meshes only
	 * @return true or false
	 */
	public boolean contains(final WB_Coord p, final boolean isConvex) {
		final WB_Vector dir = new WB_Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
		final WB_Ray R = new WB_Ray(p, dir);
		int c = 0;
		WB_Plane P;
		WB_IntersectionResult lpi;
		HE_Face face;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			P = face.getPlane();
			if (isConvex) {
				if (WB_GeometryOp.classifyPointToPlane3D(p, P) == WB_Classification.FRONT) {
					return false;
				}
			} else {
				lpi = WB_GeometryOp.getIntersection3D(R, P);
				if (lpi.intersection) {
					if (pointIsInFace((WB_Point) lpi.object, face)) {
						/*
						 * if (!HE_Mesh.pointIsStrictlyInFace( (WB_Point)
						 * lpi.object, face)) { return contains(p, isConvex); }
						 */
						c++;
					}
				}
			}
		}
		return (isConvex) ? true : ((c % 2) == 1);
	}

	/**
	 * Check if point lies inside or on edge of face.
	 *
	 * @param p
	 *            point
	 * @param f
	 *            the f
	 * @return true/false
	 */
	public static boolean pointIsInFace(final WB_Coord p, final HE_Face f) {
		return isZero(WB_GeometryOp.getDistanceToClosestPoint3D(p, f.toPolygon()));
	}

	/**
	 * Check if point lies strictly inside face.
	 *
	 * @param p
	 *            point
	 * @param f
	 *            the f
	 * @return true/false
	 */
	public static boolean pointIsStrictlyInFace(final WB_Coord p, final HE_Face f) {
		final WB_Polygon poly = f.toPolygon();
		if (!isZeroSq(WB_GeometryOp.getSqDistance3D(p, WB_GeometryOp.getClosestPoint3D(p, poly)))) {
			return false;
		}
		if (!isZeroSq(WB_GeometryOp.getSqDistance3D(p, WB_GeometryOp.getClosestPointOnPeriphery3D(p, poly)))) {
			return false;
		}
		return true;
	}

	/**
	 * Fit in aabb.
	 *
	 * @param AABB
	 *
	 */
	public void fitInAABB(final WB_AABB AABB) {
		final WB_AABB self = getAABB();
		move(new WB_Vector(self.getMin(), AABB.getMin()));
		scale(AABB.getWidth() / self.getWidth(), AABB.getHeight() / self.getHeight(), AABB.getDepth() / self.getDepth(),
				new WB_Point(AABB.getMin()));
	}

	/**
	 * Fit in aabb constrained.
	 *
	 * @param AABB
	 *
	 * @return
	 */
	public double fitInAABBConstrained(final WB_AABB AABB) {
		final WB_AABB self = getAABB();
		move(new WB_Vector(self.getCenter(), AABB.getCenter()));
		double f = Math.min(AABB.getWidth() / self.getWidth(), AABB.getHeight() / self.getHeight());
		f = Math.min(f, AABB.getDepth() / self.getDepth());
		scale(f, new WB_Point(AABB.getCenter()));
		return f;
	}

	/**
	 * Delete face and remove all references.
	 *
	 * @param faces
	 *            faces to delete
	 */
	public void delete(final HE_Selection faces) {
		HE_Face f;
		final Iterator<HE_Face> fItr = faces.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			remove(f);
		}
		cleanUnusedElementsByFace();
		capHalfedges();
	}

	/**
	 * Select all faces.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllFaces() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addFaces(getFacesAsArray());
		return _selection;
	}

	/**
	 *
	 *
	 * @param chance
	 * @return
	 */
	public HE_Selection selectRandomFaces(final double chance) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (Math.random() <= chance) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	public HE_Selection selectRandomFaces(final double chance, final long seed) {
		final WB_MTRandom random = new WB_MTRandom(seed);
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (random.nextFloat() <= chance) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 * Select all faces with given label.
	 *
	 * @param label
	 *            the label
	 * @return the h e_ selection
	 */
	public HE_Selection selectFacesWithLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param label
	 * @return
	 */
	public HE_Selection selectFacesWithInternalLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getInternalLabel() == label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public HE_Selection selectFacesWithNormal(final WB_Coord v) {
		final HE_Selection _selection = new HE_Selection(this);
		final WB_Vector w = new WB_Vector(v);
		w.normalizeSelf();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (WB_Vector.dot(f.getFaceNormal(), v) > (1.0 - WB_Epsilon.EPSILON)) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param P
	 * @return
	 */
	public HE_Selection selectFaces(final WB_Plane P) {
		final HE_Selection _selection = new HE_Selection(this);
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (WB_GeometryOp.classifyPolygonToPlane3D(f.toPolygon(), P) == WB_Classification.FRONT) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param P
	 * @return
	 */
	public HE_Selection selectCrossingFaces(final WB_Plane P) {
		final HE_Selection _selection = new HE_Selection(this);
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (WB_GeometryOp.classifyPolygonToPlane3D(f.toPolygon(), P) == WB_Classification.CROSSING) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 * Select all faces except with given label.
	 *
	 * @param label
	 *            the label
	 * @return the h e_ selection
	 */
	public HE_Selection selectFacesWithOtherLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() != label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param label
	 * @return
	 */
	public HE_Selection selectFacesWithOtherInternalLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getInternalLabel() != label) {
				_selection.add(f);
			}
		}
		return _selection;
	}

	/**
	 * Select all edges.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllEdges() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addHalfedges(getEdgesAsArray());
		return _selection;
	}

	/**
	 * Select all halfedges.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addHalfedges(getHalfedgesAsArray());
		return _selection;
	}

	/**
	 * Select all vertices.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllVertices() {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addVertices(getVerticesAsArray());
		return _selection;
	}

	/**
	 * Select all vertices with given label.
	 *
	 * @param label
	 *            the label
	 * @return the h e_ selection
	 */
	public HE_Selection selectVerticesWithLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() == label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 * Select all vertices except with given label.
	 *
	 * @param label
	 *            the label
	 * @return the h e_ selection
	 */
	public HE_Selection selectVerticesWithOtherLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getLabel() != label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 *
	 *
	 * @param label
	 * @return
	 */
	public HE_Selection selectVerticesWithInternalLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getInternalLabel() == label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 * Select all vertices except with given label.
	 *
	 * @param label
	 *            the label
	 * @return the h e_ selection
	 */
	public HE_Selection selectVerticesWithOtherInternalLabel(final int label) {
		final HE_Selection _selection = new HE_Selection(this);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getInternalLabel() != label) {
				_selection.add(v);
			}
		}
		return _selection;
	}

	/**
	 * Select all halfedges on inside of boundary.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllInnerBoundaryHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair().getFace() == null) {
				_selection.add(he);
			}
		}
		return _selection;
	}

	/**
	 * Select all halfedges on outside of boundary.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllOuterBoundaryHalfedges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he);
			}
		}
		return _selection;
	}

	/**
	 * Select all edges on boundary.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllBoundaryEdges() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.isInnerBoundary()) {
				_selection.add(he);
			}
		}
		return _selection;
	}

	/**
	 * Select all faces on boundary.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllBoundaryFaces() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he.getPair().getFace());
			}
		}
		return _selection;
	}

	/**
	 * Select all vertices on boundary.
	 *
	 * @return the h e_ selection
	 */
	public HE_Selection selectAllBoundaryVertices() {
		final HE_Selection _selection = new HE_Selection(this);
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_selection.add(he.getVertex());
			}
		}
		return _selection;
	}

	/**
	 * Fuse all coplanar faces connected to face. New face can be concave.
	 *
	 * @param face
	 *            starting face
	 * @param a
	 *            the a
	 * @return new face
	 */
	public HE_Face fuseCoplanarFace(final HE_Face face, final double a) {
		List<HE_Face> neighbors;
		FastTable<HE_Face> facesToCheck = new FastTable<HE_Face>();
		final FastTable<HE_Face> newFacesToCheck = new FastTable<HE_Face>();
		facesToCheck.add(face);
		final HE_Selection sel = new HE_Selection(this);
		sel.add(face);
		HE_Face f;
		HE_Face fn;
		int ni = -1;
		int nf = 0;
		double sa = Math.sin(a);
		sa *= sa;
		while (ni < nf) {
			newFacesToCheck.clear();
			for (int i = 0; i < facesToCheck.size(); i++) {
				f = facesToCheck.get(i);
				neighbors = f.getNeighborFaces();
				for (int j = 0; j < neighbors.size(); j++) {
					fn = neighbors.get(j);
					if (!sel.contains(fn)) {
						if (WB_Vector.isParallel(f.getFaceNormal(), fn.getFaceNormal(), sa)) {
							sel.add(fn);
							newFacesToCheck.add(fn);
						}
					}
				}
			}
			facesToCheck = newFacesToCheck;
			ni = nf;
			nf = sel.getNumberOfFaces();
		}
		if (sel.getNumberOfFaces() == 1) {
			return face;
		}
		final List<HE_Halfedge> halfedges = sel.getOuterHalfedgesInside();
		final HE_Face newFace = new HE_Face();
		add(newFace);
		newFace.copyProperties(sel.getFaceWithIndex(0));
		setHalfedge(newFace,halfedges.get(0));
		for (int i = 0; i < halfedges.size(); i++) {
			final HE_Halfedge hei = halfedges.get(i);
			final HE_Halfedge hep = halfedges.get(i).getPair();
			for (int j = 0; j < halfedges.size(); j++) {
				final HE_Halfedge hej = halfedges.get(j);
				if ((i != j) && (hep.getVertex() == hej.getVertex())) {
					setNext(hei,hej);
				}
			}
			setFace(hei,newFace);
			setHalfedge(hei.getVertex(),hei);
		}
		removeFaces(sel.getFacesAsArray());
		cleanUnusedElementsByFace();
		return newFace;
	}

	/**
	 * Fuse all planar faces. Can lead to concave faces.
	 *
	 */
	public void fuseCoplanarFaces() {
		fuseCoplanarFaces(0);
	}

	/**
	 * Fuse all planar faces. Can lead to concave faces.
	 *
	 * @param a
	 *            the a
	 */
	public void fuseCoplanarFaces(final double a) {
		int ni;
		int no;
		do {
			ni = getNumberOfFaces();
			final List<HE_Face> faces = this.getFacesAsList();
			for (int i = 0; i < faces.size(); i++) {
				final HE_Face f = faces.get(i);
				if (contains(f)) {
					fuseCoplanarFace(f, a);
				}
			}
			no = getNumberOfFaces();
		} while (no < ni);
	}

	/**
	 * Remove all redundant vertices in straight edges.
	 *
	 */
	public void deleteCollinearVertices() {
		final Iterator<HE_Vertex> vItr = vItr();
		HE_Vertex v;
		HE_Halfedge he;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (v.getVertexOrder() == 2) {
				he = v.getHalfedge();
				if (WB_Vector.isParallel(he.getHalfedgeTangent(), he.getNextInVertex().getHalfedgeTangent())) {
					setNext(he.getPrevInFace(),he.getNextInFace());
					setNext(he.getPair().getPrevInFace(),he.getPair().getNextInFace());
					setVertex(he.getPair().getNextInFace(),he.getNextInFace().getVertex());
					if (he.getFace() != null) {
						if (he.getFace().getHalfedge() == he) {
							setHalfedge(he.getFace(),he.getNextInFace());
						}
					}
					if (he.getPair().getFace() != null) {
						if (he.getPair().getFace().getHalfedge() == he.getPair()) {
							setHalfedge(he.getPair().getFace(),he.getPair().getNextInFace());
						}
					}
					vItr.remove();
					remove(he);
					remove(he.getPair());
				}
			}
		}
	}

	/**
	 *
	 */
	public void deleteDegenerateTriangles() {
		final List<HE_Face> faces = this.getFacesAsList();
		HE_Halfedge he;
		for (final HE_Face face : faces) {
			if (!contains(face)) {
				continue; // face already removed by a previous change
			}
			if (face.isDegenerate()) {
				final int fo = face.getFaceOrder();
				if (fo == 3) {
					HE_Halfedge degeneratehe = null;
					he = face.getHalfedge();
					do {
						if (isZero(he.getLength())) {
							degeneratehe = he;
							break;
						}
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					if (degeneratehe != null) {
						// System.out.println("Zero length change!");
						collapseHalfedge(he);
						continue;
					}
					he = face.getHalfedge();
					double d;
					double dmax = 0;
					do {
						d = he.getLength();
						if (d > dmax) {
							degeneratehe = he;
							dmax = d;
						}
						he = he.getNextInFace();
					} while (he != face.getHalfedge());
					// System.out.println("Deleting longest edge: " + he);
					deleteEdge(degeneratehe);
				}
			}
		}
	}

	/**
	 * Reset labels.
	 */
	public void resetLabels() {
		resetVertexLabels();
		resetFaceLabels();
		resetEdgeLabels();
	}

	/**
	 * Reset vertex labels.
	 */
	public void resetVertexLabels() {
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().setLabel(-1);
		}
	}

	/**
	 * Reset face labels.
	 */
	public void resetFaceLabels() {
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			fItr.next().setLabel(-1);
		}
	}

	/**
	 * Reset edge labels.
	 */
	public void resetEdgeLabels() {
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			eItr.next().setLabel(-1);
		}
	}

	/**
	 * Reset labels.
	 */
	public void resetInternalLabels() {
		resetVertexInternalLabels();
		resetFaceInternalLabels();
		resetEdgeInternalLabels();
	}

	/**
	 * Reset vertex labels.
	 */
	public void resetVertexInternalLabels() {
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().setInternalLabel(-1);
		}
	}

	/**
	 * Reset face labels.
	 */
	public void resetFaceInternalLabels() {
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			fItr.next().setInternalLabel(-1);
		}
	}

	/**
	 * Reset edge labels.
	 */
	public void resetEdgeInternalLabels() {
		final Iterator<HE_Halfedge> eItr = eItr();
		while (eItr.hasNext()) {
			eItr.next().setInternalLabel(-1);
		}
	}

	/**
	 * Label all faces of a selection.
	 *
	 * @param sel
	 *            selection
	 * @param label
	 *            label to use
	 */
	public void labelFaceSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Face> fItr = sel.fItr();
		while (fItr.hasNext()) {
			fItr.next().setLabel(label);
		}
	}

	/**
	 * Label edge selection.
	 *
	 * @param sel
	 *            the sel
	 * @param label
	 *            the label
	 */
	public void labelEdgeSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Halfedge> eItr = sel.eItr();
		while (eItr.hasNext()) {
			eItr.next().setLabel(label);
		}
	}

	/**
	 * Label vertex selection.
	 *
	 * @param sel
	 *            the sel
	 * @param label
	 *            the label
	 */
	public void labelVertexSelection(final HE_Selection sel, final int label) {
		final Iterator<HE_Vertex> vItr = sel.vItr();
		while (vItr.hasNext()) {
			vItr.next().setLabel(label);
		}
	}

	/**
	 * Return a KD-tree containing all face centers.
	 *
	 * @return WB_KDTree
	 */
	public WB_KDTree<WB_Coord, Long> getFaceTree() {
		final WB_KDTree<WB_Coord, Long> tree = new WB_KDTree<WB_Coord, Long>();
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			tree.add(f.getFaceCenter(), f.key());
		}
		return tree;
	}

	/**
	 * Return a KD-tree containing all vertices.
	 *
	 * @return WB_KDTree
	 */
	public WB_KDTree<WB_Coord, Long> getVertexTree() {
		final WB_KDTree<WB_Coord, Long> tree = new WB_KDTree<WB_Coord, Long>();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tree.add(v, v.key());
		}
		return tree;
	}

	/**
	 * Return the closest vertex on the mesh.
	 *
	 * @param p
	 *            query point
	 * @param vertexTree
	 *            KD-tree from mesh (from vertexTree())
	 * @return HE_Vertex closest vertex
	 */
	public HE_Vertex getClosestVertex(final WB_Coord p, final WB_KDTree<WB_Coord, Long> vertexTree) {
		final WB_KDEntry<WB_Coord, Long>[] closestVertex = vertexTree.getNearestNeighbors(p, 1);
		if (closestVertex.length == 0) {
			return null;
		}
		return getVertexWithKey(closestVertex[0].value);
	}

	/**
	 * Return the closest point on the mesh.
	 *
	 * @param p
	 *            query point
	 * @param vertexTree
	 *            KD-tree from mesh (from vertexTree())
	 * @return WB_Coordinate closest point
	 */
	public WB_Coord getClosestPoint(final WB_Coord p, final WB_KDTree<WB_Coord, Long> vertexTree) {
		final WB_KDEntry<WB_Coord, Long>[] closestVertex = vertexTree.getNearestNeighbors(p, 1);
		final HE_Vertex v = getVertexWithKey(closestVertex[0].value);
		if (v == null) {
			return null;
		}
		final List<HE_Face> faces = v.getFaceStar();
		double d;
		double dmin = Double.POSITIVE_INFINITY;
		WB_Coord result = new WB_Point();
		for (int i = 0; i < faces.size(); i++) {
			final WB_Polygon poly = faces.get(i).toPolygon();
			final WB_Coord tmp = WB_GeometryOp.getClosestPoint3D(p, poly);
			d = WB_GeometryOp.getSqDistance3D(tmp, p);
			if (d < dmin) {
				dmin = d;
				result = tmp;
			}
		}
		return result;
	}

	/**
	 * Split the closest face in the query point.
	 *
	 * @param p
	 *            query point
	 * @param vertexTree
	 *            KD-tree from mesh (from vertexTree())
	 */
	public void addPointInClosestFace(final WB_Coord p, final WB_KDTree<WB_Coord, Long> vertexTree) {
		final WB_KDEntry<WB_Coord, Long>[] closestVertex = vertexTree.getNearestNeighbors(p, 1);
		final HE_Vertex v = getVertexWithKey(closestVertex[0].value);
		final List<HE_Face> faces = v.getFaceStar();
		double d;
		double dmin = Double.POSITIVE_INFINITY;
		HE_Face face = new HE_Face();
		for (int i = 0; i < faces.size(); i++) {
			final WB_Polygon poly = faces.get(i).toPolygon();
			final WB_Coord tmp = WB_GeometryOp.getClosestPoint3D(p, poly);
			d = WB_GeometryOp.getSqDistance3D(tmp, p);
			if (d < dmin) {
				dmin = d;
				face = faces.get(i);
				;
			}
		}
		final HE_Vertex nv = HEM_TriSplit.splitFaceTri(this, face, p).vItr().next();
		vertexTree.add(nv, nv.key());
	}

	/**
	 *
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public List<HE_Face> getSharedFaces(final HE_Vertex v1, final HE_Vertex v2) {
		final List<HE_Face> result = v1.getFaceStar();
		final List<HE_Face> compare = v2.getFaceStar();
		final Iterator<HE_Face> it = result.iterator();
		while (it.hasNext()) {
			if (!compare.contains(it.next())) {
				it.remove();
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<WB_Polygon> getBoundaryAsPolygons() {
		final List<WB_Polygon> polygons = new FastTable<WB_Polygon>();
		final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
		final List<HE_Halfedge> loop = new FastTable<HE_Halfedge>();
		final List<WB_Coord> points = new FastTable<WB_Coord>();
		while (halfedges.size() > 0) {
			points.clear();
			loop.clear();
			HE_Halfedge he = halfedges.get(0);
			do {
				loop.add(he);
				points.add(he.getVertex());
				he = he.getNextInFace();
				if (loop.contains(he)) {
					break;
				}
			} while (he != halfedges.get(0));
			polygons.add(gf.createSimplePolygon(points));
			halfedges.removeAll(loop);
		}
		return polygons;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Halfedge> getBoundaryLoopHalfedges() {
		final List<HE_Halfedge> hes = new FastTable<HE_Halfedge>();
		final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
		final List<HE_Halfedge> loop = new FastTable<HE_Halfedge>();
		while (halfedges.size() > 0) {
			loop.clear();
			HE_Halfedge he = halfedges.get(0);
			hes.add(he);
			do {
				loop.add(he);
				he = he.getNextInFace();
				if (loop.contains(he)) {
					break;
				}
			} while (he != halfedges.get(0));
			halfedges.removeAll(loop);
		}
		return hes;
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Path[] getBoundaryAsPath() {
		final List<HE_Halfedge> boundaryhes = getBoundaryLoopHalfedges();
		final HE_Path[] result = new HE_Path[boundaryhes.size()];
		for (int i = 0; i < boundaryhes.size(); i++) {
			result[i] = new HE_Path(boundaryhes.get(i));
		}
		return result;
	}

	/**
	 * @deprecated Use {@link #fixNonManifoldVerticesOnePass()} instead
	 */
	@Deprecated
	public void resolvePinchPoints() {
		fixNonManifoldVerticesOnePass();
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean fixNonManifoldVerticesOnePass() {
		class VertexInfo {
			FastTable<HE_Halfedge> out;

			VertexInfo() {
				out = new FastTable<HE_Halfedge>();
			}
		}
		final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(1024, 0.5f, -1L);
		HE_Vertex v;
		VertexInfo vi;
		WB_ProgressCounter counter = new WB_ProgressCounter(getNumberOfHalfedges(), 10);
		tracker.setStatus(this, "Classifying halfedges per vertex.", counter);
		HE_HalfedgeIterator heItr=heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he=heItr.next();
			v = he.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.out.add(he);
			counter.increment();
		}
		final List<HE_Vertex> toUnweld = new FastTable<HE_Vertex>();
		counter = new WB_ProgressCounter(getNumberOfVertices(), 10);
		tracker.setStatus(this, "Checking vertex umbrellas.", counter);
		Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			final List<HE_Halfedge> outgoing = vertexLists.get(v.key()).out;
			final List<HE_Halfedge> vStar = v.getHalfedgeStar();
			if (outgoing.size() != vStar.size()) {
				toUnweld.add(v);
			}
		}
		vItr = toUnweld.iterator();
		counter = new WB_ProgressCounter(toUnweld.size(), 10);
		tracker.setStatus(this, "Splitting vertex umbrellas. ", counter);

		while (vItr.hasNext()) {
			v = vItr.next();
			final List<HE_Halfedge> vHalfedges = vertexLists.get(v.key()).out;
			final List<HE_Halfedge> vStar = v.getHalfedgeStar();
			final HE_Vertex vc = new HE_Vertex(v);
			add(vc);
			for (int i = 0; i < vStar.size(); i++) {
				setVertex(vStar.get(i),vc);
			}
			setHalfedge(vc,vStar.get(0));
			for (int i = 0; i < vHalfedges.size(); i++) {
				he = vHalfedges.get(i);
				if (he.getVertex() == v) {
					setHalfedge(v,he);
					break;
				}
			}
			counter.increment();
		}
		return (toUnweld.size() > 0);
	}

	public void fixNonManifoldVertices() {
		int counter = 0;
		do {
			counter++;
		} while (fixNonManifoldVerticesOnePass() || (counter < 10));// Normally
		// this should
		// run at most
		// 3 or 4
		// times
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getArea() {
		final Iterator<HE_Face> fItr = fItr();
		double A = 0.0;
		while (fItr.hasNext()) {
			A += fItr.next().getFaceArea();
		}
		return A;
	}

	/**
	 * Triangulate face.
	 *
	 * @param key
	 *            key of face
	 * @return
	 */
	public HE_Selection triangulate(final long key) {
		return triangulate(getFaceWithKey(key));
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public HE_Selection triangulateFaceStar(final HE_Vertex v) {
		return HET_MeshOp.triangulateFaceStar(v, this);
	}

	/**
	 *
	 *
	 * @param vertexkey
	 * @return
	 */
	public HE_Selection triangulateFaceStar(final long vertexkey) {
		return HET_MeshOp.triangulateFaceStar(vertexkey, this);
	}

	/**
	 *
	 *
	 * @param face
	 * @return
	 */
	public HE_Selection triangulate(final HE_Face face) {
		return HET_MeshOp.triangulate(face, this);
	}

	/**
	 * Triangulate all faces.
	 *
	 * @return
	 */
	public HE_Selection triangulate() {
		return HET_MeshOp.triangulate(this);
	}

	/**
	 * Triangulate.
	 *
	 * @param sel
	 *            the sel
	 * @return
	 */
	public HE_Selection triangulate(final HE_Selection sel) {
		return HET_MeshOp.triangulate(sel, this);
	}

	/**
	 * Clean.
	 */
	public void clean() {
		modify(new HEM_Clean());
	}

	/**
	 * Smooth.
	 */
	public void smooth() {
		subdivide(new HES_CatmullClark());
	}

	/**
	 *
	 *
	 * @param rep
	 */
	public void smooth(final int rep) {
		subdivide(new HES_CatmullClark(), rep);
	}

	/**
	 * Fix loops.
	 */
	public void fixLoops() {
		for (final HE_Halfedge he : getHalfedgesAsList()) {
			if (he.getPrevInFace() == null) {
				HE_Halfedge hen = he.getNextInFace();
				while (hen.getNextInFace() != he) {
					hen = hen.getNextInFace();
				}
				setNext(hen,he);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Geometry#getType()
	 */
	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.MESH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public HE_Mesh apply(final WB_Transform T) {
		final HE_Mesh result = get();
		return result.transform(T);
	}

	public HE_Mesh applySelf(final WB_Transform T) {
		return transform(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getFaceNormal(int)
	 */
	@Override
	public WB_Coord getFaceNormal(final int id) {
		return getFaceWithIndex(id).getFaceNormal();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getFaceCenter(int)
	 */
	@Override
	public WB_Coord getFaceCenter(final int id) {
		return getFaceWithIndex(id).getFaceCenter();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getVertexNormal(int)
	 */
	@Override
	public WB_Coord getVertexNormal(final int i) {
		return getVertexWithIndex(i).getVertexNormal();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getVertex(int)
	 */
	@Override
	public WB_Coord getVertex(final int i) {
		return getVertexWithIndex(i);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getEdgesAsInt()
	 */
	@Override
	public int[][] getEdgesAsInt() {
		final int[][] result = new int[getNumberOfEdges()][2];
		final TLongIntMap vertexKeys = new TLongIntHashMap(10, 0.5f, -1L, -1);
		final Iterator<HE_Vertex> vItr = vItr();
		int i = 0;
		while (vItr.hasNext()) {
			vertexKeys.put(vItr.next().key(), i);
			i++;
		}
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge he;
		i = 0;
		while (eItr.hasNext()) {
			he = eItr.next();
			result[i][0] = vertexKeys.get(he.getVertex().key());
			he = he.getPair();
			result[i][1] = vertexKeys.get(he.getVertex().key());
			i++;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getPoints()
	 */
	@Override
	public List<WB_Coord> getPoints() {
		final List<WB_Coord> result = new FastTable<WB_Coord>();

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			result.add(v);

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#getColor()
	 */
	@Override
	public int getColor() {
		return meshcolor;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#setColor(int)
	 */
	@Override
	public void setColor(final int color) {
		meshcolor = color;
	}

	/**
	 *
	 *
	 * @param color
	 */
	public void setFaceColor(final int color) {
		final HE_FaceIterator fitr = fItr();
		while (fitr.hasNext()) {
			fitr.next().setColor(color);
		}
	}

	/**
	 *
	 *
	 * @param color
	 */
	public void setVertexColor(final int color) {
		final HE_VertexIterator vitr = vItr();
		while (vitr.hasNext()) {
			vitr.next().setColor(color);
		}
	}

	/**
	 *
	 *
	 * @param color
	 */
	public void setHalfedgeColor(final int color) {
		final HE_HalfedgeIterator heitr = heItr();
		while (heitr.hasNext()) {
			heitr.next().setColor(color);
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setFaceColorWithLabel(final int color, final int i) {
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setFaceColorWithInternalLabel(final int color, final int i) {
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getInternalLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setVertexColorWithLabel(final int color, final int i) {
		final HE_VertexIterator fitr = vItr();
		HE_Vertex f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setVertexColorWithInternalLabel(final int color, final int i) {
		final HE_VertexIterator fitr = vItr();
		HE_Vertex f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getInternalLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setHalfedgeColorWithLabel(final int color, final int i) {
		final HE_HalfedgeIterator fitr = heItr();
		HE_Halfedge f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setHalfedgeColorWithInternalLabel(final int color, final int i) {
		final HE_HalfedgeIterator fitr = heItr();
		HE_Halfedge f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getInternalLabel() == i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setFaceColorWithOtherLabel(final int color, final int i) {
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getLabel() != i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setFaceColorWithOtherInternalLabel(final int color, final int i) {
		final HE_FaceIterator fitr = fItr();
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getInternalLabel() != i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setVertexColorWithOtherLabel(final int color, final int i) {
		final HE_VertexIterator fitr = vItr();
		HE_Vertex f;
		while (fitr.hasNext()) {
			f = fitr.next();
			if (f.getLabel() != i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setVertexColorWithOtherInternalLabel(final int color, final int i) {
		final HE_VertexIterator vitr = vItr();
		HE_Vertex v;
		while (vitr.hasNext()) {
			v = vitr.next();
			if (v.getInternalLabel() != i) {
				v.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setHalfedgeColorWithOtherLabel(final int color, final int i) {
		final HE_HalfedgeIterator heitr = heItr();
		HE_Halfedge he;
		while (heitr.hasNext()) {
			he = heitr.next();
			if (he.getLabel() != i) {
				he.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @param color
	 * @param i
	 */
	public void setHalfedgeColorWithOtherInternalLabel(final int color, final int i) {
		final HE_HalfedgeIterator heitr = heItr();;
		HE_Halfedge f;
		while (heitr.hasNext()) {
			f = heitr.next();
			if (f.getInternalLabel() != i) {
				f.setColor(color);
			}
		}
	}

	/**
	 *
	 *
	 * @return
	 */
	public int getGenus() {
		return (2 - getEulerCharacteristic()-getNumberOfBoundaryComponents() ) / 2;
	}

	public int getEulerCharacteristic() {
		return (getNumberOfVertices() - getNumberOfEdges()) + getNumberOfFaces();
	}

	public void clearVisitedElements() {
		final HE_FaceIterator fitr = fItr();
		while (fitr.hasNext()) {
			fitr.next().clearVisited();
		}
		final HE_VertexIterator vitr = vItr();
		while (vitr.hasNext()) {
			vitr.next().clearVisited();
		}
		final HE_HalfedgeIterator heitr = heItr();
		while (heitr.hasNext()) {
			heitr.next().clearVisited();
		}
	}

	public void cleanUVW() {
		HE_VertexIterator vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().cleanUVW();

		}
	}

	public int getNumberOfBoundaryComponents(){
		return getBoundaryLoopHalfedges().size();


	}
}