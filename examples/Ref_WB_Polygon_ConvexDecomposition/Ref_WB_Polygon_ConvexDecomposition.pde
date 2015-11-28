//Convex decomposition of a polygon, holes are not supported.

import wblut.processing.*;
import wblut.hemesh.*;
import wblut.geom.*;
import wblut.math.*;
import java.util.List;

WB_GeometryFactory gf=WB_GeometryFactory.instance();
WB_Render2D render;
ArrayList<WB_Point> shell;
WB_Polygon polygon;
List<WB_Polygon> convexDecomposition;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  render=new WB_Render2D(this);
  shell= new ArrayList<WB_Point>();

  for (int i=0; i<40; i++) {
    shell.add(gf.createPointFromPolar(100*(i%2+random(1, 3)), TWO_PI/40.0*i));
  } 
  polygon=gf.createSimplePolygon(shell);
  convexDecomposition=gf.createConvexPolygonDecomposition(polygon);
  background(255);
}

void draw() {
  background(255);
  translate(width/2, height/2);
  scale(1, -1);
  noFill();
  strokeWeight(2);
  stroke(0,0,255);
  render.drawPolygonEdges2D(polygon);
  strokeWeight(1.4);
  stroke(0);
  for (WB_Polygon convexPoly : convexDecomposition){
    render.drawPolygonEdges2D(convexPoly);
  }
 
}