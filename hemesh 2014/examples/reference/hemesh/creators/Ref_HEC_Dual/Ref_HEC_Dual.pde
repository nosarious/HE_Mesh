import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
HE_Mesh dual;
WB_Render render;

void setup() {
  size(1600, 800, OPENGL);
  smooth(8);

  mesh=new HE_Mesh(new HEC_Cube(50,1,1,2).setRadius(100)); 
 
  mesh.modify(new HEM_Crocodile().setDistance(150));
  mesh.modify(new HEM_Diagrid());
  HEC_Dual creator=new HEC_Dual();
  creator.setSource(mesh).setFixNonPlanarFaces(true);
  dual=new HE_Mesh(creator);
 
  HET_Diagnosis.validate(dual);
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  strokeWeight(2);
  pushMatrix();
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  render.drawEdges(mesh);

  
  noStroke();
 render.drawFaces(mesh);
  
  popMatrix();
   translate(1200, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  
  render.drawEdges(dual);
  
  noStroke();

  render.drawFaces(dual);
}

