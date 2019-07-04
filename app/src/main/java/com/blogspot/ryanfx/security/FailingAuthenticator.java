package com.blogspot.ryanfx.security;

import android.util.Log;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class
        FailingAuthenticator extends Authenticator{

	//Make a ThreadLocal attmpted boolean.  This will let every thread try once before it fails
	private ThreadLocal<Boolean> attempted  = new ThreadLocal<Boolean>();
	private String username;
	private char[] password;
	
	public FailingAuthenticator(String username,  char[] password){
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		//If calling thread has not invoked this method yet, or it is in a "ready" state
		if (attempted.get() == null || !attempted.get()){
			Log.i(getClass().getName(), "First call from thread " + Thread.currentThread().getId());
			//Set attempted to true, so a further call will return null and will not retry
			attempted.set(true);
			return new PasswordAuthentication(username, password); 
		}
		//A previous try has been attempted.  Return null to cancel retries and reset this thread
		Log.i(getClass().getName(), "Second call (now resetting) from thread " + Thread.currentThread().getId());
		attempted.set(false);
		return null;
	}
	
	/**
	 * Call this when a successful connection has been made to reset the attempted variable.
	 */
	public void setSuccessfulConnectionMade(){
		attempted.set(false);
	}
}