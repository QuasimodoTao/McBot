package util;

public class BitString {
	private byte [] str = null;
	private int bitOff = 0;
	private int byteOff = 0;
	private int count = 0;
	public BitString(long[] s) {
		str = new byte[s.length * 8];
		count = s.length * 64;
		for(int i = 0;i < s.length;i++) {
			long val = s[i];
			for(int j = 0;j < 8;j++) {
				str[i * 8 + j] = (byte)(val & 0xff);
				val >>= 8;
			}
		}
	}
	public int getBits(int len) throws Exception{
		if(byteOff * 8 + bitOff + len > count) throw new Exception("Access overflow");
		int res = 0;
		for(int i = 0;i < len;i++) {
			res |= (str[byteOff] & (1 << bitOff)) == 0 ? 0  : (1 << i);
			bitOff++;
			if(bitOff >= 8) {
				byteOff++;
				bitOff = 0;
			}
		}
		return res;
	}
}
