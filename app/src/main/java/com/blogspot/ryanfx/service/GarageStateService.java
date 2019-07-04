package com.blogspot.ryanfx.service;

import android.content.Intent;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GarageStateService extends GarageService {

	public GarageStateService() {
		super("GarageStateService");
	}

	protected HttpURLConnection getRequestFromIntent(String urlAddress, Intent intent)
			throws ClientProtocolException, IOException {
		if (intent.getAction().equals(GarageService.INTENT_STATE)) {
			URL url = new URL(urlAddress + "state");
			HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setRequestMethod("GET");
			setupConnectionProperties(httpurlconnection);
			return httpurlconnection;
		} else {
			throw new IllegalArgumentException(
					"Invalid action passed: " + intent.getAction());
		}
	}
}
