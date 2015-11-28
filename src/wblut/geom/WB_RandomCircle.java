/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for vectors or points uniformly distributed on a circle with
 * radius r.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomCircle implements WB_RandomPoint {
	/**
	 *
	 */
	private final WB_MTRandom randomGen;
	private double radius;
	private WB_Vector offset;

	/**
	 *
	 */
	public WB_RandomCircle() {
		randomGen = new WB_MTRandom();
		radius = 1.0;
		offset = new WB_Vector();
	}

	public WB_RandomCircle(final long seed) {
		randomGen = new WB_MTRandom(seed);
		radius = 1.0;
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomCircle setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	public WB_RandomCircle setRadius(double r) {
		this.radius = r;
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Point(radius * Math.cos(t), radius * Math.sin(t), 0).addSelf(offset);
	}

	@Override
	public WB_Vector nextVector() {
		final double t = 2 * Math.PI * randomGen.nextDouble();
		return new WB_Vector(radius * Math.cos(t), radius * Math.sin(t), 0).addSelf(offset);
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