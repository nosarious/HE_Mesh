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
  mesh=new HE_Mesh(new HEC_Torus(80, 200, 12, 24).setTwist(12)); 
  mesh.modify(new HEM_Crocodile().setDistance(100).setChamfer(0.25));
  mesh.smooth();
  assignColorsAlongZAxis();
  render=new WB_Render(this);
}

void assignColorsAlongZAxis(){
  color[] colors = new color[ 2 ];
  colors[0] = color( 255, 0, 255 );
  colors[1] = color( 255, 255, 0 );
  int numOfColors= 64;
  Palette palette = new Gradient( this ).addColors( colors ).setSteps(numOfColors );

   WB_AABB box=mesh.getAABB();
   float zmin=(float)box.getMin(2);
  float zmax=(float)box.getMax(2);
  HE_FaceIterator fitr=mesh.fItr();
  HE_Face f;
  while(fitr.hasNext()){
    f=fitr.next();
    WB_Coordinate fc=f.getFaceCenter();
    int colorIndex= (int) (numOfColors*(fc.zf()-zmin)/(zmax-zmin));
    if(colorIndex==numOfColors) colorIndex=numOfColors-1;
    f.setLabel(colorIndex);
 
  }
  
  
  HET_Texture.setFaceColorFromPalette(mesh,palette);
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

