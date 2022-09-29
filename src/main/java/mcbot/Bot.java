package mcbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKey;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;

import mc.Enchantment;
import mc.Entity;
import mc.Face;
import mc.Hand;
import mc.Location;
import mc.MinecraftBlock;
import mc.MinecraftEntity;
import mc.MinecraftEntityID;
import mc.MinecraftItemID;
import mc.MinecraftTool;
import mc.MinecraftWorld;
import mc.Slot;
import mc.World;
import util.Pack;
import util.Event;
import util.Crypt;

public class Bot extends Thread{
	class PackInPing{
		public String json;
		PackInPing(Pack  p) throws Exception{
			if(p.getID() != 0x00) 
				throw new Exception("Bad pack type");
			json = p.readString();
		}
	}
	class PackInCompress{
		public int size;
		public PackInCompress(Pack p) throws Exception {
			if(p.getID() != 0x03)
				throw new Exception("Bad pack type");
			size = p.readVarInt();
		}
	}
	class PackInDisconnect{
		public String reason;
		public PackInDisconnect(Pack p) throws Exception{
			if(p.getID() != 0x00 && p.getID() != 0x1a) 
				throw new Exception("Bad pack type");
			reason = p.readString();
		}
	}
	class PackInLoginSuccess{
		public String name;
		public PackInLoginSuccess(Pack p) throws Exception{
			if(p.getID() != 0x02)
				throw new Exception("Bad pack type");
			p.readLong();
			p.readLong();
			name = p.readString();
		}
	}
	class PackInEncryptionRequest{
		public String serverID;
		public PublicKey publicKey;
		public byte[] verifyToken;
		public PackInEncryptionRequest(Pack p) throws Exception {
			if(p.getID() != 0x01)
				throw new Exception("Bad pack type");
			serverID = p.readString();
			int len = p.readVarInt();
			byte[] pub = new byte[len];
			p.readArray(pub);
			publicKey = Crypt.decodePublicKey(pub);
			len = p.readVarInt();
			verifyToken = new byte[len];
			p.readArray(verifyToken);
		}
	}
	
	public User user;
	public String[]admin;
	public Net net = null;
	public Item item = null;
	public World world = new World();
	public boolean worldLoadEnable = false;
	public Chat chat = null;
	public Auth auth = null;
	public volatile long prevUpdateTime = 0;
	public volatile long worldAge = 0;
	public volatile int tickTime = 50;
	
	private Entity[] entityList;
	public volatile float health;
	public volatile int food;
	public volatile float saturation;
	public volatile boolean healthUpdate = false;
	public int selfID;
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public Event posUpdate = new Event();
	public Thread mainThread = null;

	private static Thread pingThread = null;
	class Keepalive extends Thread{
		public volatile long prevTime;
		public void run() {
			prevTime = System.currentTimeMillis();
			try {
				while(true) {
					sleep(2000);
					main.vaildLink(net.link);
					if(System.currentTimeMillis() - prevTime > 30000) {
						net.close();
						return;
					}
				}
			} catch (InterruptedException e) {}
		}
	}
	public Keepalive keepalive = new Keepalive();
	
	public volatile int fishFloatID;
	
	private void cleanResource() {
		int i;
		for (i = 0; i < entityList.length; i++) entityList[i] = null;
		world.respawn(MinecraftWorld.mwUndef);
		for(i = 0;i < digLocation.length;i++) digLocation[i] = null;
		prevUpdateTime = 0;
		worldAge = 0;
		tickTime = 50;
	}
	
	private static AtomicInteger online = new AtomicInteger(0);
	private static synchronized boolean tryOffline(boolean isOnline) {
		if(main.MAX_ONLINE == -1) return isOnline ? false : true;
		int _online = online.get();
		if(_online <= 0) return isOnline ? false : true;
		if(isOnline) {
			if(_online > (main.MAX_ONLINE + 3)) {
				online.decrementAndGet();
				return true;
			}
		}
		else {
			if(_online < (main.MAX_ONLINE)) {
				online.incrementAndGet();
				return true;
			}
		}
		return false;
	}
	
	private static volatile boolean serverOnline = true;
	public static void pingStart() {
		pingThread = new Thread(()->{
			try {
				while(true) {
					Net n = new Net();
					int ret = n.connect();
					if(ret == Net.HOST_UNREACHABLE) {
						serverOnline = false;
						sleep(10000);
					}
					else {
						try {
							n.packOutHandshakeState();
							n.packOutRequest();
							Pack p = n.recive();
							String js = p.readString();
							JSONObject json = JSONObject.parseObject(js);
							if(json.containsKey("players") == false) online.set(0);
							else if(json.getJSONObject("players").containsKey("online") == false) online.set(0);
							else {
								online.set(json.getJSONObject("players").getInteger("online"));
								System.out.println("Online player(s) " + json.getJSONObject("players").getInteger("online"));
							}
						} catch (Exception e) {
							n.disconnect();
							sleep(5000);
							continue;
						}
						n.disconnect();
						serverOnline = true;
						sleep(60 * 1000 * 5);
					}
				}
			} catch (InterruptedException e1) {}
		});
		pingThread.start();
	}
	public static void pingStop() {
		pingThread.interrupt();
	}
		
