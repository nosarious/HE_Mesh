package wblut.math;

public interface WB_Noise {
	public void setSeed(long seed);

	public double value1D(double x);

	public double value2D(double x, double y);

	public double value3D(double x, double y, double z);

	public double value4D(double x, double y, double z, double w);

	public void setScale(double sx);

	public void setScale(double sx, double sy);

	public void setScale(double sx, double sy, double sz);

	public void setScale(double sx, double sy, double sz, double sw);
}
