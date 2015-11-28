/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for vectors uniformly distributed on a sphere with radius r.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomOnSphere implements WB_RandomPoint {
	private final WB_MTRandom randomGen;
	private double radius;
	private WB_Vector offset;

	public WB_RandomOnSphere() {
		randomGen = new WB_MTRandom();
		radius = 1.0;
		offset = new WB_Vector();
	}

	public WB_RandomOnSphere(final long seed) {
		randomGen = new WB_MTRandom(seed);
		radius = 1.0;
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomOnSphere setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	public WB_RandomOnSphere setRadius(double r) {
		radius = r;
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		final double eps = randomGen.nextDouble();
		final double z = 1.0 - (2.0 * eps);
		final double r = radius * Math.sqrt(1.0 - (z * z));
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Point(r * Math.cos(t), r * Math.sin(t), radius * z);
	}

	@Override
	public WB_Vector nextVector() {
		final double eps = randomGen.nextDouble();
		final double z = 1.0 - (2.0 * eps);
		final double r = radius * Math.sqrt(1.0 - (z * z));
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Vector(r * Math.cos(t), r * Math.sin(t), radius * z);
	}

	@Override
	public void reset() {
		randomGen.reset();
	}

	@Override
	public WB_RandomPoint setOffset(WB_Coord offset) {
		this.offset.set(offset);
		return this;
	}

	@Override
	public WB_RandomPoint setOffset(double x, double y) {
		this.offset.set(x, y, 0);
		return this;
	}

	@Override
	public WB_RandomPoint setOffset(double x, double y, double z) {
		this.offset.set(x, y, z);
		return this;
	}
}
