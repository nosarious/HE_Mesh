import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

WB_Curve curve2D;
WB_Curve curve3D;
WB_Render render;

void setup(){
   size(800, 800, OPENGL);
  smooth(8);
  double a=7;
  double b=2.5;
  double c=5;
  //Epitrochoid
  curve2D= new WB_ExpressionCurve((a+b)+"*cos(t)-"+c+"*cos(("+a/b+"+1)*t)",(a+b)+"*sin(t)-"+c+"*sin(("+a/b+"+1)*t)", "t");
  curve3D= new WB_ExpressionCurve((a+b)+"*cos(t)-"+c+"*cos(("+a/b+"+1)*t)",(a+b)+"*sin(t)-"+c+"*sin(("+a/b+"+1)*t)", "10*sin(t)" ,"t");
  render=new WB_Render(this);
}

void draw(){
   background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
 scale(15);
 strokeWeight(0.05);
  stroke(255,0,0);
  render.drawCurve(curve2D,-2.5*TWO_PI,2.5*TWO_PI,1024);
   stroke(0);
  render.drawCurve(curve3D,-2.5*TWO_PI,2.5*TWO_PI,1024);
  
  
}
