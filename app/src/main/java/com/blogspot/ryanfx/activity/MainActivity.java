package com.blogspot.ryanfx.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.ryanfx.R;
import com.blogspot.ryanfx.application.GarageApplication;
import com.blogspot.ryanfx.service.GarageService;
import com.blogspot.ryanfx.service.GarageStateService;
import com.blogspot.ryanfx.service.GarageToggleService;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.List;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class MainActivity extends AppCompatActivity {

	private Button toggleButton;
	private Button closeButton;
	private GarageApplication application;
	private TextView status;

	private final static int AUTHENTICATION_INTENT = 0;

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GarageToggleService.INTENT_CLOSE);
		filter.addAction(GarageToggleService.INTENT_TOGGLE);
		filter.addAction(GarageToggleService.INTENT_ERROR);
		filter.addAction(GarageToggleService.INTENT_STATE);
		registerReceiver(receiver, filter);
		// Start up the getState loop.
		if (application.getSelectedAccount() != null) {
			startStateLoop();
		}
		// If the actvitiy was closed before the previous service started, the
		// buttons
		// will still be in a disabled state. Re-enable them here.
		if (!isServiceRunning(GarageToggleService.class.getName())) {
			enableButtons(true);
		}
	}

	public boolean isServiceRunning(String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(
					serviceClassName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

//		if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(android.R.id.content, new ConfigurationActivity ())
//					.commit();
//		}

        toggleButton = (Button) findViewById(R.id.toggle_garage_button);
		closeButton = (Button) findViewById(R.id.close_garage_button);
		status = (TextView) findViewById(R.id.status);

		closeButton.setOnClickListener(buttonListener);
		toggleButton.setOnClickListener(buttonListener);

		application = (GarageApplication) getApplication();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			startActivity(new Intent(this, ConfigurationActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String action = data.getAction();
		if (requestCode == AUTHENTICATION_INTENT) {
			if (resultCode == RESULT_OK) {
				getAuthAndDo(action);
			} else {
				enableButtons(true);
			}
		}
	}

	private OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (application.getSelectedAccount() != null) {
				enableButtons(false);
			   	if (v.getId() == R.id.toggle_garage_button) {
					getAuthAndDo(GarageToggleService.INTENT_TOGGLE);
				} else if (v.getId() == R.id.close_garage_button) {
					getAuthAndDo(GarageToggleService.INTENT_CLOSE);
				}
			} else {
				Toast.makeText(MainActivity.this,
						"User Account must be selected first!",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int statusCode = intent.getExtras().getInt(
					GarageToggleService.EXTRA_HTTP_RESPONSE_CODE);
			String response = intent.getExtras().getString(
					GarageToggleService.EXTRA_HTTP_RESPONSE_TEXT);
			if (intent.getAction().equals(GarageToggleService.INTENT_CLOSE)) {
				enableButtons(true);

				if (statusCode == HttpStatus.SC_OK) {
					if (response.equals("closed")) {
						Toast.makeText(MainActivity.this,
								"Door is already closed!", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(MainActivity.this, "Door is closing...",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(
							MainActivity.this,
							"Error Occured - Status: " + statusCode + " "
									+ response, Toast.LENGTH_LONG).show();
				}
			} else if (intent.getAction().equals(
					GarageToggleService.INTENT_TOGGLE)) {
				enableButtons(true);
				if (statusCode == HttpStatus.SC_OK) {
					Toast.makeText(MainActivity.this, "Door is toggling",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							MainActivity.this,
							"Error Occured - Status: " + statusCode + " "
									+ response, Toast.LENGTH_LONG).show();
				}

			} else if (intent.getAction().equals(
					GarageToggleService.INTENT_STATE)) {
				if (statusCode == HttpStatus.SC_OK) {
					if (response.equals("closed")) {
						status.setText(response);
					} else if (response.equals("open")) {
						status.setText(response);
					} else {
						status.setText("Invalid state response");

					}
					startGetStateService();
				} else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
					// If we were rejected, maybe our auth token is expired.
					MainActivity.this.invalidateAuthToken();
					Log.e(MainActivity.class.getName(), "Unauthorized request!");
					status.setText("Authentication Failure");
					startGetStateService();
				} else {
					status.setText("...");
					startGetStateService();
				}
			}
		}
	};

	public void enableButtons(boolean enabled) {
		toggleButton.setEnabled(enabled);
		closeButton.setEnabled(enabled);
	}

	public void getAuthAndDo(String intent) {
		Account account = application.getSelectedAccount();
		AccountManager manager = AccountManager.get(this);
		manager.getAuthToken(account,
				"oauth2:https://www.googleapis.com/auth/userinfo.email",
				new Bundle(), this, new GarageActionAuthCallback(intent), null);
	}

	public void invalidateAuthToken() {
		AccountManager manager = AccountManager.get(this);
		manager.invalidateAuthToken("com.google", application.getAuthToken());
	}

	private void startStateLoop() {
		getAuthAndDo(GarageService.INTENT_STATE);
	}

	private void startGetStateService() {
		Intent serviceIntent = new Intent(MainActivity.this,
				GarageStateService.class);
		serviceIntent.setAction(GarageService.INTENT_STATE);
		startService(serviceIntent);
	}

	private void startToggleService() {
		Intent serviceIntent = new Intent(MainActivity.this,
				GarageToggleService.class);
		serviceIntent.setAction(GarageService.INTENT_TOGGLE);
		startService(serviceIntent);
	}

	private void startCloseService() {
		Intent serviceIntent = new Intent(MainActivity.this,
				GarageToggleService.class);
		serviceIntent.setAction(GarageService.INTENT_CLOSE);
		startService(serviceIntent);
	}


	private class GarageActionAuthCallback implements
	AccountManagerCallback<Bundle> {

		private String action = null;

		private GarageActionAuthCallback(String action) {
			this.action = action;
		}

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				// If further authorization from the user is required
				if (intent != null) {
					intent.setAction(action);
					startActivityForResult(intent, AUTHENTICATION_INTENT);
				}
				// Otherwise, let's do the action we wanted to do
				else {
					String token = bundle
							.getString(AccountManager.KEY_AUTHTOKEN);
					application.setAuthToken(token);
					if (action.equals(GarageService.INTENT_STATE)) {
						startGetStateService();
					} else if (action.equals(GarageService.INTENT_TOGGLE)) {
						startToggleService();
					} else if (action.equals(GarageService.INTENT_CLOSE)) {
						startCloseService();
					}
				}
			} catch (OperationCanceledException e) {
				Log.e(this.getClass().getName(), "Authentication Cancelled", e);
				enableButtons(true);
				Toast.makeText(MainActivity.this,
						"Authentication Cancelled: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			} catch (AuthenticatorException e) {
				Log.e(this.getClass().getName(), "Authentication Failed", e);
				enableButtons(true);
				Toast.makeText(MainActivity.this,
						"Authentication Failed: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "Authentication Failed", e);
				enableButtons(true);
				// IOException means the AccountManager had a network error
				// call, restart the state loop.
				startStateLoop();
			}
		}
	}
}
