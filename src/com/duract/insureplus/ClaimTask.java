package com.duract.insureplus;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.duract.insureplus.AndroidMultiPartEntity.ProgressListener;

import android.os.AsyncTask;
import android.util.Log;

public class ClaimTask extends AsyncTask<String, Integer, String> {

	private long totalSize;
	private ClaimProgressActivity activity;
	private GPSTracker tracker;
	private int gpsAttempts;

	public ClaimTask(ClaimProgressActivity activity) {
		this.activity = activity;
		tracker = new GPSTracker(activity);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		String responseText = null;
		try {

			do {
				if (gpsAttempts > 30) {
					return "BAD_GPS";
			}
				AppData.GPS_LOCATION = tracker.getLocation();
				Thread.sleep(3000);
				Log.d("THREAD", "EXECUTED!");
				gpsAttempts++;
			} while (AppData.GPS_LOCATION == null);
			String longitude = String.valueOf(AppData.GPS_LOCATION
					.getLongitude());
			String latitude = String
					.valueOf(AppData.GPS_LOCATION.getLatitude());

			Log.d("LENGTH", String.valueOf(params.length));
			// if (params.length >= 7) {
			String vehicle = params[0].split("_")[0];
			String damage = params[1];
			String city = params[2];
			String description = params[3];
			String filePath = params[4];

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ClaimActivity.CLAIM_URL);

			AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
					new ProgressListener() {

						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			File sourceFile = new File(filePath);

			entity.addPart("evidence", new FileBody(sourceFile));

			entity.addPart("vehicle", new StringBody(vehicle));
			entity.addPart("damage", new StringBody(damage));
			entity.addPart("city", new StringBody(city));
			entity.addPart("longitude", new StringBody(longitude));
			entity.addPart("latitude", new StringBody(latitude));
			entity.addPart("description", new StringBody(description));

			totalSize = entity.getContentLength();

			request.setEntity(entity);

			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseText = EntityUtils.toString(response.getEntity());
			} else {
				responseText = "NOT_RESPOND";
			}
		} catch (Exception e) {
			responseText = "ERROR";
		}

		// }

		return responseText;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d("RESPOND:", result);
		activity.parseRespond(result);
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		Log.d("CLAIM TASK", "STARTED!");
		activity.setEnableButtons(false);
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		activity.showProgress(values[0]);
	}

}
