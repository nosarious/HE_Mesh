/*
 *
 */
package wblut.math;

/**
 * The Class WB_ConstantParameter.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 *         A parameter which is constant, i.e. a single unchanging value.
 */
public class WB_ConstantScalarParameter implements WB_ScalarParameter {
    /** The value. */
    double value;

    public WB_ConstantScalarParameter(final double value) {
	this.value = value;
    }

    @Override
    public double evaluate(final double... x) {
	return value;
    }
}