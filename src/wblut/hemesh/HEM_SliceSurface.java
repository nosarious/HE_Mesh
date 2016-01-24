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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.core.WB_ProgressCounter;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Classification;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.math.WB_Epsilon;

/**
 * Planar cut of a mesh. No faces are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_SliceSurface extends HEM_Modifier {
	static final int ONEDGE=0, ONVERTEX=1,BACK=2, FRONT=3;
	/** Cut plane. */
	private WB_Plane P;
	/** Stores cut faces. */
	public HE_Selection cut;
	/**
	 *
	 */
	public HE_Selection front;
	/**
	 *
	 */
	public HE_Selection back;
	/** Stores new edges. */
	public HE_Selection cutEdges;
	/**
	 *
	 */
	private List<HE_Path> paths;

	private WB_GeometryFactory gf=WB_GeometryFactory.instance();

	/**
	 * Instantiates a new HEM_SliceSurface.
	 */
	public HEM_SliceSurface() {
		super();
	}

	/**
	 * Set cut plane.
	 *
	 * @param P
	 *            cut plane
	 * @return self
	 */
	public HEM_SliceSurface setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	/**
	 *
	 *
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param nx
	 * @param ny
	 * @param nz
	 * @return
	 */
	public HEM_SliceSurface setPlane(final double ox, final double oy, final double oz, final double nx,
			final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 *
	 */
	private double offset;

	/**
	 * Set offset.
	 *
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEM_SliceSurface setOffset(final double d) {
		offset = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		tracker.setStatus(this, "Starting HEM_SliceSurface.", +1);
		cut = new HE_Selection(mesh);
		front = new HE_Selection(mesh);
		back = new HE_Selection(mesh);
		cutEdges = new HE_Selection(mesh);
		mesh.resetEdgeTemporaryLabels();
		mesh.resetVertexTemporaryLabels();
		paths = new FastTable<HE_Path>();
		// no plane defined
		if (P == null) {
			tracker.setStatus(this, "No cutplane defined. Exiting HEM_SliceSurface.", -1);
			return mesh;
		}
		// empty mesh
		if (mesh.getNumberOfVertices() == 0) {
			tracker.setStatus(this, "Empty mesh. Exiting HEM_SliceSurface.", -1);
			return mesh;
		}
		// check if plane intersects mesh
		final WB_Plane lP = new WB_Plane(P.getNormal(), P.d() + offset);
		if (!WB_GeometryOp.checkIntersection3D(mesh.getAABB(), lP)) {
			tracker.setStatus(this,
					"Plane doesn't intersect bounding box. Exiting HEM_SliceSurface.", -1);
			return mesh;
		}
		tracker.setStatus(this, "Creating bounding box tree.", 0);
		final WB_AABBTree tree = new WB_AABBTree(mesh, Math.max(64, (int)Math.sqrt(mesh.getNumberOfFaces())));
		final HE_Selection faces = new HE_Selection(mesh);
		tracker.setStatus(this, "Retrieving intersection candidates.", 0);
		faces.addFaces(HE_GeometryOp.getPotentialIntersectedFaces(tree, lP));
		faces.collectVertices();
		faces.collectEdgesByFace();
		WB_Classification tmp;
		final HashMap<Long, WB_Classification> vertexClass = new HashMap<Long, WB_Classification>();
		WB_ProgressCounter counter = new WB_ProgressCounter(faces.getNumberOfVertices(), 10);

		tracker.setStatus(this, "Classifying vertices.", counter);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = faces.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = WB_GeometryOp.classifyPointToPlane3D(v, lP);
			if(tmp==WB_Classification.ON) {
				v.setTemporaryLabel(ONVERTEX);
			} else if(tmp==WB_Classification.BACK) {
				v.setTemporaryLabel(BACK);
			} else if(tmp==WB_Classification.FRONT) {
				v.setTemporaryLabel(FRONT);
			}
			vertexClass.put(v.key(), tmp);
			counter.increment();
		}
		counter = new WB_ProgressCounter(faces.getNumberOfEdges(), 10);

		tracker.setStatus(this, "Classifying edges.", counter);
		new ArrayList<HE_Vertex>();
		final HE_Selection split = new HE_Selection(mesh);
		final FastMap<Long, Double> edgeInt = new FastMap<Long, Double>();
		final Iterator<HE_Halfedge> eItr = faces.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.ON) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					cutEdges.add(e);
					e.setTemporaryLabel(1);
					e.getPair().setTemporaryLabel(1);
				} else {
					edgeInt.put(e.key(), 0.0);
				}
			} else if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.BACK) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.FRONT) {
					edgeInt.put(e.key(), HE_GeometryOp.getIntersection(e, lP));
				}
			} else {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.BACK) {
					edgeInt.put(e.key(), HE_GeometryOp.getIntersection(e, lP));
				}
			}
			counter.increment();
		}
		counter = new WB_ProgressCounter(edgeInt.size(), 10);

		tracker.setStatus(this, "Indexing edge intersection.", counter);
		for (final Map.Entry<Long, Double> en : edgeInt.entrySet()) {
			final HE_Halfedge ce = mesh.getHalfedgeWithKey(en.getKey());
			final double u = en.getValue();
			if (ce.getFace() != null) {
				split.add(ce.getFace());
			}
			if (ce.getPair().getFace() != null) {
				split.add(ce.getPair().getFace());
			}
			HE_Vertex vi=mesh.splitEdge(ce, u).vItr().next();
			vi.setTemporaryLabel(ONEDGE);
			split.add(vi);

			counter.increment();
		}
		counter = new WB_ProgressCounter(split.getNumberOfFaces(), 10);

		tracker.setStatus(this, "Splitting faces.", counter);
		HE_Face f;
		final Iterator<HE_Face> fItr = split.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			splitFaceInIntersections(f,mesh);
			counter.increment();
		}
		buildPaths(cutEdges);
		tracker.setStatus(this, "Exiting HEM_SliceSurface.", -1);
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		tracker.setStatus(this, "Starting HEM_SliceSurface.", +1);
		selection.parent.resetEdgeTemporaryLabels();
		cut = new HE_Selection(selection.parent);
		front = new HE_Selection(selection.parent);
		back = new HE_Selection(selection.parent);
		cutEdges = new HE_Selection(selection.parent);
		paths = new FastTable<HE_Path>();
		// no plane defined
		if (P == null) {
			tracker.setStatus(this, "No cutplane defined. Exiting HEM_SliceSurface.", -1);
			return selection.parent;
		}
		// empty mesh
		if (selection.parent.getNumberOfVertices() == 0) {
			tracker.setStatus(this, "Empty vertex selection. Exiting HEM_SliceSurface.", -1);
			return selection.parent;
		}
		final WB_Plane lP = new WB_Plane(P.getNormal(), P.d() + offset);
		tracker.setStatus(this, "Creating bounding box tree.", 0);
		final WB_AABBTree tree = new WB_AABBTree(selection.parent, 64);
		final HE_Selection faces = new HE_Selection(selection.parent);
		tracker.setStatus(this, "Retrieving intersection candidates.", 0);
		faces.addFaces(HE_GeometryOp.getPotentialIntersectedFaces(tree, lP));
		final HE_Selection lsel = selection.get();
		lsel.intersect(faces);
		lsel.collectEdgesByFace();
		lsel.collectVertices();
		// empty mesh
		if (lsel.getNumberOfVertices() == 0) {
			tracker.setStatus(this,
					"Plane doesn't intersect bounding box tree. Exiting HEM_SliceSurface.", -1);
			return lsel.parent;
		}
		// check if plane intersects mesh
		boolean positiveVertexExists = false;
		boolean negativeVertexExists = false;
		WB_Classification tmp;
		final FastMap<Long, WB_Classification> vertexClass = new FastMap<Long, WB_Classification>();
		HE_Vertex v;
		WB_ProgressCounter counter = new WB_ProgressCounter(lsel.getNumberOfVertices(), 10);

		tracker.setStatus(this, "Classifying vertices.", counter);
		final Iterator<HE_Vertex> vItr = lsel.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = WB_GeometryOp.classifyPointToPlane3D(v, lP);
			vertexClass.put(v.key(), tmp);
			if (tmp == WB_Classification.FRONT) {
				positiveVertexExists = true;
			}
			if (tmp == WB_Classification.BACK) {
				negativeVertexExists = true;
			}
			counter.increment();
		}
		if (positiveVertexExists && negativeVertexExists) {
			new ArrayList<HE_Vertex>();
			final HE_Selection split = new HE_Selection(lsel.parent);
			final HashMap<Long, Double> edgeInt = new HashMap<Long, Double>();
			final Iterator<HE_Halfedge> eItr = lsel.eItr();
			HE_Halfedge e;
			counter = new WB_ProgressCounter(lsel.getNumberOfEdges(), 10);

			tracker.setStatus(this, "Classifying edges.", counter);
			while (eItr.hasNext()) {
				e = eItr.next();
				if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.ON) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						cutEdges.add(e);
						e.setTemporaryLabel(1);
					} else {
						edgeInt.put(e.key(), 0.0);
					}
				} else if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.BACK) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						edgeInt.put(e.key(), 1.0);
					} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.FRONT) {
						edgeInt.put(e.key(), HE_GeometryOp.getIntersection(e, lP));
					}
				} else {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						edgeInt.put(e.key(), 1.0);
					} else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.BACK) {
						edgeInt.put(e.key(), HE_GeometryOp.getIntersection(e, lP));
					}
				}
				counter.increment();
			}
			counter = new WB_ProgressCounter(edgeInt.size(), 10);

			tracker.setStatus(this, "Indexing edge intersection.", counter);
			for (final Map.Entry<Long, Double> en : edgeInt.entrySet()) {
				final HE_Halfedge ce = lsel.parent.getHalfedgeWithKey(en.getKey());
				final double u = en.getValue();
				if (lsel.contains(ce.getFace())) {
					split.add(ce.getFace());
				}
				if (lsel.contains(ce.getPair().getFace())) {
					split.add(ce.getPair().getFace());
				}
				if (u == 0.0) {
					split.add(ce.getStartVertex());
				} else if (u == 1.0) {
					split.add(ce.getEndVertex());
				} else {
					split.add(lsel.parent.splitEdge(ce, u).vItr().next());
				}
				counter.increment();
			}
			HE_Face f;
			counter = new WB_ProgressCounter(split.getNumberOfFaces(), 10);

			tracker.setStatus(this, "Splitting faces.", counter);
			final Iterator<HE_Face> fItr = split.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				splitFaceInIntersections(f,lsel.parent);
				counter.increment();
			}
			paths = new FastTable<HE_Path>();
			if (cutEdges.getNumberOfEdges() > 1) {
				buildPaths(cutEdges);
			}
		}
		tracker.setStatus(this, "Exiting HEM_SliceSurface.", -1);
		return lsel.parent;
	}

	/**
	 *
	 *
	 * @param cutEdges
	 */
	private void buildPaths(final HE_Selection cutEdges) {
		tracker.setStatus(this, "Building slice paths.", 0);
		if (cutEdges.getNumberOfEdges() == 0) {
			return;
		}
		final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();
		for (final HE_Halfedge he : cutEdges.getEdgesAsList()) {
			final HE_Face f = he.getFace();
			if(f!=null){
				if (WB_GeometryOp.classifyPointToPlane3D(f.getFaceCenter(), P) == WB_Classification.FRONT) {
					edges.add(he.getPair());
				} else {
					edges.add(he);
				}
			}
		}
		WB_ProgressCounter counter = new WB_ProgressCounter(edges.size(), 10);

		tracker.setStatus(this, "Processing slice edges.", counter);
		while (edges.size() > 0) {
			final List<HE_Halfedge> pathedges = new FastTable<HE_Halfedge>();
			HE_Halfedge current = edges.get(0);
			pathedges.add(current);
			boolean loop = false;
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getVertex() == current.getEndVertex()) {
					if (i > 0) {
						current = edges.get(i);
						pathedges.add(current);
						i = -1;
					} else {
						loop = true;
						break;
					}
				}
			}
			if (!loop) {
				final List<HE_Halfedge> reversepathedges = new FastTable<HE_Halfedge>();
				current = edges.get(0);
				for (int i = 0; i < edges.size(); i++) {
					if (edges.get(i).getEndVertex() == current.getVertex()) {
						if (i > 0) {
							current = edges.get(i);
							reversepathedges.add(current);
							i = 0;
						}
					}
				}
				final List<HE_Halfedge> finalpathedges = new FastTable<HE_Halfedge>();
				for (int i = reversepathedges.size() - 1; i > -1; i--) {
					finalpathedges.add(reversepathedges.get(i));
				}
				finalpathedges.addAll(pathedges);
				paths.add(new HE_Path(finalpathedges, loop));
				edges.removeAll(finalpathedges);
			} else {
				paths.add(new HE_Path(pathedges, loop));
				edges.removeAll(pathedges);
			}
			counter.increment(pathedges.size());
		}
	}

	/**
	 *
	 * @return List of HE_Path created by the cutting plane
	 */
	public List<HE_Path> getPaths() {
		return paths;
	}


	void splitFaceInIntersections(final HE_Face f, final HE_Mesh mesh){

		Long[] intersectionVertices=new Long[2*f.getFaceOrder()];
		Long[] polygon=new Long[2*f.getFaceOrder()];
		int intersectionCount=0;
		int polygonCount=0;
		HE_FaceVertexCirculator fvCrc=f.fvCrc();
		HE_Vertex v;
		while(fvCrc.hasNext()){
			v=fvCrc.next();
			polygon[polygonCount++]=v.getKey();
			if((v.getTemporaryLabel()==ONVERTEX)||(v.getTemporaryLabel()==ONEDGE)){
				intersectionVertices[intersectionCount++]=v.getKey();

				if(v.getTemporaryLabel()==ONVERTEX) {

					polygon[polygonCount++]=v.getKey();
				}
				v.clearVisited();

			}

		}
		if(intersectionCount<2) {
			return;
		}

		else{
			intersectionVertices=Arrays.copyOf(intersectionVertices, intersectionCount);
			polygon=Arrays.copyOf(polygon, polygonCount);
			FastTable<Long[]> subPolygons=new FastTable<Long[]>();
			HE_Vertex v0=mesh.getVertexWithKey(intersectionVertices[0]);
			HE_Vertex v1;
			int i=1;
			do{
				v1=mesh.getVertexWithKey(intersectionVertices[i++]);
			}while(WB_Epsilon.isZeroSq(v0.getSqDistance3D(v1)));

			WB_Line intersectionLine= gf.createLineThroughPoints(v0, v1);
			Arrays.sort(intersectionVertices, new VertexOnLineComparator(mesh,intersectionLine));


			int trial=0;

			while(intersectionVertices.length>1){

				long key0 = intersectionVertices[0];
				long key1 = intersectionVertices[1];
				int index0=indexOf(key0,polygon);
				int index1=indexOf(key1,polygon);

				boolean solved = false;
				if(firstUnvisited(polygon, index0,mesh) == index1) {
					solved = true;
				} else
				{
					key1 = intersectionVertices[0];
					key0 = intersectionVertices[1];
					int tmp=index0;
					index0=index1;
					index1=tmp;
					if(firstUnvisited(polygon, index0,mesh) == index1) {
						solved = true;
					}
				}

				if(solved)
				{
					trial--;
					Long[] subPolygon=getSubPolygon(polygon,index0,index1);
					if(subPolygon.length>2) {
						subPolygons.add(subPolygon);
					}
					polygon=getSubPolygon(polygon,index1,index0);
					intersectionVertices=Arrays.copyOfRange(intersectionVertices, 2,intersectionVertices.length);
					if(intersectionVertices.length <2) {
						subPolygons.add(polygon);
					}
					mesh.getVertexWithKey(key0).setVisited();
					mesh.getVertexWithKey(key1).setVisited();
				}
				else { trial++; reverse(intersectionVertices); }
				if(trial>1) {
					break;
				}
			}

			for(Long[] subPoly:subPolygons){
				FastTable<HE_Halfedge> halfedges=new FastTable<HE_Halfedge>();
				HE_Halfedge he;
				HE_Face subFace=new HE_Face();
				subFace.copyProperties(f);
				for(int j=0;j<subPoly.length;j++){
					he=new HE_Halfedge();
					if(subPoly[j]!=subPoly[(j+1)%subPoly.length]){
						mesh.setVertex(he,mesh.getVertexWithKey(subPoly[j]));
						mesh.setHalfedge(he.getVertex(),he);
						mesh.setFace(he,subFace);
						halfedges.add(he);
						if(j==(subPoly.length-1)){
							he.setTemporaryLabel(1);
							cutEdges.add(he);
						}
					}
				}

				if(halfedges.size()>2){
					for(HE_Halfedge fhe:halfedges){
						if(fhe.getVertex().getTemporaryLabel()==FRONT){
							front.add(subFace);
						}else if(fhe.getVertex().getTemporaryLabel()==BACK){
							back.add(subFace);

						}

					}
					mesh.setHalfedge(subFace, halfedges.get(0));
					for(int j=0,k=halfedges.size()-1;j<halfedges.size();k=j,j++){
						mesh.setNext(halfedges.get(k),halfedges.get(j));
					}
					mesh.add(subFace);
					mesh.addHalfedges(halfedges);
				}





			}
			mesh.cutFace(f);
			mesh.pairHalfedges();
		}



	}

	static int firstUnvisited(final Long[] keys, final int start, final HE_Mesh mesh){
		int n=keys.length;
		HE_Vertex v;
		int index=-1;
		for (int i=0; i<keys.length; i++)
		{
			index=(start+1+i)%n;
			v=mesh.getVertexWithKey(keys[index]);
			if (((v.getTemporaryLabel()==ONVERTEX) || (v.getTemporaryLabel()==ONEDGE))&&!v.isVisited()) {
				return index;
			}
		}

		return -1;
	}


	static int indexOf(final Long key, final Long[] keys)
	{
		for (int i=0; i<keys.length; i++)
		{
			if (keys[i].equals(key)) {
				return i;
			}
		}

		return -1;
	}

	static Long[] getSubPolygon(final Long[] polygon, final int start, int end){
		int n = polygon.length;

		if(end<start) {
			end += n;
		}
		Long[] subPoly=new Long[(end-start)+1];
		int index=0;
		for(int i=start; i< (end+1); i++) {
			subPoly[index++]=polygon[i%n];
		}
		return subPoly;

	}

	public static void reverse(final Long[]  data) {
		int left = 0;
		int right = data.length - 1;
		while( left < right ) {
			long temp = data[left];
			data[left] = data[right];
			data[right] = temp;
			left++;
			right--;
		}
	}

	class VertexOnLineComparator implements Comparator<Long> {
		WB_Line L;
		HE_Mesh mesh;

		VertexOnLineComparator(final HE_Mesh mesh, final WB_Line L){
			this.L=L;
			this.mesh=mesh;
		}

		@Override
		public int compare(final Long v0, final Long v1) {
			double d=WB_GeometryOp.pointAlongLine(mesh.getVertexWithKey(v0), L)-WB_GeometryOp.pointAlongLine(mesh.getVertexWithKey(v1), L);
			return (WB_Epsilon.isZero(d))?0:((d>0)?1:-1);
		}
	}



	public static void main(final String[] args) {
		WB_Point[] basepoints =new WB_Point[24];
		for (int i=0;i<24;i++) {
			basepoints[i]=new WB_Point(0,50+(250*(i%2)),0);
			if(i>0) {
				basepoints[i].rotateAbout2PointAxisSelf((Math.PI/12.0)*i,0,0,0,0,0,1);
			}
		}

		//create polygon from base points, HEC_Polygon assumes the polygon is planar
		WB_Polygon polygon=WB_GeometryFactory.instance().createSimplePolygon(basepoints);

		HEC_Polygon creator=new HEC_Polygon();

		creator.setPolygon(polygon);//alternatively polygon can be a WB_Polygon2D
		creator.setThickness(50);// thickness 0 creates a surface

		HE_Mesh mesh=new HE_Mesh(creator);
		mesh.modify(new HEM_SliceSurface().setPlane(0,50.0,0,0,-1,0));



	}


}
