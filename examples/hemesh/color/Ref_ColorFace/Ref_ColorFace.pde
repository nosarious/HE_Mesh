import wblut.math.*;
import wblut.processing.*;
import wblut.hemesh.*;
import wblut.geom.*;
import wblut.core.*;

HE_Mesh mesh;
WB_Render3D render;

void setup() {
  size(800, 800,P3D);
  smooth(8);

  HEC_Dodecahedron creator=new HEC_Dodecahedron();
  creator.setEdge(200); 
  mesh=new HE_Mesh(creator); 
 HET_MeshOp.splitFacesTri(mesh);
  HE_FaceIterator fitr=mesh.fItr();
  while(fitr.hasNext()){
   fitr.next().setColor(color(random(255), 0,0));
  }
  mesh.smooth(3);
  HET_MeshOp.splitFacesTri(mesh);  
  render=new WB_Render3D(this);
}

void draw() {
  background(55);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  render.drawEdges(mesh);
  noStroke();
  render.drawFacesFC(mesh);
}