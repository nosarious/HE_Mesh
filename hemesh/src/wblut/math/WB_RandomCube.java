/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed in the unit cube.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomCube implements WB_RandomPoint {
    /**
     *
     */
    private final WB_MTRandom randomGen;

    /**
     *
     */
    public WB_RandomCube() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomCube(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    /**
     *
     *
     * @param seed
     * @return
     */
    @Override
    public WB_RandomCube setSeed(final long seed) {
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
	return new WB_Point(randomGen.nextDouble(), randomGen.nextDouble(),
		randomGen.nextDouble());
    }

    /**
     *
     *
     * @return
     */
    @Override
    public WB_Vector nextVector() {
	return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(),
		randomGen.nextDouble());
    }

    public void reset() {
	randomGen.reset();
    }
}
