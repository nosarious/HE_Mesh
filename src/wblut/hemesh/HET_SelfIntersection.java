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
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Segment;
import wblut.math.WB_Epsilon;

/**

 *
 * @author Frederik Vanhoutte, W:Blut
 */
public class HET_SelfIntersection {


	/**
	 *
	 *
	 * @param tri
	 * @param tree
	 * @return
	 */
	static List<HET_SelfIntersectionResult> checkSelfIntersection(final HE_Face tri,
			final WB_AABBTree tree) {
		final List<HET_SelfIntersectionResult> selfints = new FastTable<HET_SelfIntersectionResult>();
		final HE_RASTrove<HE_Face> candidates = new HE_RASTrove<HE_Face>();
		final WB_AABB aabb = tri.toAABB();
		final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(aabb,
				tree);
		for (final WB_AABBNode n : nodes) {
			candidates.addAll(n.getFaces());
		}
		for (final HE_Vertex v : tri.getFaceVertices()) {
			candidates.removeAll(v.getFaceStar());
		}
		for (final HE_Face candidate : candidates) {
			if (candidate.getKey() > tri.getKey()) {// Check each face pair only
				// once
				final WB_IntersectionResult ir = WB_GeometryOp
						.getIntersection3D(tri.getHalfedge().getVertex(),tri.getHalfedge().getEndVertex(),tri.getHalfedge().getNextInFace().getEndVertex(),
								candidate.getHalfedge().getVertex(),candidate.getHalfedge().getEndVertex(),candidate.getHalfedge().getNextInFace().getEndVertex());
				if (ir.intersection
						&& (ir.object != null)
						&& !WB_Epsilon.isZero(((WB_Segment) ir.object)
								.getLength())) {
					candidate.setTemporaryLabel(1);
					selfints.add(new HET_SelfIntersectionResult(tri, candidate,
							(WB_Segment) ir.object));
				}
			}
		}
		return selfints;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static List<HET_SelfIntersectionResult> checkSelfIntersection(
			final HE_Mesh mesh) {
		mesh.triangulate();
		mesh.resetFaceTemporaryLabels();
		final WB_AABBTree tree = new WB_AABBTree(mesh, 1);
		/*final HE_FaceIterator fitr = mesh.fItr();
		final List<HET_SelfIntersectionResult> result = new FastTable<HET_SelfIntersectionResult>();
		List<HET_SelfIntersectionResult> selfints;
		HE_Face f;
		while (fitr.hasNext()) {
			f = fitr.next();
			selfints = checkSelfIntersection(f, tree);
			if (selfints.size() > 0) {
				f.setInternalLabel(1);
			}
			result.addAll(selfints);
		}*/
		return checkSelfIntersection(mesh.faces.getObjects(),tree);
	}



	/**
	 * 
	 *
	 * @param faces 
	 * @param tree 
	 * @return 
	 */
	private static List<HET_SelfIntersectionResult> checkSelfIntersection(final List<HE_Face> faces,
			final WB_AABBTree tree){

		List<HET_SelfIntersectionResult> selfints =new FastTable<HET_SelfIntersectionResult>();
		try {
			int threadCount = Runtime.getRuntime().availableProcessors();
			int dfaces = faces.size() / threadCount;
			if(dfaces<1024){
				dfaces=1024;
				threadCount=(int)Math.ceil(faces.size()/1024.0);

			}
			final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
			final List<Future<List<HET_SelfIntersectionResult>>>  list=new ArrayList<Future<List<HET_SelfIntersectionResult>>>();
			int i = 0;
			for (i = 0; i < (threadCount - 1); i++) {
				final Callable<List<HET_SelfIntersectionResult>> runner = new SelfIntersectionChecker(dfaces * i, (dfaces * (i + 1)) - 1,i,faces,tree);

				list.add(executor.submit(runner));
			}
			final Callable<List<HET_SelfIntersectionResult>> runner = new SelfIntersectionChecker(dfaces * i, faces.size() - 1,i,faces,tree);
			list.add(executor.submit(runner));


			for (Future<List<HET_SelfIntersectionResult>> future : list) {
				selfints.addAll(future.get());
			}



			executor.shutdown();

		}catch(final InterruptedException ex) {
			ex.printStackTrace();
		} catch(final ExecutionException ex) {
			ex.printStackTrace();
		}
		return selfints;
	}

	/**
	 *
	 */
	static class SelfIntersectionChecker implements Callable<List<HET_SelfIntersectionResult>>{
		int start;
		int end;
		int id;
		int[] triangles;
		List<HE_Face> faces;
		WB_AABBTree tree;

		/**
		 * 
		 *
		 * @param s 
		 * @param e 
		 * @param id 
		 * @param faces 
		 * @param tree 
		 */
		public SelfIntersectionChecker(final int s, final int e,final int id,final List<HE_Face> faces, final WB_AABBTree tree) {
			start = s;
			end = e;
			this.id=id;
			this.faces=faces;
			this.tree=tree;
		}


		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public List<HET_SelfIntersectionResult> call() {
			ArrayList<HET_SelfIntersectionResult> selfints=new ArrayList<HET_SelfIntersectionResult>();
			ListIterator<HE_Face> itr=faces.listIterator(start);
			for (int i = start; i <= end; i++) {
				selfints.addAll(checkSelfIntersection(itr.next(),
						tree));

			}
			return selfints;
		}
	}




	/**
	 *
	 */
	public static class HET_SelfIntersectionResult {
		/**
		 *
		 */
		HE_Face f1;
		/**
		 *
		 */
		HE_Face f2;
		/**
		 *
		 */
		WB_Segment segment;

		/**
		 *
		 *
		 * @param f1
		 * @param f2
		 * @param seg
		 */
		public HET_SelfIntersectionResult(final HE_Face f1, final HE_Face f2,
				final WB_Segment seg) {
			this.f1 = f1;
			this.f2 = f2;
			segment = seg;
		}

		/**
		 *
		 *
		 * @return
		 */
		public HE_Face getFace1() {
			return f1;
		}

		/**
		 *
		 *
		 * @return
		 */
		public HE_Face getFace2() {
			return f2;
		}

		/**
		 *
		 *
		 * @return
		 */
		public WB_Segment getSegment() {
			return segment;
		}
	}
}
