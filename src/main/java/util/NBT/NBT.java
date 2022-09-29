package util.NBT;

import util.StreamArray;

public abstract class NBT {
	public String name = null;
	public byte type;
	
	public byte getType() {
		return this.type;
	}
	
	protected NBT(int type,String name) {
		this.type = (byte)type;
		if(name == null) this.name = "";
		else this.name = name;
	}
	
	public NBT setName(String name) {
		if(name == null) this.name = "";
		else this.name = name;
		return this;
	}
	
	public String getName() {
		if(this.name == null) return "";
		else return this.name;
	}
	public abstract void read(StreamArray in) throws Exception;
	public static final NBT generalNBT(byte par0, String par1Str) {
	    switch (par0) {
	      case 0:
	        return new NBTEnd();
	      case 1:
	        return new NBTByte(par1Str);
	      case 2:
	        return new NBTShort(par1Str);
	      case 3:
	        return new NBTInt(par1Str);
	      case 4:
	        return new NBTLong(par1Str);
	      case 5:
	        return new NBTFloat(par1Str);
	      case 6:
	        return new NBTDouble(par1Str);
	      case 7:
	        return new NBTByteArray(par1Str);
	      case 8:
	        return new NBTString(par1Str);
	      case 9:
	        return new NBTList(par1Str);
	      case 10:
	        return new NBTComp(par1Str);
	      case 11:
	        return new NBTIntArray(par1Str);
	      case 12:
	    	  return new NBTLongArray(par1Str);
	    } 
	    return null;
	  }
	public static final void a(NBT par0NBTBase, StreamArray par1DataOutput) throws Exception {
	    par1DataOutput.writeByte(par0NBTBase.type);
	    if (par0NBTBase.type != 0) {
	      par1DataOutput.writeShortString(par0NBTBase.getName());
	      par0NBTBase.write(par1DataOutput);
	    }
	}
	public static final NBT b(StreamArray par0DataInput) throws Exception {
		byte var2 = (byte) par0DataInput.readByte();
		if (var2 == 0) return new NBTEnd(); 
		String var3 = par0DataInput.readShortString();
		NBT var4 = generalNBT(var2, var3);
		if (var4 == null) {
			throw new Exception("Bad nbt type:" + var2);
		} 
		try {
		    var4.read(par0DataInput);
		    return var4;
		} catch (Exception var8) {
		    return null;
		} 
	}
	private byte[] data = null;
	public void write(StreamArray out) {
		if(this.data == null) return;
		out.write(this.data);
	}
	public NBT dup() throws Exception {
		if(this.data == null) return null;
		StreamArray b = new StreamArray(this.data);
		return NBT.parseNBT(b);
	}
	public boolean isEqual(NBT n) {
		if(this == n) return true;
		if(this.data == null) {
			if(n.data == null) return true;
			else return false;
		}
		else {
			if(n.data == null) return false;
			if(this.data.length != n.data.length) return false;
			for(int i = 0;i < this.data.length;i++) {
				if(this.data[i] != n.data[i]) return false;
			}
			return true;
		}
	}
	public static final NBT parseNBT(StreamArray par0DataInput) throws Exception {
		int s = par0DataInput.tellg();
		byte var2 = (byte) par0DataInput.readByte();
		if (var2 == 0) {
			NBT res = new NBTEnd();
			int en = par0DataInput.tellg();
			res.data = new byte[en - s];
			par0DataInput.seekg(s);
			par0DataInput.readArray(res.data, en - s);
			return res; 
		}
		String var3 = par0DataInput.readShortString();
		NBT var4 = generalNBT(var2, var3);
		if (var4 == null) {
			throw new Exception("Bad nbt type:" + var2);
		}
		try {
		    var4.read(par0DataInput);
		    int en = par0DataInput.tellg();
		    var4.data = new byte[en - s];
			par0DataInput.seekg(s);
			par0DataInput.readArray(var4.data, en - s);
		    return var4;
		} catch (Exception var8) {
		    return null;
		} 
	}
}
