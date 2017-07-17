package com.duract.insureplus;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.os.AsyncTask;
import android.util.Log;

public class LoginTask extends AsyncTask<String, String, Integer> {

	LoginActivity l;
	boolean running = false;

	private final String NAMESPACE = "http://Controller/";
	private final String METHOD_NAME = "loginOwner";
	private final String SOAP_ACTION = "http://Controller/OwnerService";

	public LoginTask(LoginActivity l) {
		this.l = l;
	}

	@Override
	protected Integer doInBackground(String... values) {

		try {
			if (values.length >= 2) {
				String nic = values[0];
				String pword = values[1];

				if (nic != null & pword != null) {
					Log.d("OK", "INSIDE-IF");
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					request.addProperty("nic", nic);
					request.addProperty("password", pword);

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(
							LoginActivity.URL);

					Log.d("OK:", "URL-" + LoginActivity.URL);

					androidHttpTransport.call(SOAP_ACTION, envelope);

					Log.d("OK:", "WEB-SERV-CALLED");

					SoapPrimitive response = (SoapPrimitive) envelope
							.getResponse();
					if (response != null) {
						String json = response.toString();

						Log.d("JSON", json);

						publishProgress("OK", json);
					} else {
						Log.w("STATUS:", "NULL Object");
					}

				}
			}
		} catch (Exception e) {
			l.debugStatus("ERROR:" + e);
			publishProgress("ERROR", e.toString());
			Log.d("ERROR:", e.toString());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		running = false;
		l.debugStatus("TASK FINISHED");
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		running = true;
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(String... prog) {
		if (prog[0].equals("OK")) {
			l.proceedLogin(prog[1]);
		} else if (prog[0].equals("ERROR")) {
			l.showToast("Something went wrong !");
		}
		super.onProgressUpdate(prog);
	}

}
