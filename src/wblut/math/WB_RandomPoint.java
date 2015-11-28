package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Interface for random vector/point generators
 *
 * @author frederikvanhoutte
 *
 */
public interface WB_RandomPoint {
    /**
     * Set the seed for the RNG
     *
     * @param seed
     * @return this
     */
    public WB_RandomPoint setSeed(final long seed);

    /**
     * Get the next random point
     *
     * @return
     */
    public WB_Point nextPoint();

    /**
     * Get the next random vector
     *
     * @return
     */
    public WB_Vector nextVector();

    /**
     * Reset the RNG
     */
    public void reset();
}
