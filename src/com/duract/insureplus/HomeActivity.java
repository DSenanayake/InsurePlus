package com.duract.insureplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class HomeActivity extends Activity {

	public static String CURRENT_OWNER;

	TextView claimView, vehicleView, historyView, profileView;

	private GPSTracker gps;

	private TextView txtWelcomeNote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (AppData.debug) {
			CURRENT_OWNER = "951733083v";
		}
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home);

		getCurrentLocation();

		initComponents();

		setListeners();

		setOwnerDetails();

	}

	private void setOwnerDetails() {
		String fname = getIntent().getStringExtra("OWNER");
		if (fname == null) {
			fname = "debug_user";
		}
		txtWelcomeNote.setText("Welcome, " + fname);
	}

	private void setListeners() {
		claimView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (getCurrentLocation()) {
					Intent claim = new Intent(getApplicationContext(),
							ClaimActivity.class);
					startActivity(claim);
				}
			}
		});
	}

	private void initComponents() {
		claimView = (TextView) findViewById(R.id.btnClaim);
		vehicleView = (TextView) findViewById(R.id.btnVehicle);
		historyView = (TextView) findViewById(R.id.btnHistory);
		profileView = (TextView) findViewById(R.id.btnProfile);
		txtWelcomeNote = (TextView) findViewById(R.id.textViewNote);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	private boolean getCurrentLocation() {
		if (gps == null) {
			gps = new GPSTracker(this);
		}
		if (!gps.canGetLocation()) {
			gps.showSettingsAlert();
			return false;
		}
		AppData.GPS_LOCATION = gps.getLocation();
		return true;
	}
}
