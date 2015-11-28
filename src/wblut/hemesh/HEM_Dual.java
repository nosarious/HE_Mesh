/*
 * 
 */
package wblut.hemesh;

public class HEM_Dual extends HEM_Modifier {

	/**
	 * 
	 */
	private double limitAngle;

	/**
	 *
	 */
	public HEM_Dual() {

	}

	/**
	 * 
	 * 
	 * /* (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		HE_Mesh result = new HE_Mesh(new HEC_Dual(mesh));
		mesh.set(result);
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		return apply(selection.parent);
	}
}
