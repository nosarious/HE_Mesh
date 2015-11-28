/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for vectors uniformly distributed on a halfopen linesegment
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomLine implements WB_RandomPoint {
	private final WB_MTRandom randomGen;
	WB_Point start;
	WB_Point end;
	private WB_Vector offset;

	public WB_RandomLine(final WB_Coord start, final WB_Coord end) {
		this.start = new WB_Point(start);
		this.end = new WB_Point(end);
		randomGen = new WB_MTRandom();
		offset = new WB_Vector();
	}

	public WB_RandomLine(final WB_Coord start, final WB_Coord end, final long seed) {
		this.start = new WB_Point(start);
		this.end = new WB_Point(end);
		randomGen = new WB_MTRandom(seed);
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomLine setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		final double d = randomGen.nextDouble();
		return new WB_Point(start.xd() + d * (end.xd() - start.xd()), start.yd() + d * (end.yd() - start.yd()),
				start.zd() + d * (end.zd() - start.zd()));
	}

	@Override
	public WB_Vector nextVector() {
		final double d = randomGen.nextDouble();
		return new WB_Vector(start.xd() + d * (end.xd() - start.xd()), start.yd() + d * (end.yd() - start.yd()),
				start.zd() + d * (end.zd() - start.zd()));
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
