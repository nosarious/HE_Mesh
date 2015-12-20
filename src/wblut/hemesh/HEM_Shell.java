/*
 *
 */
package wblut.hemesh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Turns a solid into a rudimentary shelled structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Shell extends HEM_Modifier {
	/**
	 *
	 */
	private double d;

	/**
	 *
	 */
	public HEM_Shell() {
		super();
		d = 0;
	}

	/**
	 *
	 *
	 * @param d
	 * @return
	 */
	public HEM_Shell setThickness(final double d) {
		this.d = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (d == 0) {
			return mesh;
		}
		final HE_Mesh innerMesh = mesh.get();
		final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(-d);
		innerMesh.modify(expm);
		final HashMap<Long, Long> heCorrelation = new HashMap<Long, Long>();
		final Iterator<HE_Halfedge> heItr1 = mesh.heItr();
		final Iterator<HE_Halfedge> heItr2 = innerMesh.heItr();
		HE_Halfedge he1;
		HE_Halfedge he2;
		while (heItr1.hasNext()) {
			he1 = heItr1.next();
			he2 = heItr2.next();
			if (he1.getFace() == null) {
				heCorrelation.put(he1.key(), he2.key());
			}
		}
		innerMesh.flipAllFaces();
		mesh.addVertices(innerMesh.getVerticesAsArray());
		mesh.addFaces(innerMesh.getFacesAsArray());
		mesh.addHalfedges(innerMesh.getHalfedgesAsArray());
		final Iterator<Map.Entry<Long, Long>> it = heCorrelation.entrySet()
				.iterator();
		HE_Halfedge heio, heoi;
		HE_Face fNew;
		while (it.hasNext()) {
			final Map.Entry<Long, Long> pairs = it.next();
			he1 = mesh.getHalfedgeWithKey(pairs.getKey());
			he2 = mesh.getHalfedgeWithKey(pairs.getValue());
			heio = new HE_Halfedge();
			heoi = new HE_Halfedge();
			mesh.add(heio);
			mesh.add(heoi);
			mesh.setVertex(heio,he1.getPair().getVertex());
			mesh.setVertex(heoi,he2.getPair().getVertex());
			mesh.setNext(he1,heio);
			mesh.setNext(heio,he2);
			mesh.setNext(he2,heoi);
			mesh.setNext(heoi,he1);
			fNew = new HE_Face();
			mesh.add(fNew);
			mesh.setHalfedge(fNew,he1);
			mesh.setFace(he1,fNew);
			mesh.setFace(he2,fNew);
			mesh.setFace(heio,fNew);
			mesh.setFace(heoi,fNew);
		}
		mesh.pairHalfedges();
		if (d < 0) {
			mesh.flipAllFaces();
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return apply(selection.parent);
	}
}
