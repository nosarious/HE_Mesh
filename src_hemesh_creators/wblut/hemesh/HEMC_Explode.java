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

import java.util.List;

/**
 *
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEMC_Explode extends HEMC_MultiCreator {
	/** Source mesh. */
	private HE_Mesh mesh;

	/**
	 * Instantiates a new HEMC_SplitMesh.
	 *
	 */
	public HEMC_Explode() {
		super();
	}

	public HEMC_Explode(final HE_Mesh mesh) {
		super();
		setMesh(mesh);
	}

	/**
	 * Set source mesh.
	 *
	 * @param mesh
	 *            mesh to split
	 * @return self
	 */
	public HEMC_Explode setMesh(final HE_Mesh mesh) {
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
		mesh.clearVisitedElements();
		HE_Face start = mesh.getFaceWithIndex(0);
		int lastfound = 0;
		HE_Selection submesh;
		do {
			// find next invisited face
			for (int i = lastfound; i < mesh.getNumberOfFaces(); i++) {
				start = mesh.getFaceWithIndex(i);
				lastfound = i;
				if (!start.isVisited()) {// found
					break;
				}
			}
			// reached last face, was already visited
			if (start.isVisited()) {
				break;
			}
			start.setVisited();// visited
			submesh = new HE_Selection(mesh);
			submesh.add(start);
			// find all unvisited faces connected to face
			HE_RAS<HE_Face> facesToProcess = new HE_RAS.HE_RASTrove<HE_Face>();
			HE_RAS<HE_Face> newFacesToProcess;
			facesToProcess.add(start);
			List<HE_Face> neighbors;
			do {

				newFacesToProcess = new HE_RAS.HE_RASTrove<HE_Face>();
				for (final HE_Face f : facesToProcess) {
					neighbors = f.getNeighborFaces();
					for (final HE_Face neighbor : neighbors) {
						if (!neighbor.isVisited()) {
							neighbor.setVisited();// visited
							submesh.add(neighbor);
							newFacesToProcess.add(neighbor);
						}
					}
				}
				facesToProcess = newFacesToProcess;
			} while (facesToProcess.size() > 0);

			result.add(submesh.getAsMesh());
		} while (true);
		return result;
	}
}
