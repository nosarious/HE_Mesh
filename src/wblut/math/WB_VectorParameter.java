/*
 *
 */
package wblut.math;

import wblut.geom.WB_Coordinate;

/**
 *
 *
 *
 */
public interface WB_VectorParameter {
    /**
     *
     *
     * @param x
     * @return
     */
    public WB_Coordinate evaluate(double... x);
}
