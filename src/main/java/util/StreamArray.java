package util;

import java.io.UnsupportedEncodingException;

public class StreamArray {
	private byte buf[] = null;
	int dataLen = 0;
	private int readIndex = 0;
	private int writeIndex = 0;
	private static final int INC_SCALE = 4096;
	
	protected void set(StreamArray a) {
		if(a == null) return;
		if(a.buf == null) return;
		this.buf = new byte[a.buf.length];
		System.arraycopy(a.buf, 0, this.buf, 0, a.buf.length);
		this.readIndex = a.readIndex;
		this.writeIndex = a.writeIndex;
		this.dataLen = a.dataLen;
	}
	public void build(StreamArray a) {
		if(a == null) return;
		if(a.buf == null) return;
		this.buf = a.buf;
		this.readIndex = a.readIndex;
		this.writeIndex = a.writeIndex;
		this.dataLen = a.dataLen;
	}
	public void build(byte[] a) {
		if(a == null) return;
		this.buf = a;
		dataLen = a.length;
	}
	
	public byte[] getBuf() {
		return buf;
	}
	public StreamArray(StreamArray a) {
		set(a);
	}
	public StreamArray() {
		
	}
	public StreamArray(byte b[]) {
		if(b == null) return;
		int len = b.length;
		int rem = len % INC_SCALE;
		if(rem != 0) len += INC_SCALE - rem;
		this.buf = new byte[len];
		dataLen = b.length;
		System.arraycopy(b, 0, this.buf, 0, b.length);		
	}
	public void seekg(int val) {
		if(val >= this.buf.length) this.readIndex = this.buf.length;
		else this.readIndex = val;
	}
	public int tellg() {
		return this.readIndex;
	}
	public void seekp(int val) {
		if(val >= this.buf.length) this.writeIndex = this.buf.length;
		else this.writeIndex = val;
	}
	public int tellp() {
		return this.writeIndex;
	}
	public boolean readBoolean() throws Exception{
		int val;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex >= dataLen) throw new Exception("Access overflow");
		val = buf[readIndex];
		val &= 0xff;
		readIndex++;
		return val == 0 ? false : true;
	}
	public int readUByte() throws Exception{
		int val;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex >= dataLen) throw new Exception("Access overflow");
		val = buf[readIndex];
		val &= 0xff;
		readIndex++;
		return val;
	}
	public int readByte() throws Exception{
		int val;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex >= dataLen) throw new Exception("Access overflow");
		val = buf[readIndex];
		val &= 0xff;
		readIndex++;
		return val;
	}
	public int readUShort() throws Exception{
		int val0,val1;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex + 1 >= dataLen) throw new Exception("Access overflow");
		val1 = buf[readIndex];
		val1 <<= 8;
		readIndex++;
		val0 = buf[readIndex];
		readIndex++;
		val0 &= 0xff;
		val1 |= val0;
		val1 &= 0xffff;
		return val1;
	}
	public int readShort() throws Exception{
		int val0,val1;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex + 1 >= dataLen) throw new Exception("Access overflow");
		val1 = buf[readIndex];
		val1 <<= 8;
		readIndex++;
		val0 = buf[readIndex];
		readIndex++;
		val0 &= 0xff;
		val1 |= val0;
		return val1;
	}
	public int readInt() throws Exception{
		int val0,val1,val2,val3;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex + 3 >= dataLen) throw new Exception("Access overflow");
		val3 = buf[readIndex];
		val3 <<= 24;
		readIndex++;
		
		val2 = buf[readIndex];
		val2 &= 0xff;
		val2 <<= 16;
		readIndex++;
		
		val1 = buf[readIndex];
		val1 &= 0xff;
		val1 <<= 8;
		readIndex++;
		
		val0 = buf[readIndex];
		readIndex++;
		val0 &= 0xff;
		return val0 | val1 | val2 | val3;
	}
	public long readLong() throws Exception{
		long val0,val1,val2,val3,val4,val5,val6,val7;
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex + 7 >= dataLen) throw new Exception("Access overflow");
		val7 = buf[readIndex];
		val7 <<= 56;
		readIndex++;
		val6 = buf[readIndex];
		val6 &= 0xff;
		val6 <<= 48;
		readIndex++;
		val5 = buf[readIndex];
		val5 &= 0xff;
		val5 <<= 40;
		readIndex++;
		val4 = buf[readIndex];
		val4 &= 0xff;
		val4 <<= 32;
		readIndex++;
		val3 = buf[readIndex];
		val3 &= 0xff;
		val3 <<= 24;
		readIndex++;
		val2 = buf[readIndex];
		val2 &= 0xff;
		val2 <<= 16;
		readIndex++;
		val1 = buf[readIndex];
		val1 &= 0xff;
		val1 <<= 8;
		readIndex++;
		val0 = buf[readIndex];
		readIndex++;
		val0 &= 0xff;
		return val0 | val1 | val2 | val3 | val4 | val5 | val6 | val7;
	}
	public void writeBoolean(boolean val) {
		if(buf == null) {
			buf = new byte[INC_SCALE];
		}
		else if(writeIndex >= buf.length) {
			byte b[] = new byte[INC_SCALE + buf.length];
			System.arraycopy(buf, 0, b, 0, buf.length);
			buf = b;
		}
		buf[writeIndex] = (byte)(val ? 1 : 0);
		writeIndex++;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public void writeByte(int val) {
		if(buf == null) {
			buf = new byte[INC_SCALE];
		}
		else if(writeIndex >= buf.length) {
			byte b[] = new byte[INC_SCALE + buf.length];
			System.arraycopy(buf, 0, b, 0, buf.length);
			buf = b;
		}
		buf[writeIndex] = (byte)val;
		writeIndex++;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public void writeShort(int val) {
		if(buf == null) {
			buf = new byte[INC_SCALE];
		}
		else if(writeIndex + 1 >= buf.length) {
			byte b[] = new byte[INC_SCALE + buf.length];
			System.arraycopy(buf, 0, b, 0, buf.length);
			buf = b;
		}
		buf[writeIndex + 1] = (byte)val;
		buf[writeIndex] = (byte)(val >> 8);
		writeIndex += 2;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public void writeInt(int val) {
		if(buf == null) {
			buf = new byte[INC_SCALE];
		}
		else if(writeIndex + 3 >= buf.length) {
			byte b[] = new byte[INC_SCALE + buf.length];
			System.arraycopy(buf, 0, b, 0, buf.length);
			buf = b;
		}
		buf[writeIndex + 3] = (byte)val;
		buf[writeIndex + 2] = (byte)(val >> 8);
		buf[writeIndex + 1] = (byte)(val >> 16);
		buf[writeIndex + 0] = (byte)(val >> 24);
		writeIndex += 4;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public void writeLong(long val) {
		if(buf == null) {
			buf = new byte[INC_SCALE];
		}
		else if(writeIndex + 3 >= buf.length) {
			byte b[] = new byte[INC_SCALE + buf.length];
			System.arraycopy(buf, 0, b, 0, buf.length);
			buf = b;
		}
		buf[writeIndex + 7] = (byte)val;
		buf[writeIndex + 6] = (byte)(val >> 8);
		buf[writeIndex + 5] = (byte)(val >> 16);
		buf[writeIndex + 4] = (byte)(val >> 24);
		buf[writeIndex + 3] = (byte)(val >> 32);
		buf[writeIndex + 2] = (byte)(val >> 40);
		buf[writeIndex + 1] = (byte)(val >> 48);
		buf[writeIndex + 0] = (byte)(val >> 56);
		writeIndex += 8;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public float readFloat() throws Exception{
		return Float.intBitsToFloat(readInt());
	}
	public double readDouble() throws Exception{
		return Double.longBitsToDouble(readLong());
	}
	public void writeFloat(float val) {
		writeInt(Float.floatToRawIntBits(val));
	}
	public void writeDouble(double val) {
		writeLong(Double.doubleToLongBits(val));
	}
	public void readArray(byte b[]) throws Exception{
		if(buf == null) throw new Exception("Access overflow");
		if(readIndex + b.length > buf.length) throw new Exception("Access overflow");
		System.arraycopy(buf, readIndex, b, 0, b.length);
		readIndex += b.length;
	}
	public void readArray(byte b[], int len) throws Exception{
		if(buf == null) throw new Exception("Access overflow");
		if(len > b.length) len = b.length;
		if(readIndex + len > buf.length) throw new Exception("Access overflow");
		System.arraycopy(buf, readIndex, b, 0, len);
		readIndex += len;
	}
	public void writeArray(byte b[]) {
		if(buf == null) {
			int len = b.length;
			int rem = len % INC_SCALE;
			if(rem != 0) len += INC_SCALE - rem;
			buf = new byte[len];
		}
		else if(writeIndex + b.length >= buf.length) {
			int len = buf.length + b.length;
			int rem = len % INC_SCALE;
			if(rem != 0) len += INC_SCALE - rem;
			byte b2[] = new byte[len];
			System.arraycopy(buf, 0, b2, 0, buf.length);
			buf = b2;
		}
		System.arraycopy(b, 0, buf, writeIndex, b.length);
		writeIndex += b.length;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public void writeArray(byte b[], int len2) {
		if(b == null) return;
		if(len2 > b.length) len2 = b.length;
		if(buf == null) {
			int len = len2;
			int rem = len % INC_SCALE;
			if(rem != 0) len = len + INC_SCALE - rem;
			buf = new byte[len];
		}
		else if(writeIndex + len2 >= buf.length) {
			int len = buf.length + len2;
			int rem = len % INC_SCALE;
			if(rem != 0) len = len + INC_SCALE - rem;
			byte b2[] = new byte[len];
			System.arraycopy(buf, 0, b2, 0, buf.length);
			buf = b2;
		}
		try {
			System.arraycopy(b, 0, buf, writeIndex, len2);
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		writeIndex += len2;
		if(dataLen < writeIndex) dataLen = writeIndex;
	}
	public int readVarInt() throws Exception{
		int val = 0;
		int shift = 0;
		while(true) {
			int by = readUByte();
			val |= (by & 0x7f) << (shift * 7);
			if((by & 0x80) == 0) return val;
			shift++;
		}
	}
	public long readVarLong() throws Exception{
		long val = 0;
		int shift = 0;
		while(true) {
			int by = readUByte();
			val |= (by & 0x7f) << (shift * 7);
			if((by & 0x80) == 0) return val;
			shift++;
		}
	}
	public void writeVarInt(int val) {
		byte ar[] = new byte[5];
		int c = 0;
		int by;
		ar[0] = 0;
		while(val != 0) {
			by = val & 0x7f;
			val >>= 7;
			if(val == 0) {
				ar[c] = (byte)by;
				break;
			}
			ar[c] = (byte)(by | 0x80);
			c++;
		}
		c++;
		writeArray(ar,c);
	}
	public void writeVarLong(long val) {
		byte ar[] = new byte[12];
		int c = 0;
		long by;
		ar[0] = 0;
		while(val != 0) {
			by = val & 0x7f;
			val >>= 7;
			if(val == 0) {
				ar[c] = (byte)by;
				break;
			}
			ar[c] = (byte)(by | 0x80);
			c++;
		}
		c++;
		writeArray(ar,c);
	}
	public String readString() throws Exception{
		int len;
		
		len = readVarInt();
		if(len == 0) return new String();
		byte[] ar = new byte[len];
		readArray(ar);
		return new String(ar,"UTF-8");
	}
	public void writeString(String val) {
		byte[] ar;
		try {
			ar = val.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			ar = new String("Unsuppoer char.").getBytes();
		}
		writeVarInt(ar.length);
		writeArray(ar);
	}
	public String readShortString() throws Exception{
		int len = readUShort();
		if(len == 0) return new String();
		byte[] ar = new byte[len];
		readArray(ar);
		return new String(ar,"UTF-8");
	}
	public void writeShortString(String val) {
		byte[] ar;
		try {
			ar = val.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			ar = new String("Unsuppoer char.").getBytes();
		}
		writeShort(ar.length);
		writeArray(ar);
	}
	public void write(byte[] a) {
		this.writeArray(a);
	}
	public void readFully(byte[] a) throws Exception {
		this.readArray(a);
	}
}
