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

import java.util.List;


public interface WB_Mesh extends WB_Geometry {

	/**
	 *
	 *
	 * @return
	 */
	public WB_Coord getCenter();


	/**
	 *
	 *
	 * @return
	 */
	public WB_AABB getAABB();


	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public WB_Coord getFaceNormal(final int id);


	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	public WB_Coord getFaceCenter(final int id);


	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public WB_Coord getVertexNormal(final int i);


	/**
	 *
	 *
	 * @return
	 */
	public int getNumberOfFaces();


	/**
	 *
	 *
	 * @return
	 */
	public int getNumberOfVertices();


	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public WB_Coord getVertex(final int i);


	/**
	 *
	 *
	 * @return
	 */
	public List<WB_Coord> getPoints();


	/**
	 *
	 *
	 * @return
	 */
	public int[][] getFacesAsInt();


	/**
	 *
	 *
	 * @return
	 */
	public int[][] getEdgesAsInt();
}
