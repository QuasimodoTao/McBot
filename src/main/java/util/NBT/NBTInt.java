package util.NBT;

import util.StreamArray;

public class NBTInt extends NBT{
	public int val;
	public NBTInt(String name) {
		super(3, name);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.val = par1DataInput.readInt();
	}
	public String toString() {
		return "NBT_Int:" + name + ":" + val;
	}
}
