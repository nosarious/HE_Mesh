/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed inside a sphere with
 * radius 1.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomInSphere implements WB_RandomPoint {
    private final WB_MTRandom randomGen;

    public WB_RandomInSphere() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomInSphere(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    @Override
    public WB_RandomInSphere setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    @Override
    public WB_Point nextPoint() {
	final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
	final double azimuth = 2 * Math.PI * randomGen.nextDouble();
	final double r = Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
	return new WB_Point(r * Math.cos(elevation) * Math.cos(azimuth), r
		* Math.cos(elevation) * Math.sin(azimuth), r
		* Math.sin(elevation));
    }

    @Override
    public WB_Vector nextVector() {
	final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
	final double azimuth = 2 * Math.PI * randomGen.nextDouble();
	final double r = Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
	return new WB_Vector(r * Math.cos(elevation) * Math.cos(azimuth), r
		* Math.cos(elevation) * Math.sin(azimuth), r
		* Math.sin(elevation));
    }

    @Override
    public void reset() {
	randomGen.reset();
    }
}
