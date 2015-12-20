/*
 *
 */
package wblut.hemesh;

import java.util.Iterator;
import java.util.List;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;

/**
 *
 */
public class HEM_CapHoles extends HEM_Modifier {

	/**
	 *
	 */
	public HEM_CapHoles() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatus(this, "Starting HEM_CapHoles.", +1);
		tracker.setStatus(this, "Uncapping boundary edges.", 0);
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		HE_Halfedge he;
		final List<HE_Halfedge> remove = new FastTable<HE_Halfedge>();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				mesh.setHalfedge(he.getVertex(),he.getNextInVertex());
				mesh.clearPair(he);
				remove.add(he);
			}
		}
		mesh.removeHalfedges(remove);
		tracker.setStatus(this, "Capping simple planar holes.", 0);
		final List<HE_Face> caps = new FastTable<HE_Face>();
		final List<HE_Halfedge> unpairedEdges = mesh.getUnpairedHalfedges();
		HE_RAS<HE_Halfedge> loopedHalfedges;
		HE_Halfedge start;
		HE_Halfedge hen;
		HE_Face nf;
		HE_RAS<HE_Halfedge> newHalfedges;
		HE_Halfedge phe;
		HE_Halfedge nhe;
		WB_ProgressCounter counter = new WB_ProgressCounter(unpairedEdges.size(), 10);
		tracker.setStatus(this, "Finding loops and closing holes.", counter);
		while (unpairedEdges.size() > 0) {
			loopedHalfedges = new HE_RASTrove<HE_Halfedge>();
			start = unpairedEdges.get(0);
			loopedHalfedges.add(start);
			he = start;
			hen = start;
			boolean stuck = false;
			do {
				for (int i = 0; i < unpairedEdges.size(); i++) {
					hen = unpairedEdges.get(i);
					if (hen.getVertex() == he.getNextInFace().getVertex()) {
						loopedHalfedges.add(hen);
						break;
					}
				}
				if (hen.getVertex() != he.getNextInFace().getVertex()) {
					stuck = true;
				}
				he = hen;
			} while ((hen.getNextInFace().getVertex() != start.getVertex()) && (!stuck));
			unpairedEdges.removeAll(loopedHalfedges);
			nf = new HE_Face();
			mesh.add(nf);
			caps.add(nf);
			newHalfedges = new HE_RASTrove<HE_Halfedge>();
			for (int i = 0; i < loopedHalfedges.size(); i++) {
				phe = loopedHalfedges.get(i);
				nhe = new HE_Halfedge();
				mesh.add(nhe);
				newHalfedges.add(nhe);
				mesh.setVertex(nhe,phe.getNextInFace().getVertex());
				mesh.setPair(nhe,phe);
				mesh.setFace(nhe,nf);
				if (nf.getHalfedge() == null) {
					mesh.setHalfedge(nf,nhe);
				}
			}
			mesh.cycleHalfedgesReverse(newHalfedges.getObjects());
			counter.increment(newHalfedges.size());
		}
		tracker.setStatus(this, "Capped simple, planar holes.", 0);
		tracker.setStatus(this, "Pairing halfedges.", 0);
		class VertexInfo {
			FastTable<HE_Halfedge> out;
			FastTable<HE_Halfedge> in;

			VertexInfo() {
				out = new FastTable<HE_Halfedge>();
				in = new FastTable<HE_Halfedge>();
			}
		}
		final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(1024, 0.5f, -1L);
		final List<HE_Halfedge> unpairedHalfedges = mesh.getUnpairedHalfedges();
		HE_Vertex v;
		VertexInfo vi;
		counter = new WB_ProgressCounter(unpairedHalfedges.size(), 10);
		tracker.setStatus(this, "Classifying unpaired halfedges.", counter);
		for (final HE_Halfedge hed : unpairedHalfedges) {
			v = hed.getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.out.add(hed);
			v = hed.getNextInFace().getVertex();
			vi = vertexLists.get(v.key());
			if (vi == null) {
				vi = new VertexInfo();
				vertexLists.put(v.key(), vi);
			}
			vi.in.add(hed);
			counter.increment();
		}
		HE_Halfedge he2;
		counter = new WB_ProgressCounter(vertexLists.size(), 10);
		tracker.setStatus(this, "Pairing unpaired halfedges per vertex.", counter);
		// System.out.println("HE_Mesh : pairing unpaired halfedges per
		// vertex.");
		final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
		VertexInfo vInfo;
		while (vitr.hasNext()) {
			vitr.advance();
			vInfo = vitr.value();
			for (int i = 0; i < vInfo.out.size(); i++) {
				he = vInfo.out.get(i);
				if (he.getPair() == null) {
					for (int j = 0; j < vInfo.in.size(); j++) {
						he2 = vInfo.in.get(j);
						if ((he2.getPair() == null) && (he.getVertex() == he2.getNextInFace().getVertex())
								&& (he2.getVertex() == he.getNextInFace().getVertex())) {
							mesh.setPair(he,he2);
							break;
						}
					}
				}
			}
			counter.increment();
		}
		tracker.setStatus(this, "Processed unpaired halfedges.", 0);
		tracker.setStatus(this, "Exiting HEM_CapHoles.", -1);
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
