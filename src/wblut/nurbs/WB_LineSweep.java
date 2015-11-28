/*
 *
 */
package wblut.nurbs;

import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

/**
 *
 */
public class WB_LineSweep {
	/**
	 *
	 *
	 * @param C
	 * @param v
	 * @param f
	 * @return
	 */
	public static WB_BSplineSurface getLineSweep(final WB_BSpline C, final WB_Coord v, final double f) {
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final WB_Point[][] points = new WB_Point[C.n() + 1][2];
		for (int i = 0; i <= C.n(); i++) {
			points[i][0] = new WB_Point(C.points()[i]);
			points[i][1] = points[i][0].addMul(f, v);
		}
		return new WB_BSplineSurface(points, C.knot(), VKnot);
	}

	/**
	 *
	 *
	 * @param C
	 * @param v
	 * @param f
	 * @return
	 */
	public static WB_RBSplineSurface getLineSweep(final WB_RBSpline C, final WB_Coord v, final double f) {
		final WB_NurbsKnot VKnot = new WB_NurbsKnot(2, 1);
		final WB_Point[][] points = new WB_Point[C.n() + 1][2];
		final double[][] weights = new double[C.n() + 1][2];
		for (int i = 0; i <= C.n(); i++) {
			points[i][0] = new WB_Point(C.points()[i]);
			points[i][1] = WB_Point.addMul(C.points()[i], f, v);
			weights[i][0] = C.weights()[i];
			weights[i][1] = weights[i][0];
		}
		return new WB_RBSplineSurface(points, C.knot(), VKnot, weights);
	}
}
