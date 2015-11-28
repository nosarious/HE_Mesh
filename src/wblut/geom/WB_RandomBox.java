/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for vectors uniformly distributed in the halfopen box
 * [-X/2,-Y/2,-Z/2]-(X/2,Y/2,Z/2).
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomBox implements WB_RandomPoint {
	private final WB_MTRandom randomGen;
	private double X, Y, Z;
	private WB_Vector offset;

	public WB_RandomBox() {
		randomGen = new WB_MTRandom();
		X = Y = Z = 1.0;
		offset = new WB_Vector();
	}

	public WB_RandomBox(final long seed) {
		randomGen = new WB_MTRandom(seed);
		X = Y = Z = 1.0;
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomBox setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	public WB_RandomBox setSize(double X, double Y, double Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		return new WB_Point(X * randomGen.nextCenteredDouble(), Y * randomGen.nextCenteredDouble(),
				Z * randomGen.nextCenteredDouble()).addSelf(offset);
	}

	@Override
	public WB_Vector nextVector() {
		return new WB_Vector(X * randomGen.nextCenteredDouble(), Y * randomGen.nextCenteredDouble(),
				Z * randomGen.nextCenteredDouble()).addSelf(offset);
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
