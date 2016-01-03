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

public class WB_TransformMap implements WB_Map {

	private WB_Transform T;
	private WB_Transform invT;

	/**
	 * 
	 *
	 * @param transform 
	 */
	public WB_TransformMap(WB_Transform transform) {
		T = transform.get();
		invT = transform.get();
		invT.inverse();
	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#mapPoint3D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void mapPoint3D(WB_Coord p, WB_MutableCoord result) {
		T.applyAsPoint(p, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#mapPoint3D(double, double, double, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void mapPoint3D(double x, double y, double z, WB_MutableCoord result) {
		T.applyAsPoint(x, y, z, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#unmapPoint3D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapPoint3D(WB_Coord p, WB_MutableCoord result) {
		invT.applyAsPoint(p, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#unmapPoint3D(double, double, double, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapPoint3D(double u, double v, double w, WB_MutableCoord result) {
		invT.applyAsPoint(u, v, w, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#mapVector3D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void mapVector3D(WB_Coord p, WB_MutableCoord result) {
		T.applyAsVector(p, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#mapVector3D(double, double, double, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void mapVector3D(double x, double y, double z, WB_MutableCoord result) {
		T.applyAsVector(x, y, z, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#unmapVector3D(wblut.geom.WB_Coord, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapVector3D(WB_Coord p, WB_MutableCoord result) {
		invT.applyAsVector(p, result);

	}

	/* (non-Javadoc)
	 * @see wblut.geom.WB_Map#unmapVector3D(double, double, double, wblut.geom.WB_MutableCoord)
	 */
	@Override
	public void unmapVector3D(double u, double v, double w, WB_MutableCoord result) {
		invT.applyAsVector(u, v, w, result);

	}

}
