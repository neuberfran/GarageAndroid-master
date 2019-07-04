package com.blogspot.ryanfx.service;

import android.content.Intent;

import com.blogspot.ryanfx.service.GarageService;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GarageToggleService extends GarageService {

    public GarageToggleService() {
        super ( "GarageToggleService" );
    }

    @Override
    protected HttpURLConnection getRequestFromIntent(String baseString , Intent intent) throws ClientProtocolException, IOException {
        HttpURLConnection urlConnection = null;
        URL url = null;
        if (intent.getAction ().equals ( INTENT_TOGGLE )) {
            url = new URL ( baseString + "toggle" );
            urlConnection = (HttpURLConnection) url.openConnection ();
        } else if (intent.getAction ().equals ( INTENT_CLOSE )) {
            url = new URL ( baseString + "close" );
            urlConnection = (HttpURLConnection) url.openConnection ();
        } else {
            throw new IllegalArgumentException ( "Invalid action passed: " + intent.getAction () );
        }
        urlConnection.setRequestMethod ( HttpPost.METHOD_NAME );
        setupConnectionProperties ( urlConnection );
        return urlConnection;
    }

}

