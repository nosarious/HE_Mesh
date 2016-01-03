/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 * 
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 * 
 */
package wblut.geom;

/**
 *
 * WB_EmbeddedPlane maps coordinates from world space into the coordinate system
 * associated with a plane. The plane can be X-, Y- or Z-plane with offset,
 * reverse X-, Y- or Z-plane with offset, or an arbitrary
 * {@link wblut.geom.WB_Plane}
 *
 */
public class WB_PlanarMap extends WB_CoordinateSystem implements WB_Map2D {
	/**
	 *
	 */
	private double offset;
	/**
	 *
	 */
	int id;
	/**
	 *
	 */
	private final WB_Transform T2D3D;
	/**
	 *
	 */
	private int mode;
	/**
	 *
	 */
	public static final int X = 0;
	/**
	 *
	 */
	public static final int Y = 1;
	/**
	 *
	 */
	public static final int Z = 2;
	/**
	 *
	 */
	public static final int Xrev = 3;
	/**
	 *
	 */
	public static final int Yrev = 4;
	/**
	 *
	 */
	public static final int Zrev = 5;
	/**
	 *
	 */
	public static final int PLANE = 6;
	/**
	 *
	 */
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();

	/**
	 *
	 */
	public WB_PlanarMap() {
		this(Z, 0);
	}

