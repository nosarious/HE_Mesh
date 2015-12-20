/*
 *
 */
package wblut.hemesh;

import java.util.Iterator;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import wblut.core.WB_ProgressCounter;

/**
 * Axis Aligned Box.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Copy extends HEC_Creator {
	/**
	 *
	 */
	HE_MeshStructure source;

	/**
	 *
	 */
	public HEC_Copy() {
		super();
		override = true;
	}

	/**
	 *
	 *
	 * @param source
	 */
	public HEC_Copy(final HE_MeshStructure source) {
		super();
		setMesh(source);
		override = true;
	}

	/**
	 *
	 *
	 * @param source
	 * @return
	 */
	public HEC_Copy setMesh(final HE_MeshStructure source) {
		this.source = source;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		tracker.setStatus(this, "Starting HEC_Copy.", +1);
		final HE_Mesh result = new HE_Mesh();
		if (source == null) {
			tracker.setStatus(this, "No source mesh. Exiting HEC_Copy.", -1);
			return result;
		}
		result.copyProperties(source);
		if (source instanceof HE_Mesh) {
			final HE_Mesh mesh = (HE_Mesh) source;
			final TLongLongMap vertexCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			final TLongLongMap faceCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			final TLongLongMap halfedgeCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			HE_Vertex rv;
			HE_Vertex v;
			WB_ProgressCounter counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
			tracker.setStatus(this, "Creating vertices.", counter);
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				rv = new HE_Vertex(v);
				result.add(rv);
				rv.copyProperties(v);
				vertexCorrelation.put(v.key(), rv.key());
				counter.increment();
			}


			HE_Face rf;
			HE_Face f;
			counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
			tracker.setStatus(this, "Creating faces.", counter);
			final Iterator<HE_Face> fItr = mesh.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				rf = new HE_Face();
				result.add(rf);
				rf.copyProperties(f);
				faceCorrelation.put(f.key(), rf.key());
				counter.increment();
			}

			HE_Halfedge rhe;
			HE_Halfedge he;
			counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
			tracker.setStatus(this, "Creating halfedges.", counter);
			final Iterator<HE_Halfedge> heItr = mesh.heItr();
			while (heItr.hasNext()) {
				he = heItr.next();
				rhe = new HE_Halfedge();
				result.add(rhe);
				rhe.copyProperties(he);
				halfedgeCorrelation.put(he.key(), rhe.key());
				counter.increment();
			}


			counter = new WB_ProgressCounter(mesh.getNumberOfVertices(), 10);
			tracker.setStatus(this, "Setting vertex properties.", counter);
			HE_Vertex sv;
			HE_Vertex tv;
			final Iterator<HE_Vertex> svItr = mesh.vItr();
			final Iterator<HE_Vertex> tvItr = result.vItr();
			Long key;
			while (svItr.hasNext()) {
				sv = svItr.next();
				tv = tvItr.next();
				tv.set(sv);
				if (sv.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sv.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tv,result.getHalfedgeWithKey(key));
					}
				}
				counter.increment();
			}


			counter = new WB_ProgressCounter(mesh.getNumberOfFaces(), 10);
			tracker.setStatus(this, "Setting face properties.", counter);
			HE_Face sf;
			HE_Face tf;
			final Iterator<HE_Face> sfItr = mesh.fItr();
			final Iterator<HE_Face> tfItr = result.fItr();
			while (sfItr.hasNext()) {
				sf = sfItr.next();
				tf = tfItr.next();
				if (sf.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sf.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tf,result.getHalfedgeWithKey(key));
					}
				}
				counter.increment();
			}


			counter = new WB_ProgressCounter(mesh.getNumberOfHalfedges(), 10);
			tracker.setStatus(this, "Setting halfedge properties.", counter);
			HE_Halfedge she;
			HE_Halfedge the;
			final Iterator<HE_Halfedge> sheItr = mesh.heItr();
			while (sheItr.hasNext()) {
				she = sheItr.next();
				key = halfedgeCorrelation.get(she.key());
				the = result.getHalfedgeWithKey(key);
				if (she.getPair() != null) {
					key = halfedgeCorrelation.get(she.getPair().key());
					if (key >= 0) {
						result.setPair(the,result.getHalfedgeWithKey(key));
					}
				}
				if (she.getNextInFace() != null) {
					key = halfedgeCorrelation.get(she.getNextInFace().key());
					if (key >= 0) {
						result.setNext(the,result.getHalfedgeWithKey(key));
					}
				}
				if (she.getVertex() != null) {
					key = vertexCorrelation.get(she.getVertex().key());
					if (key >= 0) {
						result.setVertex(the,result.getVertexWithKey(key));
					}
				}
				if (she.getFace() != null) {
					key = faceCorrelation.get(she.getFace().key());
					if (key >= 0) {
						result.setFace(the,result.getFaceWithKey(key));
					}
				}
				counter.increment();
			}

			tracker.setStatus(this, "Exiting HEC_Copy.", -1);
		} else if (source instanceof HE_Selection) {
			final HE_Selection sel = ((HE_Selection) source).get();
			sel.completeFromFaces();
			final TLongLongMap vertexCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			final TLongLongMap faceCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			final TLongLongMap halfedgeCorrelation = new TLongLongHashMap(10, 0.5f, -1L, -1L);
			HE_Vertex rv;
			HE_Vertex v;
			WB_ProgressCounter counter = new WB_ProgressCounter(sel.getNumberOfVertices(), 10);
			tracker.setStatus(this, "Creating vertices.", counter);
			final Iterator<HE_Vertex> vItr = sel.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				rv = new HE_Vertex(v);
				result.add(rv);
				rv.copyProperties(v);
				vertexCorrelation.put(v.key(), rv.key());
				counter.increment();
			}
			HE_Face rf;
			HE_Face f;
			counter = new WB_ProgressCounter(sel.getNumberOfFaces(), 10);
			tracker.setStatus(this, "Creating faces.", counter);
			final Iterator<HE_Face> fItr = sel.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				rf = new HE_Face();
				result.add(rf);
				rf.copyProperties(f);
				faceCorrelation.put(f.key(), rf.key());
				counter.increment();
			}
			HE_Halfedge rhe;
			HE_Halfedge he;
			counter = new WB_ProgressCounter(sel.getNumberOfHalfedges(), 10);
			tracker.setStatus(this, "Creating halfedges.", counter);
			final Iterator<HE_Halfedge> heItr = sel.heItr();
			while (heItr.hasNext()) {
				he = heItr.next();
				rhe = new HE_Halfedge();
				result.add(rhe);
				rhe.copyProperties(he);
				halfedgeCorrelation.put(he.key(), rhe.key());
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfVertices(), 10);
			tracker.setStatus(this, "Setting vertex properties.", counter);
			HE_Vertex sv;
			HE_Vertex tv;
			final Iterator<HE_Vertex> svItr = sel.vItr();
			final Iterator<HE_Vertex> tvItr = result.vItr();
			Long key;
			while (svItr.hasNext()) {
				sv = svItr.next();
				tv = tvItr.next();
				tv.set(sv);
				if (sv.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sv.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tv,result.getHalfedgeWithKey(key));
					}
				}
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfFaces(), 10);
			tracker.setStatus(this, "Setting face properties.", counter);
			HE_Face sf;
			HE_Face tf;
			final Iterator<HE_Face> sfItr = sel.fItr();
			final Iterator<HE_Face> tfItr = result.fItr();
			while (sfItr.hasNext()) {
				sf = sfItr.next();
				tf = tfItr.next();
				if (sf.getHalfedge() != null) {
					key = halfedgeCorrelation.get(sf.getHalfedge().key());
					if (key >= 0) {
						result.setHalfedge(tf,result.getHalfedgeWithKey(key));
					}
				}
				counter.increment();
			}
			counter = new WB_ProgressCounter(sel.getNumberOfHalfedges(), 10);
			tracker.setStatus(this, "Setting halfedge properties.", counter);
			HE_Halfedge she;
			HE_Halfedge the;
			final Iterator<HE_Halfedge> sheItr = sel.heItr();

			while (sheItr.hasNext()) {

				she = sheItr.next();
				key = halfedgeCorrelation.get(she.key());
				the = result.getHalfedgeWithKey(key);
				if (she.getPair() != null) {
					key = halfedgeCorrelation.get(she.getPair().key());
					if (key >= 0) {
						result.setPair(the,result.getHalfedgeWithKey(key));
					}
				}
				if (she.getNextInFace() != null) {
					key = halfedgeCorrelation.get(she.getNextInFace().key());
					if (key >= 0) {
						result.setNext(the,result.getHalfedgeWithKey(key));
					}
				}
				if (she.getVertex() != null) {
					key = vertexCorrelation.get(she.getVertex().key());
					if (key >= 0) {
						result.setVertex(the,result.getVertexWithKey(key));
					}
				}
				if (she.getFace() != null) {
					key = faceCorrelation.get(she.getFace().key());
					if (key >= 0) {
						result.setFace(the,result.getFaceWithKey(key));
					}
				}
				counter.increment();
			}
			result.capHalfedges();
			tracker.setStatus(this, "Exiting HEC_Copy.", -1);
		}
		return result;
	}
}
