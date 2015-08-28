import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, OPENGL);
  smooth(8);
  HEC_UVParametric creator=new  HEC_UVParametric();
  WB_ExpressionVectorParameter expression = new WB_ExpressionVectorParameter(
  "sin(8*v)/16+(1.3+sin(u))*cos(v)",
  "sin(8*v)/16+2.0/3.0*cos(u)+(1.3+sin(u))*sin(v)",
  "sin(8*v)/16+0.01*sin(100*v)-0.1*sin(3*u)+cos(u)" ,
  "u", "v");
  creator.setUVSteps(40, 40);
  creator.setURange(0,2*Math.PI);
  creator.setVRange(0,2*Math.PI);
  creator.setScale(100);
  creator.setFixDuplicatedVertices(true);
  creator.setEvaluator(expression);
  mesh=new HE_Mesh(creator); 
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.drawFaces(mesh);
  stroke(0);
  render.drawEdges(mesh);
}



