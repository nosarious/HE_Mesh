import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;
WB_Plane P;
HEM_Slice modifier;

void setup() {
  size(800, 800, P3D);
  createMesh();
  modifier=new HEM_Slice();
  P=new WB_Plane(0,0, 0, 0, 0, 1); 
  modifier.setPlane(P);
  modifier.setSimpleCap(false);
  mesh.modify(modifier);
  render=new WB_Render(this);
}

void draw() {
  background(55);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(width/2, height/2, 0);
  rotateY(map(mouseX, 0, width, -PI, PI));
  rotateX(map(mouseY, 0, height, PI, -PI));
  noStroke();
  fill(255);
  render.drawFaces(mesh);
  noFill();
  strokeWeight(1);
  stroke(0);
  render.drawEdges(mesh);
  stroke(255, 0, 0);
  render.drawPlane(P, 300);
}

void createMesh() {
  HEC_Torus creator=new HEC_Torus(80, 200, 6, 16).setTwist(3);
  mesh=new HE_Mesh(creator);
  creator=new HEC_Torus(40, 200, 6, 16).setTwist(-3);
  HE_Mesh inner=new HE_Mesh(creator);
  inner.flipFaces();
  mesh.add(inner);
  mesh.smooth(2);
}