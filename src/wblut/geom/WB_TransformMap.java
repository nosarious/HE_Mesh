package wblut.geom;

public class WB_TransformMap implements WB_Map {

	private WB_Transform T;
	private WB_Transform invT;

	public WB_TransformMap(WB_Transform transform) {
		T = transform.get();
		invT = transform.get();
		invT.inverse();
	}

	@Override
	public void mapPoint3D(WB_Coord p, WB_MutableCoord result) {
		T.applyAsPoint(p, result);

	}

	@Override
	public void mapPoint3D(double x, double y, double z, WB_MutableCoord result) {
		T.applyAsPoint(x, y, z, result);

	}

	@Override
	public void unmapPoint3D(WB_Coord p, WB_MutableCoord result) {
		invT.applyAsPoint(p, result);

	}

	@Override
	public void unmapPoint3D(double u, double v, double w, WB_MutableCoord result) {
		invT.applyAsPoint(u, v, w, result);

	}

	@Override
	public void mapVector3D(WB_Coord p, WB_MutableCoord result) {
		T.applyAsVector(p, result);

	}

	@Override
	public void mapVector3D(double x, double y, double z, WB_MutableCoord result) {
		T.applyAsVector(x, y, z, result);

	}

	@Override
	public void unmapVector3D(WB_Coord p, WB_MutableCoord result) {
		invT.applyAsVector(p, result);

	}

	@Override
	public void unmapVector3D(double u, double v, double w, WB_MutableCoord result) {
		invT.applyAsVector(u, v, w, result);

	}

}
