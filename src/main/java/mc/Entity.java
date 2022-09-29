package mc;

public class Entity {
	public volatile double x;
	public volatile double y;
	public volatile double z;
	public volatile float yaw;
	public volatile float pitch;
	public volatile float headPitch;
	public int id;
	public int type;
	public int data;
	public volatile int vx,vy,vz;
	public String name = null;
	public volatile boolean updateVelo;
	public volatile boolean updatePos;
}
