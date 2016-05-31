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

import java.util.Collections;
import java.util.List;

import javolution.util.FastTable;

public class WB_Danzer3D {

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();

	public static enum Type {
		A, B, C, K
	}

	static class DanzerTile {

		public int p1, p2, p3, p4;
		public Type type;
		public int generation;

		/**
		 *
		 *
		 * @param t
		 * @param g
		 */
		public DanzerTile(final Type t, final int g) {
			type = t;
			p1 = p2 = p3 = p4 = -1;
			generation = g;

		}
	}

	final static double tau = 0.5 + 0.5 * Math.sqrt(5);
	final static double tau2 = tau * tau;
	final static double tau3 = tau2 * tau;
	final static double itau = 1.0 / tau;
	protected double scale;

	protected Type type;

	protected List<WB_Point> points;

	protected List<DanzerTile> tiles;

	/**
	 *
	 *
	 * @param sc
	 * @param t
	 */
	public WB_Danzer3D(final double sc, final Type t) {
		this(sc, t, new WB_Point());
	}

	/**
	 *
	 *
	 * @param sc
	 * @param t
	 * @param offset
	 */
	public WB_Danzer3D(final double sc, final Type t, final WB_Coord offset) {
		points = new FastTable<WB_Point>();
		tiles = new FastTable<DanzerTile>();
		type = t;
		final DanzerTile T = new DanzerTile(type, 0);

		points.add(new WB_Point(offset));
		switch (type) {
		case A:
			points.add(new WB_Point(tau3, 0, tau2).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau2, tau2, tau2).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau2, 1.0, 0).mulSelf(sc).addSelf(offset));
			break;
		case B:
			points.add(new WB_Point(tau3, 0, tau2).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau2, tau2, tau2).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau2, tau, 1.0).mulSelf(sc).addSelf(offset));
			break;
		case C:
			points.add(new WB_Point(-tau, 0, 1.0).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau2, tau2, tau2).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(0, tau2, 1.0).mulSelf(sc).addSelf(offset));
			break;
		case K:
			points.add(new WB_Point(-1, tau, 0).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(tau, tau, tau).mulSelf(sc).addSelf(offset));
			points.add(new WB_Point(-1, itau, tau).mulSelf(0.5 * sc).addSelf(offset));
			break;
		default:
		}
		T.p1 = 0;
		T.p2 = 1;
		T.p3 = 2;
		T.p4 = 3;
		tiles.add(T);
	}

	/**
	 *
	 */
	public void inflate() {
		final List<DanzerTile> newTiles = new FastTable<DanzerTile>();
		for (int i = 0; i < tiles.size(); i++) {
			newTiles.addAll(inflateTileInt(tiles.get(i)));
		}
		tiles = newTiles;
	}

	/**
	 *
	 *
	 * @param rep
	 */
	public void inflate(final int rep) {
		for (int r = 0; r < rep; r++) {
			inflate();
		}
	}

	/**
	 *
	 *
	 * @param T
	 * @return
	 */
	protected List<DanzerTile> inflateTileInt(final DanzerTile T) {
		final List<DanzerTile> newTiles = new FastTable<DanzerTile>();
		points.get(T.p1);
		points.get(T.p2);
		points.get(T.p3);
		points.get(T.p4);
		points.size();
		final Type type = T.type;
		switch (type) {
		case A:

			break;
		case B:

			break;
		case C:
			break;
		case K:
			break;
		default:
		}
		return newTiles;
	}

	/**
	 *
	 *
	 * @param i
	 * @return
	 */
	public DanzerTile tile(final int i) {
		return tiles.get(i);
	}

	/**
	 *
	 *
	 * @return
	 */
	public int oldest() {
		int result = Integer.MAX_VALUE;
		for (final DanzerTile T : tiles) {
			result = Math.min(T.generation, result);
			if (result == 0) {
				return 0;
			}
		}
		return result;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int youngest() {
		int result = -1;
		for (final DanzerTile T : tiles) {
			result = Math.max(T.generation, result);
		}
		return result;
	}

	/**
	 *
	 *
	 * @param i
	 */
	public void inflateTile(final int i) {
		tiles.addAll(inflateTileInt(tiles.get(i)));
		tiles.remove(i);
	}

	/**
	 *
	 */
	public void inflateOldest() {
		inflateOldest(0);
	}

	/**
	 *
	 *
	 * @param r
	 */
	public void inflateOldest(final int r) {
		final int age = oldest();
		Collections.shuffle(tiles);
		for (final DanzerTile T : tiles) {
			if (T.generation <= age + r) {
				tiles.addAll(inflateTileInt(T));
				tiles.remove(T);
				return;
			}
		}
	}

	/**
	 *
	 *
	 * @param i
	 */
	public void removeTile(final int i) {
		tiles.remove(i);
	}

	/**
	 *
	 *
	 * @return
	 */
	public int size() {
		return tiles.size();
	}

	/**
	 *
	 *
	 * @return
	 */
	public int numberOfPoints() {
		return points.size();
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<WB_Point> points() {
		return points;
	}

	/**
	 *
	 *
	 * @return
	 */
	public List<WB_Tetrahedron> getTiles() {
		final List<WB_Tetrahedron> faces = new FastTable<WB_Tetrahedron>();
		clean();
		for (final DanzerTile T : tiles) {
			faces.add(geometryfactory.createTetrahedron(points.get(T.p1), points.get(T.p2), points.get(T.p3),
					points.get(T.p4)));
		}
		return faces;
	}

	/**
	 *
	 *
	 * @return
	 */
	public int[] getTilesAsIndices() {
		clean();
		final int[] indices = new int[tiles.size() * 4];
		int i = 0;
		for (final DanzerTile T : tiles) {
			indices[i++] = T.p1;
			indices[i++] = T.p2;
			indices[i++] = T.p3;
			indices[i++] = T.p4;
		}
		return indices;
	}

	/**
	 *
	 */
	private void clean() {
		final boolean[] used = new boolean[points.size()];
		final int[] newindices = new int[points.size()];
		for (final DanzerTile T : tiles) {
			used[T.p1] = true;
			used[T.p2] = true;
			used[T.p3] = true;
			used[T.p4] = true;
		}
		int ni = 0;
		final List<WB_Point> newpoints = new FastTable<WB_Point>();
		for (int i = 0; i < points.size(); i++) {
			if (used[i]) {
				newindices[i] = ni++;
				newpoints.add(points.get(i));
			}
		}
		for (final DanzerTile T : tiles) {
			T.p1 = newindices[T.p1];
			T.p2 = newindices[T.p2];
			T.p3 = newindices[T.p3];
			T.p4 = newindices[T.p4];
		}
		points = newpoints;
	}
}
