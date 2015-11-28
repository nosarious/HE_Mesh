/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for vectors uniformly distributed inside a sphere with
 * radius r.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomInSphere implements WB_RandomPoint {
	private final WB_MTRandom randomGen;
	private double radius;
	private WB_Vector offset;

	public WB_RandomInSphere() {
		randomGen = new WB_MTRandom();
		radius = 1.0;
		offset = new WB_Vector();
	}

	public WB_RandomInSphere(final long seed) {
		randomGen = new WB_MTRandom(seed);
		radius = 1.0;
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomInSphere setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	public WB_RandomInSphere setRadius(double r) {
		radius = r;
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
		final double azimuth = 2 * Math.PI * randomGen.nextDouble();
		final double r = radius * Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
		return new WB_Point(r * Math.cos(elevation) * Math.cos(azimuth), r * Math.cos(elevation) * Math.sin(azimuth),
				r * Math.sin(elevation));
	}

	@Override
	public WB_Vector nextVector() {
		final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
		final double azimuth = 2 * Math.PI * randomGen.nextDouble();
		final double r = radius * Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
		return new WB_Vector(r * Math.cos(elevation) * Math.cos(azimuth), r * Math.cos(elevation) * Math.sin(azimuth),
				r * Math.sin(elevation));
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
