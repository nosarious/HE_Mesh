/*
 *
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 * Random generator for vectors uniformly distributed on the unit disk.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomDisk implements WB_RandomPoint {
    /** The random gen. */
    private final WB_MTRandom randomGen;

    /**
     * Instantiates a new w b_ random disc.
     */
    public WB_RandomDisk() {
	randomGen = new WB_MTRandom();
    }

    public WB_RandomDisk(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    /**
     * Set random seed.
     *
     * @param seed
     *            seed
     * @return self
     */
    @Override
    public WB_RandomDisk setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * Next point.
     *
     * @return next random WB_Point on unit disk
     */
    @Override
    public WB_Point nextPoint() {
	final double r = Math.sqrt(randomGen.nextDouble());
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Point(r * Math.cos(t), r * Math.sin(t), 0);
    }

    /**
     * Next vector.
     *
     * @return next random WB_Vector on unit disk
     */
    @Override
    public WB_Vector nextVector() {
	final double r = Math.sqrt(randomGen.nextDouble());
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Vector(r * Math.cos(t), r * Math.sin(t), 0);
    }

    public void reset() {
	randomGen.reset();
    }
}