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

import wblut.core.WB_ProgressCounter;

/**
 * Flip face normals.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_FlipFaces extends HEM_Modifier {

	/**
	 * Instantiates a new HEM_FlipFaces.
	 */
	public HEM_FlipFaces() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Flipping faces.", +1);
		WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfEdges(), 10);
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Reversing edges.", counter);
		HE_Halfedge he1;
		HE_Halfedge he2;
		HE_Vertex tmp;
		HE_Halfedge[] prevHe;
		HE_TextureCoordinate[] nextHeUVW;
		HE_Halfedge he;
		mesh.clearVisitedElements();
		prevHe = new HE_Halfedge[mesh.getNumberOfHalfedges()];
		nextHeUVW = new HE_TextureCoordinate[mesh.getNumberOfHalfedges()];
		int i = 0;
		HE_HalfedgeIterator heItr = mesh.heItr();
		counter = new WB_ProgressCounter(2 * mesh.getNumberOfHalfedges(), 10);
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Reordering halfedges.", counter);
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			nextHeUVW[i] = he.getNextInFace().hasHalfedgeUVW() ? he.getNextInFace().getHalfedgeUVW() : null;
			i++;
			counter.increment();
		}
		i = 0;
		heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			mesh.setNext(he, prevHe[i]);
			if (nextHeUVW[i] == null) {
				he.clearUVW();
			} else {
				he.setUVW(nextHeUVW[i]);
			}
			i++;
			counter.increment();
		}
		counter = new WB_ProgressCounter(2 * mesh.getNumberOfEdges(), 10);
		tracker.setStatusStr("HET_MeshOp", "Flipping edges.", counter);

		final HE_EdgeIterator eItr = mesh.eItr();
		while (eItr.hasNext()) {
			he1 = eItr.next();
			he2 = he1.getPair();
			tmp = he1.getVertex();
			mesh.setVertex(he1, he2.getVertex());

			mesh.setVertex(he2, tmp);

			mesh.setHalfedge(he1.getVertex(), he1);

			mesh.setHalfedge(he2.getVertex(), he2);

			counter.increment();
		}

		tracker.setStatusStr("HET_MeshOp", "Faces flipped.", -1);
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Flipping faces.", +1);
		WB_ProgressCounter counter = new WB_ProgressCounter(selection.getNumberOfEdges(), 10);
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Reversing edges.", counter);
		HE_Halfedge he1;
		HE_Halfedge he2;
		HE_Vertex tmp;
		HE_Halfedge[] prevHe;
		HE_TextureCoordinate[] nextHeUVW;
		HE_Halfedge he;
		selection.parent.clearVisitedElements();
		prevHe = new HE_Halfedge[selection.getNumberOfHalfedges()];
		nextHeUVW = new HE_TextureCoordinate[selection.getNumberOfHalfedges()];
		int i = 0;
		HE_HalfedgeIterator heItr = selection.heItr();
		counter = new WB_ProgressCounter(2 * selection.getNumberOfHalfedges(), 10);
		tracker.setStatusStr("HEM_FlipFacesMeshOp", "Reordering halfedges.", counter);
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			nextHeUVW[i] = he.getNextInFace().hasHalfedgeUVW() ? he.getNextInFace().getHalfedgeUVW() : null;
			i++;
			counter.increment();
		}
		i = 0;
		heItr = selection.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			selection.setNext(he, prevHe[i]);
			if (nextHeUVW[i] == null) {
				he.clearUVW();
			} else {
				he.setUVW(nextHeUVW[i]);
			}
			i++;
			counter.increment();
		}
		counter = new WB_ProgressCounter(2 * selection.getNumberOfEdges(), 10);
		tracker.setStatusStr("HET_MeshOp", "Flipping edges.", counter);

		final HE_EdgeIterator eItr = selection.eItr();
		while (eItr.hasNext()) {
			he1 = eItr.next();
			he2 = he1.getPair();
			tmp = he1.getVertex();
			selection.parent.setVertex(he1, he2.getVertex());

			selection.parent.setVertex(he2, tmp);

			selection.parent.setHalfedge(he1.getVertex(), he1);

			selection.parent.setHalfedge(he2.getVertex(), he2);

			counter.increment();
		}

		tracker.setStatusStr("HET_MeshOp", "Faces flipped.", -1);
		return selection.parent;
	}
}
