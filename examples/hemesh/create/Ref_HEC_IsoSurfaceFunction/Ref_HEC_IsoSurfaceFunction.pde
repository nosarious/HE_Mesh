import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;
import processing.opengl.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(1000,1000,P3D);
  smooth(8);


  HEC_IsoSurface creator=new HEC_IsoSurface();
  creator.setResolution(128, 64,64);
  creator.setSize(8, 8,8);
  //3D grids of values can take up a lot of memory, using a function
  //can decrease the memory use, allow larger grids. The resulting number
  //of triangles can still be a limiting factor for Processing though.
  creator.setValues(new ScalarField(),0.0,0.0,0.0,.1,.1,.1);

  creator.setIsolevel(.6);
  creator.setInvert(false);
  creator.setBoundary(100);
  //Gamma controls level of grid snap, 0.0-0.5. Can improve the 
  //quality of the triangles, but can give small changes in topology.
  creator.setGamma(0.3); 
  

  mesh=new HE_Mesh(creator);

  render=new WB_Render(this);
}

void draw() {
  background(55);
  lights();
  translate(width/2, height/2);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.drawFaces(mesh);
  stroke(0);
  render.drawEdges(mesh);
}

class ScalarField implements WB_ScalarParameter{
  double evaluate(double... x){
   return 2.5*sin((float)x[0])*cos((float)x[1])*sin((float)x[2])+4*noise(0.2*(float)x[0],0.4*(float)x[1],0.2*(float)x[2]); 
  }
}