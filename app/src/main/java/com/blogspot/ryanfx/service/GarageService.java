package com.blogspot.ryanfx.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.blogspot.ryanfx.application.GarageApplication;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.net.HttpURLConnection;

public abstract class GarageService extends IntentService{
	public static final String AUTH_TOKEN = "AUTH_TOKEN";
	public static final String INTENT_TOGGLE = "com.blogspot.ryan.garage-toggle-result";
	public static final String INTENT_CLOSE  = "com.blogspot.ryan.garage-close-result";
	public static final String INTENT_STATE  = "com.blogspot.ryan.garage-state-result";
	public static final String INTENT_ERROR  = "com.blogspot.ryan.garage-error-result";
	public static final String EXTRA_HTTP_RESPONSE_CODE = "EXTRA_HTTP_RESPONSE_CODE";
	public static final String EXTRA_HTTP_RESPONSE_TEXT = "EXTRA_HTTP_RESPONSE_TEXT";
	private static final int TIMEOUT = 3000;
	private final boolean SECURE = true;

	protected GarageApplication application;

	public GarageService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = (GarageApplication) getApplication();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String host = application.getServerHost();
		int port = application.getServerPort();
		Intent broadcast = new Intent(intent.getAction());
		try {
			String protocol =  "http";
			String baseString = protocol + "://" + host + ":" + port + "/GarageDoor/Garage/";
			HttpURLConnection urlConnection = getRequestFromIntent(baseString, intent);
			int responseCode = urlConnection.getResponseCode();
			application.getAuthenticator().setSuccessfulConnectionMade();
			//getResponseCode or getInputStream signals to actually make the request
			if (responseCode == HttpStatus.SC_OK){
				String responseBody = convertStreamToString(urlConnection.getInputStream());
				urlConnection.getInputStream().close();
				broadcast.putExtra(EXTRA_HTTP_RESPONSE_TEXT, responseBody);
			}else {
				broadcast.putExtra(EXTRA_HTTP_RESPONSE_TEXT, "");
			}
			Log.i(GarageService.class.getName(), "Response Code: " + responseCode);
			broadcast.putExtra(EXTRA_HTTP_RESPONSE_CODE, responseCode);
		} catch (Exception e) {
			Log.e(getClass().getName(), "Exception", e);
			broadcast.putExtra(EXTRA_HTTP_RESPONSE_CODE, -1);
			broadcast.putExtra(EXTRA_HTTP_RESPONSE_TEXT, e.getMessage());
		}finally{
			sendBroadcast(broadcast);
		}
	}

	protected void setupConnectionProperties(HttpURLConnection urlConnection) {
		urlConnection.setConnectTimeout(TIMEOUT);
	}

	protected static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	protected abstract HttpURLConnection getRequestFromIntent(String baseString, Intent intent) throws ClientProtocolException, IOException;

}
