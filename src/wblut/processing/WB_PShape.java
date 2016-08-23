/**
 *
 */
package wblut.processing;

import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_Vector;
import wblut.hemesh.HE_EdgeIterator;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

/**
 * @author FVH
 *
 */
public class WB_PShape {

	private static WB_GeometryFactory geometryfactory = new WB_GeometryFactory();

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacetedPShape(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 * @param mesh
	 * @param img
	 * @return
	 */
	public static PShape toFacetedPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		retained.texture(img);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	public static PShape toFacetedPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);

		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.texture(img[f.getTextureId()]);
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param offset
	 * @return
	 */
	public static PShape toFacetedPShape(final HE_MeshStructure mesh, final double offset, final PApplet home) {

		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);

		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		List<HE_Vertex> vertices;
		HE_Vertex v;
		WB_Coord fn;
		final float df = (float) offset;
		while (fItr.hasNext()) {
			f = fItr.next();
			vertices = f.getFaceVertices();
			if (vertices.size() > 2) {
				final int[] tris = f.getTriangles();
				for (int i = 0; i < tris.length; i += 3) {
					v = vertices.get(tris[i]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
					v = vertices.get(tris[i + 1]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
					v = vertices.get(tris[i + 2]);
					fn = v.getVertexNormal();
					retained.vertex(v.xf() + df * fn.xf(), v.yf() + df * fn.yf(), v.zf() + df * fn.zf(),
							v.getUVW(f).xf(), v.getUVW(f).yf());
				}
			}

		}
		retained.endShape();

		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacetedPShape(final WB_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_Mesh lmesh = geometryfactory.createTriMesh(mesh);
		final List<WB_Coord> seq = lmesh.getPoints();
		WB_Coord p = seq.get(0);
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			p = seq.get(id);
			;
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacetedPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			retained.fill(f.getColor());
			do {
				v = he.getVertex();
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacetedPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.fill(v.getColor());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacettedPShape(final HE_Mesh mesh, final PApplet home) {
		return toFacetedPShape(mesh, home);
	}

	public static PShape toFacettedPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		return toFacetedPShape(mesh, img, home);
	}

	public static PShape toFacettedPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		return toFacetedPShape(mesh, img, home);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @param offset
	 * @return
	 */
	public static PShape toFacettedPShape(final HE_MeshStructure mesh, final double offset, final PApplet home) {
		return toFacetedPShape(mesh, offset, home);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacettedPShape(final WB_Mesh mesh, final PApplet home) {
		return toFacetedPShape(mesh, home);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacettedPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		return toFacetedPShapeWithFaceColor(mesh, home);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toFacettedPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		return toFacetedPShapeWithVertexColor(mesh, home);
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toSmoothPShape(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		WB_Coord n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	public static PShape toSmoothPShape(final HE_Mesh mesh, final PImage img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		retained.texture(img);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		WB_Coord n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	public static PShape toSmoothPShape(final HE_Mesh mesh, final PImage[] img, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		WB_Coord n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.texture(img[f.getTextureId()]);
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toSmoothPShape(final WB_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_Mesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_Vector v = geometryfactory.createVector();
		final List<WB_Coord> seq = lmesh.getPoints();
		WB_Coord p = seq.get(0);
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			v.set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.get(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toSmoothPShapeWithFaceColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		WB_Coord n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			retained.fill(f.getColor());
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toSmoothPShapeWithVertexColor(final HE_Mesh mesh, final PApplet home) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.copy();
		lmesh.triangulate();
		WB_Coord n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.fill(v.getColor());
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf(), v.getUVW(f).xf(), v.getUVW(f).yf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
		}
		retained.endShape();
		return retained;
	}

	/**
	 *
	 *
	 * @param mesh
	 * @return
	 */
	public static PShape toWireframePShape(final HE_MeshStructure mesh, final PApplet home) {
		final PShape retained = home.createShape();
		if (mesh instanceof HE_Selection) {
			((HE_Selection) mesh).collectEdgesByFace();
		}
		final HE_EdgeIterator eItr = mesh.eItr();
		HE_Halfedge e;
		HE_Vertex v;
		retained.beginShape(PConstants.LINES);
		while (eItr.hasNext()) {
			e = eItr.next();
			v = e.getVertex();
			retained.vertex(v.xf(), v.yf(), v.zf());
			v = e.getEndVertex();
			retained.vertex(v.xf(), v.yf(), v.zf());
		}
		retained.endShape();
		return retained;
	}

}
