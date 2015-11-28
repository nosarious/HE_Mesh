
/*
 *
 */
package wblut.geom;

import wblut.math.WB_MTRandom;

/**
 *
 * Random generator for points on a halfopen curve. The distribution is only
 * uniform in the curve parameter.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_RandomCurve implements WB_RandomPoint {
	private final WB_MTRandom randomGen;
	private WB_Curve curve;
	private double start, end;
	private WB_Vector offset;

	public WB_RandomCurve(WB_Curve curve, final double start, final double end) {
		this.start = start;
		this.end = end;
		this.curve = curve;
		randomGen = new WB_MTRandom();
		offset = new WB_Vector();
	}

	public WB_RandomCurve(WB_Curve curve, final double start, final double end, final long seed) {
		this.start = start;
		this.end = end;
		this.curve = curve;
		randomGen = new WB_MTRandom(seed);
		offset = new WB_Vector();
	}

	@Override
	public WB_RandomCurve setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	@Override
	public WB_Point nextPoint() {
		final double d = start + (end - start) * randomGen.nextDouble();
		return curve.curvePoint(d).addSelf(offset);
	}

	@Override
	public WB_Vector nextVector() {
		final double d = start + (end - start) * randomGen.nextDouble();
		return curve.curvePoint(d).addSelf(offset);
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
