package mcbot;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.*;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;

public class Auth {
	private String name;
	private String password;
	private static final String MOJANG_AUTH_ENDPOINT = "https://authserver.mojang.com";
    public String accessToken;
    public String clientToken;
    public String profile;
    public JSONObject responseJson;
    public String UID;
	
	public Auth(String name,String password) {
		this.name = name;
		this.password = password;
	}
	public boolean authenticate() {
		HttpClient client = HttpClientBuilder.create().build();;
        String send = "{\"agent\":{\"name\":\"minecraft\",\"version\":\"1\"},\"username\":\"" + name + "\",\"password\":\"" + password + "\"}";
        try {
            HttpUriRequest request = RequestBuilder.post()
                .setUri(MOJANG_AUTH_ENDPOINT + "/authenticate")
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .setEntity(new StringEntity(send))
                .build();
            HttpResponse answer = client.execute(request);
            if (answer.getStatusLine().getStatusCode() == 200) {
	            responseJson = JSONObject.parseObject(EntityUtils.toString(answer.getEntity(), Charsets.UTF_8));
	            accessToken = responseJson.getString("accessToken");
	            clientToken = responseJson.getString("clientToken");
	            profile = responseJson.getJSONObject("selectedProfile").getString("id");
	            UID = responseJson.getJSONObject("selectedProfile").getString("name");
	            return true;
            }
            else {
            	MsaAuthenticationService authService = new MsaAuthenticationService("fef9faea-d962-4476-9ce7-4960c8baa946");
                authService.setUsername(name);
                authService.setPassword(password);
                try {
                    authService.login();
                    GameProfile profile = authService.getSelectedProfile();
                    accessToken = authService.getAccessToken();
                    clientToken = authService.getClientToken();
                    this.profile = profile == null ? null : profile.getIdAsString().replace("-", "");
                    UID = authService.getUsername();
                    return true;
               } catch (RequestException e) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
	
}
