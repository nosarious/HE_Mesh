/*
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_Mesh extends WB_Geometry {
    /**
     *
     *
     * @return
     */
    public WB_Coordinate getCenter();

    /**
     *
     *
     * @return
     */
    public WB_AABB getAABB();

    /**
     *
     *
     * @param id
     * @return
     */
    public WB_Coordinate getFaceNormal(final int id);

    /**
     *
     *
     * @param id
     * @return
     */
    public WB_Coordinate getFaceCenter(final int id);

    /**
     *
     *
     * @param i
     * @return
     */
    public WB_Coordinate getVertexNormal(final int i);

    /**
     *
     *
     * @return
     */
    public int getNumberOfFaces();

    /**
     *
     *
     * @return
     */
    public int getNumberOfVertices();

    /**
     *
     *
     * @param i
     * @return
     */
    public WB_Coordinate getVertex(final int i);

    /**
     *
     *
     * @return
     */
    public WB_CoordinateSequence getPoints();

    /**
     *
     *
     * @return
     */
    public int[][] getFacesAsInt();

    /**
     *
     *
     * @return
     */
    public int[][] getEdgesAsInt();
}
