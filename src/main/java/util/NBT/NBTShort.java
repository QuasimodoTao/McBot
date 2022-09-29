package util.NBT;

import util.StreamArray;

public class NBTShort extends NBT{
	public short val;
	public NBTShort(String name) {
		super(2, name);
	}
	public void read(StreamArray in) throws Exception {
		this.val = (short)in.readShort();
	}
	public String toString() {
		return "NBT_Short:" + name + ":" + val;
	}
}
