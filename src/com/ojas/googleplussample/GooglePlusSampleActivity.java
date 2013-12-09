package com.ojas.googleplussample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;

public class GooglePlusSampleActivity extends Activity implements ConnectionCallbacks,
OnConnectionFailedListener, OnClickListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private PlusClient mPlusClient;
	private Button btnSignIn, btnShare;
	private TextView textUserName, txtlogin;
	private ProgressDialog mConnectionProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPlusClient = new PlusClient.Builder(this, this, this)
		.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
		.setScopes("PLUS_LOGIN")  // Space separated list of scopes
		.build();

		btnSignIn = (Button) findViewById(R.id.sign_in_button);
		btnShare = (Button) findViewById(R.id.share_button);
		textUserName = (TextView) findViewById(R.id.txt_user_name);
		txtlogin = (TextView) findViewById(R.id.txt_login);
		txtlogin.setVisibility(View.GONE);
		// btnPlus = (PlusOneButton) findViewById(R.id.plus_one_small_button);

		/*
		 * check google plus application available or not in device
		 */
/*		int errorCode = GooglePlusUtil.checkGooglePlusApp(this);
		if (errorCode != GooglePlusUtil.SUCCESS) {
			GooglePlusUtil.getErrorDialog(errorCode, this, 0).show();
		} else {
			btnSignIn.setOnClickListener(this);
			btnShare.setOnClickListener(this);
		}*/

		btnSignIn.setOnClickListener(this);
		btnShare.setOnClickListener(this);

		// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sign_in_button) {

			if (!mPlusClient.isConnected() && btnSignIn.getText().equals(
					getString(R.string.btn_signin))) {

				mPlusClient.connect();

			} else if (mPlusClient.isConnected() && btnSignIn.getText().equals(
					getString(R.string.btn_signout))) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
				btnSignIn.setText(getString(R.string.btn_signin));
				textUserName.setText("");
				txtlogin.setVisibility(View.GONE);
			}

		} else if (v.getId() == R.id.share_button) {

			if (mPlusClient.isConnected()) {

				Intent shreIntent = new PlusShare.Builder(GooglePlusSampleActivity.this)
				.setText("Check out: http://example.com/cheesecake/lemon")
				.setType("text/plain")
				.setContentUrl(Uri.parse("http://example.com/cheesecake/lemon")).getIntent();
				startActivity(shreIntent);

			} else {
				Toast.makeText(this, "Please Sign-in with google Account", Toast.LENGTH_LONG)
				.show();
			}

		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.disconnect();
				mPlusClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mConnectionProgressDialog.dismiss();
		String accountName = mPlusClient.getAccountName();
		Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG)
		.show();
		btnSignIn.setText(getString(R.string.btn_signout));
		textUserName.setText(accountName);
		txtlogin.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

}
