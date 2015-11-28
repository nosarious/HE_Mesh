/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed in the halfopen cube
 * [0,0,0]-(1,1,1).
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomCube implements WB_RandomPoint {
    private final WB_MTRandom randomGen;

    public WB_RandomCube() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomCube(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    @Override
    public WB_RandomCube setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    @Override
    public WB_Point nextPoint() {
	return new WB_Point(randomGen.nextDouble(), randomGen.nextDouble(),
		randomGen.nextDouble());
    }

    @Override
    public WB_Vector nextVector() {
	return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(),
		randomGen.nextDouble());
    }

    @Override
    public void reset() {
	randomGen.reset();
    }
}
