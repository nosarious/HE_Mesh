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
 * Abstract base class for mesh reduction. Implementation should preserve mesh
 * validity.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HES_Simplifier extends HE_Machine {
	/**
	 * Instantiates a new HES_Simplifier.
	 */
	public HES_Simplifier() {
	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (mesh == null || mesh.getNumberOfVertices() == 0) {
			return new HE_Mesh();
		}
		HE_Mesh copy = mesh.get();
		try {
			return applyInt(mesh);
		} catch (Exception e) {
			System.out.println("HES_Simplifier failed. Resetting mesh");
			mesh.setNoCopy(copy);
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
			return applyInt(selection);
		} catch (Exception e) {
			System.out.println("HES_Simplifier failed. Resetting mesh");
			selection.parent.setNoCopy(copy);
			return selection.parent;
		}

	}

	protected abstract HE_Mesh applyInt(final HE_Mesh mesh);

	protected abstract HE_Mesh applyInt(final HE_Selection selection);
}