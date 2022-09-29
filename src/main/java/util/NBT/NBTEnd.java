package util.NBT;

import util.StreamArray;

public class NBTEnd extends NBT{
	public NBTEnd() {
		super(0, null);
	}
	public void read(StreamArray in) throws Exception {}
	public String toString() {
		return "NBT_End:";
	}
}