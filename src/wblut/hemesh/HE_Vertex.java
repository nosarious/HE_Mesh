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
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Coord;
import wblut.geom.WB_CoordinateSystem;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_MutableCoord;
import wblut.geom.WB_MutableCoordinateFull;
import wblut.geom.WB_Point;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

/**
 * Vertex element of half-edge mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Vertex extends HE_MeshElement implements WB_HasColor,WB_MutableCoordinateFull  {
	double vx,vy,vz;

	/** Halfedge associated with this vertex. */
	private HE_Halfedge _halfedge;

	private int vertexcolor;
	private HE_TextureCoordinate uvw = null;

	/**
	 * Instantiates a new HE_Vertex.
	 */
	public HE_Vertex() {
		super();
		vx=vy=vz=0;
		vertexcolor = -1;
		uvw = null;

	}

	/**
	 * Instantiates a new HE_Vertex at position x, y, z.
	 *
	 * @param x
	 *            x-coordinate of vertex
	 * @param y
	 *            y-coordinate of vertex
	 * @param z
	 *            z-coordinate of vertex
	 */
	public HE_Vertex(final double x, final double y, final double z) {
		super();
		vx=x;vy=y;vz=z;
		vertexcolor = -1;
		uvw = null;
	}

	/**
	 * Instantiates a new HE_Vertex at position v.
	 *
	 * @param v
	 *            position of vertex
	 */
	public HE_Vertex(final WB_Coord v) {
		super();
		vx=v.xd();
		vy=v.yd();
		vz=v.zd();
		vertexcolor = -1;
		uvw = null;
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexEdgeCirculator veCrc() {
		return new HE_VertexEdgeCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexFaceCirculator vfCrc() {
		return new HE_VertexFaceCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexVertexCirculator vvCrc() {
		return new HE_VertexVertexCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeInCirculator vheiCrc() {
		return new HE_VertexHalfedgeInCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_VertexHalfedgeOutCirculator vheoCrc() {
		return new HE_VertexHalfedgeOutCirculator(this);
	}

	/**
	 *
	 *
	 * @return
	 */
	public HE_Vertex get() {
		final HE_Vertex copy = new HE_Vertex(vx,vy,vz);
		copy.copyProperties(this);
		return copy;
	}

	/**
	 * Get halfedge associated with this vertex.
	 *
	 * @return halfedge
	 */
	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge associated with this vertex.
	 *
	 * @param halfedge
	 *            the new halfedge
	 */
	protected void _setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	/**
	 * Set position to v.
	 *
	 * @param v
	 *            position
	 */
	public void set(final HE_Vertex v) {
		vx=v.xd();
		vy=v.yd();
		vz=v.zd();
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public WB_Point getOffset(final double d) {
		return new WB_Point(vx,vy,vz).addMulSelf(d, getVertexNormal());
	}

	public HE_Vertex getNextInFace() {
		return getHalfedge().getNextInFace().getVertex();
	}

	public HE_Vertex getPrevInFace() {
		return getHalfedge().getPrevInFace().getVertex();
	}

	/**
	 * Get vertex type. Returns stored value if update status is true.
	 *
	 * @return HE.VertexType.FLAT: vertex is flat in all faces,
	 *         HE.VertexType.CONVEX: vertex is convex in all faces,
	 *         HE.VertexType.CONCAVE: vertex is concave in all faces,
	 *         HE.VertexType.FLATCONVEX: vertex is convex or flat in all faces,
	 *         HE.VertexType.FLATCONCAVE: vertex is concave or flat in all
	 *         faces, HE.VertexType.SADDLE: vertex is convex and concave in at
	 *         least one face each
	 */
	public WB_Classification getVertexType() {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
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
		} while (he != _halfedge);
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Vertex key: " + key() + " [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
	}

	/**
	 * Clear halfedge.
	 */
	protected void _clearHalfedge() {
		_halfedge = null;
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
	 * Get halfedges in vertex.
	 *
	 * @return halfedges
	 */
	public List<HE_Halfedge> getHalfedgeStar() {
		final List<HE_Halfedge> vhe = new FastTable<HE_Halfedge>();
		if (getHalfedge() == null) {
			return vhe;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (!vhe.contains(he)) {
				vhe.add(he);
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vhe;
	}

	/**
	 * Get edges in vertex.
	 *
	 * @return edges
	 */
	public List<HE_Halfedge> getEdgeStar() {
		final List<HE_Halfedge> ve = new FastTable<HE_Halfedge>();
		if (getHalfedge() == null) {
			return ve;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.isEdge()) {
				if (!ve.contains(he)) {
					ve.add(he);
				}
			} else {
				if (!ve.contains(he.getPair())) {
					ve.add(he.getPair());
				}
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return ve;
	}

	/**
	 * Get faces in vertex.
	 *
	 * @return faces
	 */
	public List<HE_Face> getFaceStar() {
		final List<HE_Face> vf = new FastTable<HE_Face>();
		if (getHalfedge() == null) {
			return vf;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getFace() != null) {
				if (!vf.contains(he.getFace())) {
					vf.add(he.getFace());
				}
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vf;
	}

	/**
	 * Get neighboring vertices.
	 *
	 * @return neighbors
	 */
	public List<HE_Vertex> getNeighborVertices() {
		final List<HE_Vertex> vv = new FastTable<HE_Vertex>();
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hen = he.getNextInFace();
			if ((hen.getVertex() != this) && (!vv.contains(hen.getVertex()))) {
				vv.add(hen.getVertex());
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Vertex> getVertexStar() {
		return getNeighborVertices();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<HE_Vertex> getNextNeighborVertices() {
		final List<HE_Vertex> result = new FastTable<HE_Vertex>();
		if (getHalfedge() == null) {
			return result;
		}
		final List<HE_Vertex> vv = getNeighborVertices();
		for (final HE_Vertex v : vv) {
			result.addAll(v.getNeighborVertices());
		}
		final Iterator<HE_Vertex> vitr = result.iterator();
		HE_Vertex w;
		while (vitr.hasNext()) {
			w = vitr.next();
			if ((w == this) || (vv.contains(w))) {
				vitr.remove();
			}
		}
		return result;
	}

	/**
	 * Gets the neighbors as points.
	 *
	 * @return the neighbors as points
	 */
	public WB_Coord[] getNeighborsAsPoints() {
		final WB_Coord[] vv = new WB_Coord[getVertexOrder()];
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		int i = 0;
		do {
			vv[i] = he.getEndVertex();
			i++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 * Get number of edges in vertex.
	 *
	 * @return number of edges
	 */
	public int getVertexOrder() {
		int result = 0;
		if (getHalfedge() == null) {
			return 0;
		}
		HE_Halfedge he = getHalfedge();
		do {
			result++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return result;
	}

	/**
	 * Get area of faces bounding vertex.
	 *
	 * @return area
	 */
	public double getVertexArea() {
		if (getHalfedge() == null) {
			return 0;
		}
		double result = 0;
		int n = 0;
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getFace() != null) {
				result += he.getFace().getFaceArea();
				n++;
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return result / n;
	}



	/**
	 * Checks if is boundary.
	 *
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getFace() == null) {
				return true;
			}
			he = he.getNextInVertex();
		} while (he != _halfedge);
		return false;
	}



	/* (non-Javadoc)
	 * @see wblut.geom.WB_Coord#xd()
	 */
	@Override
	public double xd() {
		return vx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#yd()
	 */
	@Override
	public double yd() {
		return vy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zd()
	 */
	@Override
	public double zd() {
		return vz;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zd()
	 */
	@Override
	public double wd() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#getd(int)
	 */
	@Override
	public double getd(final int i) {
		if (i == 0) {
			return vx;
		}
		if (i == 1) {
			return vy;
		}
		if (i == 2) {
			return vz;
		}
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#xf()
	 */
	@Override
	public float xf() {
		return (float)vx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#yf()
	 */
	@Override
	public float yf() {
		return (float)vy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zf()
	 */
	@Override
	public float zf() {
		return (float)vz;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#zf()
	 */
	@Override
	public float wf() {
		return 1.0f;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Coordinate#getf(int)
	 */
	@Override
	public float getf(final int i) {
		if (i == 0) {
			return (float) vx;
		}
		if (i == 1) {
			return (float) vy;
		}
		if (i == 2) {
			return (float) vz;
		}
		return Float.NaN;
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public int compareTo(final WB_Coord p) {
		final int compX = compare(xd(), p.xd());
		if (compX != 0) {
			return compX;
		}
		final int compY = compare(yd(), p.yd());
		if (compY != 0) {
			return compY;
		}
		return compare(zd(), p.zd());
	}

	/**
	 *
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int compare(final double a, final double b) {
		if (a < b) {
			return -1;
		}
		if (a > b) {
			return 1;
		}
		if (Double.isNaN(a)) {
			if (Double.isNaN(b)) {
				return 0;
			}
			return -1;
		}
		if (Double.isNaN(b)) {
			return 1;
		}
		return 0;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setX(double)
	 */
	@Override
	public void setX(final double x) {
		vx=x;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setY(double)
	 */
	@Override
	public void setY(final double y) {
		vy=y;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setZ(double)
	 */
	@Override
	public void setZ(final double z) {
		vz=z;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setW(double)
	 */
	@Override
	public void setW(final double w) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#setCoord(int, double)
	 */
	@Override
	public void setCoord(final int i, final double v) {
		if (i == 0) {
			this.vx = v;
		}
		if (i == 1) {
			this.vy = v;
		}
		if (i == 2) {
			this.vz = v;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(wblut.geom.WB_Coordinate)
	 */
	@Override
	public void set(final WB_Coord p) {
		set(p.xd(),p.yd(),p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double)
	 */
	@Override
	public void set(final double x, final double y) {
		vx=x;
		vy=y;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double, double)
	 */
	@Override
	public void set(final double x, final double y, final double z) {
		vx=x;
		vy=y;
		vz=z;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinate#set(double, double, double, double)
	 */
	@Override
	public void set(final double x, final double y, final double z, final double w) {
		vx=x;
		vy=y;
		vz=z;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#getColor()
	 */
	@Override
	public int getColor() {
		return vertexcolor;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_HasColor#setColor(int)
	 */
	@Override
	public void setColor(final int color) {
		vertexcolor = color;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem getCS() {
		WB_Coord vn = getVertexNormal();

		final WB_Vector normal = (vn == null) ? null : new WB_Vector(getVertexNormal());
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
		return geometryfactory.createCSFromOXYZ(this, t1, t2, normal);
	}

	// Common area-weighted mean normal
	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexNormal() {
		if (_halfedge == null) {
			return null;
		}


		return getVertexAngleNormal();
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexAverageNormal() {
		WB_Vector normal = new WB_Vector();
		final WB_Vector[] temp = new WB_Vector[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = new WB_Vector();
		}
		final HE_Vertex d = _halfedge.getEndVertex();
		do {
			_halfedge = _halfedge.getNextInVertex();
			if (_halfedge.getFace() == null) {
				continue;
			}
			final double area = computeNormal3D(this, _halfedge.getEndVertex(),
					_halfedge.getPrevInFace().getVertex(), temp[0], temp[1], temp[2]);
			normal.addMulSelf(area, temp[2]);
		} while (_halfedge.getEndVertex() != d);
		final double n = normal.getLength3D();
		normal.mulSelf(1.0 / n);
		return normal;
	}


	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexAreaNormal() {
		WB_Vector normal = new WB_Vector();
		final WB_Vector[] temp = new WB_Vector[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = new WB_Vector();
		}
		final HE_Vertex d = _halfedge.getEndVertex();
		do {
			_halfedge = _halfedge.getNextInVertex();
			if (_halfedge.getFace() == null) {
				continue;
			}
			final double area = computeNormal3D(this, _halfedge.getEndVertex(),
					_halfedge.getPrevInFace().getVertex(), temp[0], temp[1], temp[2]);
			normal.addMulSelf(area, temp[2]);
		} while (_halfedge.getEndVertex() != d);
		final double n = normal.getLength3D();
		normal.mulSelf(1.0 / n);
		return normal;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getVertexAngleNormal() {
		HE_Halfedge he=getHalfedge();
		WB_Vector v=new WB_Vector();
		do{
			if(he.getFace()!=null){
				v.addMulSelf(he.getAngle(),he.getFace().getFaceNormal());
			}
			he=he.getNextInVertex();
		}while(he!=getHalfedge());
		v.normalizeSelf();
		return v;

	}


	/**
	 * Returns the discrete Gaussian curvature and the mean normal. These
	 * discrete operators are described in "Discrete Differential-Geometry
	 * Operators for Triangulated 2-Manifolds", Mark Meyer, Mathieu Desbrun,
	 * Peter Schr�der, and Alan H. Barr.
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 * @param meanCurvatureVector
	 * @return
	 */
	public double getGaussianCurvature(final WB_Vector meanCurvatureVector) {
		meanCurvatureVector.set(0, 0, 0);
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		double mixed = 0.0;
		double gauss = 0.0;
		HE_Halfedge ot = getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			/*
			if (ot.getPair().getFace() == null) {
				meanCurvatureVector.set(0, 0, 0);
				return 0.0;
			}
			 */
			final HE_Vertex p1 = ot.getEndVertex();
			final HE_Vertex p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(this,p1);
			vect2 = new WB_Vector(p1,p2);
			vect3 =new WB_Vector(p2,this);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			final double c31 = vect3.dot(vect1);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			if (c31 > 0.0) {
				mixed += 0.5 * area;
			} else if ((c12 > 0.0) || (c23 > 0.0)) {
				mixed += 0.25 * area;
			} else {
				if ((area > 0.0) && (area > (-WB_Epsilon.EPSILON * (c12 + c23)))) {
					mixed -= (0.125 * 0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1.dot(vect1)))) / area;
				}
			}
			gauss += Math.abs(Math.atan2(2.0 * area, -c31));
			meanCurvatureVector.addMulSelf(0.5 / area, vect3.mulAddMul(c12, -c23, vect1));
		} while (ot.getEndVertex() != d);
		meanCurvatureVector.mulSelf(0.5 / mixed);
		return ((2.0 * Math.PI) - gauss) / mixed;
	}

	/**
	 * Returns the discrete Gaussian curvature. These discrete operators are
	 * described in "Discrete Differential-Geometry Operators for Triangulated
	 * 2-Manifolds", Mark Meyer, Mathieu Desbrun, Peter Schr�der, and Alan H.
	 * Barr. http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 *
	 *
	 * @return
	 */
	public double getGaussianCurvature() {
		final WB_Vector meanCurvatureVector = new WB_Vector(0, 0, 0);
		if(isBoundary()){
			return 0.0;

		}
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		double mixed = 0.0;
		double gauss = 0.0;
		HE_Halfedge ot = getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			/*
			if (ot.getPair().getFace() == null) {
				meanCurvatureVector.set(0, 0, 0);
				return 0.0;
			}
			 */
			final HE_Vertex p1 = ot.getEndVertex();
			final HE_Vertex p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(this,p1);
			vect2 = new WB_Vector(p1,p2);
			vect3 =new WB_Vector(p2,this);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			final double c31 = vect3.dot(vect1);

			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			//This angle is obtuse
			if (c31 > 0.0) {
				mixed += 0.5 * area;
				//One of the other angles is obtuse
			} else if ((c12 > 0.0) || (c23 > 0.0)) {
				mixed += 0.25 * area;
			} else {

				if ((area > 0.0) && (area > (-WB_Epsilon.EPSILON * (c12 + c23)))) {
					mixed -= (0.125 * 0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1.dot(vect1)))) / area;
				}
			}
			gauss += Math.abs(Math.atan2(2.0 * area, -c31));
			meanCurvatureVector.addMulSelf(0.5 / area, vect3.mulAddMul(c12, -c23, vect1));
		} while (ot.getEndVertex() != d);
		meanCurvatureVector.mulSelf(0.5 / mixed);
		// Discrete gaussian curvature
		return ((2.0 * Math.PI) - gauss) / mixed;
	}

	/**
	 *
	 *
	 * @return
	 */
	public WB_CoordinateSystem getCurvatureDirections() {
		final WB_CoordinateSystem tangent = getCS();
		if (tangent == null) {
			return null;
		}
		final WB_Vector vect1 = findOptimalSolution(tangent.getZ(), tangent.getX(), tangent.getY());
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
					.sqrt(((vect1.xd() - vect1.zd()) * (vect1.xd() - vect1.zd())) + (4.0 * vect1.yd() * vect1.yd()));
			double K1;
			if ((vect1.xd() + vect1.zd()) < 0.0) {
				K1 = 0.5 * ((vect1.xd() + vect1.zd()) - delta);
			} else {
				K1 = 0.5 * (vect1.xd() + vect1.zd() + delta);
			}
			e1 = (K1 - vect1.xd()) / vect1.yd();
			final double n = Math.sqrt((e1 * e1) + (e2 * e2));
			e1 /= n;
			e2 /= n;
		}
		final WB_Vector t1 = tangent.getX();
		final WB_Vector t2 = tangent.getY();
		final WB_Vector X = t1.mulAddMul(e1, e2, t2);
		final WB_Vector Y = t1.mulAddMul(-e2, e1, t2);
		return geometryfactory.createCSFromOXYZ(this, X, Y, tangent.getZ());
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
		tempD1 = WB_Point.subToVector3D(p1,p2);
		tempD2 =  WB_Point.subToVector3D(p2,p0);
		tempD1.crossInto(ret, tempD2);
		double norm = ret.getLength3D();
		if ((norm * norm) > (WB_Epsilon.SQEPSILON
				* ((tempD1.xd() * tempD1.xd()) + (tempD1.yd() * tempD1.yd()) + (tempD1.zd() * tempD1.zd())
						+ (tempD2.xd() * tempD2.xd()) + (tempD2.yd() * tempD2.yd()) + (tempD2.zd() * tempD2.zd())))) {
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
	private WB_Vector findOptimalSolution(final WB_Vector normal, final WB_Vector t1, final WB_Vector t2) {
		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		final WB_Vector g0 = new WB_Vector();
		final WB_Vector g1 = new WB_Vector();
		final WB_Vector g2 = new WB_Vector();
		final WB_Vector h = new WB_Vector();
		HE_Halfedge ot = getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			final WB_Coord p1 = ot.getEndVertex();
			final WB_Coord p2 = ot.getPrevInFace().getVertex();
			vect1 = new WB_Vector(this, p1);
			vect2 = new WB_Vector(p1, p2);
			vect3 = new WB_Vector(p2, this);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength3D();
			final double len2 = vect1.dot(vect1);
			if (len2 < WB_Epsilon.SQEPSILON) {
				continue;
			}
			final double kappa = (2.0 * vect1.dot(normal)) / len2;
			double d1 = vect1.dot(t1);
			double d2 = vect1.dot(t2);
			final double n = Math.sqrt((d1 * d1) + (d2 * d2));
			if (n < WB_Epsilon.EPSILON) {
				continue;
			}
			d1 /= n;
			d2 /= n;
			final double omega = (0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1.dot(vect1)))) / area;
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

	/**
	 *
	 *
	 * @param el
	 */
	public void copyProperties(final HE_Vertex el) {
		super.copyProperties(el);
		vertexcolor = el.getColor();
		if (el.getVertexUVW() == null) {
			uvw = null;
		} else {
			uvw = new HE_TextureCoordinate(el.getVertexUVW());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Element#clear()
	 */
	@Override
	public void clear() {
		_halfedge = null;

	}

	/**
	 *
	 *
	 * @return
	 */
	public double getUmbrellaAngle() {
		double result = 0;
		HE_Halfedge he = _halfedge;
		if (he == null) {
			return 0;
		}
		do {
			result += he.getAngle();
			he = he.getNextInVertex();
		} while (he != _halfedge);
		return result;
	}

	/**
	 *
	 *
	 * @param f
	 * @return
	 */
	public HE_Halfedge getHalfedge(final HE_Face f) {
		HE_Halfedge he = _halfedge;
		if (he == null) {
			return null;
		}
		if (f == null) {
			do {
				if (he.getFace() == null) {
					return he;
				}
				he = he.getNextInVertex();
			} while (he != _halfedge);
		} else {
			do {
				if (he.getFace() == f) {
					return he;
				}
				he = he.getNextInVertex();
			} while (he != _halfedge);
		}
		return null;
	}

	// TEXTURE COORDINATES

	/**
	 * Clear vertex UVW.
	 */

	public void clearUVW() {
		uvw = null;
	}

	/**
	 * Set vertex UVW.
	 *
	 * @param u
	 * @param v
	 * @param w
	 */
	public void setUVW(final double u, final double v, final double w) {
		uvw = new HE_TextureCoordinate(u, v, w);
	}

	/**
	 * Set vertex UVW.
	 *
	 * @param uvw            WB_Coord
	 */
	public void setUVW(final WB_Coord uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Set vertex UVW.
	 *
	 * @param uvw            HE_TextureCoordinate
	 */
	public void setUVW(final HE_TextureCoordinate uvw) {
		if (uvw == null) {
			return;
		}
		this.uvw = new HE_TextureCoordinate(uvw);
	}

	/**
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param u
	 * @param v
	 * @param w
	 * @param face
	 */
	public void setUVW(final double u, final double v, final double w, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(u, v, w);
		}

	}

	/**
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param uvw            WB_Coord
	 * @param face
	 */
	public void setUVW(final WB_Coord uvw, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(uvw);
		}

	}

	/**
	 * Set UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param uvw            HE_TextureCoordinate
	 * @param face
	 */
	public void setUVW(final HE_TextureCoordinate uvw, final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.setUVW(uvw);
		}

	}

	/**
	 * Clear UVW in halfedge in this vertex, belonging to face. If no such
	 * halfedge exists, nothing happens.
	 *
	 * @param face
	 */

	public void clearUVW(final HE_Face face) {
		HE_Halfedge he = getHalfedge(face);
		if (he != null) {
			he.clearUVW();
		}

	}

	/**
	 * Check if this vertex has a UVW for this face, either a halfedge UVW or a
	 * vertex UVW.
	 *
	 * @param f
	 * @return
	 */
	public boolean hasUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if ((he != null) && he.hasHalfedgeUVW()) {
			return true;
		} else {
			return uvw != null;
		}
	}

	/**
	 * Check if this vertex has a vertex UVW.
	 *
	 * @return
	 */
	public boolean hasVertexUVW() {
		return uvw != null;
	}

	/**
	 * Check if this vertex has a halfedge UVW for this face.
	 *
	 * @param f
	 * @return
	 */
	public boolean hasHalfedgeUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if ((he != null) && he.hasHalfedgeUVW()) {
			return true;
		}
		return false;
	}

	/**
	 * Get the vertex UVW. If none exists, return zero coordinates.
	 *
	 * @return
	 */
	public HE_TextureCoordinate getVertexUVW() {
		if (uvw == null) {
			return HE_TextureCoordinate.ZERO;
		}
		return uvw;
	}

	/**
	 * Get the halfedge UVW belonging to a face. If none exists, return zero
	 * coordinates.
	 *
	 * @param f
	 * @return
	 */
	public HE_TextureCoordinate getHalfedgeUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if ((he != null) && he.hasHalfedgeUVW()) {
			return he.getUVW();
		} else {
			return HE_TextureCoordinate.ZERO;
		}
	}

	/**
	 * Get the UVW belonging to a face. If approprate halfedge UVW exists, the
	 * vertex UVW is retrieved.* If neither exist, zero coordinates are
	 * returned.
	 *
	 * @param f
	 * @return
	 */

	public HE_TextureCoordinate getUVW(final HE_Face f) {
		final HE_Halfedge he = getHalfedge(f);
		if (he != null) {
			return he.getUVW();
		}
		return uvw == null ? HE_TextureCoordinate.ZERO : uvw;
	}

	/**
	 *
	 */
	public void cleanUVW() {
		if (_halfedge == null) {
			return;
		}
		List<HE_Halfedge> halfedges = getHalfedgeStar();

		if (halfedges.size() == 0) {
			return;
		}
		int i = 0;
		while (!hasVertexUVW() && (i < halfedges.size())) {
			if (halfedges.get(i).hasHalfedgeUVW()) {
				setUVW(halfedges.get(i).getHalfedgeUVW());
			}
			i++;
		}
		if (hasVertexUVW()) {
			for (HE_Halfedge he : halfedges) {
				if (he.hasHalfedgeUVW()) {
					if (he.getHalfedgeUVW().equals(getVertexUVW())) {
						he.clearUVW();
					}

				}
			}

		}

	}


	/**
	 *
	 *
	 * @return
	 */
	public double getAngularDefect(){
		return (2*Math.PI)-getUmbrellaAngle();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#absDot(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double absDot(final WB_Coord p) {
		return WB_Math.fastAbs(WB_GeometryOp.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#absDot2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double absDot2D(final WB_Coord p) {
		return WB_Math.fastAbs(WB_GeometryOp.dot2D(xd(), yd(), p.xd(), p.yd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#add(double, double, double)
	 */
	@Override
	public WB_Point add(final double... x) {
		return new WB_Point(this.xd() + x[0], this.yd() + x[1], this.zd() + x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#add(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point add(final WB_Coord p) {
		return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addInto(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addInto(final WB_MutableCoord result, final double... x) {
		result.set(this.xd() + x[0], this.yd() + x[1], this.zd() + x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addInto(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addInto(final WB_MutableCoord result, final WB_Coord p) {
		result.set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMul(double, double, double, double)
	 */
	@Override
	public WB_Point addMul(final double f, final double... x) {
		return new WB_Point(this.xd() + (f * x[0]), this.yd() + (f * x[1]), this.zd() + (f * x[2]));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMul(double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point addMul(final double f, final WB_Coord p) {
		return new WB_Point(xd() + (f * p.xd()), yd() + (f * p.yd()), zd() + (f * p.zd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMulInto(double, double, double,
	 * double, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addMulInto(final WB_MutableCoord result, final double f, final double... x) {
		result.set(this.xd() + (f * x[0]), this.yd() + (f * x[1]), this.zd() + (f * x[2]));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMulInto(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addMulInto(final WB_MutableCoord result, final double f, final WB_Coord p) {
		result.set(xd() + (f * p.xd()), yd() + (f * p.yd()), zd() + (f * p.zd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double, double,
	 * double, double)
	 */
	@Override
	public HE_Vertex addMulSelf(final double f, final double... x) {
		set(xd() + (f * x[0]), yd() + (f * x[1]), zd() + (f * x[2]));
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex addMulSelf(final double f, final WB_Coord p) {
		set(xd() + (f * p.xd()), yd() + (f * p.yd()), zd() + (f * p.zd()));
		return this;
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public HE_Vertex addSelf(final double x, final double y, final double z) {
		set(xd() + x, yd() + y, zd() + z);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addSelf(double, double, double)
	 */
	@Override
	public HE_Vertex addSelf(final double... x) {
		set(xd() + x[0], yd() + x[1], zd() + x[2]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#addSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex addSelf(final WB_Coord p) {
		set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point apply(final WB_Transform T) {
		final WB_Point v = new WB_Point(this);
		return v.applySelf(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateTransform#applyAsNormal(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Vector applyAsNormal(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsNormal(this, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#applyAsNormalInto(wblut.geom.
	 * WB_Transform , wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void applyAsNormalInto(final WB_MutableCoord result, final WB_Transform T) {
		T.applyAsNormal(this, result);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsNormalSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public HE_Vertex applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateTransform#applyAsPoint(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point applyAsPoint(final WB_Transform T) {
		final WB_Point result = new WB_Point();
		T.applyAsPoint(this, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#applyAsPointInto(wblut.geom.
	 * WB_Transform , wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void applyAsPointInto(final WB_MutableCoord result, final WB_Transform T) {
		T.applyAsPoint(this, result);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsPointSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public HE_Vertex applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateTransform#applyAsVector(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Vector applyAsVector(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsVector(this, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#applyAsVectorInto(wblut.geom.
	 * WB_Transform , wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void applyAsVectorInto(final WB_MutableCoord result, final WB_Transform T) {
		T.applyAsVector(this, result);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsVectorSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public HE_Vertex applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#applyInto(wblut.geom.WB_Transform,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void applyInto(final WB_MutableCoord result, final WB_Transform T) {
		T.applyAsVector(this, result);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#applySelf(wblut.geom.WB_Transform)
	 */
	@Override
	public HE_Vertex applySelf(final WB_Transform T) {
		return applyAsPointSelf(T);
	}

	/**
	 *
	 *
	 * @return
	 */
	public double[] coords() {
		return new double[] { xd(), yd(), zd() };
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#cross(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point cross(final WB_Coord p) {
		return new WB_Point((yd() * p.zd()) - (zd() * p.yd()), (zd() * p.xd()) - (xd() * p.zd()),
				(xd() * p.yd()) - (yd() * p.xd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#crossInto(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void crossInto(final WB_MutableCoord result, final WB_Coord p) {
		result.set((yd() * p.zd()) - (zd() * p.yd()), (zd() * p.xd()) - (xd() * p.zd()),
				(xd() * p.yd()) - (yd() * p.xd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#crossSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex crossSelf(final WB_Coord p) {
		set((yd() * p.zd()) - (this.zd() * p.yd()), (this.zd() * p.xd()) - (this.xd() * p.zd()),
				(this.xd() * p.yd()) - (yd() * p.xd()));
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#div(double)
	 */
	@Override
	public WB_Point div(final double f) {
		return mul(1.0 / f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#divInto(double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void divInto(final WB_MutableCoord result, final double f) {
		mulInto(result, 1.0 / f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#divSelf(double)
	 */
	@Override
	public HE_Vertex divSelf(final double f) {
		return mulSelf(1.0 / f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#dot(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double dot(final WB_Coord p) {
		return WB_GeometryOp.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#dot2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double dot2D(final WB_Coord p) {
		return WB_GeometryOp.dot2D(xd(), yd(), p.xd(), p.yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Coord)) {
			return false;
		}
		final WB_Coord p = (WB_Coord) o;
		if (!WB_Epsilon.isEqualAbs(xd(), p.xd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(yd(), p.yd())) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(zd(), p.zd())) {
			return false;
		}
		return true;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getAngle(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getAngle(final WB_Coord p) {
		return WB_GeometryOp.angleBetween(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public double getAngle(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.angleBetween(q.xd(), q.yd(), q.zd(), p.xd(), p.yd(), p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getAngleNorm(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getAngleNorm(final WB_Coord p) {
		return WB_GeometryOp.angleBetweenNorm(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public double getAngleNorm(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.angleBetweenNorm(q.xd(), q.yd(), q.zd(), p.xd(), p.yd(), p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getDistance2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getDistance2D(final WB_Coord p) {
		return WB_GeometryOp.getDistance2D(xd(), yd(), p.xd(), p.yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getDistance3D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getDistance3D(final WB_Coord p) {
		return WB_GeometryOp.getDistance3D(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#heading2D()
	 */
	@Override
	public double getHeading2D() {
		return Math.atan2(yd(), xd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getLength2D()
	 */
	@Override
	public double getLength2D() {
		return WB_GeometryOp.getLength2D(xd(), yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getLength3D()
	 */
	@Override
	public double getLength3D() {
		return WB_GeometryOp.getLength3D(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getOrthoNormal2D()
	 */
	@Override
	public WB_Vector getOrthoNormal2D() {
		final WB_Vector a = new WB_Vector(-yd(), xd(), 0);
		a.normalizeSelf();
		return a;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getOrthoNormal3D()
	 */
	@Override
	public WB_Vector getOrthoNormal3D() {
		if (Math.abs(zd()) > WB_Epsilon.EPSILON) {
			final WB_Vector a = new WB_Vector(1, 0, -xd() / zd());
			a.normalizeSelf();
			return a;
		} else {
			return new WB_Vector(0, 0, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getSqDistance2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getSqDistance2D(final WB_Coord p) {
		return WB_GeometryOp.getSqDistance2D(xd(), yd(), p.xd(), p.yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMetric#getSqDistance3D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double getSqDistance3D(final WB_Coord p) {
		return WB_GeometryOp.getSqDistance3D(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getSqLength2D()
	 */
	@Override
	public double getSqLength2D() {
		return WB_GeometryOp.getSqLength2D(xd(), yd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getSqLength3D()
	 */
	@Override
	public double getSqLength3D() {
		return WB_GeometryOp.getSqLength3D(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return WB_GeometryOp.calculateHashCode(xd(), yd(), zd());
	}


	/**
	 *
	 */
	public void invert() {
		mulSelf(-1);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public boolean isCollinear(final WB_Coord p, final WB_Coord q) {
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(this, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(this, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(sub(p).cross(sub(q)).getSqLength3D());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallel(final WB_Coord p) {
		final double pm2 = (p.xd() * p.xd()) + (p.yd() * p.yd()) + (p.zd() * p.zd());
		return ((cross(p).getSqLength3D() / (pm2 * getSqLength3D())) < WB_Epsilon.SQEPSILON);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallel(final WB_Coord p, final double t) {
		final double pm2 = (p.xd() * p.xd()) + (p.yd() * p.yd()) + (p.zd() * p.zd());
		return ((cross(p).getSqLength3D() / (pm2 * getSqLength3D())) < (t + WB_Epsilon.SQEPSILON));
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallelNorm(final WB_Coord p) {
		return (cross(p).getLength3D() < WB_Epsilon.EPSILON);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallelNorm(final WB_Coord p, final double t) {
		return (cross(p).getLength3D() < (t + WB_Epsilon.EPSILON));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#isZero()
	 */
	@Override
	public boolean isZero() {
		return WB_GeometryOp.isZero3D(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mul(double)
	 */
	@Override
	public WB_Point mul(final double f) {
		return new WB_Point(xd() * f, yd() * f, zd() * f);
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateMath#mulAddMul(double, double, double[])
	 */
	@Override
	public WB_Point mulAddMul(final double f, final double g, final double... x) {
		return new WB_Point((f * this.xd()) + (g * x[0]), (f * this.yd()) + (g * x[1]), (f * this.zd()) + (g * x[2]));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mulAddMul(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point mulAddMul(final double f, final double g, final WB_Coord p) {
		return new WB_Point((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()), (f * zd()) + (g * p.zd()));
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateMath#mulAddMulInto(wblut.geom.WB_MutableCoord, double, double, double[])
	 */
	@Override
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final double... x) {
		result.set((f * this.xd()) + (g * x[0]), (f * this.yd()) + (g * x[1]), (f * this.zd()) + (g * x[2]));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mulAddMulInto(double, double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final WB_Coord p) {
		result.set((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()), (f * zd()) + (g * p.zd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#mulAddMulSelf(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex mulAddMulSelf(final double f, final double g, final WB_Coord p) {
		set((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()), (f * zd()) + (g * p.zd()));
		return this;
	}

	@Override
	public HE_Vertex mulAddMulSelf(final double f, final double g, final double... x) {
		set((f * this.xd()) + (g * x[0]), (f * this.yd()) + (g * x[1]), (f * this.zd()) + (g * x[2]));
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mulInto(double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mulInto(final WB_MutableCoord result, final double f) {
		scaleInto(result, f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#mulSelf(double)
	 */
	@Override
	public HE_Vertex mulSelf(final double f) {
		set(f * xd(), f * yd(), f * zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#normalizeSelf()
	 */
	@Override
	public double normalizeSelf() {
		final double d = getLength3D();
		if (WB_Epsilon.isZero(d)) {
			set(0, 0, 0);
		} else {
			set(xd() / d, yd() / d, zd() / d);
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * double, double, double, double, double, double)
	 */
	/**
	 * @deprecated Use {@link #rotateAboutAxis2P(double,double,double,double,double,double,double)} instead
	 */
	@Deprecated
	@Override
	public WB_Point rotateAbout2PointAxis(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		return rotateAboutAxis2P(angle, p1x, p1y, p1z, p2x, p2y, p2z);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * double, double, double, double, double, double)
	 */
	@Override
	public WB_Point rotateAboutAxis2P(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	/**
	 * @deprecated Use {@link #rotateAboutAxis2P(double,WB_Coord,WB_Coord)} instead
	 */
	@Deprecated
	@Override
	public WB_Point rotateAbout2PointAxis(final double angle, final WB_Coord p1, final WB_Coord p2) {
		return rotateAboutAxis2P(angle, p1, p2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutAxis2P(final double angle, final WB_Coord p1, final WB_Coord p2) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	/**
	 * @deprecated Use {@link #rotateAboutAxis2PSelf(double,double,double,double,double,double,double)} instead
	 */
	@Deprecated
	@Override
	public HE_Vertex rotateAbout2PointAxisSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		return rotateAboutAxis2PSelf(angle, p1x, p1y, p1z, p2x, p2y, p2z);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	@Override
	public HE_Vertex rotateAboutAxis2PSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsPoint(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	/**
	 * @deprecated Use {@link #rotateAboutAxis2PSelf(double,WB_Coord,WB_Coord)} instead
	 */
	@Deprecated
	@Override
	public HE_Vertex rotateAbout2PointAxisSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {
		return rotateAboutAxis2PSelf(angle, p1, p2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex rotateAboutAxis2PSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		raa.applySelfAsPoint(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	@Override
	public WB_Point rotateAboutAxis(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(ax, ay, az));
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutAxis(final double angle, final WB_Coord p, final WB_Coord a) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	@Override
	public HE_Vertex rotateAboutAxisSelf(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(ax, ay, az));
		raa.applySelfAsPoint(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#rotateAboutAxisSelf(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex rotateAboutAxisSelf(final double angle, final WB_Coord p, final WB_Coord a) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		raa.applySelfAsPoint(this);
		return this;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	@Override
	public WB_Point rotateAboutOrigin(final double angle, final double x, final double y, final double z) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(x, y, z));
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutOrigin(final double angle, final WB_Coord a) {
		final WB_Point result = new WB_Point(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		raa.applySelfAsPoint(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	@Override
	public HE_Vertex rotateAboutOriginSelf(final double angle, final double x, final double y, final double z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(x, y, z));
		raa.applySelfAsPoint(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex rotateAboutOriginSelf(final double angle, final WB_Coord a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		raa.applySelfAsPoint(this);
		return this;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#scalarTriple(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public double scalarTriple(final WB_Coord v, final WB_Coord w) {
		return WB_GeometryOp.scalarTriple(xd(), yd(), zd(), v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#scale(double)
	 */
	@Override
	public WB_Point scale(final double f) {
		return mul(f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#scale(double, double, double)
	 */
	@Override
	public WB_Point scale(final double fx, final double fy, final double fz) {
		return new WB_Point(xd() * fx, yd() * fy, zd() * fz);
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateTransform#scaleInto(wblut.geom.WB_MutableCoord, double)
	 */
	@Override
	public void scaleInto(final WB_MutableCoord result, final double f) {
		result.set(xd() * f, yd() * f, zd() * f);
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateTransform#scaleInto(wblut.geom.WB_MutableCoord, double, double, double)
	 */
	@Override
	public void scaleInto(final WB_MutableCoord result, final double fx, final double fy, final double fz) {
		result.set(xd() * fx, yd() * fy, zd() * fz);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double)
	 */
	@Override
	public HE_Vertex scaleSelf(final double f) {
		mulSelf(f);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double, double,
	 * double)
	 */
	@Override
	public HE_Vertex scaleSelf(final double fx, final double fy, final double fz) {
		set(xd() * fx, yd() * fy, zd() * fz);
		return this;
	}

	/**
	 *
	 *
	 * @param otherXYZ
	 * @return
	 */
	public boolean smallerThan(final WB_Coord otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(xd(), otherXYZ.xd());
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(yd(), otherXYZ.yd());
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(zd(), otherXYZ.zd());
		return (_tmp < 0);
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateMath#sub(double[])
	 */
	@Override
	public WB_Point sub(final double... x) {
		return new WB_Point(this.xd() - x[0], this.yd() - x[1], this.zd() - x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#sub(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point sub(final WB_Coord p) {
		return new WB_Point(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_CoordinateMath#subInto(wblut.geom.WB_MutableCoord, double[])
	 */
	@Override
	public void subInto(final WB_MutableCoord result, final double... x) {
		result.set(this.xd() - x[0], this.yd() - x[1], this.zd() - x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#subInto(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void subInto(final WB_MutableCoord result, final WB_Coord p) {
		result.set(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#subSelf(double, double, double)
	 */
	@Override
	public HE_Vertex subSelf(final double... x) {
		set(xd() - x[0], yd() - x[1], zd() - x[2]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#subSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public HE_Vertex subSelf(final WB_Coord v) {
		set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#tensor(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_M33 tensor(final WB_Coord v) {
		return new WB_M33(WB_GeometryOp.tensor3D(xd(), yd(), zd(), v.xd(), v.yd(), v.zd()));
	}

	/**
	 *
	 *
	 * @param u
	 * @param v
	 * @return
	 */
	public WB_M33 tensor(final WB_Coord u, final WB_Coord v) {
		return new WB_M33(WB_GeometryOp.tensor3D(u.xd(), u.yd(), u.zd(), v.xd(), v.yd(), v.zd()));
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#trimSelf(double)
	 */
	@Override
	public HE_Vertex trimSelf(final double d) {
		if (getSqLength3D() > (d * d)) {
			normalizeSelf();
			mulSelf(d);
		}
		return this;
	}


	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public WB_Vector subToVector3D(final double x, final double y, final double z) {
		return new WB_Vector(this.xd() - x, this.yd() - y, this.zd() - z);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public WB_Vector subToVector3D(final WB_Coord p) {
		return new WB_Vector(xd() - p.xd(), yd() - p.yd(), zd() - p.zd());
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public WB_Vector subToVector2D(final double x, final double y, final double z) {
		return new WB_Vector(this.xd() - x, this.yd() - y, 0);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public WB_Vector subToVector2D(final WB_Coord p) {
		return new WB_Vector(xd() - p.xd(), yd() - p.yd(), 0);
	}




}