 	public Bot(User user,String admin[]) {
		this.user = user;
		this.admin = admin;
		if(user.json != null &&
			user.json.containsKey("worldLoadEnable") &&
			user.json.getBooleanValue("worldLoadEnable") == true) worldLoadEnable = true;
		entityList = new Entity[256];
		for(int i = 0;i < 256;i++) entityList[i] = null;
	}
	public void close() throws InterruptedException {
		if(this.net != null) this.net.close();
	}
	public void onlineCommand(String user,String cmd) {
		try {
			net.packOutChat(cmd);
		} catch (Exception e) {
		}
	}
	public Link getLink() {
		if(net == null) return null;
		return net.link;
	}

	public void showMessage(String str) {
		System.out.println(user.name + ":" + str);
	}
	
	private static final int CONNECT_SUCCESS = 0;
	private static final int CONNECT_FAIL = 1;
	private static final int CONNECT_RETRY = 2;
	private int netConnect(Link link) throws InterruptedException {
		net = new Net(link);
		int ret = net.connect();
		if(ret == Net.CONNECT_SUCCESS) return CONNECT_SUCCESS;
		if(ret != Net.HOST_UNREACHABLE) return CONNECT_FAIL;
		if(serverOnline == false) {
			while(serverOnline == false) sleep(1000);
			return CONNECT_RETRY;
		}
		else {
			sleep(30*1000);
			net = null;
			if(serverOnline == false) {
				while(serverOnline == false) sleep(1000);
				return CONNECT_RETRY;
			}
			else return CONNECT_FAIL;
		}
	}
	
	private String urlEncode(String par0Str) throws IOException {
        return URLEncoder.encode(par0Str, "UTF-8");
    }
	
