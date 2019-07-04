
package com.blogspot.ryanfx.activity;

import com.blogspot.ryanfx.R;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
//import android.preference.ListPreference;
//import android.preference.PreferenceActivity;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class ConfigurationActivity extends PreferenceFragmentCompat {

	private ListPreference listPreference;


//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		addPreferencesFromResource(R.xml.server_config);
//		listPreference = (ListPreference) findPreference("user");
//	}

    @Override

	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		// Indicate here the XML resource you created above that holds the preferences
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