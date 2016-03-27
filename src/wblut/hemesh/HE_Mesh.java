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

import static wblut.math.WB_Epsilon.isZero;
import static wblut.math.WB_Epsilon.isZeroSq;

import java.util.Iterator;
import java.util.List;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_FacelistMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryType;
import wblut.geom.WB_HasColor;
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
	private int meshcolor;

	/**
	 * Instantiates a new HE_Mesh.
	 *
	 */
	public HE_Mesh() {
		super();
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

	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_FacelistMesh toFacelistMesh() {
		return WB_GeometryFactory.instance().createMesh(getVerticesAsPoint(), getFacesAsInt());
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
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public WB_Frame getFrame() {
		final WB_Frame frame = new WB_Frame(getVerticesAsPoint());
		final TLongIntMap map = new TLongIntHashMap(10, 0.5f, -1L, -1);
		map.putAll(getVertexKeyToIndexMap());
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
		return modify(new HEM_Transform(T));
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

		WB_Point center = getCenter();
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next().addSelf(x - center.xd(), y - center.yd(), z - center.zd());
		}
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
	 * Rotate entire mesh around an arbitrary axis defined by 2 points.
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
	 * @deprecated Use
	 *             {@link #rotateAboutAxis2PSelf(double,double,double,double,double,double,double)}
	 *             instead
	 */
	@Deprecated
	public HE_Mesh rotateAbout2PointAxis(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		return rotateAboutAxis2PSelf(angle, p1x, p1y, p1z, p2x, p2y, p2z);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by 2 points..
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
	public HE_Mesh rotateAboutAxis2PSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by 2 points..
	 *
	 * @param angle
	 *            angle
	 * @param p1
	 *            first point on axis
	 * @param p2
	 *            second point on axis
	 * @return self
	 * @deprecated Use {@link #rotateAboutAxis2PSelf(double,WB_Coord,WB_Coord)}
	 *             instead
	 */
	@Deprecated
	public HE_Mesh rotateAbout2PointAxis(final double angle, final WB_Coord p1, final WB_Coord p2) {
		return rotateAboutAxis2PSelf(angle, p1, p2);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by 2 points..
	 *
	 * @param angle
	 *            angle
	 * @param p1
	 *            first point on axis
	 * @param p2
	 *            second point on axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxis2PSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by a point and a
	 * direction.
	 *
	 * @param angle
	 *            angle
	 * @param p
	 *            rotation point
	 * @param a
	 *            axis
	 * @return self
	 * @deprecated Use {@link #rotateAboutAxisSelf(double,WB_Coord,WB_Coord)}
	 *             instead
	 */
	@Deprecated
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Coord p, final WB_Coord a) {
		return rotateAboutAxisSelf(angle, p, a);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by a point and a
	 * direction.
	 *
	 * @param angle
	 *            angle
	 * @param p
	 *            rotation point
	 * @param a
	 *            axis
	 * @return self
	 */
	public HE_Mesh rotateAboutAxisSelf(final double angle, final WB_Coord p, final WB_Coord a) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by a point and a
	 * direction.
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 * @return
	 * @deprecated Use
	 *             {@link #rotateAboutAxisSelf(double,double,double,double,double,double,double)}
	 *             instead
	 */
	@Deprecated
	public HE_Mesh rotateAboutAxis(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		return rotateAboutAxisSelf(angle, px, py, pz, ax, ay, az);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis defined by a point and a
	 * direction.
	 *
	 * @param angle
	 * @param px
	 * @param py
	 * @param pz
	 * @param ax
	 * @param ay
	 * @param az
	 * @return
	 */
	public HE_Mesh rotateAboutAxisSelf(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(px, py, pz), new WB_Vector(ax, ay, az));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis in origin.
	 *
	 * @param angle
	 *            angle
	 * @param a
	 *            axis
	 * @return self
	 */
	public HE_Mesh rotateAboutOriginSelf(final double angle, final WB_Coord a) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis in origin.
	 *
	 *
	 * @param angle
	 * @param ax
	 * @param ay
	 * @param az
	 * @return
	 */
	public HE_Mesh rotateAboutOriginSelf(final double angle, final double ax, final double ay, final double az) {

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(ax, ay, az));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		return this;
	}

	/**
	 * Rotate entire mesh around an arbitrary axis in center.
	 *
	 * @param angle
	 *            angle
	 * @param a
	 *            axis
	 * @return self
	 */
	public HE_Mesh rotateAboutCenterSelf(final double angle, final WB_Coord a) {

		return rotateAboutAxisSelf(angle, getCenter(), a);
	}

	/**
	 * Rotate entire mesh around an arbitrary axis in center.
	 *
	 * @param angle
	 * @param ax
	 * @param ay
	 * @param az
	 * @return
	 */
	public HE_Mesh rotateAboutCenterSelf(final double angle, final double ax, final double ay, final double az) {
		return rotateAboutAxisSelf(angle, getCenter(), new WB_Vector(ax, ay, az));
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

		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(c.xd() + scaleFactorx * (v.xd() - c.xd()), c.yd() + scaleFactory * (v.yd() - c.yd()),
					c.zd() + scaleFactorz * (v.zd() - c.zd()));
		}

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
		WB_Point center = getCenter();
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(center.xd() + scaleFactorx * (v.xd() - center.xd()),
					center.yd() + scaleFactory * (v.yd() - center.yd()),
					center.zd() + scaleFactorz * (v.zd() - center.zd()));
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
	public WB_Point getCenter() {
		final WB_Point c = new WB_Point(0, 0, 0);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			c.addSelf(vItr.next());
		}
		c.divSelf(getNumberOfVertices());
		return c;
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
			for (int j = 0; j < n - 1; j++) {
				he = halfedges.get(j);
				setNext(he, halfedges.get(j + 1));
			}
			he = halfedges.get(n - 1);
			setNext(he, halfedges.get(0));
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
			setNext(he, halfedges.get(n - 1));

			for (int j = 1; j < n; j++) {
				he = halfedges.get(j);
				setNext(he, halfedges.get(j - 1));

			}
		}
	}

	public void orderHalfedges(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {
			for (int j = 0; j < n - 1; j++) {
				he = halfedges.get(j);
				setNext(he, halfedges.get(j + 1));
			}
		}
	}

	/**
	 * Cycle halfedges.
	 *
	 * @param halfedges
	 *            halfedges to cycle
	 */
	public void orderHalfedgesReverse(final List<HE_Halfedge> halfedges) {
		HE_Halfedge he;
		final int n = halfedges.size();
		if (n > 0) {

			for (int j = 1; j < n; j++) {
				he = halfedges.get(j);
				setNext(he, halfedges.get(j - 1));

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
	 *
	 * @return
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
							if (he.getVertex() == he2.getNextInFace().getVertex()
									&& he2.getVertex() == he.getNextInFace().getVertex()) {
								setPair(he, he2);
								break;
							}
						}
					}
					for (int j = 0; j < vInfo.out.size(); j++) {
						he2 = vInfo.out.get(j);
						if (he2 != he && he2.getPair() == null) {
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

	/**
	 *
	 */
	public void pairHalfedges() {
		pairHalfedgesOnePass();
	}

	/**
	 *
	 *
	 * @param partition1
	 * @param partition2
	 * @return
	 */
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
							if (he2.getPair() == null && he2.getNextInFace().getVertex() == v1) {
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
							if (he.getVertex() == he2.getNextInFace().getVertex()
									&& he2.getVertex() == he.getNextInFace().getVertex()) {
								setPair(he, he2);
								break;
							}
						}
					}
					for (int j = 0; j < vInfo.out.size(); j++) {
						he2 = vInfo.out.get(j);
						if (he2 != he && he2.getPair() == null) {
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
			setVertex(he2, he1.getNextInFace().getVertex());
			setPair(he1, he2);
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
							setNext(he1, he2);
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

		List<HE_Halfedge> halfedges = getHalfedges();
		final HE_RAS<HE_Halfedge> keep = new HE_RASTrove<HE_Halfedge>();
		for (HE_Halfedge he : halfedges) {

			if (he.getFace() == null) {
				setHalfedge(he.getVertex(), he.getNextInVertex());
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
		HEM_CapHoles ch = new HEM_CapHoles();
		modify(ch);
		return ch.caps;
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
	 * @deprecated Use {@link #flipFaces()} instead
	 */
	@Deprecated
	public HE_Mesh flipAllFaces() {
		return flipFaces();
	}

	/**
	 * Reverse all faces. Flips normals.
	 *
	 * @return
	 */
	public HE_Mesh flipFaces() {
		return HET_MeshOp.flipFaces(this);
	}

	/**
	 *
	 *
	 * @param he
	 * @return
	 */
	public boolean flipEdge(final HE_Halfedge he) {
		return HET_MeshOp.flipEdge(this, he);
	}

	/**
	 * Collapse halfedge. Start vertex is removed. Degenerate faces are removed.
	 * This function can result in non-manifold meshes.
	 *
	 * @param he
	 *
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
				setHalfedge(f, hen);
			}
			if (fp != null) {
				setHalfedge(fp, hePairn);
			}
			setNext(hep, hen);
			setNext(hePairp, hePairn);
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i), vp);
			}
			setHalfedge(vp, hen);
			remove(he);
			remove(hePair);
			remove(v);

			if (f != null) {
				HET_Fixer.deleteTwoEdgeFace(this, f);
			}
			if (fp != null) {
				HET_Fixer.deleteTwoEdgeFace(this, fp);
			}

			return true;
		}
		return false;
	}

	/**
	 * Collapse halfedge if its vertex doesn't belong to the boundary
	 *
	 * @param he
	 *            he
	 * @return true, if successful
	 */
	public boolean collapseHalfedgeBoundaryPreserving(final HE_Halfedge he) {
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
				setVertex(tmp.get(i), vp);
			}
			setHalfedge(vp, hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f, hen);
			}
			if (fp != null) {
				setHalfedge(fp, hePairn);
			}
			setNext(hep, hen);
			setNext(hePairp, hePairn);
			remove(he);
			remove(hePair);
			remove(v);
			HET_Fixer.deleteTwoEdgeFace(this, f);
			HET_Fixer.deleteTwoEdgeFace(this, fp);
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
			final HE_Halfedge he = e.isEdge() ? e : e.getPair();
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			vp.addSelf(v).mulSelf(0.5);
			final List<HE_Halfedge> tmp = v.getHalfedgeStar();
			for (int i = 0; i < tmp.size(); i++) {
				setVertex(tmp.get(i), vp);
			}
			setHalfedge(vp, hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f, hen);
			}
			if (fp != null) {
				setHalfedge(fp, hePairn);
			}
			setNext(hep, hen);
			setNext(hePairp, hePairn);
			remove(he);
			remove(hePair);
			remove(v);
			if (f != null) {
				HET_Fixer.deleteTwoEdgeFace(this, f);
			}
			if (fp != null) {
				HET_Fixer.deleteTwoEdgeFace(this, fp);
			}
			return true;
		}
		return false;
	}

	/**
	 * Collapse edge to its midpoint or to point on boundary
	 *
	 * @param e
	 * @param strict
	 *            if true then an edge with two vertices on the boundary is
	 *            always preserved
	 *
	 * @return
	 */
	public boolean collapseEdgeBoundaryPreserving(final HE_Halfedge e, final boolean strict) {
		if (contains(e)) {
			final HE_Halfedge he = e.isEdge() ? e : e.getPair();
			final HE_Halfedge hePair = he.getPair();
			final HE_Face f = he.getFace();
			final HE_Face fp = hePair.getFace();
			final HE_Vertex v = he.getVertex();
			final HE_Vertex vp = hePair.getVertex();
			if (v.isBoundary()) {
				if (vp.isBoundary()) {
					// In some cases both vertices are on the boundary but the
					// edge itself is not a boundary edge.
					// Collapsing this edge would pinch the mesh creating an
					// invalid topology.
					if (!he.isInnerBoundary() || strict) {
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
				setVertex(tmp.get(i), vp);
			}
			setHalfedge(vp, hePair.getNextInVertex());
			final HE_Halfedge hen = he.getNextInFace();
			final HE_Halfedge hep = he.getPrevInFace();
			final HE_Halfedge hePairn = hePair.getNextInFace();
			final HE_Halfedge hePairp = hePair.getPrevInFace();
			if (f != null) {
				setHalfedge(f, hen);
			}
			if (fp != null) {
				setHalfedge(fp, hePairn);
			}
			setNext(hep, hen);
			setNext(hePairp, hePairn);
			remove(he);
			remove(hePair);
			remove(e);
			remove(v);
			if (f != null) {
				HET_Fixer.deleteTwoEdgeFace(this, f);
			}
			if (fp != null) {
				HET_Fixer.deleteTwoEdgeFace(this, fp);
			}
			return true;
		}
		return false;
	}

	/**
	 * Delete face and remove all references. The halfedges remain and form a
	 * valid boundary loop.
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
	 * Delete face and remove all references. The halfedges are removed, the
	 * boundary loop is unpaired.
	 *
	 * @param f
	 */

	public void cutFace(final HE_Face f) {
		HE_Halfedge he = f.getHalfedge();
		do {
			setHalfedge(he.getVertex(), he.getNextInVertex());

			he = he.getNextInFace();
		} while (he != f.getHalfedge());

		do {
			clearFace(he);
			clearPair(he);
			remove(he);
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
			setHalfedge(v, he1.getNextInVertex());
		}
		v = he2.getVertex();
		if (v.getHalfedge() == he2) {
			setHalfedge(v, he2.getNextInVertex());
		}
		setNext(he1p, he2n);
		setNext(he2p, he1n);
		if (he1.getFace() != null && he2.getFace() != null) {
			f = new HE_Face();
			f.copyProperties(e.getPair().getFace());
			add(f);
			setHalfedge(f, he1p);
			HE_Halfedge he = he1p;
			do {
				setFace(he, f);
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
	 * @param edge
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
	 * @return
	 */
	public HE_Selection triangulateConcaveFaces() {
		return HET_MeshOp.triangulateConcaveFaces(this);
	}

	/**
	 *
	 *
	 * @param sel
	 * @return
	 */
	public HE_Selection triangulateConcaveFaces(final List<HE_Face> sel) {
		return HET_MeshOp.triangulateConcaveFaces(sel, this);
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param key
	 *            key of face
	 * @return
	 */
	public HE_Selection triangulateConcaveFace(final long key) {
		return HET_MeshOp.triangulateConcaveFace(key, this);
	}

	/**
	 * Triangulate face if concave.
	 *
	 * @param face
	 *            key of face
	 * @return
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
		setHalfedge(vNew, he1);
		add(vNew);
		he = he1;
		do {
			setVertex(he, vNew);
			he = he.getNextInVertex();
		} while (he != he2);
		final HE_Halfedge he1p = he1.getPrevInFace();
		final HE_Halfedge he2p = he2.getPrevInFace();
		final HE_Halfedge he1new = new HE_Halfedge();
		final HE_Halfedge he2new = new HE_Halfedge();
		setVertex(he1new, v);
		setVertex(he2new, vNew);
		setNext(he1p, he1new);
		setNext(he1new, he1);
		setNext(he2p, he2new);
		setNext(he2new, he2);
		setPair(he1new, he2new);
		setFace(he1new, f1);
		setFace(he2new, f2);
		add(he1new);
		add(he2new);
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
		return isConvex ? true : c % 2 == 1;
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
		setHalfedge(newFace, halfedges.get(0));
		for (int i = 0; i < halfedges.size(); i++) {
			final HE_Halfedge hei = halfedges.get(i);
			final HE_Halfedge hep = halfedges.get(i).getPair();
			for (int j = 0; j < halfedges.size(); j++) {
				final HE_Halfedge hej = halfedges.get(j);
				if (i != j && hep.getVertex() == hej.getVertex()) {
					setNext(hei, hej);
				}
			}
			setFace(hei, newFace);
			setHalfedge(hei.getVertex(), hei);
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
		return new HEC_Transform(this, T).create();
	}

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	public HE_Mesh applySelf(final WB_Transform T) {
		return modify(new HEM_Transform(T));
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
	 * Get the vertices as a List<WB_Coord>
	 *
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Mesh#getPoints()
	 */
	@Override
	public List<WB_Coord> getPoints() {
		final List<WB_Coord> result = new FastTable<WB_Coord>();
		result.addAll(vertices.getObjects());
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
	 * @return
	 */
	public int getGenus() {
		return (2 - getEulerCharacteristic() - getNumberOfBoundaryComponents()) / 2;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int getEulerCharacteristic() {
		return getNumberOfVertices() - getNumberOfEdges() + getNumberOfFaces();
	}

	/**
	 *
	 */
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

	/**
	 *
	 *
	 * @return
	 */
	public int getNumberOfBoundaryComponents() {
		return getBoundaryLoopHalfedges().size();

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
			for (int i = 0; i < vertices.length - 1; i++) {
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
	 * Set vertex positions to values in a 2D array. If length of array is not
	 * the same as number of vertices, nothing happens.
	 *
	 * @param values
	 *            2D array of float. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromFloat(final float[][] values) {
		if (values.length != getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;
		}
	}

	/**
	 * Set vertex positions to values in a 1D array. If length of array is not
	 * 3* number of vertices, nothing happens.
	 *
	 * @param values
	 *            1D array of float. 3 values, x,y, and z, per point
	 */
	public void setVerticesFromFloat(final float[] values) {
		if (values.length != 3 * getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i], values[i + 1], values[i + 2]);
			i += 3;
		}
	}

	/**
	 * Set vertex positions to array of WB_Coord. If length of array is not the
	 * same as number of vertices, nothing happens.
	 *
	 * @param values
	 *            array of WB_Coord.
	 */
	public void setVerticesFromPoint(final WB_Coord[] values) {
		if (values.length != getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i]);
			i++;
		}
	}

	/**
	 * Set vertex positions to List of WB_Coord. If the size of the List is not
	 * the same as number of vertices, nothing happens.
	 *
	 * @param values
	 *            array of WB_Coord.
	 */
	public void setVerticesFromPoint(final List<? extends WB_Coord> values) {
		if (values.size() != getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values.get(i));
			i++;
		}
	}

	/**
	 * Set vertex positions to values in a 2D array. If length of array is not
	 * the same as number of vertices, nothing happens.
	 *
	 * @param values
	 *            2D array of double. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromDouble(final double[][] values) {
		if (values.length != getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;
		}
	}

	/**
	 * Set vertex positions to values in a 1D array. If length of array is not
	 * 3* number of vertices, nothing happens.
	 *
	 * @param values
	 *            1D array of float. 3 values, x,y, and z, per point
	 */
	public void setVerticesFromFloat(final double[] values) {
		if (values.length != 3 * getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i], values[i + 1], values[i + 2]);
			i += 3;
		}
	}

	/**
	 * Set vertex positions to values in a 2D array. If length of array is not
	 * the same as number of vertices, nothing happens.
	 *
	 * @param values
	 *            2D array of int. First index is number of vertices, second
	 *            index is 3 (x-,y- and z-coordinate)
	 */
	public void setVerticesFromInt(final int[][] values) {
		if (values.length != getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i][0], values[i][1], values[i][2]);
			i++;
		}
	}

	/**
	 * Set vertex positions to values in a 1D array. If length of array is not
	 * 3* number of vertices, nothing happens.
	 *
	 * @param values
	 *            1D array of float. 3 values, x,y, and z, per point
	 */
	public void setVerticesFromFloat(final int[] values) {
		if (values.length != 3 * getNumberOfVertices()) {
			return;
		}
		int i = 0;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(values[i], values[i + 1], values[i + 2]);
			i += 3;
		}
	}

}