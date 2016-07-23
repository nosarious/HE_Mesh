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

import wblut.math.WB_Ease;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 *
 */
public class WB_Point extends WB_Vector {

	private static final WB_Coord X = new WB_SimpleVector(1, 0, 0);
	private static final WB_Coord Y = new WB_SimpleVector(0, 1, 0);
	private static final WB_Coord Z = new WB_SimpleVector(0, 0, 1);
	private static final WB_Coord ORIGIN = new WB_SimpleVector(0, 0, 0);
	private static final WB_Coord ZERO = new WB_SimpleVector(0, 0, 0);

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord X() {
		return X;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord Y() {
		return Y;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord Z() {
		return Z;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord ZERO() {
		return ZERO;
	}

	/**
	 *
	 *
	 * @return
	 */
	public static WB_Coord ORIGIN() {
		return ORIGIN;
	}

	/**
	 *
	 */
	public WB_Point() {
		super();
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 */
	public WB_Point(final double x, final double y) {
		super(x, y);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public WB_Point(final double x, final double y, final double z) {
		super(x, y, z);
	}

	/**
	 *
	 *
	 * @param x
	 */
	public WB_Point(final double[] x) {
		super(x);
	}

	/**
	 *
	 *
	 * @param fromPoint
	 * @param toPoint
	 */
	public WB_Point(final double[] fromPoint, final double[] toPoint) {
		super(fromPoint, toPoint);
	}

	/**
	 *
	 *
	 * @param v
	 */
	public WB_Point(final WB_Coord v) {
		super(v);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addMulSelf(double, double, double, double)
	 */
	@Override
	public WB_Point addMulSelf(final double f, final double... x) {
		set(xd() + f * x[0], yd() + f * x[1], zd() + f * x[2]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addMulSelf(double, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point addMulSelf(final double f, final WB_Coord p) {
		set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addSelf(double, double, double)
	 */
	@Override
	public WB_Point addSelf(final double x, final double y, final double z) {
		set(xd() + x, yd() + y, zd() + z);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addSelf(double, double, double)
	 */
	@Override
	public WB_Point addSelf(final double... x) {
		set(xd() + x[0], yd() + x[1], zd() + x[2]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point addSelf(final WB_Coord p) {
		set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#applyAsNormalSelf(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#applyAsPointSelf(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#applyAsVectorSelf(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#crossSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point crossSelf(final WB_Coord p) {
		set(yd() * p.zd() - this.zd() * p.yd(), this.zd() * p.xd() - this.xd() * p.zd(),
				this.xd() * p.yd() - yd() * p.xd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#divSelf(double)
	 */
	@Override
	public WB_Point divSelf(final double f) {
		return mulSelf(1.0 / f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#mulAddMulSelf(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point mulAddMulSelf(final double f, final double g, final WB_Coord p) {
		set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
		return this;
	}

	@Override
	public WB_Point mulAddMulSelf(final double f, final double g, final double... x) {
		if (x.length == 3) {
			set(f * this.xd() + g * x[0], f * this.yd() + g * x[1], f * this.zd() + g * x[2]);
			return this;
		} else if (x.length == 2) {
			set(f * this.xd() + g * x[0], f * this.yd() + g * x[1], this.zd());
			return this;
		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#mulSelf(double)
	 */
	@Override
	public WB_Point mulSelf(final double f) {
		set(f * xd(), f * yd(), f * zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#scaleSelf(double)
	 */
	@Override
	public WB_Point scaleSelf(final double f) {
		mulSelf(f);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#scale(double)
	 */
	@Override
	public WB_Point scale(final double f) {
		return mul(f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#scaleSelf(double, double, double)
	 */
	@Override
	public WB_Point scaleSelf(final double fx, final double fy, final double fz) {
		set(xd() * fx, yd() * fy, zd() * fz);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#scale(double, double, double)
	 */
	@Override
	public WB_Point scale(final double fx, final double fy, final double fz) {
		return new WB_Point(xd() * fx, yd() * fy, zd() * fz);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#subSelf(double, double, double)
	 */
	@Override
	public WB_Point subSelf(final double... x) {
		set(xd() - x[0], yd() - x[1], zd() - x[2]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#subSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point subSelf(final WB_Coord v) {
		set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#trimSelf(double)
	 */
	@Override
	public WB_Point trimSelf(final double d) {
		if (getSqLength3D() > d * d) {
			normalizeSelf();
			mulSelf(d);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#add(double[])
	 */
	@Override
	public WB_Point add(final double... x) {
		return new WB_Point(this.xd() + x[0], this.yd() + x[1], this.zd() + x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#add(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point add(final WB_Coord p) {
		return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addMul(double, double[])
	 */
	@Override
	public WB_Point addMul(final double f, final double... x) {
		return new WB_Point(this.xd() + f * x[0], this.yd() + f * x[1], this.zd() + f * x[2]);
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @param q
	 * @return
	 */
	public static WB_Point addMul(final WB_Coord p, final double f, final WB_Coord q) {
		return new WB_Point(p.xd() + f * q.xd(), p.yd() + f * q.yd(), p.zd() + f * q.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#addMul(double, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point addMul(final double f, final WB_Coord p) {
		return new WB_Point(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point apply(final WB_Transform T) {
		final WB_Point p = new WB_Point(this);
		return p.applySelf(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#applySelf(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Point applySelf(final WB_Transform T) {
		return applyAsPointSelf(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#applyAsNormal(wblut.geom.WB_Transform)
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
	 * @see wblut.geom.WB_Vector#applyAsPoint(wblut.geom.WB_Transform)
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
	 * @see wblut.geom.WB_Vector#applyAsVector(wblut.geom.WB_Transform)
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
	 * @see wblut.geom.WB_Vector#cross(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point cross(final WB_Coord p) {
		return new WB_Point(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd() * p.zd(),
				xd() * p.yd() - yd() * p.xd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#div(double)
	 */
	@Override
	public WB_Point div(final double f) {
		return mul(1.0 / f);
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
	 * @see wblut.geom.WB_Vector#get()
	 */
	@Override
	public WB_Point get() {
		return new WB_Point(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return WB_HashCode.calculateHashCode(xd(), yd(), zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#mul(double)
	 */
	@Override
	public WB_Point mul(final double f) {
		return new WB_Point(xd() * f, yd() * f, zd() * f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#mulAddMul(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point mulAddMul(final double f, final double g, final WB_Coord p) {
		return new WB_Point(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
	}

	@Override
	public WB_Point mulAddMul(final double f, final double g, final double... x) {
		if (x.length == 3) {
			return new WB_Point(f * this.xd() + g * x[0], f * this.yd() + g * x[1], f * this.zd() + g * x[2]);
		} else if (x.length == 2) {
			return new WB_Point(f * this.xd() + g * x[0], f * this.yd() + g * x[1], this.zd());
		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#sub(double[])
	 */
	@Override
	public WB_Point sub(final double... x) {
		return new WB_Point(this.xd() - x[0], this.yd() - x[1], this.zd() - x[2]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#sub(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point sub(final WB_Coord p) {
		return new WB_Point(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Point sub(final WB_Coord p, final WB_Coord q) {
		return new WB_Point(p.xd() - q.xd(), p.yd() - q.yd(), p.zd() - q.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#toString()
	 */
	@Override
	public String toString() {
		return "WB_Point [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#getOrthoNormal2D()
	 */
	@Override
	public WB_Point getOrthoNormal2D() {
		final WB_Point a = new WB_Point(-yd(), xd(), 0);
		a.normalizeSelf();
		return a;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Vector#getOrthoNormal3D()
	 */
	@Override
	public WB_Point getOrthoNormal3D() {
		if (Math.abs(zd()) > WB_Epsilon.EPSILON) {
			final WB_Point a = new WB_Point(1, 0, -xd() / zd());
			a.normalizeSelf();
			return a;
		} else {
			return new WB_Point(0, 0, 1);
		}
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
		raa.applyAsPointSelf(result);
		return result;
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
		raa.applyAsPointSelf(result);
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
	public WB_Point rotateAboutAxis2PSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applyAsPointSelf(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutAxis2PSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		raa.applyAsPointSelf(this);
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
		raa.applyAsPointSelf(result);
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
		raa.applyAsPointSelf(result);
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
	public WB_Point rotateAboutAxisSelf(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(ax, ay, az));
		raa.applyAsPointSelf(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#rotateAboutAxisSelf(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutAxisSelf(final double angle, final WB_Coord p, final WB_Coord a) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		raa.applyAsPointSelf(this);
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
		raa.applyAsPointSelf(result);
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
		raa.applyAsPointSelf(result);
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
	public WB_Point rotateAboutOriginSelf(final double angle, final double x, final double y, final double z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(x, y, z));
		raa.applyAsPointSelf(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Point rotateAboutOriginSelf(final double angle, final WB_Coord a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		raa.applyAsPointSelf(this);
		return this;
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double absDot(final WB_Coord p, final WB_Coord q) {
		return WB_Math.fastAbs(WB_GeometryOp.dot(p.xd(), p.yd(), p.zd(), q.xd(), q.yd(), q.zd()));
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double absDot2D(final WB_Coord p, final WB_Coord q) {
		return WB_Math.fastAbs(WB_GeometryOp.dot2D(p.xd(), p.yd(), q.xd(), q.yd()));
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Point add(final WB_Coord p, final WB_Coord q) {
		return new WB_Point(q.xd() + p.xd(), q.yd() + p.yd(), q.zd() + p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Point cross(final WB_Coord p, final WB_Coord q) {
		return new WB_Point(p.yd() * q.zd() - p.zd() * q.yd(), p.zd() * q.xd() - p.xd() * q.zd(),
				p.xd() * q.yd() - p.yd() * q.xd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @return
	 */
	public static WB_Point div(final WB_Coord p, final double f) {
		return WB_Point.mul(p, 1.0 / f);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double dot(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.dot(p.xd(), p.yd(), p.zd(), q.xd(), q.yd(), q.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static double dot2D(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.dot2D(p.xd(), p.yd(), q.xd(), q.yd());
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public static double getDistance2D(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.getDistance2D(q.xd(), q.yd(), p.xd(), p.yd());
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public static double getDistance3D(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.getDistance3D(q.xd(), q.yd(), q.zd(), p.xd(), p.yd(), p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getHeading2D(final WB_Coord p) {
		return Math.atan2(p.yd(), p.xd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getLength2D(final WB_Coord p) {
		return WB_GeometryOp.getLength2D(p.xd(), p.yd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static double getLength3D(final WB_Coord p) {
		return WB_GeometryOp.getLength3D(p.xd(), p.yd(), p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static WB_Point getOrthoNormal2D(final WB_Coord p) {
		final WB_Point a = new WB_Point(-p.yd(), p.xd(), 0);
		a.normalizeSelf();
		return a;
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static WB_Point getOrthoNormal3D(final WB_Coord p) {
		if (Math.abs(p.zd()) > WB_Epsilon.EPSILON) {
			final WB_Point a = new WB_Point(1, 0, -p.xd() / p.zd());
			a.normalizeSelf();
			return a;
		} else {
			return new WB_Point(0, 0, 1);
		}
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public static double getSqDistance2D(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.getSqDistance2D(q.xd(), q.yd(), p.xd(), p.yd());
	}

	/**
	 *
	 *
	 * @param q
	 * @param p
	 * @return
	 */
	public static double getSqDistance3D(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.getSqDistance3D(q.xd(), q.yd(), q.zd(), p.xd(), p.yd(), p.zd());
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public static double getSqLength2D(final WB_Coord v) {
		return WB_GeometryOp.getSqLength2D(v.xd(), v.yd());
	}

	/**
	 *
	 *
	 * @param v
	 * @return
	 */
	public static double getSqLength3D(final WB_Coord v) {
		return WB_GeometryOp.getSqLength3D(v.xd(), v.yd(), v.zd());
	}

	/**
	 *
	 *
	 * @param o
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isCollinear(final WB_Coord o, final WB_Coord p, final WB_Coord q) {
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(o, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(o, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(cross(sub(o, p), sub(o, q)).getSqLength3D());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallel(final WB_Coord p, final WB_Coord q) {
		final double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
		final double qm2 = q.xd() * q.xd() + q.yd() * q.yd() + q.zd() * q.zd();
		return cross(p, q).getSqLength3D() / (pm2 * qm2) < WB_Epsilon.SQEPSILON;
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isParallel(final WB_Coord p, final WB_Coord q, final double t) {
		final double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
		final double qm2 = q.xd() * q.xd() + q.yd() * q.yd() + q.zd() * q.zd();
		return cross(p, q).getSqLength3D() / (pm2 * qm2) < t + WB_Epsilon.SQEPSILON;
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallelNorm(final WB_Coord p, final WB_Coord q) {
		return cross(p, q).getLength3D() < WB_Epsilon.EPSILON;
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isParallelNorm(final WB_Coord p, final WB_Coord q, final double t) {
		return cross(p, q).getLength3D() < t + WB_Epsilon.EPSILON;
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @return
	 */
	public static WB_Point mul(final WB_Coord p, final double f) {
		return new WB_Point(p.xd() * f, p.yd() * f, p.zd() * f);
	}

	/**
	 *
	 *
	 * @param f
	 * @param p
	 * @param g
	 * @param q
	 * @return
	 */
	public static WB_Point mulAddMul(final double f, final WB_Coord p, final double g, final WB_Coord q) {
		return new WB_Point(f * p.xd() + g * q.xd(), f * p.yd() + g * q.yd(), f * p.zd() + g * q.zd());
	}

	/**
	 *
	 *
	 * @param u
	 * @param v
	 * @param w
	 * @return
	 */
	public static double scalarTriple(final WB_Coord u, final WB_Coord v, final WB_Coord w) {
		return WB_GeometryOp.scalarTriple(u.xd(), u.yd(), u.zd(), v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Vector subToVector2D(final WB_Coord p, final WB_Coord q) {
		return new WB_Vector(p.xd() - q.xd(), p.yd() - q.yd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Vector subToVector3D(final WB_Coord p, final WB_Coord q) {
		return new WB_Vector(p.xd() - q.xd(), p.yd() - q.yd(), p.zd() - q.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final WB_Coord p) {
		int cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(zd(), p.zd());
	}

	public static WB_Point interpolate(final WB_Coord v, final WB_Coord w, final double f) {
		return new WB_Point(WB_GeometryOp.interpolate(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f));
	}

	public static WB_Point interpolateEaseIn(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Point(WB_GeometryOp.interpolateEaseIn(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}

	public static WB_Point interpolateEaseOut(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Point(WB_GeometryOp.interpolateEaseOut(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}

	public static WB_Point interpolateEaseInOut(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Point(
				WB_GeometryOp.interpolateEaseInOut(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}

}
