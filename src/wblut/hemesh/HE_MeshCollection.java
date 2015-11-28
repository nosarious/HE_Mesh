package wblut.hemesh;

import java.util.List;

import javolution.util.FastTable;

/** OBject consisting of several meshes */
public class HE_MeshCollection {
	final List<HE_Mesh> meshes;

	public HE_MeshCollection() {
		meshes = new FastTable<HE_Mesh>();
	}

	public HE_MeshIterator mItr() {
		return new HE_MeshIterator(this);
	}

	public HE_Mesh getMesh(final int i) {
		return meshes.get(i);
	}

	public HE_Mesh set(final int i, final HE_Mesh mesh) {
		return meshes.set(i, mesh);
	}

	public boolean add(final HE_Mesh mesh) {
		return meshes.add(mesh);
	}

	public boolean remove(final HE_Mesh mesh) {
		return meshes.remove(mesh);
	}

	public int getNumberOfMeshes() {
		return meshes.size();
	}

	public int size() {
		return meshes.size();
	}
}
