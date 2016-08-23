/**
 *
 */
package wblut.geom;

/**
 * @author FVH
 *
 *
 */
public abstract class WB_CoordViewer {

	public static WB_CoordViewer XY = new WB_CoordViewerXY();
	public static WB_CoordViewer YZ = new WB_CoordViewerYZ();
	public static WB_CoordViewer ZX = new WB_CoordViewerZX();

	/**
	 * Get x as double.
	 *
	 * @return x
	 */
	public abstract double xd(WB_Coord p);

	/**
	 * Get y as double.
	 *
	 * @return y
	 */
	public abstract double yd(WB_Coord p);

	/**
	 * Get z as double.
	 *
	 * @return z
	 */
	public abstract double zd(WB_Coord p);

	/**
	 * Get w as double.
	 *
	 * @return w
	 */
	public abstract double wd(WB_Coord p);

	/**
	 * Get i'th ordinate as double. An implementation of this interface does not
	 * necessarily check the validity of the passed parameter.
	 *
	 * @param i
	 * @return i'th ordinate
	 */
	public abstract double getd(WB_Coord p, int i);

	/**
	 * Get x as float.
	 *
	 * @return x
	 */
	public abstract float xf(WB_Coord p);

	/**
	 * Get y as float.
	 *
	 * @return y
	 */
	public abstract float yf(WB_Coord p);

	/**
	 * Get z as float.
	 *
	 * @return z
	 */
	public abstract float zf(WB_Coord p);

	/**
	 * Get w as float.
	 *
	 * @return w
	 */
	public abstract float wf(WB_Coord p);

	/**
	 * Get i'th ordinate as float. An implementation of this interface does not
	 * necessarily check the validity of the passed parameter.
	 *
	 * @param i
	 * @return i'th ordinate
	 */
	public abstract float getf(WB_Coord p, int i);

	private static class WB_CoordViewerXY extends WB_CoordViewer {

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xd(wblut.geom.WB_Coord)
		 */
		@Override
		public double xd(final WB_Coord p) {
			return p.xd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yd(wblut.geom.WB_Coord)
		 */
		@Override
		public double yd(final WB_Coord p) {
			return p.yd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zd(wblut.geom.WB_Coord)
		 */
		@Override
		public double zd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wd(wblut.geom.WB_Coord)
		 */
		@Override
		public double wd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getd(wblut.geom.WB_Coord, int)
		 */
		@Override
		public double getd(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.xd();
			}
			if (i == 1) {
				return p.yd();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Double.NaN;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xf(wblut.geom.WB_Coord)
		 */
		@Override
		public float xf(final WB_Coord p) {
			return p.xf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yf(wblut.geom.WB_Coord)
		 */
		@Override
		public float yf(final WB_Coord p) {
			return p.yf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zf(wblut.geom.WB_Coord)
		 */
		@Override
		public float zf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wf(wblut.geom.WB_Coord)
		 */
		@Override
		public float wf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getf(wblut.geom.WB_Coord, int)
		 */
		@Override
		public float getf(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.xf();
			}
			if (i == 1) {
				return p.yf();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Float.NaN;
		}
	}

	private static class WB_CoordViewerYZ extends WB_CoordViewer {

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xd(wblut.geom.WB_Coord)
		 */
		@Override
		public double xd(final WB_Coord p) {
			return p.yd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yd(wblut.geom.WB_Coord)
		 */
		@Override
		public double yd(final WB_Coord p) {
			return p.zd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zd(wblut.geom.WB_Coord)
		 */
		@Override
		public double zd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wd(wblut.geom.WB_Coord)
		 */
		@Override
		public double wd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getd(wblut.geom.WB_Coord, int)
		 */
		@Override
		public double getd(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.yd();
			}
			if (i == 1) {
				return p.zd();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Double.NaN;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xf(wblut.geom.WB_Coord)
		 */
		@Override
		public float xf(final WB_Coord p) {
			return p.yf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yf(wblut.geom.WB_Coord)
		 */
		@Override
		public float yf(final WB_Coord p) {
			return p.zf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zf(wblut.geom.WB_Coord)
		 */
		@Override
		public float zf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wf(wblut.geom.WB_Coord)
		 */
		@Override
		public float wf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getf(wblut.geom.WB_Coord, int)
		 */
		@Override
		public float getf(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.yf();
			}
			if (i == 1) {
				return p.zf();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Float.NaN;
		}
	}

	private static class WB_CoordViewerZX extends WB_CoordViewer {

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xd(wblut.geom.WB_Coord)
		 */
		@Override
		public double xd(final WB_Coord p) {
			return p.zd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yd(wblut.geom.WB_Coord)
		 */
		@Override
		public double yd(final WB_Coord p) {
			return p.xd();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zd(wblut.geom.WB_Coord)
		 */
		@Override
		public double zd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wd(wblut.geom.WB_Coord)
		 */
		@Override
		public double wd(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getd(wblut.geom.WB_Coord, int)
		 */
		@Override
		public double getd(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.zd();
			}
			if (i == 1) {
				return p.xd();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Double.NaN;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#xf(wblut.geom.WB_Coord)
		 */
		@Override
		public float xf(final WB_Coord p) {
			return p.zf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#yf(wblut.geom.WB_Coord)
		 */
		@Override
		public float yf(final WB_Coord p) {
			return p.xf();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#zf(wblut.geom.WB_Coord)
		 */
		@Override
		public float zf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#wf(wblut.geom.WB_Coord)
		 */
		@Override
		public float wf(final WB_Coord p) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see wblut.geom.WB_CoordViewer#getf(wblut.geom.WB_Coord, int)
		 */
		@Override
		public float getf(final WB_Coord p, final int i) {
			if (i == 0) {
				return p.zf();
			}
			if (i == 1) {
				return p.xf();
			}
			if (i == 2) {
				return 0;
			}
			if (i == 3) {
				return 0;
			}
			return Float.NaN;
		}
	}
}
