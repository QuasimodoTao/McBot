package mcbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

class TimeOutException extends Exception{
	private static final long serialVersionUID = 1L;	
}
public class main{
	
	private static boolean MASK_USER_ENABLE = true;
	//public static int PLAY_WAIT_TIME = 5000;
	public static int PROXY_UPDATE_HOUR = 8;
	private static int FAIL_THRESHOLD = 11;
	private static boolean UPDATE_ENABLE = true;
	public static int CONNECT_TIME_OUT = 1000;
	private static String updateCommand = null;
	public static int MAX_ONLINE = -1;
	public static boolean ONLINE_MODE = false;
	
	private static long prevCheckTime;
	
	private static User[] user;
	private static Link[] link;
	private static String[] admin;
	private static volatile boolean linkInvaild = false;
	private static Bot[] bot = null;
	
	public static String host = "wolfxmc.org";
	public static int port = 25565;
	
	public static int getIpFromString(String ip) throws Exception{
        String[] svrIpSeg = ip.split("\\.");
        if(svrIpSeg.length != 4) throw new Exception("invaild IP format");
        int res = Integer.parseInt(svrIpSeg[3]) + (Integer.parseInt(svrIpSeg[2]) << 8) + (Integer.parseInt(svrIpSeg[1]) << 16) + (Integer.parseInt(svrIpSeg[0]) << 24);
        return res;
    }
	private static JSONObject loadJSON(String path) throws Exception{
    	FileInputStream f = new FileInputStream(path);
        byte[] s = new byte[f.available()];
        f.read(s);
        f.close();
        String jsonStr = new String(s);
    	return JSONObject.parseObject(jsonStr);
    }
	private static void saveJSON(String path,JSONObject json) throws Exception {
		FileOutputStream f = new FileOutputStream(path);
		f.write(json.toJSONString().getBytes());
		f.close();
	}
	private static boolean loadConfig(JSONObject config) {
		JSONArray userAr = config.getJSONArray("user");
		host = config.getString("service");
		if(host == null || host.isEmpty() == true) return false;
		port = 0;
		if(config.containsKey("port")) port = config.getIntValue("port");
		if(port == 0) port = 25565;

		//PLAY_WAIT_TIME = 30000;
		//if(config.containsKey("playWaitTime")) PLAY_WAIT_TIME = config.getIntValue("playWaitTime");
		PROXY_UPDATE_HOUR = 8;
		if(config.containsKey("proxyUpdateHour")) PROXY_UPDATE_HOUR = config.getIntValue("proxyUpdateHour");
		FAIL_THRESHOLD = 11;
		if(config.containsKey("proxyFailThreshold")) FAIL_THRESHOLD = config.getIntValue("proxyFailThreshol");
		UPDATE_ENABLE = true;
		if(config.containsKey("updateEnable")) UPDATE_ENABLE = config.getBooleanValue("updateEnable");
		CONNECT_TIME_OUT = 1000;
		if(config.containsKey("connectTimeOut")) CONNECT_TIME_OUT = config.getIntValue("connectTimeOut");
		updateCommand = null;
		if(config.containsKey("updateCommandLine")) updateCommand = config.getString("updateCommandLine");
		MASK_USER_ENABLE = true;
		if(config.containsKey("defaultMaskUserEnable")) MASK_USER_ENABLE = config.getBooleanValue("defaultMaskUserEnable");
		MAX_ONLINE = -1;
		if(config.containsKey("maxOnline")) MAX_ONLINE = config.getIntValue("maxOnline");
		if(config.containsKey("onlineMode")) ONLINE_MODE = config.getBooleanValue("onlineMode");
		if(host != null) {
			if(host.isEmpty()) host = null;
			else if(host.compareTo("0.0.0.0") == 0) host = null;
			//else if(host.compareTo("127.0.0.1") == 0) host = null;
		}
		if(userAr == null) {
			System.out.println("No user in config.");
			return false;
		}
		user = new User[userAr.size()];
		for(int i = 0;i < user.length;i++) {
			user[i] = new User();
			JSONObject per = userAr.getJSONObject(i);
			if(per == null) continue;
			user[i].name = per.getString("name");
			if(user[i].name == null || user[i].name.isEmpty() == true) continue;
			//if(per.containsKey("password")) user[i].password = per.getString("password");
			//else user[i].password = null;
			//if(user[i].password == null || user[i].password.isEmpty() == true) continue;
			if(per.containsKey("work")) user[i].work = per.getString("work");
			if(per.containsKey("maskUserEnable")) user[i].maskUser = per.getBooleanValue("maskUserEnable");
			else user[i].maskUser = MASK_USER_ENABLE;
			if(per.containsKey("onlinePassword")) user[i].onlinePassword = per.getString("onlinePassword");
			user[i].vaild = true;
			user[i].index = i;
			user[i].json = per;
		}
		
		String defNet = config.getString("defaultBindNet");
		if(defNet != null) {
			if(defNet.isEmpty()) defNet = null;
			else if(defNet.compareTo("0.0.0.0") == 0) defNet = null;
			else if(defNet.compareTo("127.0.0.1") == 0) defNet = null;
		}
		JSONArray linkAr = config.getJSONArray("link");
		link = new Link[linkAr.size()];
		linkIndex = 0;
		int [] linkt = new int[linkAr.size()];
		
		for(int i = 0;i < link.length;i++) {
			JSONObject per = linkAr.getJSONObject(i);
			if(per == null) continue;
			if(host != null) {
				String proxyName = per.getString("proxyName");
				if(proxyName != null) {
					if(proxyName.isEmpty()) proxyName = null;
					else if(proxyName.contains("0.0.0.0") == true) continue;
					else if(proxyName.contains("127.0.0.1") == true) continue;
				}
				int proxyPort = 1080;
				if(per.containsKey("proxyPort") == true) proxyPort = per.getIntValue("proxyPort");
				linkt[i] = 0;
				if(proxyName != null) {
					try {
						linkt[i] = getIpFromString(proxyName);
					} catch(Exception e) {
						continue;
					}
				}
				int ip = linkt[i];
				int j;
				for(j = 0;j < i;j++) {
					if(link[j] != null && linkt[j] == ip && link[j].proxyPort == proxyPort) break;
				}
				if(j < i) continue;
				link[i] = new Link();
				link[i].proxyHost = proxyName;
				link[i].proxyPort = proxyPort;
				if(proxyName != null) link[i].proxy = true;
			}
			else link[i] = new Link();
			link[i].index = i;
			String bindIP = per.getString("bindNet");
			if(bindIP != null) {
				if(bindIP.isEmpty()) bindIP = null;
				else if(bindIP.compareTo("0.0.0.0") == 0) bindIP = null;
				else if(bindIP.compareTo("127.0.0.1") == 0) bindIP = null;
			}
			if(bindIP == null) link[i].bindIP = defNet;
			else link[i].bindIP = bindIP;
			if(link[i].bindIP != null) link[i].bind = true;
		}
		JSONArray adminAr = config.getJSONArray("admin");
		admin = null;
		if(adminAr != null) {
			admin = new String[adminAr.size()];
			for(int i = 0;i < admin.length;i++) {
				admin[i] = adminAr.getString(i);
				if(admin[i].isEmpty() == true) admin[i] = null;
			}
		}
		return true;
	}
	static int linkIndex = 0;
	public static void invaildNet(Link l) {
		if(UPDATE_ENABLE == false) return; 
		synchronized(l) {
			l.failTimes++;
			if(l.failTimes >= FAIL_THRESHOLD) link[l.index] = null;
		}
	}
	public static void vaildLink(Link l) {
		if(UPDATE_ENABLE == false) return; 
		synchronized(l) {
			l.failTimes = 0;
		}
	}
	public static synchronized Link allocNet() throws InterruptedException {
		if(link == null || link.length == 0) {
			return new Link();
		}
		int org = linkIndex;
		while(true) {
			if(link[linkIndex] == null) {
				linkIndex++;
				if(linkIndex >= link.length) linkIndex = 0;
				else if(linkIndex == org) {
					linkInvaild = true;
					return null;
				}
				continue;
			}
			Link ret = link[linkIndex];
			linkIndex++;
			if(linkIndex >= link.length) linkIndex = 0;
			//System.out.println("Alloc proxy:" + ret.proxyHost + ":" + ret.proxyPort);
			return ret;
		}
	}
	public static void runCatchProxy() {
		Process proc;
		System.out.println("Update proxy pool.");
		InputStream is;
		if(updateCommand == null || updateCommand.isEmpty() == true) return;
		try {
			proc = Runtime.getRuntime().exec(updateCommand);
			is = proc.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		new Thread(()->{
			try {
				int ch = is.read();
				if(ch < 0) {
					Thread.sleep(100);
				}
				else {
					System.out.print((char)ch);
				}
			} catch(Exception e) {
				return;
			}
		}).start();
		try {
			proc.waitFor();
			is.close();
		} catch (Exception e) {
			return;
		}
		return;
	}
	
	public static void main(String[] args) {
		while(true) {
			try {
				JSONObject config;
				try {
					config = loadJSON("config.json");
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				if(loadConfig(config) == false) {
					Thread.sleep(1000*5);
					continue;
				}
				System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + ":Load config.");
				prevCheckTime = System.currentTimeMillis();
				entry();
				runCatchProxy();
			} catch (InterruptedException e) {
				System.exit(0);
				return;
			}
		}
	}
	public static void saveConfig() {
		JSONArray linkAr = new JSONArray();
		if(UPDATE_ENABLE == true) {
			for(int i = 0;i < bot.length;i++) if(bot[i] != null) {
				Link l = bot[i].getLink();
				if(l == null) continue;
				JSONObject per = new JSONObject();
				if(link[l.index].proxy) {
					per.put("proxyName", l.proxyHost);
					per.put("proxyPort", l.proxyPort);
				}
				if(link[l.index].bind) {
					per.put("bindNet", l.bindIP);
				}
				per.put("vaild", true);
				linkAr.add(per);
			}
		}
		JSONObject config;
		try {
			config = loadJSON("config.json");
			if(UPDATE_ENABLE == true) config.replace("link", linkAr);
			JSONArray usr = config.getJSONArray("user");
			for(int i = 0;i < user.length;i++) {
				if(user[i].modify == false) continue;
				for(int j = 0;j < usr.size();j++) {
					if(user[i].name.compareTo(usr.getJSONObject(j).getString("name")) == 0) {
						
						usr.set(j, user[i].json);
						break;
					}
				}
			}
			config.replace("user", usr);
			saveJSON("config.json",config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void entry(){
		bot = new Bot[user.length];
		linkInvaild = false;
		Bot.pingStart();
		for(int i = 0;i < bot.length;i++) if(user[i].vaild) {
			bot[i] = new Bot(user[i],admin);
			bot[i].start();
		}
		try {
			if(UPDATE_ENABLE == false) while(true) Thread.sleep(1000000);
			while(true) {
				Thread.sleep(10000);
				if((System.currentTimeMillis() - prevCheckTime > (PROXY_UPDATE_HOUR * 60 * 60 * 1000)) || 
					linkInvaild == true) {
					break;
				}
			}
		} catch (InterruptedException e) {
			for(int i = 0;i < bot.length;i++) if(bot[i] != null) bot[i].interrupt();
			Bot.pingStop();
			saveConfig();
			System.exit(0);
		}
		for(int i = 0;i < bot.length;i++) if(bot[i] != null) bot[i].interrupt();
		Bot.pingStop();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			saveConfig();
			System.exit(0);
		}
		saveConfig();
		return;
	}
}
