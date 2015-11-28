import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;
import colorlib.webservices.*;
import colorlib.tools.*;
import colorlib.*;

ColourLovers cl;
ArrayList<Palette> palettes;
HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, OPENGL);
  smooth(8);
  
 color[] colors = new color[ 3 ];
  colors[0] = color( 0, 0, 0 );
  colors[1] = color( 50, 50, 50 );
  colors[2] = color( 255, 255, 0 );
  int numOfColors= 64;
  Palette palette = new Gradient( this ).addColors( colors ).setSteps(numOfColors );
  
  mesh=new HE_Mesh(new HEC_Torus(80, 200, 12, 24).setTwist(12)); 
  mesh.modify(new HEM_Crocodile().setDistance(100).setChamfer(0.25));
  mesh.smooth();
  HET_Texture.setRandomFaceColorFromPalette(mesh,palette);
 
  render=new WB_Render(this);
}

void draw() {
  background(120);
  noLights();
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.drawFacesFC(mesh);
  stroke(0);
  render.drawEdges(mesh);
}

