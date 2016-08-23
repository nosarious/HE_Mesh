import wblut.core.*;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.math.*;
import wblut.nurbs.*;
import wblut.processing.*;

import java.util.List;


/*
 *
 *  HET_Recorder tries to turn record a valid mesh while Processing is drawing.
 *
 */

HET_Recorder recorder;
WB_Render render;
HE_MeshCollection meshes;
float ax,ay;

void setup() {
  size(800,800,P3D);

  //Instance of HET_Recorder, refers to the calling applet.
  recorder=new HET_Recorder(this);
  render=new WB_Render(this);
}

void draw() {
  background(255);
  lights();
  translate(400,400);
  rotateX(map(mouseY,0,height,-PI,PI));
  rotateY(map(mouseX,0,width,-PI,PI));


  /* Similar to other Processing recorders, recording should be enabled
   * and disabled. typically this will be done with some kind of UI.
   * Here recording is enabled on the first frame. Later frames draw the
   * recorded meshes.
  */
  
  if (frameCount==1) {
    //Start recording geometry
    recorder.start();

    // TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS and QUADSTRIP are supported
    beginShape(QUAD_STRIP);
    for(int i=0;i<100;i++) {
      vertex((200-i)*cos(0.05*TWO_PI*i),2*i-180,(200-i)*sin(0.05*TWO_PI*i));
      vertex((200-i)*cos(0.05*TWO_PI*i),3*i-180,(200-i)*sin(0.05*TWO_PI*i));
    }
    endShape();
   
    // end recording of mesh and start new one.
    recorder.nextMesh();
    beginShape(QUAD_STRIP);
    for(int i=0;i<100;i++) {
      vertex((20+i)*cos(0.05*TWO_PI*i),2*i,(20+i)*sin(0.05*TWO_PI*i));
      vertex((20+i)*cos(0.05*TWO_PI*i),3*i,(20+i)*sin(0.05*TWO_PI*i));
    }
    endShape();
    
    recorder.nextMesh();
    beginShape(QUAD_STRIP);
    for(int i=0;i<100;i++) {
      vertex(80*cos(0.05*TWO_PI*i),6*i-300,80*sin(0.05*TWO_PI*i));
      vertex(80*cos(0.05*TWO_PI*i),6*i-320,80*sin(0.05*TWO_PI*i));
    }
    endShape();

    // end recording
    recorder.stop();

    // the recorder object stores all meshes as HE_Mesh. These meshes remain
    // available in the recorder object's scope.
    meshes=recorder.meshes;

    //after recording, the meshes can be modified like any HE_Mesh.
    /*
    for(int i=0;i<meshes.size();i++) {
      meshes.get(i).modify(new HEM_Shell().setThickness(10));
    meshes.get(i).smooth();
    }
    */
  }

  for(int i=0;i<meshes.size();i++) {
    noStroke();
    fill(255);
    render.drawFaces(meshes.getMesh(i));
    stroke(0);
   render.drawEdges(meshes.getMesh(i));
  }
}