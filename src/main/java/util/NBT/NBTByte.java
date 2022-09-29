package util.NBT;

import util.StreamArray;

public class NBTByte extends NBT{
	public byte val;
	public NBTByte(String name) {
		super(1,name);
	}
	public void read(StreamArray in) throws Exception {
		this.val = (byte)in.readByte();
	}
	public String toString() {
		return "NBT_Byte:" + name + ":" + val;
	}
}
