/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed in the unit square.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomSquare implements WB_RandomPoint {
    /**
     *
     */
    private final WB_MTRandom randomGen;

    /**
     *
     */
    public WB_RandomSquare() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomSquare(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    /**
     *
     *
     * @param seed
     * @return
     */
    @Override
    public WB_RandomSquare setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public WB_Point nextPoint() {
	return new WB_Point(randomGen.nextDouble(), randomGen.nextDouble(), 0);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public WB_Vector nextVector() {
	return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(), 0);
    }

    public void reset() {
	randomGen.reset();
    }
}
