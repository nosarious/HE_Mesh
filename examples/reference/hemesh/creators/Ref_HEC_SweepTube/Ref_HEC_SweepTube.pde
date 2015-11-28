import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

WB_Render render;
WB_Curve C;
WB_Point[] points;
HE_Mesh mesh;

void setup() {
  size(800, 800, OPENGL);
  smooth(8);
  // Several WB_Curve classes are in development. HEC_SweepTube provides
  // a way of generating meshes from them.

 
 double a=7;
  double b=2.5;
  double c=10;
  C= new WB_ExpressionCurve((a+b)+"*cos(t)-"+c+"*cos(("+a/b+"+1)*t)",(a+b)+"*sin(t)-"+c+"*sin(("+a/b+"+1)*t)", "10*sin(t)" ,"t");
  

  HEC_SweepTube creator=new HEC_SweepTube();
  creator.setCurve(C);//curve should be a WB_BSpline
  creator.setRadius(4);
  creator.setSteps(1024);
  creator.setFacets(8);
  creator.setScale(13);
  creator.setRange(-2.4*TWO_PI,2.4*TWO_PI);
  creator.setCap(true,true);
  mesh=new HE_Mesh(creator); 
 
  HET_Diagnosis.validate(mesh);
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  render.drawEdges(mesh);
  noStroke();
  render.drawFaces(mesh);
}

