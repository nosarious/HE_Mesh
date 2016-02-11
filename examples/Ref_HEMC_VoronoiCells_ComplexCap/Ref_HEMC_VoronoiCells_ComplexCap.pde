import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

float[][] points;
int numpoints;
HE_Mesh container;
HE_MeshCollection cells;
int numcells;
WB_Render render;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  HEC_Torus creator=new HEC_Torus(80, 200, 6, 16);
  container=new HE_Mesh(creator);
  creator=new HEC_Torus(40, 200, 6, 16);
  HE_Mesh inner=new HE_Mesh(creator);
  inner.flipFaces();
  container.add(inner);
 
container.smooth(2);
  
  HE_FaceIterator fitr=container.fItr();
  while (fitr.hasNext()) {
    fitr.next().setColor(color(0, 200, 50));
  }

  numpoints=20;
  points=new float[numpoints][3];
  for (int i=0; i<numpoints; i++) {
    points[i][0]=random(-250, 250);
    points[i][1]=random(-250, 250);
    points[i][2]=random(-100, 100);
  }

  HEMC_VoronoiCells multiCreator=new HEMC_VoronoiCells();
  multiCreator.setPoints(points);
  multiCreator.setN(numpoints);
  multiCreator.setContainer(container);
  multiCreator.setOffset(20);
 multiCreator.setSimpleCap(false);
  cells=multiCreator.create();
  numcells=cells.size();
  render=new WB_Render(this);
}

void draw() {
  background(55);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  drawFaces();
  drawEdges();
}

void drawEdges() {
  stroke(0);
  for (int i=0; i<numcells; i++) {
    render.drawEdges(cells.getMesh(i));
  }
}

void drawFaces() {
  noStroke();
  fill(255);
  for (int i=0; i<numcells; i++) {
    render.drawFacesFC(cells.getMesh(i));
  }
}