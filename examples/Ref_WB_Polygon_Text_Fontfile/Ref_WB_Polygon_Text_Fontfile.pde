import wblut.processing.*;
import wblut.hemesh.*;
import wblut.geom.*;
import wblut.math.*;
import java.util.List;

WB_GeometryFactory gf=WB_GeometryFactory.instance();
WB_Render2D render;
List<WB_Polygon> text;
WB_AABB AABB;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  render=new WB_Render2D(this);
  // http://osp.kitchen/foundry/belgica-belgika/
  // License: http://scripts.sil.org/OFL
  text=gf.createTextWithOpenTypeFont("hemesh 2014", sketchPath("Belgika-5th.otf"),0, 64.0,1.0);//text; font; style: REGULAR:0, BOLD:1, ITALIC:2, BOLD-ITALIC:3 ; font size; flatness
  
  //also createTextWithTTFFont
  //     createTextWithType1Font (untested)

  createAABB();

  background(255);
}

void draw() {
  background(255);
  translate(width/2, height/2);
  scale(1, -1);
  fill(255, 0, 0);
  translate(-AABB.getCenter().xf(), -AABB.getCenter().yf());
  for (WB_Polygon poly : text) {
    render.drawPolygon2D(poly);
  }
}


void createAABB() {
  AABB=new WB_AABB();
  for (WB_Polygon poly : text) {
    for (int i=0; i<poly.getNumberOfShellPoints(); i++) {
      AABB.expandToInclude(poly.getPoint(i));
    }
  }
}