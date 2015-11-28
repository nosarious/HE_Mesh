/*
 *
 */
package wblut.math;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

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

    public WB_RandomLine(final WB_Coordinate start, final WB_Coordinate end) {
	this.start = new WB_Point(start);
	this.end = new WB_Point(end);
	randomGen = new WB_MTRandom();
    }

    public WB_RandomLine(final long seed) {
	randomGen = new WB_MTRandom(seed);
    }

    @Override
    public WB_RandomLine setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    @Override
    public WB_Point nextPoint() {
	final double d = randomGen.nextDouble();
	return new WB_Point(start.xd() + d * (end.xd() - start.xd()),
		start.yd() + d * (end.yd() - start.yd()), start.zd() + d
			* (end.zd() - start.zd()));
    }

    @Override
    public WB_Vector nextVector() {
	final double d = randomGen.nextDouble();
	return new WB_Vector(start.xd() + d * (end.xd() - start.xd()),
		start.yd() + d * (end.yd() - start.yd()), start.zd() + d
			* (end.zd() - start.zd()));
    }

    @Override
    public void reset() {
	randomGen.reset();
    }
}
