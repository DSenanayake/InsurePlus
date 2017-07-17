package com.duract.insureplus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ClaimActivity extends ActionBarActivity {

	protected int RECORD_VIDEO_REQ = 40;
	protected int CLAIM_PROGRESS_REQ = 41;
	private Spinner listDamage;
	private ArrayList<String> damages;
	private ArrayAdapter<String> adapter;
	private Spinner listVehicle;
	private Button btnAttach;
	private Button btnNext;
	private Uri filePath;
	private EditText txtCity;
	private EditText txtDesc;
	public static final String CLAIM_URL = AppData.HOST_ADDRESS
			+ "/InsurePlus/ReportClaim";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		new GetVehicles().execute();
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_claim);

		listDamage = (Spinner) findViewById(R.id.spinner1);
		listVehicle = (Spinner) findViewById(R.id.spinner2);
		btnAttach = (Button) findViewById(R.id.btnPhoto);
		txtCity = (EditText) findViewById(R.id.editTextCity);
		txtDesc = (EditText) findViewById(R.id.editTextDescription);

		// Set damages
		damages = new ArrayList<String>();

		damages.add("Low");
		damages.add("Medium");
		damages.add("High");

		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, damages);

		listDamage.setAdapter(adapter);

		// Setup TextViews

		// Set Claim Action
		btnNext = (Button) findViewById(R.id.btnBack);

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (validateForm()) {
					Intent intent = new Intent(getApplicationContext(),
							ClaimProgressActivity.class);

					intent.putExtra("vehicle", listVehicle.getSelectedItem()
							.toString());
					intent.putExtra("damage", String.valueOf(listDamage
							.getSelectedItemPosition() + 1));
					intent.putExtra("city", txtCity.getText().toString());
					intent.putExtra("description", txtDesc.getText().toString());
					intent.putExtra("filePath", filePath.getPath());

					startActivityForResult(intent, CLAIM_PROGRESS_REQ);
				}
			}
		});

		btnAttach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

				filePath = getGenaratedPath();

				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);

				startActivityForResult(intent, RECORD_VIDEO_REQ);
			}
		});
	}

	@SuppressLint("NewApi")
	protected boolean validateForm() {
		String city = txtCity.getText() != null ? txtCity.getText().toString()
				.trim() : null;
		String desc = txtDesc.getText() != null ? txtDesc.getText().toString()
				.trim() : null;
		if (city != null) {
			if (!city.isEmpty()) {
				if (desc != null) {
					if (!desc.isEmpty()) {
						if (filePath != null) {
							return true;
						} else {
							makeToast("You have to put some video\nfootage as an evidence !");
						}
					} else {
						makeToast("Please include some details !");
					}
				} else {
					makeToast("Please include some details !");
				}
			} else {
				makeToast("Please Enter your nearest city !");
			}
		} else {
			makeToast("Please Enter your nearest city !");
		}
		return false;
	}

	private void makeToast(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
				.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECORD_VIDEO_REQ) {
			if (resultCode == RESULT_OK) {

			} else if (resultCode == RESULT_CANCELED) {
//				filePath = null;
			}
		} else if (requestCode == CLAIM_PROGRESS_REQ) {
			if (resultCode == ClaimProgressActivity.DONE) {
				finish();
			} else if (resultCode == ClaimProgressActivity.BACK) {

			}
		}
	}

	private Uri getGenaratedPath() {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
				"INSURE_PLUS/EVIDENCE");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("ERRRRRRRR!", "Oops! Failed create directory");
				return null;
			}
		}

		File file = new File(mediaStorageDir.getPath()
				+ File.separator
				+ new SimpleDateFormat("yyyy-MM-dd hh_mm_ss",
						Locale.getDefault()).format(new Date()) + "_VID.mp4");
		Log.d("PATH", file.getPath());
		return Uri.fromFile(file);
	}

	public class GetVehicles extends AsyncTask<String, Integer, String> {

		private String NAMESPACE = "http://Controller/";
		private String METHOD_NAME = "getVehicles";
		private String SOAP_ACTION = "http://Controller/OwnerService";

		@Override
		protected String doInBackground(String... params) {
			String responseText = null;
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

				request.addProperty("nic", HomeActivity.CURRENT_OWNER);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
						LoginActivity.URL);

				Log.d("OK:", "URL-" + LoginActivity.URL);

				androidHttpTransport.call(SOAP_ACTION, envelope);

				Log.d("OK:", "WEB-SERV-CALLED");

				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				if (response != null) {
					String json = response.toString();

					Log.d("JSON", json);

					responseText = json;
				}
			} catch (Exception e) {
				responseText = "ERROR";
			}
			return responseText;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (!result.equals("ERROR")) {
					try {
						Log.d("JSON:", result);
						JSONArray array = new JSONArray(result);
						String status = array.getJSONObject(0).getString(
								"status");
						if (status.equalsIgnoreCase("ok")) {
							JSONArray vehicles = array.getJSONObject(0)
									.getJSONArray("vehicles");
							List<String> lstV = new ArrayList<String>();
							for (int i = 0; i < vehicles.length(); i++) {
								JSONObject v = vehicles.getJSONObject(i);
								lstV.add(v.getString("id") + "_"
										+ v.getString("brand") + " "
										+ v.getString("model"));
							}
							adapter = new ArrayAdapter<String>(
									getApplicationContext(),
									android.R.layout.simple_list_item_1, lstV);

							listVehicle.setAdapter(adapter);
						} else {
							Toast.makeText(getApplicationContext(),
									"Can't get vehicles !", Toast.LENGTH_LONG)
									.show();
							ClaimActivity.this.finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Something went wrong !", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Something went wrong !", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}
