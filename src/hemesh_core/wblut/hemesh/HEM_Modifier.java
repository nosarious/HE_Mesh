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
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HEM_Modifier extends HE_Machine {
	/**
	 * Instantiates a new HEM_Modifier.
	 */
	public HEM_Modifier() {
	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatus(this, "Starting modifier.", tracker.STARTLVL);
		if (mesh == null || mesh.getNumberOfVertices() == 0) {
			return new HE_Mesh();
		}
		HE_Mesh copy = mesh.get();
		try {
			tracker.setStatus(this, "Mesh modified.", tracker.STOPLVL);
			return applyInt(mesh);
		} catch (Exception e) {
			mesh.setNoCopy(copy);
			tracker.setStatus(this, "Modifier failed. Resetting mesh.", tracker.STOPLVL);
			return mesh;
		}

	}

	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		tracker.setStatus(this, "Starting modifier.", tracker.STARTLVL);
		if (selection == null) {
			return new HE_Mesh();
		}
		HE_Mesh copy = selection.parent.get();
		try {
			tracker.setStatus(this, "Mesh modified.", tracker.STOPLVL);
			return applyInt(selection);
		} catch (Exception e) {
			selection.parent.setNoCopy(copy);
			tracker.setStatus(this, "Modifier failed. Resetting mesh.", tracker.STOPLVL);
			return selection.parent;
		}

	}

	protected abstract HE_Mesh applyInt(final HE_Mesh mesh);

	protected abstract HE_Mesh applyInt(final HE_Selection selection);

}
