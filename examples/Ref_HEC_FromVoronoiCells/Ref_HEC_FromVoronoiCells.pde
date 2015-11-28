import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

import processing.opengl.*;

WB_Point[] points;
int numpoints;
HE_Mesh container;
HE_MeshCollection cells;
int numcells;

HE_Mesh mesh;

WB_Render render;

void setup() {
  size(800, 800, P3D);
  smooth(8);

  //HEC_FromVoronoiCells can be used to recombine meshes generated
  //bij HEMC_VoronoiCells into a single mesh.

  //create a container mesh
  container=new HE_Mesh(new HEC_Geodesic().setRadius(300)); 
  HE_FaceIterator fitr=new HE_FaceIterator(container);
  while (fitr.hasNext()) fitr.next().setColor(color(0, 200, 50));

  //generate points
  numpoints=100;
  create();
  render=new WB_Render(this);
}

void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(width/2, height/2, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.drawFacesFC(mesh);
  stroke(0);
  render.drawEdges(mesh);
}

void mousePressed() {
  create();
}


void create() {  
  // generate points
  points=new WB_Point[numpoints];
  WB_RandomPoint generator=new WB_RandomInSphere().setRadius(250);
  for (int i=0; i<numpoints; i++) {
    points[i]=generator.nextPoint();
  }

  // generate voronoi cells
  HEMC_VoronoiCells multiCreator=new HEMC_VoronoiCells().setPoints(points).setN(numpoints).setContainer(container).setOffset(0);
  cells=multiCreator.create();

  //color the cells
  int counter=0;
  HE_MeshIterator mItr=cells.mItr();
  while (mItr.hasNext()) {
    mItr.next().setFaceColorWithOtherInternalLabel(color(255-counter, 220, counter), -1);
    counter++;
  }

  numcells=cells.size();
  boolean[] isCellOn=new boolean[numcells];
  for (int i=0; i<numcells; i++) {
    isCellOn[i]=(random(100)<50);
  }

  //build new mesh from active cells

  HEC_FromVoronoiCells creator=new HEC_FromVoronoiCells();
  creator.setCells(cells);// output of HEMC_VoronoiCells, 
  creator.setActive(isCellOn);// boolean array
  mesh=new HE_Mesh(creator);
  
  //clean-up mesh (join fragmented faces back together where possible)
  mesh.fuseCoplanarFaces();
  mesh.deleteTwoEdgeVertices();
  
  //smooth interior
  mesh.subdivideSelected(new HES_CatmullClark(), mesh.selectFacesWithOtherInternalLabel(-1), 2);
}