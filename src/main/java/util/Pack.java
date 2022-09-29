package util;

import mc.Location;

public class Pack extends StreamArray{
	private int packId = -1;
	public Pack(int id) {
		this.packId = id;
		super.writeVarInt(id);
	}
	public Pack(StreamArray ar) throws Exception{
		super.build(ar);
		packId = readVarInt();
	}
	public Pack(byte[] ar) throws Exception{
		super.build(ar);
		packId = readVarInt();
	}
	public int getID() {
		return packId;
	}
	public Location readPos() throws Exception{
		long val = super.readLong();
		int _x,_y,_z;
		_x = (int)(val >> 38);
		_y = (int)(val & 0xfff);
		_z = (int)((val >> 12) & 0x3ffffff);
		if ((_x & 0x02000000) != 0) _x |= 0xfc000000;
		if ((_y & 0x800) != 0) _y |= 0xfffff000;
		if ((_z & 0x02000000) != 0) _z |= 0xfc000000;
		return new Location(_x,_y,_z);
	}
	public void writePos(int x,int y,int z) {
		long val = 0;
		val = (((long)x) & 0x3ffffff) << 38;
		val |= (((long)z) & 0x3ffffff) << 12;
		val |= y & 0xfff;
		writeLong(val);
	}
	public double readFixed() throws Exception {
		int val = readInt();
		double fv = val;
		fv /= 32.0;
		return fv;
	}
	public void writeFixed(double val) {
		int iv = (int) (val * 32);
		writeInt(iv);
	}

}
