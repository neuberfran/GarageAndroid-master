
package com.blogspot.ryanfx.activity;

import com.blogspot.ryanfx.R;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
//import android.preference.ListPreference;
//import android.preference.PreferenceActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import android.preference.PreferenceActivity;
import androidx.preference.PreferenceScreen;

public class ConfigurationActivity extends AppCompatActivity {

	//	implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

//private ListPreference listPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportFragmentManager()
				.beginTransaction()
			//	.replace(R.id.settings_container, new MySettingsFragment())
				.commit();

	//	addPreferencesFromResource(R.xml.server_config);
	//	listPreference = (ListPreference) findPreference("user");
	}

	public class MySettingsFragment extends PreferenceFragmentCompat {

		public ListPreference listPreference;

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.server_config, rootKey);
			listPreference = (ListPreference) findPreference("user");
		}

		@Override
		public void onResume() {
			super.onResume();
			//When the Activity resumes, refill the list with an updated account list
			fillUserList();
		}

		private void fillUserList() {
			String[] accountList = getAccounts();
			listPreference.setEntries(accountList);
			listPreference.setEntryValues(accountList);
		}

		private String[] getAccounts() {
			AccountManager manager = AccountManager.get(getContext());
			// Only request the user accounts associated with Google.
			Account[] accounts = manager.getAccountsByType("com.google");
			String[] accountList = new String[accounts.length];
			for (int i = 0; i < accounts.length; i++) {
				accountList[i] = accounts[i].name;
			}
			return accountList;
		}

	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		//When the Activity resumes, refill the list with an updated account list
//		fillUserList();
//	}
//
//	private void fillUserList() {
//		String[] accountList = getAccounts();
//		listPreference.setEntries(accountList);
//		listPreference.setEntryValues(accountList);
//	}

//	private String[] getAccounts() {
//		AccountManager manager = AccountManager.get(this);
//		// Only request the user accounts associated with Google.
//		Account[] accounts = manager.getAccountsByType("com.google");
//		String[] accountList = new String[accounts.length];
//		for (int i = 0; i < accounts.length; i++) {
//			accountList[i] = accounts[i].name;
//		}
//		return accountList;
//	}

//	@Override
//	public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller , Preference pref) {
//		// Instantiate the new Fragment
//		final Bundle args = pref.getExtras();
//		final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
//				getClassLoader(),
//				pref.getFragment(),
//				args);
//		fragment.setArguments(args);
//		fragment.setTargetFragment(caller, 0);
//		// Replace the existing Fragment with the new Fragment
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.settings_container, fragment)
//				.addToBackStack(null)
//				.commit();
//		return true;
//	}

//		return false;
//	}
}