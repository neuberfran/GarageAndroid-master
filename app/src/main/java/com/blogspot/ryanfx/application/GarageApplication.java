package com.blogspot.ryanfx.application;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import com.blogspot.ryanfx.security.FailingAuthenticator;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GarageApplication extends Application{
	
	private SharedPreferences sp;
	private static GarageApplication application;
	private String authToken = "";
	private FailingAuthenticator failingAuthenticator;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		application = this;
		//Enables automatic handling of cookies for HTTPURLConnection(s)
		CookieHandler.setDefault(new CookieManager());
		setupAuthentication(authToken);
	}
	
	public FailingAuthenticator getAuthenticator(){
		return failingAuthenticator;
	}
	/**
	 * Check out the comments in FailingAuthenticator for more details
	 * on why we need this
	 * @param authToken
	 */
	private void setupAuthentication(String authToken) {
		failingAuthenticator = new FailingAuthenticator
				(authToken, application.getServerPassword().toCharArray());
		Authenticator.setDefault(failingAuthenticator);
	}
	
	public static GarageApplication getApplication(){
		return application;
	}
	
	public Account getSelectedAccount(){
		String user = sp.getString("user", "");
		return getAccountFromString(user);
	}
	
	public int getServerPort(){
		return Integer.valueOf(sp.getString("port", "0"));
	}
	
	public String getServerHost(){
		return sp.getString("host", "");
	}
	
	public String getServerPassword(){
		return sp.getString("password", "");
	}
	
	public String getAuthCookie(){
		return sp.getString("cookie", "");
	}
	
	public void setAuthCookie(String cookie){
		sp.edit().putString("cookie", cookie).commit();
	}
	
	
	private Account getAccountFromString(String user){
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType("com.google");
		for (Account current : accounts){
			if (current.name.equals(user)){
				return current;
			}
		}
		return null;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		//If the authorization token has changed, update the default authenticator
		if (!this.authToken.equals(authToken)){
			this.authToken = authToken;
			setupAuthentication(this.authToken);
		}
	}
}
