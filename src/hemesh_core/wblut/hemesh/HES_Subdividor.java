/*
 * This file is part of HE_Mesh, a library for creating and manipulating meshes.
 * It is dedicated to the public domain. To the extent possible under law,
 * I , Frederik Vanhoutte, have waived all copyright and related or neighboring
 * rights.
 *
 * This work is published from Belgium. (http://creativecommons.org/publicdomain/zero/1.0/)
 *
 */
package wblut.hemesh;

/**
 * Abstract base class for mesh subdivision. Implementation should preserve mesh
 * validity.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HES_Subdividor extends HE_Machine {
	/**
	 * Instantiates a new HES_Subdividor.
	 */
	public HES_Subdividor() {
	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatus(this, "Starting subvidision.", tracker.STARTLVL);
		if (mesh == null || mesh.getNumberOfVertices() == 0) {
			return new HE_Mesh();
		}
		HE_Mesh copy = mesh.get();
		try {
			HE_Mesh result = applyInt(mesh);
			tracker.setStatus(this, "Mesh subdivided.", tracker.STOPLVL);
			return result;
		} catch (Exception e) {

			mesh.setNoCopy(copy);
			tracker.setStatus(this, "Subdivision failed. Resetting mesh.", tracker.STOPLVL);
			return mesh;
		}

	}

	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if (selection == null) {
			return new HE_Mesh();
		}
		HE_Mesh copy = selection.parent.get();
		try {
			HE_Mesh result = applyInt(selection);
			tracker.setStatus(this, "Mesh subdivided.", tracker.STOPLVL);
			return result;
		} catch (Exception e) {
			System.out.println("HES_Subdividor failed. Resetting mesh");
			selection.parent.setNoCopy(copy);
			return selection.parent;
		}

	}

	protected abstract HE_Mesh applyInt(final HE_Mesh mesh);

	protected abstract HE_Mesh applyInt(final HE_Selection selection);

}
