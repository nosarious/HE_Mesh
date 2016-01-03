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

import wblut.math.WB_Epsilon;

/**
 * @author Frederik Vanhoutte, W:Blut
 */
public class WB_PointHomogeneous extends WB_Point4D {
    /**
     *
     */
    private boolean pointAtInfinity;

    /**
     *
     */
    public WB_PointHomogeneous() {
	super();
	pointAtInfinity = false;
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public WB_PointHomogeneous(final double x, final double y, final double z,
	    final double w) {
	super(w * x, w * y, w * z, w);
	pointAtInfinity = false;
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @param w
     * @param atInfinity
     */
    public WB_PointHomogeneous(final double x, final double y, final double z,
	    final double w, final boolean atInfinity) {
	super(x, y, z, w);
	if (atInfinity) {
	    setW(0);
	}
	pointAtInfinity = atInfinity;
    }

    /**
     *
     *
     * @param v
     */
    public WB_PointHomogeneous(final WB_PointHomogeneous v) {
	super(v);
	pointAtInfinity = v.pointAtInfinity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Point4D#get()
     */
    @Override
    public WB_PointHomogeneous get() {
	return new WB_PointHomogeneous(this);
    }

    /**
     *
     *
     * @param v
     * @param w
     */
    public WB_PointHomogeneous(final WB_Coord v, final double w) {
	setX(w * v.xd());
	setY(w * v.yd());
	setZ(w * v.zd());
	setW(w);
	pointAtInfinity = false;
    }

    /**
     *
     *
     * @param v
     * @param w
     * @param atInfinity
     */
    public WB_PointHomogeneous(final WB_Coord v, final double w,
	    final boolean atInfinity) {
	if (atInfinity) {
	    setX(v.xd());
	    setY(v.yd());
	    setZ(v.zd());
	    setW(0);
	} else {
	    setX(w * v.xd());
	    setY(w * v.yd());
	    setZ(w * v.zd());
	    setW(w);
	}
	pointAtInfinity = atInfinity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Point4D#set(double, double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z,
	    final double w) {
	setX(x * w);
	setY(y * w);
	setZ(z * w);
	setW(w);
	pointAtInfinity = false;
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @param w
     * @param atInfinity
     */
    public void set(final double x, final double y, final double z,
	    final double w, final boolean atInfinity) {
	if (atInfinity) {
	    setX(x);
	    setY(y);
	    setZ(z);
	    setW(0);
	} else {
	    setX(w * x);
	    setY(w * y);
	    setZ(w * z);
	    setW(w);
	}
	pointAtInfinity = atInfinity;
    }

    /**
     *
     *
     * @param v
     * @param w
     * @param atInfinity
     */
    public void set(final WB_Coord v, final double w,
	    final boolean atInfinity) {
	if (atInfinity) {
	    setX(v.xd());
	    setY(v.yd());
	    setZ(v.zd());
	    setW(0);
	} else {
	    setX(w * v.xd());
	    setY(w * v.yd());
	    setZ(w * v.zd());
	    setW(w);
	}
	pointAtInfinity = atInfinity;
    }

    /**
     *
     *
     * @param p
     */
    public void set(final WB_PointHomogeneous p) {
	setX(p.xd());
	setY(p.yd());
	setZ(p.zd());
	setW(p.wd());
	pointAtInfinity = p.pointAtInfinity;
    }

    /**
     *
     *
     * @return
     */
    public WB_Point project() {
	if (pointAtInfinity) {
	    return new WB_Point(xd(), yd(), zd());
	} else if (WB_Epsilon.isZero(wd())) {
	    return new WB_Point(0, 0, 0);
	}
	final double iw = 1.0 / wd();
	return new WB_Point(xd() * iw, yd() * iw, zd() * iw);
    }

    /**
     *
     *
     * @param w
     */
    public void setWeight(final double w) {
	final WB_Point p = project();
	set(p, w, pointAtInfinity);
    }

    /**
     *
     *
     * @param p0
     * @param p1
     * @param t
     * @return
     */
    public static WB_PointHomogeneous interpolate(final WB_PointHomogeneous p0,
	    final WB_PointHomogeneous p1, final double t) {
	return new WB_PointHomogeneous(p0.xd() + (t * (p1.xd() - p0.xd())),
		p0.yd() + (t * (p1.yd() - p0.yd())), p0.zd()
			+ (t * (p1.zd() - p0.zd())), p0.wd()
			+ (t * (p1.wd() - p0.wd())));
    }

    /**
     *
     *
     * @return
     */
    public boolean isInfinite() {
	return pointAtInfinity;
    }
}
