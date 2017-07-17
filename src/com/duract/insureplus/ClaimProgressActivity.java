package com.duract.insureplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class ClaimProgressActivity extends Activity {

	private Button btnClaim;
	private Button btnBack;
	private ClaimTask claimTask;

	// Claim Data Declaration
	protected String vehicle;
	protected String description;
	protected String filePath;
	protected String damage;
	protected String city;
	private ProgressBar progBar;
	private VideoView videoPreview;
	private GPSTracker gps;
	private TextView txtStatus;
	public static final int DONE = 55;
	public static final int BACK = 56;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_claim_progress);

		initComponents();

		getDetails();

		previewVideo();

		setListeners();

	}

	private void previewVideo() {
		videoPreview.setVideoPath(filePath);
		videoPreview.start();
	}

	private void setListeners() {
		btnClaim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (getCurrentLocation()) {
					claimTask = new ClaimTask(ClaimProgressActivity.this);

					claimTask.execute(vehicle, damage, city, description,
							filePath);
				}
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setResult(BACK);
				finish();
			}
		});
	}

	private void initComponents() {
		progBar = (ProgressBar) findViewById(R.id.progressBar1);
		btnClaim = (Button) findViewById(R.id.btnClaimNow);
		btnBack = (Button) findViewById(R.id.btnBack);
		videoPreview = (VideoView) findViewById(R.id.videoViewEvi);
		txtStatus = (TextView) findViewById(R.id.textViewClaimStatus);
	}

	private void getDetails() {
		Intent intent = getIntent();

		vehicle = intent.getStringExtra("vehicle");
		damage = intent.getStringExtra("damage");
		city = intent.getStringExtra("city");
		description = intent.getStringExtra("description");
		filePath = intent.getStringExtra("filePath");

	}

	private boolean getCurrentLocation() {
		if (gps == null) {
			gps = new GPSTracker(ClaimProgressActivity.this);
		}
		if (gps.canGetLocation()) {
			txtStatus.setText("Getting your Location...");
			return true;
		} else {
			gps.showSettingsAlert();
		}
		return false;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	public void showProgress(int value) {
		progBar.setVisibility(View.VISIBLE);
		progBar.setProgress(value);
		txtStatus.setText("Please wait " + value + "%");
	}

	public void setEnableButtons(boolean enable) {
		btnBack.setEnabled(enable);
		btnClaim.setEnabled(enable);
	}

	public void parseRespond(String result) {
		if (result != null) {
			if (result.equalsIgnoreCase("OK")) {
				Toast.makeText(getApplicationContext(),
						"Claim reported successfully !", Toast.LENGTH_LONG)
						.show();
				setResult(DONE);
				finish();
			} else if (result.equalsIgnoreCase("BAD_GPS")) {
				txtStatus
						.setText("Cannot get you location.\nPlease try again !.");
				setEnableButtons(true);
			} else if (result.equalsIgnoreCase("NOT_RESPOND")) {
				txtStatus
						.setText("Not responding from the server !\nPlease try again later.");
				setEnableButtons(true);
			} else if (result.equalsIgnoreCase("ERROR")) {
				txtStatus.setText("Something went wrong !");
				setEnableButtons(true);
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Something Went Wrong !..\nPlease try again later !",
					Toast.LENGTH_SHORT).show();
			setEnableButtons(true);
		}
	}

}