	/**
	 *
	 *
	 * @param mode
	 * @param offset
	 */
	public WB_PlanarMap(final int mode, final double offset) {
		super();
		this.mode = mode;
		this.offset = offset;
		if ((mode < 0) || (mode > 5)) {
			throw (new IndexOutOfBoundsException());
		}
		if (mode == X) {
			set(geometryfactory.createPoint(offset, 0, 0), geometryfactory.Y(), geometryfactory.Z(),
					geometryfactory.X(), geometryfactory.WORLD());
			this.mode = X;
		} else if (mode == Y) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Z(), geometryfactory.X(),
					geometryfactory.Y(), geometryfactory.WORLD());
			this.mode = Y;
		} else if (mode == Xrev) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Z(), geometryfactory.Y(),
					geometryfactory.minX(), geometryfactory.WORLD());
			this.mode = Xrev;
		} else if (mode == Yrev) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.X(), geometryfactory.Z(),
					geometryfactory.minY(), geometryfactory.WORLD());
			this.mode = Yrev;
		} else if (mode == Zrev) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Y(), geometryfactory.X(),
					geometryfactory.minZ(), geometryfactory.WORLD());
			this.mode = Zrev;
		} else {// XY
			set(geometryfactory.createPoint(0, 0, offset), geometryfactory.X(), geometryfactory.Y(),
					geometryfactory.Z(), geometryfactory.WORLD());
			this.mode = Z;
		}
		T2D3D = getTransformToWorld();
	}

	/**
	 *
	 *
	 * @param mode
	 */
	public WB_PlanarMap(final int mode) {
		this(mode, 0);
	}

	/**
	 *
	 *
	 * @param P
	 */
	public WB_PlanarMap(final WB_Plane P) {
		super(P.getOrigin(), P.getU(), P.getV(), P.getW(), geometryfactory.WORLD());
		mode = PLANE;
		T2D3D = getTransformToWorld();
	}

	/**
	 *
	 *
	 * @param P
	 * @param offset
	 */
	public WB_PlanarMap(final WB_Plane P, final double offset) {
		super(P.getOrigin().addMul(offset, P.getNormal()), P.getU(), P.getV(), P.getW(), geometryfactory.WORLD());
		mode = PLANE;
		T2D3D = getTransformToWorld();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#pointTo2D(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mapPoint3D(final WB_Coord p, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(p.xd(), p.yd(), p.zd() - offset);
			break;
		case X:
			result.set(p.yd(), p.zd(), p.xd() - offset);
			break;
		case Y:
			result.set(p.zd(), p.xd(), p.yd() - offset);
			break;
		case Zrev:
			result.set(p.yd(), p.xd(), offset - p.zd());
			break;
		case Xrev:
			result.set(p.zd(), p.yd(), offset - p.xd());
			break;
		case Yrev:
			result.set(p.xd(), p.zd(), offset - p.yd());
			break;
		default:
			T2D3D.applyInvAsPoint(p, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#pointTo2D(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mapPoint3D(final double x, final double y, final double z, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(x, y, z - offset);
			break;
		case X:
			result.set(y, z, x - offset);
			break;
		case Y:
			result.set(z, x, y - offset);
			break;
		case Zrev:
			result.set(y, x, offset - z);
			break;
		case Xrev:
			result.set(z, y, offset - x);
			break;
		case Yrev:
			result.set(x, z, offset - y);
			break;
		default:
			T2D3D.applyInvAsPoint(x, y, z, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#pointTo3D(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapPoint3D(final WB_Coord p, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(p.xd(), p.yd(), p.zd() + offset);
			break;
		case X:
			result.set(p.zd() + offset, p.xd(), p.yd());
			break;
		case Y:
			result.set(p.yd(), p.zd() + offset, p.xd());
			break;
		case Zrev:
			result.set(p.yd(), p.xd(), offset - p.zd());
			break;
		case Xrev:
			result.set(offset - p.zd(), p.yd(), p.xd());
			break;
		case Yrev:
			result.set(p.xd(), offset - p.zd(), p.yd());
			break;
		default:
			T2D3D.applyAsPoint(p, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#pointTo3D(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapPoint3D(final double u, final double v, final double w, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(u, v, w + offset);
			break;
		case X:
			result.set(w + offset, u, v);
			break;
		case Y:
			result.set(v, w + offset, u);
			break;
		case Zrev:
			result.set(v, u, offset - w);
			break;
		case Xrev:
			result.set(offset - w, v, u);
			break;
		case Yrev:
			result.set(u, offset - w, v);
			break;
		default:
			T2D3D.applyAsPoint(u, v, w, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#pointTo3D(double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapPoint2D(final double u, final double v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(u, v, offset);
			break;
		case X:
			result.set(offset, u, v);
			break;
		case Y:
			result.set(v, offset, u);
			break;
		case Zrev:
			result.set(v, u, offset);
			break;
		case Xrev:
			result.set(offset, v, u);
			break;
		case Yrev:
			result.set(u, offset, v);
			break;
		default:
			T2D3D.applyAsPoint(u, v, 0, result);
		}
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map2D#unmapPoint2D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapPoint2D(final WB_Coord p, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(p.xf(), p.yf(), offset);
			break;
		case X:
			result.set(offset, p.xf(), p.yf());
			break;
		case Y:
			result.set(p.yf(), offset, p.xf());
			break;
		case Zrev:
			result.set(p.yf(), p.xf(), offset);
			break;
		case Xrev:
			result.set(offset, p.yf(), p.xf());
			break;
		case Yrev:
			result.set(p.xf(), offset, p.yf());
			break;
		default:
			T2D3D.applyAsPoint(p.xf(), p.yf(), 0, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#vectorTo2D(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mapVector3D(final WB_Coord v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(v.xd(), v.yd(), v.zd() - offset);
			break;
		case X:
			result.set(v.yd(), v.zd(), v.xd() - offset);
			break;
		case Y:
			result.set(v.zd(), v.xd(), v.yd() - offset);
			break;
		case Zrev:
			result.set(v.yd(), v.xd(), offset - v.zd());
			break;
		case Xrev:
			result.set(v.zd(), v.yd(), offset - v.xd());
			break;
		case Yrev:
			result.set(v.xd(), v.zd(), offset - v.yd());
			break;
		default:
			T2D3D.applyInvAsVector(v, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#vectorTo2D(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mapVector3D(final double x, final double y, final double z, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(x, y, z - offset);
			break;
		case X:
			result.set(y, z, x - offset);
			break;
		case Y:
			result.set(z, x, y - offset);
			break;
		case Zrev:
			result.set(y, x, offset - z);
			break;
		case Xrev:
			result.set(z, y, offset - x);
			break;
		case Yrev:
			result.set(x, z, offset - y);
			break;
		default:
			T2D3D.applyInvAsVector(x, y, z, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#vectorTo3D(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapVector3D(final WB_Coord v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(v.xd(), v.yd(), v.zd() + offset);
			break;
		case X:
			result.set(v.zd() + offset, v.xd(), v.yd());
			break;
		case Y:
			result.set(v.yd(), v.zd() + offset, v.xd());
			break;
		case Zrev:
			result.set(v.yd(), v.xd(), offset - v.zd());
			break;
		case Xrev:
			result.set(offset - v.zd(), v.yd(), v.xd());
			break;
		case Yrev:
			result.set(v.xd(), offset - v.zd(), v.yd());
			break;
		default:
			T2D3D.applyAsVector(v, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#vectorTo3D(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapVector3D(final double u, final double v, final double w, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(u, v, w + offset);
			break;
		case X:
			result.set(w + offset, u, v);
			break;
		case Y:
			result.set(v, w + offset, u);
			break;
		case Zrev:
			result.set(v, u, offset - w);
			break;
		case Xrev:
			result.set(offset - w, v, u);
			break;
		case Yrev:
			result.set(u, offset - w, v);
			break;
		default:
			T2D3D.applyAsVector(u, v, w, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Context2D#vectorTo3D(double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void unmapVector2D(final double u, final double v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(u, v, offset);
			break;
		case X:
			result.set(0, u, v + offset);
			break;
		case Y:
			result.set(v, 0, u + offset);
			break;
		case Zrev:
			result.set(v, u, offset);
			break;
		case Xrev:
			result.set(offset, v, u);
			break;
		case Yrev:
			result.set(u, offset, v);
			break;
		default:
			T2D3D.applyAsVector(u, v, 0, result);
		}
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map2D#unmapVector2D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapVector2D(final WB_Coord v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(v.xf(), v.yf(), offset);
			break;
		case X:
			result.set(offset, v.xf(), v.yf());
			break;
		case Y:
			result.set(v.yf(), offset, v.xf());
			break;
		case Zrev:
			result.set(v.yf(), v.xf(), offset);
			break;
		case Xrev:
			result.set(offset, v.yf(), v.xf());
			break;
		case Yrev:
			result.set(v.xf(), offset, v.yf());
			break;
		default:
			T2D3D.applyAsVector(v.xf(), v.yf(), 0, result);
		}
	}
}
