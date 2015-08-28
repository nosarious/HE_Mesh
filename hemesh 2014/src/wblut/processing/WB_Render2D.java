/*
 * 
 */
package wblut.processing;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Geometry;
import wblut.geom.WB_GeometryCollection;
import wblut.geom.WB_Line;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Ring;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Triangulation2D;

/**
 * 
 */
public class WB_Render2D {
    
    /**
     * 
     */
    private  PGraphics home;

    
    public WB_Render2D() {
    	
        }
    
    /**
     * 
     *
     * @param home 
     */
    public WB_Render2D(final PApplet home) {
	this.home = home.g;
    }

    /**
     * 
     *
     * @param home 
     */
    public WB_Render2D(final PGraphics home) {
	this.home = home;
    }

    /**
     * 
     *
     * @param p 
     */
    public void drawPoint2D(final WB_Coordinate p) {
	home.point(p.xf(), p.yf());
    }

    /**
     * 
     *
     * @param p 
     * @param r 
     */
    public void drawPoint2D(final WB_Coordinate p, final double r) {
	home.ellipse(p.xf(), p.yf(), 2 * (float) r, 2 * (float) r);
    }

    /**
     * 
     *
     * @param v 
     * @param p 
     * @param r 
     */
    public void drawVector2D(final WB_Coordinate v, final WB_Coordinate p,
	    final double r) {
	home.pushMatrix();
	home.translate(p.xf(), p.yf());
	home.line(0f, 0f, (float) (r * v.xd()), (float) (r * v.yd()));
	home.popMatrix();
    }

    /**
     * 
     *
     * @param L 
     * @param d 
     */
    public void drawLine2D(final WB_Line L, final double d) {
	home.line((float) (L.getOrigin().xd() - (d * L.getDirection().xd())),
		(float) (L.getOrigin().yd() - (d * L.getDirection().yd())),
		(float) (L.getOrigin().xd() + (d * L.getDirection().xd())),
		(float) (L.getOrigin().yd() + (d * L.getDirection().yd())));
    }

    /**
     * 
     *
     * @param R 
     * @param d 
     */
    public void drawRay2D(final WB_Ray R, final double d) {
	home.line((float) (R.getOrigin().xd()), (float) (R.getOrigin().yd()),
		(float) (R.getOrigin().xd() + (d * R.getDirection().xd())),
		(float) (R.getOrigin().yd() + (d * R.getDirection().yd())));
    }

   
    /**
     * 
     *
     * @param p 
     * @param q 
     */
    public void drawSegment2D(final WB_Coordinate p, final WB_Coordinate q) {
	home.line((float) (p.xd()), (float) (p.yd()), (float) (q.xd()),
		(float) (q.yd()));
    }

    /**
     * 
     *
     * @param P 
     */
    public void drawPolyLine2D(final WB_PolyLine P) {
	for (int i = 0; i < (P.getNumberOfPoints() - 1); i++) {
	    home.line((float) (P.getPoint(i).xd()),
		    (float) (P.getPoint(i).yd()),
		    (float) (P.getPoint(i + 1).xd()),
		    (float) (P.getPoint(i + 1).yd()));
	}
    }

    /**
     * 
     *
     * @param P 
     */
    public void drawRing2D(final WB_Ring P) {
	for (int i = 0, j = P.getNumberOfPoints() - 1; i < P
		.getNumberOfPoints(); j = i++) {
	    home.line((float) (P.getPoint(j).xd()),
		    (float) (P.getPoint(j).yd()), (float) (P.getPoint(i).xd()),
		    (float) (P.getPoint(i).yd()));
	}
    }

    /**
     * 
     *
     * @param P 
     */
    public void drawPolygon2D(final WB_Polygon P) {
	final int[][] tris = P.getTriangles();
	for (final int[] tri : tris) {
	    drawTriangle2D(P.getPoint(tri[0]), P.getPoint(tri[1]),
		    P.getPoint(tri[2]));
	}
    }

    /**
     * 
     *
     * @param P 
     */
    public void drawPolygonEdges2D(final WB_Polygon P) {
	final int[] npc = P.getNumberOfPointsPerContour();
	int index = 0;
	for (int i = 0; i < P.getNumberOfContours(); i++) {
	    home.beginShape();
	    for (int j = 0; j < npc[i]; j++) {
		vertex2D(P.getPoint(index++));
	    }
	    home.endShape(PConstants.CLOSE);
	}
    }

    /**
     * 
     *
     * @param C 
     */
    public void drawCircle2D(final WB_Circle C) {
	home.ellipse((float) C.getCenter().xd(), (float) C.getCenter().yd(),
		2 * (float) C.getRadius(), 2 * (float) C.getRadius());
    }

    /**
     * 
     *
     * @param T 
     */
    public void drawTriangle2D(final WB_Triangle T) {
	home.beginShape(PConstants.TRIANGLE);
	vertex2D(T.p1());
	vertex2D(T.p2());
	vertex2D(T.p3());
	home.endShape();
    }

    /**
     * 
     *
     * @param p1 
     * @param p2 
     * @param p3 
     */
    public void drawTriangle2D(final WB_Coordinate p1, final WB_Coordinate p2,
	    final WB_Coordinate p3) {
	home.beginShape(PConstants.TRIANGLE);
	vertex2D(p1);
	vertex2D(p2);
	vertex2D(p3);
	home.endShape();
    }

    /**
     * 
     *
     * @param tri 
     * @param points 
     */
    public void drawTriangle2D(final int[] tri,
	    final List<? extends WB_Coordinate> points) {
	home.beginShape(PConstants.TRIANGLE);
	vertex2D(points.get(0));
	vertex2D(points.get(1));
	vertex2D(points.get(2));
	home.endShape();
    }

