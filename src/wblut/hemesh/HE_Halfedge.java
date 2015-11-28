/*
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * Half-edge element of half-edge data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Halfedge extends HE_Element implements WB_HasColor {
	/** Start vertex of halfedge. */
	private HE_Vertex _vertex;
	/** Halfedge pair. */
	private HE_Halfedge _pair;
	/** Next halfedge in face. */
	private HE_Halfedge _next;
	/** Previous halfedge in face. */
	private HE_Halfedge _prev;
	/** Associated face. */
	private HE_Face _face;
	/** The _data. */
	// private HashMap<String, Object> _data;
	private int hecolor;
	private HE_TextureCoordinate uvw;
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	/**
	 * Instantiates a new HE_Halfedge.
	 */
	public HE_Halfedge() {
		super();
		hecolor = -1;
		uvw = null;
	}

	/**
	 * Get key.
	 *
	 * @return key
	 */
	public long key() {
		return super.getKey();
	}

	/**
	 * Get previous halfedge in face.
	 *
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInFace() {
		return _prev;
	}

	/**
	 * Get next halfedge in face.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInFace() {
		return _next;
	}

	/**
	 * Get next halfedge in vertex.
	 *
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInVertex() {
		if (_pair == null) {
			return null;
		}
		return _pair.getNextInFace();
	}

	/**
	 * Get previous halfedge in vertex.
	 *
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInVertex() {
		return getPrevInFace().getPair();
	}

	/**
	 * Get paired halfedge.
	 *
	 * @return paired halfedge
	 */
	public HE_Halfedge getPair() {
		return _pair;
	}

	/**
	 * Set next halfedge in face.
	 *
	 * @param he
	 *            next halfedge
	 */
	public void setNext(final HE_Halfedge he) {
		_next = he;
	}

	/**
	 * Sets previous halfedge in face, only to be called by setNext.
	 *
	 * @param he
	 *            next halfedge
	 */
	public void setPrev(final HE_Halfedge he) {
		_prev = he;
	}

	/**
	 * Mutually pair halfedges.
	 *
	 * @param he
	 *            halfedge to pair
	 */
	public void setPair(final HE_Halfedge he) {
		_pair = he;
	}

	/**
	 * Get type of face vertex associated with halfedge.
	 *
	 * @return HE.FLAT, HE.CONVEX, HE.CONCAVE
	 */
	public WB_Classification getHalfedgeType() {
		if (_vertex == null) {
			return null;
		}
		WB_Vector v = _vertex.getPoint().subToVector3D(getPrevInFace()._vertex);
		v.normalizeSelf();
		final WB_Vector vn = getNextInFace()._vertex.getPoint().subToVector3D(_vertex);
		vn.normalizeSelf();
		v = v.cross(vn);
		final WB_Coord n;
		if (_face == null) {
			n = WB_Vector.mul(_pair._face.getFaceNormal(), -1);
		} else {
			n = _face.getFaceNormal();
		}
		final double dot = v.dot(n);
		if (v.isParallel(vn)) {
			return WB_Classification.FLAT;
		} else if (dot > -WB_Epsilon.EPSILON) {
			return WB_Classification.CONVEX;
		} else {
			return WB_Classification.CONCAVE;
		}
	}

	/**
	 * Get tangent WB_Vector of halfedge.
	 *
	 * @return tangent
	 */
	public WB_Coord getHalfedgeTangent() {
		if ((_pair != null) && (_vertex != null) && (_pair.getVertex() != null)) {
			final WB_Vector v = _pair.getVertex().getPoint().subToVector3D(_vertex);
			v.normalizeSelf();
			return v;
		}
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeTangent() {
		final WB_Coord v = getHalfedgeTangent();
		if (v == null) {
			return null;
		}
		return isEdge() ? v : WB_Vector.mul(v, -1);
	}

	/**
	 * Get center of halfedge.
	 *
	 * @return center
	 */
	public WB_Coord getHalfedgeCenter() {
		if ((_next != null) && (_vertex != null) && (_next.getVertex() != null)) {
			return gf.createMidpoint(_next.getVertex(), _vertex);
		}
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeCenter() {
		if ((_next != null) && (_vertex != null) && (_next.getVertex() != null)) {
			return gf.createMidpoint(_next.getVertex(), _vertex);
		}
		return null;
	}

	public WB_Coord getEdgeCenter(final double f) {
		if ((_next != null) && (_vertex != null) && (_next.getVertex() != null)) {
			return gf.createMidpoint(_next.getVertex(), _vertex).addMulSelf(f, getEdgeNormal());
		}
		return null;
	}

	/**
	 * Get edge of halfedge.
	 *
	 * @return edge
	 */
	public HE_Halfedge getEdge() {
		if (isEdge()) {
			return this;
		}
		return _pair;
	}

	/**
	 * Get face of halfedge.
	 *
	 * @return face
	 */
	public HE_Face getFace() {
		return _face;
	}

	/**
	 * Sets the face.
	 *
	 * @param face
	 *            the new face
	 */
	public void setFace(final HE_Face face) {
		_face = face;
	}

	/**
	 * Get vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getVertex() {
		return _vertex;
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Vertex getStartVertex() {
		return _vertex;
	}

	/**
	 * Sets the vertex.
	 *
	 * @param vertex
	 *            the new vertex
	 */
	public void setVertex(final HE_Vertex vertex) {
		_vertex = vertex;
	}

	/**
	 * Get end vertex of halfedge.
	 *
	 * @return vertex
	 */
	public HE_Vertex getEndVertex() {
		if (_pair != null) {
			return _pair._vertex;
		}
		return _next._vertex;
	}

	/**
	 * Clear next.
	 */
	public void clearNext() {
		_next = null;
	}

	/**
	 * Clear prev.
	 */
	public void clearPrev() {
		// _prev = null;
	}

	/**
	 * Clear pair.
	 */
	public void clearPair() {
		_pair = null;
	}

	/**
	 * Clear face.
	 */
	public void clearFace() {
		_face = null;
	}

	/**
	 * Clear vertex.
	 */
	public void clearVertex() {
		_vertex = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getEdgeNormal() {
		if (_pair == null) {
			return null;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if ((he1._face == null) && (he2._face == null)) {
			return null;
		}
		final WB_Coord n1 = (he1._face != null) ? he1._face.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Coord n2 = (he2._face != null) ? he2._face.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Vector n = new WB_Vector(n1.xd() + n2.xd(), n1.yd() + n2.yd(), n1.zd() + n2.zd());
		n.normalizeSelf();
		return n;
	}

	/**
	 * Get halfedge normal.
	 *
	 * @return in-face normal of face, points inwards
	 */
	public WB_Coord getHalfedgeNormal() {
		WB_Coord fn;
		if ((getFace() == null) && (getPair() == null)) {
			return null;
		}
		if (getFace() == null) {
			if (getPair().getFace() == null) {
				return null;
			}
			fn = getPair().getFace().getFaceNormal();
		} else {
			fn = getFace().getFaceNormal();
		}
		final HE_Vertex vn = getNextInFace().getVertex();
		final WB_Vector _normal = new WB_Vector(vn);
		_normal.subSelf(getVertex());
		_normal.set(WB_Vector.cross(fn, _normal));
		_normal.normalizeSelf();
		return _normal;
	}

	/**
	 * Get area of faces bounding halfedge.
	 *
	 * @return area
	 */
	public double getHalfedgeArea() {
		return 0.5 * getEdgeArea();
	}

	/**
	 * Get angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getHalfedgeDihedralAngle() {
		return getEdgeDihedralAngle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Halfedge key: " + key() + ", paired with halfedge " + getPair().key() + ". Vertex: "
				+ getVertex().key() + ". Is this an edge: " + isEdge() + ".";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#getColor()
	 */
	@Override
	public int getColor() {
		return hecolor;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#setColor(int)
	 */
	@Override
	public void setColor(final int color) {
		hecolor = color;
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getLength() {
		return WB_GeometryOp.getDistance3D(getVertex(), getEndVertex());
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isEdge() {
		if ((_face == null) || (_pair == null)) {
			return false;
		}
		if (_pair._face == null) {
			return true;
		}
		return (_key < _pair._key);
	}

	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #isInnerBoundary()} instead
	 */
	@Deprecated
	public boolean isBoundary() {
		return isInnerBoundary();
	}

	/**
	 *
	 *
	 * @return
	 */
	public boolean isInnerBoundary() {
		if ((_face == null) || (_pair == null)) {
			return false;
		}
		if (_pair._face == null) {
			return true;
		}
		return false;
	}

	public boolean isOuterBoundary() {
		if (_face == null) {
			return true;
		}
		return false;
	}

	/**
	 * Get area of faces bounding edge.
	 *
	 * @return area
	 */
	public double getEdgeArea() {
		if (_pair == null) {
			return Double.NaN;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if ((he1._face == null) && (he2._face == null)) {
			return Double.NaN;
		}
		double result = 0;
		int n = 0;
		if (he1._face != null) {
			result += he1._face.getFaceArea();
			n++;
		}
		if (he2._face != null) {
			result += he2._face.getFaceArea();
			n++;
		}
		return result / n;
	}

	/**
	 * Return angle between adjacent faces.
	 *
	 * @return angle
	 */
	public double getEdgeDihedralAngle() {
		if (_pair == null) {
			return Double.NaN;
		}
		HE_Halfedge he1, he2;
		if (isEdge()) {
			he1 = this;
			he2 = _pair;
		} else {
			he1 = _pair;
			he2 = this;
		}
		if ((he1._face == null) || (he2._face == null)) {
			return Double.NaN;
		} else {
			final WB_Coord n1 = he1._face.getFaceNormal();
			final WB_Coord n2 = he2._face.getFaceNormal();
			return Math.PI - Math.acos(WB_Math.clamp(WB_Vector.dot(n1, n2), -1, 1));
		}
	}

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Halfedge el) {
		super.copyProperties(el);
		hecolor = el.getColor();
		if (el.getUVW() == null) {
			uvw = null;
		} else {
			uvw = new HE_TextureCoordinate(el.getUVW());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Element#clear()
	 */
	@Override
	public void clear() {
		_face = null;
		_next = null;
		_pair = null;
		_vertex = null;
		uvw = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public double getAngle() {
		final WB_Coord c = getVertex();
		final WB_Coord p1 = getEndVertex();
		final WB_Coord p2 = this.getPrevInFace().getVertex();
		if (c == null) {
			return Double.NaN;
		}
		return WB_GeometryOp.angleBetween(c.xd(), c.yd(), c.zd(), p1.xd(), p1.yd(), p1.zd(), p2.xd(), p2.yd(), p2.zd());
	}

	// TEXTURE COORDINATES

	/**
	 * Get texture coordinate belonging to the halfedge vertex in this face. If
	 * no halfedge UVW exists, returns the vertex UVW. If neither exist, zero
	 * coordinates are returned.
	 * 
	 * @return
	 */
	public HE_TextureCoordinate getUVW() {
		if (uvw == null) {
			if (_vertex != null) {
				return _vertex.getVertexUVW();
			} else {
				return HE_TextureCoordinate.ZERO;
			}
		}
		return uvw;
	}

	/**
	 * Get texture coordinate belonging to this halfedge . If no halfedge UVW
	 * exists, zero coordinates are returned.
	 * 
	 * @return
	 */
	public HE_TextureCoordinate getHalfedgeUVW() {
		if (uvw == null) {
			return HE_TextureCoordinate.ZERO;
		}
		return uvw;
	}

	/**
	 * Get texture coordinate belonging to the halfedge vertex. If no vertex UVW
	 * exists, zero coordinates are returned.
	 * 
	 * @return
	 */
	public HE_TextureCoordinate getVertexUVW() {
		if (_vertex != null) {
			return _vertex.getVertexUVW();
		} else {
			return HE_TextureCoordinate.ZERO;
		}
	}

	/**
	 * Clear halfedge UVW.
	 */
	public void clearUVW() {
		uvw = null;
	}

	/**
	 * Set halfedge UVW
	 * 
	 * @param u
	 * @param v
	 * @param w
	 */
	public void setUVW(final double u, final double v, final double w) {
		uvw = new HE_TextureCoordinate(u, v, w);
	}

	/**
	 * Set halfedge UVW
	 * 
	 * @param uvw
	 *            WB_Coord
	 */
	public void setUVW(final WB_Coord uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Set halfedge UVW
	 * 
	 * @param uvw
	 *            HE_TextureCoordinate
	 */
	public void setUVW(final HE_TextureCoordinate uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Check if this halfedge has texture coordinates
	 * 
	 * @return
	 */
	public boolean hasHalfedgeUVW() {
		return uvw != null;
	}

	/**
	 * Check if the halfedge vertex has a UVW for this face, either a halfedge
	 * UVW or a vertex UVW.
	 * 
	 * 
	 * @return
	 */
	public boolean hasUVW() {
		if (uvw != null)
			return true;
		if (_vertex != null && _vertex.hasVertexUVW()) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the halfedge vertex has a vertex UVW.
	 * 
	 * @return
	 */
	public boolean hasVertexUVW() {
		if (_vertex != null && _vertex.hasVertexUVW()) {
			return true;
		}
		return false;
	}

}
