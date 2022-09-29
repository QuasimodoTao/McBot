package mcbot;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Inflater;

import javax.crypto.SecretKey;

import mc.Slot;
import util.Crypt;
import util.Pack;
import util.StreamArray;
import java.security.PublicKey;

class OutOfTimeException extends Exception{
	private static final long serialVersionUID = 1L;
}

public class Net {
	public static final int CONNECT_SUCCESS = 0;
	public static final int BAD_PROXY = 1;
	public static final int HOST_UNREACHABLE = 2;
	public static final int NET_FAIL = 3;
	
	private Socket sc = null;
	private AtomicBoolean lock = new AtomicBoolean(false);
	private boolean compressed = false;
	private volatile InputStream is = null;
	private volatile OutputStream os = null;
	public boolean encEnable = false;
	private PublicKey publicKey;
	private SecretKey secretKey;
	
	public Link link;
	
	public Net(Link link) {
		this.link = link;
	}
	public Net() {
		this.link = null;
	}
	public void setCompress(boolean p) {
		compressed = p;
	}
	private boolean readDataFromIS(byte [] data,int off,int len,int timeOut,boolean throw_) throws Exception{
		int sleepTime = 0;
		if(timeOut == 0) timeOut = 30000;
		while(len > 0) {
			int ret;
			try {
				ret = is.read(data,off,len);
			} catch(Exception e) {
				disconnect();
				throw e;
			}
			if(ret == 0) {
				Thread.sleep(50);
				sleepTime += 50;
				if(sleepTime >= 30000) {
					disconnect();
					if(throw_) throw new Exception("Out of time");
					return false;
				}
				continue;
			}
			else if(ret < 0) {
				disconnect();
				throw new Exception("Connect interrupt");
			}
			sleepTime = 0;
			len -= ret;
			off += ret;
		}
		return true;
	}
	private int handshakeSocks4(){
		InetAddress host;
		try {
			host = InetAddress.getByName(main.host);
		} catch (UnknownHostException e) {
			disconnect();
			return BAD_PROXY;
		}
		if(host == null) {
			disconnect();
			return BAD_PROXY;
		}
		byte [] data = new byte[32];
		byte [] ip = host.getAddress();
		if(ip == null || ip.length != 4) {
			disconnect();
			return BAD_PROXY;
		}
		data[0] = 0x04;
		data[1] = 0x01;
		data[2] = (byte)(main.port >> 8);
		data[3] = (byte)main.port;
		data[4] = ip[0];
		data[5] = ip[1];
		data[6] = ip[2];
		data[7] = ip[3];
		data[8] = 0;
		try {
			os.write(data,0,9);
			readDataFromIS(data,0,8,2000,true);
		} catch (Exception e) {
			return NET_FAIL;
		}
		if(data[0] == 0x00 && data[1] == 0x5a) return CONNECT_SUCCESS;
		disconnect();
		if(data[1] == 0x5c) return HOST_UNREACHABLE;
		return BAD_PROXY;
	}
	private int handshakeSocks5(){
		byte [] data = new byte[20 + main.host.length()];
		data[0] = 0x05;
		data[1] = 0x01;
		data[2] = 0x00;
		try {
			os.write(data,0,3);
		} catch (IOException e) {
			return NET_FAIL;
		}
		try {
			if(readDataFromIS(data,0,2,2000,false) == false) {
				link.isSocks5 = false;
				if(link.bind == false) {
					sc = new Socket();
					sc.setSoTimeout(1000);
					SocketAddress ss = new InetSocketAddress(link.proxyHost,link.proxyPort);
					sc.connect(ss,1000);
				}
				else {
					sc = new Socket();
					SocketAddress sa = new InetSocketAddress(link.bindIP,0);
					sc.bind(sa);
					SocketAddress ss = new InetSocketAddress(link.proxyHost,link.proxyPort);
					sc.connect(ss);
				}
				is = sc.getInputStream();
				os = sc.getOutputStream();
				return handshakeSocks4();
			}
		} catch (Exception e) {
			return BAD_PROXY;
		}
		if(data[0] != 0x05 || data[1] != 0x00) {
			disconnect();
			return BAD_PROXY;
		}
		data[0] = 0x05;
		data[1] = 0x01;
		data[2] = 0x00;
		data[3] = 0x03;
		data[4] = (byte)main.host.length();
		int i;
		for(i = 0;i < main.host.length();i++) 
			data[5 + i] = (byte)main.host.charAt(i);
		i += 5;
		data[i] = (byte)(main.port >> 8);
		data[i + 1] = (byte)main.port;
		i += 2;
		try {
			os.write(data,0,i);
			readDataFromIS(data,0,4,0,true);
			if(data[0] != 5 || data[1] != 0) {
				disconnect();
				if(data[1] == 0x03 || data[1] == 0x04 || data[1] == 0x05) 
					return HOST_UNREACHABLE;
			}
			if(data[3] == 1) {
				readDataFromIS(data,0,6,0,true);
			}
			else if(data[3] == 3) {
				readDataFromIS(data,0,1,0,true);
				int len = data[0];
				readDataFromIS(data,0,len + 2,0,true);
			}
			else if(data[3] == 4) {
				readDataFromIS(data,0,18,0,true);
			}
			else {
				disconnect();
				return BAD_PROXY;
			}
		} catch (Exception e) {
			return NET_FAIL;
		}
		return CONNECT_SUCCESS;
	}
	public int connect(){
		sc = new Socket();
		if(link == null) {
			try {
				SocketAddress ss = new InetSocketAddress(main.host,main.port);
				sc.connect(ss,main.CONNECT_TIME_OUT);
			} catch (UnknownHostException e) {
				return HOST_UNREACHABLE;
			} catch (IOException e) {
				return HOST_UNREACHABLE;
			}
		}
		else if(link.proxy == true) {
			if(link.bind == false) {
				try {
					sc.setSoTimeout(1000);
					SocketAddress ss = new InetSocketAddress(link.proxyHost,link.proxyPort);
					sc.connect(ss,main.CONNECT_TIME_OUT);
				} catch (Exception e) {
					return BAD_PROXY;
				}
			}
			else {
				try {
					SocketAddress sa = new InetSocketAddress(link.bindIP,0);
					sc.bind(sa);
					SocketAddress ss = new InetSocketAddress(link.proxyHost,link.proxyPort);
					sc.connect(ss,main.CONNECT_TIME_OUT);
				} catch (IOException e) {
					return BAD_PROXY;
				}
			}
			try {
				is = sc.getInputStream();
				os = sc.getOutputStream();
			} catch (IOException e) {
				return NET_FAIL;
			}
			if(link.isSocks5) return handshakeSocks5();
			else return handshakeSocks4();
		}
		else {
			if(link.bind == false) {
				try {
					SocketAddress ss = new InetSocketAddress(main.host,main.port);
					sc.connect(ss,main.CONNECT_TIME_OUT);
				} catch (UnknownHostException e) {
					return HOST_UNREACHABLE;
				} catch (IOException e) {
					return HOST_UNREACHABLE;
				}
			}
			else {
				try {
					SocketAddress sa = new InetSocketAddress(link.bindIP,0);
					sc.bind(sa);
					SocketAddress ss = new InetSocketAddress(main.host,main.port);
					sc.connect(ss,main.CONNECT_TIME_OUT);
				} catch (IOException e) {
					return HOST_UNREACHABLE;
				}
			}
		}
		try {
			sc.setSoTimeout(20000);
			is = sc.getInputStream();
			os = sc.getOutputStream();
		} catch (Exception e) {
			return NET_FAIL;
		}
		return CONNECT_SUCCESS;
	}
	public void disconnect(){
		while(lock.compareAndSet(false, true) == false)
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			is.close();
		} catch(Exception e) {}
		try {
			os.close();
		} catch(Exception e) {}
		try {
			sc.shutdownInput();
		} catch(Exception e) {}
		try {
			sc.shutdownOutput();
		} catch(Exception e) {}
		try {
			sc.close();
		} catch(Exception e) {}
		sc = null;
		lock.set(false);
	}
	public void close() {disconnect();}
	private int varint2int(byte[] var) {
		int i = 0;
		int res = 0;
		
		while(true) {
			int v = var[i];
			res |= (v & 0x7f) << (i * 7);
			if((v & 0x80) == 0) return res;
			i++;
		}
	}
	private int int2varint(int v,byte[] var) {
		int i = 0;
		var[0] = 0;
		while(v != 0) {
			byte by = (byte)(v & 0x7f);
			v >>= 7;
			if (v == 0) {
				var[i] = by;
				break;
			}
			var[i] = (byte)(by | 0x80);
			i++;
		}
		i++;
		return i;
	}
	public void enableEnc(PublicKey pub,SecretKey sec) throws IOException {
		encEnable = true;
		publicKey = pub;
		secretKey = sec;
		os.flush();
        os = new DataOutputStream(new BufferedOutputStream(Crypt.encryptOuputStream(secretKey, os), 5120));
        is = new DataInputStream(Crypt.decryptInputStream(secretKey, is));
	}
	
	
	public Pack recive() throws Exception{
		int len = 0;
		byte[] by = new byte[8];
		byte[] by2 = new byte[8];
		int ret;

		StreamArray rs = new StreamArray();
		int uncompressedLen = 0;
		
		try {
			while(true) {//read sizeof(data) + sizeof(uncommpressLen)(if exist)
				ret = is.read(by,len,1);
				if(ret < 0) {
					disconnect();
					throw new Exception("Connect interrupt");
				}
				if((by[len] & 0x80) == 0) break;
				len++;
			}
			len++;
			int packSize = varint2int(by);
			len = 0;
			if(compressed) {
				while(true) {//read uncompressed len
					ret = is.read(by2,len,1);
					if(ret < 0) {
						disconnect();
						throw new Exception("Connect interrupt");
					}
					if((by2[len] & 0x80) == 0) break;
					len++;
				}
				len++;
				uncompressedLen = varint2int(by2);
			}
			packSize -= len;
			byte da[] = new byte[512];
			while(packSize != 0) {//read data
				ret = is.read(da,0,packSize > 512 ? 512 : packSize);
				if(ret < 0) {
					disconnect();
					throw new Exception("Connect interrupt");
				}
				rs.writeArray(da,ret);
				packSize -= ret;
			}
		} catch(IOException e) {
			disconnect();
			throw e;
		}
		if(uncompressedLen == 0) return new Pack(rs);
		byte ar2[] = new byte[uncompressedLen];
		Inflater inflater = new Inflater();
		inflater.setInput(rs.getBuf());
		try {
			inflater.inflate(ar2);
		} catch (Exception e) {
			disconnect();
			throw e;
		}
		return new Pack(ar2);
	}

	public void send(Pack p) throws Exception{
		if(sc == null) throw new Exception("Try recive pack but not connected");
		
		byte varLen[] = new byte[8];
		int packSize = p.tellp();
		if(compressed) packSize += 1;
		int varLenLen = int2varint(packSize,varLen);
		if(compressed) {
			varLen[varLenLen] = 0;
			varLenLen++;
		}
		int totalSize = varLenLen;
		totalSize += p.tellp();
		byte [] t = new byte[totalSize];
		System.arraycopy(varLen, 0, t, 0, varLenLen);
		System.arraycopy(p.getBuf(), 0, t, varLenLen, p.tellp());
		synchronized(this) {
			os.write(t);
			os.flush();
		}
	}

	public void packOutHandshakeState() throws Exception{
		Pack p = new Pack(0);
		p.writeVarInt(758);
		p.writeString(main.host);
		p.writeShort(main.port);
		p.writeVarInt(1);
		send(p);
	}
	public void packOutRequest() throws Exception{
		Pack p = new Pack(0);
		send(p);
	}
	public void packOutHandshakeLogin() throws Exception{
		Pack p = new Pack(0);
		p.writeVarInt(758);
		p.writeString(main.host);
		p.writeShort(main.port);
		p.writeVarInt(2);
		send(p);
	}
	public void packOutLogin(String user) throws Exception{
		Pack p = new Pack(0);
		p.writeString(user);
		send(p);
	}
	public void packOutEncryptionResponse(PublicKey publicKey,byte[] verifyToken,SecretKey secretKey) throws Exception{
		Pack p = new Pack(1);
		
		byte[] sharedSecret = Crypt.encryptData(publicKey, secretKey.getEncoded());
        byte[] verifyToken2 = Crypt.encryptData(publicKey, verifyToken);
        p.writeVarInt(sharedSecret.length);
        p.write(sharedSecret);
        p.writeVarInt(verifyToken2.length);
        p.write(verifyToken2);
        send(p);
	}
	
	public void packOutTeleConfirm(int id) throws Exception {
		Pack p = new Pack(0);
		p.writeVarInt(id);
		send(p);
	}
	//void packOutQueryBlockNBT(int id,int x,int  y,int z){}
	//void packOutSetDifficult(){}
	public void packOutChat(String msg) throws Exception {
		Pack p = new Pack(3);
		p.writeString(msg);
		send(p);
		System.out.println("Send chat\"" + msg + "\".");
	}
	public void packOutClientStatus(int id) throws Exception{
		Pack p = new Pack(4);
		p.writeVarInt(id);
		send(p);
	}
	public void packOutClientSetting(int distance) throws Exception{
		Pack p = new Pack(5);
		p.writeString("en_GB");
		p.writeByte(distance);
		p.writeVarInt(0);
		p.writeByte(1);
		p.writeByte(127);
		p.writeVarInt(1);
		p.writeByte(1);
		p.writeByte(1);
		send(p);
	}
	//void packOutTabComplete(int id,String st){}
	public void packOutClickWinButton(int win,int bid) throws Exception{
		Pack p = new Pack(7);
		p.writeByte(win);
		p.writeByte(bid);
		send(p);
	}
	public void packOutClickWindow(int wid,int state,int slot,int button,
			int mode,int[] slist,Slot[] list,Slot clicked) throws Exception{
		if(slist.length != list.length) 
			throw new Exception("Length of slot ID list must equal to length of slot list");
		Pack p = new Pack(8);
		p.writeByte(wid);
		p.writeVarInt(state);
		p.writeShort(slot);
		p.writeByte(button);
		p.writeVarInt(mode);
		if(slist != null) {
			p.writeVarInt(slist.length);
			for(int i = 0;i < slist.length;i++) {
				p.writeShort(slist[i]);
				list[i].write(p);
			}
		}
		else p.writeVarInt(0);
		clicked.write(p);
		send(p);
	}
	public void packOutCloseWindow(int wid) throws Exception{
		Pack p = new Pack(9);
		p.writeByte(wid);
		send(p);
	}
	//void packOutPluginMessage(){}
	//void packOutEditBook(){}
	//void packOutQueryEntityNBT(){}
	public void packOutInteractEntity(int id,int type,float x,float y,float z,int hand,boolean sneaking)
		throws Exception{
		Pack p = new Pack(0x0d);
		p.writeVarInt(id);
		p.writeVarInt(type);
		if(type == 2) {
			p.writeFloat(x);
			p.writeFloat(y);
			p.writeFloat(z);
			p.writeVarInt(hand);
		}
		else if(type == 0) p.writeVarInt(hand);
		p.writeBoolean(sneaking);
		send(p);
	}
	//void packOutGenerateStructure(){}
	public void packOutKeepAlive(long id)throws Exception{
		Pack p = new Pack(0x0f);
		p.writeLong(id);
		send(p);
	}
	//public void packOutLockDifficult(){}
	public void packOutPlayLocation(double x,double y,double z,boolean onGround) throws Exception{
		Pack p = new Pack(0x11);
		p.writeDouble(x);
		p.writeDouble(y);
		p.writeDouble(z);
		p.writeBoolean(onGround);
		send(p);
	}
	public void packOutPlayPosRotation(double x,double y,double z,
			float yaw,float pitch,boolean onGround) throws Exception{
		Pack p = new Pack(0x12);
		p.writeDouble(x);
		p.writeDouble(y);
		p.writeDouble(z);
		p.writeFloat(yaw);
		p.writeFloat(pitch);
		p.writeBoolean(onGround);
		send(p);
	}
	public void packOutPlayRotation(float yaw,float pitch,boolean onGround) throws Exception{
		Pack p = new Pack(0x13);
		p.writeFloat(yaw);
		p.writeFloat(pitch);
		p.writeBoolean(onGround);
		send(p);
	}
	public void packOutPlayMovement(boolean onGround) throws Exception{
		Pack p = new Pack(0x14);
		p.writeBoolean(onGround);
		send(p);
	}
	//public void packOutVehicleMove(){}
	//public void packOutSteerBoat(){}
	//public void packOutPickItem(int slot) {}
	//public void packOutCraftRecipeRequest(){}
	//public void packOutPlayAbility(){}
	public void packOutDig(int lev,int x,int y,int z,int face) throws Exception {
		Pack p = new Pack(0x1a);
		p.writeVarInt(lev);
		p.writePos(x, y, z);
		p.writeByte(face);
		send(p);
	}
	//public void packOutAction(){}
	//public void packOutSteerVehicle(){}
	//public void packOutRecipeBooState(){}
	//public void packOutSetDisplayRecipe(){}
	//public void packOutNameItem(){}
	//public void packOutResourcePackStatus(){}
	//public void packOutAdvancementTab(){}
	public void packOutSelectTrade(int slot) throws Exception{
		Pack p = new Pack(0x23);
		p.writeVarInt(slot);
		send(p);
	}
	//public void packOutSetCeaconEffect(){}
	public void packOutHeldItemChange(int slot) throws Exception{
		Pack p = new Pack(0x25);
		if(slot < 0 || slot >= 9) throw new Exception("BUG: hold slot is invaild");
		p.writeShort(slot);
		send(p);
	}
	//public void packOuUpdateCommandBlockMinecraft(){}
	//public void packOutCreativeInventoryAction(){}
	//public void packOutJigsaw(){}
	//public void packOutUpdateStructureBlock(){}
	//public void packOutIpdateSign(){}
	public void packOutAnimation(int hand) throws Exception{
		Pack p = new Pack(0x2c);
		p.writeVarInt(hand);
		send(p);
	}
	//public void packOutSpectate(){}
	public void packOutPlaceBlock(int hand,int x,int y,int z,int face,
			float cx,float cy,float cz,boolean insideBlock) throws Exception{
		Pack p = new Pack(0x2e);
		p.writeVarInt(hand);
		p.writePos(x, y, z);
		p.writeVarInt(face);
		p.writeFloat(cx);
		p.writeFloat(cy);
		p.writeFloat(cz);
		p.writeBoolean(insideBlock);
		send(p);
	}
	public void packOutUseItem(int hand) throws Exception{
		Pack p = new Pack(0x2f);
		p.writeVarInt(hand);
		send(p);
	}
	
	
}
