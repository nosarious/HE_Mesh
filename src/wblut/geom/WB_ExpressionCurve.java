package wblut.geom;

import java.security.InvalidParameterException;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class WB_ExpressionCurve implements WB_Curve {
	Expression expressionX;
	Expression expressionY;
	Expression expressionZ;
	String variable;
	boolean is3D;
	double lowerU, upperU;
	double h;

	public WB_ExpressionCurve(final String equationX, final String equationY, final String equationZ,
			final String var) {
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(equationX);
		expressionBuilder.variable(var);
		variable = var;
		try {
			expressionX = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for X. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationY);
		expressionBuilder.variable(var);
		try {
			expressionY = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Y. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationZ);
		expressionBuilder.variable(var);
		try {
			expressionZ = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Z. Please check equation and parameters.");
		}
		is3D = true;
		lowerU = Double.NEGATIVE_INFINITY;
		upperU = Double.POSITIVE_INFINITY;
		h = 0.00001;
	}

	public WB_ExpressionCurve(final String equationX, final String equationY, final String var) {
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(equationX);
		variable = var;
		expressionBuilder.variable(var);
		try {
			expressionX = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for X. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationY);
		expressionBuilder.variable(var);
		try {
			expressionY = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Y. Please check equation and parameters.");
		}
		expressionZ = null;
		is3D = false;
		lowerU = Double.NEGATIVE_INFINITY;
		upperU = Double.POSITIVE_INFINITY;
		h = 0.00001;
	}

	public WB_ExpressionCurve(final String equationX, final String equationY, final String equationZ, final String var,
			final double minU, final double maxU) {
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(equationX);
		expressionBuilder.variable(var);
		variable = var;
		try {
			expressionX = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for X. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationY);
		expressionBuilder.variable(var);
		try {
			expressionY = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Y. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationZ);
		expressionBuilder.variable(var);
		try {
			expressionZ = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Z. Please check equation and parameters.");
		}
		is3D = true;
		lowerU = minU;
		upperU = maxU;
		h = 0.00001;
	}

	public WB_ExpressionCurve(final String equationX, final String equationY, final String var, final double minU,
			final double maxU) {
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(equationX);
		variable = var;
		expressionBuilder.variable(var);
		try {
			expressionX = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for X. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationY);
		expressionBuilder.variable(var);
		try {
			expressionY = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Y. Please check equation and parameters.");
		}
		expressionZ = null;
		is3D = false;
		lowerU = minU;
		upperU = maxU;
		h = 0.00001;
	}

	private WB_Point evaluate2D(final double value) {
		expressionX.setVariable(variable, value);
		expressionY.setVariable(variable, value);
		final double x, y;
		try {
			x = expressionX.evaluate();
			y = expressionY.evaluate();
		} catch (final ArithmeticException name) {
			return null;
		}
		return new WB_Point(x, y, 0);
	}

	private WB_Point evaluate3D(final double value) {
		expressionX.setVariable(variable, value);
		expressionY.setVariable(variable, value);
		expressionZ.setVariable(variable, value);
		final double x, y, z;
		try {
			x = expressionX.evaluate();
			y = expressionY.evaluate();
			z = expressionZ.evaluate();
		} catch (final ArithmeticException name) {
			return null;
		}
		return new WB_Point(x, y, z);
	}

	@Override
	public WB_Point curvePoint(final double u) {
		if (u < lowerU || u > upperU) {
			return null;
		}
		if (is3D) {
			return evaluate3D(u);
		}
		return evaluate2D(u);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#loweru()
	 */
	@Override
	public double getLowerU() {
		return lowerU;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.WB_Curve#upperu()
	 */
	@Override
	public double getUpperU() {
		return upperU;
	}

	@Override
	public WB_Vector curveDirection(double u) {

		if (u < lowerU || u > upperU) {
			return null;
		}
		if (u > upperU - h)
			u = upperU - h;
		WB_Vector v;
		if (u > upperU - h) {
			v = new WB_Vector(curvePoint(u - h), curvePoint(u));
			v.normalizeSelf();
		} else if (u < lowerU + h) {
			v = new WB_Vector(curvePoint(u), curvePoint(u + h));
			v.normalizeSelf();
		} else {
			v = new WB_Vector(curvePoint(u - h), curvePoint(u + h));
			v.normalizeSelf();
		}
		return v;
	}

	@Override
	public WB_Vector curveDerivative(double u) {

		if (u < lowerU || u > upperU) {
			return null;
		}
		WB_Vector v;
		if (u > upperU - h) {
			v = new WB_Vector(curvePoint(u - h), curvePoint(u));
			v.div(h);
		} else if (u < lowerU + h) {
			v = new WB_Vector(curvePoint(u), curvePoint(u + h));
			v.div(h);
		} else {
			v = new WB_Vector(curvePoint(u - h), curvePoint(u + h));
			v.div(2 * h);
		}
		return v;
	}
}
