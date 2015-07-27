/*
 *
 */
package wblut.geom;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Collection;

/**
 * Storing lots and lots of WB_Coordinates can fill the Java Heap Memory.
 * WB_CoordinateSequence tries to avoid this by storing the coordinates in a
 * single TDoubleArrayList. A WB_SequenceVector or WB_SequencePoint adds a view
 * in this data structure that acts identical to a WB_Vector or WB_Point.
 *
 */
public class WB_MutableCoordinateSequence extends WB_CoordinateSequence {
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     *
     */
    public WB_MutableCoordinateSequence() {
	super();
    }

    /**
     *
     *
     * @param tuples
     */
    public WB_MutableCoordinateSequence(
	    final Collection<? extends WB_Coordinate> tuples) {
	super(tuples);
    }

    /**
     *
     *
     * @param tuples
     */
    public WB_MutableCoordinateSequence(final WB_Coordinate[] tuples) {
	super(tuples);
    }

    /**
     *
     *
     * @param tuples
     */
    public WB_MutableCoordinateSequence(final WB_CoordinateSequence tuples) {
	super(tuples);
    }

    /**
     *
     *
     * @param ordinates
     */
    public WB_MutableCoordinateSequence(final double[] ordinates) {
	super(ordinates);
    }

    /**
     *
     *
     * @param tuples
     */
    public WB_MutableCoordinateSequence(final double[][] tuples) {
	super(tuples);
    }

    public void add(final double x, final double y) {
	ordinates.add(x);
	ordinates.add(y);
	ordinates.add(0);
	ordinates.add(0);
    }

    public void add(final double x, final double y, final double z) {
	ordinates.add(x);
	ordinates.add(y);
	ordinates.add(z);
	ordinates.add(0);
    }

    public void add(final double x, final double y, final double z,
	    final double w) {
	ordinates.add(x);
	ordinates.add(y);
	ordinates.add(z);
	ordinates.add(w);
    }

    public void add(final WB_Coordinate v) {
	ordinates.add(v.xd());
	ordinates.add(v.yd());
	ordinates.add(v.zd());
	ordinates.add(v.wd());
    }

    public void remove(final int i) {
	if (i < n) {
	    ordinates.set(i * 4, ordinates.get(ordinates.size() - 4));
	    ordinates.set(i * 4 + 1, ordinates.get(ordinates.size() - 3));
	    ordinates.set(i * 4 + 2, ordinates.get(ordinates.size() - 2));
	    ordinates.set(i * 4 + 3, ordinates.get(ordinates.size() - 1));
	    ordinates.remove(4, ordinates.size() - 4);
	    revision++;
	}
    }

    /**
     *
     *
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence getCopy() {
	final WB_MutableCoordinateSequence subseq = new WB_MutableCoordinateSequence();
	final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
		Double.NaN);
	System.arraycopy(ordinates, 0, subordinates, 0, 4 * n);
	subseq.ordinates = subordinates;
	subseq.n = n;
	return subseq;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsNormal(final WB_Transform T) {
	final WB_MutableCoordinateSequence result = getCopy();
	return result.applyAsNormalSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsPoint(final WB_Transform T) {
	final WB_MutableCoordinateSequence result = getCopy();
	return result.applyAsPointSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsVector(final WB_Transform T) {
	final WB_MutableCoordinateSequence result = getCopy();
	return result.applyAsVectorSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsNormalSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsNormal(x1, y1, z1, getVector(j));
	}
	return this;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsPointSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsPoint(x1, y1, z1, getPoint(j));
	}
	return this;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    @Override
    public WB_MutableCoordinateSequence applyAsVectorSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsVector(x1, y1, z1, getVector(j));
	}
	return this;
    }

    @Override
    public void clear() {
	ordinates = new TDoubleArrayList(100, Double.NaN);
	revision++;
    }
}