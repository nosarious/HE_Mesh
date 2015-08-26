/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public interface WB_Curve {
    
    /**
     * 
     *
     * @param u 
     * @return 
     */
    public WB_Point curvePoint(double u);

    /**
     * 
     *
     * @return 
     */
    public double getLowerU();

    /**
     * 
     *
     * @return 
     */
    public double getUpperU();
}
