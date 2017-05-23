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

import java.util.Collection;

import javolution.util.FastMap;

/**
 *
 */
public class WB_Triangulation2D {

	/**
	 *
	 */
	private int[] triangles;

	/**
	 *
	 */
	private int[] edges;

	/**
	 *
	 */
	public WB_Triangulation2D() {
	}

	/**
	 *
	 *
	 * @param T
	 * @param E
	 */
	public WB_Triangulation2D(final int[] T, final int[] E) {
		triangles = T;
		edges = E;
	}

	public WB_Triangulation2D(final int[] T) {
		triangles = T;
		if (triangles.length == 0) {
			edges = new int[0];
		} else {
			extractEdges(triangles);
		}
	}

	/**
	 *
	 *
	 * @return
	 */
	public int[] getTriangles() {
		return triangles;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int[] getEdges() {
		return edges;
	}

	private void extractEdges(final int[] tris) {
		final int f = tris.length;
		final FastMap<Long, int[]> map = new FastMap<Long, int[]>();
		for (int i = 0; i < tris.length; i += 3) {
			final int v0 = tris[i];
			final int v1 = tris[i + 1];
			final int v2 = tris[i + 2];
			long index = getIndex(v0, v1, f);
			map.put(index, new int[] { v0, v1 });
			index = getIndex(v1, v2, f);
			map.put(index, new int[] { v1, v2 });
			index = getIndex(v2, v0, f);
			map.put(index, new int[] { v2, v0 });
		}
		edges = new int[2 * map.size()];
		final Collection<int[]> values = map.values();
		int i = 0;
		for (final int[] value : values) {
			edges[2 * i] = value[0];
			edges[2 * i + 1] = value[1];
			i++;
		}

	}

	private long getIndex(final int i, final int j, final int f) {
		return i > j ? j + i * f : i + j * f;
	}
}