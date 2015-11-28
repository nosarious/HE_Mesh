/*
 *
 */
package wblut.hemesh;

import wblut.math.WB_MTRandom;

/**
 * Planar cut of a mesh. Both parts are returned as separate meshes.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEMC_Panelizer extends HEMC_MultiCreator {
	/** Source mesh. */
	private HE_Mesh mesh;
	/** The thickness. */
	private double thickness;
	/** The range. */
	private double range;
	private WB_MTRandom random;

	/**
	 * Set thickness.
	 *
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEMC_Panelizer setThickness(final double d) {
		thickness = d;
		range = 0;
		random = new WB_MTRandom();
		return this;
	}

	/**
	 * Sets the thickness.
	 *
	 * @param dmin
	 *            the dmin
	 * @param dmax
	 *            the dmax
	 * @return the hEM c_ panelizer
	 */
	public HEMC_Panelizer setThickness(final double dmin, final double dmax) {
		thickness = dmin;
		range = dmax - dmin;
		return this;
	}

	public HEMC_Panelizer setThickness(final double dmin, final double dmax, final long seed) {
		thickness = dmin;
		range = dmax - dmin;
		random.setSeed(seed);
		return this;
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh
	 *            mesh to panelize
	 * @return self
	 */
	public HEMC_Panelizer setMesh(final HE_Mesh mesh) {
		this.mesh = mesh;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_MeshCollection create() {
		final HE_MeshCollection result = new HE_MeshCollection();
		if (mesh == null) {
			_numberOfMeshes = 0;
			return result;
		}

		int id = 0;
		final HEC_Polygon pc = new HEC_Polygon().setThickness(thickness);
		for (final HE_Face f : mesh.getFacesAsList()) {
			pc.setThickness(thickness + range > 0 ? thickness + random.nextDouble() * range : 0);
			pc.setPolygon(f.toPolygon());
			result.add(new HE_Mesh(pc));
			id++;
		}
		_numberOfMeshes = id;
		return result;
	}
}
