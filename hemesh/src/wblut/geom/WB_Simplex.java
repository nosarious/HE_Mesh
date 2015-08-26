/*
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_Simplex extends WB_Geometry {
    /**
     *
     *
     * @param i
     * @return
     */
    public WB_Coordinate getPoint(int i);

    /**
     *
     *
     * @return
     */
    public WB_Coordinate getCenter();
}
