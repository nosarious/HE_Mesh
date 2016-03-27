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
import wblut.math.WB_M33;
import wblut.math.WB_Math;

/**
 *
 */
public class WB_Vector extends WB_SimpleVector implements WB_MutableCoordinateFull {
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
	public WB_Vector() {
		super();
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 */
	public WB_Vector(final double x, final double y) {
		super(x, y);
	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public WB_Vector(final double x, final double y, final double z) {
		super(x, y, z);
	}

	/**
	 *
	 *
	 * @param x
	 */
	public WB_Vector(final double[] x) {
		super(x);
	}

	/**
	 *
	 *
	 * @param fromPoint
	 * @param toPoint
	 */
	public WB_Vector(final double[] fromPoint, final double[] toPoint) {
		super(fromPoint, toPoint);
	}

	/**
	 *
	 *
	 * @param v
	 */
	public WB_Vector(final WB_Coord v) {
		super(v);
	}

	/**
	 *
	 *
	 * @param fromPoint
	 * @param toPoint
	 */
	public WB_Vector(final WB_Coord fromPoint, final WB_Coord toPoint) {
		super(fromPoint, toPoint);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double, double,
	 * double, double)
	 */
	@Override
	public WB_Vector addMulSelf(final double f, final double... x) {
		if (x.length == 3) {
			set(xd() + f * x[0], yd() + f * x[1], zd() + f * x[2]);
			return this;
		} else if (x.length == 2) {
			set(xd() + f * x[0], yd() + f * x[1], zd());
			return this;

		}

		throw new IllegalArgumentException("Array should be length 2 or 3.");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector addMulSelf(final double f, final WB_Coord p) {
		set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#addSelf(double, double, double)
	 */
	@Override
	public WB_Vector addSelf(final double... x) {
		if (x.length == 3) {
			set(xd() + x[0], yd() + x[1], zd() + x[2]);
			return this;
		} else if (x.length == 2) {
			set(xd() + x[0], yd() + x[1], zd());
			return this;

		}

		throw new IllegalArgumentException("Array should be length 2 or 3.");

	}

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public WB_Vector addSelf(final double x, final double y, final double z) {
		set(xd() + x, yd() + y, zd() + z);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#addSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector addSelf(final WB_Coord p) {
		set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsNormalSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public WB_Vector applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsPointSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public WB_Vector applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#applyAsVectorSelf(wblut.geom.
	 * WB_Transform )
	 */
	@Override
	public WB_Vector applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#crossSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector crossSelf(final WB_Coord p) {
		set(yd() * p.zd() - this.zd() * p.yd(), this.zd() * p.xd() - this.xd() * p.zd(),
				this.xd() * p.yd() - yd() * p.xd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#divSelf(double)
	 */
	@Override
	public WB_Vector divSelf(final double f) {
		return mulSelf(1.0 / f);
	}

	/**
	 *
	 */
	public void invert() {
		mulSelf(-1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#mulAddMulSelf(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector mulAddMulSelf(final double f, final double g, final WB_Coord p) {
		set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#mulAddMulSelf(double, double,
	 * double[])
	 */
	@Override
	public WB_Vector mulAddMulSelf(final double f, final double g, final double... x) {
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
	 * @see wblut.geom.WB_MutableCoordinateMath#mulSelf(double)
	 */
	@Override
	public WB_Vector mulSelf(final double f) {
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
	 * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double)
	 */
	@Override
	public WB_Vector scaleSelf(final double f) {
		mulSelf(f);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#scale(double)
	 */
	@Override
	public WB_Vector scale(final double f) {
		return mul(f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double, double,
	 * double)
	 */
	@Override
	public WB_Vector scaleSelf(final double fx, final double fy, final double fz) {
		set(xd() * fx, yd() * fy, zd() * fz);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#scale(double, double, double)
	 */
	@Override
	public WB_Vector scale(final double fx, final double fy, final double fz) {
		return new WB_Vector(xd() * fx, yd() * fy, zd() * fz);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#subSelf(double, double, double)
	 */
	@Override
	public WB_Vector subSelf(final double... x) {
		if (x.length == 3) {
			set(xd() - x[0], yd() - x[1], zd() - x[2]);
			return this;
		} else if (x.length == 2) {
			set(xd() - x[0], yd() - x[1], zd());
			return this;
		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#subSelf(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector subSelf(final WB_Coord v) {
		set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateMath#trimSelf(double)
	 */
	@Override
	public WB_Vector trimSelf(final double d) {
		if (getSqLength3D() > d * d) {
			normalizeSelf();
			mulSelf(d);
		}
		return this;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#absDot2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double absDot2D(final WB_Coord p) {
		return WB_Math.fastAbs(WB_GeometryOp.dot2D(xd(), yd(), p.xd(), p.yd()));
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#add(double, double, double)
	 */
	@Override
	public WB_Vector add(final double... x) {
		if (x.length == 3) {
			return new WB_Vector(this.xd() + x[0], this.yd() + x[1], this.zd() + x[2]);
		} else if (x.length == 2) {
			return new WB_Vector(this.xd() + x[0], this.yd() + x[1], this.zd());

		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addInto(double, double, double,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addInto(final WB_MutableCoord result, final double... x) {
		if (x.length == 3) {
			result.set(xd() + x[0], yd() + x[1], zd() + x[2]);
		} else if (x.length == 2) {
			result.set(xd() + x[0], yd() + x[1], zd());
		} else {

			throw new IllegalArgumentException("Array should be length 2 or 3.");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#add(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector add(final WB_Coord p) {
		return new WB_Vector(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Vector add(final WB_Coord p, final WB_Coord q) {
		return new WB_Vector(q.xd() + p.xd(), q.yd() + p.yd(), q.zd() + p.zd());
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
	public WB_Vector addMul(final double f, final double... x) {
		if (x.length == 3) {
			return new WB_Vector(this.xd() + f * x[0], this.yd() + f * x[1], this.zd() + f * x[2]);
		} else if (x.length == 2) {
			return new WB_Vector(this.xd() + f * x[0], this.yd() + f * x[1], this.zd());
		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMulInto(double, double, double,
	 * double, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addMulInto(final WB_MutableCoord result, final double f, final double... x) {
		if (x.length == 3) {
			result.set(xd() + f * x[0], yd() + f * x[1], zd() + f * x[2]);

		} else if (x.length == 2) {
			result.set(xd() + f * x[0], yd() + f * x[1], zd());
		} else {

			throw new IllegalArgumentException("Array should be length 2 or 3.");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMul(double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector addMul(final double f, final WB_Coord p) {
		return new WB_Vector(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @param q
	 * @return
	 */
	public static WB_Vector addMul(final WB_Coord p, final double f, final WB_Coord q) {
		return new WB_Vector(p.xd() + f * q.xd(), p.yd() + f * q.yd(), p.zd() + f * q.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#addMulInto(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void addMulInto(final WB_MutableCoord result, final double f, final WB_Coord p) {
		result.set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#apply(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Vector apply(final WB_Transform T) {
		final WB_Vector v = new WB_Vector(this);
		return v.applySelf(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateMath#applySelf(wblut.geom.WB_Transform)
	 */
	@Override
	public WB_Vector applySelf(final WB_Transform T) {
		return applyAsVectorSelf(T);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#applyInto(wblut.geom.WB_Transform,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	/**
	 *
	 *
	 * @param T
	 * @param result
	 * @deprecated Use {@link #applyInto(WB_MutableCoord,WB_Transform)} instead
	 */
	@Deprecated

	public void applyInto(final WB_Transform T, final WB_MutableCoord result) {
		applyInto(result, T);
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
	/**
	 *
	 *
	 * @param T
	 * @param result
	 * @deprecated Use {@link #applyAsNormalInto(WB_MutableCoord,WB_Transform)}
	 *             instead
	 */
	@Deprecated

	public void applyAsNormalInto(final WB_Transform T, final WB_MutableCoord result) {
		applyAsNormalInto(result, T);
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
	/**
	 *
	 *
	 * @param T
	 * @param result
	 * @deprecated Use {@link #applyAsPointInto(WB_MutableCoord,WB_Transform)}
	 *             instead
	 */
	@Deprecated

	public void applyAsPointInto(final WB_Transform T, final WB_MutableCoord result) {
		applyAsPointInto(result, T);
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
	/**
	 *
	 *
	 * @param T
	 * @param result
	 * @deprecated Use {@link #applyAsVectorInto(WB_MutableCoord,WB_Transform)}
	 *             instead
	 */
	@Deprecated

	public void applyAsVectorInto(final WB_Transform T, final WB_MutableCoord result) {
		applyAsVectorInto(result, T);
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
	public WB_Vector cross(final WB_Coord p) {
		return new WB_Vector(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd() * p.zd(),
				xd() * p.yd() - yd() * p.xd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Vector cross(final WB_Coord p, final WB_Coord q) {
		return new WB_Vector(p.yd() * q.zd() - p.zd() * q.yd(), p.zd() * q.xd() - p.xd() * q.zd(),
				p.xd() * q.yd() - p.yd() * q.xd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#crossInto(wblut.geom.WB_Coordinate,
	 * wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void crossInto(final WB_MutableCoord result, final WB_Coord p) {
		result.set(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd() * p.zd(), xd() * p.yd() - yd() * p.xd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#div(double)
	 */
	@Override
	public WB_Vector div(final double f) {
		return mul(1.0 / f);
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @return
	 */
	public static WB_Vector div(final WB_Coord p, final double f) {
		return WB_Vector.mul(p, 1.0 / f);
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
	 * @see wblut.geom.WB_CoordinateMath#dot(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double dot(final WB_Coord p) {
		return WB_GeometryOp.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#dot2D(wblut.geom.WB_Coordinate)
	 */
	@Override
	public double dot2D(final WB_Coord p) {
		return WB_GeometryOp.dot2D(xd(), yd(), p.xd(), p.yd());
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

	/**
	 *
	 *
	 * @return
	 */
	public WB_Vector get() {
		return new WB_Vector(xd(), yd(), zd());
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
	public static double getAngle(final WB_Coord q, final WB_Coord p) {
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
	public static double getAngleNorm(final WB_Coord q, final WB_Coord p) {
		return WB_GeometryOp.angleBetweenNorm(q.xd(), q.yd(), q.zd(), p.xd(), p.yd(), p.zd());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getLength3D()
	 */
	@Override
	public double getLength3D() {
		return WB_GeometryOp.getLength3D(xd(), yd(), zd());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getLength2D()
	 */
	@Override
	public double getLength2D() {
		return WB_GeometryOp.getLength2D(xd(), yd());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getSqLength3D()
	 */
	@Override
	public double getSqLength3D() {
		return WB_GeometryOp.getSqLength3D(xd(), yd(), zd());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMetric#getSqLength2D()
	 */
	@Override
	public double getSqLength2D() {
		return WB_GeometryOp.getSqLength2D(xd(), yd());
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
	 * @see wblut.geom.WB_CoordinateMetric#heading2D()
	 */
	/**
	 *
	 *
	 * @return
	 * @deprecated Use {@link #getHeading2D()} instead
	 */
	@Deprecated

	public double heading2D() {
		return getHeading2D();
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
		return WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine3D(this, p, q));
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
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(o, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint3D(o, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine3D(o, p, q));
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public boolean isCollinear2D(final WB_Coord p, final WB_Coord q) {
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(this, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(this, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine2D(this, p, q));
	}

	/**
	 *
	 *
	 * @param o
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isCollinear2D(final WB_Coord o, final WB_Coord p, final WB_Coord q) {
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(o, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(o, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine2D(o, p, q));
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallel(final WB_Coord p) {
		return WB_GeometryOp.isParallel(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallel(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isParallel(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallel(final WB_Coord p, final double t) {
		return WB_GeometryOp.isParallel(this, p, t);
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
		return WB_GeometryOp.isParallel(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallelNorm(final WB_Coord p) {
		return WB_GeometryOp.isParallelNorm(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallelNorm(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isParallelNorm(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallelNorm(final WB_Coord p, final double t) {
		return WB_GeometryOp.isParallelNorm(this, p, t);
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
		return WB_GeometryOp.isParallelNorm(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallel2D(final WB_Coord p) {
		return WB_GeometryOp.isParallel2D(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallel2D(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isParallel2D(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallel2D(final WB_Coord p, final double t) {
		return WB_GeometryOp.isParallel2D(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isParallel2D(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isParallel2D(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isParallelNorm2D(final WB_Coord p) {
		return WB_GeometryOp.isParallelNorm(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isParallelNorm2D(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isParallelNorm(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isParallelNorm2D(final WB_Coord p, final double t) {
		return WB_GeometryOp.isParallelNorm(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isParallelNorm2D(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isParallelNorm(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isOrthogonal(final WB_Coord p) {
		return WB_GeometryOp.isOrthogonal(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isOrthogonal(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isOrthogonal(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isOrthogonal(final WB_Coord p, final double t) {
		return WB_GeometryOp.isOrthogonal(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isOrthogonal(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isOrthogonal(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isOrthogonalNorm(final WB_Coord p) {
		return WB_GeometryOp.isOrthogonalNorm(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isOrthogonalNorm(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isOrthogonalNorm(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isOrthogonalNorm(final WB_Coord p, final double t) {
		return WB_GeometryOp.isOrthogonalNorm(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isOrthogonalNorm(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isOrthogonalNorm(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isOrthogonal2D(final WB_Coord p) {
		return WB_GeometryOp.isOrthogonal2D(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isOrthogonal2D(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isOrthogonal2D(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isOrthogonal2D(final WB_Coord p, final double t) {
		return WB_GeometryOp.isOrthogonal2D(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isOrthogonal2D(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isOrthogonal2D(p, q, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public boolean isOrthogonalNorm2D(final WB_Coord p) {
		return WB_GeometryOp.isOrthogonalNorm(this, p);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean isOrthogonalNorm2D(final WB_Coord p, final WB_Coord q) {
		return WB_GeometryOp.isOrthogonalNorm(p, q);
	}

	/**
	 *
	 *
	 * @param p
	 * @param t
	 * @return
	 */
	public boolean isOrthogonalNorm2D(final WB_Coord p, final double t) {
		return WB_GeometryOp.isOrthogonalNorm(this, p, t);
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @param t
	 * @return
	 */
	public static boolean isOrthogonalNorm2D(final WB_Coord p, final WB_Coord q, final double t) {
		return WB_GeometryOp.isOrthogonalNorm(p, q, t);
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
	public WB_Vector mul(final double f) {
		return new WB_Vector(xd() * f, yd() * f, zd() * f);
	}

	/**
	 *
	 *
	 * @param p
	 * @param f
	 * @return
	 */
	public static WB_Vector mul(final WB_Coord p, final double f) {
		return new WB_Vector(p.xd() * f, p.yd() * f, p.zd() * f);
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
	 * @see wblut.geom.WB_CoordinateMath#mulAddMul(double, double,
	 * wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector mulAddMul(final double f, final double g, final WB_Coord p) {
		return new WB_Vector(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
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
	public static WB_Vector mulAddMul(final double f, final WB_Coord p, final double g, final WB_Coord q) {
		return new WB_Vector(f * p.xd() + g * q.xd(), f * p.yd() + g * q.yd(), f * p.zd() + g * q.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mulAddMulInto(double, double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
	 */
	@Override
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final WB_Coord p) {
		result.set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
	 * , double, double, double, double, double, double)
	 */
	/**
	 * @deprecated Use
	 *             {@link #rotateAboutAxis2PSelf(double,double,double,double,double,double,double)}
	 *             instead
	 */
	@Deprecated
	@Override
	public WB_Vector rotateAbout2PointAxisSelf(final double angle, final double p1x, final double p1y, final double p1z,
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
	public WB_Vector rotateAboutAxis2PSelf(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsVector(this);
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
	 * @deprecated Use {@link #rotateAboutAxis2PSelf(double,WB_Coord,WB_Coord)}
	 *             instead
	 */
	@Deprecated
	@Override
	public WB_Vector rotateAbout2PointAxisSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {
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
	public WB_Vector rotateAboutAxis2PSelf(final double angle, final WB_Coord p1, final WB_Coord p2) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		raa.applySelfAsVector(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_MutableCoordinateTransform#rotateAboutAxisSelf(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector rotateAboutAxisSelf(final double angle, final WB_Coord p, final WB_Coord a) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		raa.applySelfAsVector(this);
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
	public WB_Vector rotateAboutAxisSelf(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(ax, ay, az));
		raa.applySelfAsVector(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * double, double, double, double, double, double)
	 */
	/**
	 * @deprecated Use
	 *             {@link #rotateAboutAxis2P(double,double,double,double,double,double,double)}
	 *             instead
	 */
	@Deprecated
	@Override
	public WB_Vector rotateAbout2PointAxis(final double angle, final double p1x, final double p1y, final double p1z,
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
	public WB_Vector rotateAboutAxis2P(final double angle, final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsVector(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	/**
	 * @deprecated Use {@link #rotateAboutAxis2P(double,WB_Coord,WB_Coord)}
	 *             instead
	 */
	@Deprecated
	@Override
	public WB_Vector rotateAbout2PointAxis(final double angle, final WB_Coord p1, final WB_Coord p2) {
		return rotateAboutAxis2P(angle, p1, p2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector rotateAboutAxis2P(final double angle, final WB_Coord p1, final WB_Coord p2) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
		raa.applySelfAsVector(result);
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
	public WB_Vector rotateAboutAxis(final double angle, final double px, final double py, final double pz,
			final double ax, final double ay, final double az) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(ax, ay, az));
		raa.applySelfAsVector(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector rotateAboutAxis(final double angle, final WB_Coord p, final WB_Coord a) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);
		raa.applySelfAsVector(result);
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
	public WB_Vector rotateAboutOrigin(final double angle, final double x, final double y, final double z) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(x, y, z));
		raa.applySelfAsVector(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector rotateAboutOrigin(final double angle, final WB_Coord a) {
		final WB_Vector result = new WB_Vector(this);
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		raa.applySelfAsVector(result);
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
	public WB_Vector rotateAboutOriginSelf(final double angle, final double x, final double y, final double z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, new WB_Vector(x, y, z));
		raa.applySelfAsVector(this);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
	 * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector rotateAboutOriginSelf(final double angle, final WB_Coord a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutOrigin(angle, a);
		raa.applySelfAsVector(this);
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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateTransform#scaleInto(wblut.geom.WB_MutableCoord,
	 * double)
	 */
	@Override
	public void scaleInto(final WB_MutableCoord result, final double f) {
		result.set(xd() * f, yd() * f, zd() * f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateTransform#scaleInto(wblut.geom.WB_MutableCoord,
	 * double, double, double)
	 */
	@Override
	public void scaleInto(final WB_MutableCoord result, final double fx, final double fy, final double fz) {
		result.set(xd() * fx, yd() * fy, zd() * fz);
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
			return _tmp < 0;
		}
		_tmp = WB_Epsilon.compareAbs(yd(), otherXYZ.yd());
		if (_tmp != 0) {
			return _tmp < 0;
		}
		_tmp = WB_Epsilon.compareAbs(zd(), otherXYZ.zd());
		return _tmp < 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#sub(double[])
	 */
	@Override
	public WB_Vector sub(final double... x) {
		if (x.length == 3) {
			return new WB_Vector(this.xd() - x[0], this.yd() - x[1], this.zd() - x[2]);
		} else if (x.length == 2) {
			return new WB_Vector(this.xd() - x[0], this.yd() - x[1], this.zd());

		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#subInto(wblut.geom.WB_MutableCoord,
	 * double[])
	 */
	@Override
	public void subInto(final WB_MutableCoord result, final double... x) {
		if (x.length == 3) {
			result.set(xd() - x[0], yd() - x[1], zd() - x[2]);
		} else if (x.length == 2) {
			result.set(xd() - x[0], yd() - x[1], zd());
		} else {

			throw new IllegalArgumentException("Array should be length 2 or 3.");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#sub(wblut.geom.WB_Coordinate)
	 */
	@Override
	public WB_Vector sub(final WB_Coord p) {
		return new WB_Vector(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
	}

	/**
	 *
	 *
	 * @param p
	 * @param q
	 * @return
	 */
	public static WB_Vector sub(final WB_Coord p, final WB_Coord q) {
		return new WB_Vector(p.xd() - q.xd(), p.yd() - q.yd(), p.zd() - q.zd());
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
	public static WB_M33 tensor(final WB_Coord u, final WB_Coord v) {
		return new WB_M33(WB_GeometryOp.tensor3D(u.xd(), u.yd(), u.zd(), v.xd(), v.yd(), v.zd()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WB_Vector [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
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

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static WB_Vector getOrthoNormal2D(final WB_Coord p) {
		final WB_Vector a = new WB_Vector(-p.yd(), p.xd(), 0);
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

	/**
	 *
	 *
	 * @param p
	 * @return
	 */
	public static WB_Vector getOrthoNormal3D(final WB_Coord p) {
		if (Math.abs(p.zd()) > WB_Epsilon.EPSILON) {
			final WB_Vector a = new WB_Vector(1, 0, -p.xd() / p.zd());
			a.normalizeSelf();
			return a;
		} else {
			return new WB_Vector(0, 0, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_CoordinateMath#mulAddMul(double, double, double[])
	 */
	@Override
	public WB_Vector mulAddMul(final double f, final double g, final double... x) {
		if (x.length == 3) {
			return new WB_Vector(f * this.xd() + g * x[0], f * this.yd() + g * x[1], f * this.zd() + g * x[2]);
		} else if (x.length == 2) {
			return new WB_Vector(f * this.xd() + g * x[0], f * this.yd() + g * x[1], this.zd());
		}
		throw new IllegalArgumentException("Array should be length 2 or 3.");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.geom.WB_CoordinateMath#mulAddMulInto(wblut.geom.WB_MutableCoord,
	 * double, double, double[])
	 */
	@Override
	public void mulAddMulInto(final WB_MutableCoord result, final double f, final double g, final double... x) {
		if (x.length == 3) {
			result.set(f * this.xd() + g * x[0], f * this.yd() + g * x[1], f * this.zd() + g * x[2]);

		} else if (x.length == 2) {
			result.set(f * this.xd() + g * x[0], f * this.yd() + g * x[1], this.zd());

		} else {
			throw new IllegalArgumentException("Array should be length 2 or 3.");
		}
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

	public static WB_Vector interpolate(final WB_Coord v, final WB_Coord w, final double f) {
		return new WB_Vector(WB_GeometryOp.interpolate(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f));
	}

	public static WB_Vector interpolateEaseIn(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Vector(WB_GeometryOp.interpolateEaseIn(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}

	public static WB_Vector interpolateEaseOut(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Vector(WB_GeometryOp.interpolateEaseOut(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}

	public static WB_Vector interpolateEaseInOut(final WB_Coord v, final WB_Coord w, final double f,
			final WB_Ease.Ease ease) {
		return new WB_Vector(
				WB_GeometryOp.interpolateEaseInOut(v.xd(), v.yd(), v.zd(), w.xd(), w.yd(), w.zd(), f, ease));
	}
}
