/*
 *
 */
package wblut.math;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_SimpleVector;

/**
 * The Class WB_ConstantParameter.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 *         A parameter which is constant, i.e. a single unchanging value.
 */
public class WB_ConstantVectorParameter implements WB_VectorParameter {
    /** The value. */
    WB_Coordinate value;

    public WB_ConstantVectorParameter(final WB_Coordinate value) {
	this.value = new WB_SimpleVector(value);
    }

    @Override
    public WB_Coordinate evaluate(final double... x) {
	return value;
    }
}
