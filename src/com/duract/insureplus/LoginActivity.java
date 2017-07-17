package com.duract.insureplus;

import org.json.JSONArray;
import org.json.JSONException;
import com.duract.insureplus.util.SystemUiHider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LoginActivity extends Activity {
	public static final String URL = AppData.HOST_ADDRESS
			+ "/InsurePlus/OwnerService?WSDL";
	// http://localhost:8080/InsurePlus/OwnerService?WSDL

	EditText txtNic, txtPword;

	TextView txtStatus;
	Button btnSignIn;

	LoginTask login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		login = new LoginTask(LoginActivity.this);

		txtNic = (EditText) findViewById(R.id.editTextNIC);
		txtPword = (EditText) findViewById(R.id.editTextPassword);

		txtStatus = (TextView) findViewById(R.id.textViewStatus);
		btnSignIn = (Button) findViewById(R.id.btnSignIn);

		btnSignIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				debugStatus("Please wait....");
				String nic = txtNic.getText().toString();
				String pword = txtPword.getText().toString();

				if (nic != null & pword != null) {
					if (!login.running) {
						login = new LoginTask(LoginActivity.this);
						Log.d("NIC/PWORD:", nic + "|" + pword);
						login.execute(nic.toString(), pword.toString());
					} else {
						Toast.makeText(LoginActivity.this,
								"Please wait !.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(LoginActivity.this,
							"Please Enter NIC & Password !.", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		if (AppData.debug) {
			proceedToHome("debug_user");
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide(100);
	}

	public void debugStatus(String string) {
		if (txtStatus != null & AppData.debug) {
			txtStatus.setText(string);
		}
	}

	public void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	public void proceedLogin(String string) {
		try {
			JSONArray array = new JSONArray(string);
			String status = array.getJSONObject(0).getString("status");

			if (status.equals("found")) {
				showToast("Login succeed !");
				CurrentOwner owner = saveOwner(string);
				if (owner != null) {
					proceedToHome(owner.getFname());
				}
			} else {
				showToast("Please check NIC/Password");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private CurrentOwner saveOwner(String string) {
		try {
			CurrentOwner owner = new CurrentOwner();

			owner.setNic(DataManager.getDetails("nic", "owner", string));
			owner.setFname(DataManager.getDetails("fname", "owner", string));
			owner.setLname(DataManager.getDetails("lname", "owner", string));
			owner.setPword(DataManager.getDetails("pword", "owner", string));
			owner.setEmail(DataManager.getDetails("email", "owner", string));
			owner.setDob(DataManager.getDetails("dob", "owner", string));
			owner.setAddress(DataManager.getDetails("address", "owner", string));
			owner.setCity(DataManager.getDetails("city", "owner", string));
			owner.setMobile(DataManager.getDetails("mobile", "owner", string));
			owner.setStatus(DataManager.getDetails("status", "owner", string));

			DBManager.saveOwner(owner);
			return owner;
		} catch (Exception e) {
			Log.e("ERROR:SAVE OWNER", e.toString());
			return null;
		}
	}

	private void proceedToHome(String owner) {
		finish();
		Intent home = new Intent(getApplicationContext(), HomeActivity.class);
		home.putExtra("OWNER", owner);
		startActivity(home);
	}

}
