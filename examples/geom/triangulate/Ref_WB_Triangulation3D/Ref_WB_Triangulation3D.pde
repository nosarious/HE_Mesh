import wblut.geom.*;
import wblut.processing.*;

WB_RandomPoint source;
WB_Render3D render;
WB_Point[] points;
int numPoints;
int[] tetrahedra;

void setup() {
  size(800, 800, P3D);
  source=new WB_RandomInSphere().setRadius(250);
  render=new WB_Render3D(this);
  numPoints=400;
  points=new WB_Point[numPoints];
  for (int i=0; i<numPoints; i++) {
    points[i]=source.nextPoint();
  }
  WB_Triangulation3D triangulation=WB_Triangulate.triangulate3D(points);
  tetrahedra=triangulation.getTetrahedra();// 1D array of indices of tetrahedra, 4 indices per tetrahedron
  println("First tetrahedron: ["+tetrahedra[0]+", "+tetrahedra[1]+", "+tetrahedra[2]+", "+tetrahedra[3]+"]");
  noFill();

}


void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(width/2, height/2);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  render.drawTetrahedron(tetrahedra, points);
}