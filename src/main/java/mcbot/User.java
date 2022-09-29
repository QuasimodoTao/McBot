package mcbot;

import com.alibaba.fastjson.JSONObject;

public class User {
	private static final String defaultWork = "fishing";
	
	public String name;
	//public String password;
	public String work = defaultWork;
	public String UID = null;
	public String onlinePassword = null;
	public boolean maskUser = true;
	public boolean vaild = false;
	public JSONObject json = null;
	public int index = -1;
	public boolean modify = false;
	public boolean inputPassword = true;
}
