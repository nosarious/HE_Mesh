/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed on a sphere with radius 1.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomOnSphere implements WB_RandomPoint {
    private final WB_MTRandom randomGen;

    public WB_RandomOnSphere() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomOnSphere(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    @Override
    public WB_RandomOnSphere setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    @Override
    public WB_Point nextPoint() {
	final double eps = randomGen.nextDouble();
	final double z = 1.0 - (2.0 * eps);
	final double r = Math.sqrt(1.0 - (z * z));
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Point(r * Math.cos(t), r * Math.sin(t), z);
    }

    @Override
    public WB_Vector nextVector() {
	final double eps = randomGen.nextDouble();
	final double z = 1.0 - (2.0 * eps);
	final double r = Math.sqrt(1.0 - (z * z));
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Vector(r * Math.cos(t), r * Math.sin(t), z);
    }

    @Override
    public void reset() {
	randomGen.reset();
    }
}
