package util.NBT;

import util.StreamArray;

public class NBTDouble extends NBT{
	public double a;
	  
	public NBTDouble(String par1Str) {
	    super(6, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.a = par1DataInput.readDouble();
	}
	public String toString() {
		return "NBT_Double:" + name + ":" + a;
	}
}
