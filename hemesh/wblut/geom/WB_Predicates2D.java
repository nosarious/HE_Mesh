package wblut.geom;

import wblut.math.WB_DoubleDouble;

public class WB_Predicates2D {

	private static double orientErrorBound = -1;

	private static double incircleErrorBound = -1;

	private static double findMachEpsilon() {
		double epsilon, check, lastcheck;
		epsilon = 1.0;
		check = 1.0;
		do {
			lastcheck = check;
			epsilon *= 0.5;
			check = 1.0 + epsilon;
		} while ((check != 1.0) && (check != lastcheck));
		return epsilon;
	}

	private static void init() {
		final double epsilon = findMachEpsilon();
		orientErrorBound = (3.0 + 16.0 * epsilon) * epsilon;
		incircleErrorBound = (10.0 + 96.0 * epsilon) * epsilon;
	}

	// >0 if pa,pb,pc ccw
	// <0 if pa,pb,pc cw
	// =0 if colinear

	public static double orient2d(final WB_Coordinate pa,
			final WB_Coordinate pb, final WB_Coordinate pc) {
		if (orientErrorBound == -1) {
			init();
		}
		double detleft, detright, det;
		double detsum, errbound;

		detleft = (pa.xd() - pc.xd()) * (pb.yd() - pc.yd());
		detright = (pa.yd() - pc.yd()) * (pb.xd() - pc.xd());
		det = detleft - detright;

		if (detleft > 0.0) {
			if (detright <= 0.0) {
				return Math.signum(det);
			} else {
				detsum = detleft + detright;
			}
		} else if (detleft < 0.0) {
			if (detright >= 0.0) {
				return Math.signum(det);
			} else {
				detsum = -detleft - detright;
			}
		} else {
			return Math.signum(det);
		}

		errbound = orientErrorBound * detsum;
		if ((det >= errbound) || (-det >= errbound)) {
			return Math.signum(det);
		}
		return orientDD2d(pa, pb, pc);
	}

	public static double orientDD2d(final WB_Coordinate pa,
			final WB_Coordinate pb, final WB_Coordinate pc) {
		WB_DoubleDouble ax, ay, bx, by, cx, cy;
		WB_DoubleDouble acx, bcx, acy, bcy;
		WB_DoubleDouble detleft, detright, det;
		det = WB_DoubleDouble.valueOf(0.0);
		ax = WB_DoubleDouble.valueOf(pa.xd());
		ay = WB_DoubleDouble.valueOf(pa.yd());
		bx = WB_DoubleDouble.valueOf(pb.xd());
		by = WB_DoubleDouble.valueOf(pb.yd());
		cx = WB_DoubleDouble.valueOf(pc.xd());
		cy = WB_DoubleDouble.valueOf(pc.yd());
		acx = ax.add(cx.negate());
		bcx = bx.add(cx.negate());
		acy = ay.add(cy.negate());
		bcy = by.add(cy.negate());
		detleft = acx.multiply(bcy);
		detright = acy.multiply(bcx);
		det = detleft.add(detright.negate());

		return det.compareTo(WB_DoubleDouble.ZERO);
	}

	// >0 if pd inside circle through pa,pb,pc (if ccw)
	// <0 if pd outside circle through pa,pb,pc (if ccw)
	// =0 if on circle

	public static double incircle2d(final WB_Coordinate pa,
			final WB_Coordinate pb, final WB_Coordinate pc,
			final WB_Coordinate pd) {

		if (incircleErrorBound == -1) {
			init();
		}
		double adx, ady, bdx, bdy, cdx, cdy;
		double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
		double alift, blift, clift;
		double det;
		double permanent, errbound;

		adx = pa.xd() - pd.xd();
		bdx = pb.xd() - pd.xd();
		cdx = pc.xd() - pd.xd();
		ady = pa.yd() - pd.yd();
		bdy = pb.yd() - pd.yd();
		cdy = pc.yd() - pd.yd();

		bdxcdy = bdx * cdy;
		cdxbdy = cdx * bdy;
		alift = adx * adx + ady * ady;

		cdxady = cdx * ady;
		adxcdy = adx * cdy;
		blift = bdx * bdx + bdy * bdy;

		adxbdy = adx * bdy;
		bdxady = bdx * ady;
		clift = cdx * cdx + cdy * cdy;

		det = alift * (bdxcdy - cdxbdy) + blift * (cdxady - adxcdy) + clift
				* (adxbdy - bdxady);

		if (bdxcdy < 0) {
			bdxcdy = -bdxcdy;
		}
		if (cdxbdy < 0) {
			cdxbdy = -cdxbdy;
		}
		if (cdxady < 0) {
			cdxady = -cdxady;
		}
		if (adxcdy < 0) {
			adxcdy = -adxcdy;
		}
		if (adxbdy < 0) {
			adxbdy = -adxbdy;
		}
		if (bdxady < 0) {
			bdxady = -bdxady;
		}

		permanent = (bdxcdy + cdxbdy) * alift + (cdxady + adxcdy) * blift
				+ (adxbdy + bdxady) * clift;
		errbound = incircleErrorBound * permanent;
		if ((det > errbound) || (-det > errbound)) {
			return Math.signum(det);
		}
		return incircleDD2d(pa, pb, pc, pd);
	}

