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
 * WB_OrthoProject projects coordinates from world space to the X, Y or Z-plane.
 * Since a projection is not reversible, the 2D-to-3D functions always return a
 * point on the X-,Y- or Z-plane, unless the w-coordinate is explicitly given.
 *
 */
public class WB_OrthoProject implements WB_Map2D {
	/**
	 *
	 */
	int id;
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
	public WB_OrthoProject() {
		this(Z);
	}

	/**
	 *
	 *
	 * @param mode
	 */
	public WB_OrthoProject(final int mode) {
		super();
		if (mode < 0 || mode > 2) {
			throw new IndexOutOfBoundsException();
		}
		this.mode = mode;
	}

	/**
	 *
	 *
	 * @param v
	 */
	public WB_OrthoProject(final WB_Coord v) {
		set(v);
	}

	/**
	 *
	 *
	 * @param P
	 */
	public WB_OrthoProject(final WB_Plane P) {
		set(P.getNormal());
	}

	/**
	 *
	 *
	 * @param c
	 */
	public void set(final WB_Coord c) {
		if (Math.abs(c.xd()) > Math.abs(c.yd())) {
			mode = Math.abs(c.xd()) > Math.abs(c.zd()) ? X : Z;
		} else {
			mode = Math.abs(c.yd()) > Math.abs(c.zd()) ? Y : Z;
		}
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
			result.set(p.xd(), p.yd(), 0);
			break;
		case X:
			result.set(p.yd(), p.zd(), 0);
			break;
		case Y:
			result.set(p.zd(), p.xd(), 0);
			break;
		case Zrev:
			result.set(p.yd(), p.xd(), 0);
			break;
		case Xrev:
			result.set(p.zd(), p.yd(), 0);
			break;
		case Yrev:
			result.set(p.xd(), p.zd(), 0);
			break;
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
			result.set(x, y, 0);
			break;
		case X:
			result.set(y, z, 0);
			break;
		case Y:
			result.set(z, x, 0);
			break;
		case Zrev:
			result.set(y, x, 0);
			break;
		case Xrev:
			result.set(z, y, 0);
			break;
		case Yrev:
			result.set(x, z, 0);
			break;
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
			result.set(p.xd(), p.yd(), 0);
			break;
		case X:
			result.set(0, p.xd(), p.yd());
			break;
		case Y:
			result.set(p.yd(), 0, p.xd());
			break;
		case Zrev:
			result.set(p.yd(), p.xd(), 0);
			break;
		case Xrev:
			result.set(0, p.yd(), p.xd());
			break;
		case Yrev:
			result.set(p.xd(), 0, p.yd());
			break;
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
			result.set(u, v, w);
			break;
		case X:
			result.set(w, u, v);
			break;
		case Y:
			result.set(v, w, u);
			break;
		case Zrev:
			result.set(v, u, -w);
			break;
		case Xrev:
			result.set(-w, v, u);
			break;
		case Yrev:
			result.set(u, -w, v);
			break;
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
			result.set(u, v, 0);
			break;
		case X:
			result.set(0, u, v);
			break;
		case Y:
			result.set(v, 0, u);
			break;
		case Zrev:
			result.set(v, u, 0);
			break;
		case Xrev:
			result.set(0, v, u);
			break;
		case Yrev:
			result.set(u, 0, v);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Map2D#unmapPoint2D(wblut.geom.WB_Coord,
	 * wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapPoint2D(final WB_Coord p, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(p.xf(), p.yf(), 0);
			break;
		case X:
			result.set(0, p.xf(), p.yf());
			break;
		case Y:
			result.set(p.yf(), 0, p.xf());
			break;
		case Zrev:
			result.set(p.yf(), p.xf(), 0);
			break;
		case Xrev:
			result.set(0, p.yf(), p.xf());
			break;
		case Yrev:
			result.set(p.xf(), 0, p.yf());
			break;
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
			result.set(v.xd(), v.yd(), 0);
			break;
		case X:
			result.set(v.yd(), v.zd(), 0);
			break;
		case Y:
			result.set(v.zd(), v.xd(), 0);
			break;
		case Zrev:
			result.set(v.yd(), v.xd(), 0);
			break;
		case Xrev:
			result.set(v.zd(), v.yd(), 0);
			break;
		case Yrev:
			result.set(v.xd(), v.zd(), 0);
			break;
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
			result.set(x, y, 0);
			break;
		case X:
			result.set(y, z, 0);
			break;
		case Y:
			result.set(z, x, 0);
			break;
		case Zrev:
			result.set(y, x, 0);
			break;
		case Xrev:
			result.set(z, y, 0);
			break;
		case Yrev:
			result.set(x, z, 0);
			break;
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
			result.set(v.xd(), v.yd(), 0);
			break;
		case X:
			result.set(0, v.xd(), v.yd());
			break;
		case Y:
			result.set(v.yd(), 0, v.xd());
			break;
		case Zrev:
			result.set(v.yd(), v.xd(), 0);
			break;
		case Xrev:
			result.set(0, v.yd(), v.xd());
			break;
		case Yrev:
			result.set(v.xd(), 0, v.yd());
			break;
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
			result.set(u, v, w);
			break;
		case X:
			result.set(w, u, v);
			break;
		case Y:
			result.set(v, w, u);
			break;
		case Zrev:
			result.set(v, u, -w);
			break;
		case Xrev:
			result.set(-w, v, u);
			break;
		case Yrev:
			result.set(u, -w, v);
			break;
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
			result.set(u, v, 0);
			break;
		case X:
			result.set(0, u, v);
			break;
		case Y:
			result.set(v, 0, u);
			break;
		case Zrev:
			result.set(v, u, 0);
			break;
		case Xrev:
			result.set(0, v, u);
			break;
		case Yrev:
			result.set(u, 0, v);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Map2D#unmapVector2D(wblut.geom.WB_Coord,
	 * wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapVector2D(final WB_Coord v, final WB_MutableCoord result) {
		switch (mode) {
		case Z:
			result.set(v.xf(), v.yf(), 0);
			break;
		case X:
			result.set(0, v.xf(), v.yf());
			break;
		case Y:
			result.set(v.yf(), 0, v.xf());
			break;
		case Zrev:
			result.set(v.yf(), v.xf(), 0);
			break;
		case Xrev:
			result.set(0, v.yf(), v.xf());
			break;
		case Yrev:
			result.set(v.xf(), 0, v.yf());
			break;
		}
	}
}