    /**
     * 
     *
     * @param tri 
     * @param points 
     */
    public void drawTriangle2D(final int[] tri, final WB_Coordinate[] points) {
	home.beginShape(PConstants.TRIANGLE);
	vertex2D(points[0]);
	vertex2D(points[1]);
	vertex2D(points[2]);
	home.endShape();
    }

    /**
     * 
     *
     * @param tri 
     * @param points 
     */
    public void drawTriangulation2D(final WB_Triangulation2D tri,
	    final List<? extends WB_Coordinate> points) {
	final int[][] triangles = tri.getTriangles();
	home.beginShape(PConstants.TRIANGLES);
	for (final int[] triangle : triangles) {
	    vertex2D(points.get(triangle[0]));
	    vertex2D(points.get(triangle[1]));
	    vertex2D(points.get(triangle[2]));
	}
	home.endShape();
    }

    /**
     * 
     *
     * @param tri 
     * @param points 
     */
    public void drawTriangulationEdges2D(final WB_Triangulation2D tri,
	    final List<? extends WB_Coordinate> points) {
	final int[][] edges = tri.getEdges();
	for (final int[] edge : edges) {
	    drawSegment2D(points.get(edge[0]), points.get(edge[1]));
	}
    }

    /**
     * 
     *
     * @param p 
     */
    private void vertex2D(final WB_Coordinate p) {
	home.vertex(p.xf(), p.yf());
    }

    /**
     * 
     *
     * @param geometry 
     * @param f 
     */
    public void drawGeometry2D(final WB_Geometry geometry, final double... f) {
	if (geometry instanceof WB_Coordinate) {
	    if (f.length == 0) {
		drawPoint2D((WB_Coordinate) geometry);
	    } else if (f.length == 1) {
		drawPoint2D((WB_Coordinate) geometry, f[0]);
	    }
	} else if (geometry instanceof WB_Segment) {
	    if (f.length == 0) {
		drawSegment2D((WB_Segment) geometry);
	    }
	} else if (geometry instanceof WB_Ray) {
	    if (f.length == 1) {
		drawRay2D((WB_Ray) geometry, f[0]);
	    }
	} else if (geometry instanceof WB_Line) {
	    if (f.length == 1) {
		drawLine2D((WB_Line) geometry, f[0]);
	    }
	} else if (geometry instanceof WB_Circle) {
	    if (f.length == 0) {
		drawCircle2D((WB_Circle) geometry);
	    }
	} else if (geometry instanceof WB_Triangle) {
	    if (f.length == 0) {
		drawTriangle2D((WB_Triangle) geometry);
	    }
	} else if (geometry instanceof WB_Polygon) {
	    if (f.length == 0) {
		drawPolygon2D((WB_Polygon) geometry);
	    }
	} else if (geometry instanceof WB_Ring) {
	    if (f.length == 0) {
		drawRing2D((WB_Ring) geometry);
	    }
	} else if (geometry instanceof WB_PolyLine) {
	    if (f.length == 0) {
		drawPolyLine2D((WB_PolyLine) geometry);
	    }
	} else if (geometry instanceof WB_GeometryCollection) {
	    final WB_GeometryCollection geo = (WB_GeometryCollection) geometry;
	    for (int i = 0; i < geo.getNumberOfGeometries(); i++) {
		drawGeometry2D(geo.getGeometry(i), f);
	    }
	}
    }

    /**
     * 
     *
     * @param geometry 
     * @param f 
     */
    public void drawGeometry2D(final Collection<? extends WB_Geometry> geometry,
	    final double... f) {
	for (final WB_Geometry geo : geometry) {
	    drawGeometry2D(geo, f);
	}
    }

    /**
     * 
     *
     * @param geometry 
     * @param f 
     */
    public void drawGeometry2D(final WB_Geometry[] geometry, final double... f) {
	for (final WB_Geometry geo : geometry) {
	    drawGeometry2D(geo, f);
	}
    }

	/**
	 *
	 *
	 * @param triangles
	 */
	public void drawTriangle2D(final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangle2D(triItr.next());
	}
	}


	/**
	 *
	 *
	 * @param triangles
	 */
	public void drawTriangle2D(final WB_Triangle[] triangles) {
	for (final WB_Triangle triangle : triangles) {
	    drawTriangle2D(triangle);
	}
	}

	/**
	 *
	 *
	 * @param triangles
	 */
	public void drawTriangle2DEdges(
	    final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangle2DEdges(triItr.next());
	}
	}

	/**
	 *
	 *
	 * @param triangle
	 */
	public void drawTriangle2DEdges(final WB_Triangle triangle) {
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p2().xf(),
		triangle.p2().yf());
	home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p2().xf(),
		triangle.p2().yf());
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p3().xf(),
		triangle.p3().yf());
	}

	/**
	 *
	 *
	 * @param triangles
	 */
	public void drawTriangle2DEdges(final WB_Triangle[] triangles) {
	for (final WB_Triangle triangle : triangles) {
	    drawTriangle2DEdges(triangle);
	}
	}

	/**
	 *
	 *
	 * @param segments
	 */
	public void drawSegment2D(final Collection<? extends WB_Segment> segments) {
	final Iterator<? extends WB_Segment> segItr = segments.iterator();
	while (segItr.hasNext()) {
	    drawSegment2D(segItr.next());
	}
	}

	/**
	 *
	 *
	 * @param segment
	 */
	public void drawSegment2D(final WB_Segment segment) {
	home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
		.getEndpoint().xf(), segment.getEndpoint().yf());
	}

	/**
	 *
	 *
	 * @param segments
	 */
	public void drawSegment2D(final WB_Segment[] segments) {
	for (final WB_Segment segment : segments) {
	    drawSegment2D(segment);
	}
	}
}
