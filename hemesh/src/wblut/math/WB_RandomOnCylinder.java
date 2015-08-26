/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors or points uniformly distributed on the mantle of
 * a cylinder with radius 1 and height 1.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomOnCylinder implements WB_RandomPoint {
    /**
     *
     */
    private final WB_MTRandom randomGen;

    /**
     *
     */
    public WB_RandomOnCylinder() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomOnCylinder(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    @Override
    public WB_RandomOnCylinder setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    @Override
    public WB_Point nextPoint() {
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Point(Math.cos(t), Math.sin(t), randomGen.nextDouble());
    }

    @Override
    public WB_Vector nextVector() {
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Vector(Math.cos(t), Math.sin(t), randomGen.nextDouble());
    }

    @Override
    public void reset() {
	randomGen.reset();
    }
}