	public static double incircleDD2d(final WB_Coordinate pa,
			final WB_Coordinate pb, final WB_Coordinate pc,
			final WB_Coordinate pd) {
		WB_DoubleDouble ax, ay, bx, by, cx, cy, dx, dy;
		WB_DoubleDouble adx, ady, bdx, bdy, cdx, cdy;
		WB_DoubleDouble bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
		WB_DoubleDouble alift, blift, clift;
		WB_DoubleDouble det;

		det = WB_DoubleDouble.valueOf(0.0);

		ax = WB_DoubleDouble.valueOf(pa.xd());
		ay = WB_DoubleDouble.valueOf(pa.yd());
		bx = WB_DoubleDouble.valueOf(pb.xd());
		by = WB_DoubleDouble.valueOf(pb.yd());
		cx = WB_DoubleDouble.valueOf(pc.xd());
		cy = WB_DoubleDouble.valueOf(pc.yd());
		dx = WB_DoubleDouble.valueOf(pd.xd());
		dy = WB_DoubleDouble.valueOf(pd.yd());

		dx = dx.negate();
		dy = dy.negate();

		adx = ax.add(dx);
		bdx = bx.add(dx);
		cdx = cx.add(dx);
		ady = ay.add(dy);
		bdy = by.add(dy);
		cdy = cy.add(dy);

		bdxcdy = bdx.multiply(cdy);
		cdxbdy = cdx.multiply(bdy);

		cdxady = cdx.multiply(ady);
		adxcdy = adx.multiply(cdy);

		adxbdy = adx.multiply(bdy);
		bdxady = bdx.multiply(ady);

		adx = adx.multiply(adx);
		ady = ady.multiply(ady);
		alift = adx.add(ady);

		bdx = bdx.multiply(bdx);
		bdy = bdy.multiply(bdy);
		blift = bdx.add(bdy);

		cdx = cdx.multiply(cdx);
		cdy = cdy.multiply(cdy);
		clift = cdx.add(cdy);

		alift = alift.multiply(bdxcdy.add(cdxbdy.negate()));
		blift = blift.multiply(cdxady.add(adxcdy.negate()));
		clift = clift.multiply(adxbdy.add(bdxady.negate()));

		det = alift.add(blift).add(clift);

		return det.compareTo(WB_DoubleDouble.ZERO);
	}

	// >0 if pd inside circle through pa,pb,pc (cw or ccw)
	// <0 if pd outside circle through pa,pb,pc (cw or ccw)
	// =0 if on circle

	public static double incircle2dOrient(final WB_Coordinate pa,
			final WB_Coordinate pb, final WB_Coordinate pc,
			final WB_Coordinate pd) {

		if (orient2d(pa, pb, pc) > 0) {
			return incircle2d(pa, pb, pc, pd);
		}
		final double ic = incircle2d(pa, pb, pc, pd);
		if (ic > 0) {
			return -1;
		}
		if (ic < 0) {
			return 1;
		}
		return 0;

	}

	public static boolean getIntersection2dProper(final WB_Coordinate a,
			final WB_Coordinate b, final WB_Coordinate c, final WB_Coordinate d) {
		if (WB_Predicates2D.orient2d(a, b, c) == 0
				|| WB_Predicates2D.orient2d(a, b, d) == 0
				|| WB_Predicates2D.orient2d(c, d, a) == 0
				|| WB_Predicates2D.orient2d(c, d, b) == 0) {
			return false;
		} else if (WB_Predicates2D.orient2d(a, b, c)
				* WB_Predicates2D.orient2d(a, b, d) > 0
				|| WB_Predicates2D.orient2d(c, d, a)
						* WB_Predicates2D.orient2d(c, d, b) > 0) {
			return false;
		} else {
			return true;
		}
	}

}