	private void sendSessionRequest(String user, String session, String serverid) throws IOException {
    	String url = "http://session.minecraft.net/game/joinserver.jsp"
                    + "?user=" + urlEncode(user)
                    + "&sessionId=" + urlEncode(session)
                    + "&serverId=" + urlEncode(serverid);
    	URL var4 = new URL(url);
        BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.openStream()));
        var5.readLine();
        var5.close();
    }
	
	private static final int HANDSHAKE_SUCCESS = 0;
	private static final int HANDSHAKE_CONNECT_FAIL = 1;
	private static final int HANDSHAKE_RETRY = 2;
	private int sleepTime = 10*1000;
	
	private int handshake(Link link) throws InterruptedException {
		try {
			sleep(200);
			net.packOutHandshakeLogin();
			sleep(200);
			net.packOutLogin(user.UID);
		} catch (Exception e2) {
			main.invaildNet(link);
			net.close();
			return HANDSHAKE_CONNECT_FAIL;
		}
		Pack p;
		try {
			while(true) {
				p = net.recive();
				if(p.getID() == 0) {
					PackInDisconnect pc = new PackInDisconnect(p);
					System.out.println("Handshake faile.");
					System.out.println(pc.reason);
					net.close();
					if(pc.reason.contains("登录上限")) return HANDSHAKE_CONNECT_FAIL;
					if(pc.reason.contains("速度")) {
						sleepTime += 5*1000;
						sleep(sleepTime);
						return HANDSHAKE_RETRY;
					}
					sleep(10*1000);
					return HANDSHAKE_RETRY;
				}
				else if(p.getID() == 0x01) {
					PackInEncryptionRequest pe = new PackInEncryptionRequest(p);
			        SecretKey secretKey = Crypt.createNewSharedKey();

			        byte[] serverIdHash = Crypt.getServerIdHash(pe.serverID.trim(), pe.publicKey, secretKey);
			        if(serverIdHash == null) {
			        	net.close();
			        	return HANDSHAKE_CONNECT_FAIL;
			        }
			        String var5 = new BigInteger(serverIdHash).toString(16);
			        sendSessionRequest(user.UID,"token:" + accessToken + ":" + profile, var5);
			        
			        net.packOutEncryptionResponse(pe.publicKey, pe.verifyToken, secretKey);
			        net.enableEnc(pe.publicKey, secretKey);
				}
				else if(p.getID() == 0x02) {
					sleepTime = 10*1000;
					return HANDSHAKE_SUCCESS;
				}
				else if(p.getID() == 0x03) {
					PackInCompress ps = new PackInCompress(p);
					if(ps.size > 0) net.setCompress(true);
				}
				else {
					System.out.println("Unknow type of pack:" + p.getID());
					
					
				}
			}
		} catch(Exception e) {
			net.close();
			return HANDSHAKE_CONNECT_FAIL;
		}
	}
	
	private static final int PREV_PLAY_SUCCESS = 0;
	private static final int PREV_PLAY_NET_FAIL = 1;
	private static final int PREV_PLAY_RETRY = 2;
	private int prevPlay(Link link) {
		Pack p;
		try {
			//long curTime = System.currentTimeMillis();
			p = net.recive();
			net.packOutClientSetting(4);
			//while(true) {
				int id = p.getID();
				if(id >= PackLoop.event.length) {
					System.out.println("BUG:Event receive class is lost");
					System.out.println("Pack ID:" + id + ", total " + p.tellp() + " byte(s). Ignore.");
				}
				else if(id == 0x1a) {
					net.close();
					PackInDisconnect pc = new PackInDisconnect(p);
					System.out.println(user.name + " offline.");
					if(pc.reason.contains("登录上限")) return PREV_PLAY_NET_FAIL;
					sleep(10*1000);
					return PREV_PLAY_RETRY;
				}
				else if(PackLoop.event[id] != null) PackLoop.event[id].event(Bot.this, p);
				return PREV_PLAY_SUCCESS;
				//if(System.currentTimeMillis() - curTime > main.PLAY_WAIT_TIME) {
					//if(chat.loginEvent.getEvent() == true) return PREV_PLAY_SUCCESS;
					//net.close();
					//return PREV_PLAY_NET_FAIL;
				//}
				//p = net.recive();
			//}
		} catch(Exception e) {
			net.close();
			main.invaildNet(link);
			return PREV_PLAY_NET_FAIL;
		}
	}
	
	private static String pas = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`~!@#$%^&*()_+-=[]{}|\\;:'\"/?,.<>";
	
	private String accessToken = null;
    private String clientToken = null;
    private String profile = null;
    private volatile boolean authSuccess = false;
	public boolean authenticate() {
    	MsaAuthenticationService authService = new MsaAuthenticationService("fef9faea-d962-4476-9ce7-4960c8baa946");
        authService.setUsername(user.name);
        authService.setPassword(user.onlinePassword);
        try {
        	authService.login();
            GameProfile profile = authService.getSelectedProfile();
            accessToken = authService.getAccessToken();
            clientToken = authService.getClientToken();
            this.profile = profile == null ? null : profile.getIdAsString().replace("-", "");
            user.UID = authService.getUsername();
            if(this.profile == null) {
            	return false;
            }
            return true;
        } catch (RequestException e) {
        	return false;
        }
    }
	
	class AuthThread extends Thread{
		private Bot bot;
		public AuthThread(Bot bot) {
			this.bot = bot;
		}
		public void run() {
			try {
				while(main.ONLINE_MODE == true) {
					authSuccess = false;
					if(bot.authenticate() == false) {
						System.out.println("Authenticate " + user.name + " faile.");
							sleep(10000);
					}
					else {
						authSuccess = true;
						sleep(2*60*60*1000);
					}
				}
			} catch (InterruptedException e) {
				return;
			}
		}
		
	}
	
	private AuthThread authThread = null;
	
	public void run() {
		int ret;
		Link link = null;
		boolean keepOnline = false;
		if(user.json != null && user.json.containsKey("keepOnline")) keepOnline = user.json.getBooleanValue("keepOnline");
		try {
			user.UID = user.name;
			authThread = new AuthThread(this);
			authThread.start();
			if(main.ONLINE_MODE == true) while(authSuccess == false) sleep(1000);
			System.out.println("Authenticate " + user.name + " success.");
			while(true) {
				if(this.isInterrupted()) return;
				if(link == null) {
					link = main.allocNet();
					if(link == null) return;
				}
				int loop = 0;
				synchronized(link) {
					if((keepOnline == false) && (tryOffline(false) == false)) {
						sleep(100);
						continue;
					}
					while(loop < 16) {
						loop++;
						ret = netConnect(link);
						if(this.isInterrupted()) return;
						if(ret == CONNECT_SUCCESS) break;
						else if(ret == CONNECT_FAIL) {
							link = null;
							break;
						}
					}
					if(link == null) continue;
					if(loop >= 16) {
						main.invaildNet(link);
						link = null;
						continue;
					}
					cleanResource();
					item = new Item(this);
					chat = new Chat(this);
					ret = handshake(link);
					if(this.isInterrupted()) return;
					if(ret == HANDSHAKE_RETRY) continue;
					else if(ret == HANDSHAKE_CONNECT_FAIL) {
						link = null;
						continue;
					}
					String out = "Try login " + user.name;
					if(link.proxy) out += " from proxy " + link.proxyHost + ":" + link.proxyPort + ".";
					System.out.println(out);
					if(user.work.compareTo("fishing") == 0) {
						mainThread = new FishMan(this);
					}
					else if(user.work.compareTo("fighting") == 0) {
						mainThread = new Thread(()->{
							try {
								sleep(500);
								while(true) {
									attackMonster();
									sleep(100);
									if(food < 20) {
										int fs = item.findOutFastFood();
										if(fs < 0) continue;
										item.holdItemChange(fs);
										eat(Hand.hMain);
									}
								}
							} catch (Exception e) {
								return;
							}
						});
					}
					else if(user.work.compareTo("trade") == 0) {
						mainThread = new Thread(()->{
							try {
								glassMain();
							} catch (InterruptedException e) {
								return;
							} catch (Exception e) {
								return;
							}
						});
					}
					else if(user.work.compareTo("getEmerald") == 0) {
						mainThread = new Thread(()->{
							try {
								sleep(500);
								while(true) {
									if(user.json.containsKey("onRespawn")) 
										chat.delayableCommand(user.json.getString("onRespawn"), -1);
									else chat.tpCommand("24e", -1);
									int noMonsterTime = 0;
									while(true) {
										int ret2 = attackMonster();
										if(ret2 == 0) {
											sleep(500);
											noMonsterTime += 500;
											if(noMonsterTime >= 60*1000) break;
										}
										else noMonsterTime = 0;
										if(this.isInterrupted()) return;
									}
									chat.tpCommand("qsz",-1);
									long startTime = System.currentTimeMillis();
									while(true) {
										if(this.isInterrupted()) return;
										attackMonster();
										if(System.currentTimeMillis() - startTime > 50*1000) break;
									}
								}
							} catch (Exception e) {}
						});
					}
					else if(user.work.compareTo("map") == 0) {
						mainThread = new MatrixMan(mainThread, this);
					}
					else if(user.work.compareTo("lumberer") == 0) {
						mainThread = new Lumberer(mainThread, this);
					}
					else if(user.work.compareTo("carrot") == 0) {
						mainThread = new CarrotsBot(mainThread,this);
					}
					else if(user.work.compareTo("work") == 0) {
						mainThread = new WorkBot(mainThread,this);
					}
					ret = prevPlay(link);
					if(this.isInterrupted()) {
						net.close();
						return;
					}
					if(ret != PREV_PLAY_SUCCESS) System.out.println(user.name + " login fail.");
					if(ret == PREV_PLAY_RETRY) continue;
					else if(ret == PREV_PLAY_NET_FAIL) {
						sleep(6000);
						link = null;
						continue;
					}
				}
				
				try {
					if(mainThread != null) mainThread.start();
					keepalive = new Keepalive();
					keepalive.start();
					int packCnt = 0;
					while(true) {
						if(this.isInterrupted()) {
							net.close();
							if(mainThread != null) mainThread.interrupt();
							keepalive.interrupt();
							net.close();
							Thread.sleep(10000);
							return;
						}
						Pack pk = net.recive();
						int id = pk.getID();
						if(id == 0x1a) {
							net.close();
							PackInDisconnect pc = new PackInDisconnect(pk);
							System.out.println(user.name + " offline: " + pc.reason);
							if(mainThread != null) mainThread.interrupt();
							keepalive.interrupt();
							net.close();
							Thread.sleep(10000);
							break;
						}
						else if(id >= PackLoop.event.length) {
							System.out.println("BUG:Event receive class is lost");
							System.out.println("Pack ID:" + id + ", total " + pk.tellp() + " byte(s). Ignore.");
						}	
						else if(PackLoop.event[id] != null) PackLoop.event[id].event(Bot.this, pk);
						if(keepOnline == false) {
							packCnt++;
							if(packCnt > 10) {
								if(tryOffline(true) == true) {
									System.out.println(user.name + " offline.");
									if(mainThread != null) mainThread.interrupt();
									keepalive.interrupt();
									net.close();
									Thread.sleep(6000);
									sleep(120*1000);
									break;
								}
								else packCnt = 0;
							}
						}
					}
				} catch(Exception e) {
					System.out.println(user.name + " offline.");
					if(mainThread != null) mainThread.interrupt();
					keepalive.interrupt();
					net.close();
					Thread.sleep(6000);
				}
			}
		} catch (InterruptedException e) {
			if(authThread != null) authThread.interrupt();
			return;
		}
	}
	public void spawnEntity(Entity en) {
		if(en.type == MinecraftEntityID.meFishingBobber.ordinal() && en.data == selfID) 
				fishFloatID = en.id;
		for(int i = 0;i < entityList.length;i++) {
			if(entityList[i] == null) {
				entityList[i] = en;
				return;
			}
		}
	}
	public Entity getEntity(int id) {
		Entity en;
		for(int i = 0;i < entityList.length;i++) {
			en = entityList[i];
			if(en != null && en.id == id) return en;
		}
		return null;
	}
	public void destroyEntity(int id) {
		Entity en;
		for(int i = 0;i < entityList.length;i++) {
			en = entityList[i];
			if(en != null && en.id == id) entityList[i] = null;
		}
	}
	
	public void startEat(Hand hand) throws Exception {
		net.packOutUseItem(hand == Hand.hMain ? 0 : 1);
	}
	public void useItem(Hand hand) throws Exception {
		net.packOutUseItem(hand == Hand.hMain ? 0 : 1);
	}
	public void endEat() throws Exception {
		net.packOutDig(5, 0, 0, 0, 0);
	}
	public void shoot() throws Exception{
		net.packOutDig(5, 0, 0, 0, 0);
	}
	public void startDig(int x,int y,int z) throws Exception{
		net.packOutDig(0, x, y, z, 0);
	}
	public void endDig(int x,int y,int z) throws Exception{
		net.packOutDig(2, x, y, z, 0);
	}
	public void cancleDig(int x,int y,int z) throws Exception{
		net.packOutDig(1, x, y, z, 0);
	}

	public void moveTo(double x, double y, double z) throws Exception {
		net.packOutPlayLocation(x, y, z, true);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void rotation(float yaw, float pitch) throws Exception {
		net.packOutPlayRotation(yaw, pitch, true);
		this.yaw = yaw;
		this.pitch = pitch;
	}
	public void moveAndRotation(double x, double y, double z, float yaw, float pitch) throws Exception {
		net.packOutPlayPosRotation(x, y, z, yaw, pitch, true);
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	public void rotationContinue(float yaw, float pitch, int step) throws Exception,InterruptedException {
		float dy, dp;
		float cy, cp;
		cy = this.yaw;
		cp = this.pitch;
		dy = yaw - this.yaw;
		dp = pitch - this.pitch;
		if (step == 0) step = 1;
		dy /= step;
		dp /= step;
		while (step != 0) {
			step--;
			cy += dy;
			cp += dp;
			rotation(cy, cp);
			Thread.sleep(50);
		}
		rotation(yaw, pitch);
		Thread.sleep(50);
	}
	public void attackEntity(int entity) throws Exception {
		swingArm(Hand.hMain);
		net.packOutInteractEntity(entity, 1, 0, 0, 0, 0, false);
	}
	public void interactEntity(int entity, Hand hand) throws Exception {
		swingArm(hand);
		net.packOutInteractEntity(entity, 0, 0, 0, 0, hand == Hand.hMain ? 0 : 1, false);
	}
	public void interactAtEntity(int entity, Hand hand, float x, float y, float z) throws Exception {
		swingArm(hand);
		net.packOutInteractEntity(entity, 2, x, y, z, hand == Hand.hMain ? 0 : 1, false);
	}
	public void swingArm(Hand hand) throws Exception {
		net.packOutAnimation(hand == Hand.hMain ? 0 : 1);
	}
	public boolean placeBlock(Hand hand,double x, double y, double z, Face face, 
		float posx, float posy, float posz, boolean insig,int block) throws Exception {
		item.lockSlot();
		Slot[] hold;
		int holdIndex;
		if (hand == Hand.hAlter) {
			hold = item.alterHand;
			holdIndex = 0;
		}
		else {
			hold = item.inventorys;
			holdIndex = item.holdSlot;
		}
		if (hold[holdIndex].present) {
			hold[holdIndex].count--;
			if (hold[holdIndex].count == 0) hold[holdIndex] = Slot.Empty;
		}
		else {
			item.unlockSlot();
			return false;
		}
		item.unlockSlot();
		world.setBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), block);
		swingArm(hand);
		net.packOutPlaceBlock(hand == Hand.hMain ? 0 : 1, (int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), face.ordinal(), posx, posy, posz, insig);
		return true;
	}
	public void animation(Hand hand) throws Exception {
		net.packOutAnimation(hand == Hand.hMain ? 0 : 1);
	}

	private Location[] digLocation = new Location[32];
	public void addDigList(int x,int y,int z) {
		for(int i = 0;i < digLocation.length;i++) {
			if(digLocation[i] == null) {
				digLocation[i] = new Location(x,y,z);
				return;
			}
		}
	}
	public void acknowledgeDig(Location loc,int cur) {
		for(int i = 0;i < digLocation.length;i++) {
			if(digLocation[i] != null) {
				if(digLocation[i].x == loc.x && 
				digLocation[i].y == loc.y &&
				digLocation[i].z == loc.z) world.setBlock(loc.x,loc.y,loc.z, cur);
				digLocation[i] = null;
			}
		}
	}
	
	public boolean dig(int x, int y, int z) throws Exception,InterruptedException {
		int blockID;
		double dx, dy, dz;
		double tick;
		MinecraftTool tool;
		int holdTool;
		double rate;
		int toolType = 0;
		int temp;
		int ticki;
		Slot holdItem = item.getHoldItem();

		
		dx = this.x - x;
		dy = this.y + 1.5 - y;
		dz = this.z - z;
		dx *= dx;
		dy *= dy;
		dz *= dz;
		if (dx + dy + dz > 36) return false;
		blockID = world.getBlock(x, y, z);
		if (blockID == 0) return true;
		blockID = MinecraftBlock.getBaseID(blockID);
		if (MinecraftBlock.block[blockID].hardness < 0) return false;
		tool = MinecraftBlock.block[blockID].tool;
		if (holdItem.present) holdTool = holdItem.id;
		else holdTool = 0;
		tick = MinecraftBlock.block[blockID].hardness * 200;
		if (tool != MinecraftTool.mtNone && tick != 0) {
			switch (MinecraftItemID.values()[holdTool]) {
			case miWoodenAxe:
			case miWoodenHoe:
			case miWoodenPickaxe:
			case miWoodenShovel:
				rate = 2;
				break;
			case miStoneAxe:
			case miStoneHoe:
			case miStonePickaxe:
			case miStoneShovel:
				rate = 4;
				break;
			case miIronAxe:
			case miIronHoe:
			case miIronPickaxe:
			case miIronShovel:
				rate = 6;
				break;
			case miDiamondAxe:
			case miDiamondHoe:
			case miDiamondPickaxe:
			case miDiamondShovel:
				rate = 8;
			case miNetheriteAxe:
			case miNetheriteHoe:
			case miNetheritePickaxe:
			case miNetheriteShovel:
				rate = 9;
				break;
			case miGoldenAxe:
			case miGoldenHoe:
			case miGoldenPickaxe:
			case miGoldenShovel:
				rate = 12;
				break;
			default:
				rate = 0.2;
			}
			switch (MinecraftItemID.values()[holdTool]) {
			case miWoodenAxe:
			case miStoneAxe:
			case miIronAxe:
			case miDiamondAxe:
			case miNetheriteAxe:
			case miGoldenAxe:
				toolType = 1;
				break;
			case miWoodenHoe:
			case miStoneHoe:
			case miIronHoe:
			case miDiamondHoe:
			case miNetheriteHoe:
			case miGoldenHoe:
				toolType = 2;
				break;
			case miWoodenPickaxe:
			case miStonePickaxe:
			case miIronPickaxe:
			case miDiamondPickaxe:
			case miNetheritePickaxe:
			case miGoldenPickaxe:
				toolType = 3;
				break;
			case miWoodenShovel:
			case miStoneShovel:
			case miIronShovel:
			case miDiamondShovel:
			case miNetheriteShovel:
			case miGoldenShovel:
				toolType = 4;
				break;
			case miShears:
				toolType = 5;
				break;
			default:
				toolType = 0;
				break;
			}
			switch (tool) {
			case mtWoodenAxe:
				if (toolType != 1) rate = 0.2;
				break;
			case mtWoodenHoe:
				if (toolType != 2) rate = 0.2;
				break;
			case mtWoodenPickaxe:
				if (toolType != 3) rate = 0.2;
				break;
			case mtDiamondPickaxe:
				if (holdTool == MinecraftItemID.miIronPickaxe.ordinal()) {
					rate = 0.2;
					break;
				}
			case mtIronPickaxe:
				if (holdTool == MinecraftItemID.miStonePickaxe.ordinal()) {
					rate = 0.2;
					break;
				}
				if (holdTool == MinecraftItemID.miGoldenPickaxe.ordinal()) {
					rate = 0.2;
					break;
				}
			case mtStonePickaxe:
				if (holdTool == MinecraftItemID.miWoodenPickaxe.ordinal()) rate = 0.2;
				break;
			case mtWoodenShovel:
				if (toolType != 4) rate = 0.2;
				break;
			case mtShears:
				if (toolType != 5) rate = 0.2;
				break;
			}
			if ((holdItem.present) && (holdItem.ench != null) && 
					(holdItem.ench.enchantments & (1L << Enchantment.efficiency)) != 0) {
				if (rate != 0.2) {
					temp = holdItem.ench.enchantmentsLvl[Enchantment.efficiency];
					temp *= temp;
					rate += 1 + temp;
				}
			}
			tick /= rate;
		}
		tick *= 0.9;
		ticki = (int) tick;
		addDigList(x,y,z);
		net.packOutDig(0, x, y, z, 0);
		Thread.sleep(ticki);
		net.packOutDig(2, x, y, z, 0);
		world.setBlock(x, y, z, 0);
		if (ticki < 25) Thread.sleep(25 - ticki);
		return true;
	}
	public void click(int x, int y, int z) throws Exception,InterruptedException {
		startDig(x, y, z);
		Thread.sleep(50);
		cancleDig(x, y, z);
	}
	public boolean inRound(Entity en, double x, double y, double z, double round) {
		x -= en.x;
		y -= en.y;
		z -= en.z;
		x *= x;
		y *= y;
		z *= z;
		round *= round;
		if (x + y + z <= round) return true;
		return false;
	}
	public void eat(Hand hand) throws Exception,InterruptedException {
		if(hand == Hand.hMain) {
			int b = item.findOutFastFood();
			if (b < 0) return;
			int orgFood = this.food;
			healthUpdate = false;
			item.holdItemChange(b);
			startEat(Hand.hMain);
			while(true) {
				while(healthUpdate == false) Thread.sleep(50);
				healthUpdate = false;
				int curFood = this.food;
				if(orgFood < curFood) break;
			}
			endEat();
		}
		else {
			int id = item.alterHand[0].id;
			if(id == 0) return;
			int i;
			for(i = 0;i < Item.foods.length;i++) {
				if(id == Item.foods[i]) break;
			}
			if(i >= Item.foods.length) return;
			int orgFood = this.food;
			healthUpdate = false;
			startEat(Hand.hAlter);
			while(true) {
				while(healthUpdate == false) Thread.sleep(50);
				healthUpdate = false;
				int curFood = this.food;
				if(orgFood < curFood) break;
			}
			endEat();
		}
		
	}

	public int findEntityInRect(double x, double y, double z, int dx, int dy, int dz, MinecraftEntityID type) {
		double sx, sy, sz, ex, ey, ez;
		sx = x;
		sy = y;
		sz = z;
		ex = x + dx;
		ey = y + dy;
		ez = z + dz;
		Entity en;
		for (int i = 0; i < entityList.length; i++) {
			en = entityList[i];
			if (en != null && en.type == type.ordinal()) {
				if (en.x >= sx && en.x <= ex &&
					en.y >= sy && en.y <= ey &&
					en.z >= sz && en.z <= ez) {
					return en.id;
				}
			}
		}
		return -1;
	}
	public void moveLine(double ex, double ey, double ez, int step) throws Exception,InterruptedException {
		if (step == 0) step = 1;
		double dx = ex - x;
		double dy = ey - y;
		double dz = ez - z;
		dx /= step;
		dy /= step;
		dz /= step;
		ex = x;
		ey = y;
		ez = z;
		while (step != 0) {
			step--;
			ex += dx;
			ey += dy;
			ez += dz;
			moveTo(ex, ey, ez);
			Thread.sleep(70);
		}
	}
	public int attackMonster() throws InterruptedException{
		Entity en;
		int i;
		int count = 0;
		double x, y, z, r;
		for (i = 0; i < entityList.length; i++) {
			en = entityList[i];
			if (en == null) continue;
			if (MinecraftEntity.entity[en.type].isActive) {
				x = en.x - this.x;
				y = en.y - this.y;
				z = en.z - this.z;
				r = x * x + y * y + z * z;
				if (r <= 16) {
					int sslot = item.findItemOnFastSlot(MinecraftItemID.miNetheriteSword.ordinal());
					try {
					if (sslot >= 0)
						item.holdItemChange(sslot);
						attackEntity(en.id);
					} catch (Exception e) {
						return count;
					}
					count++;
					Thread.sleep(120);
				}
			}
		}
		return count;
	}
	
	public int replaceBlock(int x,int y,int z,int blockID) throws Exception {
		int orgBlockID;
		int baseBlockID;
		double distance,tmp;
		int tool;
		
		if(world.isLoaded(x, y, z) == false) return 1;
		orgBlockID = world.getBlock(x, y, z);
		if(orgBlockID == blockID) return 0;
		baseBlockID = MinecraftBlock.getBaseID(orgBlockID);
		
		distance = Bot.this.x - x;
		distance *= distance;
		tmp = (Bot.this.y + 1.5) - y;
		distance += tmp * tmp;
		tmp = Bot.this.z - z;
		distance += tmp * tmp;
		if(distance >= 36) return 2;
		
		switch(MinecraftBlock.block[baseBlockID].tool) {
		case mtWoodenAxe:
			tool = MinecraftItemID.miNetheriteAxe.ordinal();
			break;
		case mtWoodenHoe:
			tool = MinecraftItemID.miNetheriteHoe.ordinal();
			break;
		case mtWoodenPickaxe:
		case mtStonePickaxe:
		case mtGoldenPickaxe:
		case mtIronPickaxe:
		case mtDiamondPickaxe:
			tool = MinecraftItemID.miNetheritePickaxe.ordinal();
			break;
		case mtWoodenShovel:
			tool = MinecraftItemID.miNetheriteShovel.ordinal();
			break;
		case mtWoodenSword:
			tool = MinecraftItemID.miNetheriteSword.ordinal();
			break;
		case mtShears:
			tool = MinecraftItemID.miShears.ordinal();
			break;
		case mtBucket:
			tool = MinecraftItemID.miBucket.ordinal();
			break;
		case mtDigless:
			tool = -1;
			break;
		case mtUndigable:
			return 3;
		case mtNone:
		default:
			tool = 0;
		}
		if(tool != -1) {
			if(tool != 0) {
				int bestSlot = item.findItemOnFastSlot(tool);
				if(bestSlot >= 0) item.holdItemChange(bestSlot);
			}
		}
		dig(x,y,z);
		if(blockID == 0) return 0;
		baseBlockID = MinecraftBlock.getBaseID(blockID);
		int itemID = MinecraftBlock.toItem[baseBlockID].ordinal();
		if(itemID <= 0) return 4;
		Hand h = Hand.hAlter;
		if(item.alterHand[0].present && item.alterHand[0].id == itemID) {}
		else {
			int slot = item.findItemOnFastSlot(itemID);
			if (slot >= 0 && slot <= 8) {
				item.holdItemChange(slot);
				h = Hand.hMain;
			}
			else {
				slot = item.findOutItemInInventory(itemID);
				if (slot < 0) return 5;
				item.swapInventory(Hand.hAlter, slot, 0);
			}
			sleep(25);
		}
		item.windowUpdate.clearEvent();
		placeBlock(h, x, y, z, Face.PosY, 0, 0, 0, false, blockID);
		return 0;
	}
	
	public boolean exchangeQuartxBlockOrGlass(int entityID) throws Exception,InterruptedException {
		int need, sup;
		int wait = 0;

		int emeraldCount = item.counterItemInInventory(MinecraftItemID.miEmerald.ordinal(), false);
		if (emeraldCount == 0) return false;
		if (false == item.waitForTrade(entityID, 5000)) {
			item.closeWindow();
			return false;
		}
		int slot = item.findTradeSlot(MinecraftItemID.miEmerald, MinecraftItemID.miQuartzBlock);
		if (slot < 0) {
			slot = item.findTradeSlot(MinecraftItemID.miEmerald, MinecraftItemID.miGlass);
			if (slot < 0) {
				item.closeWindow();
				return false;
			}
		}
		if (item.tradeList[slot].disabled) {
			item.closeWindow();
			return false;
		}
		if (item.tradeList[slot].maxTradedCount - 1 <= item.tradeList[slot].tradedCount) {
			item.closeWindow();
			return false;
		}
		need = item.cacuPrice(slot);
		if (need > 1) {
			item.closeWindow();
			return false;
		}
		item.windowUpdate.clearEvent();
		item.selectTrade(slot);
		while (!this.item.slots[0].present || !this.item.slots[2].present) {
			Thread.sleep(50); 
			wait += 50;
			if (wait >= 5000) {
				item.closeWindow();
				return false;
			}
		}
		if (emeraldCount < 64) sup = emeraldCount;
		else sup = 64;
		for (int j = item.tradeList[slot].tradedCount; j < item.tradeList[slot].maxTradedCount - 1; j++) {
			if (sup < need) {
				item.putDownItemInInventory();
				while (true) {
					int stoneSlot = item.findOutItemInInventory(MinecraftItemID.miEmerald.ordinal());
					if (stoneSlot < 0) break;
					sup += item.inventorys[stoneSlot].count;
					item.clickInventory(stoneSlot);
					Thread.sleep(100);
					item.clickSlot(0);
					if (sup >= need) break;
				}
				if (sup < need) break;
				if (sup > 64) {
					sup = 64;
					Thread.sleep(100);
					item.putDownItemInInventory();
				}
			}
			Thread.sleep(100);
			item.clickSlot(2);
			sup -= need;
		}
		item.putDownItemInInventory();
		Thread.sleep(100);
		item.clickSlot(0);
		Thread.sleep(100);
		item.putDownItemInInventory();
		Thread.sleep(100);
		item.closeWindow();
		return true;
	}
	public boolean tradeWithAVillager(int entityID) throws InterruptedException, Exception {
		int emptySlotCount = item.counterEmptyInInventory(false);
		if (emptySlotCount == 0) return false;
		int emeraldCount = item.counterItemInInventory(MinecraftItemID.miEmerald.ordinal(), false);
		if (emeraldCount != 0) exchangeQuartxBlockOrGlass(entityID);
		int count4 = item.counterItemInInventory(MinecraftItemID.miEmerald.ordinal(), false);
		if (count4 == 0) return false;
		return true;
	}
	public int glassMain() throws Exception,InterruptedException {
		//chat.loginEvent.waitEvent();
		Thread.sleep(5000);
		short[] needDrop = new short[36];
		short[] quartzBlockDrop = new short[36];
		short[] glassDrop = new short[36];
		
		while (true) {
			chat.tpCommand("6.sy.buyEmerald",40000);
			int dropCount = 0;
			int quartzBlockCount = 0;
			int glassCount = 0;
			int emCount = 0;
			item.lockSlot();
			for (int i = 0; i < 36; i++) {
				if (!item.inventorys[i].present) continue;
				if (item.inventorys[i].id == MinecraftItemID.miEmerald.ordinal()) emCount += item.inventorys[i].count;
				else if (item.inventorys[i].id == MinecraftItemID.miQuartzBlock.ordinal()) {
					quartzBlockDrop[quartzBlockCount] = (short)i;
					quartzBlockCount++;
				}
				else if (item.inventorys[i].id == MinecraftItemID.miGlass.ordinal()) {
					glassDrop[glassCount] = (short)i;
					glassCount++;
				}
				else {
					needDrop[dropCount] = (short)i;
					dropCount++;
				}
			}
			item.unlockSlot();
			rotationContinue(180, 0, 5);
			while (glassCount != 0) {
				glassCount--;
				item.dropInventoryItemStack(glassDrop[glassCount]);
				Thread.sleep(100);
			}
			rotationContinue(270, 0, 5);
			while (dropCount != 0) {
				dropCount--;
				item.dropInventoryItemStack(needDrop[dropCount]);
				Thread.sleep(100);
			}
			rotationContinue((float)0.0, 0, 5);
			while (quartzBlockCount != 0) {
				quartzBlockCount--;
				item.dropInventoryItemStack(quartzBlockDrop[quartzBlockCount]);
				Thread.sleep(100);
			}
			if(emCount < 1024) chat.buyItemFromChestShop(67779, 7, -71234, 1024 - emCount);

			double sx, sy, sz;
			int st;
			int id;
			int loop;
			double dz = 6.0;

			chat.tpCommand("6.sy.startPos",40000);//trade with villagers
			sx = 67769.5;
			sy = 7.0;
			sz = -71264.5;

			for (loop = 0; loop < 7; loop++) {
				st = 0;
				while (st < (24 + 16)) {
					id = findEntityInRect(sx + 1.5, sy, sz - 0.5, 2, 2, 1, MinecraftEntityID.meVillager);
					if (id != -1 && false == tradeWithAVillager(id)) break;
					sz += dz;
					moveLine(sx, sy, sz, 6);
					st++;
				}
				if (st < (24 + 16)) continue;
				id = findEntityInRect(sx + 1.5, sy, sz - 0.5, 2, 2, 1, MinecraftEntityID.meVillager);
				if (id >= 0 && false == tradeWithAVillager(id)) continue;
				sz += 2.0;
				moveLine(sx, sy, sz, 2);
				sx += 8.0;
				moveLine(sx, sy, sz, 8);
				sz -= 2.0;
				moveLine(sx, sy, sz, 2);
				dz = -dz;
			}
		}
	}
	

}
