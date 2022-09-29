package util.NBT;

import util.StreamArray;

public class NBTLongArray extends NBT{
	public long[] a;
	protected NBTLongArray(String name) {
		super(12, name);
	}
	@Override
	public void read(StreamArray in) throws Exception {
		int cnt = in.readInt();
		a = new long[cnt];
		for(int i = 0;i < cnt;i++) a[i] = in.readLong();
	}

}
