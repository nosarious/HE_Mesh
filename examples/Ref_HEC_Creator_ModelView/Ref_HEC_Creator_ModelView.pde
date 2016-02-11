import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh[] meshes;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  HEC_Icosahedron creator=new HEC_Icosahedron();
  // .setToModelview(this) uses the Processing transforms to set the positions
  // .setToWorldview(), the default mode, ignores Processing transfors
  creator.setEdge(40).setToModelview(this);
  meshes=new HE_Mesh[17];
  for (int i=0; i<17; i++) {
    pushMatrix();
    rotateZ(i*QUARTER_PI);
    translate(200, 0,-150+20*i);
    scale(1.2-(i-8)*(i-8)*0.02);
    meshes[i]=new HE_Mesh(creator); 
    popMatrix();
  }
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  for(HE_Mesh mesh:meshes){
  stroke(0);
  render.drawEdges(mesh);
  noStroke();
  render.drawFaces(mesh);
  }
}